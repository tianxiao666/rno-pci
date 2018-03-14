package com.hgicreate.rno.dao;

import java.io.IOException;
import java.sql.Statement;
import java.util.*;

import javax.annotation.Resource;

import com.hgicreate.rno.mapper.G4AzimuthCalcMapper;
import com.hgicreate.rno.service.SysAreaSyncService;
import com.hgicreate.rno.tool.RnoHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.TableName;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.hgicreate.rno.model.G4SfDescQueryCond;
import com.hgicreate.rno.properties.RnoProperties;
import com.hgicreate.rno.tool.DateUtil;

@Repository("rno4GAzimuthCalcDaoImpl")
public class Rno4GAzimuthCalcDaoImpl implements Rno4GAzimuthCalcDao {

	private static final Logger logger = LoggerFactory.getLogger(Rno4GAzimuthCalcDaoImpl.class);
	@Resource(name = "hadoopConfig")
	private Configuration configuration;
	@Autowired
	private RnoProperties rnoProperties;

	@Autowired
	private G4AzimuthCalcMapper g4AzimuthCalcMapper;
	@Autowired
	@Qualifier("sysAreaSyncServiceImpl")
	private SysAreaSyncService sysAreaSyncService;


	/**
	 * 通过主进程 jobId 更新 lte_pci_job 表中 MR_JOB_ID
	 * 
	 * @param jobId
	 * @param mrJobId
	 * @param type
	 */
	public void addMapReduceJobId(long jobId, String mrJobId, String type) {
		String table = "rno_ms_4g_azimuth_job";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobId", jobId);
		map.put("mrJobId", mrJobId);
		map.put("table", table);
		g4AzimuthCalcMapper.addMapReduceJobId(map);
	}

	/**
	 * 
	 * @title 通过区域ID获取LTE小区标识对应的小区信息
	 * @param cityId
	 * @return
	 */
	public Map<String, List<String>> getLteCellInfoByCellId(long cityId) {
		logger.debug("进入方法体getLteCellInfoByCellId。cityId=" + cityId);
		Map<String, List<String>> cellToCells = new HashMap<String, List<String>>();

		List<Map<String, Object>> lteCells = g4AzimuthCalcMapper.getLteCellInfoByCellId(cityId);
		if (lteCells == null || lteCells.size() == 0) {
			return null;
		}
		String cellId = "";
		String pci = "";
		String cellName = "";
		String earfcn = "";
		for (Map<String, Object> map : lteCells) {
			// enodebId=Integer.parseInt(map.get("ENODEB_ID").toString());
			cellId = map.get("CELL").toString();
			pci = map.get("PCI").toString();
			cellName = map.get("NAME").toString();
			earfcn = map.get("EARFCN").toString();
			cellToCells.put(cellId, Arrays.asList(cellName, pci, earfcn));
		}
		return cellToCells;
	}

	/*
	 * 统计4g方位角任务数量
	 */
	public long get4GAzimuthCalcTaskCount(Map<String, String> cond, String account){
		logger.debug("进入get4GAzimuthCalcTaskCount，cond={},account={}",cond,account);
		cond.put("account", account);
		return g4AzimuthCalcMapper.get4GAzimuthCalcTaskCount(cond);
	}
	/*
	 * 分页获取4g方位角计算任务信息
	 */
	public List<Map<String, Object>> query4GAzimuthCalcTaskByPage(Map<String, String> cond, String account,
			int startIndex, int cnt) {
		logger.debug("进入query4GAzimuthCalcTaskByPage，cond={},account={}",cond,account);
		cond.put("account", account);
		cond.put("startIndex", startIndex+"");
		cond.put("cnt", cnt+"");
		List<Map<String, Object>> res = g4AzimuthCalcMapper.query4GAzimuthCalcTaskByPage(cond);
		
		if (res != null && res.size() > 0) {
			String cityName = "";
			/*for (String k : cond.keySet()) {
				if ("cityId".equalsIgnoreCase(k)) {
					Map<String, Object> area = AuthDsDataDaoImpl.getAreaData(Long.parseLong(cond.get(k).toString()));
					if (area != null) {
						cityName = (String) area.get("NAME");
					}
				}
			}*/
			cityName = g4AzimuthCalcMapper.getCityNameByCityId(Long.parseLong(cond.get("cityId")));
			for (Map<String, Object> one : res) {
				one.put("CITY_NAME", cityName);
			}
		}
		return res;
	}
	/*
	 * 获取某个地市 的小区方位角信息
	 */
	public Map<String, String> queryCellAzimuthByCityId(long cityId){
		logger.debug("进入queryCellAzimuthByCityId，cityId={}",cityId);
		String areaIdStr = sysAreaSyncService.getSubAreaAndSelfIdListStrByParentId(cityId);
		List<Map<String, Object>>  resultList= g4AzimuthCalcMapper.queryCellAzimuthByAreaStr(areaIdStr);
		Map<String, String> map = new HashMap<String, String>();
		for (Map<String, Object> result : resultList) {
			map.put(result.get("CELL_ID").toString(), result.get("AZIMUTH").toString());
		}
		return map;
	}
	/*
	 * 获取某个地市 的小区频段类型信息
	 */
	public Map<String, String> queryCellBandTypeByCityId(long cityId){
		logger.debug("进入queryCellBandTypeByCityId，cityId={}",cityId);
		String areaIdStr = sysAreaSyncService.getSubAreaAndSelfIdListStrByParentId(cityId);
		List<Map<String, Object>>  resultList= g4AzimuthCalcMapper.queryCellBandTypeByAreaStr(areaIdStr);
		Map<String, String> map = new HashMap<String, String>();
		for (Map<String, Object> result : resultList) {
			map.put(result.get("CELL_ID").toString(), result.get("BAND_TYPE").toString());
		}
		return map;
	}

