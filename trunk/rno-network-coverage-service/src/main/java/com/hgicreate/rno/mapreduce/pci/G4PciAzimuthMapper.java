package com.hgicreate.rno.mapreduce.pci;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

public class G4PciAzimuthMapper extends TableMapper<Text, Text> {
	private static Logger log=Logger.getLogger("MyDFSClient");
	private String enodebId=""	;
	private String cityId=""      ;
	private String meaTime=""     ;
	private String cellId =""     ;
	//private String cellName  =""  ;
	private String cellLon =""   ;
	private String cellLat  =""   ;
	private String ncellId  =""   ;
	private String ncell    =""    ;
	private String ncellLon  ="" ;
	private String ncellLat   ="" ;
	private String timestotal =""  ;
/*	private String rsrptimes0 =""  ;
	private String rsrptimes1 =""  ;
	private String rsrptimes2 =""  ;
	private String rsrptimes3 =""  ;
	private String rsrptimes4 =""  ;*/
	private String distance="";
	/**最大距离限制，单位米**/
	private double maxDisLimit = 5000;
	/**最小距离限制，单位米，用来排除同站小区，暂时默认100**/
	private double minDisLimit = 100;
	private String cellBcch  =""   ;
	private String ncellBcch  =""   ;
	private String mixingSum ="";//混频
	Configuration conf = null;
	String [] ncellArr=new String [5];
	private String numerator="";
	private String time1="";
	Map<String, String> cellIdToBandTypes=null;
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		//获取配置信息
		conf = context.getConfiguration();
//		System.out.println("setup 中的conf能获得吗?="+conf);
		if(conf!=null){
			numerator=conf.get("numerator");
			numerator=numerator.toUpperCase();
			String arr[]=conf.getStrings("cellIdToBandType");
//			System.out.println("cellIdToBandTypesarr[0]==="+arr[0]);
			if(arr!=null && arr.length!=0){
				cellIdToBandTypes=string2Map(arr[0]);
				//System.out.println("cellIdToBandTypes="+cellIdToBandTypes);
			}
			if (conf.get("dislimit") == null || "".equals(conf.get("dislimit"))) {
				maxDisLimit = 5000;
			}else{
				maxDisLimit = Double.parseDouble(conf.get("dislimit"));
			}
		}
		System.out.println("cellIdToBandTypes 大小==="+cellIdToBandTypes.size());
		System.out.println("PCIMapper 执行setup中..改变了....");
		log.error("PCIMapper 执行setup中..");
		super.setup(context);
	}
	
	protected void map(ImmutableBytesWritable row, Result value, Context context)
			throws IOException, InterruptedException {
		
		//TableSplit split=(TableSplit)context.getInputSplit();
		//TableName tableName = split.getTable();
//		System.out.println("表："+tableName.toString());
		NavigableMap<byte[], byte[]> navi = null;
/*		
		if (("RNO_4G_ERI_MR").equals(tableName.toString())) {*/
			navi=value.getFamilyMap(Bytes.toBytes("MRINFO"));
			
			cellId = new String(navi.get("CELL_ID".getBytes()));

			if(!cellIdToBandTypes.containsKey(cellId)){
				System.out.println("小区cellId="+cellId+",匹配不到工参，过滤。。");
				return;
			}

			enodebId = new String(navi.get("ENODEB_ID".getBytes()));
			cityId = new String(navi.get("CITY_ID".getBytes()));
			meaTime = new String(navi.get("MEA_TIME".getBytes()));

			//cellName = new String(navi.get("CELL_NAME".getBytes()));
			cellLon = new String(navi.get("CELL_LON".getBytes()));
			cellLat = new String(navi.get("CELL_LAT".getBytes()));
			ncellId = new String(navi.get("NCELL_ID".getBytes()));
			if(navi.get("NCELL".getBytes())!=null){
				ncell = new String(navi.get("NCELL".getBytes()));
			}
			ncellLon = new String(navi.get("NCELL_LON".getBytes()));
			ncellLat = new String(navi.get("NCELL_LAT".getBytes()));
			timestotal = new String(navi.get("TIMESTOTAL".getBytes()));
/*			rsrptimes0 = new String(navi.get("RSRPTIMES0".getBytes()));
			rsrptimes1 = new String(navi.get("RSRPTIMES1".getBytes()));
			rsrptimes2 = new String(navi.get("RSRPTIMES2".getBytes()));
			rsrptimes3 = new String(navi.get("RSRPTIMES3".getBytes()));
			rsrptimes4 = new String(navi.get("RSRPTIMES4".getBytes()));*/
			distance = new String(navi.get("DISTANCE".getBytes()));
			cellBcch = new String(navi.get("CELL_BCCH".getBytes()));
			ncellBcch = new String(navi.get("NCELL_BCCH".getBytes()));
			mixingSum = new String(navi.get("MIXINGSUM".getBytes()));
			time1 = new String(navi.get(numerator.getBytes()));
/*			
		} else if (("RNO_4G_ZTE_MR").equals(tableName.toString())) {
			navi=value.getFamilyMap(Bytes.toBytes("MRINFO"));
			enodebId = new String(navi.get("ENODEB_ID".getBytes()));
			cityId = new String(navi.get("CITY_ID".getBytes()));
			meaTime = new String(navi.get("MEA_TIME".getBytes()));
			cellId = new String(navi.get("CELL_ID".getBytes()));
			//cellName = new String(navi.get("CELL_NAME".getBytes()));
			cellLon = new String(navi.get("CELL_LON".getBytes()));
			cellLat = new String(navi.get("CELL_LAT".getBytes()));
			ncellId = new String(navi.get("NCELL_ID".getBytes()));
			ncell = new String(navi.get("NCELL".getBytes()));
			ncellLon = new String(navi.get("NCELL_LON".getBytes()));
			ncellLat = new String(navi.get("NCELL_LAT".getBytes()));
			timestotal = new String(navi.get("TIMESTOTAL".getBytes()));
			rsrptimes0 = new String(navi.get("RSRPTIMES0".getBytes()));
			rsrptimes1 = new String(navi.get("RSRPTIMES1".getBytes()));
			rsrptimes2 = new String(navi.get("RSRPTIMES2".getBytes()));
			rsrptimes3 = new String(navi.get("RSRPTIMES3".getBytes()));
			rsrptimes4 = new String(navi.get("RSRPTIMES4".getBytes()));
			distance = new String(navi.get("DISTANCE".getBytes()));
			cellBcch = new String(navi.get("CELL_BCCH".getBytes()));
			ncellBcch = new String(navi.get("NCELL_BCCH".getBytes()));
			mixingSum = new String(navi.get("MIXINGSUM".getBytes()));
			time1 = new String(navi.get(numerator.getBytes()));
			
		} */
		/*LTE剔除条件
		1.	排除E频段（E频段就是室分，不参与计算）
		2.	近距离邻区不考虑，暂定为距离小于100米的邻区不参与计算*/
		//同频累加
	    /*if(cellBcch == ncellBcch  && !Pattern.matches("e|E|E频段", cellIdToBandTypes.get(cellId)) && Double.parseDouble(distance)*1000>100){
	    	System.out.println(ncellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+cellLon+","+cellLat+","+ncellLon+","+ncellLat+","+cellName+",MR");
			context.write(new Text(cellId), new Text(ncellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+cellLon+","+cellLat+","+ncellLon+","+ncellLat+","+cellName+",MR"));
		}*/
//			System.out.println("cellBcch="+cellBcch+"---ncellBcch="+ncellBcch+"--ncellId="+ncellId+"--cellIdToBandTypes="+cellIdToBandTypes.get(cellId)+"---distance="+distance);
		if(cellBcch.equals(ncellBcch)  && !"".equals(ncellBcch) && !"".equals(ncellId) && !Pattern.matches("e|E|E频段", cellIdToBandTypes.get(cellId)) && Double.parseDouble(distance)*1000>minDisLimit && Double.parseDouble(distance)*1000<maxDisLimit){
//					System.out.println("进来了就是满足条件的。。。。。。。。。。。。。。。。。。。。。。。。");
//					System.out.println("dis="+distance+"==========" +ncellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+cellLon+","+cellLat+","+ncellLon+","+ncellLat+","+cellId+",MR");
//			context.write(new Text(cellId), new Text(ncellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+cellLon+","+cellLat+","+ncellLon+","+ncellLat+","+cellName+",MR"));
			//添加字段混频项,并且从以小区为主服务小区变更为以邻区为主服务小区
//			context.write(new Text(cellId), new Text(ncellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+cellLon+","+cellLat+","+ncellLon+","+ncellLat+","+cellName+","+mixingSum+",MR"));
			context.write(new Text(ncellId), new Text(cellId+","+timestotal+","+time1+","+enodebId+","+cityId+","+meaTime+","+ncellLon+","+ncellLat+","+cellLon+","+cellLat+","+ncell+","+mixingSum+",MR"));
		}

	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
	}
	public static Map<String, String> string2Map(String enodebs) {
		
	    Map<String, String> enodebToCells=new HashMap<String, String>();
		String keyarr[]=null;
		String valarr[]=null;
//		System.out.println("enodebs====="+enodebs);
		keyarr=enodebs.split("\\|");
		for (int i = 0; i < keyarr.length; i++) {
//			System.out.println("keyarr["+i+"]===="+keyarr[i]);
			valarr=keyarr[i].split("=");
//			System.out.println("valarr:--"+valarr[0]+"---"+valarr[1].replace("#", ","));
			enodebToCells.put(valarr[0],valarr[1].replace("#", ","));
		}
	return enodebToCells;
	}
}
