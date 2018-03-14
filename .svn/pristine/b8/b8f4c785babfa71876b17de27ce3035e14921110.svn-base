package com.hgicreate.rno.service;

import com.hgicreate.rno.dao.RnoLteDynaOverGraphDao;
import com.hgicreate.rno.mapper.LteDynaOverGraphMapper;
import com.hgicreate.rno.model.Area;
import com.hgicreate.rno.tool.CacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

@Service("rnoLteDynaOverGraphServiceImpl")
public class RnoLteDynaOverGraphServiceImpl implements RnoLteDynaOverGraphService {

	private static final Logger logger = LoggerFactory.getLogger(RnoLteDynaOverGraphServiceImpl.class);

	@Autowired
	private LteDynaOverGraphMapper lteDynaOverGraphMapper;

	@Autowired
	@Qualifier("rnoLteDynaOverGraphDaoImpl")
	private RnoLteDynaOverGraphDao rnoLteDynaOverGraphDao;
	@Autowired
	@Qualifier("cacheHelper")
	private CacheHelper cacheHelper;
	@Autowired
	@Qualifier("sysAreaSyncServiceImpl")
	private SysAreaSyncService sysAreaSyncService;
	
	private static double defaultImgSize = 0.005; // 曲线点距离服务小区的长度系数0.005
	private static double evalCoeff = 0.3; // 服务小区评估方向的长度系数，经调试0.3较好
	private static float scale = 0.6f; //控制点收缩系数 ，经调试0.6较好
	private static double step = 0.01; //曲线密度(0-1)0.01
	@SuppressWarnings("unused")
	private static double defaultImgSizeCoff = 10; // 矢量长度系数10

	/*
     * spark
     */
	@Value("${spring.datasource.spark.url}")
	private String sparkUrl;
	@Value("${spring.datasource.spark.username}")
	private String sparkUser;
	@Value("${spring.datasource.spark.password}")
	private String sparkPassword;
	@Value("${spring.datasource.spark.driverClassName}")
	private String sparkDriveName;

	@Override
	public List<Area> gisfindAreaInSpecLevelListByUserId(String accountId, String areaLevel) {
		logger.info("进入 gisfindAreaInSpecLevelListByUserId 。 accountId={},areaLevel={}" , accountId,areaLevel);

		List<Map<String, Object>> user_area_list = lteDynaOverGraphMapper
				.getAreaByAccount(accountId);

		HashSet<Long> areaIdSet = new HashSet<Long>();
		for (Map<String, Object> usea : user_area_list) {
			areaIdSet.add(Long.parseLong(usea.get("AREA_ID").toString()));
			// areaIds[i++] = Long.parseLong(usea.get("AREA_ID").toString());
		}

		long[] areaIds = new long[areaIdSet.size()];
		int i = 0;
		Iterator<Long> iter = areaIdSet.iterator();
		while (iter.hasNext()) {
			areaIds[i++] = iter.next();
		}

		// String area_level = "区/县";
		List<Map<String, Object>> user_area_in_spec_level_list = lteDynaOverGraphMapper
				.getSubAreasInSpecAreaLevel(areaIds, areaLevel);
		List<Area> result = new ArrayList<Area>();
		Area area;
		for (Map<String, Object> one : user_area_in_spec_level_list) {
			area = Area.fromMap(one);
			if (area != null) {
				if (areaLevel.equals(area.getAreaLevel())) {
					result.add(area);
				}
			}
		}
		return result;
	}

