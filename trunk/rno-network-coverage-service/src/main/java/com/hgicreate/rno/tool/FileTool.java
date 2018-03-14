package com.hgicreate.rno.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
@Component
public class FileTool {

	@Autowired
	@Qualifier("hadoopConfig")
	private Configuration config ;
	
	public FileTool() {
		// TODO Auto-generated constructor stub
	}
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
			cell.setCellValue((Double) res.get(i).get("oriInterVal"));
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
			cell1.setCellValue(res.get(i).get("cellId").toString());
			cell1 = row1.createCell(2);
			cell1.setCellValue(Double.parseDouble(res.get(i).get("asso").toString()));
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
	public File getFile(String pathStr) {
		File file = null;
		if (pathStr.startsWith("hdfs://")) {
			URI uri = URI.create(pathStr);
			Path hdfsPath = new Path(uri);
			try {
				FileSystem fs = hdfsPath.getFileSystem(new Configuration());
				if(!fs.exists(hdfsPath)){
					return null;
				}
				String localDst = "/tmp/"
						+ UUID.randomUUID().toString().replaceAll("-", "")
						+ hdfsPath.getName();
				Path localPath = new Path(localDst);
				fs.copyToLocalFile(hdfsPath, localPath);
				file = new File(localDst);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else if (pathStr.startsWith("file:///")) {
			pathStr = pathStr.substring("file:///".length());
			file = new File(pathStr);
		} else {
			file = new File(pathStr);// local
		}
		return file;
	}
	/**
	 * 
	 * @title 在项目的数据目录创建4g方位角计算任务结果excel文件
	 * @param fileRealPath
	 * @param res
	 * @return
	 * @author chao.xj
	 * @date 2015-4-29下午4:28:54
	 * @company 怡创科技
	 * @version 2.0.1
	 */
	public boolean create4GAzimuthExcelFile(String fileRealPath, List<Map<String,Object>> res) {
		
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
		cell.setCellValue("现网方向角");
		cell = row.createCell(3);
		cell.setCellValue("计算方向角");
		cell = row.createCell(4);
		cell.setCellValue("方向角差值");
        //最终写入文件
        try {
        	for (int i = 0; i < res.size(); i++) {
    			row = sheet.createRow(i + 1);
    			cell = row.createCell(0);
    			cell.setCellValue((res.get(i).get("cellName")==null?"":res.get(i).get("cellName").toString()));
    			cell = row.createCell(1);
    			cell.setCellValue(res.get(i).get("cellId").toString());
    			cell = row.createCell(2);
    			cell.setCellValue(res.get(i).get("oldAzimuth").toString());
    			cell = row.createCell(3);
    			cell.setCellValue(res.get(i).get("newAzimuth").toString());
    			cell = row.createCell(4);
    			cell.setCellValue(res.get(i).get("azimuthDiff").toString());
    		}
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				fos.flush();
				fos.close();
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
        
        return true;
	}
}
