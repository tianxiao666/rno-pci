package com.hgicreate.rno.dao;

import com.hgicreate.rno.mapper.LteDynaOverGraphMapper;
import com.hgicreate.rno.properties.RnoProperties;
import com.hgicreate.rno.service.SysAreaSyncService;
import com.hgicreate.rno.tool.RnoHelper;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("rnoLteDynaOverGraphDaoImpl")
public class RnoLteDynaOverGraphDaoImpl implements RnoLteDynaOverGraphDao {

	private static final Logger logger = LoggerFactory.getLogger(RnoLteDynaOverGraphDaoImpl.class);
	@Resource(name = "hadoopConfig")
	private Configuration configuration;
//	private static Configuration configuration = HBaseConfiguration.create();
	@Autowired
	private RnoProperties rnoProperties;
	@Autowired
	private LteDynaOverGraphMapper lteDynaOverGraphMapper;
	@Autowired
	@Qualifier("sysAreaSyncServiceImpl")
	private SysAreaSyncService sysAreaSyncService;

	/*
     * spark
     */
	/*@Value("${spring.datasource.spark.url}")
	private String sparkUrl;
	@Value("${spring.datasource.spark.username}")
	private String sparkUser;
	@Value("${spring.datasource.spark.password}")
	private String sparkPassword;
	@Value("${spring.datasource.spark.driverClassName}")
	private String sparkDriveName;*/
	/**
	 * 获取某个地市 的小区频段类型信息
	 */
	/*public List<Map<String, Object>> queryCellBandTypeByAreaStr(String areaStr){
	   return lteDynaOverGraphMapper.queryCellBandTypeByAreaStr(areaStr);
	}*/

	/**
	 * 从spark数据查询动态覆盖数据
	 * @param cityId
	 * @param lteCellId
	 * @param startDate
	 * @param endDate
	 * @param inOrOut
	 * out 出干扰：即被别人所检测  in 入干扰：即检测到别人
	 * @return
	 */
	public Map<String,List<Map<String,Object>>> queryDynaCoverDataFromSparkMrTable(
			long cityId, String lteCellId,
			String startDate, String endDate,String inOrOut,Connection connection) {
		/*DataSource db = DataSourceBuilder.create().url(sparkUrl).username(sparkUser).password(sparkPassword)
				.driverClassName(sparkDriveName).build();*/
//		 Connection sparkConnection = null;
//		lteCellId = lteCellId.substring(0,lteCellId.length()-1)+"-"+lteCellId.substring(lteCellId.length()-1);
		 Statement stmt = null;
		List<Map<String, String>> mrData = null;
		String sql = "",outSql = "",inSql ="";
		try {
			stmt = connection.createStatement();
		}catch (Exception e){
			logger.debug("准备SPARK数据仓库 stmt 失败！");
			e.printStackTrace();
		}
		List<Map<String,Object>> lteCells = lteDynaOverGraphMapper.queryCellParmsByCityId(cityId);
		Map<String,Object> cellIdToStationSpace = new HashMap<String, Object>();
		for (Map<String, Object> one : lteCells) {
//			System.out.println(one.get("CELL_ID")+"----"+one.get("LON").toString()+"_"+one.get("LAT").toString());
			cellIdToStationSpace.put(one.get("CELL_ID").toString(),
					one.get("STATION_SPACE").toString());
		}
		outSql = "select																			"
				+"	t.cell_id NCELL_ID,                                           "
				+"	c.longitude LNG,                                                                        "
				+"	c.latitude LAT,                                                                        "
				+"	t.ncell_id CELL_ID,                                         "
				+"	d.longitude as CELL_LNG,                                                        "
				+"	d.latitude as CELL_LAT,                                                          "
				+"	t.rsrptimes0 RSRPTIMES0,                                                                       "
				+"	t.rsrptimes2 RSRPTIMES2,                                                                       "
				+"	t.mixingsum,                                                                        "
				+"	t.rsrptimes0 / t.mixingsum as VAL1,                                               "
				+"	t.rsrptimes2 / t.mixingsum as VAL2,                                                "
				+"	t.distance_km*1000 DISTANCE                                                             "
				+"from                                                                                  "
				+"	lte_mr_data t,                                                                      "
				+"	lte_cell_param c,                                                                   "
				+"	lte_cell_param d                                                                    "
				+"where                                                                                 "
				+"	t.cell_id = c.cell_id                                                               "
				+"	and t.ncell_id = d.cell_id                                                          "
				+"	and d.cover_type = '室外'                                                           "
				+"	and t.mixingsum >= 50                                                               "
				+"	and t.distance_km >= 0.1                                                            "
				+"	and t.distance_km <= 5                                                              "
				+"	and t.rsrptimes0 > 0                                                                "
				+"	and t.ncell_id = '"+lteCellId+"'                                                         "
				+"	and t.area_id = "+cityId+"                                                                  "
				+"	and from_unixtime(unix_timestamp(t.record_time, 'yyyyMMdd'))>='"+startDate.trim()+" 00-00-00' "
				+"	and from_unixtime(unix_timestamp(t.record_time, 'yyyyMMdd'))<='"+endDate.trim()+" 00-00-00' ";

		inSql =  "select																					"
				+"	t.cell_id CELL_ID,                                               "
				+"	c.longitude CELL_LNG,                                                                   "
				+"	c.latitude CELL_LAT,                                                                    "
				+"	t.ncell_id NCELL_ID,                                             "
				+"	d.longitude as LNG,                                                                     "
				+"	d.latitude as LAT,                                                                      "
				+"	t.rsrptimes0 RSRPTIMES0,                                                                "
				+"	t.rsrptimes2 RSRPTIMES2,                                                                "
				+"	t.mixingsum,                                                                            "
				+"	t.rsrptimes0 / t.mixingsum as VAL1,                                                     "
				+"	t.rsrptimes2 / t.mixingsum as VAL2,                                                     "
				+"	t.distance_km*1000 DISTANCE                                                             "
				+"from                                                                                      "
				+"	lte_mr_data t,                                                                          "
				+"	lte_cell_param c,                                                                       "
				+"	lte_cell_param d                                                                        "
				+"where                                                                                     "
				+"	t.cell_id = c.cell_id                                                                   "
				+"	and t.ncell_id = d.cell_id                                                              "
				+"	and d.cover_type = '室外'                                                               "
				+"	and t.mixingsum >= 50                                                                   "
				+"	and t.distance_km >= 0.1                                                                "
				+"	and t.distance_km <= 5                                                                  "
				+"	and t.rsrptimes0 > 0                                                                    "
				+"	and t.cell_id = '"+lteCellId+"'                                                              "
				+"	and t.area_id = "+cityId+"                                                                      "
				+"	and from_unixtime(unix_timestamp(t.record_time, 'yyyyMMdd'))>='"+startDate.trim()+" 00-00-00'     "
				+"	and from_unixtime(unix_timestamp(t.record_time, 'yyyyMMdd'))<='"+endDate.trim()+" 00-00-00'    ";
		if(inOrOut.toUpperCase().equals("IN")){
			sql = inSql;
		}else if (inOrOut.toUpperCase().equals("OUT")){
			sql = outSql;
		}
		//MR数据
		List<Map<String,Object>> mrDatas = new ArrayList<Map<String,Object>>();
		try{
			mrDatas = RnoHelper.commonQuery(stmt,sql);
			logger.debug("从spark数据仓库获取数据 为："+mrDatas.size());
//			System.out.println("从spark数据仓库获取数据 为："+mrDatas.size());
		}catch (Exception e){
			logger.debug("SPARK数据仓库获取数据异常！");
			e.printStackTrace();
		}
	    //小区站间距详情
		List<Map<String,Object>> cellStationSps = new ArrayList<Map<String,Object>>();
		cellStationSps.add(cellIdToStationSpace);

		Map<String,List<Map<String,Object>>> result
				= new HashMap<String, List<Map<String,Object>>>();
		/*result.put("res1", res1);
		result.put("res2", res2);
		result.put("resInterDetail", resInterDetail);*/
		result.put("cellStationSps",cellStationSps);
		result.put("res",mrDatas);
//		System.out.println("res1=="+res1.size()+"---res2==="+res2+"----resInterDetail=="+resInterDetail.size());
		return result;
	}
	/**
	 * 获取某个地市 的小区频段类型信息
	 */
	public Map<String, Object> queryCellBandTypeByCityId(long cityId){
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas=lteDynaOverGraphMapper.queryCellParmsByCityId(cityId);
		if(datas!=null){
			for (int i = 0; i < datas.size(); i++) {
				map.put(datas.get(i).get("CELL_ID").toString(), datas.get(i).get("BAND_TYPE"));
			}
		}
		return map;
	}
	