	/*
	 * 获取画LTE小区动态覆盖图(贝塞尔曲线)所需的数据
	 *//*
	public Map<String, List<Map<String, Object>>> get4GDynaCoverageDataByCityAndDate(
			String lteCellId, long cityId, String startDate, String endDate,double imgSizeCoeff) {

		logger.debug("get4GDynaCoverageDataByCityAndDate, lteCellId=" + lteCellId + ", cityId=" + cityId
				+ ", startDate=" + startDate + ", endDate=" + endDate);
		
		long stime = System.currentTimeMillis();

		//RELSS>-12
		List<Map<String,Object>> dynaCoverData_12 = new ArrayList<Map<String,Object>>();
		//RELSS>3
		List<Map<String,Object>> dynaCoverData_3 = new ArrayList<Map<String,Object>>();
		//关联度详情
		List<Map<String,Object>> resInterDetail = new ArrayList<Map<String,Object>>();
		
		Map<String,List<Map<String,Object>>> res = rnoLteDynaOverGraphDao
					.queryDynaCoverDataFromHbaseMrTable(cityId,lteCellId,startDate,endDate);
		
		dynaCoverData_12 = res.get("res1");
		dynaCoverData_3 = res.get("res2");
		resInterDetail=res.get("resInterDetail");
		if((dynaCoverData_12 == null || dynaCoverData_12.size() == 0)
				&&(dynaCoverData_3 == null || dynaCoverData_3.size() == 0)) {
			return null;
		}
		long etime = System.currentTimeMillis();
		logger.debug("从数据库读取(RELSS>-12)数据量："+dynaCoverData_12.size()
				+";(RELSS>3)数据量："+dynaCoverData_3.size()+"; 共耗时："+(etime-stime));
		
		//获取画(RELSS>-12)图形所需曲线点坐标集合
		Map<String, List<Map<String, Object>>> res_12 
				= calc4GDynaCoveragePointsData(dynaCoverData_12,imgSizeCoeff);
		//获取画(RELSS>3)图形所需曲线点坐标集合
		Map<String, List<Map<String, Object>>> res_3 
				= calc4GDynaCoveragePointsData(dynaCoverData_3,imgSizeCoeff);
		
		Map<String, List<Map<String,Object>>> result = new HashMap<String, List<Map<String,Object>>>();
		result.put("vectorPoint_12", res_12.get("vectorPoint"));
		result.put("vectorPoint_3", res_3.get("vectorPoint"));
		result.put("curvePoints_12", res_12.get("curvePoints"));
		result.put("curvePoints_3", res_3.get("curvePoints"));
		result.put("resInterDetail", resInterDetail);
		return result;
	}*/
	/*
	 * 获取画LTE小区动态覆盖图(折线)所需的数据
	 */
	public Map<String, List<Map<String, Object>>> get4GDynaCoverageData2ByCityAndDate(
			String lteCellId, long cityId, String startDate, String endDate,
			double imgCoeff,double imgSizeCoeff) {
		logger.debug("getDynaCoverageData2ByCityAndDate, lteCellId=" + lteCellId + ", cityId=" + cityId
				+ ", startDate=" + startDate + ", endDate=" + endDate);
		
		long stime = System.currentTimeMillis();

		DataSource db = DataSourceBuilder.create().url(sparkUrl).username(sparkUser).password(sparkPassword)
				.driverClassName(sparkDriveName).build();
		Connection connection = null;
		try {
			connection = db.getConnection();
		}catch (Exception e){
			logger.debug("准备SPARK数据仓库连接失败！");
			e.printStackTrace();
		}
		/*//RELSS>-12 rsr0
		List<Map<String,Object>> dynaCoverData_12 = new ArrayList<Map<String,Object>>();
		//RELSS>3 rsr2
		List<Map<String,Object>> dynaCoverData_3 = new ArrayList<Map<String,Object>>();
		//关联度详情
		List<Map<String,Object>> resInterDetail = new ArrayList<Map<String,Object>>();*/

		//动态覆盖数据
		List<Map<String,Object>> dynaCoverData = new ArrayList<Map<String,Object>>();

		//小区站间距
		Map<String,Object> cellStationSpaces;
		/*Map<String,List<Map<String,Object>>> res = rnoLteDynaOverGraphDao
					.queryDynaCoverDataFromHbaseMrTable(cityId,lteCellId,startDate,endDate);*/
		Map<String,List<Map<String,Object>>> res = rnoLteDynaOverGraphDao
				.queryDynaCoverDataFromSparkMrTable(cityId,lteCellId,startDate,endDate,"OUT",connection);
		/*dynaCoverData_12 = res.get("res1");   //rsr0
		dynaCoverData_3 = res.get("res2");      //rsr2
		resInterDetail=res.get("resInterDetail");*/
		dynaCoverData = res.get("res");
		cellStationSpaces = res.get("cellStationSps").get(0);

		/*for (int i=0;i<dynaCoverData_12.size();i++){
			Map<String,Object> dynaCoverData= dynaCoverData_12.get(i);
			Iterator it = dynaCoverData.entrySet().iterator();
			while (it.hasNext()){
				Map.Entry<String, String> entry1=(Map.Entry<String, String>)it.next();
				System.out.println(entry1.getKey()+"=="+entry1.getValue());
			}
		}*/
		/*if((dynaCoverData_12 == null || dynaCoverData_12.size() == 0)
				&&(dynaCoverData_3 == null || dynaCoverData_3.size() == 0)) {
			return null;
		}*/
		if(dynaCoverData == null || dynaCoverData.size() == 0) {
			return null;
		}
		long etime = System.currentTimeMillis();
		logger.debug("从数据库读取数据量："+dynaCoverData.size()+"; 共耗时："+(etime-stime));
		
		//获取画(RELSS>-12)图形所需折线点坐标集合 rsr0
		Map<String, List<Map<String, Object>>> res_12 = calc4GDynaCoveragePointsData2(
				dynaCoverData, imgCoeff,imgSizeCoeff,cellStationSpaces,"RSRP0");
		//获取画(RELSS>3)图形所需折线点坐标集合 rsr1
		/*Map<String, List<Map<String, Object>>> res_3 =  calc4GDynaCoveragePointsData2(
				dynaCoverData_3, imgCoeff,imgSizeCoeff,cellStationSpaces);*/

		Map<String, List<Map<String,Object>>> result = new HashMap<String, List<Map<String,Object>>>();
		result.put("vectorPoint_12", res_12.get("vectorPoint"));
		/*result.put("vectorPoint_3", res_3.get("vectorPoint"));*/
		result.put("curvePoints_12", res_12.get("curvePoints"));
//		result.put("curvePoints_12_", res_12.get("curvePoints_"));
		/*result.put("curvePoints_3", res_3.get("curvePoints"));*/
//		result.put("resInterDetail", resInterDetail);
		result.put("resInterDetail", dynaCoverData);
		return result;
	}
	/*
	 * 获取画LTE小区动态覆盖 in干扰所需的数据【小区位置】
	 */
	public List<Map<String,String>> get4GDynaCoverageInInferDataByCityAndDate(
			String lteCellId, long cityId, String startDate, String endDate) {
		logger.debug("get4GDynaCoverageInInferDataByCityAndDate, lteCellId=" + lteCellId + ", cityId=" + cityId
				+ ", startDate=" + startDate + ", endDate=" + endDate);
		
		long stime = System.currentTimeMillis();
		DataSource db = DataSourceBuilder.create().url(sparkUrl).username(sparkUser).password(sparkPassword)
				.driverClassName(sparkDriveName).build();
		Connection connection = null;
		try {
			connection = db.getConnection();
		}catch (Exception e){
			logger.debug("准备SPARK数据仓库连接失败！");
			e.printStackTrace();
		}
//		List<Map<String,String>> sumDynaCoverData=new ArrayList<Map<String,String>>();
		/*List<Map<String,String>> dynaCoverDataEri = rnoLteDynaOverGraphDao
					.query4GCellMrIndexFromHBase("RNO_4G_MR", cityId, lteCellId, startDate, endDate, "CELL_MEATIME");*/
		Map<String,List<Map<String,Object>>> datas = rnoLteDynaOverGraphDao.queryDynaCoverDataFromSparkMrTable(cityId, lteCellId, startDate, endDate,"IN",connection);
		/*List<Map<String,String>> dynaCoverDataZte = rnoLteDynaOverGraphDao
				.query4GCellMrIndexFromHBase("RNO_4G_ZTE_MR", cityId, lteCellId, startDate, endDate, "CELL_MEATIME");*/
		List<Map<String,Object>> dynaCoverData = datas.get("res");
		if((dynaCoverData == null || dynaCoverData.size() == 0)
				) {
			return null;
		}
		//获取小区对应的形状坐标  cellid->113.105,23.0255;113.10571,23.02539;113.10525,23.02488
		Map<String, Object> cellToShapes=rnoLteDynaOverGraphDao.queryLteCellShapeDataByCityId(cityId);

		long etime = System.currentTimeMillis();
		logger.debug("从数据库读取(mr)数据量："+dynaCoverData.size()
				+"; 共耗时："+(etime-stime));
		
		String cellId,ncellId;
		double cellLon,cellLat,ncellLon,ncellLat;
		String celllonlatArr[]=new String[2];
		String ncelllonlatArr[]=new String[2];
		
		List<Map<String,String>> cells=new ArrayList<Map<String,String>>();
		Map<String, String> cell=null;
		String cellshapelonlatArr[]=new String[2];
		String ncellshapelonlatArr[]=new String[2];
		//工参数据为GPS坐标，通过基准点数据进行纠集获取百度坐标
		for (int i = 0; i < dynaCoverData.size(); i++) {
			
			cell=new HashMap<String, String>();
			
			cellId=dynaCoverData.get(i).get("CELL_ID").toString();
			/*cellLon=sumDynaCoverData.get(i).get("CELL_LON");
			cellLat=sumDynaCoverData.get(i).get("CELL_LAT");*/
			ncellId=dynaCoverData.get(i).get("NCELL_ID").toString();
			/*ncellLon=sumDynaCoverData.get(i).get("NCELL_LON");
			ncellLat=sumDynaCoverData.get(i).get("NCELL_LAT");*/
			if("".equals(ncellId)){
				continue;
			}

			cellshapelonlatArr=cellToShapes.get(cellId).toString().split(";");
//			cellshapelonlatArr=cellToShapes.get(cellId).toString().split("\\],\\[");
			//获取中心点坐标
			cellLon = Double.parseDouble(cellshapelonlatArr[0].substring(0,
					cellshapelonlatArr[0].indexOf(",")));
//			cellLon = Double.parseDouble(cellshapelonlatArr[1].split(",")[0]);
			cellLat = Double.parseDouble(cellshapelonlatArr[0].substring(
					cellshapelonlatArr[0].split(";")[0].indexOf(",") + 1,
					cellshapelonlatArr[0].length()));
//			cellLat = Double.parseDouble(cellshapelonlatArr[1].split(",")[1]);
//			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split(";");
			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split(",");
//			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split("\\],\\[");

			/*ncellLon = Double.parseDouble(ncellshapelonlatArr[0].substring(0,
					ncellshapelonlatArr[0].indexOf(",")));*/
			ncellLon = Double.parseDouble(ncellshapelonlatArr[0]);
//			ncellLon = Double.parseDouble(ncellshapelonlatArr[1].split(",")[0]);
			ncellLat = Double.parseDouble(ncellshapelonlatArr[1]) ;
			/*ncellLat = Double.parseDouble(ncellshapelonlatArr[0].substring(
					ncellshapelonlatArr[0].split(";")[0].indexOf(",") + 1,
					ncellshapelonlatArr[0].length())) ;*/
//			ncellLat = Double.parseDouble(ncellshapelonlatArr[1].split(",")[1]);
			cell.put("CELL_ID", cellId);
			cell.put("NCELL_ID", ncellId);
			//此处校正坐标
			/*celllonlatArr=getBaiduLnglat(cellLon,cellLat, standardPoints);
			ncelllonlatArr=getBaiduLnglat(ncellLon,ncellLat, standardPoints);*/
			/*cell.put("CELL_LON", celllonlatArr[0]);
			cell.put("CELL_LAT", celllonlatArr[1]);
			cell.put("NCELL_LON", ncelllonlatArr[0]);
			cell.put("NCELL_LAT", ncelllonlatArr[1]);*/

			cell.put("CELL_LON", Double.toString(cellLon));
			cell.put("CELL_LAT", Double.toString(cellLat));
			cell.put("NCELL_LON", Double.toString(ncellLon));
			cell.put("NCELL_LAT", Double.toString(ncellLat));
			cells.add(cell);
		}
		
		
		return cells;
	}
	/*
	 * 获取画LTE小区动态覆盖 out干扰所需的数据[邻区位置]
	 */
	public List<Map<String,String>> get4GDynaCoverageOutInferDataByCityAndDate(
			String lteCellId, long cityId, String startDate, String endDate) {
		logger.debug("get4GDynaCoverageOutInferDataByCityAndDate, lteCellId=" + lteCellId + ", cityId=" + cityId
				+ ", startDate=" + startDate + ", endDate=" + endDate);
		
		long stime = System.currentTimeMillis();
		DataSource db = DataSourceBuilder.create().url(sparkUrl).username(sparkUser).password(sparkPassword)
				.driverClassName(sparkDriveName).build();
		Connection connection = null;
		try {
			connection = db.getConnection();
		}catch (Exception e){
			logger.debug("准备SPARK数据仓库连接失败！");
			e.printStackTrace();
		}

		Map<String,List<Map<String,Object>>> datas = rnoLteDynaOverGraphDao.queryDynaCoverDataFromSparkMrTable(cityId, lteCellId, startDate, endDate,"OUT",connection);

		List<Map<String,Object>> dynaCoverData = datas.get("res");
		if((dynaCoverData == null || dynaCoverData.size() == 0)
				) {
			return null;
		}
		//获取小区对应的形状坐标  cellid->113.105,23.0255;113.10571,23.02539;113.10525,23.02488
		Map<String, Object> cellToShapes=rnoLteDynaOverGraphDao.queryLteCellShapeDataByCityId(cityId);


		long etime = System.currentTimeMillis();
		logger.debug("从数据库读取(eri)数据量："+dynaCoverData.size()
				+"; 共耗时："+(etime-stime));
		
		String cellId,ncellId;
		double cellLon,cellLat,ncellLon,ncellLat;
		String celllonlatArr[]=new String[2];
		String ncelllonlatArr[]=new String[2];
		
		List<Map<String,String>> cells=new ArrayList<Map<String,String>>();
		Map<String, String> cell=null;
		String cellshapelonlatArr[]=new String[2];
		String ncellshapelonlatArr[]=new String[2];
		//工参数据为GPS坐标，通过基准点数据进行纠集获取百度坐标
		for (int i = 0; i < dynaCoverData.size(); i++) {
			
			cell=new HashMap<String, String>();
			
			cellId=dynaCoverData.get(i).get("CELL_ID").toString();
			/*cellLon=sumDynaCoverData.get(i).get("CELL_LON");
			cellLat=sumDynaCoverData.get(i).get("CELL_LAT");*/
			ncellId=dynaCoverData.get(i).get("NCELL_ID").toString();
			/*ncellLon=sumDynaCoverData.get(i).get("NCELL_LON");
			ncellLat=sumDynaCoverData.get(i).get("NCELL_LAT");*/
			if("".equals(ncellId)){
				continue;
			}

			cellshapelonlatArr=cellToShapes.get(cellId).toString().split(",");
//			cellshapelonlatArr=cellToShapes.get(cellId).toString().split("\\],\\[");
			//获取中心点坐标
			cellLon = Double.parseDouble(cellshapelonlatArr[0]);
//			cellLon = Double.parseDouble(cellshapelonlatArr[1].split(",")[0]);
			cellLat = Double.parseDouble(cellshapelonlatArr[1]);
//			cellLat = Double.parseDouble(cellshapelonlatArr[1].split(",")[1]);
//			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split(";");
			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split(",");
//			ncellshapelonlatArr=cellToShapes.get(ncellId).toString().split("\\],\\[");
			ncellLon = Double.parseDouble(ncellshapelonlatArr[0]);
//			ncellLon = Double.parseDouble(ncellshapelonlatArr[1].split(",")[0]);
			ncellLat = Double.parseDouble(ncellshapelonlatArr[1]);
//			ncellLat = Double.parseDouble(ncellshapelonlatArr[1].split(",")[1]);
			cell.put("CELL_ID", ncellId);
			cell.put("NCELL_ID", cellId);
			//此处校正坐标
			/*celllonlatArr=getBaiduLnglat(cellLon, cellLat, standardPoints);
			ncelllonlatArr=getBaiduLnglat(ncellLon, ncellLat, standardPoints);*/
			/*cell.put("CELL_LON", ncelllonlatArr[0]);
			cell.put("CELL_LAT", ncelllonlatArr[1]);
			cell.put("NCELL_LON", celllonlatArr[0]);
			cell.put("NCELL_LAT", celllonlatArr[1]);*/

			cell.put("CELL_LON", Double.toString(ncellLon));
			cell.put("CELL_LAT", Double.toString(ncellLat));
			cell.put("NCELL_LON", Double.toString(cellLon));
			cell.put("NCELL_LAT", Double.toString(cellLat));
			
			cells.add(cell);
		}
		
		
		return cells;
	}
	/*
	 * 4G获取画图所需曲线点坐标集合
	 */
	private Map<String, List<Map<String, Object>>> calc4GDynaCoveragePointsData(
			List<Map<String, Object>> dynaCoverData,double imgSizeCoeff) {
		
		long stime = System.currentTimeMillis();
	
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String,Object>>>();

		double cellLng = Double.parseDouble(dynaCoverData.get(0).get("CELL_LNG").toString());
		double cellLat = Double.parseDouble(dynaCoverData.get(0).get("CELL_LAT").toString());
		
		//图形大小系数取最强前六邻区与本小区的距离平均值
		double imgSize = 0.0;
		//取出最强6邻区的相关数据，用于获取距离平均值
		Map<String,Map<String,Double>> ncellIdToLngLat = new HashMap<String, Map<String,Double>>();
		Map<String,Double> lnglatMap = null;
		Map<String,Double> ncellIdToVal = new HashMap<String, Double>();
		for (Map<String, Object> one : dynaCoverData) {
			double val = Double.parseDouble(one.get("VAL").toString());
			double lng = Double.parseDouble(one.get("LNG").toString());
			double lat = Double.parseDouble(one.get("LAT").toString());
			String ncellId = one.get("NCELL_ID").toString();
			ncellIdToVal.put(ncellId, val);
			lnglatMap = new HashMap<String, Double>();
			lnglatMap.put("LNG", lng);
			lnglatMap.put("LAT", lat);
			ncellIdToLngLat.put(ncellId, lnglatMap);
		}
		//排序
		ncellIdToVal = sortMapByValue(ncellIdToVal);
		int num = 0;
		double totDistance = 0.0;
		for (String ncellId : ncellIdToVal.keySet()) {
			if(num >= 6) break;
			lnglatMap = ncellIdToLngLat.get(ncellId);
			double lng = lnglatMap.get("LNG");
			double lat = lnglatMap.get("LAT");
			if(lng==0 || lat==0) continue;
			//System.out.println(lng+"  "+lat);
			totDistance += Math.sqrt((lng-cellLng)*(lng-cellLng)+(lat-cellLat)*(lat-cellLat));
			num++;
		}
		imgSize = totDistance/num;
		
		//如果图形大小系数未设置，取默认值
		if(imgSize == 0) {
			imgSize = defaultImgSize;
		}
		
		
		/*String cellBaiduPoint[] = getBaiduLnglat(cellLng, cellLat,standardPoints);
		if(cellBaiduPoint==null) {
			return null;
		}
		double cellbaiduLng = Double.parseDouble(cellBaiduPoint[0]);
		double cellbaiduLat = Double.parseDouble(cellBaiduPoint[1]);*/
		double cellbaiduLng = cellLng;
		double cellbaiduLat = cellLat;
		
		List<ReferencePoint> points = new ArrayList<ReferencePoint>();
		ReferencePoint point = null;
		for (Map<String, Object> one : dynaCoverData) {
			double val = Double.parseDouble(one.get("VAL").toString())*8;
//			val =val * imgSizeCoeff;//乘以常量0.005  js写是10
			val =val * 0.005;//乘以常量0.005
			double lng = Double.parseDouble(one.get("LNG").toString());
			double lat = Double.parseDouble(one.get("LAT").toString());
			//增加
			/*double ncellLng = Double.parseDouble(one.get("CELL_LNG").toString());
			double ncellLat = Double.parseDouble(one.get("CELL_LAT").toString());
			double dis=LatLngHelper.Distance(lng, lat, ncellLng, ncellLat);
			val =val * dis;*/ //乘以小区间的距离
			
			if(lng==0 || lat==0) continue;
		
			//转化成百度坐标
			/*String baiduPoint[] = getBaiduLnglat(lng, lat,standardPoints);
			if(baiduPoint==null) {
				continue;
			}*/
			/*double oneBaiduLng = Double.parseDouble(baiduPoint[0]);
			double oneBaiduLat = Double.parseDouble(baiduPoint[1]);*/
			double oneBaiduLng = lng;
			double oneBaiduLat = lat;
			
			double lngDiff = oneBaiduLng - cellbaiduLng;
			double latDiff = oneBaiduLat - cellbaiduLat;
			//System.out.println(lngDiff+"    "+latDiff);
			double cosV = lngDiff/(Math.sqrt(lngDiff*lngDiff+latDiff*latDiff)); //正弦值
			double sinV = latDiff/(Math.sqrt(lngDiff*lngDiff+latDiff*latDiff)); //余弦值
			
			double oneLng = cellbaiduLng + val*imgSize*cosV;
			double oneLat = cellbaiduLat + val*imgSize*sinV;
			//System.out.println(oneLng+"    "+oneLat);
			point = new ReferencePoint(oneLng, oneLat);
			points.add(point);
		}
		long etime = System.currentTimeMillis();
		logger.debug("数据通过基准点转为百度坐标，耗时："+(etime-stime));
		
		//以服务小区为中心，从第一象限到第四象限，按照角度大小排序
		stime = System.currentTimeMillis();
		List<ReferencePoint> originPoints = ascAndInducePoints(points,cellbaiduLng,cellbaiduLat);
		etime = System.currentTimeMillis();
		logger.debug("数据坐标以服务小区为中心按照角度大小排序，耗时："+(etime-stime));
		
		//计算矢量相加后的点坐标
		stime = System.currentTimeMillis();
		double vectorlng = cellbaiduLng;
		double vectorlat = cellbaiduLat;
 		for (ReferencePoint p : originPoints) {
			double lngdiff = p.getBaiduLng() - cellbaiduLng;
			double latdiff = p.getBaiduLat() - cellbaiduLat;
	 		//System.out.println(lngdiff+	"      "+latdiff);
			vectorlng += lngdiff*evalCoeff;
			vectorlat += latdiff*evalCoeff;
		}

 		//System.out.println(vectorlng + "   " +vectorlat);
 		List<Map<String,Object>> vectorPoints = new ArrayList<Map<String,Object>>();
 		Map<String,Object> vectorPoint = new HashMap<String, Object>();
 		vectorPoint.put("lng", vectorlng);
 		vectorPoint.put("lat", vectorlat);
 		vectorPoints.add(vectorPoint);
 		result.put("vectorPoint", vectorPoints);
 		
		//计算曲线的点集合
//		List<Map<String,Object>> curvePoints = calculatePoints(originPoints);
//		result.put("curvePoints", curvePoints);
		/* 直接将8个象限坐标点返回  绘制曲线，由前端贝塞尔类库实现*/
		List<Map<String,Object>> oriPointsList = new ArrayList<Map<String,Object>>();
		Map<String,Object> ori = new HashMap<String,Object>();
		for (int i = 0; i < originPoints.size(); i++) {

			ori.put(i+"",originPoints.get(i));
		}
		oriPointsList.add(ori);
		result.put("curvePoints", oriPointsList);
		etime = System.currentTimeMillis();
		logger.debug("通过数据计算曲线点集合，耗时："+(etime-stime));
		
		return result;
	}

