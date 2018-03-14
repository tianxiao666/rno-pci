package com.hgicreate.rno.lte.pciafp.service;

import com.hgicreate.rno.lte.pciafp.model.Cell;
import com.hgicreate.rno.lte.pciafp.model.InterMatrix;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chen.c10
 */
public interface PciAfpDataService {

    /**
     * 通过区域ID获取LTE小区标识对应的小区信息
     *
     * @param cityId 城市id
     * @return 小区id到小区对象的map
     */
    Map<String, Cell> getLteCellMapByCityId(long cityId);

    /**
     * 读取干扰矩阵数据
     *
     * @param cityId 获取干扰矩阵的城市id
     * @return 干扰矩阵列表
     */
    List<InterMatrix> getLatestMatrixByCityId(long cityId);

    /**
     * 读取干扰矩阵数据
     *
     * @param cityId 获取干扰矩阵的城市id
     * @param cells 需要改变pci的小区表
     * @return 干扰矩阵列表
     */
    List<InterMatrix> getLatestMatrixByCityIdAndCells(long cityId, List<String> cells);
}
