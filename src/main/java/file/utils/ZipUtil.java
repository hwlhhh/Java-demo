package file.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtil {
    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    public static void main(String[] args) {


        String dir = "E:\\file_csv2";
        String zippath = "F:\\ziptest\\file_csv.zip";
        ZipUtil.zip(dir, zippath);

        String unzipfile = "F:\\ziptest\\file_csv.zip";
        String unzipdir = "F:\\ziptest\\file_csv";
        ZipUtil.unzip(new File(unzipfile), new File(unzipdir));

        System.out.println("success!");
    }

    /**
     * zip压缩文件
     *
     * @param srcDir
     * @param zipDir
     */
    public static void zip(String srcDir, String zipDir) {
        List<String> files = getFiles(srcDir);
        compressFilesZip(files.toArray(new String[files.size()]), srcDir, zipDir);
    }

    /**
     * 递归获取当前目录下所有文件和文件夹
     *
     * @param dir
     * @return
     */
    private static List<String> getFiles(String dir) {
        List<String> fileList = new ArrayList<>();
        return getFiles(new File(dir), fileList);
    }

    private static List<String> getFiles(File baseFile, List<String> fileList) {
        if (baseFile.exists()) {
            File[] files = baseFile.listFiles();
            if (files.length == 0) {
                fileList.add(baseFile.getAbsolutePath());
            } else {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getFiles(file, fileList);
                    } else {
                        String str = file.getAbsolutePath();
                        fileList.add(str);
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * 文件名处理
     *
     * @param basePath
     * @param filePath
     * @return
     */
    private static String getFilePathName(String basePath, String filePath) {
        String p = filePath.replace(basePath + File.separator, "");
        p = p.replace("\\", "/");
        return p;
    }

    /**
     * 把文件压缩成zip格式
     *
     * @param files       需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径,如"D:/test/aa.zip";
     */
    private static void compressFilesZip(String[] files, String basePath, String zipFilePath) {
        if (files == null || files.length <= 0) {
            return;
        }
        ZipArchiveOutputStream zaos = null;
        try {
            File zipFile = new File(zipFilePath);
            zaos = new ZipArchiveOutputStream(zipFile);
            zaos.setUseZip64(Zip64Mode.AsNeeded);
            zaos.setEncoding("UTF-8");
            for (String filePath : files) {
                File file = new File(filePath);
                String name = getFilePathName(basePath, filePath);
                ZipArchiveEntry entry = new ZipArchiveEntry(file, name);
                // 注意：如果是空目录直接zos.putNextEntry(new ZipEntry(baseDir + dir.getName() + File.separator))
                // 并不用写入文件内容，其中最主要的涉及到目录的压缩的，
                // 就是这一句话  out.putNextEntry(new ZipEntry(base + "/")); //放入一级目录 (防止空目录被丢弃)
                zaos.putArchiveEntry(entry);
                if (file.isDirectory()) {
                    continue;
                }
                BufferedInputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(file));

//                    FileInputStream in = new FileInputStream(file);
//                    in.getChannel()

//                    byte[] buffer = new byte[1024];
//                    int len = -1;
//                    while ((len = is.read(buffer)) != -1) {
//                        //把缓冲区的字节写入到ZipArchiveEntry
//                        zaos.write(buffer, 0, len);
//                    }
                    IOUtils.copy(in, zaos);
                    zaos.closeArchiveEntry();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
//            if (entry != null) {
//                try {
//                    zaos.closeArchiveEntry();
//                } catch (IOException e) {
//                    logger.warn(e.getMessage());
//                }
//            }
            if (zaos != null) {
                try {
                    zaos.finish();
                    zaos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    /**
     * 把zip文件解压到指定文件夹
     *
     * @param unzipfile
     * @param unzipdir
     */
    public static void unzip(File unzipfile, File unzipdir) {
        byte[] buf = new byte[65536];
        ZipArchiveInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new ZipArchiveInputStream(new FileInputStream(unzipfile), "utf-8");
            ZipArchiveEntry zipArchiveEntry;
            while ((zipArchiveEntry = in.getNextZipEntry()) != null) {
                if (in.canReadEntryData(zipArchiveEntry)) {
                    File file = new File(unzipdir.getAbsolutePath() + File.separator + zipArchiveEntry.getName());
                    if (zipArchiveEntry.isDirectory()) {
                        file.mkdirs();
                        continue;
                    }
                    File fileParent = file.getParentFile();
                    if (!fileParent.canExecute()) {
                        fileParent.mkdirs();
                    }
                    out = new BufferedOutputStream(new FileOutputStream(file));
                    int n = -1;
                    while ((n = in.read(buf)) != -1) {
                        out.write(buf, 0, n);
                    }
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (Exception e) {
            logger.error("解压异常：" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

}
