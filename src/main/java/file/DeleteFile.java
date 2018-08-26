package file;

import file.utils.MyFileUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by HWL on 2018/8/15
 */
public class DeleteFile {


    public static void main(String[] args) {
        String filePath = "E:/file_csv";

        MyFileUtils.deleteDir(filePath);

    }

}
