package com.hgicreate.rno.lte.pciafp.task;

import com.hgicreate.rno.lte.pciafp.model.*;
import com.hgicreate.rno.lte.pciafp.service.CommonRestService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpDataService;
import com.hgicreate.rno.lte.pciafp.service.PciAfpRestService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
strictfp class PciAfpCalcConfig {
    private static final int SAME_STATION_NCELL_SIZE = 5;

    /**
     * canAssignCells小于cellList一定比例时，对查询语句进行优化，提高效率。2%是个经验数据
     */
    private static final double QUERY_OPTIMIZATION_RATE = 0.02;

    private long jobId;

    private long cityId;

    private final CommonRestService commonRestService;

    private final PciAfpDataService pciAfpDataService;

    private final PciAfpRestService pciAfpRestService;

    private double m3r = 1;
    private double m6r = 0.8;
    private double m30r = 0.1;
    /**
     * top n%
     */
    private double topRate = 0.1;
    /**
     * 给定的干扰差值比例m
     */
    private double defInterRate = 0.05;
    /**
     * 给定的方差值
     */
    private double defVariance = 0.05;
    private int divideNumber = 10;
    /**
     * 路测修正值
     */
    private double dtKs = -1.0;

    /**
     * 评估方案，默认1
     */
    private String planType = "ONE";
    /**
     * 收敛方案，默认1
     */
    private String convergenceType = "ONE";
    /**
     * 邻区核查，默认进行
     */
    private boolean checkNCell;
    /**
     * 邻区核查方案输出，默认不输出
     */
    private boolean exportNcCheckPlan;

    /**
     * 保存返回信息
     */
    private String returnInfo = "";

    /**
     * 距离限制，单位米
     **/
    private double disLimit = 5000.0;

    /**
     * 变小区表
     **/
    private final List<String> need2AssignCells = new ArrayList<>();

    /**
     * 可以改变pci的小区表。
     * 包括变小区表所有小区；
     * 如果该小区的同站邻区不大于SAME_STATION_NCELL_SIZE个,则也包括所有同站邻区。
     **/
    private final List<String> canAssignCells = new ArrayList<>();

    /**
     * 参与计算的小区表。
     * 包括可以改变pci的小区表的所有小区；
     * 包括在干扰矩阵中与可以改变pci的小区表中小区成对的小区，当然该小区必须在小区列表中。
     **/
    private final List<String> relatedCalcCells = new ArrayList<>();

    /**
     * 变小区关联度从大到小排序的队列
     **/
    private final List<String> descNeed2AssignCells = new ArrayList<>();

    /**
     * TOP变PCI小区表大小
     **/
    private int topSize;

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
    private final List<String> cellList = new ArrayList<>();

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

    PciAfpCalcConfig(long jobId, CommonRestService commonRestService, PciAfpRestService pciAfpRestService, PciAfpDataService pciAfpDataService) {
        this.jobId = jobId;
        this.commonRestService = commonRestService;
        this.pciAfpRestService = pciAfpRestService;
        this.pciAfpDataService = pciAfpDataService;
    }

    boolean buildPciTaskConf() {
        log.debug("生成计算配置，jobId={}", jobId);
        Date startTime = new Date();
        String msg;

        // 通过 jobId 获取干扰矩阵计算记录信息(rno_lte_pci_job表），包括变小区的 CLOB 信息
        PciAfpTask pciAfpTask = pciAfpRestService.queryTaskRecordByJobId(jobId);

        if (pciAfpTask == null) {
            msg = "任务配置信息不存在";
            log.error(msg);
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "获取任务配置信息");
            commonRestService.endJob(jobId, JobParseStatus.Fail.toString());
            return false;
        }
        log.debug("taskInfo={}", pciAfpTask);
        Job job = pciAfpTask.getJob();

        String optimizeCells = pciAfpTask.getOptimizeCells();

        String[] cellArr;
        if (optimizeCells != null && !"".equals(optimizeCells.trim())) {
            cellArr = optimizeCells.split(",");
            if (cellArr.length == 0) {
                // 保存报告信息
                msg = "变PCI小区字符串逗号分割后的长度为０,不满足基本需求！";
                log.debug(msg);
                commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "变PCI小区字符串逗号分割后的长度为０,不满足基本需求！");
                commonRestService.endJob(job, JobParseStatus.Fail.toString());
                return false;
            }
        } else {
            // 保存报告信息
            msg = "变PCI小区字符串为NULL！";
            log.debug(msg);
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "变PCI小区字符串为NULL！");
            commonRestService.endJob(job, JobParseStatus.Fail.toString());
            return false;
        }
        // 去重
        this.need2AssignCells.addAll(new ArrayList<>(new HashSet<>(Arrays.asList(cellArr))));
        log.debug("需要分配的小区总数={}", need2AssignCells.size());

        this.cityId = pciAfpTask.getAreaId();

        if (!handleCellToParameter()) {
            msg = "该区域下的lte小区数据不存在";
            log.debug(msg);
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "通过城市ID获取小区工参");
            commonRestService.endJob(job, JobParseStatus.Fail.toString());
            return false;
        }

        if (!handleThresholds()) {
            msg = "该区域下的门限值不存在";
            log.debug(msg);
            commonRestService.addOneReport(jobId, startTime, msg, DataParseStatus.Fail.toString(), "通过任务ID获取任务门限");
            commonRestService.endJob(job, JobParseStatus.Fail.toString());
            return false;
        }

        this.planType = pciAfpTask.getPlanType();
        this.convergenceType = pciAfpTask.getConvergenceType();
        this.checkNCell = pciAfpTask.isCheckNCell();
        this.exportNcCheckPlan = pciAfpTask.isExportNcCheckPlan();

        // 获取修正值
        this.dtKs = pciAfpTask.getKs() > 0 ? pciAfpTask.getKs() : -1;

        return true;
    }

    private boolean handleCellToParameter() {
        log.debug("获取小区工参。cityId={}", cityId);

        // 因为要做邻区优化的缘故，工参数据无法进行降维。
        this.cellToOriPci = new HashMap<>(5000);
        this.cellToLonLat = new HashMap<>(5000);
        this.cellToEarfcn = new HashMap<>(5000);
        this.enodebToCells = new HashMap<>(1000);
        this.cell2Enodeb = new HashMap<>(5000);

        Map<String, Cell> lteCells = pciAfpDataService.getLteCellMapByCityId(cityId);
        if (lteCells == null || lteCells.isEmpty()) {
            return false;
        }
        String cid, eid;
        List<String> cells;
        for (Cell cell : lteCells.values()) {
            try {
                cid = cell.getId();
                if (!cellList.contains(cid)) {
                    eid = cell.getEnodebId();
                    cellList.add(cid);
                    cellToOriPci.put(cid, cell.getPci());
                    cellToLonLat.put(cid, new double[]{cell.getLongitude(), cell.getLatitude()});
                    cellToEarfcn.put(cid, cell.getEarfcn());
                    cell2Enodeb.put(cid, eid);
                    cells = enodebToCells.computeIfAbsent(eid, k -> new ArrayList<>());
                    if (!cells.contains(cid)) {
                        cells.add(cid);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.debug("小区总数={}", cellList.size());
        return true;
    }

    private boolean handleThresholds() {
        log.debug("处理门限值。");
        // 获取页面自定义的阈值门限值
        List<Threshold> thresholds = new ArrayList<>();
        List<PciAfpParam> rawData = pciAfpRestService.queryParamInfo(jobId);

        if (CollectionUtils.isEmpty(rawData)) {
            // 取默认门限值
            thresholds = pciAfpRestService.getThresholdsByModuleType("LTEINTERFERCALC");
        } else {
            for (PciAfpParam map : rawData) {
                Threshold threshold = new Threshold();
                threshold.setCode(map.getParamCode());
                threshold.setDefaultVal(map.getParamVal());
                thresholds.add(threshold);
            }
        }

        for (Threshold threshold : thresholds) {
            String code = threshold.getCode();
            String val = threshold.getDefaultVal();
            if (code.equalsIgnoreCase("CELLM3RINTERFERCOEF".toUpperCase())) {
                this.m3r = Double.parseDouble(val);
            }
            if (code.equalsIgnoreCase("CELLM6RINTERFERCOEF".toUpperCase())) {
                this.m6r = Double.parseDouble(val);
            }
            if (code.equalsIgnoreCase("CELLM30RINTERFERCOEF".toUpperCase())) {
                this.m30r = Double.parseDouble(val);
            }
            if (code.equalsIgnoreCase("TOPNCELLLIST".toUpperCase())) {
                this.topRate = Double.parseDouble(val) * 0.01;
            }
            if (code.equalsIgnoreCase("CONVERMETHOD1TARGETVAL".toUpperCase())) {
                this.defInterRate = Double.parseDouble(val) * 0.01;
            }
            if (code.equalsIgnoreCase("CONVERMETHOD2TARGETVAL".toUpperCase())) {
                this.defVariance = Double.parseDouble(val) * 0.01;
            }
            if (code.equalsIgnoreCase("CONVERMETHOD2SCOREN".toUpperCase())) {
                this.divideNumber = Integer.parseInt(val);
            }
            if (code.equalsIgnoreCase("DISLIMIT".toUpperCase())) {
                this.disLimit = Double.parseDouble(val);
            }
        }
        return true;
    }

    /**
     * 从本地文件或者hive仓库中读取数据
     */
    boolean readData() {

        // 处理同站小区
        final Set<String> canAssignCellSet = new HashSet<>();
        cellList.stream().filter(need2AssignCells::contains).forEach(c -> {
            canAssignCellSet.add(c);
            List<String> sameStationCells = cellToSameStationCells.computeIfAbsent(c, e -> enodebToCells.get(cell2Enodeb.get(e)).stream()
                    .filter(b -> !b.equals(e) && Objects.equals(cellToEarfcn.get(e), cellToEarfcn.get(b))).collect(Collectors.toList()));
            if (sameStationCells.size() <= SAME_STATION_NCELL_SIZE) {
                canAssignCellSet.addAll(sameStationCells);
            }
        });
        canAssignCells.addAll(canAssignCellSet);

        // 如果可以改变PCI小区表数目小于查询优化值，对获取干扰矩阵的方式进行优化，减少运算数据量，加快方案运行速度。
        if (canAssignCells.size() < QUERY_OPTIMIZATION_RATE * cellList.size()) {
            // 根据可以改变PCI小区表减少cellToSameStationCells数据量
            cellList.stream().filter(canAssignCells::contains).forEach(c -> cellToSameStationCells.computeIfAbsent(c, e -> enodebToCells.get(cell2Enodeb.get(e)).stream()
                    .filter(b -> !b.equals(e) && Objects.equals(cellToEarfcn.get(e), cellToEarfcn.get(b))).collect(Collectors.toList())));

            log.debug("查询干扰矩阵数据开始。cityId={},cells={}", cityId, canAssignCells);
            Instant now = Instant.now();
            List<InterMatrix> matrixList = pciAfpDataService.getLatestMatrixByCityIdAndCells(cityId, canAssignCells);
//            List<InterMatrix> matrixList = pciAfpDataService.getLatestMatrixByCityId(cityId);
            log.debug("查询干扰矩阵数据完成。duration={}", Duration.between(now, Instant.now()));

            log.debug("解析干扰矩阵数据开始。");
            now = Instant.now();
            if (null != matrixList && matrixList.size() > 0) {
                log.debug("过滤干扰矩阵开始。matrixList.size={}", matrixList.size());
                Instant now1 = Instant.now();
                // 过滤干扰矩阵
                matrixList = matrixList.parallelStream()
                        .filter(e -> cellList.contains(e.getCellId()) && cellList.contains(e.getNcellId()))
                        .collect(Collectors.toList());
                log.debug("过滤干扰矩阵完成。matrixList.size={},duration={}", matrixList.size(), Duration.between(now1, Instant.now()));

                log.debug("获取相关小区开始。");
                now1 = Instant.now();
                // 获取相关小区
                List<String> relatedCalcCellList = matrixList.parallelStream()
                        .filter(e -> canAssignCells.contains(e.getCellId()) || canAssignCells.contains(e.getNcellId()))
                        .map(e -> new ArrayList<String>() {{
                            add(e.getCellId());
                            add(e.getNcellId());
                        }}).flatMap(Collection::stream).distinct().collect(Collectors.toList());
                relatedCalcCells.addAll(relatedCalcCellList);
                log.debug("获取相关小区完成。duration={}", Duration.between(now1, Instant.now()));

                log.debug("封装数据开始。");
                now1 = Instant.now();
                // 封装数据
                matrixList.forEach(m -> {
                    if (relatedCalcCells.contains(m.getCellId())) {
                        cellToNcellRelevancy.computeIfAbsent(m.getCellId(), e -> new HashMap<>(32)).put(m.getNcellId().intern(), m.getRelaVal());
                    }
                    if (relatedCalcCells.contains(m.getNcellId())) {
                        ncellToCellRelevancy.computeIfAbsent(m.getNcellId(), e -> new HashMap<>(32)).put(m.getCellId().intern(), m.getRelaVal());
                    }
                });
                log.debug("封装数据完成。duration={}", Duration.between(now1, Instant.now()));
            }
            log.debug("解析干扰矩阵数据完成。duration={}", Duration.between(now, Instant.now()));
        }
        // 需要调整pci的小区数据太多，视为全网调整。采用完整数据,效率较低。
        else {
            // 补全完整的cellToSameStationCells数据量
            cellList.forEach(c -> cellToSameStationCells.computeIfAbsent(c, e -> enodebToCells.get(cell2Enodeb.get(e)).stream()
                    .filter(b -> !b.equals(e) && Objects.equals(cellToEarfcn.get(e), cellToEarfcn.get(b))).collect(Collectors.toList())));

            log.debug("查询干扰矩阵数据开始。cityId={}", cityId);
            Instant now = Instant.now();
            List<InterMatrix> matrixList = pciAfpDataService.getLatestMatrixByCityId(cityId);
            Duration duration = Duration.between(now, Instant.now());
            log.debug("查询干扰矩阵数据完成。duration={}", duration.toMillis());

            log.debug("解析干扰矩阵数据开始。");
            now = Instant.now();
            if (null != matrixList && matrixList.size() > 0) {
                // 过滤工参不存在的小区
                matrixList = matrixList.parallelStream()
                        .filter(e -> cellList.contains(e.getCellId()) && cellList.contains(e.getNcellId()))
                        .collect(Collectors.toList());

                matrixList.forEach(m -> {
                    cellToNcellRelevancy.computeIfAbsent(m.getCellId(), e -> new HashMap<>(32)).put(m.getNcellId().intern(), m.getRelaVal());
                    ncellToCellRelevancy.computeIfAbsent(m.getNcellId(), e -> new HashMap<>(32)).put(m.getCellId().intern(), m.getRelaVal());
                });
            }
            duration = Duration.between(now, Instant.now());
            log.debug("解析干扰矩阵数据完成。duration={}", duration.toMillis());
        }

        if (!cellToNcellRelevancy.isEmpty()) {

            cellToNcellRelevancy.forEach((key, value) -> cellToTotalRelevancy.put(key, value.values()
                    .stream().reduce(0d, Double::sum)));
            cellToTotalRelevancy = sortMapByValue(cellToTotalRelevancy);

            // 获取变小区关联度从大到小排序的队列（这里可以排除没有测量数据的变小区）
            descNeed2AssignCells.addAll(cellToTotalRelevancy.keySet().stream()
                    .filter(need2AssignCells::contains).collect(Collectors.toList()));

            // top小区数量
            topSize = (int) (descNeed2AssignCells.size() * topRate);

            // 邻区关联度从大到小排序
            reverseMapping(cellToNcellRelevancy, cellToNotSameStationCells);
            // 求以邻区为key的反向映射
            reverseMapping(ncellToCellRelevancy, ncellToNotSameStationCells);

            log.debug("小区工参：");
            log.debug("小区总数={}", cellList.size());
            log.debug("cellToOriPci.size()=" + cellToOriPci.size());
            log.debug("cellToLonLat.size()=" + cellToLonLat.size());
            log.debug("cellToEarfcn.size()=" + cellToEarfcn.size());
            log.debug("待处理小区：");
            log.debug("变pci小区总数={}", need2AssignCells.size());
            log.debug("排序变pci小区总数={}", descNeed2AssignCells.size());
            log.debug("可变pci小区总数={}", canAssignCells.size());
            log.debug("参与计算小区总数={}", relatedCalcCells.size());
            log.debug("top小区数量={}", topSize);
            log.debug("干扰数据统计：");
            log.debug("主小区：");
            log.debug("cellToNcellRelevancy.size()=" + cellToNcellRelevancy.size());
            log.debug("cellToTotalRelevancy.size()=" + cellToTotalRelevancy.size());
            log.debug("cellToSameStationCells.size()=" + cellToSameStationCells.size());
            log.debug("cellToNotSameStationCells.size()=" + cellToNotSameStationCells.size());
            log.debug("邻区：");
            log.debug("ncellToCellRelevancy.size()=" + ncellToCellRelevancy.size());
            log.debug("ncellToNotSameStationCells.size()=" + ncellToNotSameStationCells.size());
        }
        log.debug("读取干扰矩阵数据完成。");
        return true;
    }

    private void reverseMapping(Map<String, Map<String, Double>> cellToNcellRelevancy, Map<String, List<String>> cellToNotSameStationCells) {
        List<String> sstCells;
        List<String> nsstCells;
        // 邻区关联度从大到小排序
        for (String cell : cellToNcellRelevancy.keySet()) {
            cellToNcellRelevancy.put(cell.intern(), sortMapByValue(cellToNcellRelevancy.get(cell)));

            sstCells = cellToSameStationCells.get(cell);
            if (sstCells == null) {
                sstCells = new ArrayList<>();
            }

            if (!cellToNotSameStationCells.containsKey(cell)) {
                nsstCells = new ArrayList<>();
                for (String ncell : cellToNcellRelevancy.get(cell).keySet()) {
                    if (!sstCells.contains(ncell) && Objects.equals(cellToEarfcn.get(cell), cellToEarfcn.get(ncell))) {
                        nsstCells.add(ncell.intern());
                    }
                }
                cellToNotSameStationCells.put(cell.intern(), nsstCells);
            }
        }
    }

    private Map<String, Double> sortMapByValue(Map<String, Double> unSortMap) {
        return unSortMap.entrySet().parallelStream().sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .collect(LinkedHashMap::new, (m, p) -> m.put(p.getKey(), p.getValue()), Map::putAll);
    }
}
