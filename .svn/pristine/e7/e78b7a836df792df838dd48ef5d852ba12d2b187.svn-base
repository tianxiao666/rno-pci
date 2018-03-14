package com.iscreate.rno.microservice.pci.afp.mapper.spark;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.iscreate.rno.microservice.pci.afp.model.G4SfDescQueryCond;

@Mapper
public interface SparkMapper {

	/**
	 * 查询扫频文件
	 */
	public List<Map<String, String>> querySFfile(G4SfDescQueryCond cond);
	
	/**
	 * 查询MR数据的测量时间
	 */
	public List<String> queryMrMeaDate(Map<String, Object> map);
	
	/**
	 * 准备MR临时表数据
	 */
	public void createMrTempTable(Map<String, Object> map);
	
	/**
	 * 查询HO数据的测量时间
	 */
	public List<String> queryHoMeaDate(Map<String, Object> map);
	
	/**
	 * 准备HO临时表数据
	 */
	public void createHoTempTable(Map<String, Object> map);
	
	/**
	 * 查询SF数据的测量时间
	 */
	public List<String> querySfMeaDate(Map<String, Object> map);
	
	/**
	 * 准备SF临时表数据
	 */
	public void createSfTempTable(Map<String, Object> map);
	
	/**
	 * 查询流量数据的测量时间
	 */
	public List<String> queryFlowMeaDate(Map<String, Object> map);
	
	/**
	 * 准备流量临时表数据
	 */
	public void createFlowTempTable(Map<String, Object> map);
	
	/**
	 * 计算干扰矩阵(mr,ho,sf)
	 */
	public void createMatrix(Map<String, Object> map);
	
	/**
	 * 计算干扰矩阵(mr,ho)
	 */
	public void createMatrixWithoutSf(Map<String, Object> map);
	
	/**
	 * 读取d1d2小区数据
	 */
	public List<Map<String, Object>> getSFd1d2Cell(int jobId);
	
	/**
	 * 读取干扰矩阵数据
	 */
	public List<Map<String, Object>> getMatrix(int jobId);
	
	/**
	 * 获取流量数据
	 */
	public List<Map<String, Object>> getFlow(Map<String, Object> map);
	
	/**
	 * 通过城市ID获取从基站标识到lte小区的映射集合
	 */
	public List<Map<String, Object>> getLteCellInfoByCellId(long cityId);
	
	/**
	 * 通过城市ID获取小区和经纬度的映射集合
	 */
	public List<Map<String, Object>> getParameterForCellsMap(long cityId);
	
}
