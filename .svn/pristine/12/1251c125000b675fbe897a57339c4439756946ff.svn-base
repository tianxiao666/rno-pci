package com.hgicreate.rno.lte.pciafp.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen.c10
 */
public class PciAfpCalcMaterial {
    /**
     * 小区与同站其他小区列的映射，同站其他小区已按关联度从大到小排列 <br>
     * 比如key为1 <br>
     * 干扰矩阵为 <br>
     * 1->2 <br>
     * 1->3 <br>
     * 同站
     **/
    private Map<String, List<String>> cellToSameStationCells = new HashMap<>();

    /**
     * 小区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
     * 比如key为1 、 <br>
     * 干扰矩阵为 <br>
     * 1->12 <br>
     * 1->23 <br>
     * 非同站
     **/
    private Map<String, List<String>> cellToNotSameStationCells = new HashMap<>();

    /**
     * 邻区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
     * 比如key为1 <br>
     * 干扰矩阵为 <br>
     * 12->1 <br>
     * 23->1 <br>
     * 非同站
     **/
    private Map<String, List<String>> ncellToNotSameStationCells = new HashMap<>();

    /**
     * 小区与邻区关联度的映射（包含了同站其他小区） 以主小区为key
     **/
    private Map<String, Map<String, Double>> cellToNcellRelevancy = new HashMap<>();

    /**
     * 小区与邻区关联度的映射 以邻小区为key
     **/
    private Map<String, Map<String, Double>> ncellToCellRelevancy = new HashMap<>();

    /**
     * 小区与小区总关联度的映射
     **/
    private Map<String, Double> cellToTotalRelevancy = new HashMap<>();

    /**
     * 小区列表
     **/
    private List<String> cellList;

    /**
     * 小区与原PCI的映射
     **/
    private Map<String, Integer> cellToOriPci;

    /**
     * 小区到经纬度的映射，不重复
     **/
    private Map<String, double[]> cellToLonLat;

    /**
     * 小区与频率的映射
     **/
    private Map<String, Integer> cellToEarfcn;

    /**
     * 基站到小区列表的映射
     **/
    private Map<String, List<String>> enodebToCells;

    /**
     * 小区到基站的映射
     **/
    private Map<String, String> cell2Enodeb;

    /**
     * 小区与KS值的映射
     */
    private Map<String, Double> cellToKs = new HashMap<>();
}
