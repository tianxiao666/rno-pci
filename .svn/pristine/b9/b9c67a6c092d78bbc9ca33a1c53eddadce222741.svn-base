package com.hgicreate.rno.lte.pciafp.task;

import com.hgicreate.rno.lte.pciafp.model.PlanItem;
import com.hgicreate.rno.lte.pciafp.service.PciAfpRestService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
strictfp class PciAfpCalcActuator {
    /**
     * 地球的平均半径，单位为米
     */
    private static final double R = 6371000;

    private static final int MOD_3 = 3;
    private static final int MOD_6 = 6;
    private static final int MOD_30 = 30;
    private static final int SAME_STATION_NCELL_SIZE = 5;
    private static final int PCI_AMOUNT = 504;
    private static final int PCI_GROUP_AMOUNT = 168;

    private final PciAfpRestService pciAfpRestService;

    /**
     * 关联度和PCI的映射，用于分组分配PCI的方式，ConcurrentHashMap在多线程下拥有良好的性能
     **/
    private final Map<Double, List<Integer>> interValToPciList = new ConcurrentHashMap<>();

    /**
     * 配置
     */
    private PciAfpCalcConfig config;

    /**
     * PCI分组工具
     */
    private PciUtil pciUtil = new PciUtil();
    /**
     * 当前正在计算的方案
     **/
    private PlanObj bestPlan = new PlanObj();
    /**
     * 小区与同站其他小区列的映射，同站其他小区已按关联度从大到小排列 <br>
     * 比如key为1 <br>
     * 干扰矩阵为 <br>
     * 1->2 <br>
     * 1->3 <br>
     * 同站
     **/
    private Map<String, List<String>> cellToSameStationCells;
    /**
     * 小区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
     * 比如key为1 、 <br>
     * 干扰矩阵为 <br>
     * 1->12 <br>
     * 1->23 <br>
     * 非同站
     **/
    private Map<String, List<String>> cellToNotSameStationCells;
    /**
     * 邻区与非同站小区列表的映射，非同站小区已按关联度从大到小排列 <br>
     * 比如key为1 <br>
     * 干扰矩阵为 <br>
     * 12->1 <br>
     * 23->1 <br>
     * 非同站
     **/
    private Map<String, List<String>> ncellToNotSameStationCells;
    /**
     * 小区与邻区关联度的映射（包含了同站其他小区）
     **/
    private Map<String, Map<String, Double>> cellToNcellRelevancy;
    /**
     * 小区与邻区关联度的映射(以邻区为树根)
     **/
    private Map<String, Map<String, Double>> ncellToCellRelevancy;
    /**
     * 小区与小区总关联度的映射,以按总关联度从大到小排序
     **/
    private Map<String, Double> cellToTotalRelevancy;
    /**
     * 变小区关联度从大到小排序的队列
     **/
    private List<String> descNeed2AssignCells;
    /**
     * TOP变PCI小区表大小
     **/
    private int topSize;
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
    private Map<String, Integer> cellToOriEarfcn;
    /**
     * 小区与原始总干扰值的映射
     **/
    private Map<String, Double> cellToOriInterVal = new HashMap<>();
    /**
     * 对 PCI 的分配次数进行记录，用于干扰值相同时 PCI 的判断依据。
     **/
    private Map<Integer, Integer> pciToAllocAmount = new HashMap<>();
    /**
     * 小区到KS的映射
     **/
    private Map<String, Double> cellToKs;

    /**
     * 收敛方案最终的差值比例或增均方差，用于返回信息
     */
    private double lastInterRate = 0.0;
    private double lastVariance = 0.0;
    /**
     * 方案生成次数
     **/
    private int planCreateTimes = 0;
    /**
     * 方案替换次数
     **/
    private int planChangeTimes = 0;
    /**
     * topList生成次数
     **/
    private int generateTopListTimes = 0;
    /**
     * topList小区变化次数
     **/
    private int topListCellChangeTimes = 0;
    /**
     * 是否同pci出现的距离太近
     **/
    private boolean isSamePciTooClose = false;
    /**
     * 尾数差异收敛算法
     */
    private boolean mantissaDifferencesConvergence = false;
    /**
     * Mean squared error(MSE)均方差 均方差收敛算法
     */
    private boolean mseConvergence = false;
    /**
     * 原始总干扰值
     */
    private double originalTotalInterVal;
    /**
     * 最小总干扰值
     */
    private double minTotalInterVal;

    PciAfpCalcActuator(PciAfpCalcConfig config, PciAfpRestService pciAfpRestService) {
        log.debug("正在初始化计算类。。。");
        this.config = config;
        this.pciAfpRestService = pciAfpRestService;

        this.cellToNcellRelevancy = config.getCellToNcellRelevancy();
        this.ncellToCellRelevancy = config.getNcellToCellRelevancy();
        this.cellToTotalRelevancy = config.getCellToTotalRelevancy();
        log.debug("主小区数量=" + cellToTotalRelevancy.size());

        this.descNeed2AssignCells = config.getDescNeed2AssignCells();
        this.topSize = config.getTopSize();

        this.cellToSameStationCells = config.getCellToSameStationCells();
        this.cellToNotSameStationCells = config.getCellToNotSameStationCells();
        this.ncellToNotSameStationCells = config.getNcellToNotSameStationCells();
        this.cellToOriPci = config.getCellToOriPci();
        this.cellToOriEarfcn = config.getCellToEarfcn();
        this.cellToLonLat = config.getCellToLonLat();
        this.cellToKs = config.getCellToKs();

        // 初始化 PCI 分配次数，初始值都为0
        for (int i = 0; i < PCI_AMOUNT; i++) {
            pciToAllocAmount.put(i, 0);
        }

        // pci+-30循环列表
        Map<Integer, List<Integer>> pciToAlter30List = new HashMap<>();
        for (int i = 0; i < MOD_30; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = i; j < PCI_AMOUNT; j += MOD_30) {
                list.add(j);
            }
            pciToAlter30List.put(i, list);
        }
        for (int i = 30; i < PCI_AMOUNT; i++) {
            pciToAlter30List.put(i, pciToAlter30List.get(i % 30));
        }
        log.debug("初始化计算类完成。");
    }

    /**
     * Top 小区列表排名是否有变化
     */
    private static boolean isRankingChanged(List<String> topCellList, List<String> newTopCellList, int topIndex,
                                            double minTotalInterVal, double lastTotalInterVal, double currentTotalInterVal,
                                            List<String> minInterValCellList) {
        long t = System.currentTimeMillis();
        boolean flag = false;
        if (!topCellList.get(topIndex).equals(newTopCellList.get(topIndex))) {
            if (currentTotalInterVal == minTotalInterVal) {
                flag = !compareList(newTopCellList, minInterValCellList);
            }
            // 本次结果比上次更差，直接开始新的循环
            //TODO 合理性有待进一步考证，从控制逻辑上感觉有点怪怪的
            else if (currentTotalInterVal > lastTotalInterVal) {
                flag = true;
            }
        }
        log.debug("计算排名用时：{}", System.currentTimeMillis() - t);
        return flag;
    }

    /**
     * 毕竟两个list的顺序是否相同
     */
    private static boolean compareList(List<String> list1, List<String> list2) {
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算gps里程(米)
     */
    private static double distance(double[] start, double[] end) {
        return distance(start[0], start[1], end[0], end[1]);
    }

    /**
     * 计算gps里程(米)
     */
    private static double distance(double startLon, double startLat, double endLon, double endLat) {
        double a, b;
        startLat = startLat * Math.PI / 180.0;
        endLat = endLat * Math.PI / 180.0;
        a = startLat - endLat;
        b = (startLon - endLon) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(startLat) * Math.cos(endLat) * sb2 * sb2));
        return d;
    }

    /**
     * PCI计算
     */
    void execCalc() {
        log.debug("计算开始。。。");
        // 在原始方案中先为所有主小区分配原始PCI或earfcn
        assignOriPciAndEarfcnByDescCells(bestPlan);
        // 计算原始方案的每小区干扰值
        bestPlan.calInterVal(cellToNotSameStationCells);
        // 缓存原始方案
        cellToOriInterVal.putAll(bestPlan.getCellToInterVal());
        // 计算原始方案总干拢值
        originalTotalInterVal = bestPlan.getTotalInterVal();
        log.debug("原始方案总干拢值：originalTotalInterVal=" + originalTotalInterVal);
        minTotalInterVal = originalTotalInterVal;

        // 创建新的方案
        PlanObj plan = bestPlan.clone();
        // 首次变小区PCI分配，按变小区关联度大小顺序分配，生成首套方案。
        assignPciToCellsPlan(plan, descNeed2AssignCells);
        // 计算原始方案的每小区干扰值
        plan.calInterVal(cellToNotSameStationCells);
        // 记录上一次的总关联度
        double lastTotalInterVal = minTotalInterVal;
        // 计算首套方案总干拢值
        double currentTotalInterVal = plan.getTotalInterVal();
        log.debug("首套方案总干拢值：currentTotalInterVal=" + currentTotalInterVal);
        if (log.isDebugEnabled()) {
            log.debug("循环分配中，最小干扰总值：" + minTotalInterVal);
            log.debug("循环分配中，上次干扰总值：" + lastTotalInterVal);
            log.debug("循环分配中，当前干扰总值：" + currentTotalInterVal);
        }
        // 首套方案优于原始方案，用首套方案替换原始方案
        if (currentTotalInterVal < minTotalInterVal) {
            // 最小总干扰值设置为当前的总干扰值
            minTotalInterVal = currentTotalInterVal;
            // 当前的最好方案
            bestPlan = plan.clone();
        }
        // 统计首套方案
        planCreateTimes++;
        boolean isCalFinish = false;

        List<String> minInterValCellList = new ArrayList<>();

        // 准备分配的小区
        String assignCell;
        // top小区列表
        List<String> topCellList;
        // 新topList
        List<String> newTopCellList;
        // top小区序号
        int topIndex = 0;
        long t1 = System.currentTimeMillis();
        // 收敛判断
        while (!isConvergence()) {
            log.debug("一次方案用时：{}", System.currentTimeMillis() - t1);
            t1 = System.currentTimeMillis();
            // 方案累加
            log.debug("方案评估循环：{}", planCreateTimes++);
            // 创建新的方案
            plan = bestPlan.clone();
            // 获得 Top 小区表
            topCellList = plan.createTopCellList();
            log.debug("topCellList={}", topCellList);
            // 如果topList为空，退出
            if (topCellList.isEmpty()) {
                break;
            }
            // 累计topList小区变化次数
            topListCellChangeTimes += topIndex + 1;
            // 每次重新获取Top小区，序号归零
            topIndex = 0;
            // 变PCI小区表生成次数
            int topListTimes = 1;
            // 保存topList 顺序
            minInterValCellList.clear();
            minInterValCellList.addAll(topCellList);

            // 方案评估，总干拢值越小越好
            while (true) {
                log.debug("当前第<{}>套方案，生成第<{}>个 TOP List，使用第<{}>个TOP Cell。", planCreateTimes, topListTimes, topIndex + 1);
                // 记录需要调整的小区
                assignCell = topCellList.get(topIndex);
                // 重新分配 pci 给 topCellList 的小区
                if (assignPciToCellsPlanForOneCell(plan, assignCell).isUpdated()) {
                    log.debug("小区：{},重新分配pci：{}。", assignCell, plan.getPciByCell(assignCell));
                    // 成功分配新pci，计算干扰质
                    // 计算当前方案的每个小区对应的干扰值，只计算上次调整的小区和其相关小区
                    plan.calInterVal(assignCell);
                    // 记录上一次的总关联度
                    lastTotalInterVal = currentTotalInterVal;
                    // 当前方案的总干拢值
                    currentTotalInterVal = plan.getTotalInterVal();

                    if (log.isDebugEnabled()) {
                        log.debug("循环分配中，最小干扰总值：{}", minTotalInterVal);
                        log.debug("循环分配中，上次干扰总值：{}", lastTotalInterVal);
                        log.debug("循环分配中，当前干扰总值：{}", currentTotalInterVal);
                    }

                    if (currentTotalInterVal < minTotalInterVal) {
                        log.debug("当前方案总干扰值小于最佳方案总干扰值，替换最佳方案。");
                        // 新方案全网干拢值优于旧方案，替换旧方案
                        // 最小总干扰值设置为当前的总干扰值
                        minTotalInterVal = currentTotalInterVal;
                        // 当前的最好方案
                        bestPlan = plan.clone();
                        // 方案替换次数累计
                        planChangeTimes++;

                        break;
                    } else {
                        log.debug("当前方案总干扰值不小于最佳方案总干扰值。");
                        // 产生新的 Top 小区列表（按干扰总值从大到小排序）
                        newTopCellList = plan.createTopCellList();
                        log.debug("newTopCellList={}", newTopCellList);
                        // 排名是否有变化
                        if (isRankingChanged(topCellList, newTopCellList, topIndex, minTotalInterVal,
                                lastTotalInterVal, currentTotalInterVal, minInterValCellList)) {
                            // 总干扰值更差了，但最差的小区的提名提升了，用新列表，继续对当前位置小区进行优化。
                            log.debug("排名有变化，topIndex={}, topCellList={}, newTopCellList={}", topIndex, topCellList.get(topIndex), newTopCellList.get(topIndex));
                            // 用新topList替换
                            topCellList = newTopCellList;
                            // 累加当前方案的TOP list生成次数。
                            topListTimes++;
                            // 累计topList小区变化次数
                            topListCellChangeTimes += topIndex + 1;
                            // 每次重新获取Top小区，序号归零
                            topIndex = 0;
                        } else {
                            // 总干扰值更差了，并且最差的小区提名没有提升，用回原来列表，并对下一个位置小区进行优化。
                            // 当 TOP 列表最后一个都优化后，优化计算结束。
                            if (++topIndex >= topCellList.size()) {
                                log.debug("排名无变化，TopList 已分配完，计算结束。");
                                isCalFinish = true;
                                break;
                            }
                        }
                    }
                } else {
                    log.debug("小区：{},未重新分配pci。", assignCell);
                    // 未重新分配pci，说明当前pci的干扰值已经最小，继续分配下一个
                    if (++topIndex >= topCellList.size()) {
                        log.debug("排名无变化，TopList 已分配完，计算结束。");
                        isCalFinish = true;
                        break;
                    }
                }
            }

            // 累计TOP list 生成次数
            generateTopListTimes += topListTimes;
            if (isCalFinish) {
                break;
            }
        }
        // 第一次多加了1，现在减去
        if (topListCellChangeTimes > 0) {
            topListCellChangeTimes--;
        }

        // 输出当前方案之前，以最佳方案进行邻区核查。
        if (config.isCheckNCell()) {
            checkNCellPci(bestPlan, descNeed2AssignCells);
        }

        // 输出当前方案之前，以最佳方案进行同频同PCI核查。
        checkNeighborAreaPci(bestPlan, descNeed2AssignCells);
        // 保存最终方案
        saveBestPlan(bestPlan);
        // 保存返回信息
        config.setReturnInfo(getReturnInfo());
        log.debug("计算完成！");
    }

    /**
     * 收敛判断
     */
    private boolean isConvergence() {
        // 收敛方法1: 尾数差异收敛算法 -- top n%小区干扰值与中间n%小区干扰值的差值比例 小于等于m%
        double interRate = getInterRate();
        double defInterRate = config.getDefInterRate();
        lastInterRate = interRate;
        if (log.isDebugEnabled()) {
            log.debug("TOP {}% 小区与中间 {}% 的干扰差值比例={}%, 给定值={}%", config.getTopRate() * 100,
                    config.getTopRate() * 100, interRate * 100, defInterRate * 100);
        }
        if (interRate <= defInterRate) {
            if (log.isDebugEnabled()) {
                log.debug("【top n%小区干扰总值与中间n%小区干扰总值的差值比例 小于等于m%】，终止继续优化");
            }
            mantissaDifferencesConvergence = true;
        }

        // 收敛方法2: 均方差收敛算法 -- 全网以小区数均分为n份，求均方差，当值小于m
        double varianceByCurrentPlan = getVarianceByCurrentPlan();
        double defVariance = config.getDefVariance();
        lastVariance = varianceByCurrentPlan;
        if (log.isDebugEnabled()) {
            log.debug("全网小区干扰值方差=" + varianceByCurrentPlan + ", 给定值=" + defVariance);
        }
        if (varianceByCurrentPlan < defVariance) {
            if (log.isDebugEnabled()) {
                log.debug("【求全网小区干扰值方差，当值小于给定值m】，终止继续优化");
            }
            mseConvergence = true;
        }
        return mantissaDifferencesConvergence || mseConvergence;
    }

    /**
     * 返回任务信息
     */
    private String getReturnInfo() {
        String resultInfo = "原始方案总干拢值：" + originalTotalInterVal;
        resultInfo += "<br>";
        resultInfo += "最优方案总干拢值：" + minTotalInterVal;
        resultInfo += "<br>";
        resultInfo += "优化结束方式：";
        if (mantissaDifferencesConvergence && mseConvergence) {
            resultInfo += "收敛方式1(根据Top差值比例) 和 收敛方式2(根据求方差) 均收敛";
        } else if (mantissaDifferencesConvergence) {
            resultInfo += "收敛方式1(根据Top差值比例)";
        } else if (mseConvergence) {
            resultInfo += "收敛方式2(根据求方差)";
        } else {
            resultInfo += "未达到收敛，循环已完成";
        }
        resultInfo += "<br>";
        resultInfo += "收敛信息：";
        resultInfo += "<br>";
        resultInfo += "最终的差值比例：" + lastInterRate + "，差值比例目标值：" + config.getDefInterRate();
        resultInfo += "<br>";
        resultInfo += "最终的均方差：" + lastVariance + "，均方差目标值" + config.getDefVariance();

        resultInfo += "<br>";
        resultInfo += "是否进行邻区核查：";
        if (config.isCheckNCell()) {
            resultInfo += "是；";
            resultInfo += "<br>";
            resultInfo += config.getDisLimit() + "米内，有无同PCI情况：";
            if (isSamePciTooClose) {
                resultInfo += "有。";
            } else {
                resultInfo += "无。";
            }
        } else {
            resultInfo += "否；";
        }

        resultInfo += "<br>";
        resultInfo += "方案生成次数：" + planCreateTimes;
        resultInfo += "<br>";
        resultInfo += "方案替换次数：" + planChangeTimes;
        resultInfo += "<br>";
        resultInfo += "生成TOPlist次数：" + generateTopListTimes;
        resultInfo += "<br>";
        resultInfo += "分配topCell次数：" + topListCellChangeTimes;
        if (config.getDtKs() != -1.0) {
            resultInfo += "<br>";
            resultInfo += "Ks修正值：" + config.getDtKs();
        }
        return resultInfo;
    }

    /**
     * 收敛方式1(根据Top差值比例)
     */
    private double getInterRate() {
        List<Double> descInterValList = new ArrayList<>(sortMapByValue(bestPlan.getCellToInterVal()).values());
        // top区间
        int start1 = 0;
        int start2 = (int) (descInterValList.size() * config.getTopRate());
        // 中间区间，如果 top 区间是奇数，则 mid 区间加大1
        int mid1 = descInterValList.size() / 2 - start2 / 2 - (start2 % 2);
        int mid2 = descInterValList.size() / 2 + start2 / 2;

        double topInterVal = 0;
        for (int i = start1; i < start2; i++) {
            topInterVal += descInterValList.get(i);
        }
        double midInterVal = 0;
        for (int i = mid1; i < mid2; i++) {
            midInterVal += descInterValList.get(i);
        }
        if (log.isDebugEnabled()) {
            log.debug("topInterVal={}, midInterVal={},start1={},start2={},mid1={},mid2={}", topInterVal, midInterVal, start1, start2, mid1, mid2);
        }
        return (topInterVal == 0) ? 0 : (topInterVal - midInterVal) / topInterVal;
    }

    /**
     * 收敛方式2(根据求方差)
     */
    private double getVarianceByCurrentPlan() {
        // 获取当前方案的干扰值小区排序，从大到小
        List<Double> descInterValList = new ArrayList<>(sortMapByValue(bestPlan.getCellToInterVal()).values());
        int size = bestPlan.getCellToInterVal().size();
        int div = size / config.getDivideNumber();
        int mod = size % config.getDivideNumber();

        int tmpDivideNumber = config.getDivideNumber();
        if (mod > 0) {
            div = div + 1;
            tmpDivideNumber = tmpDivideNumber - 1;
        }

        double totVal = 0;
        List<Double> listTotal = new ArrayList<>();
        // 以小区数均分为 n 份
        int k = 0;
        for (int i = 0; i < tmpDivideNumber; i++) {
            double total = 0;
            for (int j = 0; j < div; j++) {
                total += descInterValList.get(k);
                k++;
            }
            totVal += total;
            listTotal.add(total);
        }

        if (mod > 0) {
            double total = 0;
            for (int i = descInterValList.size() - div; i < descInterValList.size(); i++) {
                total += descInterValList.get(i);
            }
            totVal += total;
            listTotal.add(total);
        }

        // 平均值
        double averageVal = totVal / config.getDivideNumber();

        double val = 0;
        for (double total : listTotal) {
            double tmpVal = total - averageVal;
            val += tmpVal * tmpVal;
        }
        return Math.sqrt(val / size);
    }

    /**
     * 对整个列表进行PCI分配
     */
    private void assignPciToCellsPlan(PlanObj plan, List<String> descNeedToAssignCellList) {
        // 已手动分配了PCI的小区列表，防止再被重新分配
        List<String> assignedList = new ArrayList<>();
        // 循环分配每个小区
        for (String cellId : descNeedToAssignCellList) {
            // 如果主小区已分配过，则它的同站小区也都分配过，直接跳过。
            if (assignedList.contains(cellId)) {
                continue;
            }
            // 加入已分配列表
            assignedList.addAll(assignPciToCellsPlanForOneCell(plan, cellId).getAssignCells());
        }
    }

    /**
     * 为一个小区和其同站同频小区分配PCI
     */
    private AssignResult assignPciToCellsPlanForOneCell(PlanObj plan, String cellId) {
        long t1 = System.currentTimeMillis();
        // 需要在一个很大的循环中（最极端的情况下 168*6！）使用，故取出与cell相关的MR数据，放入一个较小的集合中
        final SmallParameter smallParameter = new SmallParameter();
        // 重置PCI工具
        pciUtil.reset();
        // 待分配小区
        final List<String> assignCells = new ArrayList<>();
        // 最初排列
        final List<Integer> bestPcis = new ArrayList<>();

        // 小区本身放入待分配列表
        assignCells.add(cellId);
        // 如果cell的同站邻区个数不超过5个，则将同站邻区与cell一起分配
        List<String> sstCells = cellToSameStationCells.get(cellId);
        if (null != sstCells && sstCells.size() <= SAME_STATION_NCELL_SIZE) {
            assignCells.addAll(sstCells);
        }
        // 填充临时数据，减少多线程分配计算量。这里不使用并行，可以用非线程安全的final数据集保存数据，对轻量级的操作减少无谓的并发，让数据类型更加简单
        // 除了 bestPcis 其他数据集以后都不需要再修改，但bestPcis不应该被并行修改，应用缓存代替
        assignCells.forEach(e -> {
            int oldPci = plan.getPciByCell(e);
            if (oldPci == -1) {
                oldPci = cellToOriPci.getOrDefault(e, -1);
            }
            bestPcis.add(oldPci);
            smallParameter.getCellToNotSameStationCells().put(e.intern(),
                    cellToNotSameStationCells.getOrDefault(e, new ArrayList<>()));
            smallParameter.getCellToNcellRelevancy().put(e.intern(),
                    cellToNcellRelevancy.getOrDefault(e, new HashMap<>(32)));
            smallParameter.getNcellToNotSameStationCells().put(e.intern(),
                    ncellToNotSameStationCells.getOrDefault(e, new ArrayList<>()));
            smallParameter.getNcellToCellRelevancy().put(e.intern(),
                    ncellToCellRelevancy.getOrDefault(e, new HashMap<>(32)));
        });
        // 与out和in干扰有关小区的pci映射,利用java8新特新，将Collection<List<String>>合并，并通过flatmap打散，去重，再进行pci提取.同样不使用并行
        Stream.concat(smallParameter.getCellToNotSameStationCells().values().stream(),
                smallParameter.getNcellToNotSameStationCells().values().stream()).flatMap(Collection::stream).distinct()
                .forEach(e -> {
                    int oldPci = plan.getPciByCell(e);
                    if (oldPci == -1) {
                        oldPci = cellToOriPci.getOrDefault(e, -1);
                    }
                    smallParameter.getCellToPci().put(e.intern(), oldPci);
                });
        boolean isUpdated;

        //pci分配的类型
        String pciAssignType = "GROUP";
        if ("GROUP".equals(pciAssignType.toUpperCase())) {
            // 选出每种pci列表下，干扰值与最优的排列的映射
            isUpdated = selectBestPciGroupForCellsByGroup(pciUtil, assignCells, bestPcis, smallParameter);
        } else {
            isUpdated = selectBestPciGroupForCellsByCell(pciUtil, assignCells, bestPcis, smallParameter);
        }
        // 如果pci有变化，则将最优排列更新到方案里
        if (isUpdated) {
            plan.addAllCellsToPcis(assignCells, bestPcis);
        }
        log.debug("分配一个小区的时间：{}", System.currentTimeMillis() - t1);
        return new AssignResult(isUpdated, assignCells);
    }

    /**
     * 选出待选的最优PCI列表
     */
    private boolean selectBestPciGroupForCellsByGroup(final PciUtil pciUtil,
                                                      final List<String> assignCells, final List<Integer> oriPcis, final SmallParameter smallParameter) {
        // 清空结果集
        interValToPciList.clear();
        // 最初排列下的干扰值
        final double oriInterVal = calNotSameStationCellsTotalInterVal(assignCells, oriPcis, smallParameter);

        // 分别对168个组求最优pci排列
        Stream.iterate(0, n -> n += 1).limit(168).parallel().forEach(e -> assignPciGroupsForCells(pciUtil,
                assignCells, oriPcis, e, interValToPciList, oriInterVal, smallParameter));
        // 只有当干扰值小于初始值的情况才会进入interValToPcis中，如果初始值为最优，那么interValToPcis肯定为空，就不会触发对oriPcis的修改
        if (interValToPciList.isEmpty()) {
            return false;
        }
        // 在最优pci排列结果集中，找出最优pci排列。
        interValToPciList.entrySet().parallelStream().min(Comparator.comparingDouble(Entry::getKey))
                .ifPresent(e -> {
                    log.debug("assignCells={},oriInterVal={},minInterVal={},oriPcis={},bestPcis={}", assignCells, oriInterVal, e.getKey(), oriPcis, e.getValue());
                    oriPcis.clear();
                    oriPcis.addAll(e.getValue());
                });
        return true;
    }

    /**
     * 从待选排列中选出干扰值最小的PCI排列
     */
    private void assignPciGroupsForCells(final PciUtil pciUtil, final List<String> assignCells,
                                         final List<Integer> oriPcis, final int groupId, final Map<Double, List<Integer>> resultMap,
                                         final double oriInterVal, final SmallParameter smallParameter) {
        // 获取备选pci组
        final List<List<Integer>> pciAllocList = new ArrayList<>();
        allocSameStationOtherCellsPci(pciAllocList, pciUtil.getPciGroupsById(groupId, assignCells.size()),
                new ArrayList<>(), assignCells.size());
        // 为每个备选pci组，循环分配pci
        double minInterVal = oriInterVal;
        List<Integer> bestPcis = new ArrayList<>(oriPcis);
        for (List<Integer> pics : pciAllocList) {
            double interVal = calNotSameStationCellsTotalInterVal(assignCells, pics, smallParameter);
            if (interVal < minInterVal) {
                minInterVal = interVal;
                bestPcis = new ArrayList<>(pics);
            }
        }
        // 如果新的最小干扰值小于初始干扰值，则将结果存入结果集中
        if (minInterVal < oriInterVal) {
            resultMap.put(minInterVal, bestPcis);
        }
    }

    /**
     * 选出待选的最优PCI列表
     */
    private boolean selectBestPciGroupForCellsByCell(final PciUtil pciUtil, final List<String> assignCells, final List<Integer> oriPcis, final SmallParameter smallParameter) {
        // 最初排列下的干扰值
        double minInterVal = calNotSameStationCellsTotalInterVal(assignCells.get(0), oriPcis.get(0), smallParameter);
        // 为主小区选出最优PCI
        for (int pci = 0; pci < PCI_AMOUNT; pci++) {
            double interVal = calNotSameStationCellsTotalInterVal(assignCells.get(0), pci, smallParameter);
            if (interVal < minInterVal) {
                minInterVal = interVal;
                oriPcis.set(0, pci);
            }
        }
        // 有同站小区，继续分配
        if (assignCells.size() > 1) {
            assignPciForCell(pciUtil, assignCells, oriPcis, smallParameter);
        }
        return true;
    }

    /**
     * 为同站同频其他小区分配PCI
     */
    private void assignPciForCell(final PciUtil pciUtil, final List<String> assignCells,
                                  final List<Integer> oriPcis, final SmallParameter smallParameter) {
        // 当list的长度大于0时，subList(1, size) 是安全的，sublist 不改变原对象，运用clear操作可以清空原list对应的数据
        final List<String> sstCells = assignCells.subList(1, assignCells.size());
        final List<Integer> oriSstPcis = oriPcis.subList(1, oriPcis.size());

        List<List<Integer>> pciAllocList = new ArrayList<>();
        allocSameStationOtherCellsPci(pciAllocList, pciUtil.getPciGroups(oriPcis.get(0), sstCells.size()),
                new ArrayList<>(), sstCells.size());
        double minInterVal = Double.MAX_VALUE;
        for (List<Integer> pics : pciAllocList) {
            // 分配PCI
            double interVal = calNotSameStationCellsTotalInterVal(sstCells, pics, smallParameter);
            if (interVal < minInterVal) {
                minInterVal = interVal;
                oriSstPcis.clear();
                oriSstPcis.addAll(pics);
            }
        }
    }

    /**
     * 计算非同站小区的总干扰值
     */
    private double calNotSameStationCellsTotalInterVal(final List<String> assignCells,
                                                       final List<Integer> pics, final SmallParameter smallParameter) {
        return Stream.iterate(0, n -> n += 1)
                .limit(assignCells.size())
                .mapToDouble(i -> calNotSameStationCellsTotalInterVal(assignCells.get(i), pics.get(i), smallParameter))
                .sum();
    }

    /**
     * 计算非同站小区的总干扰值
     */
    private double calNotSameStationCellsTotalInterVal(final String cellId, final int cellPci,
                                                       final SmallParameter smallParameter) {
        // out 干扰 + in 干扰
        return calNotSameStationCellsTotalInterVal(cellId, cellPci, smallParameter.getCellToNotSameStationCells(),
                smallParameter.getCellToNcellRelevancy(), smallParameter.getCellToPci())
                + calNotSameStationCellsTotalInterVal(cellId, cellPci,
                smallParameter.getNcellToNotSameStationCells(), smallParameter.getNcellToCellRelevancy(),
                smallParameter.getCellToPci());
    }

    /**
     * 计算干扰值
     */
    private double calNotSameStationCellsTotalInterVal(final String cellId, final int cellPci,
                                                       final Map<String, List<String>> cellToCells, final Map<String, Map<String, Double>> assocDegree,
                                                       final Map<String, Integer> cellToPci) {
        // 如果pci小于0 则pci不存在，返回0
        if (cellPci < 0) {
            return 0;
        }
        return cellToCells.getOrDefault(cellId, new ArrayList<>()).stream()
                .mapToDouble(e -> getModVal(cellPci, cellToPci.get(e)) * assocDegree.get(cellId).getOrDefault(e, 0.0))
                .sum() * calKs(cellId);
    }

    /**
     * 计算两个小区间的模系数
     */
    private double getModVal(int cellPci, int ncellPci) {
        // cellPci 已经检查过，检查ncellPci
        if (ncellPci < 0) {
            return 0;
        }
        double modVal = 0;
        if (cellPci % MOD_3 == (ncellPci % MOD_3)) {
            modVal += config.getM3r();
        }
        if (cellPci % MOD_6 == (ncellPci % MOD_6)) {
            modVal += config.getM6r();
        }
        if (cellPci % MOD_30 == (ncellPci % MOD_30)) {
            modVal += config.getM30r();
        }
        return modVal;
    }

    /**
     * 分配同站其他小区的递归算法
     */
    private void allocSameStationOtherCellsPci(final List<List<Integer>> pciAllocList, final List<Integer> pcis,
                                               final List<Integer> tmpPcis, final int length) {
        if (length == 0) {
            pciAllocList.add(tmpPcis);
            return;
        }
        List<Integer> tmpPcis2;
        for (Integer i : pcis) {
            if (!tmpPcis.contains(i)) {
                tmpPcis2 = new ArrayList<>(tmpPcis);
                tmpPcis2.add(i);
                allocSameStationOtherCellsPci(pciAllocList, pcis, tmpPcis2, length - 1);
            }
        }
    }

    /**
     * 在首套方案中为所有主小区分配原始PCI
     * chen.c10在添加D1D2频率调整时修改，增加频点以适用频点变动要求
     */
    private void assignOriPciAndEarfcnByDescCells(PlanObj plan) {
        for (String cellId : cellToTotalRelevancy.keySet()) {
            if (cellToOriPci.containsKey(cellId) && cellToOriEarfcn.containsKey(cellId)) {
                // 进行首次分配
                plan.assignFirstTime(cellId, cellToOriPci.get(cellId), cellToOriEarfcn.get(cellId));
                // 为同站其他小区分配原始PCI和earfcn
                if (cellToSameStationCells.get(cellId) != null) {
                    for (String oCellId : cellToSameStationCells.get(cellId)) {
                        if (cellToOriPci.containsKey(oCellId) && cellToOriEarfcn.containsKey(cellId)) {
                            // 进行首次分配
                            plan.assignFirstTime(oCellId, cellToOriPci.get(oCellId), cellToOriEarfcn.get(oCellId));
                        }
                    }
                }
            }
        }
    }

    /**
     * 进行邻区核查，为了保证服务小区跟所有邻区之间的pci不相等，相等就+-30来修正
     *
     * @param plan         当前方案
     * @param descCellList 小区与邻区列的映射的列表
     */
    private void checkNCellPci(PlanObj plan, List<String> descCellList) {
        if (log.isDebugEnabled()) {
            log.debug("===>>>checkNCellPci():进入pci+-30循环");
        }

        // 检查服务小区，邻区之间是否存在同pci,存在则+30或-30
        for (String aDescCellList : descCellList) {
            List<String> cells = cellToNotSameStationCells.get(aDescCellList);
            if (cells == null) {
                continue;
            }

            // 最多检查前50个非同站小区
            int maxCheck = cells.size();
            if (maxCheck > 50) {
                maxCheck = 50;
            }

            // 加入服务小区,服务小区不能和其他小区相同pci
            cells.add(aDescCellList.intern());

            // 获取小区列所使用的pci
            Map<String, Integer> ncellToPci = new HashMap<>(32);
            for (String c : cells) {
                ncellToPci.put(c.intern(), plan.getPciByCell(c));
            }

            int count = 0;
            for (int k = 0; k < maxCheck; k++) {
                for (int j = 0; j < maxCheck; j++) {
                    if (count > 1000) {
                        count = 0;
                        break;
                    }
                    if (k == j) {
                        continue;
                    }
                    // 不需要优化的小区，跳过
                    if (!descNeed2AssignCells.contains(cells.get(k))) {
                        continue;
                    }
                    int pci1 = plan.getPciByCell(cells.get(k));
                    int pci2 = plan.getPciByCell(cells.get(j));
                    if (pci1 == -1 || pci2 == -1) {
                        continue;
                    }
                    if (pci1 == pci2) {
                        if (503 >= (pci1 + 30)) {
                            pci1 += 30;
                            while (ncellToPci.containsValue(pci1)) {
                                if (503 < (pci1 + 30)) {
                                    break;
                                }
                                pci1 += 30;
                            }
                            plan.addCellToPci(cells.get(k), pci1);
                            ncellToPci.put(cells.get(k).intern(), pci1);
                        } else {
                            pci1 -= 30;
                            while (ncellToPci.containsValue(pci1)) {
                                if (pci1 < 30) {
                                    break;
                                }
                                pci1 -= 30;
                            }
                            plan.addCellToPci(cells.get(k), pci1);
                            ncellToPci.put(cells.get(k).intern(), pci1);
                        }
                    }
                    count++;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("===>>>checkNCellPci():退出pci+-30循环");
        }
    }

    /**
     * 检查在主小区距离限制范围内有无同频同pci的情况，有则进行+-30处理
     */
    private void checkNeighborAreaPci(PlanObj plan, List<String> descNeedToAssignCellList) {
        if (log.isDebugEnabled()) {
            log.debug("===>>>checkNeighborAreaPci2():进入pci+-30循环");
        }

        long t1 = System.nanoTime();
        Map<String, Integer> cellToNewPci = new HashMap<>(cellToOriPci);
        Map<Integer, Integer> pciToNewAllocAmount = new HashMap<>(pciToAllocAmount);
        cellToNewPci.putAll(plan.getCellToPci());
        if (log.isDebugEnabled()) {
            log.debug("工参小区数量为: {}", cellToNewPci.size());
            log.debug("方案小区数量为：{}", plan.getCellToPci().size());
            log.debug("修改小区数量为：{}", descNeedToAssignCellList.size());
        }
        Map<Integer, List<String>> pciToCell = new HashMap<>(504);
        for (String cid : cellToNewPci.keySet()) {
            int pci = cellToNewPci.get(cid);
            // 如果方案列表里面不包含该小区
            if (!plan.getCellToPci().containsKey(cid)) {
                pciToNewAllocAmount.put(pci, pciToNewAllocAmount.get(pci) + 1);
            }
            if (!pciToCell.containsKey(pci)) {
                pciToCell.put(pci, new ArrayList<>());
            }
            pciToCell.get(pci).add(cid);
        }

        List<String> cellToAllocList = new ArrayList<>();
        List<String> descCellList = new ArrayList<>(descNeedToAssignCellList);
        isSamePciTooClose = false;

        List<String> sstCells;
        int mPci;
        int mPciGp;
        Map<Integer, List<Integer>> pciGroup = new HashMap<>(168);

        for (String cellId : descCellList) {
            // 如果作为同站小区分配过了，则跳过
            if (cellToAllocList.contains(cellId)) {
                continue;
            }
            mPci = cellToNewPci.get(cellId);
            mPciGp = (int) Math.floor(mPci / 3);
            // 获取该小区的同站小区
            sstCells = new ArrayList<>();
            if (cellToSameStationCells.containsKey(cellId)) {
                sstCells = cellToSameStationCells.get(cellId);
            }
            // 与主小区同组的PCI列表，都需要参与计算
            Map<String, Integer> sgCellToPci = new HashMap<>(32);
            // 将主小区本身加入其中
            sgCellToPci.put(cellId.intern(), mPci);
            // 计算同组小区
            for (String sstCellId : sstCells) {
                if (cellToNewPci.containsKey(sstCellId)) {
                    int sstPci = cellToNewPci.get(sstCellId);
                    if ((int) Math.floor(sstPci / 3) == mPciGp) {
                        sgCellToPci.put(sstCellId.intern(), sstPci);
                    }
                }
            }
            boolean sameEarfcnSamePciCloseFlag = false;
            // 先检查有无同频同pci的小区组，大于距离限制，sameEarfcnSamePciCloseFlag = false为大于
            for (String sgCell : sgCellToPci.keySet()) {
                // 检查五公里有无同频同PCI
                if (checkDisSameFreqSamePci(plan, sgCell, pciToCell.get(sgCellToPci.get(sgCell)))) {
                    sameEarfcnSamePciCloseFlag = true;
                    break;
                }
            }
            // 如果有同频同pci不满足距离限制
            if (sameEarfcnSamePciCloseFlag) {
                // 按PCI除以3的整数部分进行分组，如1和2都为0，为0组，4，和5都为1，为1组。
                pciGroup.clear();
                // 0到503可以分为168组
                for (int i = 0; i < PCI_GROUP_AMOUNT; i++) {
                    // 排除主小区本身的组，选择与主小区组模10值相同的组，这样能保证PCI模30值相同。
                    if (i != mPci / 3 && i % 10 == mPci / 3 % 10) {
                        List<Integer> l = new ArrayList<>();
                        // 组号*3再加上PCI的模3值可得到与PCI模30值相同的PCI
                        for (int j : sgCellToPci.values()) {
                            l.add(i * 3 + j % 3);
                        }
                        pciGroup.put(i, l);
                    }
                }
                // 用于配对的模30PCI组分配完毕。
                Map<Integer, Double> groupToDis = new HashMap<>(32);
                Map<Integer, Double> mcPciGpToDis = new HashMap<>(32);
                // 按组号进行循环，计算每一组的情况。
                for (int pciGpId : pciGroup.keySet()) {
                    List<Double> minDisList = new ArrayList<>();
                    // 对该组中每个PCI进行计算。
                    for (int pci : pciGroup.get(pciGpId)) {
                        // 获取该PCI对应的cellId,只为了获取该小区频点，待优化。
                        String tmpCellId = "";
                        for (String c : sgCellToPci.keySet()) {
                            if (pci % 30 == sgCellToPci.get(c) % 30) {
                                tmpCellId = c;
                            }
                        }
                        // 防止获取不到cellId，保护
                        if (tmpCellId.length() == 0) {
                            continue;
                        }
                        // 获取频点
                        Map<String, Double> closeCells = new HashMap<>(32);
                        Map<String, Double> farCells = new HashMap<>(32);
                        if (pciToCell.containsKey(pci)) {
                            // 判断已分配pci
                            for (String spCellId : pciToCell.get(pci)) {
                                // 检查五公里有无同频PCI为待选PCI的小区
                                if (!tmpCellId.equals(spCellId)
                                        && plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(tmpCellId)) {
                                    // 检查有无小于距离限制的小区
                                    // 计算同频小区与计算小区的距离。这里因为都是主小区的同站小区，tmpCellId可以用主小区ID代替。
                                    double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(tmpCellId));
                                    // 保存小区距离限制的小区
                                    if (dis < config.getDisLimit()) {
                                        closeCells.put(spCellId.intern(), dis);
                                    } else {
                                        farCells.put(spCellId.intern(), dis);
                                    }
                                }
                            }
                        }
                        // 如果5公里内有同频小区，保存最小距离
                        if (closeCells.size() > 0) {
                            // 一组最多3个pci
                            // 采用Double.MAX_VALUE/6来表示单个距离的最大值，保证后续求平均计算时不会导致超过double的情况
                            double minDis = Double.MAX_VALUE / 6;
                            for (double dis : closeCells.values()) {
                                if (minDis > dis) {
                                    minDis = dis;
                                }
                            }
                            minDisList.add(minDis);
                        } else if (farCells.size() > 0) {
                            // 保存结果为距离限制
                            double minDis = Double.MAX_VALUE / 6;
                            for (double dis : farCells.values()) {
                                if (minDis > dis) {
                                    minDis = dis;
                                }
                            }
                            minDisList.add(minDis);
                        } else {
                            // 如果以上两个条件都不满足，说明当前pci未使用过
                            minDisList.add(Double.MAX_VALUE / 6);
                        }
                    }

                    if (minDisList.size() > 0) {
                        // 最近距离
                        double minDis = Double.MAX_VALUE / 6;
                        for (double d : minDisList) {
                            if (minDis > d) {
                                minDis = d;
                            }
                        }
                        // 如果最近距离小于距离限制，则保存。
                        if (minDis < config.getDisLimit()) {
                            groupToDis.put(pciGpId, minDis);
                        } else {// 如果最近距离不小于距离限制，说明该组PCI每个PCI都满足要求
                            // 选择找到符合要求的组就结束运算采用这组还是缓存这组数据？在所有满足条件的组中选择最优的？
                            mcPciGpToDis.put(pciGpId, minDis);
                        }
                    } else {
                        // 如果不满足距离限制的距离列表为空，说明该pci组可用
                        mcPciGpToDis.put(pciGpId, Double.MAX_VALUE / 6);
                    }
                }
                // 缓存最终分配PCI的组
                int tmpPciGp = mPciGp;
                // 如果不存在满足条件的PCI组，只能选择平均距离最远的组
                if (mcPciGpToDis.isEmpty()) {
                    // 判断放弃同组匹配，改用单小区匹配,检查五公里有无同频同PCI
                    if (checkDisSameFreqSamePci(plan, cellId, pciToCell.get(sgCellToPci.get(cellId)))) {
                        Map<Integer, Double> canAssignPcis = new HashMap<>(504);
                        for (int pci = 0; pci < PCI_AMOUNT; pci++) {
                            if (pci % 30 != mPci % 30) {
                                continue;
                            }
                            if (!pciToCell.containsKey(pci)) {
                                canAssignPcis.put(pci, Double.MAX_VALUE / 3);
                            } else {
                                // 判断已分配pci
                                for (String spCellId : pciToCell.get(pci)) {
                                    // 检查五公里有无同频PCI为待选PCI的小区
                                    if (!cellId.equals(spCellId)
                                            && plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(cellId)) {
                                        // 检查有无小于距离限制的小区
                                        // 计算同频小区与计算小区的距离。这里因为都是主小区的同站小区，tmpCellId可以用主小区ID代替。
                                        double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(cellId));
                                        // 保存小区距离限制的小区
                                        if (dis > config.getDisLimit()) {
                                            canAssignPcis.put(pci, dis);
                                        }
                                    }
                                }
                            }
                        }
                        if (!canAssignPcis.isEmpty()) {
                            double minDis = Double.MAX_VALUE;
                            List<Integer> selects = new ArrayList<>();
                            for (double dis : canAssignPcis.values()) {
                                if (dis < minDis) {
                                    minDis = dis;
                                }
                            }
                            for (Integer pci : canAssignPcis.keySet()) {
                                if (canAssignPcis.get(pci) == minDis) {
                                    selects.add(pci);
                                }
                            }
                            int minAllocAmount = Integer.MAX_VALUE;
                            int minCntPci = mPci;
                            for (Integer pci : selects) {
                                if (pciToNewAllocAmount.get(pci) < minAllocAmount) {
                                    minAllocAmount = pciToNewAllocAmount.get(pci);
                                    minCntPci = pci;
                                }
                            }
                            // 分配新PCI
                            plan.addCellToPci(cellId, minCntPci);

                            // 全网小区缓存也要更新
                            // 进行 PCI 分配次数的记录，先为新的 pci 分配次数累加1
                            // 初始化时为复用次数分配了0，不进行null判断
                            pciToNewAllocAmount.put(minCntPci, pciToNewAllocAmount.get(minCntPci) + 1);
                            // 把cellId分配给新的pci
                            if (!pciToCell.containsKey(minCntPci)) {
                                pciToCell.put(minCntPci, new ArrayList<>());
                            }
                            pciToCell.get(minCntPci).add(cellId.intern());
                            // 再把旧的 pci 分配次数减1
                            if (cellToNewPci.containsKey(cellId)) {
                                int oldPci = cellToNewPci.get(cellId);
                                pciToNewAllocAmount.put(oldPci, pciToNewAllocAmount.get(oldPci) - 1);
                                // 把cellId从旧的pci中删除
                                pciToCell.get(cellToNewPci.get(cellId)).remove(cellId);
                            }
                            cellToNewPci.put(cellId.intern(), minCntPci);

                            // 添加到已经分配过的列表
                            if (!cellToAllocList.contains(cellId)) {
                                cellToAllocList.add(cellId.intern());
                            }
                            continue;
                        }
                    }
                    // 发现同频同PCI不满足距离限制的小区，将标记改为真。
                    isSamePciTooClose = true;

                    double maxDis = 0.0;
                    // 获取平均距离最远的PCI组
                    for (Integer pciGp : groupToDis.keySet()) {
                        if (maxDis < groupToDis.get(pciGp)) {
                            maxDis = groupToDis.get(pciGp);
                            tmpPciGp = pciGp;
                        }
                    }
                    // 如果有多个PCI距离相等，缓存
                    List<Integer> sameDisPciList = new ArrayList<>();
                    for (Integer pciGp : groupToDis.keySet()) {
                        if (maxDis == groupToDis.get(pciGp)) {
                            sameDisPciList.add(pciGp);
                        }
                    }
                    // 如果缓存多余一个，则选择复用度较小的,复用度再相等的情况不再考虑
                    tmpPciGp = pickMinReuseDegreePciGp(tmpPciGp, sameDisPciList, pciGroup, pciToNewAllocAmount);
                } else {
                    // 允许一组或多组距离大于距离限制的组
                    double maxDis = 0.0;
                    // 获取平均距离最远的PCI组
                    for (Integer pciGp : mcPciGpToDis.keySet()) {
                        if (maxDis < mcPciGpToDis.get(pciGp)) {
                            maxDis = mcPciGpToDis.get(pciGp);
                            tmpPciGp = pciGp;
                        }
                    }
                    // 如果有多个PCI距离相等，缓存
                    List<Integer> sameDisPciList = new ArrayList<>();
                    for (Integer pciGp : mcPciGpToDis.keySet()) {
                        if (maxDis == mcPciGpToDis.get(pciGp)) {
                            sameDisPciList.add(pciGp);
                        }
                    }
                    tmpPciGp = pickMinReuseDegreePciGp(tmpPciGp, sameDisPciList, pciGroup, pciToNewAllocAmount);
                }
                // 如果最优的组有变化，则需要修改
                if (tmpPciGp != mPciGp) {
                    // 为这组分配PCI
                    for (int pci : pciGroup.get(tmpPciGp)) {
                        for (String c : sgCellToPci.keySet()) {
                            // 对PCI进行mod3匹配，找到这个pci对应的小区
                            if (pci % 3 == sgCellToPci.get(c) % 3) {
                                // 分配新PCI
                                plan.addCellToPci(c, pci);

                                // 全网小区缓存也要更新
                                // 进行 PCI 分配次数的记录，先为新的 pci 分配次数累加1
                                // 初始化时为复用次数分配了0，不进行null判断
                                pciToNewAllocAmount.put(pci, pciToNewAllocAmount.get(pci) + 1);
                                // 把cellId分配给新的pci
                                if (!pciToCell.containsKey(pci)) {
                                    pciToCell.put(pci, new ArrayList<>());
                                }
                                pciToCell.get(pci).add(c.intern());
                                // 再把旧的 pci 分配次数减1
                                if (cellToNewPci.containsKey(c)) {
                                    int oldPci = cellToNewPci.get(c);
                                    pciToNewAllocAmount.put(oldPci, pciToNewAllocAmount.get(oldPci) - 1);
                                    // 把cellId从旧的pci中删除
                                    pciToCell.get(cellToNewPci.get(c)).remove(c);
                                }
                                cellToNewPci.put(c.intern(), pci);

                                // 添加到已经分配过的列表
                                if (!cellToAllocList.contains(c)) {
                                    cellToAllocList.add(c.intern());
                                }
                            }
                        }
                    }
                } else {
                    // 无变化也要将小区加入已经分配过的列表
                    for (String sstCellId : sgCellToPci.keySet()) {
                        if (!cellToAllocList.contains(sstCellId)) {
                            cellToAllocList.add(sstCellId.intern());
                        }
                    }
                }
            } else {
                // 如果不需要更改，也要将主小区和他的同组同站小区加入到已处理列表。
                for (String sstCellId : sgCellToPci.keySet()) {
                    if (!cellToAllocList.contains(sstCellId)) {
                        cellToAllocList.add(sstCellId.intern());
                    }
                }
            }
        }
        log.debug("===>>>checkNeighborAreaPci():退出pci+-30循环，本次距离运算，结果为：{}，descCellList size={},用时：{}毫秒。",
                isSamePciTooClose, plan.getCellToPci().size(), (System.nanoTime() - t1) / 1000000);
    }

    /**
     * 选择复用度最小的pci组
     */
    private int pickMinReuseDegreePciGp(int tmpPciGp, List<Integer> sameDisPciList, Map<Integer, List<Integer>> pciGroup, Map<Integer, Integer> pciToNewAllocAmount) {
        if (sameDisPciList.size() > 1) {
            int tmpLastAllocAmount = Integer.MAX_VALUE;
            // 循环平均距离相同的组
            for (int pciGp : sameDisPciList) {
                // 计算该组PCI的总复用度
                int totAllocAmount = 0;
                for (int pci : pciGroup.get(pciGp)) {
                    totAllocAmount += pciToNewAllocAmount.get(pci);
                }
                // 如果总复用度有减少，更新，并保存相应的组号。
                if (tmpLastAllocAmount > totAllocAmount) {
                    tmpLastAllocAmount = totAllocAmount;
                    tmpPciGp = pciGp;
                }
            }
        }
        return tmpPciGp;
    }

    /**
     * 检查五公里有无同频同PCI
     */
    private boolean checkDisSameFreqSamePci(PlanObj plan, String cellId, List<String> cells) {
        for (String spCellId : cells) {
            // 先找出同频同pci的小区
            if (!cellId.equals(spCellId) && plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(cellId)) {
                // 检查有无小于距离限制的小区
                double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(cellId));
                if (dis < config.getDisLimit()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * map根据value值,从大到小排序
     */
    private Map<String, Double> sortMapByValue(Map<String, Double> unsortMap) {
        return unsortMap.entrySet().parallelStream().sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .collect(LinkedHashMap::new, (m, p) -> m.put(p.getKey(), p.getValue()), Map::putAll);
    }

    /**
     * 对由long值组成的List<String>按照从小到大排序
     */
    private List<String> sortStringList(List<String> unSortList) {
        return unSortList.parallelStream().sorted(Comparator.comparingLong(o -> Long.parseLong(o.replace("-", ""))))
                .collect(Collectors.toList());
    }

    /**
     * 保存最终方案到oracle
     */
    private void saveBestPlan(PlanObj plan) {
        if (log.isDebugEnabled()) {
            log.debug("把最佳方案写入到oracle");
        }
        try {
            pciAfpRestService.batchInsertBestPlan(plan2Table(plan, -1));
        } catch (Exception e) {
            log.error("pci最终方案保存失败！");
            e.printStackTrace();
        }
    }

    /**
     * 生成表格数据
     */
    private List<PlanItem> plan2Table(PlanObj plan, int planNum) {
        log.info("最终方案小区数：{}", plan.getCellToPci().size());
        List<String> need2AssignCells = config.getNeed2AssignCells();
        log.info("需要优化小区数：{}", need2AssignCells.size());
        // 将MR数据和待优化PCI的小区都并在一起输出
        // MR数据存在原始干扰值，计算干扰值，计算PCI
        // 待优化PCI小区中可能存在着部分小区在这批MR数据中并不存在，所以不能进行计算干扰值和分配PCI
        // 这里处理的方法是将MR数据中的存在的所有小区都输出，然后检查待优化小区哪些是不存在于MR数据中的小区
        // 将这些小区也以默认值输出，默认原干扰值0，计算干扰值0，PCI为-1

        List<PlanItem> items = new ArrayList<>();

        // 写入数据中存在的小区PCI以及干扰值
        for (String cellId : sortStringList(new ArrayList<>(plan.getCellToPci().keySet()))) {
            PlanItem planItem = new PlanItem();
            planItem.setJobId(config.getJobId());
            planItem.setPlanNum(planNum);
            planItem.setCellId(cellId);
            planItem.setEarfcn(plan.getEarfcnByCell(cellId));
            planItem.setPci(plan.getPciByCell(cellId));
            if (cellToOriInterVal.get(cellId) != null) {
                planItem.setOldInterVal(cellToOriInterVal.get(cellId));
            }
            planItem.setNewInterVal(plan.getInterValByCell(cellId));

            items.add(planItem);
        }

        List<String> cellsNotInData = new ArrayList<>();
        for (String cellId : need2AssignCells) {
            if (!plan.getCellToPci().containsKey(cellId)) {
                cellsNotInData.add(cellId.intern());
            }
        }

        // 写入数据中不存在的小区默认PCI以及干扰值
        for (String cellId : sortStringList(cellsNotInData)) {
            PlanItem planItem = new PlanItem();
            planItem.setJobId(config.getJobId());
            planItem.setPlanNum(planNum);
            planItem.setCellId(cellId);
            items.add(planItem);
        }
        return items;
    }

    /**
     * 获取ks
     */
    private double calKs(String cellId) {
        return cellToKs.containsKey(cellId) ? cellToKs.get(cellId) : 1;
    }

    /**
     * 将PCI分成168个组，每组三个PCI。
     */
    class PciUtil {
        /**
         * 按照[0,1,2],[3,4,5]....分组
         */
        List<List<Integer>> pciGroups = new ArrayList<>();

        /**
         * 当前获取到第几个pci组
         */
        int step = 0;

        PciUtil() {
            List<Integer> temp = new ArrayList<>();
            for (int i = 0; i < PCI_AMOUNT; i++) {
                temp.add(i);
                if ((i + 1) % 3 == 0) {
                    pciGroups.add(temp);
                    temp = new ArrayList<>();
                }
            }
        }

        /**
         * 重置PCI分组工具
         */
        void reset() {
            step = 0;
        }

        /**
         * 通过组Id获取一组pci列表
         */
        List<Integer> getPciGroups(int pci, int length) {
            List<Integer> result = getPciGroupsById(pci / 3, length + 1);
            result.remove(new Integer(pci));
            // 创建新的list不影响源数据
            return result;
        }

        /**
         * 通过组Id获取一组pci列表
         */
        List<Integer> getPciGroupById(int groupId) {
            return new ArrayList<>(pciGroups.get(groupId));
        }

        /**
         * 通过组Id获取一组pci列表
         */
        List<Integer> getPciGroupsById(int groupId, int length) {
            List<Integer> result = new ArrayList<>();
            // groupId可能为final
            int tmpStep = groupId;
            // length 必须大于0
            for (int i = 0; i < (length - 1) / 3 + 1; i++) {
                if (tmpStep > 167) {
                    tmpStep = 0;
                }
                result.addAll(getPciGroupById(tmpStep++));
            }
            // 创建新的list不影响源数据
            return result;
        }
    }

    /**
     * PCI优化方案
     */
    class PlanObj implements Cloneable {

        /**
         * 保存已分配 PCI 的小区
         **/
        private HashMap<String, Integer> cellToPci = new HashMap<>();

        /**
         * 保存PCI与小区的映射，用于计算同PCI小区距离
         **/
        private HashMap<Integer, List<String>> pciToCell = new HashMap<>();

        /**
         * 保存已计算干拢值的小区
         **/
        private ConcurrentHashMap<String, Double> cellToInterVal = new ConcurrentHashMap<>();

        /**
         * 保存已计算干拢值的需要分配的小区
         **/
        private ConcurrentHashMap<String, Double> needAssignCellToInterVal = new ConcurrentHashMap<>();

        /**
         * 小区与频率的映射
         **/
        private HashMap<String, Integer> cellToEarfcn = new HashMap<>();

        /**
         * 同频段下小区列表
         **/
        private HashMap<Integer, List<String>> earfcnToCell = new HashMap<>();

        /**
         * 总干扰值
         **/
        private double totalInterVal = 0;

        HashMap<String, Integer> getCellToPci() {
            return cellToPci;
        }

        /**
         * 获取已计算干拢值的小区MAP
         *
         * @return 已计算干拢值的小区MAP
         */
        ConcurrentHashMap<String, Double> getCellToInterVal() {
            return cellToInterVal;
        }

        /**
         * 进行第一次分配
         *
         * @param cellId 小区id
         * @param pci    pci
         * @param earfcn 频点
         */
        void assignFirstTime(String cellId, Integer pci, Integer earfcn) {
            if (!cellToPci.containsKey(cellId) && !cellToEarfcn.containsKey(cellId)) {
                // 为当前小区分配原始PCI
                addCellToPci(cellId, pci);
                // 为当前小区分配原始earfcn
                addCellToEarfcn(cellId, earfcn);
            }
        }

        void addCellToEarfcn(String cellId, Integer earfcn) {
            // 把cellId分配给新的pci
            if (!earfcnToCell.containsKey(earfcn)) {
                earfcnToCell.put(earfcn, new ArrayList<>());
            }
            earfcnToCell.get(earfcn).add(cellId.intern());
            // 再把旧的 pci 分配次数减1
            if (cellToEarfcn.containsKey(cellId)) {
                // 把cellId从旧的pci中删除
                earfcnToCell.get(cellToEarfcn.get(cellId)).remove(cellId);
            }
            cellToEarfcn.put(cellId.intern(), earfcn);
        }

        void addCellToPci(String cellId, Integer pci) {
            // 进行 PCI 分配次数的记录，先为新的 pci 分配次数累加1
            pciToAllocAmount.put(pci, pciToAllocAmount.get(pci) + 1);
            // 把cellId分配给新的pci
            List<String> cells = pciToCell.get(pci);
            if (null == cells) {
                pciToCell.put(pci, new ArrayList<>());
                cells = pciToCell.get(pci);
            }
            cells.add(cellId.intern());
            // 再把旧的 pci 分配次数减1
            Integer oldPci = cellToPci.get(cellId);
            if (null != oldPci) {
                pciToAllocAmount.put(oldPci, pciToAllocAmount.get(oldPci) - 1);
                // 把cellId从旧的pci中删除
                pciToCell.get(cellToPci.get(cellId)).remove(cellId);
            }
            cellToPci.put(cellId.intern(), pci);
        }

        void addAllCellsToPcis(List<String> cells, List<Integer> pcis) {
            if (cells.size() == pcis.size()) {
                for (int i = 0; i < cells.size(); i++) {
                    addCellToPci(cells.get(i), pcis.get(i));
                }
            }
        }

        void addCellToInterVal(String cellId, double interVal) {
            cellToInterVal.put(cellId.intern(), interVal);
            if (descNeed2AssignCells.contains(cellId)) {
                needAssignCellToInterVal.put(cellId.intern(), interVal);
            }
        }

        int getEarfcnByCell(String cellId) {
            if (cellToEarfcn.containsKey(cellId)) {
                return cellToEarfcn.get(cellId);
            }
            return 0;
        }

        int getPciByCell(String cellId) {
            return cellToPci.getOrDefault(cellId, -1);
        }

        double getInterValByCell(String cellId) {
            if (cellToInterVal.containsKey(cellId)) {
                return cellToInterVal.get(cellId);
            }
            return 0;
        }

        double getTotalInterVal() {
            return totalInterVal;
        }

        /**
         * 计算当前方案的每个小区对应的干扰值
         */
        void calInterVal(Map<String, List<String>> cellToNcells) {
            totalInterVal = 0;
            List<String> ncells;
            for (Entry<String, Integer> one : cellToPci.entrySet()) {
                ncells = cellToNcells.getOrDefault(one.getKey(), new ArrayList<>());
                // 计算干扰值
                double interVal = ncells.parallelStream()
                        .mapToDouble(e -> getModVal(e, one.getValue()) * getRelaVal(one.getKey(), e))
                        .sum() * calKs(one.getKey());
                // 更新总干扰值
                totalInterVal += interVal;
                // 更新小区与干扰值的映射
                addCellToInterVal(one.getKey(), interVal);
            }
        }

        /**
         * 计算当前方案的每个小区对应的干扰值
         */
        void calInterVal(final String cellId) {
            calInterValForOneCell(cellId);
            List<String> sstCells = cellToSameStationCells.get(cellId);
            if (null != sstCells && sstCells.size() <= SAME_STATION_NCELL_SIZE) {
                sstCells.parallelStream().filter(e -> !e.equals(cellId)).forEach(this::calInterValForOneCell);
            }
        }

        /**
         * 计算当前方案的单个小区对应的干扰值
         */
        void calInterValForOneCell(String cellId) {
            // 计算cell作为out小区时自身的干扰值变化
            changeCellToInterVal(cellId, cellToPci.get(cellId),
                    cellToNotSameStationCells.getOrDefault(cellId, new ArrayList<>()));
            // 计算cell作为in小区时引起主小区的干扰值变化
            ncellToNotSameStationCells.getOrDefault(cellId, new ArrayList<>()).parallelStream()
                    .forEach(e -> changeCellToInterVal(e, cellToPci.get(e),
                            cellToNotSameStationCells.getOrDefault(e, new ArrayList<>())));
        }

        /**
         * 计算小区新干扰值，并更新相关数据
         */
        private void changeCellToInterVal(String cellId, int pci, List<String> notSameStationCells) {
            // 计算干扰值
            double interVal = notSameStationCells.parallelStream()
                    .mapToDouble(e -> getModVal(e, pci) * getRelaVal(cellId, e)).sum() * calKs(cellId);
            // 更新总干扰值
            totalInterVal += interVal - cellToInterVal.get(cellId);
            // 更新小区与干扰值的映射
            addCellToInterVal(cellId, interVal);
        }

        /**
         * 获取模值
         */
        private double getModVal(String ncellId, int cellPci) {
            int ncellPci = getPciByCell(ncellId);
            if (ncellPci < 0) {
                ncellPci = cellToOriPci.getOrDefault(ncellId, -1);
                if (ncellPci < 0) {
                    return 0;
                }
            }
            double result = 0;
            if (cellPci % MOD_3 == (ncellPci % MOD_3)) {
                result += config.getM3r();
            }
            if (cellPci % MOD_6 == (ncellPci % MOD_6)) {
                result += config.getM6r();
            }
            if (cellPci % MOD_30 == (ncellPci % MOD_30)) {
                result += config.getM30r();
            }
            return result;
        }

        private double getRelaVal(String cellId, String ncellId) {
            return cellToNcellRelevancy.getOrDefault(cellId, new HashMap<>(32)).getOrDefault(ncellId, 0.0);
        }

        List<String> createTopCellList() {
            return needAssignCellToInterVal.entrySet().parallelStream()
                    .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue())).limit(topSize)
                    .collect(ArrayList::new, (l, p) -> l.add(p.getKey()), List::addAll);
        }

        @SuppressWarnings("unchecked")
        @Override
        public PlanObj clone() {
            PlanObj res = null;
            try {
                res = (PlanObj) super.clone();
                res.cellToPci = (HashMap<String, Integer>) cellToPci.clone();
                res.pciToCell = (HashMap<Integer, List<String>>) pciToCell.clone();
                res.cellToEarfcn = (HashMap<String, Integer>) cellToEarfcn.clone();
                res.earfcnToCell = (HashMap<Integer, List<String>>) earfcnToCell.clone();
                res.cellToInterVal = new ConcurrentHashMap<>(cellToInterVal);
                res.needAssignCellToInterVal = new ConcurrentHashMap<>(needAssignCellToInterVal);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return res;
        }
    }

    class AssignResult {
        private boolean updated;
        private List<String> assignCells;

        AssignResult(boolean updated, List<String> assignCells) {
            super();
            this.updated = updated;
            this.assignCells = assignCells;
        }

        boolean isUpdated() {
            return updated;
        }

        List<String> getAssignCells() {
            return assignCells;
        }
    }

    class SmallParameter {
        private final Map<String, List<String>> cellToNotSameStationCells = new HashMap<>();
        private final Map<String, Map<String, Double>> cellToNcellRelevancy = new HashMap<>();
        private final Map<String, List<String>> ncellToNotSameStationCells = new HashMap<>();
        private final Map<String, Map<String, Double>> ncellToCellRelevancy = new HashMap<>();
        private final Map<String, Integer> cellToPci = new HashMap<>();

        Map<String, List<String>> getCellToNotSameStationCells() {
            return cellToNotSameStationCells;
        }

        Map<String, Map<String, Double>> getCellToNcellRelevancy() {
            return cellToNcellRelevancy;
        }

        Map<String, List<String>> getNcellToNotSameStationCells() {
            return ncellToNotSameStationCells;
        }

        Map<String, Map<String, Double>> getNcellToCellRelevancy() {
            return ncellToCellRelevancy;
        }

        Map<String, Integer> getCellToPci() {
            return cellToPci;
        }
    }
}