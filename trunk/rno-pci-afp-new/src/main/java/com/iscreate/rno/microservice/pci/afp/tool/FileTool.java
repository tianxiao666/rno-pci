package com.iscreate.rno.microservice.pci.afp.tool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class FileTool {

	/**
	 * 在项目的数据目录创建Pci结果excel文件
	 * 
	 * @param fileRealPath
	 * @param res
	 * @return
	 */
	public static boolean createExcelFile(String fileRealPath, List<Map<String, Object>> res) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileRealPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row;
		Cell cell;
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("小区名称");
		cell = row.createCell(1);
		cell.setCellValue("小区标识");
		cell = row.createCell(2);
		cell.setCellValue("原频点");
		cell = row.createCell(3);
		cell.setCellValue("新频点");
		cell = row.createCell(4);
		cell.setCellValue("原PCI");
		cell = row.createCell(5);
		cell.setCellValue("新PCI");
		cell = row.createCell(6);
		cell.setCellValue("原干扰值");
		cell = row.createCell(7);
		cell.setCellValue("干扰值");
		cell = row.createCell(8);
		cell.setCellValue("备注");
		for (int i = 0; i < res.size(); i++) {
			row = sheet.createRow(i + 1);
			// 小区名称
			cell = row.createCell(0);
			cell.setCellValue(res.get(i).get("cellName").toString());
			// 小区标识
			cell = row.createCell(1);
			cell.setCellValue(res.get(i).get("cellId").toString());
			// 原频点
			cell = row.createCell(2);
			cell.setCellValue((Integer) res.get(i).get("oldEarfcn"));
			// 新频点
			cell = row.createCell(3);
			if (isNumeric(res.get(i).get("newEarfcn").toString())) {
				cell.setCellValue((Integer) res.get(i).get("newEarfcn"));
			} else {
				cell.setCellValue(res.get(i).get("newEarfcn").toString());
			}
			// 原pci
			cell = row.createCell(4);
			cell.setCellValue((Integer) res.get(i).get("oldPci"));
			// 新pci
			cell = row.createCell(5);
			if (isNumeric(res.get(i).get("newPci").toString())) {
				cell.setCellValue((Integer) res.get(i).get("newPci"));
			} else {
				cell.setCellValue(res.get(i).get("newPci").toString());
			}
			// 原干扰值
			cell = row.createCell(6);
			cell.setCellValue((Double)res.get(i).get("oriInterVal"));
			// 新干扰值
			cell = row.createCell(7);
			cell.setCellValue((Double) res.get(i).get("interVal"));
			// 备注
			cell = row.createCell(8);
			cell.setCellValue(res.get(i).get("remark").toString());
		}
		// 最终写入文件
		try {
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 在项目的数据目录创建关联度排序表excel文件
	 * 
	 * @param fileRealPath
	 * @param res
	 * @return
	 */
	public static boolean createAssoTableToExcel(String fileRealPath, List<Map<String, Object>> res) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileRealPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet1 = workbook.createSheet();
		Row row1;
		Cell cell1;
		row1 = sheet1.createRow(0);
		cell1 = row1.createCell(0);
		cell1.setCellValue("排序号");
		cell1 = row1.createCell(1);
		cell1.setCellValue("小区标识");
		cell1 = row1.createCell(2);
		cell1.setCellValue("关联度");
		cell1 = row1.createCell(3);
		for (int i = 0; i < res.size(); i++) {
			row1 = sheet1.createRow((short) i + 1);
			cell1 = row1.createCell(0);
			cell1.setCellValue(i + 1);
			cell1 = row1.createCell(1);
			cell1.setCellValue(res.get(i).get("CELL_ID").toString());
			cell1 = row1.createCell(2);
			cell1.setCellValue(Double.parseDouble(res.get(i).get("RELA_VAL").toString()));
		}
		// 最终写入文件
		try {
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 在项目的数据目录创建PCI优化中间方案excel文件
	 * 
	 * @param fileRealPath
	 * @param res
	 * @return
	 */
	public static boolean createMidPlanToExcel(String fileRealPath, List<Map<String, Object>> res,
			List<Map<String, Object>> res2) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileRealPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet1 = workbook.createSheet();
		Sheet sheet2 = workbook.createSheet();
		Row row1, row2;
		Cell cell1, cell2;
		row1 = sheet1.createRow(0);
		cell1 = row1.createCell(0);
		cell1.setCellValue("小区名称");
		cell1 = row1.createCell(1);
		cell1.setCellValue("小区标识");
		cell1 = row1.createCell(2);
		cell1.setCellValue("原频点");
		cell1 = row1.createCell(3);
		cell1.setCellValue("新频点");
		cell1 = row1.createCell(4);
		cell1.setCellValue("原PCI");
		cell1 = row1.createCell(5);
		cell1.setCellValue("新PCI");
		cell1 = row1.createCell(6);
		cell1.setCellValue("原干扰值");
		cell1 = row1.createCell(7);
		cell1.setCellValue("干扰值");
		cell1 = row1.createCell(8);
		cell1.setCellValue("备注");
		row2 = sheet2.createRow(0);
		cell2 = row2.createCell(0);
		cell2.setCellValue("排序号");
		cell2 = row2.createCell(1);
		cell2.setCellValue("小区标识");
		cell2 = row2.createCell(2);
		cell2.setCellValue("干扰值");
		for (int i = 0; i < res.size(); i++) {
			row1 = sheet1.createRow(i + 1);
			// 小区名称
			cell1 = row1.createCell(0);
			cell1.setCellValue(res.get(i).get("cellName").toString());
			// 小区标识
			cell1 = row1.createCell(1);
			cell1.setCellValue(res.get(i).get("cellId").toString());
			// 原频点
			cell1 = row1.createCell(2);
			cell1.setCellValue((Integer) res.get(i).get("oldEarfcn"));
			// 新频点
			cell1 = row1.createCell(3);
			if (isNumeric(res.get(i).get("newEarfcn").toString())) {
				cell1.setCellValue((Integer) res.get(i).get("newEarfcn"));
			} else {
				cell1.setCellValue(res.get(i).get("newEarfcn").toString());
			}
			// 原pci
			cell1 = row1.createCell(4);
			cell1.setCellValue((Integer) res.get(i).get("oldPci"));
			// 新pci
			cell1 = row1.createCell(5);
			if (isNumeric(res.get(i).get("newPci").toString())) {
				cell1.setCellValue((Integer) res.get(i).get("newPci"));
			} else {
				cell1.setCellValue(res.get(i).get("newPci").toString());
			}
			// 原干扰值
			cell1 = row1.createCell(6);
			cell1.setCellValue((Double) res.get(i).get("oriInterVal"));
			// 新干扰值
			cell1 = row1.createCell(7);
			cell1.setCellValue((Double) res.get(i).get("interVal"));
			// 备注
			cell1 = row1.createCell(8);
			cell1.setCellValue(res.get(i).get("remark").toString());
		}
		for (int j = 0; j < res2.size(); j++) {
			row2 = sheet2.createRow(j + 1);
			cell2 = row2.createCell(0);
			cell2.setCellValue(j + 1);
			cell2 = row2.createCell(1);
			cell2.setCellValue(res2.get(j).get("cell").toString());
			cell2 = row2.createCell(2);
			cell2.setCellValue(Double.parseDouble(res2.get(j).get("inter").toString()));
		}

		// 最终写入文件
		try {
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