	/**
	 *
	 * @param dynaCoverData
	 * @param imgCoeff
	 * @param imgSizeCoeff
	 * @param cellStationSpaces
	 * @param rsrp  RSRP0  OR RSRP2
	 * @return
	 */
	private Map<String, List<Map<String, Object>>> calc4GDynaCoveragePointsData2(
			List<Map<String, Object>> dynaCoverData,
			double imgCoeff,double imgSizeCoeff,Map<String,Object> cellStationSpaces,String rsrp) {

		String rsrpVal = "VAL1";
		if(rsrp.toUpperCase().equals("RSRP2")){
			rsrpVal = "VAL2";
		}
		long stime = System.currentTimeMillis();
	
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String,Object>>>();
		//主小区经纬度信息
		double cellLng = Double.parseDouble(dynaCoverData.get(0).get("CELL_LNG").toString());
		double cellLat = Double.parseDouble(dynaCoverData.get(0).get("CELL_LAT").toString());
		

		//如果图形大小系数未设置，取默认值
		double imgSize = 0.0;
		if(imgSize == 0) {
			imgSize = defaultImgSize;
		}
		
		/*String cellBaiduPoint[] = getBaiduLnglat(cellLng, cellLat,standardPoints);
		if(cellBaiduPoint==null) {
			return null;
		}
		double cellbaiduLng = Double.parseDouble(cellBaiduPoint[0]);
		double cellbaiduLat = Double.parseDouble(cellBaiduPoint[1]);*/
		double cellbaiduLng = cellLng;
		double cellbaiduLat = cellLat;

		List<ReferencePointCellId> points = new ArrayList<ReferencePointCellId>();
		ReferencePointCellId point = null;
		//通过经纬度存储对应的矢量长度
		Map<String, Double> lnglatToVector=new HashMap<String,Double>();

		/*                          A小区方向评估【CalAngle-A】 开始                 */
		/* 小区评估方向矢量X，Y坐标 */
		double cellEvalX = 0;
		double cellEvalY = 0;
		for (Map<String, Object> one : dynaCoverData) {
			double val = Double.parseDouble(one.get(rsrpVal).toString());

			//MR小区数据即为邻区数据，这里的经纬度信息即为邻区经纬度【不同】
			double lng = Double.parseDouble(one.get("LNG").toString());
			double lat = Double.parseDouble(one.get("LAT").toString());
			String ncell_id = one.get("NCELL_ID").toString();
			//增加这里获取的小区经纬度信息即为主小区经纬度信息【相同】
			double ncellLng = Double.parseDouble(one.get("CELL_LNG").toString());
			double ncellLat = Double.parseDouble(one.get("CELL_LAT").toString());

			if(lng==0 || lat==0) continue;
			//邻区的经纬度信息【不同】
			double oneBaiduLng = lng;
			double oneBaiduLat = lat;

			double lngDiff = Math.abs(oneBaiduLng - cellbaiduLng);
			double latDiff = Math.abs(oneBaiduLat - cellbaiduLat);
			double cosV = lngDiff/(Math.sqrt(lngDiff*lngDiff+latDiff*latDiff)); //正弦值->余弦值
			double sinV = latDiff/(Math.sqrt(lngDiff*lngDiff+latDiff*latDiff)); //余弦值->正弦值
//			System.out.println(val*imgSize*cosV+"----("+val+")---("+imgSize+")----("+cosV+")||"+val*imgSize*sinV+"---("+sinV);
			cellEvalX += val*cosV;
			cellEvalY += val*sinV;

			//缓存没有任何变化的小区经纬度信息为后续覆盖图使用
			point = new ReferencePointCellId(oneBaiduLng, oneBaiduLat,ncell_id);
			points.add(point);
			//缓存矢量长度：此时矢量长度是关联度*站间距，后面还要引入图形大小系数
//			lnglatToVector.put(oneLng+"_"+oneLat, val);
			//存储邻区到服务小区的角度大小
//			lnglatToVector.put(oneBaiduLng+"_"+oneBaiduLat, val);
			lnglatToVector.put(ncell_id+"",val);
		}
		/* 小区评估方向矢量经纬坐标 */
//		System.out.println("cellEvalX=="+cellEvalX+"-----cellEvalY=="+cellEvalY);
		double cellEvalLng = cellbaiduLng+cellEvalX;
		double cellEvalLat = cellbaiduLat+cellEvalY;
		List<Map<String,Object>> vectorPoints = new ArrayList<Map<String,Object>>();
		Map<String,Object> vectorPoint = new HashMap<String, Object>();
		vectorPoint.put("lng", cellEvalLng);
		vectorPoint.put("lat", cellEvalLat);
		vectorPoints.add(vectorPoint);
		result.put("vectorPoint", vectorPoints);
		/*                               A小区方向评估【CalAngle-A】 结束                                     */
		long etime = System.currentTimeMillis();
		logger.debug("数据通过基准点转为百度坐标，耗时："+(etime-stime));
		
		//以服务小区为中心，从第一象限到第四象限，按照角度大小排序
		//归纳16个点
		List<ReferencePoint> originPoints = ascAndInduce16PointsStage2(points,cellbaiduLng,cellbaiduLat,lnglatToVector,cellStationSpaces);

		/* 直接将16个象限坐标点返回  绘制曲线，由前端贝塞尔类库实现*/
		List<Map<String,Object>> oriPointsList = new ArrayList<Map<String,Object>>();
		Map<String,Object> ori = new HashMap<String,Object>();
		for (int i = 0; i < originPoints.size(); i++) {

			ori.put(i+"",originPoints.get(i));
		}
		oriPointsList.add(ori);
		result.put("curvePoints", oriPointsList);
		//
		/*List<Map<String,Object>> oriPointsList_ = new ArrayList<Map<String,Object>>();
		Map<String,Object> ori_ = new HashMap<String,Object>();
		for (int i = 0; i < originPoints_.size(); i++) {

			ori_.put(i+"",originPoints_.get(i));
		}
		oriPointsList_.add(ori_);
		result.put("curvePoints_", oriPointsList_);*/
		etime = System.currentTimeMillis();
		logger.debug("通过数据计算折线点集合，耗时："+(etime-stime));
		
		return result;
	}
	/*
	 * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳叠加成16个点 阶段2
	 */
	private List<ReferencePoint> ascAndInduce16PointsStage2(List<ReferencePointCellId> points,
			double cellbaiduLng, double cellbaiduLat,Map<String, Double> lnglatToVector,Map<String,Object> cellStationSpaces) {
		
		List<ReferencePointCellId> one = new ArrayList<ReferencePointCellId>();
		List<ReferencePointCellId> two = new ArrayList<ReferencePointCellId>();
		List<ReferencePointCellId> three = new ArrayList<ReferencePointCellId>();
		List<ReferencePointCellId> four = new ArrayList<ReferencePointCellId>();

		List<ReferencePoint> sumPoints = new ArrayList<ReferencePoint>();

		/*int rand = (int)(Math.random()*8)+1;
		rand = 20;
		double minVal=0.00002;//合并后矢量值设置最小值，可调，默认为0.05
		minVal=0.00000001;*/
		int oneCnt=0,twoCnt=0,threeCnt=0,fourCnt=0;
		//0.0006相对好些
		//划分落在每个象限的点
		//逆时针划分象限
		for (ReferencePointCellId p : points) {
			//第一象限
			if(p.getBaiduLng()>=cellbaiduLng && p.getBaiduLat()>cellbaiduLat) 
				{one.add(p);oneCnt++;}
			//第二象限
			if(p.getBaiduLng()<cellbaiduLng && p.getBaiduLat()>=cellbaiduLat) 
				{two.add(p);twoCnt++;}
			//第三象限
			if(p.getBaiduLng()<=cellbaiduLng && p.getBaiduLat()<cellbaiduLat) 
				{three.add(p);threeCnt++;}
			//第四象限
			if(p.getBaiduLng()>cellbaiduLng && p.getBaiduLat()<=cellbaiduLat)
				{four.add(p);fourCnt++;}
		}
		System.out.println("one===>"+oneCnt+"  tow====>"+twoCnt+" three====>"+threeCnt+"  four====>"+fourCnt);
		ReferencePoint temp = null;
		double xxi,yyi,xxj,yyj,sini,sinj,tanV,angleV,ideaDis,corDegree,cosV,sinV,angleV1;
		double ncellLng,ncellLat;
		String ncellId;
		double vectorLenth1 = 0 ;
		double vectorLenth2 = 0 ;
		double vectorLenth3 = 0 ;
		double vectorLenth4 = 0 ;
		double xxi1 = 0,xxi2 = 0,xxi3 = 0,xxi4 = 0,yyi1 = 0,yyi2 = 0,yyi3 = 0,yyi4 = 0;
		//将各自象限内的点按照角度划分顺序
		//第一象限
		for (int i = 0; i < one.size(); i++) {

			ncellLng = one.get(i).getBaiduLng();
			ncellLat = one.get(i).getBaiduLat();
			ncellId = one.get(i).getCellId();
			xxi = ncellLng - cellbaiduLng;
			yyi = ncellLat - cellbaiduLat;
			angleV = calcAngle(xxi,yyi);
			//矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
			//还需要重构小区站间距，经度_纬度的方式
			ideaDis = Double.parseDouble(cellStationSpaces.get(ncellId+"").toString());
			corDegree = Double.parseDouble(lnglatToVector.get(ncellId+"").toString());
			if (angleV>=0 && angleV<22.5){
				//决定矢量长度
				vectorLenth1 += ideaDis*defaultImgSize*corDegree;
				//经纬度矢量相加决定方向角
				xxi1+=xxi;
				yyi1+=yyi;
			}else if(angleV>=22.5 && angleV<45){
				vectorLenth2 += ideaDis*defaultImgSize*corDegree;
				xxi2+=xxi;
				yyi2+=yyi;
			}else if(angleV>=45 && angleV<67.5){
				vectorLenth3 += ideaDis*defaultImgSize*corDegree;
				xxi3+=xxi;
				yyi3+=yyi;
			}else if(angleV>=67.5 && angleV<90){
				vectorLenth4 += ideaDis*defaultImgSize*corDegree;
				xxi4+=xxi;
				yyi4+=yyi;
			}
		}
		/* 第一象限第一区域坐标矢量 */
		if (vectorLenth1!= 0 && (xxi1!=0 || yyi1!=0)){
			cosV = xxi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			sinV = yyi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			xxi1 = vectorLenth1*cosV;
			yyi1 = vectorLenth1*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1,cellbaiduLat + yyi1));
		}
		/* 第一象限第二区域坐标矢量 */
		if (vectorLenth2!= 0 && (xxi2!=0 || yyi2!=0)){
			cosV = xxi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			sinV = yyi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			xxi2 = vectorLenth2*cosV;
			yyi2 = vectorLenth2*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2,cellbaiduLat + yyi2));
		}
		/* 第一象限第三区域坐标矢量 */
		if (vectorLenth3!= 0 && (xxi3!=0 || yyi3!=0)){
			cosV = xxi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			sinV = yyi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			xxi3 = vectorLenth3*cosV;
			yyi3 = vectorLenth3*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3,cellbaiduLat + yyi3));
		}
        /* 第一象限第四区域坐标矢量 */
		if (vectorLenth4!= 0 && (xxi4!=0 || yyi4!=0)){
			cosV = xxi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			sinV = yyi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			xxi4 = vectorLenth4*cosV;
			yyi4 = vectorLenth4*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4,cellbaiduLat + yyi4));
		}
		/* 初始化 */
		vectorLenth1 = 0;
		vectorLenth2 = 0;
		vectorLenth3 = 0;
		vectorLenth4 = 0;
		xxi1 = 0;
		yyi1 = 0;
		xxi2 = 0;
		yyi2 = 0;
		xxi3 = 0;
		yyi3 = 0;
		xxi4 = 0;
		yyi4 = 0;
		//第二象限
		for (int i = 0; i < two.size(); i++) {

			ncellLng = two.get(i).getBaiduLng();
			ncellLat = two.get(i).getBaiduLat();
			ncellId = two.get(i).getCellId();
			xxi = ncellLng - cellbaiduLng;
			yyi = ncellLat - cellbaiduLat;
			angleV = calcAngle(xxi,yyi);
			//矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
			//还需要重构小区站间距，经度_纬度的方式
			ideaDis = Double.parseDouble(cellStationSpaces.get(ncellId+"").toString());
			corDegree = Double.parseDouble(lnglatToVector.get(ncellId+"").toString());
			if (angleV>=90 && angleV<112.5){
				//决定矢量长度
				vectorLenth1 += ideaDis*defaultImgSize*corDegree;
				//经纬度矢量相加决定方向角
				xxi1+=xxi;
				yyi1+=yyi;
			}else if(angleV>=112.5 && angleV<135){
				vectorLenth2 += ideaDis*defaultImgSize*corDegree;
				xxi2+=xxi;
				yyi2+=yyi;
			}else if(angleV>=135 && angleV<157.5){
				vectorLenth3 += ideaDis*defaultImgSize*corDegree;
				xxi3+=xxi;
				yyi3+=yyi;
			}else if(angleV>=157.5 && angleV<180){
				vectorLenth4 += ideaDis*defaultImgSize*corDegree;
				xxi4+=xxi;
				yyi4+=yyi;
			}
		}
		/* 第二象限第一区域坐标矢量 */
		if (vectorLenth1!= 0 && (xxi1!=0 || yyi1!=0)){
			cosV = xxi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			sinV = yyi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			xxi1 = vectorLenth1*cosV;
			yyi1 = vectorLenth1*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1,cellbaiduLat + yyi1));
		}
		/* 第二象限第二区域坐标矢量 */
		if (vectorLenth2!= 0 && (xxi2!=0 || yyi2!=0)){
			cosV = xxi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			sinV = yyi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			xxi2 = vectorLenth2*cosV;
			yyi2 = vectorLenth2*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2,cellbaiduLat + yyi2));
		}
		/* 第二象限第三区域坐标矢量 */
		if (vectorLenth3!= 0 && (xxi3!=0 || yyi3!=0)){
			cosV = xxi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			sinV = yyi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			xxi3 = vectorLenth3*cosV;
			yyi3 = vectorLenth3*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3,cellbaiduLat + yyi3));
		}
        /* 第二象限第四区域坐标矢量 */
		if (vectorLenth4!= 0 && (xxi4!=0 || yyi4!=0)){
			cosV = xxi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			sinV = yyi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			xxi4 = vectorLenth4*cosV;
			yyi4 = vectorLenth4*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4,cellbaiduLat + yyi4));
		}

		/* 初始化 */
		vectorLenth1 = 0;
		vectorLenth2 = 0;
		vectorLenth3 = 0;
		vectorLenth4 = 0;
		xxi1 = 0;
		yyi1 = 0;
		xxi2 = 0;
		yyi2 = 0;
		xxi3 = 0;
		yyi3 = 0;
		xxi4 = 0;
		yyi4 = 0;
		//第三象限
		for (int i = 0; i < three.size(); i++) {

			ncellLng = three.get(i).getBaiduLng();
			ncellLat = three.get(i).getBaiduLat();
			ncellId = three.get(i).getCellId();
			xxi = ncellLng - cellbaiduLng;
			yyi = ncellLat - cellbaiduLat;
			angleV = calcAngle(xxi,yyi);
			//矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
			//还需要重构小区站间距，经度_纬度的方式
			ideaDis = Double.parseDouble(cellStationSpaces.get(ncellId+"").toString());
			corDegree = Double.parseDouble(lnglatToVector.get(ncellId+"").toString());
			if (angleV>=180 && angleV<202.5){
				//决定矢量长度
				vectorLenth1 += ideaDis*defaultImgSize*corDegree;
				//经纬度矢量相加决定方向角
				xxi1+=xxi;
				yyi1+=yyi;
			}else if(angleV>=202.5 && angleV<225){
				vectorLenth2 += ideaDis*defaultImgSize*corDegree;
				xxi2+=xxi;
				yyi2+=yyi;
			}else if(angleV>=225 && angleV<247.5){
				vectorLenth3 += ideaDis*defaultImgSize*corDegree;
				xxi3+=xxi;
				yyi3+=yyi;
			}else if(angleV>=247.5 && angleV<270){
				vectorLenth4 += ideaDis*defaultImgSize*corDegree;
				xxi4+=xxi;
				yyi4+=yyi;
			}
		}
		/* 第三象限第一区域坐标矢量 */
		if (vectorLenth1!= 0 && (xxi1!=0 || yyi1!=0)){
			cosV = xxi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			sinV = yyi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			xxi1 = vectorLenth1*cosV;
			yyi1 = vectorLenth1*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1,cellbaiduLat + yyi1));
		}
		/* 第三象限第二区域坐标矢量 */
		if (vectorLenth2!= 0 && (xxi2!=0 || yyi2!=0)){
			cosV = xxi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			sinV = yyi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			xxi2 = vectorLenth2*cosV;
			yyi2 = vectorLenth2*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2,cellbaiduLat + yyi2));
		}
		/* 第三象限第三区域坐标矢量 */
		if (vectorLenth3!= 0 && (xxi3!=0 || yyi3!=0)){
			cosV = xxi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			sinV = yyi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			xxi3 = vectorLenth3*cosV;
			yyi3 = vectorLenth3*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3,cellbaiduLat + yyi3));
		}
        /* 第三象限第四区域坐标矢量 */
		if (vectorLenth4!= 0 && (xxi4!=0 || yyi4!=0)){
			cosV = xxi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			sinV = yyi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			xxi4 = vectorLenth4*cosV;
			yyi4 = vectorLenth4*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4,cellbaiduLat + yyi4));
		}
		/* 初始化 */
		vectorLenth1 = 0;
		vectorLenth2 = 0;
		vectorLenth3 = 0;
		vectorLenth4 = 0;
		xxi1 = 0;
		yyi1 = 0;
		xxi2 = 0;
		yyi2 = 0;
		xxi3 = 0;
		yyi3 = 0;
		xxi4 = 0;
		yyi4 = 0;
		//第四象限
		for (int i = 0; i < four.size(); i++) {

			ncellLng = four.get(i).getBaiduLng();
			ncellLat = four.get(i).getBaiduLat();
			ncellId = four.get(i).getCellId();
			xxi = ncellLng - cellbaiduLng;
			yyi = ncellLat - cellbaiduLat;
			angleV = calcAngle(xxi,yyi);
			//矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
			//还需要重构小区站间距，经度_纬度的方式
			ideaDis = Double.parseDouble(cellStationSpaces.get(ncellId+"").toString());
			corDegree = Double.parseDouble(lnglatToVector.get(ncellId+"").toString());
			if (angleV>=270 && angleV<292.5){
				//决定矢量长度
				vectorLenth1 += ideaDis*defaultImgSize*corDegree;
				//经纬度矢量相加决定方向角
				xxi1+=xxi;
				yyi1+=yyi;
			}else if(angleV>=292.5 && angleV<315){
				vectorLenth2 += ideaDis*defaultImgSize*corDegree;
				xxi2+=xxi;
				yyi2+=yyi;
			}else if(angleV>=315 && angleV<337.5){
				vectorLenth3 += ideaDis*defaultImgSize*corDegree;
				xxi3+=xxi;
				yyi3+=yyi;
			}else if(angleV>=337.5 && angleV<360){
				vectorLenth4 += ideaDis*defaultImgSize*corDegree;
				xxi4+=xxi;
				yyi4+=yyi;
			}
		}
		/* 第四象限第一区域坐标矢量 */
		if (vectorLenth1!= 0 && (xxi1!=0 || yyi1!=0)){
			cosV = xxi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			sinV = yyi1/Math.sqrt(xxi1*xxi1+yyi1*yyi1);
			xxi1 = vectorLenth1*cosV;
			yyi1 = vectorLenth1*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1,cellbaiduLat + yyi1));
		}
		/* 第四象限第二区域坐标矢量 */
		if (vectorLenth2!= 0 && (xxi2!=0 || yyi2!=0)){
			cosV = xxi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			sinV = yyi2/Math.sqrt(xxi2*xxi2+yyi2*yyi2);
			xxi2 = vectorLenth2*cosV;
			yyi2 = vectorLenth2*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2,cellbaiduLat + yyi2));
		}
		/* 第四象限第三区域坐标矢量 */
		if (vectorLenth3!= 0 && (xxi3!=0 || yyi3!=0)){
			cosV = xxi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			sinV = yyi3/Math.sqrt(xxi3*xxi3+yyi3*yyi3);
			xxi3 = vectorLenth3*cosV;
			yyi3 = vectorLenth3*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3,cellbaiduLat + yyi3));
		}
        /* 第四象限第四区域坐标矢量 */
		if (vectorLenth4!= 0 && (xxi4!=0 || yyi4!=0)){
			cosV = xxi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			sinV = yyi4/Math.sqrt(xxi4*xxi4+yyi4*yyi4);
			xxi4 = vectorLenth4*cosV;
			yyi4 = vectorLenth4*sinV;
			sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4,cellbaiduLat + yyi4));
		}
		/*System.out.println("第一次十六分区域矢量汇总后得到多少坐标点sumPoints= "+sumPoints.size());
		for (int i = 0; i < sumPoints.size(); i++) {
			System.out.println("第一次 打印  sumPoints==="+sumPoints.get(i).toString());
		}*/
		/*  十六分区域两两相邻矢量再次相加 绿色矢量（回落折线图）=相邻两个矢量相加 * 折线图形状系数（默认为0.3）  */
		double shapeFactor = 0.3;
		List<ReferencePoint> tempPoints = new ArrayList<ReferencePoint>();
		for (int i = 0; i < sumPoints.size(); i++) {
			for (int j = i+1; j < sumPoints.size(); j++) {
				xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
				yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
				xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
				yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
				//夹角大于180度放弃两坐标点矢量累加
				if (Math.abs(calcAngle(xxi,yyi)-calcAngle(xxj,yyj))>180){
					continue;
				}
				//矢量X
				xxi = xxi+xxj;
				//矢量Y
				yyi = yyi+yyj;
				vectorLenth1 = Math.sqrt(xxi*xxi+yyi*yyi)*shapeFactor;
				cosV = xxi/Math.sqrt(xxi*xxi+yyi*yyi);
				sinV = yyi/Math.sqrt(xxi*xxi+yyi*yyi);
				xxi = vectorLenth1*cosV;
				yyi = vectorLenth1*sinV;
				tempPoints.add(new ReferencePoint(cellbaiduLng + xxi,cellbaiduLat + yyi));
				if (j==sumPoints.size()-1){
					xxi = sumPoints.get(0).getBaiduLng() - cellbaiduLng;
					yyi = sumPoints.get(0).getBaiduLat() - cellbaiduLat;
					//矢量X
					xxi = xxi+xxj;
					//矢量Y
					yyi = yyi+yyj;
					vectorLenth1 = Math.sqrt(xxi*xxi+yyi*yyi)*shapeFactor;
					cosV = xxi/Math.sqrt(xxi*xxi+yyi*yyi);
					sinV = yyi/Math.sqrt(xxi*xxi+yyi*yyi);
					xxi = vectorLenth1*cosV;
					yyi = vectorLenth1*sinV;
					tempPoints.add(new ReferencePoint(cellbaiduLng + xxi,cellbaiduLat + yyi));
				}else{
					break;
				}
			}
		}
		/*System.out.println("再次汇总后tempPoints大小==="+tempPoints.size());
		System.out.println("合并前sumPoints大小==="+sumPoints.size());*/
		if (tempPoints.size()!=0){
			//合并
			sumPoints.addAll(tempPoints);
		}
