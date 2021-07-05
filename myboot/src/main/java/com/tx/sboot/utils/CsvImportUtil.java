package com.tx.sboot.utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author hxs
 * @Date 2021/4/26 15:21
 * @Description
 * @Version 1.0
 */
public class CsvImportUtil {

    //铁矿石
    public final static String IRON_ORE = "2601";
    //生铁
    public final static  String PIG_IRON = "2821，7201，7202，7203";
    //粗钢
    public final static  String CRUDE_STEEL = "7206，7207";
    //钢材
    public final static String STEEL = "7208，7209，7210，7211，7212，7213，7214，7215，7216，7217，7218，7219，7220，7221，7222，7223，7224，7225，7226，7227，7228，7229";
    //钢铁制品
    public  final static String STEEL_PRODUCTS = "7301，7302，7303，7304，7305，7306，7307，7308，7309，7310，7311，7312，7313，7314，" +
            "7315，7316，7317，7318，7319，7320，7321，7322，7323，7324，7325，7326，9406，6603，" +
            "8201，8202，8203，8204，8205，" +
            "8206，8207，8208，8209，8210，8211，8212，8213，8214，8215，" +
            "8301，8302，8303，8304，8305，8306，8307，8308，8309，8310，8311，8401，8402，8403，8404，8405，8406，8407，8408，" +
            "8409，8410，8411，8412，8413，8414，8415，8416，8417，8418，8419，8420，" +
            "8421，8422，8423，8424，8425，8426，8427，8428，8429，8430，8431，8432，" +
            "8433，8434，8435，8436，8437，8438，8439，8440，8441，8442，8443，8444，8445，8446，8447，8448，8449，8450，8451，8452，8453，8454，" +
            "8455，8456，8457，8458，8459，8460，8461，8462，8463，8464，8465，8466，8467，8468，8469，8470，8471，8472，" +
            "8473，8474，8475，8476，8477，8478，8479，8480，" +
            "8481，8482，8483，8484，" +
            "8486，8487，9506，8501，8502，8503，8504，8505，8508，8509，8510，" +
            "8511，8512，8513，8514，8515，8516，8517，8522，8525，8526，8601，8602，8603，8604，8605，" +
            "8606，8607，8608，8609，8701，8702，8703，8704，8705，8706，8707，8708，8709，8710，" +
            "8711，8712，8713，8714，8715，8716，8801，8802，8803，8804，8805，8901，8902，8903，8904，8905，" +
            "8906，9003，9004，9005，9006，9007，9008，9009，9010，9011，9012，9013，9014，9015，9016，9017，9018，9019，9020，" +
            "9021，9022，9023，9024，9025，9026，9027，9028，9029，9030，" +
            "9031，9032，9033，9301，9302，9303，9304，9305，9306，9307";
    //废料
    public final static  String SCRAP = "2618，2619，7204，7205";

    //行尾分隔符定义
    private final static String NEW_LINE_SEPARATOR = "\n";
    //上传文件的存储位置
    public final static URL PATH = Thread.currentThread().getContextClassLoader().getResource("");

    /**
     * @return File
     * @Description 创建CSV文件
     * @Param fileName 文件名，head 表头，values 表体
     **/
    public static File makeTempCSV(String fileName, String[] head, List<String[]> values) throws IOException {
//        创建文件
        File file = File.createTempFile(fileName, ".csv", new File(PATH.getPath()));
        CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

        BufferedWriter bufferedWriter =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        CSVPrinter printer = new CSVPrinter(bufferedWriter, formator);

//        写入表头
        printer.printRecord(head);

//        写入内容
        for (String[] value : values) {
            printer.printRecord(value);
        }

        printer.close();
        bufferedWriter.close();
        return file;
    }

    /**
     * 把csv生成到指定路径
     * @param fileName
     * @param head
     * @param values
     * @param path
     * @return
     * @throws IOException
     */
    public static File makeTempCSVToPath(String fileName, String[] head, List<Object[]> values,String path) throws IOException {
//        创建文件
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = File.createTempFile(fileName, ".csv", dir);
        CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        BufferedWriter bufferedWriter =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        CSVPrinter printer = new CSVPrinter(bufferedWriter, formator);
//        写入表头
        if(head != null){
            printer.printRecord(head);
        }

//        写入内容
        for (Object[] value : values) {
            printer.printRecord(value);
        }
        printer.close();
        bufferedWriter.close();
        return file;
    }

    /**
     * @return boolean
     * @Description 下载文件
     * @Param response，file
     **/
    public static boolean downloadFile(HttpServletResponse response, File file) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
        try {
            String fileName = new String(file.getName().getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            os = response.getOutputStream();
            //MS产本头部需要插入BOM
            //如果不写入这几个字节，会导致用Excel打开时，中文显示乱码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            byte[] buffer = new byte[1024];
            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //关闭流
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
        }
        return false;
    }

    /**
     * @return File
     * @Description 上传文件
     * @Param multipartFile
     **/
    public static File uploadFile(MultipartFile multipartFile) {
        String path = PATH.getPath() + multipartFile.getOriginalFilename();
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            return null;
        }

    }

    /**
     * @return List<List<String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<List<String>> readCSV(String filePath, int colNum) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);
//          表内容集合，外层List为行的集合，内层List为字段集合
            List<List<String>> values = new ArrayList<>();
            int rowIndex = 0;

            for (CSVRecord record : parser.getRecords()) {
//              跳过表头
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
//              每行的内容
                List<String> value = new ArrayList<>();
                for (int i = 0; i < record.size(); i++) {
                    value.add(record.get(i));
                }
                values.add(value);
                rowIndex++;
            }
            return values;
        } catch (IOException e) {

        }finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
