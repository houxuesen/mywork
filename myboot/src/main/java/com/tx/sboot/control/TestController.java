package com.tx.sboot.control;

import com.tx.sboot.utils.CsvImportUtil;
import com.tx.sboot.utils.ExcelUtils;
import com.tx.sboot.vo.CodeFileVo;
import com.tx.sboot.vo.CodeTestVo;
import com.tx.sboot.vo.CountryVo;
import com.tx.sboot.vo.DetailFileVo;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author hxs
 * @Date 2020/12/1 14:35
 * @Description
 * @Version 1.0
 */
@Controller
public class TestController {

    @RequestMapping("/read/exl")
    public String exl() {
        return "jq-read-exl";
    }

    @RequestMapping("/upload")
    public String upload() {
        return "upload";
    }


    /**
     * @return
     * @Description 上传CSV
     * @Param file
     **/
    @RequestMapping(value = "file/uploadCsv")
    @ResponseBody
    public String uploadCsv(@RequestParam("scvFile") MultipartFile scvFile) {
        try {
            //上传内容不能为空
            if (scvFile.isEmpty()) {
                return "500";
            }
            File file = CsvImportUtil.uploadFile(scvFile);
            List<List<String>> userRoleLists = CsvImportUtil.readCSV(file.getPath(), 2);
            file.delete();
            return "200";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "500";
    }



    @RequestMapping("file/upload")
    public void uploadExl(@RequestParam("detailFile") MultipartFile detailFile,
                            @RequestParam("codeFile") MultipartFile codeFile,
                            HttpServletResponse response) throws Exception {
        Date startDate = new Date();
        System.out.println("开始处理==============");
        CodeTestVo codeTestVos = ExcelUtils.excelToCodeFileList(codeFile.getInputStream());
        List<CountryVo> countryVoList = codeTestVos.getCountryVoList();
        List<CodeFileVo> codeFileVoList = codeTestVos.getCodeFileVoList();
        Map<String, String> countryVoMap = countryVoList.stream().collect(Collectors.toMap(CountryVo::getCountryEName, CountryVo::getCountryName,(key1, key2) -> key2));
        Map<String, Double> codeFileVoMap = codeFileVoList.stream().collect(Collectors.toMap(CodeFileVo::getCode, CodeFileVo::getCoefficient,(key1, key2) -> key2));

        String name = detailFile.getOriginalFilename();
        //筛选 Import 和 Export
        List<DetailFileVo> list = ExcelUtils.excelToDetailFileList(detailFile.getInputStream());
        list = list.stream().filter(detailFileVo -> ("Import".equals(detailFileVo.getTradeFlow()) || "Export".equals(detailFileVo.getTradeFlow()))).collect(Collectors.toList());
        //筛选一带一路国家
        List<DetailFileVo> newDetailFileVos = new ArrayList<>();
        list.stream().forEach(detailFileVo -> {
            for (Map.Entry<String, String> it : countryVoMap.entrySet()) {
                if (detailFileVo.getReporter().equals(it.getKey())) {
                    for (Map.Entry<String, String> second : countryVoMap.entrySet()) {
                        if(detailFileVo.getPartner().equals(second.getKey())){
                            newDetailFileVos.add(detailFileVo);
                        }
                    }
                }
            }
        });


        Map<String, DetailFileVo> detailFileVoMap = new HashMap<>();
        newDetailFileVos.forEach(detailFileVo -> {
            //过滤 TradeQuantity NetWeight TradeValue为空
            if ((detailFileVo.getTradeQuantity() == null || detailFileVo.getTradeQuantity() == 0)
                    && (detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)
                    && (detailFileVo.getTradeValue() == null || detailFileVo.getTradeValue() == 0)) {
                return;
            }

            //去重Partner和 code 相同   保留 netWeight 最大值
            if (detailFileVoMap.containsKey(detailFileVo.getPartner() + detailFileVo.getCode())) {
                DetailFileVo dataFile = detailFileVoMap.get(detailFileVo.getPartner() + detailFileVo.getCode());
                if (dataFile.getNetWeight() != null && detailFileVo.getNetWeight() != null) {
                    if (dataFile.getNetWeight() > detailFileVo.getNetWeight()) {
                        detailFileVoMap.put(detailFileVo.getPartner() + detailFileVo.getCode(), dataFile);
                    } else {
                        detailFileVoMap.put(detailFileVo.getPartner() + detailFileVo.getCode(), detailFileVo);
                    }
                }
            } else {
                if ((detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)
                        && "Weight in kilograms".equals(detailFileVo.getUnit()) && detailFileVo.getTradeQuantity() != null
                ) {
                    detailFileVo.setNetWeight(detailFileVo.getTradeQuantity());
                }
                detailFileVoMap.put(detailFileVo.getPartner() + detailFileVo.getCode(), detailFileVo);
            }

        });

        List<DetailFileVo> detailFileVos = new ArrayList<>();
        detailFileVoMap.forEach((s, detailFileVo) -> {
            //补齐
            if (detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0) {
                Double testTotal = 0D;
                Double netWeightTotal = 0D;
                Double tradeValueTotal = 0D;


                for (Map.Entry<String, DetailFileVo> it : detailFileVoMap.entrySet()) {
                    //先求和
                    if (detailFileVo.getCode().equals(it.getValue().getCode())) {
                        netWeightTotal += it.getValue().getNetWeight() != null ? it.getValue().getNetWeight() : 0;
                        tradeValueTotal += it.getValue().getTradeValue() != null ? it.getValue().getTradeValue() : 0;
                    }
                }


                for (Map.Entry<String, Double> it : codeFileVoMap.entrySet()) {
                    if (detailFileVo.getCode().indexOf(it.getKey()) > -1) {
                        testTotal = testTotal * it.getValue();
                    }
                }
                detailFileVo.setNetWeight(testTotal);
            }
            detailFileVos.add(detailFileVo);
        });

        Map<String,DetailFileVo> endMap = new HashMap<>();
        detailFileVos.forEach(detailFileVo -> {
            if(endMap.containsKey(detailFileVo.getYear()+detailFileVo.getReporter()+detailFileVo.getPartner())){
                DetailFileVo df = endMap.get(detailFileVo.getYear()+detailFileVo.getReporter()+detailFileVo.getPartner());
                df.setNetWeight(df.getNetWeight()+detailFileVo.getNetWeight());
                endMap.put(detailFileVo.getYear()+detailFileVo.getReporter()+detailFileVo.getPartner(),df);
            }else{
                endMap.put(detailFileVo.getYear()+detailFileVo.getReporter()+detailFileVo.getPartner(),detailFileVo);
            }
        });


        List<DetailFileVo> monthReportModels = new ArrayList<>();
        for(Map.Entry<String, DetailFileVo> it :endMap.entrySet()){
            monthReportModels.add(it.getValue());
        }
        monthReportModels =  monthReportModels.stream().sorted(Comparator.comparing(DetailFileVo::getReporter)).collect(Collectors.toList());
        exportExcel(response,monthReportModels);
        Date endDate = new Date();
        long l=endDate.getTime()-startDate.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
        System.out.println("处理结束==============");

    }

    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportExcel(HttpServletResponse response, List<DetailFileVo> monthReportModels) {
        // Excel标题
        String[] title = {"Source", "Weight", "Target"};
        // Excel文件名
        String fileName =  "数据处理.xls";
        // sheet名
        String sheetName = "数据处理";
        // 将数据放到数组中
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        String[][] content = new String[monthReportModels.size()][title.length];
        for (int i = 0; i < monthReportModels.size(); i++) {
            DetailFileVo detailFileVo = monthReportModels.get(i);
            content[i][0] = detailFileVo.getReporter();
            content[i][1] = nf.format(detailFileVo.getNetWeight());
            content[i][2] = detailFileVo.getPartner();
        }
        // 导出Excel
        try {
            HSSFWorkbook hssfWorkbook = getHSSFWorkbook(sheetName, title, content, null);
            setResponseHeader(response, fileName);
            OutputStream outputStream = response.getOutputStream();
            hssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook workbook) {
        // 创建一个HSSFWorkbook，对应一个Excel文件
        if (workbook == null) {
            workbook = new HSSFWorkbook();
        }
        // 在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 在sheet中添加表头第0行
        HSSFRow row = sheet.createRow(0);
        // 创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 声明列对象
        HSSFCell cell = null;
        // 创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(cellStyle);
        }
        // 创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                // 将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return workbook;

    }


}