//		System.out.println("合并后sumPoints大小==="+sumPoints.size());
		/*for (int i = 0; i < sumPoints.size(); i++) {
			System.out.println("第二次 打印sumPoints==="+sumPoints.get(i).toString());
		}*/
		// 再次角度大小排序 依次从小到大排列
		//保存矢量长度
//		Map<String,Double> verLens = new HashMap<String,Double>();
		double verLens[] = new double[sumPoints.size()];
		for (int i = 0; i < sumPoints.size(); i++) {

			xxi = sumPoints.get(i).getBaiduLng()-cellbaiduLng;
			yyi = sumPoints.get(i).getBaiduLat()-cellbaiduLat;
			vectorLenth1 = Math.sqrt(xxi*xxi+yyi*yyi);
//			verLens.put(i+"",vectorLenth1);
			verLens[i] = vectorLenth1;
			for (int j = i+1; j < sumPoints.size(); j++) {
				xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
				yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
				xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
				yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
				angleV = calcAngle(xxi,yyi);
				angleV1 = calcAngle(xxj,yyj);
				if(angleV > angleV1) {
					temp = sumPoints.get(i);
					sumPoints.set(i, sumPoints.get(j));
					sumPoints.set(j, temp);
				}
			}
		}
		// 再次矢量长度大小排序 依次从小到大排列
		for (int i = 0; i < sumPoints.size(); i++) {
			for (int j = i+1; j < sumPoints.size(); j++) {
				xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
				yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
				xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
				yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
				angleV = calcAngle(xxi,yyi);
				angleV1 = calcAngle(xxj,yyj);
				if(angleV > angleV1) {
					temp = sumPoints.get(i);
					sumPoints.set(i, sumPoints.get(j));
					sumPoints.set(j, temp);
				}
			}
		}
		//根据集合中最大最小值的比值确定各矢量坐标到原点坐标收缩的倍数
