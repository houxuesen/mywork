package com.tx.sboot.utils;

import com.tx.sboot.vo.CodeFileVo;
import com.tx.sboot.vo.CodeTestVo;
import com.tx.sboot.vo.CountryVo;
import com.tx.sboot.vo.DetailFileVo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author hxs
 * @Date 2021/4/25 15:28
 * @Description
 * @Version 1.0
 */
public class ExcelUtils {

    public static List<DetailFileVo> excelToDetailFileList(InputStream inputStream){
        List<DetailFileVo> objects = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();

            Sheet sheet = workbook.getSheetAt(0);

            int rowLength = sheet.getLastRowNum();
            System.out.println("总行数有多少行" + rowLength);
            Row row = sheet.getRow(0);

            int colLength = row.getLastCellNum();
            System.out.println("总列数有多少列" + colLength);

            Cell cell = row.getCell(0);
            for (int i = 1; i < rowLength + 1 ; i++) {
                row = sheet.getRow(i);
                DetailFileVo  detailFileVo = new DetailFileVo();
                for (int j = 0; j < colLength ; j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        if(j == 0){
                            detailFileVo.setYear(data);
                        }else if(j == 1){
                            detailFileVo.setTradeFlow(data);
                        }else if(j == 2){
                            detailFileVo.setReporter(data);
                        }else if(j == 3){
                            detailFileVo.setPartner(data);
                        }else if(j == 4){
                            detailFileVo.setCode(data);
                        }else if(j == 7){
                            detailFileVo.setNetWeight(StringUtils.isEmpty(data) ? null : Double.parseDouble(data));
                        }else if(j == 8){
                            detailFileVo.setUnit(data);
                        }
                    }
                }
                objects.add(detailFileVo);

            }
        } catch (Exception e) {

        }
        return objects;
    }


    public static List<Object[]> excelToList(InputStream inputStream){
        ArrayList<Object[]> objects = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();

            Sheet sheet = workbook.getSheetAt(0);
            int rowLength = sheet.getLastRowNum();
            System.out.println("总行数有多少行" + rowLength);
            Row row = sheet.getRow(0);

            int colLength = row.getLastCellNum();
            System.out.println("总列数有多少列" + colLength);

            Cell cell = row.getCell(0);
            for (int i = 0; i < rowLength + 1 ; i++) {
                row = sheet.getRow(i);
                Object[] objects1 = new Object[colLength];
                for (int j = 0; j < colLength ; j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        objects1[j] = data;
                    }
                }
                objects.add(objects1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return objects;
    }


    public static CodeTestVo excelToCodeFileList(InputStream inputStream){
        CodeTestVo codeTestVo = new CodeTestVo();
        List<CodeFileVo> codeFileVoList = new ArrayList<>();
        List<CountryVo> countryVoList  = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();

            Sheet sheet = workbook.getSheetAt(0);
            int rowLength = sheet.getLastRowNum();
            System.out.println("总行数有多少行" + rowLength);
            Row row = sheet.getRow(0);

            int colLength = row.getLastCellNum();
            System.out.println("总列数有多少列" + colLength);

            Cell cell = row.getCell(0);
            for (int i = 0; i < rowLength + 1 ; i++) {
                row = sheet.getRow(i);
                CodeFileVo  codeFileVo = new CodeFileVo();
                for (int j = 0; j < colLength ; j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        if(j == 0){
                            codeFileVo.setCode(data);
                        }else if(j == 1){
                            codeFileVo.setCoefficient(Double.valueOf(data));
                        }
                    }
                    codeFileVoList.add(codeFileVo);
                }
            }

            codeTestVo.setCodeFileVoList(codeFileVoList);

            Sheet sheet1 = workbook.getSheetAt(1);
            int rowLength1 = sheet1.getLastRowNum();
            System.out.println("总行数有多少行" + rowLength1);
            Row row1 = sheet1.getRow(0);

            int colLength1 = row1.getLastCellNum();
            System.out.println("总列数有多少列" + colLength1);

            Cell cell1 = row1.getCell(0);
            for (int i = 0; i < rowLength1 + 1 ; i++) {
                row1 = sheet1.getRow(i);
                CountryVo  countryVo = new CountryVo();
                for (int j = 0; j < colLength1 ; j++) {
                    cell1 = row1.getCell(j);
                    if (cell1 != null) {
                        cell1.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell1.getStringCellValue();
                        data = data.trim();
                        if(j == 0){
                            countryVo.setCountryName(data);
                        }else if(j == 1){
                            countryVo.setCountryEName(data);
                        }
                    }
                    countryVoList.add(countryVo);
                }
            }
            codeTestVo.setCountryVoList(countryVoList);
        } catch (Exception e) {

        }
        return codeTestVo;
    }

}
