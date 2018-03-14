package com.hgicreate.rno.lte.pciafp.tool;

import com.hgicreate.rno.lte.pciafp.model.Cell2Rela;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTool {
    public static final String NUMBER_PATTERN = "[0-9]*";
    /**
     * 在项目的数据目录创建Pci结果excel文件
     */
    public static boolean createExcelFile(String fileRealPath, List<Map<String, Object>> res) {
        try (OutputStream os = Files.newOutputStream(Paths.get(fileRealPath));
             Workbook workbook = new SXSSFWorkbook()) {
            createPlan(workbook.createSheet(), res);
            // 最终写入文件
            workbook.write(os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在项目的数据目录创建关联度排序表excel文件
     */
    public static boolean createAssoTableToExcel(String fileRealPath, List<Cell2Rela> res) {
        try (OutputStream os = Files.newOutputStream(Paths.get(fileRealPath));
             Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet1 = workbook.createSheet();
            Row row1;
            Cell cell;
            row1 = sheet1.createRow(0);
            cell = row1.createCell(0);
            cell.setCellValue("排序号");
            cell = row1.createCell(1);
            cell.setCellValue("小区标识");
            cell = row1.createCell(2);
            cell.setCellValue("关联度");
            for (int i = 0; i < res.size(); i++) {
                row1 = sheet1.createRow(i + 1);
                cell = row1.createCell(0);
                cell.setCellValue(i + 1);
                cell = row1.createCell(1);
                cell.setCellValue(res.get(i).getCellId());
                cell = row1.createCell(2);
                cell.setCellValue(res.get(i).getRelaVal());
            }
            // 最终写入文件
            workbook.write(os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在项目的数据目录创建PCI优化中间方案excel文件
     */
    public static boolean createMidPlanToExcel(String fileRealPath, List<Map<String, Object>> res, List<Map<String, Object>> res2) {
        try (OutputStream os = Files.newOutputStream(Paths.get(fileRealPath));
             Workbook workbook = new SXSSFWorkbook()) {

            createPlan(workbook.createSheet(), res);

            Sheet sheet2 = workbook.createSheet();
            Row row2 = sheet2.createRow(0);
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue("排序号");
            cell2 = row2.createCell(1);
            cell2.setCellValue("小区标识");
            cell2 = row2.createCell(2);
            cell2.setCellValue("干扰值");
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
            workbook.write(os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile(NUMBER_PATTERN);
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    private static void createPlan(Sheet sheet, List<Map<String, Object>> res) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
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
    }
}
