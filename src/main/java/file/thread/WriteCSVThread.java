package file.thread;

import file.utils.MyFileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by HWL on 2018/8/18
 */
public class WriteCSVThread implements Runnable{
    private String head;
    private List<Map<String, String>> data;
    private File outPutFile;

    public WriteCSVThread(String head, List<Map<String, String>> data, File outPutFile) {
        this.head = head;
        this.data = data;
        this.outPutFile = outPutFile;
    }

    @Override
    public void run() {
        MyFileUtils.writeCSV2(head, data, outPutFile);
        MyFileUtils.zip("F:\\zzzJavaDemo\\zip_test", "F:\\zzzJavaDemo", "zipFile");
    }
}
