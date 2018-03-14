package com.iscreate.rno.microservice.pci.afp.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iscreate.rno.microservice.pci.afp.mapper.oracle.OracleMapper;

public strictfp class NewPciCalc {
	private final static Logger log = LoggerFactory.getLogger(NewPciCalc.class);
	// 地球的平均半径，单位为米
	private final static double R = 6371000;
	
	private OracleMapper oracleMapper;
	
	private List<Map<String, Object>> dataList = null;
	
	private Map<String, Object> dataMap = null;

	// 方案优化结束的方式
	private enum PLAN_FINISH_WAY {
		PLAN_TYPE_ONE, PLAN_TYPE_TWO, SCHEME_TYPE_ONE, SCHEME_TYPE_TWO
	};

	// 以何种方案结束，用于返回信息。
	private PLAN_FINISH_WAY planFinishWay;

	// 频率调整类型
	private enum D1D2_FREQ_ADJUST_TYPE {
		ROAD_SAMEFREQ_NETWORKCONSTRUC, ROAD_INTERVAL_NETWORKCONSTRUC
	};

	// 扫频计算类型
	private D1D2_FREQ_ADJUST_TYPE d1D2FreqAdjustType;

	// 配置
	private NewPciConfig config = null;

	// PCI分组工具
	private PciUtil pciUtil = new PciUtil();

	/** 当前正在计算的方案 **/
	private PlanObj bestPlan = new PlanObj();

	/** pci分配的类型 **/
	private String pciAssginType = "GROUP";
	// private String pciAssginType = "CELL";

	/**
	 * 小区与同站其他小区列的映射，同站其他小区已按关联度从大到小排列 <br>
	 * 比如key为1 <br>
	 * 干扰矩阵为 <br>
	 * 1->2 <br>
	 * 1->3 <br>
	 * 同站
	 **/
	private Map<String, List<String>> cellToSameStationOtherCells;

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

	/** 小区与邻区关联度的映射（包含了同站其他小区） **/
	private Map<String, Map<String, Double>> cellToNcellAssocDegree;

	/** 小区与邻区关联度的映射(以邻区为树根) **/
	private Map<String, Map<String, Double>> ncellToCellAssocDegree;

	/** 小区与小区总关联度的映射,以按总关联度从大到小排序 **/
	private Map<String, Double> cellToTotalAssocDegree;

	/** 需要优化的小区表 **/
	private List<String> cellsNeedtoAssignList;

	/** 变小区关联度从大到小排序的队列 **/
	private List<String> descNeedToAssignCellList = new ArrayList<String>();

	/** TOP变PCI小区表大小 **/
	private int topSize = 0;

	/** 小区与原PCI的映射 **/
	private Map<String, Integer> cellToOriPci;

	/** 小区到经纬度的映射，不重复 **/
	private Map<String, double[]> cellToLonLat;

	/** 小区与频率的映射 **/
	private Map<String, Integer> cellToOriEarfcn;

	/** 小区与原始总干扰值的映射 **/
	private Map<String, Double> cellToOriInterVal = new HashMap<String, Double>();

	/** 对 PCI 的分配次数进行记录，用于干扰值相同时 PCI 的判断依据。 **/
	private Map<Integer, Integer> pciToAllocAmount = new HashMap<Integer, Integer>();

	/** pci+-30循环列表 **/
	private Map<Integer, List<Integer>> pciToAlter30List = new HashMap<Integer, List<Integer>>();

	/** 小区到KS的映射 **/
	private Map<String, Double> cellToKs = new HashMap<String, Double>();

	/** 关联度和PCI的映射，用于分组分配PCI的方式，ConcurrentHashMap在多线程下拥有良好的性能 **/
	private final Map<Double, List<Integer>> interValToPcis = new ConcurrentHashMap<Double, List<Integer>>();

	/** ab小区列表 **/
	private List<String> abCellList;
	/** c小区列表 **/
	private List<String> cCellList = new ArrayList<String>();
	/** d1小区列表 **/
	private ArrayList<String> d1CellList = new ArrayList<String>();
	/** d2小区列表 **/
	private ArrayList<String> d2CellList = new ArrayList<String>();

	private Map<String, List<String>> enodebToCells;

	private Map<String, String> cell2Enodeb;

	/** d1频频点 **/
	private int d1Freq = 0;
	/** d2频频点 **/
	private int d2Freq = 0;

	// 收敛方案最终的差值比例或增均方差，用于返回信息
	private double lastInterRate = 0.0;
	private double lastVariance = 0.0;
	/** 方案生成次数 **/
	private int planCreateTimes = 0;
	/** 方案替换次数 **/
	private int planChangeTimes = 0;
	/** toplist生成次数 **/
	private int generateToplistTimes = 0;
	/** toplist小区变化次数 **/
	private int toplistCellChangeTimes = 0;
	/** 中间方案个数 **/
	private int midPlanNum = 0;
	/** 是否同pci出现的距离太近 **/
	private boolean isSamePciTooClose = false;

	public NewPciCalc(NewPciConfig config, OracleMapper oracleMapper) {
		log.debug("正在初始化计算类。。。");
		this.config = config;
		this.oracleMapper = oracleMapper;

		this.cellsNeedtoAssignList = config.getCellsNeedtoAssignList();
		this.cellToNcellAssocDegree = config.getCellToNcellAssocDegree();
		this.ncellToCellAssocDegree = config.getNcellToCellAssocDegree();
		this.cellToTotalAssocDegree = config.getCellToTotalAssocDegree();
		log.debug("主小区数量=" + cellToTotalAssocDegree.size());
		if (config.isExportAssoTable()) {
			saveAssoTable(cellToTotalAssocDegree);
		}
		// 获取变小区关联度从大到小排序的队列（这里可以排除没有测量数据的变小区）
		for (String cellId : cellToTotalAssocDegree.keySet()) {
			if (cellsNeedtoAssignList.contains(cellId)) {
				descNeedToAssignCellList.add(cellId.intern());
			}
		}
		log.debug("变小区数量=" + descNeedToAssignCellList.size());
		// top小区数量
		topSize = (int) (descNeedToAssignCellList.size() * config.getTopRate());

		this.cellToSameStationOtherCells = config.getCellToSameStationOtherCells();
		this.cellToNotSameStationCells = config.getCellToNotSameStationCells();
		this.ncellToNotSameStationCells = config.getNcellToNotSameStationCells();
		this.cellToOriPci = config.getCellToOriPci();
		this.cellToOriEarfcn = config.getCellToEarfcn();
		this.cellToLonLat = config.getCellToLonLat();
		this.cellToKs = config.getCellToKs();
		this.enodebToCells = config.getEnodebToCells();
		this.cell2Enodeb = config.getCell2Enodeb();
		if ("ROAD_SAMEFREQ_NETWORKCONSTRUC".equals(config.getD1D2Type())) {
			this.d1D2FreqAdjustType = D1D2_FREQ_ADJUST_TYPE.ROAD_SAMEFREQ_NETWORKCONSTRUC;
		} else if ("ROAD_INTERVAL_NETWORKCONSTRUC".equals(config.getD1D2Type())) {
			this.d1D2FreqAdjustType = D1D2_FREQ_ADJUST_TYPE.ROAD_INTERVAL_NETWORKCONSTRUC;
		}
		// d1d2频率调整类型不为空，进行D1D2频调整
		if (d1D2FreqAdjustType != null) {
			// ab小区
			abCellList = config.getAbCellList();
			if (abCellList.isEmpty()) {
				d1D2FreqAdjustType = null;
				saveD1D2List(bestPlan.getD1CellList(), bestPlan, 1);
				saveD1D2List(bestPlan.getD2CellList(), bestPlan, 2);
			} else {
				d1Freq = Integer.parseInt(config.getD1Freq());
				d2Freq = Integer.parseInt(config.getD2Freq());
				// 将ab小区加入到D1列表
				d1CellList.addAll(abCellList);
				// 将d1频关联度不为空的变小区加入到d1列表
				for (String cellId : cellsNeedtoAssignList) {
					if (cellToOriEarfcn.containsKey(cellId) && cellToOriEarfcn.get(cellId) == d1Freq
							&& cellToNcellAssocDegree.containsKey(cellId)) {
						cCellList.add(cellId);
						d1CellList.add(cellId);
					}
				}
				bestPlan.setD1CellList(d1CellList);
				bestPlan.setD2CellList(d2CellList);
				for (String cellId : abCellList) {
					bestPlan.addCellToEarfcn(cellId, cellToOriEarfcn.get(cellId));
				}
			}
		}
		// 初始化 PCI 分配次数，初始值都为0
		for (int i = 0; i <= 503; i++) {
			pciToAllocAmount.put(i, 0);
		}
		for (int i = 0; i < 30; i++) {
			List<Integer> list = new ArrayList<Integer>();
			for (int j = i; j < 504; j += 30) {
				list.add(j);
			}
			pciToAlter30List.put(i, list);
		}
		for (int i = 30; i < 504; i++) {
			pciToAlter30List.put(i, pciToAlter30List.get(i % 30));
		}
		log.debug("初始化计算类完成。");
	}

	/**
	 * PCI计算
	 * 
	 * @param jobWorker
	 * @throws Exception
	 * @author chen.c10
	 * @date 2016年5月27日
	 * @version RNO 3.0.1
	 */
	public void execCalc() throws Exception {
		log.debug("计算开始。。。");
		// 在原始方案中先为所有主小区分配原始PCI或earfcn
		assignOriPciAndEarfcnByDescCells(bestPlan);
		// 计算原始方案的每小区干扰值
		bestPlan.calInterVal(cellToNotSameStationCells);
		// 缓存原始方案
		cellToOriInterVal.putAll(bestPlan.getCellToInterVal());
		// 计算原始方案总干拢值
		double minTotalInterVal = bestPlan.getTotalInterVal();
		log.debug("原始方案总干拢值：minTotalInterVal=" + minTotalInterVal);

		// 创建新的方案
		PlanObj plan = bestPlan.clone();
		// 首次变小区PCI分配，按变小区关联度大小顺序分配，生成首套方案。
		assignPciToCellsPlan(plan, descNeedToAssignCellList);
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

		if (config.getSchemeType().equals("ONE")) {
			planFinishWay = PLAN_FINISH_WAY.SCHEME_TYPE_ONE;
		} else {
			planFinishWay = PLAN_FINISH_WAY.SCHEME_TYPE_TWO;
		}

		List<String> minInterValCellList = new ArrayList<String>();
		// 进行D1D2调整
		if (d1D2FreqAdjustType != null) {
			d1D2FreqAdjust(d1D2FreqAdjustType, bestPlan, abCellList, cCellList);
		}
		// 准备分配的小区
		String assginCell;
		// top小区列表
		List<String> topCellList;
		// 新toplist
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
			// 如果toplist为空，退出
			if (topCellList.isEmpty()) {
				break;
			}
			// 累计toplist小区变化次数
			toplistCellChangeTimes += topIndex + 1;
			// 每次重新获取Top小区，序号归零
			topIndex = 0;
			// 变PCI小区表生成次数
			int topListTimes = 1;
			// 保存toplist 顺序
			minInterValCellList.clear();
			minInterValCellList.addAll(topCellList);
			if (config.isExportMidPlan()) {
				saveExportPlan(plan, topCellList, ++midPlanNum);
			}
			// 方案评估，总干拢值越小越好
			while (true) {
				log.debug("当前第<{}>套方案，生成第<{}>个 TOP List，使用第<{}>个TOP Cell。", planCreateTimes, topListTimes,
						topIndex + 1);
				// 记录需要调整的小区
				assginCell = topCellList.get(topIndex);
				// 重新分配 pci 给 topCellList 的小区
				if (assignPciToCellsPlanForOneCell(plan, assginCell).isUpdated()) {
					// 成功分配新pci，计算干扰质
					// 计算当前方案的每个小区对应的干扰值，只计算上次调整的小区和其相关小区
					plan.calInterVal(assginCell);
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
						// 新方案全网干拢值优于旧方案，替换旧方案
						// 最小总干扰值设置为当前的总干扰值
						minTotalInterVal = currentTotalInterVal;
						// 当前的最好方案
						bestPlan = plan.clone();
						// 方案替换次数累计
						planChangeTimes++;

						break;
					} else {
						if (config.getPlanType().equals("ONE")) {
							// 评估方案1
							// 产生新的 Top 小区列表（按干扰总值从大到小排序）
							newTopCellList = plan.createTopCellList();
							// 排名是否有变化
							if (isRankingChanged(topCellList, newTopCellList, topIndex, minTotalInterVal,
									lastTotalInterVal, currentTotalInterVal, minInterValCellList)) {
								// 总干扰值更差了，但最差的小区的提名提升了，用新列表，继续对当前位置小区进行优化。
								log.debug("排名有变化，topIndex={}, topCellList={}, newTopCellList={}", topIndex,
										topCellList.get(topIndex), newTopCellList.get(topIndex));
								// 用新toplist替换
								topCellList = newTopCellList;
								// 累加当前方案的TOP list生成次数。
								topListTimes++;
								// 累计toplist小区变化次数
								toplistCellChangeTimes += topIndex + 1;
								// 每次重新获取Top小区，序号归零
								topIndex = 0;
							} else {
								// 总干扰值更差了，并且最差的小区提名没有提升，用回原来列表，并对下一个位置小区进行优化。
								// 当 TOP 列表最后一个都优化后，优化计算结束。
								if (++topIndex >= topCellList.size()) {
									log.debug("排名无变化，TopList 已分配完，计算结束。");
									isCalFinish = true;
									planFinishWay = PLAN_FINISH_WAY.PLAN_TYPE_ONE;
									break;
								}
							}
						} else {
							// 评估方案2
							// 保持原方案，对 TOP2 小区进行优化
							if (++topIndex >= topCellList.size()) {
								log.debug("选用评估方案2，TopList 已分配完，计算结束。");
								isCalFinish = true;
								planFinishWay = PLAN_FINISH_WAY.PLAN_TYPE_TWO;
								break;
							}
						}
					}
				} else {
					// 未重新分配pci，说明当前pci的干扰值已经最小，继续分配下一个
					if (++topIndex >= topCellList.size()) {
						if (config.getPlanType().equals("ONE")) {
							log.debug("排名无变化，TopList 已分配完，计算结束。");
							planFinishWay = PLAN_FINISH_WAY.PLAN_TYPE_ONE;
						} else {
							log.debug("选用评估方案2，TopList 已分配完，计算结束。");
							planFinishWay = PLAN_FINISH_WAY.PLAN_TYPE_TWO;
						}
						isCalFinish = true;
						break;
					}
				}
			}
			// 进行D1D2调整
			if (d1D2FreqAdjustType != null) {
				d1D2FreqAdjust(d1D2FreqAdjustType, bestPlan, abCellList, cCellList);
			}
			// 累计TOP list 生成次数
			generateToplistTimes += topListTimes;
			if (isCalFinish)
				break;
		}
		// 第一次多加了1，现在减去
		if (toplistCellChangeTimes > 0) {
			toplistCellChangeTimes--;
		}
		// 保存d1d2列表
		if (d1D2FreqAdjustType != null) {
			saveD1D2List(bestPlan.getD1CellList(), bestPlan, 1);
			saveD1D2List(bestPlan.getD2CellList(), bestPlan, 2);
		}
		// 输出当前方案之前，以最佳方案进行邻区核查。
		if (config.isCheckNCell()) {
			checkNCellPci(bestPlan, descNeedToAssignCellList);
		}
		if (config.isExportNcCheckPlan()) {
			saveNcCheckPlan(bestPlan);
		}
		// 输出当前方案之前，以最佳方案进行同频同PCI核查。
		checkNeighborAreaPci(bestPlan, descNeedToAssignCellList);
		// 保存最终方案
		saveBestPlan(bestPlan);
		// 保存返回信息
		config.setReturnInfo(getReturnInfo());
		log.debug("计算完成！");
	}

	/**
	 * 调整D1D2频
	 * 
	 * @param d1d2FreqAdjustType
	 * @param plan
	 * @param d1CellList
	 * @param d2CellList
	 * @author chen.c10
	 * @date 2016年3月25日
	 * @version RNO 3.0.1
	 */
	private void d1D2FreqAdjust(D1D2_FREQ_ADJUST_TYPE d1d2FreqAdjustType, PlanObj plan, List<String> abCellList,
			List<String> cCellList) {
		log.debug("进入：d1D2FreqAdjust。d1CellList=" + d1CellList + ", d2CellList=" + d2CellList + ", cCellList="
				+ cCellList + ", abCellList=" + abCellList);
		List<String> d1CellList = plan.getD1CellList();
		List<String> d2CellList = plan.getD2CellList();
		// 调整D1，D2的频点
		switch (d1d2FreqAdjustType) {
		case ROAD_SAMEFREQ_NETWORKCONSTRUC:
			assignEarfcnByDtCellsForSameFreq(plan, abCellList, cCellList, d1CellList, d2CellList);
			break;
		case ROAD_INTERVAL_NETWORKCONSTRUC:
			assignEarfcnByDtCellsForInterval(plan, abCellList, cCellList, d1CellList, d2CellList);
			break;
		default:
			break;
		}
		double d1TotalInterVal = 0, d2TotalInterVal = 0;
		int cnt = 0, outCnt = 0, totalInCnt2 = 0;
		String lastCell = "";
		do {
			if (cnt++ > 0) {
				adjustD1TopInterValCellToD2(plan, lastCell, abCellList, cCellList, d1CellList, d2CellList);
				if (lastCell.isEmpty()) {
					break;
				}
			}
			// 改变D1,D2的邻区
			adjustD1D2CellToNcellsRel(plan, d1CellList, d2CellList);
			// 给D1D 2频分配pci
			assignPciToD1D2CellsPlan(plan, d1CellList, d2CellList);
			d1TotalInterVal = calD1D2CellsTotalInterVal(plan, d1CellList);
			d2TotalInterVal = calD1D2CellsTotalInterVal(plan, d2CellList);
			log.debug("step1 d1TotalInterVal={},d2TotalInterVal={}", d1TotalInterVal, d2TotalInterVal);
		} while (d1TotalInterVal > d2TotalInterVal);
		// 为true时结束循环，输出最终结果
		boolean forward = false;
		List<String> assignedCells = new ArrayList<String>();
		while (!forward) {
			if (++outCnt % 10 == 0) {
				log.debug("outCnt已经进行 {} 次循环", outCnt);
			}
			double p1TotalInterVal = d1TotalInterVal + d2TotalInterVal;
			double p2TotalInterVal = 0;
			log.debug("d2CellList={}", d2CellList);
			// d2干扰值列表
			Map<String, Double> oriD2CellsToInterVal = calD1D2CellsInterVal(plan, d2CellList);
			log.debug("oriD2CellsToInterVal={}", oriD2CellsToInterVal);
			if (assignedCells.size() == 3) {
				assignedCells.set(2, "");
			}
			// 结果输出点1. 当D2小区不可继续分配时，满足结束条件，跳出外层循环，输出最终结果
			if (!isCellsCanAssign(d2CellList)) {
				break;
			}
			int inCnt2 = 0;
			do {
				if (++inCnt2 % 10 == 0) {
					log.debug("inCnt2已经进行" + inCnt2 + "次循环");
				}
				// 结果输出点2. 当D2小区不可继续分配时，满足结束条件，跳出内层循环，并将外层循环结束条件置为true
				adjustD2TopInterValCellToD1(plan, d1CellList, d2CellList, oriD2CellsToInterVal, d1Freq, d2Freq,
						assignedCells);
				if (assignedCells.get(2).isEmpty()) {
					forward = true;
					break;
				}
				// 改变D1,D2的邻区
				adjustCellToNcellsRel(plan, assignedCells.subList(1, 3));
				// 给D1D 2频分配pci
				assignPciToD1D2CellsPlan(plan, d1CellList, d2CellList);
				// D1 D2 list在计算干扰值的同时，会重新按干扰值降序排序，下次循环将不用再排序
				d1TotalInterVal = calD1D2CellsTotalInterVal(plan, d1CellList);
				d2TotalInterVal = calD1D2CellsTotalInterVal(plan, d2CellList);
				p2TotalInterVal = d1TotalInterVal + d2TotalInterVal;
				log.debug("step2 d1TotalInterVal=" + d1TotalInterVal + ",d2TotalInterVal=" + d2TotalInterVal);
				log.debug("step2 p1TotalInterVal=" + p1TotalInterVal + ",p2TotalInterVal=" + p2TotalInterVal);
			} while (p1TotalInterVal <= p2TotalInterVal && d1TotalInterVal >= d2TotalInterVal);
			totalInCnt2 += inCnt2;
		}
		log.debug("outCnt=" + outCnt);
		log.debug("totalInCnt2=" + totalInCnt2);
		log.debug("退出：d1D2FreqAdjust。");
	}

	/**
	 * 小区是否可以分配,可分配为true,不可分配为false
	 * 
	 * @author chen.c10
	 * @date 2016年3月26日
	 * @version RNO 3.0.1
	 */
	private boolean isCellsCanAssign(List<String> cellList) {
		if (cellList.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 为D1D2频分配PCI
	 * 
	 * @param bestPlan
	 * @param d1CellList
	 * @param d2CellList
	 * @author chen.c10
	 * @date 2016年3月25日
	 * @version RNO 3.0.1
	 */
	private void assignPciToD1D2CellsPlan(PlanObj bestPlan, List<String> d1CellList, List<String> d2CellList) {
		log.debug("进入：assignPciToD1D2CellsPlan。");
		// 给D1频分配pci
		assignPciToCellsPlan(bestPlan, d1CellList);
		// 给D2频分配pci
		assignPciToCellsPlan(bestPlan, d2CellList);
		log.debug("退出：assignPciToD1D2CellsPlan。");
	}

	/**
	 * 将不符合要求的D1频调整到D2频
	 * 
	 * @param plan
	 * @param d1CellList
	 * @param d2CellList
	 * @author chen.c10
	 * @date 2016年3月25日
	 * @version RNO 3.0.1
	 */
	private void assignEarfcnByDtCellsForSameFreq(PlanObj plan, List<String> abCellList, List<String> cCellList,
			List<String> d1CellList, List<String> d2CellList) {
		// log.debug("进入：assignEarfcnByDtCellsForSameFreq。plan="+plan+",d1CellList="+d1CellList+",d2CellList="+d2CellList);
		log.debug("进入：assignEarfcnByDtCellsForSameFreq。");
		for (String scell : new ArrayList<String>(cCellList)) {
			for (String abCell : abCellList) {
				if (plan.getPciByCell(scell) % 3 == plan.getPciByCell(abCell) % 3
						&& plan.getInterValByCell(scell) > 0.05) {
					d2CellList.add(scell);
					d1CellList.remove(scell);
					cCellList.remove(scell);
					plan.addCellToEarfcn(scell, d2Freq);
					break;
				}
			}
		}
		log.debug("退出：assignEarfcnByDtCellsForSameFreq。");
	}

	/**
	 * 将不符合要求的D1频调整到D2频
	 * 
	 * @param plan
	 * @param d1CellList
	 * @param d2CellList
	 * @author chen.c10
	 * @date 2016年3月25日
	 * @version RNO 3.0.1
	 */
	private void assignEarfcnByDtCellsForInterval(PlanObj plan, List<String> abCellList, List<String> cCellList,
			List<String> d1CellList, List<String> d2CellList) {
		// log.debug("进入：assignEarfcnByDtCellsForInterval。plan="+plan+",d1CellList="+d1CellList+",d2CellList="+d2CellList);
		log.debug("进入：assignEarfcnByDtCellsForInterval。");
		double maxInterVal = -1.0;
		String maxCell = "";
		for (String scell : d1CellList) {
			double interVal = plan.getInterValByCell(scell);
			if (maxInterVal < interVal) {
				maxInterVal = interVal;
				maxCell = scell;
			}
		}
		if (!maxCell.isEmpty()) {
			List<String> tmp = new ArrayList<String>();
			for (String scell : d1CellList) {
				if (scell.equals(maxCell)) {
					continue;
				}
				if (cellToNcellAssocDegree.get(scell) != null && cellToNcellAssocDegree.get(scell).get(maxCell) != null
						&& cellToNcellAssocDegree.get(scell).get(maxCell) < 0.05) {
					tmp.add(scell);
					break;
				}
				if (cellToNcellAssocDegree.get(maxCell) != null
						&& cellToNcellAssocDegree.get(maxCell).get(scell) != null
						&& cellToNcellAssocDegree.get(maxCell).get(scell) < 0.05) {
					tmp.add(scell);
				}
			}
			String secCell = "";
			double secInterVal = -1.0;
			for (String scell : tmp) {
				double interVal = plan.getInterValByCell(scell);
				if (secInterVal < interVal) {
					secInterVal = interVal;
					secCell = scell;
				}
			}
			d2CellList.add(maxCell);
			d1CellList.remove(maxCell);
			plan.addCellToEarfcn(maxCell, d2Freq);
			if (!secCell.isEmpty()) {
				d2CellList.add(secCell);
				d1CellList.remove(secCell);
				plan.addCellToEarfcn(secCell, d2Freq);
			}
		}
		log.debug("退出：assignEarfcnByDtCellsForInterval。");
	}

	/**
	 * 调整D1D2频后，调整小区与邻区的关系
	 * 
	 * @param d1CellList
	 * @param d2CellList
	 * @author chen.c10
	 * @date 2016年3月23日
	 * @version RNO 3.0.1
	 */
	private void adjustD1D2CellToNcellsRel(PlanObj plan, List<String> d1CellList, List<String> d2CellList) {
		log.debug("进入：adjustD1D2CellToNcellsRel。");
		// 调整D1
		adjustCellToNcellsRel(plan, d1CellList);
		// 调整D2
		adjustCellToNcellsRel(plan, d2CellList);
		log.debug("退出：adjustD1D2CellToNcellsRel。");
	}

	/**
	 * 调整D1D2后，调整小区与邻区的关系
	 * 
	 * @param cellList
	 * @author chen.c10
	 * @date 2016年3月28日
	 * @version RNO 3.0.1
	 */
	private void adjustCellToNcellsRel(PlanObj plan, List<String> cellList) {
		if (cellList != null) {
			for (String scell : cellList) {
				adjustCellToNcellsRel(plan, scell);
			}
		}
	}

	/**
	 * 调整小区与邻区的关系
	 * 
	 * @param scell
	 * @author chen.c10
	 * @date 2016年3月28日
	 * @version RNO 3.0.1
	 */
	private void adjustCellToNcellsRel(PlanObj plan, String scell) {
		List<String> sstCells = cellToSameStationOtherCells.get(scell);
		List<String> nsstCells = cellToNotSameStationCells.get(scell);
		Map<String, Double> assocDegree = cellToNcellAssocDegree.get(scell);
		// 从原来的同频小区中移除
		if (sstCells != null) {
			for (String nscell : new ArrayList<String>(sstCells)) {
				if (cellToSameStationOtherCells.get(nscell) != null) {
					cellToSameStationOtherCells.get(nscell).remove(scell);
				}
			}
			// 重新分配分同站邻区
			sstCells.clear();
			for (String cell : enodebToCells.get(cell2Enodeb.get(scell))) {
				if (!scell.equals(cell) && plan.getEarfcnByCell(scell) == plan.getEarfcnByCell(cell)) {
					sstCells.add(cell);
				}
			}
		}
		// 从原来的非同站邻区中删除
		if (nsstCells != null) {
			for (String nscell : new ArrayList<String>(nsstCells)) {
				if (cellToNotSameStationCells.get(nscell) != null) {
					cellToNotSameStationCells.get(nscell).remove(scell);
				}
			}
			// 重新分配分同站邻区
			nsstCells.clear();
			if (assocDegree != null) {
				for (String ncell : plan.getSameEarfcnCellsByCell(scell)) {
					if (!scell.equals(ncell) && assocDegree.containsKey(ncell) && !sstCells.contains(ncell)) {
						nsstCells.add(ncell);
					}
				}
			}
		}
		nsstCells = ncellToNotSameStationCells.get(scell);
		assocDegree = ncellToCellAssocDegree.get(scell);
		// 从原来的非同站小区中删除
		if (nsstCells != null) {
			for (String nscell : new ArrayList<String>(nsstCells)) {
				if (ncellToNotSameStationCells.get(nscell) != null) {
					ncellToNotSameStationCells.get(nscell).remove(scell);
				}
			}
			// 重新分配分同站邻区
			nsstCells.clear();
			if (assocDegree != null) {
				for (String ncell : plan.getSameEarfcnCellsByCell(scell)) {
					if (!scell.equals(ncell) && assocDegree.containsKey(ncell) && !sstCells.contains(ncell)) {
						nsstCells.add(ncell);
					}
				}
			}
		}
	}

	/**
	 * 调整D2最大干扰值到D1
	 * 
	 * @param d1CellList
	 * @param d2CellList
	 * @param d1Freq
	 * @param d2Freq
	 * @param assignedCells 上次分配的小区
	 * @return 本次分配的小区
	 * @author chen.c10
	 * @date 2016年3月28日
	 * @version RNO 3.0.1
	 * @param d2CellsToInterVal
	 */
	private void adjustD2TopInterValCellToD1(PlanObj plan, List<String> d1CellList, List<String> d2CellList,
			Map<String, Double> oriD2CellsToInterVal, int d1Freq, int d2Freq, List<String> assignedCells) {
		log.debug("进入：adjustD2TopInterValCellToD1。");
		// assignedCells
		// 0 干扰最高的D小区
		// 1 上次调整的小区
		// 2 本次调整的小区
		// System.out.println("in assignedCells=" + assignedCells);
		// 从D1中移除上次的小区
		String assignedCell = "";
		if (!d1CellList.isEmpty() && !d2CellList.isEmpty()) {
			if (assignedCells.isEmpty()) {
				assignedCell = d2CellList.get(0);
				d1CellList.add(assignedCell.intern());
				d2CellList.remove(assignedCell);
				plan.addCellToEarfcn(assignedCell, d1Freq);
				assignedCells.add(assignedCell.intern());
				assignedCells.add(assignedCell.intern());
			} else if (assignedCells.size() == 3) {
				if (assignedCells.get(2).isEmpty()) {
					assignedCells.remove(2);
					assignedCells.remove(0);
					assignedCell = assignedCells.get(0);
					assignedCells.add(assignedCell);
					for (String cell : oriD2CellsToInterVal.keySet()) {
						if (getMinAssocDegreeFrom2Cell(assignedCell, cell) < 0.05) {
							assignedCell = cell;
							break;
						}
					}
					if (!assignedCell.equals(assignedCells.get(0))) {
						d1CellList.add(assignedCell.intern());
						d2CellList.remove(assignedCell);
						plan.addCellToEarfcn(assignedCell.intern(), d1Freq);
					} else {
						assignedCell = "";
					}
				} else {
					assignedCell = assignedCells.get(2);
					d2CellList.add(assignedCell.intern());
					d1CellList.remove(assignedCell);
					plan.addCellToEarfcn(assignedCell.intern(), d2Freq);

					assignedCells.remove(1);
					assignedCell = assignedCells.get(1);
					// System.out.println("oriD2CellsToInterVal=" + oriD2CellsToInterVal);
					// System.out.println("assignedCells.get(1)=" + assignedCell);
					for (String cell : oriD2CellsToInterVal.keySet()) {
						// d2CellsToInterVal已经按降序排列，第一个小区为assignedCells[0],上次小区为assignedCells[1]
						if (assignedCell.equals(cell)) {
							// System.out.println("assignedCell=" + assignedCell);
							// System.out.println("cell=" + cell);
							assignedCell = assignedCells.get(0);
							continue;
						}
						if (assignedCells.get(0).equals(assignedCell)
								&& getMinAssocDegreeFrom2Cell(assignedCell, cell) < 0.05) {
							// System.out.println("cell=" + cell);
							assignedCell = cell;
							break;
						}
					}
					// System.out.println("assignedCells.get(0)=" + assignedCells.get(0));
					// System.out.println("assignedCell=" + assignedCell);
					if (!assignedCell.equals(assignedCells.get(0))) {
						d1CellList.add(assignedCell.intern());
						d2CellList.remove(assignedCell);
						plan.addCellToEarfcn(assignedCell.intern(), d1Freq);
					} else {
						assignedCell = "";
					}
				}
			}
		}
		// System.out.println("assignedCell=" + assignedCell);
		if (assignedCells.size() == 3) {
			assignedCells.remove(2);
		}
		assignedCells.add(assignedCell.intern());
		// System.out.println("out assignedCells=" + assignedCells);
		log.debug("退出：adjustD2TopInterValCellToD1。");
	}

	/**
	 * 获取两个小区间的较小关联度
	 * 
	 * @param cell1
	 * @param cell2
	 * @return
	 * @author chen.c10
	 * @date 2016年3月30日
	 * @version RNO 3.0.1
	 */
	private double getMinAssocDegreeFrom2Cell(String cell1, String cell2) {
		double assocDegree1 = 0;
		double assocDegree2 = 0;
		boolean nullFlag = false;
		Map<String, Double> map = cellToNcellAssocDegree.get(cell1);
		if (map != null && map.get(cell2) != null) {
			assocDegree1 = map.get(cell2);
		} else {
			nullFlag = true;
		}
		map = ncellToCellAssocDegree.get(cell1);
		if (map != null && map.get(cell2) != null) {
			assocDegree2 = map.get(cell2);
		} else {
			nullFlag = true;
		}
		return nullFlag ? 0 : Math.min(assocDegree1, assocDegree2);
	}

	/**
	 * 调整干扰值最大到另一个
	 * 
	 * @author chen.c10
	 * @date 2016年3月23日
	 * @version RNO 3.0.1
	 */
	private void adjustD1TopInterValCellToD2(PlanObj plan, String cellId, List<String> abCellList,
			List<String> cCellList, List<String> d1CellList, List<String> d2CellList) {
		log.debug("进入：adjustD1TopInterValCellToD2。");
		String cellIdTmp = cellId;
		if (!d1CellList.isEmpty()) {
			for (String scell : d1CellList) {
				if (cCellList.contains(scell)) {
					if (getMinAssocDegreeFrom2Cell(scell, cellId) < 0.05) {
						cellId = scell;
						break;
					}
				}
			}
			if (cellId.equals(cellIdTmp)) {
				cellId = "";
			}
			if (!cellId.isEmpty()) {
				d2CellList.add(cellId);
				d1CellList.remove(cellId);
				cCellList.remove(cellId);
				plan.addCellToEarfcn(cellId, d2Freq);
			}
		}
		log.debug("退出：adjustD1TopInterValCellToD2。");
	}

	/**
	 * 计算D1D2的总干扰值
	 * 
	 * @param plan
	 * @param cells
	 * @return
	 * @author chen.c10
	 * @date 2016年3月23日
	 * @version RNO 3.0.1
	 */
	private Map<String, Double> calD1D2CellsInterVal(PlanObj plan, List<String> cells) {
		Map<String, Double> cellToInterVal = new HashMap<String, Double>();
		if (cells != null) {
			// 计算
			for (String cellId : cells) {
				// 主小区的非同站小区列表
				plan.calInterVal(cellId);
				cellToInterVal.put(cellId.intern(), plan.getInterValByCell(cellId));
			}
			// 排序
			cellToInterVal = sortMapByValue(cellToInterVal);
			cells.clear();
			cells.addAll(cellToInterVal.keySet());
		}
		return cellToInterVal;
	}

	/**
	 * 计算总干扰值
	 * 
	 * @param plan
	 * @param cells
	 * @return
	 * @author chen.c10
	 * @date 2016年3月23日
	 * @version RNO 3.0.1
	 */
	private double calD1D2CellsTotalInterVal(PlanObj plan, List<String> cells) {
		// log.debug("进入：calD1D2CellsTotalInterVal。plan=" + plan + ",cells=" + cells);
		log.debug("进入：calD1D2CellsTotalInterVal。");
		double totalInterVal = 0;
		Map<String, Double> cellsToInterVal = calD1D2CellsInterVal(plan, cells);
		if (cellsToInterVal != null) {
			for (double interVal : cellsToInterVal.values()) {
				totalInterVal += interVal;
			}
		}
		// log.debug("退出：calD1D2CellsTotalInterVal。totalInterVal=" + totalInterVal);
		log.debug("退出：calD1D2CellsTotalInterVal。");
		return totalInterVal;
	}

	/**
	 * Top 小区列表排名是否有变化
	 * 
	 * @param topCellList 旧的 Top 小区列表
	 * @param newTopCellList 新的 Top 小区列表
	 * @param minInterValCellList
	 * @param newTotalInterVal
	 * @param minTotalInterVal
	 * @param currentTotalInterVal
	 * @return
	 */
	private static boolean isRankingChanged(List<String> topCellList, List<String> newTopCellList, int topIndex,
			double minTotalInterVal, double lastTotalInterVal, double currentTotalInterVal,
			List<String> minInterValCellList) {
		long t = System.currentTimeMillis();
		boolean flag = false;
		if (!topCellList.get(topIndex).equals(newTopCellList.get(topIndex))) {
			if (currentTotalInterVal == minTotalInterVal) {
				flag = !compareList(newTopCellList, minInterValCellList);
			} else if (currentTotalInterVal > lastTotalInterVal) {
				flag = true;
			}
		}
		log.debug("计算排名用时：{}", System.currentTimeMillis() - t);
		return flag;
	}

	/**
	 * 毕竟两个list的顺序是否相同
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 * @author chen.c10
	 * @date 2016年6月6日
	 * @version RNO 3.0.1
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
	 * 收敛判断
	 * 
	 * @return 是否停止计算
	 */
	private boolean isConvergence() {
		boolean bool = false;
		if (config.getSchemeType().equals("ONE")) {
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
				bool = true;
			}
		} else {
			double varianceByCurrentPlan = getVarianceByCurrentPlan();
			double defVariance = config.getDefVariance();
			lastVariance = varianceByCurrentPlan;
			// 收敛方法2: 均方差收敛算法 -- 全网以小区数均分为n份，求均方差，当值小于m
			if (log.isDebugEnabled()) {
				log.debug("全网小区干扰值方差=" + varianceByCurrentPlan + ", 给定值=" + defVariance);
			}
			if (varianceByCurrentPlan < defVariance) {
				if (log.isDebugEnabled()) {
					log.debug("【求全网小区干扰值方差，当值小于给定值m】，终止继续优化");
				}
				bool = true;
			}
		}
		return bool;
	}

	/**
	 * 返回任务信息
	 * 
	 * @param planFinishWay
	 * @param isPciClose
	 */
	public String getReturnInfo() {
		String resultInfo = "<br>";
		if (config.getPlanType().equals("ONE")) {
			resultInfo += "评估方案选择了：方案评估1(三步法)；";
		} else {
			resultInfo += "评估方案选择了：方案评估2(两步法)；";
		}

		resultInfo += "<br>";

		if (config.getSchemeType().equals("ONE")) {
			resultInfo += "收敛方式选择了：方案一(根据Top差值比例)；";
		} else {
			resultInfo += "收敛方式选择了：方案二(根据求方差)；";
		}

		resultInfo += "<br>优化结束方式：";
		switch (planFinishWay) {
		case PLAN_TYPE_ONE:
			resultInfo += "方案评估1(三步法)";
			break;
		case PLAN_TYPE_TWO:
			resultInfo += "方案评估2(两步法)";
			break;
		case SCHEME_TYPE_ONE:
			resultInfo += "收敛方式1(根据Top差值比例)";
			break;
		case SCHEME_TYPE_TWO:
			resultInfo += "收敛方式2(根据求方差)";
			break;
		}

		resultInfo += "<br>是否进行邻区核查：";
		if (config.isCheckNCell()) {
			resultInfo += "是；";
		} else {
			resultInfo += "否；";
		}

		resultInfo += "<br> " + config.getDislimit() + "米内，有无同PCI情况：";
		if (isSamePciTooClose) {
			resultInfo += "有。";
		} else {
			resultInfo += "无。";
		}
		resultInfo += "<br>";
		if (config.getSchemeType().equals("ONE")) {
			resultInfo += "最终的差值比例：" + lastInterRate + "，差值比例目标值：" + config.getDefInterRate();
		} else {
			resultInfo += "最终的均方差：" + lastVariance + "，均方差目标值" + config.getDefVariance();
		}

		resultInfo += "<br>";
		resultInfo += "方案生成次数：" + planCreateTimes;
		resultInfo += "<br>";
		resultInfo += "方案替换次数：" + planChangeTimes;
		resultInfo += "<br>";
		resultInfo += "生成TOPlist次数：" + generateToplistTimes;
		resultInfo += "<br>";
		resultInfo += "分配topCell次数：" + toplistCellChangeTimes;
		if (!(config.getDtKs() == -1.0)) {
			resultInfo += "<br>";
			resultInfo += "Ks修正值：" + config.getDtKs();
		}
		return resultInfo;
	}

	/**
	 * 收敛方式1(根据Top差值比例)
	 */
	private double getInterRate() {
		List<Double> descInterVals = new ArrayList<Double>(sortMapByValue(bestPlan.getCellToInterVal()).values());
		// top区间
		int start1 = 0;
		int start2 = (int) (descInterVals.size() * config.getTopRate());
		// 中间区间，如果 top 区间是奇数，则 mid 区间加大1
		int mid1 = (int) (descInterVals.size() / 2 - start2 / 2 - (start2 % 2));
		int mid2 = (int) (descInterVals.size() / 2 + start2 / 2);

		double topInterVal = 0;
		for (int i = start1; i < start2; i++) {
			topInterVal += descInterVals.get(i);
		}
		double midInterVal = 0;
		for (int i = mid1; i < mid2; i++) {
			midInterVal += descInterVals.get(i);
		}
		if (log.isDebugEnabled()) {
			log.debug("topInterVal={}, midInterVal={},start1={},start2={},mid1={},mid2={}", topInterVal, midInterVal,
					start1, start2, mid1, mid2);
		}
		return (topInterVal == 0) ? 0 : (topInterVal - midInterVal) / topInterVal;
	}

	/**
	 * 收敛方式2(根据求方差)
	 */
	private double getVarianceByCurrentPlan() {
		// 获取当前方案的干扰值小区排序，从大到小
		List<Double> descInterVals = new ArrayList<Double>(sortMapByValue(bestPlan.getCellToInterVal()).values());
		int size = bestPlan.getCellToInterVal().size();
		int div = size / config.getDivideNumber();
		int mod = size % config.getDivideNumber();

		int tmpDevideNumber = config.getDivideNumber();
		if (mod > 0) {
			div = div + 1;
			tmpDevideNumber = tmpDevideNumber - 1;
		}

		double totVal = 0;
		List<Double> listTotal = new ArrayList<Double>();
		// 以小区数均分为 n 份
		int k = 0;
		for (int i = 0; i < tmpDevideNumber; i++) {
			double total = 0;
			for (int j = 0; j < div; j++) {
				total += descInterVals.get(k);
				k++;
			}
			totVal += total;
			listTotal.add(total);
		}

		if (mod > 0) {
			double total = 0;
			for (int i = descInterVals.size() - div; i < descInterVals.size(); i++) {
				total += descInterVals.get(i);
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
		double result = Math.sqrt(val / size);
		return result;
	}

	/**
	 * 对整个列表进行PCI分配
	 * 
	 * @param plan
	 * @param descNeedToAssignCellList
	 * @author chen.c10
	 * @date 2016年5月28日
	 * @version RNO 3.0.1
	 */
	private void assignPciToCellsPlan(PlanObj plan, List<String> descNeedToAssignCellList) {
		// 已手动分配了PCI的小区列表，防止再被重新分配
		List<String> assignedList = new ArrayList<String>();
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
	 * 
	 * @param plan
	 * @param cellId
	 * @param assignedList 已经分配小区列表，允许为null
	 * @return 分配过的列表
	 * @author chen.c10
	 * @date 2016年5月24日
	 * @version RNO 3.0.1
	 */
	private AssignResult assignPciToCellsPlanForOneCell(PlanObj plan, String cellId) {
		long t1 = System.currentTimeMillis();
		// 需要在一个很大的循环中（最极端的情况下 168*6！）使用，故取出与cell相关的MR数据，放入一个较小的集合中
		final SmallParameter smallParameter = new SmallParameter();
		// 重置PCI工具
		pciUtil.reset();
		// 待分配小区
		final List<String> assignCells = new ArrayList<String>();
		// 最初排列
		final List<Integer> bestPcis = new ArrayList<Integer>();

		// 小区本身放入待分配列表
		assignCells.add(cellId);
		// 如果cell的同站邻区个数不超过5个，则将同站邻区与cell一起分配
		List<String> sstCells = cellToSameStationOtherCells.get(cellId);
		if (null != sstCells && sstCells.size() < 6) {
			assignCells.addAll(sstCells);
		}
		// 填充临时数据，减少多线程分配计算量。这里不使用并行，可以用非线程安全的final数据集保存数据，对轻量级的操作减少无谓的并发，让数据类型更加简单
		// 除了 bestPcis 其他数据集以后都不需要再修改，但bestPcis不应该被并行修改，应用缓存代替
		assignCells.stream().forEach(e -> {
			int oldPci = plan.getPciByCell(e);
			if (oldPci == -1) {
				oldPci = cellToOriPci.getOrDefault(e, -1);
			}
			bestPcis.add(oldPci);
			smallParameter.getCellToNotSameStationCells().put(e.intern(),
					cellToNotSameStationCells.getOrDefault(e, new ArrayList<String>()));
			smallParameter.getCellToNcellAssocDegree().put(e.intern(),
					cellToNcellAssocDegree.getOrDefault(e, new HashMap<String, Double>()));
			smallParameter.getNcellToNotSameStationCells().put(e.intern(),
					ncellToNotSameStationCells.getOrDefault(e, new ArrayList<String>()));
			smallParameter.getNcellToCellAssocDegree().put(e.intern(),
					ncellToCellAssocDegree.getOrDefault(e, new HashMap<String, Double>()));
		});
		// 与out和in干扰有关小区的pci映射,利用java8新特新，将Collection<List<String>>合并，并通过flatmap打散，去重，再进行pci提取.同样不使用并行
		Stream.concat(smallParameter.getCellToNotSameStationCells().values().stream(),
				smallParameter.getNcellToNotSameStationCells().values().stream()).flatMap(e -> e.stream()).distinct()
				.forEach(e -> {
					int oldPci = plan.getPciByCell(e);
					if (oldPci == -1) {
						oldPci = cellToOriPci.getOrDefault(e, -1);
					}
					smallParameter.getCellToPci().put(e.intern(), oldPci);
				});
		boolean isUpdated = false;
		if ("GROUP".equals(pciAssginType.toUpperCase())) {
			// 选出每种pci列表下，干扰值与最优的排列的映射
			isUpdated = selectBestPciGroupForCellsByGroup(plan, pciUtil, assignCells, bestPcis, smallParameter);
		} else {
			isUpdated = selectBestPciGroupForCellsByCell(plan, pciUtil, assignCells, bestPcis, smallParameter);
		}
		// 如果pci有变化，则将最优排列更新到方案里
		if (isUpdated) {
			plan.addAllCellsToPcis(assignCells, bestPcis);
		}
		log.debug("分配一个小区的时间：{}", System.currentTimeMillis() - t1);
		return new AssignResult(isUpdated, assignCells, bestPcis);
	}

	/**
	 * 选出待选的最优PCI列表
	 * 
	 * @param plan
	 * @param assignCells
	 * @param pciUtil
	 * @param resultMap
	 * @param minInterVal
	 * @param cellToNotSameStationCells
	 * @param cellToNcellAssocDegree
	 * @param ncellToNotSameStationCells
	 * @param ncellToCellAssocDegree
	 * @author chen.c10
	 * @date 2016年5月26日
	 * @version RNO 3.0.1
	 * @param cellToPci
	 */
	private boolean selectBestPciGroupForCellsByGroup(final PlanObj plan, final PciUtil pciUtil,
			final List<String> assignCells, final List<Integer> oriPcis, final SmallParameter smallParameter) {
		// 清空结果集
		interValToPcis.clear();
		// 最初排列下的干扰值
		final double oriInterVal = calNotSameStationCellsTotalInterVal(plan, assignCells, oriPcis, smallParameter);

		// 分别对168个组求最优pci排列
		Stream.iterate(0, n -> n += 1).limit(168).parallel().forEach(e -> assginPciGroupsForCells(plan, pciUtil,
				assignCells, oriPcis, e, interValToPcis, oriInterVal, smallParameter));
		// 只有当干扰值小于初始值的情况才会进入interValToPcis中，如果初始值为最优，那么interValToPcis肯定为空，就不会触发对oriPcis的修改
		if (interValToPcis.isEmpty()) {
			return false;
		}
		// 在最优pci排列结果集中，找出最优pci排列。
		interValToPcis.entrySet().parallelStream().min((o1, o2) -> Double.compare(o1.getKey(), o2.getKey()))
				.ifPresent(e -> {
					log.debug("assignCells={},oriInterVal={},minInterVal={},oriPcis={},bestPcis={}", assignCells,
							oriInterVal, e.getKey(), oriPcis, e.getValue());
					oriPcis.clear();
					oriPcis.addAll(e.getValue());
				});
		return true;
	}

	/**
	 * 从待选排列中选出干扰值最小的PCI排列
	 * 
	 * @param plan
	 * @param pciUtil
	 * @param assignCells
	 * @param oriPcis
	 * @param groupId
	 * @param resultMap
	 * @param oriInterVal
	 * @param cellToNotSameStationCells
	 * @param cellToNcellAssocDegree
	 * @param ncellToNotSameStationCells
	 * @param ncellToCellAssocDegree
	 * @author chen.c10
	 * @date 2016年5月28日
	 * @version RNO 3.0.1
	 */
	private void assginPciGroupsForCells(final PlanObj plan, final PciUtil pciUtil, final List<String> assignCells,
			final List<Integer> oriPcis, final int groupId, final Map<Double, List<Integer>> resultMap,
			final double oriInterVal, final SmallParameter smallParameter) {
		// 获取备选pci组
		final List<List<Integer>> pciAllocList = new ArrayList<List<Integer>>();
		allocSameStationOtherCellsPci(pciAllocList, pciUtil.getPciGroupsById(groupId, assignCells.size()),
				new ArrayList<Integer>(), assignCells.size());
		// 为每个备选pci组，循环分配pci
		double minInterVal = oriInterVal;
		List<Integer> bestPcis = new ArrayList<Integer>(oriPcis);
		for (List<Integer> pics : pciAllocList) {
			double interVal = calNotSameStationCellsTotalInterVal(plan, assignCells, pics, smallParameter);
			if (interVal < minInterVal) {
				minInterVal = interVal;
				bestPcis = new ArrayList<Integer>(pics);
			}
		}
		// 如果新的最小干扰值小于初始干扰值，则将结果存入结果集中
		if (minInterVal < oriInterVal) {
			resultMap.put(minInterVal, bestPcis);
		}
	}

	/**
	 * 选出待选的最优PCI列表
	 * 
	 * @param plan
	 * @param assignCells
	 * @param pciUtil
	 * @param resultMap
	 * @param minInterVal
	 * @param cellToNotSameStationCells
	 * @param cellToNcellAssocDegree
	 * @param ncellToNotSameStationCells
	 * @param ncellToCellAssocDegree
	 * @author chen.c10
	 * @date 2016年5月26日
	 * @version RNO 3.0.1
	 * @param cellToPci
	 */
	private boolean selectBestPciGroupForCellsByCell(final PlanObj plan, final PciUtil pciUtil,
			final List<String> assignCells, final List<Integer> oriPcis, final SmallParameter smallParameter) {
		// 最初排列下的干扰值
		double minInterVal = calNotSameStationCellsTotalInterVal(plan, assignCells.get(0), oriPcis.get(0),
				smallParameter);
		// 为主小区选出最优PCI
		for (int pci = 0; pci < 504; pci++) {
			double interVal = calNotSameStationCellsTotalInterVal(plan, assignCells.get(0), pci, smallParameter);
			if (interVal < minInterVal) {
				minInterVal = interVal;
				oriPcis.set(0, pci);
			}
		}
		// 有同站小区，继续分配
		if (assignCells.size() > 1) {
			assginPciForCell(plan, pciUtil, assignCells, oriPcis, smallParameter);
		}
		return true;
	}

	/**
	 * 为同站同频其他小区分配PCI
	 * 
	 * @param plan
	 * @param pciUtil
	 * @param assignCells
	 * @param oriPcis
	 * @param cellToNotSameStationCells
	 * @param cellToNcellAssocDegree
	 * @param ncellToNotSameStationCells
	 * @param ncellToCellAssocDegree
	 * @author chen.c10
	 * @date 2016年5月28日
	 * @version RNO 3.0.1
	 */
	private void assginPciForCell(final PlanObj plan, final PciUtil pciUtil, final List<String> assignCells,
			final List<Integer> oriPcis, final SmallParameter smallParameter) {
		// 当list的长度大于0时，subList(1, size) 是安全的，sublist 不改变原对象，运用clear操作可以清空原list对应的数据
		final List<String> sstCells = assignCells.subList(1, assignCells.size());
		final List<Integer> oriSstPcis = oriPcis.subList(1, oriPcis.size());

		List<List<Integer>> pciAllocList = new ArrayList<List<Integer>>();
		allocSameStationOtherCellsPci(pciAllocList, pciUtil.getPciGroups(oriPcis.get(0), sstCells.size()),
				new ArrayList<Integer>(), sstCells.size());
		double minInterVal = Double.MAX_VALUE;
		for (List<Integer> pics : pciAllocList) {
			// 分配PCI
			double interVal = calNotSameStationCellsTotalInterVal(plan, sstCells, pics, smallParameter);
			if (interVal < minInterVal) {
				minInterVal = interVal;
				oriSstPcis.clear();
				oriSstPcis.addAll(pics);
			}
		}
		pciAllocList = null;
	}

	/**
	 * 计算非同站小区的总干扰值
	 * 
	 * @param plan
	 * @param cellToPci
	 * @return 总干扰值
	 */
	private double calNotSameStationCellsTotalInterVal(final PlanObj plan, final List<String> asignCells,
			final List<Integer> pics, final SmallParameter smallParameter) {
		return Stream.iterate(0, n -> n += 1).limit(asignCells.size())
				.mapToDouble(
						i -> calNotSameStationCellsTotalInterVal(plan, asignCells.get(i), pics.get(i), smallParameter))
				.sum();
	}

	/**
	 * 计算非同站小区的总干扰值
	 * 
	 * @param notSameStationCells 非同站小区
	 * @return 总干扰值
	 */
	private double calNotSameStationCellsTotalInterVal(final PlanObj plan, final String cellId, final int cellPci,
			final SmallParameter smallParameter) {
		// out 干扰 + in 干扰
		return calNotSameStationCellsTotalInterVal(plan, cellId, cellPci, smallParameter.getCellToNotSameStationCells(),
				smallParameter.getCellToNcellAssocDegree(), smallParameter.getCellToPci())
				+ calNotSameStationCellsTotalInterVal(plan, cellId, cellPci,
						smallParameter.getNcellToNotSameStationCells(), smallParameter.getNcellToCellAssocDegree(),
						smallParameter.getCellToPci());
	}

	/**
	 * 计算干扰值
	 * 
	 * @param plan
	 * @param cellId
	 * @param cellPci
	 * @param cellToCells
	 * @param assocDegree
	 * @return
	 * @author chen.c10
	 * @date 2016年5月25日
	 * @version RNO 3.0.1
	 */
	private double calNotSameStationCellsTotalInterVal(final PlanObj plan, final String cellId, final int cellPci,
			final Map<String, List<String>> cellToCells, final Map<String, Map<String, Double>> assocDegree,
			final Map<String, Integer> cellToPci) {
		// 如果pci小于0 则pci不存在，返回0
		if (cellPci < 0) {
			return 0;
		}
		return cellToCells.getOrDefault(cellId, new ArrayList<String>()).stream()
				.mapToDouble(e -> getModVal(cellPci, cellToPci.get(e)) * assocDegree.get(cellId).getOrDefault(e, 0.0))
				.sum() * calKs(cellId);
	}

	/**
	 * 计算两个小区间的模系数
	 * 
	 * @param cellPci
	 * @param ncellPci
	 * @return
	 * @author chen.c10
	 * @date 2016年5月24日
	 * @version RNO 3.0.1
	 */
	private double getModVal(int cellPci, int ncellPci) {
		// cellPci 已经检查过，检查ncellPci
		if (ncellPci < 0) {
			return 0;
		}
		double modVal = 0;
		if (cellPci % 3 == (ncellPci % 3)) {
			modVal += config.getM3r();
		}
		if (cellPci % 6 == (ncellPci % 6)) {
			modVal += config.getM6r();
		}
		if (cellPci % 30 == (ncellPci % 30)) {
			modVal += config.getM30r();
		}
		return modVal;
	}

	/**
	 * 分配同站其他小区的递归算法
	 * 
	 * @param pciAllocList
	 * @param pcis
	 * @param tmpPcis
	 * @param length
	 * @author chen.c10
	 * @date 2016年5月24日
	 * @version RNO 3.0.1
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
				tmpPcis2 = new ArrayList<Integer>(tmpPcis);
				tmpPcis2.add(i);
				allocSameStationOtherCellsPci(pciAllocList, pcis, tmpPcis2, length - 1);
			}
		}
	}

	/**
	 * 在首套方案中为所有主小区分配原始PCI
	 * chen.c10在添加D1D2频率调整时修改，增加频点以适用频点变动要求
	 * 
	 * @param plan
	 * @param cellList
	 */
	private void assignOriPciAndEarfcnByDescCells(PlanObj plan) {
		for (String cellId : cellToTotalAssocDegree.keySet()) {
			if (cellToOriPci.containsKey(cellId) && cellToOriEarfcn.containsKey(cellId)) {
				// 为当前小区分配原始PCI
				plan.addCellToPci(cellId, cellToOriPci.get(cellId));
				// 为当前小区分配原始earfcn
				plan.addCellToEarfcn(cellId, cellToOriEarfcn.get(cellId));
				// 为同站其他小区分配原始PCI和earfcn
				if (cellToSameStationOtherCells.get(cellId) != null) {
					for (String ocellId : cellToSameStationOtherCells.get(cellId)) {
						if (cellToOriPci.containsKey(ocellId) && cellToOriEarfcn.containsKey(cellId)) {
							plan.addCellToPci(ocellId, cellToOriPci.get(ocellId));
							plan.addCellToEarfcn(ocellId, cellToOriEarfcn.get(ocellId));
						}
					}
				}
			}
		}
	}

	/**
	 * 进行邻区核查，为了保证服务小区跟所有邻区之间的pci不相等，相等就+-30来修正
	 * 
	 * @param plan 当前方案
	 * @param descCellList 小区与邻区列的映射的列表
	 */
	private void checkNCellPci(PlanObj plan, List<String> descCellList) {
		if (log.isDebugEnabled()) {
			log.debug("===>>>checkNCellPci():进入pci+-30循环");
		}
		int count = 0;
		// 检查服务小区，邻区之间是否存在同pci,存在则+30或-30
		for (int i = 0; i < descCellList.size(); i++) {
			List<String> cells = cellToNotSameStationCells.get(descCellList.get(i));
			if (cells == null)
				continue;

			// 最多检查前50个非同站小区
			int maxCheck = cells.size();
			if (maxCheck > 50) {
				maxCheck = 50;
			}

			// 加入服务小区,服务小区不能和其他小区相同pci
			cells.add(descCellList.get(i).intern());

			// 获取小区列所使用的pci
			Map<String, Integer> ncellToPci = new HashMap<String, Integer>();
			for (String c : cells) {
				ncellToPci.put(c.intern(), plan.getPciByCell(c));
			}

			for (int k = 0; k < maxCheck; k++) {
				for (int j = 0; j < maxCheck; j++) {
					if (count > 1000) {
						// logger.debug("保证服务小区跟所有邻区之间的pci不相等,修正循环已执行了1000次，break掉！");
						count = 0;
						break;
					}
					// logger.debug("pci+-30循环中："+count);
					if (k == j)
						continue;
					// 不需要优化的小区，跳过
					if (!cellsNeedtoAssignList.contains(cells.get(k)))
						continue;
					int pci1 = plan.getPciByCell(cells.get(k));
					int pci2 = plan.getPciByCell(cells.get(j));
					if (pci1 == -1 || pci2 == -1)
						continue;
					if (pci1 == pci2) {
						if (503 >= (pci1 + 30)) {
							pci1 += 30;
							while (ncellToPci.containsValue(pci1)) {
								if (503 < (pci1 + 30))
									break;
								pci1 += 30;
							}
							plan.addCellToPci(cells.get(k), pci1);
							ncellToPci.put(cells.get(k).intern(), pci1);
						} else {
							pci1 -= 30;
							while (ncellToPci.containsValue(pci1)) {
								if (pci1 < 30)
									break;
								pci1 -= 30;
							}
							plan.addCellToPci(cells.get(k), pci1);
							ncellToPci.put(cells.get(k).intern(), pci1);
						}
					}
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("===>>>checkNCellPci():退出pci+-30循环");
		}
	}

	/**
	 * 检查在主小区距离限制范围内有无同频同pci的情况，有则进行+-30处理
	 * 
	 * @param plan
	 * @param descCellList
	 * @author chen.c10
	 * @date 2015年10月29日 上午9:42:44
	 * @company 怡创科技
	 * @version V1.0
	 */
	private void checkNeighborAreaPci(PlanObj plan, List<String> descNeedToAssignCellList) {
		if (log.isDebugEnabled()) {
			log.debug("===>>>checkNeighborAreaPci2():进入pci+-30循环");
		}

		long t1 = System.nanoTime();
		Map<String, Integer> cellToNewPci = new HashMap<String, Integer>(cellToOriPci);
		Map<Integer, Integer> pciToNewAllocAmount = new HashMap<Integer, Integer>(pciToAllocAmount);
		cellToNewPci.putAll(plan.getCellToPci());
		if (log.isDebugEnabled()) {
			log.debug("工参小区数量为: {}", cellToNewPci.size());
			log.debug("方案小区数量为：{}", plan.getCellToPci().size());
			log.debug("修改小区数量为：{}", descNeedToAssignCellList.size());
		}
		Map<Integer, List<String>> pciToCell = new HashMap<Integer, List<String>>();
		for (String cid : cellToNewPci.keySet()) {
			int pci = cellToNewPci.get(cid);
			// 如果方案列表里面不包含该小区
			if (!plan.getCellToPci().containsKey(cid)) {
				pciToNewAllocAmount.put(pci, pciToNewAllocAmount.get(pci) + 1);
			}
			if (!pciToCell.containsKey(pci)) {
				pciToCell.put(pci, new ArrayList<String>());
			}
			pciToCell.get(pci).add(cid);
		}

		List<String> cellToAllocList = new ArrayList<String>();
		List<String> descCellList = new ArrayList<String>(descNeedToAssignCellList);
		isSamePciTooClose = false;

		List<String> sstCells = null;
		int mPci = -1;
		int mPciGp = -1;
		Map<Integer, List<Integer>> pciGroup = new HashMap<Integer, List<Integer>>();

		for (String cellId : descCellList) {
			// 如果作为同站小区分配过了，则跳过
			if (cellToAllocList.contains(cellId)) {
				continue;
			}
			mPci = cellToNewPci.get(cellId);
			mPciGp = (int) Math.floor(mPci / 3);
			// 获取该小区的同站小区
			sstCells = new ArrayList<String>();
			if (cellToSameStationOtherCells.containsKey(cellId)) {
				sstCells = cellToSameStationOtherCells.get(cellId);
			}
			// 与主小区同组的PCI列表，都需要参与计算
			Map<String, Integer> sgCellToPci = new HashMap<String, Integer>();
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
			boolean sespCloseFlag = false;
			// 先检查有无同频同pci的小区组，大于距离限制，sespCloseFlag = false为大于
			for (String sgCell : sgCellToPci.keySet()) {
				// 检查五公里有无同频同PCI
				for (String spCellId : pciToCell.get(sgCellToPci.get(sgCell))) {
					// 先找出同频同pci的小区
					if (!sgCell.equals(spCellId) && plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(sgCell)) {
						// 检查有无小于距离限制的小区
						double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(sgCell));
						if (dis < config.getDislimit()) {
							sespCloseFlag = true;
							break;
						}
					}
				}
			}
			// 如果有同频同pci不满足距离限制
			if (sespCloseFlag) {
				// 按PCI除以3的整数部分进行分组，如1和2都为0，为0组，4，和5都为1，为1组。
				pciGroup.clear();
				// 0到503可以分为168组
				for (int i = 0; i < 168; i++) {
					// 排除主小区本身的组，选择与主小区组模10值相同的组，这样能保证PCI模30值相同。
					if (i != mPci / 3 && i % 10 == mPci / 3 % 10) {
						List<Integer> l = new ArrayList<Integer>();
						// 组号*3再加上PCI的模3值可得到与PCI模30值相同的PCI
						for (int j : sgCellToPci.values()) {
							l.add(i * 3 + j % 3);
						}
						pciGroup.put(i, l);
					}
				}
				// 用于配对的模30PCI组分配完毕。
				Map<Integer, Double> groupToDis = new HashMap<Integer, Double>();
				Map<Integer, Double> mcPciGpToDis = new HashMap<Integer, Double>();
				// 按组号进行循环，计算每一组的情况。
				for (int pciGpId : pciGroup.keySet()) {
					List<Double> minDisList = new ArrayList<Double>();
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
						if (tmpCellId.equals("")) {
							continue;
						}
						// 获取频点
						Map<String, Double> closeCells = new HashMap<String, Double>();
						Map<String, Double> farCells = new HashMap<String, Double>();
						if (pciToCell.containsKey(pci)) {// 判断已分配pci
							for (String spCellId : pciToCell.get(pci)) {
								// 检查五公里有无同频PCI为待选PCI的小区
								if (!tmpCellId.equals(spCellId)
										&& plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(tmpCellId)) {
									// 检查有无小于距离限制的小区
									// 计算同频小区与计算小区的距离。这里因为都是主小区的同站小区，tmpCellId可以用主小区ID代替。
									double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(tmpCellId));
									// 保存小区距离限制的小区
									if (dis < config.getDislimit()) {
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
						} else if (farCells.size() > 0) {// 保存结果为距离限制
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
						if (minDis < config.getDislimit()) {
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
					// 判断放弃同组匹配，改用单小区匹配
					boolean needAssginFlag = false;
					// 检查五公里有无同频同PCI
					for (String spCellId : pciToCell.get(sgCellToPci.get(cellId))) {
						// 先找出同频同pci的小区
						if (!cellId.equals(spCellId)
								&& plan.getEarfcnByCell(spCellId) == plan.getEarfcnByCell(cellId)) {
							// 检查有无小于距离限制的小区
							double dis = distance(cellToLonLat.get(spCellId), cellToLonLat.get(cellId));
							if (dis < config.getDislimit()) {
								needAssginFlag = true;
								break;
							}
						}
					}
					if (needAssginFlag) {
						Map<Integer, Double> canAssginPcis = new HashMap<Integer, Double>();
						for (int pci = 0; pci < 504; pci++) {
							if (pci % 30 != mPci % 30) {
								continue;
							}
							if (!pciToCell.containsKey(pci)) {
								canAssginPcis.put(pci, Double.MAX_VALUE / 3);
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
										if (dis > config.getDislimit()) {
											canAssginPcis.put(pci, dis);
										}
									}
								}
							}
						}
						if (!canAssginPcis.isEmpty()) {
							double minDis = Double.MAX_VALUE;
							List<Integer> selects = new ArrayList<Integer>();
							for (double dis : canAssginPcis.values()) {
								if (dis < minDis) {
									minDis = dis;
								}
							}
							for (Integer pci : canAssginPcis.keySet()) {
								if (canAssginPcis.get(pci) == minDis) {
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
								pciToCell.put(minCntPci, new ArrayList<String>());
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
					List<Integer> sameDisPciList = new ArrayList<Integer>();
					for (Integer pciGp : groupToDis.keySet()) {
						if (maxDis == groupToDis.get(pciGp)) {
							sameDisPciList.add(pciGp);
						}
					}
					// 如果缓存多余一个，则选择复用度较小的,复用度再相等的情况不再考虑
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
					List<Integer> sameDisPciList = new ArrayList<Integer>();
					for (Integer pciGp : mcPciGpToDis.keySet()) {
						if (maxDis == mcPciGpToDis.get(pciGp)) {
							sameDisPciList.add(pciGp);
						}
					}
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
									pciToCell.put(pci, new ArrayList<String>());
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
	 * map根据value值,从大到小排序
	 *
	 * @param unsortMap
	 * @return
	 * @author peng.jm
	 */
	private Map<String, Double> sortMapByValue(Map<String, Double> unsortMap) {
		return unsortMap.entrySet().parallelStream().sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
				.collect(LinkedHashMap::new, (m, p) -> m.put(p.getKey(), p.getValue()), Map::putAll);
	}

	/**
	 * 对由long值组成的List<String>按照从小到大排序
	 * 
	 * @param unsortList
	 * @return
	 * @author chen.c10
	 * @date 2015年10月29日 下午12:03:23
	 * @company 怡创科技
	 * @version V1.0
	 */
	private List<String> sortStringList(List<String> unsortList) {
		return unsortList.parallelStream().sorted((o1, o2) -> Long.compare(Long.parseLong(o1.replace("-", "")), Long.parseLong(o2.replace("-", ""))))
				.collect(Collectors.toList());
	}

	/**
	 * 保存最终方案到oracle
	 *
	 * @param plan
	 *            最佳方案
	 * @return
	 * @author peng.jm
	 * @date 2015年3月26日14:53:26
	 */
	private void saveBestPlan(PlanObj plan) {
		if (log.isDebugEnabled()) {
			log.debug("把最佳方案写入到oracle");
		}

		try {
			// 将MR数据和待优化PCI的小区都并在一起输出
			// MR数据存在原始干扰值，计算干扰值，计算PCI
			// 待优化PCI小区中可能存在着部分小区在这批MR数据中并不存在，所以不能进行计算干扰值和分配PCI
			// 这里处理的方法是将MR数据中的存在的所有小区都输出，然后检查待优化小区哪些是不存在于MR数据中的小区
			// 将这些小区也以默认值输出，默认原干扰值0，计算干扰值0，PCI为-1

			// 检查不存在与MR数据的小区
			List<String> cellsNotInData = new ArrayList<String>();
			for (String cellId : cellsNeedtoAssignList) {
				if (!plan.getCellToPci().containsKey(cellId)) {
					cellsNotInData.add(cellId.intern());
				}
			}
			
		    dataList = new ArrayList<>();

			// 写入数据中存在的小区PCI以及干扰值
			for (String cellId : sortStringList(new ArrayList<String>(plan.getCellToPci().keySet()))) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				// 小区ID
				dataMap.put("cellId", cellId);
				// 小区计算频点
				dataMap.put("earfcn", plan.getEarfcnByCell(cellId));
				// 小区计算PCI
				dataMap.put("pci", plan.getPciByCell(cellId));
				// 小区原干扰值
				if (cellToOriInterVal.get(cellId) != null) {
					dataMap.put("oldInterVal", cellToOriInterVal.get(cellId));
				} else {
					dataMap.put("oldInterVal", 0.0);
				}
				// 小区计算干扰值
				dataMap.put("newInterVal", plan.getInterValByCell(cellId));
				dataList.add(dataMap);
			}
			cellsNotInData = sortStringList(cellsNotInData);
			// 写入数据中不存在的小区默认PCI以及干扰值
			for (String cellId : cellsNotInData) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				dataMap.put("cellId", cellId);
				dataMap.put("earfcn", -1);
				dataMap.put("pci", -1);
				dataMap.put("oldInterVal", 0.0);
				dataMap.put("newInterVal", 0.0);
				dataList.add(dataMap);
			}
			
			oracleMapper.saveBestPlan(dataList);
				
		} catch (Exception e) {
			log.error("pci最终方案保存失败！");
			e.printStackTrace();
		} 
	}

	/**
	 * 保存需要输出的关联表到oracle
	 *
	 * @param plan
	 *            需要输出的关联表
	 * @return
	 * @author li.tf
	 * @date 2015年12月4日10:56:26
	 */
	private void saveAssoTable(Map<String, Double> asso) {
		if (log.isDebugEnabled()) {
			log.debug("把关联表写入oracle");
		}
		
	    dataList = new ArrayList<>();

		try {
			// 写入数据中存在的小区ID以及关联度
			for (String cellId : asso.keySet()) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				// 小区ID
				dataMap.put("cellId", cellId);
				// 小区关联度
				dataMap.put("relaVal", asso.get(cellId));
				dataList.add(dataMap);
			}
			
			oracleMapper.saveAssoTable(dataList);
		} catch (Exception e) {
			log.error("pci关联表保存失败！");
			e.printStackTrace();
		} 
	}

	/**
	 * 保存中间方案到oracle
	 *
	 * @param plan
	 *            中间方案
	 * @return
	 * @author li.tf
	 * @date 2015年12月4日10:15:26
	 */
	private void saveExportPlan(PlanObj plan, List<String> topCellList, int planNum) {
		if (log.isDebugEnabled()) {
			log.debug("把所有方案写入到oracle");
		}

		try {
			// 将MR数据和待优化PCI的小区都并在一起输出
			// MR数据存在原始干扰值，计算干扰值，计算PCI
			// 待优化PCI小区中可能存在着部分小区在这批MR数据中并不存在，所以不能进行计算干扰值和分配PCI
			// 这里处理的方法是将MR数据中的存在的所有小区都输出，然后检查待优化小区哪些是不存在于MR数据中的小区
			// 将这些小区也以默认值输出，默认原干扰值0，计算干扰值0，PCI为-1

			// 检查不存在与MR数据的小区
			List<String> cellsNotInData = new ArrayList<String>();
			for (String cellId : cellsNeedtoAssignList) {
				if (!plan.getCellToPci().containsKey(cellId)) {
					cellsNotInData.add(cellId.intern());
				}
			}

			List<String> cellToPci = new ArrayList<String>(plan.getCellToPci().keySet());
			cellToPci = sortStringList(cellToPci);
			
			dataList = new ArrayList<>();
			
			// 写入数据中存在的小区PCI以及干扰值
			for (String cellId : cellToPci) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				dataMap.put("planNum", planNum);
				// 小区ID
				dataMap.put("cellId", cellId);
				// 小区计算频点
				dataMap.put("earfcn", plan.getEarfcnByCell(cellId));
				// 小区计算PCI
				dataMap.put("pci", plan.getPciByCell(cellId));
				// 小区原干扰值
				if (cellToOriInterVal.get(cellId) != null) {
					dataMap.put("oldInterVal", cellToOriInterVal.get(cellId));
				} else {
					dataMap.put("oldInterVal", 0.0);
				}
				// 小区计算干扰值
				dataMap.put("newInterVal", plan.getInterValByCell(cellId));
				dataList.add(dataMap);
			}
			cellsNotInData = sortStringList(cellsNotInData);
			// 写入数据中不存在的小区默认PCI以及干扰值
			for (String cellId : cellsNotInData) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				dataMap.put("planNum", planNum);
				dataMap.put("cellId", cellId);
				dataMap.put("earfcn", -1);
				dataMap.put("pci", -1);
				dataMap.put("oldInterVal", 0.0);
				dataMap.put("newInterVal", 0.0);
				dataList.add(dataMap);
			}
			
			oracleMapper.saveMidPlan(dataList);
			
			dataList = new ArrayList<>();
			for (String cellId : topCellList) {
				dataMap = new HashMap<>();
				dataMap.put("cellId", cellId);
				dataMap.put("interVal", plan.getInterValByCell(cellId));
				dataList.add(dataMap);
			}
			
			oracleMapper.saveTopCell(dataList);
			
		} catch (Exception e) {
			log.error("pci中间方案保存失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 保存需要输出的邻区核查方案到oracle
	 *
	 * @param plan
	 *            需要输出的邻区核查方案
	 * @param suffix
	 *            文件后缀
	 * @return
	 * @author li.tf
	 * @date 2015年12月4日15:16:26
	 */
	private void saveNcCheckPlan(PlanObj plan) {
		if (log.isDebugEnabled()) {
			log.debug("把邻区核查方案写入到oracle");
		}

		try {
			List<String> cellsNotInData = new ArrayList<String>();
			for (String cellId : cellsNeedtoAssignList) {
				if (!plan.getCellToPci().containsKey(cellId)) {
					cellsNotInData.add(cellId.intern());
				}
			}

			List<String> cellToPci = new ArrayList<String>(plan.getCellToPci().keySet());
			cellToPci = sortStringList(cellToPci);
			
			dataList = new ArrayList<>();
			
			// 写入数据中存在的小区PCI以及干扰值
			for (String cellId : cellToPci) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				// 小区ID
				dataMap.put("cellId", cellId);
				// 小区计算频点
				dataMap.put("earfcn", plan.getEarfcnByCell(cellId));
				// 小区计算PCI
				dataMap.put("pci", plan.getPciByCell(cellId));
				// 小区原干扰值
				if (cellToOriInterVal.get(cellId) != null) {
					dataMap.put("oldInterVal", cellToOriInterVal.get(cellId));
				} else {
					dataMap.put("oldInterVal", 0.0);
				}
				// 小区计算干扰值
				dataMap.put("newInterVal", plan.getInterValByCell(cellId));
				dataList.add(dataMap);
			}
			cellsNotInData = sortStringList(cellsNotInData);
			// 写入数据中不存在的小区默认PCI以及干扰值
			for (String cellId : cellsNotInData) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				dataMap.put("cellId", cellId);
				dataMap.put("earfcn", -1);
				dataMap.put("pci", -1);
				dataMap.put("oldInterVal", 0.0);
				dataMap.put("newInterVal", 0.0);
				dataList.add(dataMap);
			}

			oracleMapper.saveNcCheckPlan(dataList);
			
		} catch (Exception e) {
			log.error("pci邻区核查方案保存失败！");
			e.printStackTrace();
		} 
	}

	/**
	 * 保存d1,d2 小区到oracle
	 *
	 * @return
	 * @author li.tf
	 * @date 2015年12月4日14:16:26
	 */
	private void saveD1D2List(List<String> cells, PlanObj bestPlan, int type) {
		if (log.isDebugEnabled()) {
			log.debug("把d1,d2小区表写入到oracle");
		}

		dataList = new ArrayList<>();
		try {
			for (String cell : cells) {
				dataMap = new HashMap<>();
				dataMap.put("jobId", config.getJobId());
				dataMap.put("cellId", cell);
				dataMap.put("earfcn", bestPlan.getEarfcnByCell(cell));
				dataMap.put("interVal", bestPlan.getInterValByCell(cell));
				dataList.add(dataMap);
			}
			if(type==1) {
				oracleMapper.saveD1Cell(dataList);
			}else{
				oracleMapper.saveD2Cell(dataList);
			}
		} catch (Exception e) {
	        log.error("pci的d1,d2小区表保存失败！");
			e.printStackTrace();
		} 
	}

	/**
	 * 获取ks
	 * 
	 * @param cellId
	 * @return
	 * @author chen.c10
	 * @date 2016年5月27日
	 * @version RNO 3.0.1
	 */
	private double calKs(String cellId) {
		return cellToKs.containsKey(cellId) ? cellToKs.get(cellId) : 1;
	}

	/**
	 * 计算gps里程(米)
	 * 
	 * @param startlong
	 * @return 里程
	 */
	public static double distance(double[] start, double[] end) {
		return distance(start[0], start[1], end[0], end[1]);
	}

	/**
	 * 计算gps里程(米)
	 * 
	 * @param startlong
	 * @return 里程
	 */
	public static double distance(double startlong, double startlat, double endlong, double endlat) {
		double a, b;
		startlat = startlat * Math.PI / 180.0;
		endlat = endlat * Math.PI / 180.0;
		a = startlat - endlat;
		b = (startlong - endlong) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(startlat) * Math.cos(endlat) * sb2 * sb2));
		return d;
	}

	/**
	 * 将PCI分成168个组，每组三个PCI。
	 */
	class PciUtil {
		// 按照[0,1,2],[3,4,5]....分组
		List<List<Integer>> pciGroups = new ArrayList<List<Integer>>();

		// 当前获取到第几个pci组
		int step = 0;

		public PciUtil() {
			List<Integer> temp = new ArrayList<Integer>();
			for (int i = 0; i <= 503; i++) {
				temp.add(i);
				if ((i + 1) % 3 == 0) {
					pciGroups.add(temp);
					temp = new ArrayList<Integer>();
				}
			}
		}

		/**
		 * 重置PCI分组工具
		 * 
		 * @author chen.c10
		 * @date 2016年5月25日
		 * @version RNO 3.0.1
		 */
		public void reset() {
			step = 0;
		}

		/**
		 * 取到 PCI 所在的分组
		 * 
		 * @param pci PCI 值
		 * @return 所在组的 PCI 列表
		 */
		public List<Integer> getPciGroup(int pci) {
			return getPciGroupById(pci / 3);
		}

		/**
		 * 通过组Id获取一组pci列表
		 * 
		 * @param pci
		 * @return
		 * @author chen.c10
		 * @date 2016年5月24日
		 * @version RNO 3.0.1
		 */
		public List<Integer> getPciGroups(int pci, int length) {
			List<Integer> result = getPciGroupsById(pci / 3, length + 1);
			result.remove(new Integer(pci));
			// 创建新的list不影响源数据
			return result;
		}

		/**
		 * 按顺序取到一个 PCI 分组
		 * 
		 * @return 一组的 PCI 列表
		 */
		public List<Integer> getOneGroup() {
			if (step > 167) {
				step = 0;
			}
			// 创建新的list不影响源数据
			return getPciGroupById(step++);
		}

		/**
		 * 按顺序取到一个 PCI 分组
		 * 
		 * @return 一组的 PCI 列表
		 */
		public synchronized List<Integer> getGroups(int length) {
			List<Integer> result = new ArrayList<Integer>();
			if (step > 167) {
				step = 0;
			}
			int tmpStep = step++;
			// length >0
			for (int i = 0; i < (length - 1) / 3 + 1; i++) {
				if (tmpStep > 167) {
					tmpStep = 0;
				}
				result.addAll(getPciGroupById(tmpStep++));
			}
			// 创建新的list不影响源数据
			return result;
		}

		/**
		 * 通过组Id获取一组pci列表
		 * 
		 * @param groupId
		 * @return
		 * @author chen.c10
		 * @date 2016年5月24日
		 * @version RNO 3.0.1
		 */
		public List<Integer> getPciGroupById(int groupId) {
			return new ArrayList<Integer>(pciGroups.get(groupId));
		}

		/**
		 * 通过组Id获取一组pci列表
		 * 
		 * @param groupId
		 * @return
		 * @author chen.c10
		 * @date 2016年5月24日
		 * @version RNO 3.0.1
		 */
		public List<Integer> getPciGroupsById(int groupId, int length) {
			List<Integer> result = new ArrayList<Integer>();
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

		/** 保存已分配 PCI 的小区 **/
		private HashMap<String, Integer> cellToPci = new HashMap<String, Integer>();

		/** 保存PCI与小区的映射，用于计算同PCI小区距离 **/
		private HashMap<Integer, List<String>> pciToCell = new HashMap<Integer, List<String>>();

		/** 保存已计算干拢值的小区 **/
		private ConcurrentHashMap<String, Double> cellToInterVal = new ConcurrentHashMap<String, Double>();

		/** 保存已计算干拢值的需要分配的小区 **/
		private ConcurrentHashMap<String, Double> needAssignCellToInterVal = new ConcurrentHashMap<String, Double>();

		/** 小区与频率的映射 **/
		private HashMap<String, Integer> cellToEarfcn = new HashMap<String, Integer>();

		/** 同频段下小区列表 **/
		private HashMap<Integer, List<String>> earfcnToCell = new HashMap<Integer, List<String>>();

		/** d1小区列表 **/
		private ArrayList<String> d1CellList = new ArrayList<String>();
		/** d2小区列表 **/
		private ArrayList<String> d2CellList = new ArrayList<String>();

		/** 总干扰值 **/
		private double totalInterVal = 0;

		public HashMap<String, Integer> getCellToPci() {
			return cellToPci;
		}

		public List<String> getD1CellList() {
			return d1CellList;
		}

		public void setD1CellList(ArrayList<String> d1CellList) {
			this.d1CellList = d1CellList;
		}

		public List<String> getD2CellList() {
			return d2CellList;
		}

		public void setD2CellList(ArrayList<String> d2CellList) {
			this.d2CellList = d2CellList;
		}

		public Map<String, Integer> getCellToEarfcn() {
			return cellToEarfcn;
		}

		public HashMap<Integer, List<String>> getEarfcnToCell() {
			return earfcnToCell;
		}

		/**
		 * 获取已计算干拢值的小区MAP
		 * 
		 * @return 已计算干拢值的小区MAP
		 */
		public ConcurrentHashMap<String, Double> getCellToInterVal() {
			return cellToInterVal;
		}

		public ConcurrentHashMap<String, Double> getNeedAssignCellToInterVal() {
			return needAssignCellToInterVal;
		}

		public void addCellToEarfcn(String cellId, Integer earfcn) {
			// 把cellId分配给新的pci
			if (!earfcnToCell.containsKey(earfcn)) {
				earfcnToCell.put(earfcn, new ArrayList<String>());
			}
			earfcnToCell.get(earfcn).add(cellId.intern());
			// 再把旧的 pci 分配次数减1
			if (cellToEarfcn.containsKey(cellId)) {
				// 把cellId从旧的pci中删除
				earfcnToCell.get(cellToEarfcn.get(cellId)).remove(cellId);
			}
			cellToEarfcn.put(cellId.intern(), earfcn);
		}

		public void addCellToPci(String cellId, Integer pci) {
			// 进行 PCI 分配次数的记录，先为新的 pci 分配次数累加1
			pciToAllocAmount.put(pci, pciToAllocAmount.get(pci) + 1);
			// 把cellId分配给新的pci
			List<String> cells = pciToCell.get(pci);
			if (null == cells) {
				pciToCell.put(pci, new ArrayList<String>());
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

		public void addAllCellsToPcis(List<String> cells, List<Integer> pcis) {
			if (cells.size() == pcis.size()) {
				for (int i = 0; i < cells.size(); i++) {
					addCellToPci(cells.get(i), pcis.get(i));
				}
			}
		}

		public void addCellToInterVal(String cellId, double interVal) {
			cellToInterVal.put(cellId.intern(), interVal);
			if (cellsNeedtoAssignList.contains(cellId)) {
				needAssignCellToInterVal.put(cellId.intern(), interVal);
			}
		}

		public Map<Integer, List<String>> getPciToCell() {
			return pciToCell;
		}

		public List<String> getCellsByEarfcn(int earfcn) {
			List<String> cells = earfcnToCell.get(earfcn);
			return cells == null ? new ArrayList<String>() : cells;
		}

		public int getEarfcnByCell(String cellId) {
			if (cellToEarfcn.containsKey(cellId)) {
				return cellToEarfcn.get(cellId);
			}
			return 0;
		}

		public List<String> getSameEarfcnCellsByCell(String cellId) {
			return getCellsByEarfcn(getEarfcnByCell(cellId));
		}

		public List<String> getCellsByPci(int pci) {
			return pciToCell.getOrDefault(pci, new ArrayList<String>());
		}

		public int getPciByCell(String cellId) {
			return cellToPci.getOrDefault(cellId, -1);
		}

		public List<String> getSamePciCellsByCell(String cellId) {
			return getCellsByPci(getPciByCell(cellId));
		}

		public double getInterValByCell(String cellId) {
			if (cellToInterVal.containsKey(cellId)) {
				return cellToInterVal.get(cellId);
			}
			return 0;
		}

		public double getTotalInterVal() {
			return totalInterVal;
		}

		/**
		 * 计算当前方案的每个小区对应的干扰值
		 * 
		 * @param cellToNcells
		 */
		public void calInterVal(Map<String, List<String>> cellToNcells) {
			totalInterVal = 0;
			List<String> ncells;
			for (Entry<String, Integer> one : cellToPci.entrySet()) {
				ncells = cellToNcells.getOrDefault(one.getKey(), new ArrayList<String>());
				// 计算干扰值
				double interVal = ncells.parallelStream()
						.mapToDouble(e -> getModVal(one.getKey(), e, one.getValue()) * getRelaVal(one.getKey(), e))
						.sum() * calKs(one.getKey());
				// 更新总干扰值
				totalInterVal += interVal;
				// 更新小区与干扰值的映射
				addCellToInterVal(one.getKey(), interVal);
			}
		}

		/**
		 * 计算当前方案的每个小区对应的干扰值
		 * 
		 * @param cellToNcells
		 */
		public void calInterVal(final String cellId) {
			calInterValForOneCell(cellId);
			List<String> sstCells = cellToSameStationOtherCells.get(cellId);
			if (null != sstCells && sstCells.size() < 6) {
				sstCells.parallelStream().filter(e -> !e.equals(cellId)).forEach(e -> calInterValForOneCell(e));
			}
		}

		/**
		 * 计算当前方案的单个小区对应的干扰值
		 * 
		 * @param cellToNcells
		 */
		public void calInterValForOneCell(String cellId) {
			// 计算cell作为out小区时自身的干扰值变化
			changeCellToInterVal(cellId, cellToPci.get(cellId),
					cellToNotSameStationCells.getOrDefault(cellId, new ArrayList<String>()));
			// 计算cell作为in小区时引起主小区的干扰值变化
			ncellToNotSameStationCells.getOrDefault(cellId, new ArrayList<String>()).parallelStream()
					.forEach(e -> changeCellToInterVal(e, cellToPci.get(e),
							cellToNotSameStationCells.getOrDefault(e, new ArrayList<String>())));
		}

		/**
		 * 计算小区新干扰值，并更新相关数据
		 * 
		 * @param cellId 小区
		 * @param pci 小区pci
		 * @param notSameStationCells 小区的非同站小区
		 */
		private void changeCellToInterVal(String cellId, int pci, List<String> notSameStationCells) {
			// 计算干扰值
			double interVal = notSameStationCells.parallelStream()
					.mapToDouble(e -> getModVal(cellId, e, pci) * getRelaVal(cellId, e)).sum() * calKs(cellId);
			// 更新总干扰值
			totalInterVal += interVal - cellToInterVal.get(cellId);
			// 更新小区与干扰值的映射
			addCellToInterVal(cellId, interVal);
		}

		/**
		 *
		 * @param cellId
		 * @param ncellId
		 * @param cellPci
		 * @return
		 */
		private double getModVal(String cellId, String ncellId, int cellPci) {
			int ncellPci = getPciByCell(ncellId);
			if (ncellPci < 0) {
				ncellPci = cellToOriPci.getOrDefault(ncellId, -1);
				if (ncellPci < 0) {
					return 0;
				}
			}
			double result = 0;
			if (cellPci % 3 == (ncellPci % 3)) {
				result += config.getM3r();
			}
			if (cellPci % 6 == (ncellPci % 6)) {
				result += config.getM6r();
			}
			if (cellPci % 30 == (ncellPci % 30)) {
				result += config.getM30r();
			}
			return result;
		}

		private double getRelaVal(String cellId, String ncellId) {
			return cellToNcellAssocDegree.getOrDefault(cellId, new HashMap<String, Double>()).getOrDefault(ncellId,
					0.0);
		}

		public List<String> createTopCellList() {
			return needAssignCellToInterVal.entrySet().parallelStream()
					.sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue())).limit(topSize)
					.collect(ArrayList::new, (l, p) -> l.add(p.getKey()), List::addAll);
		}

		@SuppressWarnings("unchecked")
		public PlanObj clone() {
			PlanObj res = null;
			try {
				res = (PlanObj) super.clone();
				res.cellToPci = (HashMap<String, Integer>) cellToPci.clone();
				res.pciToCell = (HashMap<Integer, List<String>>) pciToCell.clone();
				res.cellToEarfcn = (HashMap<String, Integer>) cellToEarfcn.clone();
				res.earfcnToCell = (HashMap<Integer, List<String>>) earfcnToCell.clone();
				res.cellToInterVal = new ConcurrentHashMap<String, Double>(cellToInterVal);
				res.needAssignCellToInterVal = new ConcurrentHashMap<String, Double>(needAssignCellToInterVal);
				res.d1CellList = (ArrayList<String>) d1CellList.clone();
				res.d2CellList = (ArrayList<String>) d2CellList.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return res;
		}
	}

	class AssignResult {
		private boolean updated;
		private List<String> assignCells;
		private List<Integer> bestPcis;

		public AssignResult(boolean updated, List<String> assignCells, List<Integer> bestPcis) {
			super();
			this.updated = updated;
			this.assignCells = assignCells;
			this.bestPcis = bestPcis;
		}

		public boolean isUpdated() {
			return updated;
		}

		public void setUpdated(boolean updated) {
			this.updated = updated;
		}

		public List<String> getAssignCells() {
			return assignCells;
		}

		public void setAssignCells(List<String> assignCells) {
			this.assignCells = assignCells;
		}

		public List<Integer> getBestPcis() {
			return bestPcis;
		}

		public void setBestPcis(List<Integer> bestPcis) {
			this.bestPcis = bestPcis;
		}
	}

	class SmallParameter {
		private final Map<String, List<String>> cellToNotSameStationCells = new HashMap<String, List<String>>();
		private final Map<String, Map<String, Double>> cellToNcellAssocDegree = new HashMap<String, Map<String, Double>>();
		private final Map<String, List<String>> ncellToNotSameStationCells = new HashMap<String, List<String>>();
		private final Map<String, Map<String, Double>> ncellToCellAssocDegree = new HashMap<String, Map<String, Double>>();
		private final Map<String, Integer> cellToPci = new HashMap<String, Integer>();

		public Map<String, List<String>> getCellToNotSameStationCells() {
			return cellToNotSameStationCells;
		}

		public Map<String, Map<String, Double>> getCellToNcellAssocDegree() {
			return cellToNcellAssocDegree;
		}

		public Map<String, List<String>> getNcellToNotSameStationCells() {
			return ncellToNotSameStationCells;
		}

		public Map<String, Map<String, Double>> getNcellToCellAssocDegree() {
			return ncellToCellAssocDegree;
		}

		public Map<String, Integer> getCellToPci() {
			return cellToPci;
		}
	}
}