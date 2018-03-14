package com.iscreate.rnointerfermatrix.mapper.spark;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SparkMapper {

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
	 * 计算干扰矩阵(mr,ho,sf)
	 */
	public void createMatrix(Map<String, Object> map);
	
	/**
	 * 计算干扰矩阵(mr,ho)
	 */
	public void createMatrixWithoutSf(Map<String, Object> map);
	
	/**
	 * 查询MR数据量信息
	 */
	public List<Map<String, Object>> queryMrCount(Map<String, Object> map);
	
	/**
	 * 查询HO数据量信息
	 */
	public List<Map<String, Object>> queryHoCount(Map<String, Object> map);
	
	/**
	 * 查询SF数据量信息
	 */
	public List<Map<String, Object>> querySfCount(Map<String, Object> map);
	
	/**
	 * 获取MR数据量
	 */
	public int getMrcount(Map<String, Object> map);
	
	/**
	 * 获取HO数据量
	 */
	public int getHocount(Map<String, Object> map);
	
	/**
	 * 获取SF数据量
	 */
	public int getSfcount(Map<String, Object> map);
	
	/**
	 * 下载干扰矩阵结果文件
	 */
	public List<Map<String, Object>> downloadMatrix(int jobId);
	
}