	/**
	 * 获取某个地市 的小区频段类型信息
	 */
	public Map<String, Object> queryLteCellShapeDataByCityId(long cityId){
		
		Map<String, Object> shapes = new HashMap<String, Object>();
		List<Map<String, Object>> shapeDatas=lteDynaOverGraphMapper.queryCellParmsByCityId(cityId);
		if(shapeDatas!=null){
			for (int i = 0; i < shapeDatas.size(); i++) {
				shapes.put(shapeDatas.get(i).get("CELL_ID").toString(), shapeDatas.get(i).get("SHAPE_DATA"));
			}
		}
		return shapes;
	}
	class MeaTimeToMixing{
		String meaTime;
		String mixingSum;
		String cellId;
		String ncellId;
		String distance;
		String timesTotal;
		String cellLon;
		String cellLat;
		String ncellLon;
		String ncellLat;
		String rsr0;
		String rsr2;
		public String getMeaTime() {
			return meaTime;
		}
		public void setMeaTime(String meaTime) {
			this.meaTime = meaTime;
		}
		public String getMixingSum() {
			return mixingSum;
		}
		public void setMixingSum(String mixingSum) {
			this.mixingSum = mixingSum;
		}
		public String getCellId() {
			return cellId;
		}
		public void setCellId(String cellId) {
			this.cellId = cellId;
		}
		public String getNcellId() {
			return ncellId;
		}
		public void setNcellId(String ncellId) {
			this.ncellId = ncellId;
		}
		public String getDistance() {
			return distance;
		}
		public void setDistance(String distance) {
			this.distance = distance;
		}
		public String getTimesTotal() {
			return timesTotal;
		}
		public void setTimesTotal(String timesTotal) {
			this.timesTotal = timesTotal;
		}
		public String getCellLon() {
			return cellLon;
		}
		public void setCellLon(String cellLon) {
			this.cellLon = cellLon;
		}
		public String getCellLat() {
			return cellLat;
		}
		public void setCellLat(String cellLat) {
			this.cellLat = cellLat;
		}
		public String getNcellLon() {
			return ncellLon;
		}
		public void setNcellLon(String ncellLon) {
			this.ncellLon = ncellLon;
		}
		public String getNcellLat() {
			return ncellLat;
		}
		public void setNcellLat(String ncellLat) {
			this.ncellLat = ncellLat;
		}
		public String getRsr0() {
			return rsr0;
		}
		public void setRsr0(String rsr0) {
			this.rsr0 = rsr0;
		}
		public String getRsr2() {
			return rsr2;
		}
		public void setRsr2(String rsr2) {
			this.rsr2 = rsr2;
		}
	}
}
