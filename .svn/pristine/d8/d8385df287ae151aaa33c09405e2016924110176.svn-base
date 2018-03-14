package com.hgicreate.rno.lte.pciafp.mapper;

import com.hgicreate.rno.lte.pciafp.model.Cell;
import com.hgicreate.rno.lte.pciafp.model.InterMatrix;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chen.c10
 */
@Mapper
public interface DataMapper {
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
    List<InterMatrix> getLatestMatrixByCityIdAndCells(@Param("cityId") long cityId, @Param("cells") List<String> cells);

    /**
     * 通过城市ID获取lte小区
     *
     * @param cityId 获取小区信息的城市id
     * @return 小区列表
     */
    List<Cell> getLteCellsByCityId(long cityId);
}