	/**
	 * 从spark数据仓库获取网络覆盖所需的MR数据
	 * @param cityId
	 * @param startDate
	 * @param endDate
	 * @param connection
	 * @return
	 */
	public List<Map<String,Object>> queryNetWorkCoverDataFromSparkMrTable(
			long cityId,String startDate, String endDate, Connection connection,long jobId) {

		Statement stmt = null;
		List<Map<String, String>> mrData = null;
		String sql = "";
		try {
			stmt = connection.createStatement();
		}catch (Exception e){
			logger.debug("准备SPARK数据仓库 stmt 失败！");
			e.printStackTrace();
		}

		sql = "SELECT                                                                                                                                                            "
				+"	cell_id,                                                                                                                                                        "
				+  jobId+ " job_id,  "
				+"	cell_name,                                                                                                                                                      "
				+"	cur_azimuth,                                                                                                                                                    "
				+"	cal_azimuth,                                                                                                                                                    "
				+"	IF(                                                                                                                                                             "
				+"		ABS( cal_azimuth - cur_azimuth )> 180,                                                                                                                      "
				+"		360 - ABS( cal_azimuth - cur_azimuth ),                                                                                                                     "
				+"		ABS( cal_azimuth - cur_azimuth )                                                                                                                            "
				+"	) diff_azimuth                                                                                                                                                  "
				+"from                                                                                                                                                              "
				+"	(                                                                                                                                                               "
				+"		SELECT                                                                                                                                                      "
				+"			cell_id,                                                                                                                                                "
				+"			cell_name,                                                                                                                                              "
				+"			azimuth cur_azimuth,                                                                                                                                    "
				+"			(                                                                                                                                                       "
				+"				CASE                                                                                                                                                "
				+"					WHEN lngDiff > cell_lon                                                                                                                         "
				+"					AND latDiff >= cell_lat THEN round(                                                                                                             "
				+"						degrees(                                                                                                                                    "
				+"							asin(                                                                                                                                   "
				+"								(                                                                                                                                   "
				+"									lngDiff - cell_lon                                                                                                              "
				+"								)/(                                                                                                                                 "
				+"									sqrt(( lngDiff - cell_lon )*( lngDiff - cell_lon )+( latDiff - cell_lat )*( latDiff - cell_lat ))                               "
				+"								)                                                                                                                                   "
				+"							)                                                                                                                                       "
				+"						)                                                                                                                                           "
				+"					)                                                                                                                                               "
				+"					WHEN lngDiff <= cell_lon                                                                                                                        "
				+"					AND latDiff > cell_lat THEN 360 + round(                                                                                                        "
				+"						degrees(                                                                                                                                    "
				+"							asin(                                                                                                                                   "
				+"								(                                                                                                                                   "
				+"									lngDiff - cell_lon                                                                                                              "
				+"								)/(                                                                                                                                 "
				+"									sqrt(( lngDiff - cell_lon )*( lngDiff - cell_lon )+( latDiff - cell_lat )*( latDiff - cell_lat ))                               "
				+"								)                                                                                                                                   "
				+"							)                                                                                                                                       "
				+"						)                                                                                                                                           "
				+"					)                                                                                                                                               "
				+"					WHEN lngDiff < cell_lon                                                                                                                         "
				+"					AND latDiff <= cell_lat THEN 180 - round(                                                                                                       "
				+"						degrees(                                                                                                                                    "
				+"							asin(                                                                                                                                   "
				+"								(                                                                                                                                   "
				+"									lngDiff - cell_lon                                                                                                              "
				+"								)/(                                                                                                                                 "
				+"									sqrt(( lngDiff - cell_lon )*( lngDiff - cell_lon )+( latDiff - cell_lat )*( latDiff - cell_lat ))                               "
				+"								)                                                                                                                                   "
				+"							)                                                                                                                                       "
				+"						)                                                                                                                                           "
				+"					)                                                                                                                                               "
				+"					WHEN lngDiff >= cell_lon                                                                                                                        "
				+"					AND latDiff < cell_lat THEN 180 - round(                                                                                                        "
				+"						degrees(                                                                                                                                    "
				+"							asin(                                                                                                                                   "
				+"								(                                                                                                                                   "
				+"									lngDiff - cell_lon                                                                                                              "
				+"								)/(                                                                                                                                 "
				+"									sqrt(( lngDiff - cell_lon )*( lngDiff - cell_lon )+( latDiff - cell_lat )*( latDiff - cell_lat ))                               "
				+"								)                                                                                                                                   "
				+"							)                                                                                                                                       "
				+"						)                                                                                                                                           "
				+"					)                                                                                                                                               "
				+"					ELSE 0                                                                                                                                          "
				+"				END                                                                                                                                                 "
				+"			) cal_azimuth                                                                                                                                           "
				+"		from                                                                                                                                                        "
				+"			(                                                                                                                                                       "
				+"				select                                                                                                                                              "
				+"					cell_id,                                                                                                                                        "
				+"					cell_name,                                                                                                                                      "
				+"					cell_lon,                                                                                                                                       "
				+"					cell_lat,                                                                                                                                       "
				+"					azimuth,                                                                                                                                        "
				+"					sum( relev1 *(( ncell_lon - cell_lon )/ sqrt(( cell_lon - ncell_lon )*( cell_lon - ncell_lon )+( cell_lat - ncell_lat )*( cell_lat - ncell_lat  ))))+ cell_lon lngDiff,"
				+"					sum( relev1 *(( ncell_lat - cell_lat )/ sqrt(( cell_lon - ncell_lon )*( cell_lon - ncell_lon )+( cell_lat - ncell_lat )*( cell_lat - ncell_lat  ))))+ cell_lat latDiff "
				+"				from                                                                                                                                                "
				+"					(                                                                                                                                               "
				+"						select                                                                                                                                      "
				+"							t.cell_id ncell_id,                                                                                                                     "
				+"							c.enodeb_id nc_enodeb_Id,                                                                                                               "
				+"							c.longitude ncell_lon,                                                                                                                  "
				+"							c.latitude ncell_lat,                                                                                                                   "
				+"							t.ncell_id cell_id,                                                                                                                     "
				+"							d.Azimuth azimuth,                                                                                                                      "
				+"							d.cell_name,                                                                                                                            "
				+"							d.longitude as cell_lon,                                                                                                                "
				+"							d.latitude as cell_lat,                                                                                                                 "
				+"							t.rsrptimes0,                                                                                                                           "
				+"							t.rsrptimes2,                                                                                                                           "
				+"							t.mixingsum,                                                                                                                            "
				+"							t.rsrptimes0 / t.mixingsum as relev1,                                                                                                   "
				+"							t.record_time meaTime                                                                                                                   "
				+"						from                                                                                                                                        "
				+"							lte_mr_data t,                                                                                                                          "
				+"							lte_cell_param c,                                                                                                                       "
				+"							lte_cell_param d                                                                                                                        "
				+"						where                                                                                                                                       "
				+"							t.cell_id = c.cell_id                                                                                                                   "
				+"							and t.ncell_id = d.cell_id                                                                                                              "
				+"							and d.cover_type = '室外'                                                                                                               "
				+"							and t.mixingsum >= 50                                                                                                                   "
				+"							and t.distance_km >= 0.1                                                                                                                "
				+"							and t.distance_km <= 5                                                                                                                  "
				+"							and t.rsrptimes0 > 0                                                                                                                    "
				+"							and t.area_id = "+cityId+"                                                                                                                      "
				+"							and from_unixtime(                                                                                                                      "
				+"								unix_timestamp(                                                                                                                     "
				+"									t.record_time,                                                                                                                  "
				+"									'yyyyMMdd'                                                                                                                      "
				+"								)                                                                                                                                   "
				+"							)>= '"+startDate.trim()+" 00-00-00'                                                                                                               "
				+"							and from_unixtime(                                                                                                                      "
				+"								unix_timestamp(                                                                                                                     "
				+"									t.record_time,                                                                                                                  "
				+"									'yyyyMMdd'                                                                                                                      "
				+"								)                                                                                                                                   "
				+"							)<= '"+endDate.trim()+" 00-00-00'                                                                                                               "
				+"					)                                                                                                                                               "
				+"				GROUP BY                                                                                                                                            "
				+"					cell_id,                                                                                                                                        "
				+"					cell_lon,                                                                                                                                       "
				+"					cell_lat,                                                                                                                                       "
				+"					cell_name,                                                                                                                                      "
				+"					azimuth                                                                                                                                         "
				+"			)                                                                                                                                                       "
				+"	)                                                                                                                                                              ";

		//MR数据
		List<Map<String,Object>> mrDatas = new ArrayList<Map<String,Object>>();
		try{
			mrDatas = RnoHelper.commonQuery(stmt,sql);
			logger.debug("从spark数据仓库获取数据 为："+mrDatas.size());
//			System.out.println("从spark数据仓库获取数据 为："+mrDatas.size());
		}catch (Exception e){
			logger.error("SPARK数据仓库获取数据异常！");
			e.printStackTrace();
		}

		return mrDatas;
	}
	/**
	 * 将mr网络覆盖结果批量插入oracle数据库
	 * @param mrDatas
	 */
	public boolean addNetworkCoverResBatch(List<Map<String,Object>>  mrDatas){
		boolean flag = false;
		try {
			g4AzimuthCalcMapper.addNetworkCoverResBatch(mrDatas);
			flag = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}
