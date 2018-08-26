package file;

import file.thread.WriteCSVThread;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by HWL on 2018/8/15
 */
public class FileTest {

    public static void main(String[] args) {

        Format ft = new SimpleDateFormat("yyyyMMddhhmmss");
        String timestamp = ft.format(new Date());

        String head = "行号,交易日期,交易流水号,账务日期,支付指令标识,商户号,行号,交易日期,交易流水号,账务日期,支付指令标识,商户号";

        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 50000; i++) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("rowNum", i + "");
            map.put("createdDate", "2018-08-15");
            map.put("transactionId", "10000099");
            map.put("checkDate", "2018-06-15");
            map.put("threadName", "FileTest");
            map.put("businessPartnerId", "1002");
            map.put("rowNum2", i + "");
            map.put("createdDate2", "2018-08-15");
            map.put("transactionId2", "10000099");
            map.put("checkDate2", "2018-06-15");
            map.put("threadName2", "FileTest");
            map.put("businessPartnerId2", "1002");
            data.add(map);
        }

//        String filePath = "E:/file_csv/2/3/";
        String filePath = "F:\\zzzJavaDemo\\zip_test";
        String fileName1 = "file1_A.csv";
        String fileName2 = "file2_B.csv";
        File file1 = new File(filePath + File.separator + fileName1);
        File file2 = new File(filePath + File.separator + fileName2);

        WriteCSVThread rt1 = new WriteCSVThread(head, data, file1);
        WriteCSVThread rt2 = new WriteCSVThread(head, data, file2);

        Thread t1 = new Thread(rt1);
        t1.setName("A");
        Thread t2 = new Thread(rt2);
        t2.setName("B");
        t1.start();
        t2.start();

//        MyFileUtils.deleteDir("E:/file_csv");
    }
}
