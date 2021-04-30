package com.tx.sboot.control;

import com.tx.sboot.utils.CsvImportUtil;
import com.tx.sboot.utils.ExcelUtils;
import com.tx.sboot.utils.ZipUtil;
import com.tx.sboot.vo.CodeFileVo;
import com.tx.sboot.vo.CodeTestVo;
import com.tx.sboot.vo.CountryVo;
import com.tx.sboot.vo.DetailFileVo;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

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
    public void uploadCsv(@RequestParam("scvFile") List<MultipartFile> scvFiles,
                            @RequestParam("scvCodeFile") MultipartFile scvCodeFile,
                            HttpServletResponse response) {
        try {
            CodeTestVo codeTestVo = getCodeTestVo(scvCodeFile);
            //上传内容不能为空
            List<File> files = new ArrayList();
            for(MultipartFile scvFile:scvFiles){
                if(scvFile.isEmpty()){
                    continue;
                }
                NumberFormat nf = NumberFormat.getInstance();
                List<DetailFileVo>monthReportModels = getMonthReportModels(codeTestVo,getDetailFileVos(scvFile));
                String[] title = {"Source", "Target", "Weight"};
                String fileName =  scvFile.getOriginalFilename().substring(0,scvFile.getOriginalFilename().lastIndexOf("."))+"数据处理";
                List<String[]> values = new ArrayList<>();
                for(DetailFileVo detailFileVo:monthReportModels){
                    String[] strings = new String[3];
                    strings[0]=detailFileVo.getPartner();
                    strings[1]=detailFileVo.getReporter();
                    strings[2]=nf.format(detailFileVo.getNetWeight());
                    values.add(strings);
                }
                File file =  CsvImportUtil.makeTempCSV(fileName,title,values);
                //CsvImportUtil.downloadFile(response,CsvImportUtil.makeTempCSV(fileName,title,values));
                files.add(file);
            }
            Date date = new Date();
            ZipUtil.downLoadFiles(files,response,"数据处理"+ date.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DetailFileVo> getDetailFileVos(MultipartFile scvFile){
        File file = CsvImportUtil.uploadFile(scvFile);
        List<List<String>> userRoleLists = CsvImportUtil.readCSV(file.getPath(), 11);
        file.delete();
        List<DetailFileVo> list = new ArrayList<>();
        for(List<String> str:userRoleLists){
            DetailFileVo detailFileVo = new DetailFileVo();
            for( int j = 0;j<str.size();j++ ){
                String data = str.get(j).trim();
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
                }else if(j == 6){
                    detailFileVo.setTradeValue(StringUtils.isEmpty(data) ? null : Double.parseDouble(data));
                }else if(j==9){
                    detailFileVo.setTradeQuantity(StringUtils.isEmpty(data) ? null : Double.parseDouble(data));
                }
            }

            list.add(detailFileVo);
        }
        return list;
    }

    private  static  List<DetailFileVo> getValues(File file){
        List<List<String>> userRoleLists = CsvImportUtil.readCSV(file.getPath(), 11);
        List<DetailFileVo> list = new ArrayList<>();
        for(List<String> str:userRoleLists){
            DetailFileVo detailFileVo = new DetailFileVo();
            for( int j = 0;j<str.size();j++ ){
                String data = str.get(j).trim();
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
                }else if(j == 6){
                    detailFileVo.setTradeValue(StringUtils.isEmpty(data) ? null : Double.parseDouble(data));
                }else if(j==9){
                    detailFileVo.setTradeQuantity(StringUtils.isEmpty(data) ? null : Double.parseDouble(data));
                }
            }

            list.add(detailFileVo);
        }
        return list;
    }



    @RequestMapping("file/upload")
    public void uploadExl(@RequestParam("detailFile") MultipartFile detailFile,
                            @RequestParam("codeFile") MultipartFile codeFile,
                            HttpServletResponse response) throws Exception {
        Date startDate = new Date();
        System.out.println("开始处理==============");

        String name = detailFile.getOriginalFilename();
        CodeTestVo codeTestVo = getCodeTestVo(codeFile);
        //筛选 Import 和 Export
        List<DetailFileVo> list = ExcelUtils.excelToDetailFileList(detailFile.getInputStream());
        List<DetailFileVo>monthReportModels =  getMonthReportModels(codeTestVo,list);
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

    public static void main(String[] args) throws IOException {
        NumberFormat nf = NumberFormat.getInstance();
        String path = "F:\\sj\\附件二：系数 +一带一路国家.xlsx";
        InputStream fileInputStream = new FileInputStream(path);
        CodeTestVo codeTestVo = ExcelUtils.excelToCodeFileList(fileInputStream);
        String test_name = "2021";
        String file_path = "F:\\sj\\"+test_name;
        List<File> fileList = getFiles(file_path);
        List<File> files = new ArrayList();
        for(File file_temp:fileList){
            if(file_temp.getName().contains(".csv")){
                List<DetailFileVo>monthReportModels = getMonthReportModels(codeTestVo, getValues(file_temp));
                String[] title = {"Source", "Target", "Weight"};
                String fileName =  file_temp.getName().substring(0,file_temp.getName().lastIndexOf("."))+"数据处理";
                List<String[]> values = new ArrayList<>();
                for(DetailFileVo detailFileVo:monthReportModels){
                    String[] strings = new String[3];
                    strings[0]=detailFileVo.getPartner();
                    strings[1]=detailFileVo.getReporter();
                    strings[2]=nf.format(detailFileVo.getNetWeight());
                    values.add(strings);
                }
                File file =  CsvImportUtil.makeTempCSVToPath(fileName,title,values,"F:\\sj\\sj_zip\\"+test_name);
                //CsvImportUtil.downloadFile(response,CsvImportUtil.makeTempCSV(fileName,title,values));
                files.add(file);
            }
        }
        //文件打入压缩包
        /*   File file = new File("F:\\sj\\sj_zip\\"+test_name+".zip");
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建文件输出流
        FileOutputStream fous = new FileOutputStream(file);
        ZipOutputStream zipOut = new ZipOutputStream(fous);
        ZipUtil.zipFile(files, zipOut);
        zipOut.close();
        fous.close();*/
        System.out.println("------执行结束-----------");
    }

    private CodeTestVo getCodeTestVo(MultipartFile codeFile) throws IOException {
        CodeTestVo codeTestVos = ExcelUtils.excelToCodeFileList(codeFile.getInputStream());
        return codeTestVos;
    }

    public static List<File> getFiles(String path) {
        List<File> files = new ArrayList<File>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }

        }
        return files;
    }


    private static   List<DetailFileVo> getMonthReportModels(CodeTestVo codeTestVos,List<DetailFileVo> list) throws IOException {
        List<CountryVo> countryVoList = codeTestVos.getCountryVoList();
        List<CodeFileVo> codeFileVoList = codeTestVos.getCodeFileVoList();
        Map<String, String> countryVoMap = countryVoList.stream().collect(Collectors.toMap(CountryVo::getCountryEName, CountryVo::getCountryName,(key1, key2) -> key2));
        Map<String, Double> codeFileVoMap = codeFileVoList.stream().collect(Collectors.toMap(CodeFileVo::getCode, CodeFileVo::getCoefficient,(key1, key2) -> key2));
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

        for(DetailFileVo detailFileVo:newDetailFileVos){
            if("Export".equals(detailFileVo.getTradeFlow())){
                //partner 和 reporter
                String partner = detailFileVo.getPartner();
                String reporter = detailFileVo.getReporter();
                detailFileVo.setPartner(reporter);
                detailFileVo.setReporter(partner);
            }
        }


        Map<String, DetailFileVo> detailFileVoMap = new HashMap<>();
        newDetailFileVos.forEach(detailFileVo -> {
            //去重Partner和 code 相同   保留 netWeight 最大值
            String key = detailFileVo.getReporter() +"-"+ detailFileVo.getPartner()+"-" + detailFileVo.getCode();
            if (detailFileVoMap.containsKey(key)) {
                DetailFileVo dataFile = detailFileVoMap.get(key);
                if (dataFile.getNetWeight() != null && detailFileVo.getNetWeight() != null) {
                    if (dataFile.getNetWeight() > detailFileVo.getNetWeight()) {
                        detailFileVoMap.put(key, dataFile);
                    } else {
                        detailFileVoMap.put(key, detailFileVo);
                    }
                }else if((detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)
                        && (dataFile.getNetWeight() == null || dataFile.getNetWeight() == 0)){
                    if("Weight in kilograms".equals(detailFileVo.getUnit())
                            && !"Weight in kilograms".equals(dataFile.getUnit()) ){
                        detailFileVoMap.put(key, detailFileVo);
                    }else if(!"Weight in kilograms".equals(detailFileVo.getUnit())
                            && "Weight in kilograms".equals(dataFile.getUnit()) ){
                        detailFileVoMap.put(key, dataFile);
                    }else if("Weight in kilograms".equals(detailFileVo.getUnit())
                            && "Weight in kilograms".equals(dataFile.getUnit())){
                        if (dataFile.getTradeQuantity() > detailFileVo.getTradeQuantity()) {
                            detailFileVoMap.put(key, dataFile);
                        } else {
                            detailFileVoMap.put(key, detailFileVo);
                        }
                    }
                } else {
                    if(dataFile.getNetWeight() != null
                            && (detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)){
                        detailFileVoMap.put(key, dataFile);
                    }else if(detailFileVo.getNetWeight() != null
                            && (dataFile.getNetWeight() == null || dataFile.getNetWeight() == 0)){
                        detailFileVoMap.put(key, detailFileVo);
                    }
                }
            } else {
                detailFileVoMap.put(key, detailFileVo);
            }
        });

        //过滤数据

        List<DetailFileVo>  dfVo = new ArrayList<>();
        detailFileVoMap.forEach((s, detailFileVo) -> {
            //若“Trade Quantity”和“Net Weight”均为0或空白，且“Trade Value”数据也为0或空白，删除
            if(!((detailFileVo.getTradeQuantity() == null || detailFileVo.getTradeQuantity() == 0)
                    && (detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)
                    && (detailFileVo.getTradeValue() == null || detailFileVo.getTradeValue() == 0))){
                //将“Net Weight”为0或空白的，“Unit”为“Weight in kilograms”，
                // “Trade Quantity”（三者同时满足）的数据，用“Trade Quantity”数据替换“Net Weight”数据，达到补全一部分数据的效果。
                if ((detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0)
                        && "Weight in kilograms".equals(detailFileVo.getUnit())
                        && (detailFileVo.getTradeQuantity() != null && detailFileVo.getTradeQuantity() != 0)
                ) {
                    detailFileVo.setNetWeight(detailFileVo.getTradeQuantity());
                }
                dfVo.add(detailFileVo);
            }else{
                System.out.println(s + detailFileVo.getTradeFlow());
            }
        });



        List<DetailFileVo> detailFileVos = new ArrayList<>();
        dfVo.forEach(detailFileVo -> {
            //补齐
            if (detailFileVo.getNetWeight() == null || detailFileVo.getNetWeight() == 0) {
                Double testTotal = 0D;
                Double netWeightTotal = 0D;
                Double tradeValueTotal = 0D;

                for (DetailFileVo it : dfVo) {
                    //先求和
                    if (detailFileVo.getCode().equals(it.getCode())) {
                        netWeightTotal += it.getNetWeight() != null ? it.getNetWeight() : 0;
                        if(it.getNetWeight() != null && it.getNetWeight() != 0){
                            tradeValueTotal += it.getTradeValue() != null ? it.getTradeValue() : 0;
                        }
                    }
                }

                if(netWeightTotal != 0 &&tradeValueTotal != 0  ){
                    testTotal =  netWeightTotal / tradeValueTotal * detailFileVo.getTradeValue();
                    for (Map.Entry<String, Double> it : codeFileVoMap.entrySet()) {
                        if (detailFileVo.getCode().contains(it.getKey())) {
                            testTotal = testTotal * it.getValue();
                        }
                    }
                    detailFileVo.setNetWeight(testTotal);
                }

            }else{
                for (Map.Entry<String, Double> it : codeFileVoMap.entrySet()) {
                    if (detailFileVo.getCode().contains(it.getKey())) {
                        detailFileVo.setNetWeight(detailFileVo.getNetWeight() *  it.getValue());
                    }
                }

            }
            detailFileVos.add(detailFileVo);
        });

        Map<String,DetailFileVo> endMap = new HashMap<>();
        detailFileVos.forEach(detailFileVo -> {
            if(detailFileVo.getReporter().equals(detailFileVo.getPartner())){
                return;
            }
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
        monthReportModels =  monthReportModels.stream().sorted(Comparator.comparing(DetailFileVo::getPartner)).sorted(Comparator.comparing(DetailFileVo::getReporter)).collect(Collectors.toList());
        return monthReportModels;
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