//		verLens = sortMapByValue(verLens);
		Arrays.sort(verLens);
		/*for (int i = 0 ;i <verLens.length;i++){
			logger.debug("i=="+i+"------value==="+verLens[i]);
		}*/
		double maxVal = verLens[sumPoints.size()-1];
		double minVal = verLens[0];
		logger.debug("最大矢量长度==="+maxVal);
		logger.debug("最小矢量长度==="+minVal);
		double ratio = maxVal/minVal;
		logger.debug("比值大小   ratio================"+ratio);
		for (int i = 0; i < sumPoints.size(); i++) {
			//策略调整
			if (maxVal>0.04){
				if (ratio>10000){
					ratio = 100;
				}else if(10000>=ratio && ratio>100){
					ratio = 100;
				}else if (ratio<100){
					ratio =5;
				}
			}else if (0.04>=maxVal && maxVal>0.00099){
				ratio = 1;
			}else if(maxVal<= 0.00099){
				ratio = 0.1;
			}

			xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
			yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
			cosV = xxi/Math.sqrt(xxi*xxi+yyi*yyi);
			sinV = yyi/Math.sqrt(xxi*xxi+yyi*yyi);
			xxi = cosV*Math.sqrt(xxi*xxi+yyi*yyi)/ratio;
			yyi = sinV*Math.sqrt(xxi*xxi+yyi*yyi)/ratio;
			sumPoints.get(i).setBaiduLng(cellbaiduLng+xxi);
			sumPoints.get(i).setBaiduLat(cellbaiduLat+yyi);
		}
		logger.debug("方法结束   sumPoints================"+sumPoints.size());
		return sumPoints;
	}
	public List<ReferencePoint> stage3(double cellbaiduLng,double cellbaiduLat,List<ReferencePoint> sumPoints){
		double xxi,yyi,xxj,yyj,sini,sinj,tanV,angleV,ideaDis,corDegree,cosV,sinV,angleV1;
		double ncellLng,ncellLat;
		double vectorLenth1 = 0 ;
		double vectorLenth2 = 0 ;
		double vectorLenth3 = 0 ;
		double vectorLenth4 = 0 ;
		double xxi1 = 0,xxi2 = 0,xxi3 = 0,xxi4 = 0,yyi1 = 0,yyi2 = 0,yyi3 = 0,yyi4 = 0;
		double shapeFactor = 0.3;
		List<ReferencePoint> tempPoints = new ArrayList<ReferencePoint>();
		for (int i = 0; i < sumPoints.size(); i++) {
			int j = (i+1) % sumPoints.size();
				xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
				yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
				xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
				yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
				System.out.println("xxi="+xxi+"---yyi="+yyi+"-------xxj="+xxj+"---yyi="+yyj);
				//矢量X
				xxi = xxi+xxj;
				//矢量Y
				yyi = yyi+yyj;
				vectorLenth1 = Math.sqrt(xxi*xxi+yyi*yyi)*shapeFactor;
				cosV = xxi/Math.sqrt(xxi*xxi+yyi*yyi);
				sinV = yyi/Math.sqrt(xxi*xxi+yyi*yyi);
				xxi = vectorLenth1*cosV;
				yyi = vectorLenth1*sinV;
				tempPoints.add(new ReferencePoint(cellbaiduLng + xxi,cellbaiduLat + yyi));
				System.out.println("xxi="+xxi+"---yyi="+yyi+"----cellbaiduLng + xxi=="+(cellbaiduLng + xxi)+"------------cellbaiduLat + yyi=="+(cellbaiduLat + yyi));

			if (i==sumPoints.size()-1){
				xxi = sumPoints.get(0).getBaiduLng() - cellbaiduLng;
				yyi = sumPoints.get(0).getBaiduLat() - cellbaiduLat;
				xxj = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
				yyj = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
				//矢量X
				xxi = xxi+xxj;
				//矢量Y
				yyi = yyi+yyj;
				vectorLenth1 = Math.sqrt(xxi*xxi+yyi*yyi)*shapeFactor;
				cosV = xxi/Math.sqrt(xxi*xxi+yyi*yyi);
				sinV = yyi/Math.sqrt(xxi*xxi+yyi*yyi);
				xxi = vectorLenth1*cosV;
				yyi = vectorLenth1*sinV;
				System.out.println("xxi="+xxi+"---yyi="+yyi+"----cellbaiduLng + xxi=="+(cellbaiduLng + xxi)+"------------cellbaiduLat + yyi=="+(cellbaiduLat + yyi));
				tempPoints.add(new ReferencePoint(cellbaiduLng + xxi,cellbaiduLat + yyi));
			}
		}

		return tempPoints;
	}
	/*
	 * map根据value值,从大到小排序
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Double> sortMapByValue(Map<String, Double> unsortMap) {
        List<?> list = new LinkedList(unsortMap.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	/*
	 * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳成8个点
	 */
	private List<ReferencePoint> ascAndInducePoints(List<ReferencePoint> points,
			double cellbaiduLng, double cellbaiduLat) {
		
		List<ReferencePoint> one = new ArrayList<ReferencePoint>();
		List<ReferencePoint> two = new ArrayList<ReferencePoint>();
		List<ReferencePoint> three = new ArrayList<ReferencePoint>();
		List<ReferencePoint> four = new ArrayList<ReferencePoint>();

		//划分落在每个象限的点

		for (ReferencePoint p : points) {
			//第一象限
			if(p.getBaiduLng()>=cellbaiduLng && p.getBaiduLat()>cellbaiduLat) 
				one.add(p);
			//第二象限
			if(p.getBaiduLng()<cellbaiduLng && p.getBaiduLat()>=cellbaiduLat) 
				two.add(p);
			//第三象限
			if(p.getBaiduLng()<=cellbaiduLng && p.getBaiduLat()<cellbaiduLat) 
				three.add(p);
			//第四象限
			if(p.getBaiduLng()>cellbaiduLng && p.getBaiduLat()<=cellbaiduLat)
				four.add(p);
		}
		System.out.print("one=="+one.size()+"----two==="+two.size()+"-----three=="+three.size()+"-----four="+four.size());
		ReferencePoint temp = null;
		double xxi,yyi,xxj,yyj,sini,sinj;
		//将各自象限内的点按照角度划分顺序
		//第一象限
		for (int i = 0; i < one.size(); i++) {
			for (int j = i+1; j < one.size(); j++) {
				xxi = one.get(i).getBaiduLng() - cellbaiduLng;
				yyi = one.get(i).getBaiduLat() - cellbaiduLat;
				xxj = one.get(j).getBaiduLng() - cellbaiduLng;
				yyj = one.get(j).getBaiduLat() - cellbaiduLat;				
				sini = yyi/(Math.sqrt(xxi*xxi+yyi*yyi));
				sinj = yyj/(Math.sqrt(xxj*xxj+yyj*yyj));
				if(sini > sinj) {
					temp = one.get(i);
					one.set(i, one.get(j));
					one.set(j, temp);
				}
			}
		}
		//第二象限
		for (int i = 0; i < two.size(); i++) {
			for (int j = i+1; j < two.size(); j++) {
				xxi = two.get(i).getBaiduLng() - cellbaiduLng;
				yyi = two.get(i).getBaiduLat() - cellbaiduLat;
				xxj = two.get(j).getBaiduLng() - cellbaiduLng;
				yyj = two.get(j).getBaiduLat() - cellbaiduLat;				
				sini = yyi/(Math.sqrt(xxi*xxi+yyi*yyi));
				sinj = yyj/(Math.sqrt(xxj*xxj+yyj*yyj));
				if(sini < sinj) {
					temp = two.get(i);
					two.set(i, two.get(j));
					two.set(j, temp);
				}
			}
		}	
		//第三象限
		for (int i = 0; i < three.size(); i++) {
			for (int j = i+1; j < three.size(); j++) {
				xxi = three.get(i).getBaiduLng() - cellbaiduLng;
				yyi = three.get(i).getBaiduLat() - cellbaiduLat;
				xxj = three.get(j).getBaiduLng() - cellbaiduLng;
				yyj = three.get(j).getBaiduLat() - cellbaiduLat;					
				sini = yyi/(Math.sqrt(xxi*xxi+yyi*yyi));
				sinj = yyj/(Math.sqrt(xxj*xxj+yyj*yyj));
				if(sini < sinj) {
					temp = three.get(i);
					three.set(i, three.get(j));
					three.set(j, temp);
				}
			}
		}
		//第四象限
		for (int i = 0; i < four.size(); i++) {
			for (int j = i+1; j < four.size(); j++) {
				xxi = four.get(i).getBaiduLng() - cellbaiduLng;
				yyi = four.get(i).getBaiduLat() - cellbaiduLat;
				xxj = four.get(j).getBaiduLng() - cellbaiduLng;
				yyj = four.get(j).getBaiduLat() - cellbaiduLat;					
				sini = yyi/(Math.sqrt(xxi*xxi+yyi*yyi));
				sinj = yyj/(Math.sqrt(xxj*xxj+yyj*yyj));
				if(sini > sinj) {
					temp = four.get(i);
					four.set(i, four.get(j));
					four.set(j, temp);
				}
			}
		}
		
		List<ReferencePoint> res = new ArrayList<ReferencePoint>();
		//int oneNum=0,twoNum=0,threeNum=0,fourNum=0;
		
		//对每一象限分成两份，取每份的数据矢量相加作为一个参考点，即每个象限取两个参考点
		ReferencePoint tempPoint = null;
		double tempLng = cellbaiduLng;
		double tempLat = cellbaiduLat;
		for (int i = 0; i < one.size()/2; i++) {
			tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = one.size()/2; i < one.size(); i++) {
			tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		//第二象限
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = 0; i < two.size()/2; i++) {
			tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = two.size()/2; i < two.size(); i++) {
			tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		//第三象限
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = 0; i < three.size()/2; i++) {
			tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = three.size()/2; i < three.size(); i++) {
			tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);	
		//第四象限
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = 0; i < four.size()/2; i++) {
			tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);
		tempLng = cellbaiduLng;
		tempLat = cellbaiduLat;
		for (int i = four.size()/2; i < four.size(); i++) {
			tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
			tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
		}
		//System.out.println(tempLng+"---"+tempLat);
		tempPoint = new ReferencePoint(tempLng,tempLat);
		res.add(tempPoint);	

		return res;
	}
	/*
	 * 计算贝塞尔曲线的点集合
	 */
	private List<Map<String, Object>> calculatePoints(List<ReferencePoint> originPoints) {

		int originCount = originPoints.size();
		ReferencePoint midpoints[] = new ReferencePoint[originCount];
		// 生成中点
		for (int i = 0; i < originCount; i++) {
			int nexti = (i + 1) % originCount;
			double x = (originPoints.get(i).getBaiduLng() + originPoints.get(nexti).getBaiduLng()) / 2;
			double y = (originPoints.get(i).getBaiduLat() + originPoints.get(nexti).getBaiduLat()) / 2;
			midpoints[i] = new ReferencePoint(x, y);
		}

		// 平移中点
		ReferencePoint extrapoints[] = new ReferencePoint[2 * originCount];
		for (int i = 0; i < originCount; i++) {
			// int nexti = (i + 1) % originCount;
			int backi = (i + originCount - 1) % originCount;
			ReferencePoint midinmid = new ReferencePoint();
			midinmid.setBaiduLng((midpoints[i].getBaiduLng() + midpoints[backi].getBaiduLng()) / 2);
			midinmid.setBaiduLat((midpoints[i].getBaiduLat() + midpoints[backi].getBaiduLat()) / 2);
			double offsetx = originPoints.get(i).getBaiduLng() - midinmid.getBaiduLng();
			double offsety = originPoints.get(i).getBaiduLat() - midinmid.getBaiduLat();
			int extraindex = 2 * i;
			double x = midpoints[backi].getBaiduLng() + offsetx;
			double y = midpoints[backi].getBaiduLat() + offsety;
			extrapoints[extraindex] = new ReferencePoint(x, y);
			// 朝 originPoint[i]方向收缩
			double addx = (extrapoints[extraindex].getBaiduLng() - originPoints.get(i).getBaiduLng()) * scale;
			double addy = (extrapoints[extraindex].getBaiduLat() - originPoints.get(i).getBaiduLat()) * scale;
			extrapoints[extraindex].setBaiduLng(originPoints.get(i).getBaiduLng() + addx);
			extrapoints[extraindex].setBaiduLat(originPoints.get(i).getBaiduLat() + addy);

			int extranexti = (extraindex + 1) % (2 * originCount);
			x = midpoints[i].getBaiduLng() + offsetx;
			y = midpoints[i].getBaiduLat() + offsety;
			extrapoints[extranexti] = new ReferencePoint(x, y);
			// 朝 originPoint[i]方向收缩
			addx = (extrapoints[extranexti].getBaiduLng() - originPoints.get(i).getBaiduLng()) * scale;
			addy = (extrapoints[extranexti].getBaiduLat() - originPoints.get(i).getBaiduLat()) * scale;
			extrapoints[extranexti].setBaiduLng(originPoints.get(i).getBaiduLng() + addx);
			extrapoints[extranexti].setBaiduLat(originPoints.get(i).getBaiduLat() + addy);
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempP = null;
		ReferencePoint controlPoint[] = new ReferencePoint[4];
		// 生成4控制点，产生贝塞尔曲线
		for (int i = 0; i < originCount; i++) {
			controlPoint[0] = originPoints.get(i);
			int extraindex = 2 * i;
			controlPoint[1] = extrapoints[extraindex + 1];
			int extranexti = (extraindex + 2) % (2 * originCount);
			controlPoint[2] = extrapoints[extranexti];
			int nexti = (i + 1) % originCount;
			controlPoint[3] = originPoints.get(nexti);
			float u = 1;
			while (u >= 0) {
				// 计算贝塞尔曲线
				double px = bezier3funcX(u, controlPoint);
				double py = bezier3funcY(u, controlPoint);
				// u的步长决定曲线的疏密
				u -= step;
				tempP = new HashMap<String, Object>();
				tempP.put("lng", px);
				tempP.put("lat", py);
				// 存入曲线点
				result.add(tempP);
			}
		}

		return result;
	}

	private double bezier3funcX(float uu, ReferencePoint[] controlPoint) {
		double part0 = controlPoint[0].getBaiduLng() * uu * uu * uu;
		double part1 = 3 * controlPoint[1].getBaiduLng() * uu * uu * (1 - uu);
		double part2 = 3 * controlPoint[2].getBaiduLng() * uu * (1 - uu) * (1 - uu);
		double part3 = controlPoint[3].getBaiduLng() * (1 - uu) * (1 - uu) * (1 - uu);
		return part0 + part1 + part2 + part3;
	}

	private double bezier3funcY(float uu, ReferencePoint[] controlPoint) {
		double part0 = controlPoint[0].getBaiduLat() * uu * uu * uu;
		double part1 = 3 * controlPoint[1].getBaiduLat() * uu * uu * (1 - uu);
		double part2 = 3 * controlPoint[2].getBaiduLat() * uu * (1 - uu) * (1 - uu);
		double part3 = controlPoint[3].getBaiduLat() * (1 - uu) * (1 - uu) * (1 - uu);
		return part0 + part1 + part2 + part3;
	}

	class ReferencePoint {
		double baiduLng;
		double baiduLat;

		public ReferencePoint(){
		}
		public ReferencePoint(double baiduLng, double baiduLat) {
			this.baiduLng = baiduLng;
			this.baiduLat = baiduLat;
		}
		public double getBaiduLng() {
			return baiduLng;
		}
		public void setBaiduLng(double baiduLng) {
			this.baiduLng = baiduLng;
		}
		public double getBaiduLat() {
			return baiduLat;
		}
		public void setBaiduLat(double baiduLat) {
			this.baiduLat = baiduLat;
		}
		@Override
		public String toString() {
			return "ReferencePoint [baiduLng=" + baiduLng + ", baiduLat="
					+ baiduLat + "]";
		}
	}
	class ReferencePointCellId {
		double baiduLng;
		double baiduLat;
		String cellId;

		public ReferencePointCellId(){
		}
		public ReferencePointCellId(double baiduLng, double baiduLat, String cellId) {
			this.baiduLng = baiduLng;
			this.baiduLat = baiduLat;
			this.cellId = cellId;
		}

		public String getCellId() {
			return cellId;
		}
		public void setCellId(String cellId) {
			this.cellId = cellId;
		}
		public double getBaiduLng() {
			return baiduLng;
		}
		public void setBaiduLng(double baiduLng) {
			this.baiduLng = baiduLng;
		}
		public double getBaiduLat() {
			return baiduLat;
		}
		public void setBaiduLat(double baiduLat) {
			this.baiduLat = baiduLat;
		}

		@Override
		public String toString() {
			return "ReferencePointCellId{" +
					"baiduLng=" + baiduLng +
					", baiduLat=" + baiduLat +
					", cellId=" + cellId +
					'}';
		}
	}
	public double calcAngle(double x, double y) {
		double d = 0.0;
		double tan = y / x;
		if (x == 0.0 && y == 0.0) {
			d = 0.0;
		} else if (x >= 0.0 && y == 0.0) {
			d = 90.0;
		} else if (x < 0 && y == 0) {
			d = 180.0;
		} else if (x == 0.0 && y < 0.0) {
			d = 270.0;
		} else {
			d = Math.atan(tan);
		}
		if (x > 0.0 && y > 0.0) {
			d = d * 180 / Math.PI;
		} else if (x < 0.0 && y != 0.0) {
			d = 180 + d * 180 / Math.PI;
		} else if (x > 0.0 && y < 0.0) {
			d = 360 + d * 180 / Math.PI;
		}
		return d;
	}
    /*public static void main(String[] args){
		System.out.println("tan ============"+Math.tan (1));
		System.out.println("tan ============"+Math.atan (1));
		System.out.println("tan ============"+Math.toDegrees (Math.tan (1)));
		System.out.println("tan ============"+Math.toDegrees (Math.atan (-1)));
		System.out.println("tan ============"+Math.toDegrees (Math.atan (10)));
		double degrees = 45.0;
		degrees = 210;
		double radians = Math.toRadians(degrees);
		System.out.println("radians=="+radians);
		System.out.println(radians/Math.PI*180);

		int x1=0,x2=30; //点1坐标;
		int y1=30,y2=0; //点2坐标
		int x=Math.abs(x1-x2);
		int y=Math.abs(y1-y2);
		double z=Math.sqrt(x*x+y*y);
		int jiaodu=Math.round((float)(Math.asin(y/z)/Math.PI*180));//最终角度
		System.out.println("jiaodu========"+jiaodu);
//		System.out.println(calcAngle(1,-2));
		int a=0,b=0,c=0;
		a = 0;
		b = 0;
		c = 1;
		if(a!=0 && (b!=0||c!=0)){
			System.out.println("满足条件");
		}
		double  xx =Math.atan2(1,1);
		double  yy = Math.toDegrees(xx);
		System.out.println("一 yy="+yy);

		xx =Math.atan2(1,-1);
		yy = Math.toDegrees(xx);
		System.out.println("二 yy="+yy);

		xx =Math.atan2(-1,-1);
		yy = Math.toDegrees(xx);
		System.out.println("三 yy="+yy);

		xx =Math.atan2(-1,1);
		yy = Math.toDegrees(xx);
		System.out.println("四 yy="+yy);
		String ss = "2090391";
		System.out.println(ss.substring(0,ss.length()-1)+"-"+ss.substring(ss.length()-1));
	}*/

}
