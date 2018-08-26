package file.utils;


import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

/**
 * Created by HWL on 2018/8/15
 */
public class MyFileUtils {

    private static Logger logger = LoggerFactory.getLogger(MyFileUtils.class);


    /**
     * @param title
     * @param data
     * @param file
     * @return
     */
    public static boolean writeCSV1(String title, List<Map<String, String>> data, File file) {
        long startTime = System.currentTimeMillis();
        boolean flag = false;
        BufferedWriter bw = null;
        try {
            File parentFilePath = file.getParentFile();
            if (!parentFilePath.exists()) {
                parentFilePath.mkdirs();
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            logger.info("线程" + Thread.currentThread().getName() + "准备将数据写入CSV文件" + file.toString() + " ...");
            bw.append(title).append("\r\n");
            bw.flush();
            int rowNum = 0;
            int colNum = title.split(",").length;
            int index = 0;
            if (data != null && !data.isEmpty()) {
                for (Map<String, String> row : data) {
                    rowNum++;
                    for (String col : row.values()) {
                        if (index < colNum - 1) {
                            bw.append(col).append(",");
                        } else {
                            bw.append(col).append("\r\n");
                        }
                        index++;
                    }
                    index = 0;
                    if (rowNum % 500 == 0) {
                        bw.flush();
                    }
//                    Thread.sleep(10);
                }
                bw.flush();
                flag = true;
                logger.info("线程" + Thread.currentThread().getName() + "将数据写入CSV文件成功 -> " + file.toString());
            } else {
                logger.warn("线程" + Thread.currentThread().getName() + "写CSV文件异常：数据源为空，无数据可写入");
            }
        } catch (Exception e) {
            logger.error("线程" + Thread.currentThread().getName() + "写CSV文件异常：" + e.getMessage());
            return flag;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("线程" + Thread.currentThread().getName() +
                "写CSV文件" + file.toString() + "耗时" + (System.currentTimeMillis() - startTime) + "ms");
        return flag;
    }

    /**
     * 适合多线程
     *
     * @param title
     * @param data
     * @param file
     */
    public static boolean writeCSV2(String title, List<Map<String, String>> data, File file) {
        long startTime = System.currentTimeMillis();

        boolean flag = false;
        RandomAccessFile randomAccessFile = null;
        FileChannel outChannel = null;
        FileLock fileLock = null;
        File parentFilePath = file.getParentFile();
        if (!parentFilePath.exists()) {
            parentFilePath.mkdirs();
        }
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            outChannel = randomAccessFile.getChannel();
            // 尝试3次获取文件锁
            for (int i = 0; i < 3; i++) {
                try {
                    fileLock = outChannel.tryLock();
                    if (fileLock != null) {
                        break;
                    } else {
                        logger.warn("存在其他线程正在操作当前文件" + file.toString() + "，线程" + Thread.currentThread().getName() + "休眠1秒");
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    logger.warn("存在其他线程正在操作当前文件" + file.toString() + "，线程" + Thread.currentThread().getName() + "休眠1秒");
                    Thread.sleep(1000);
                }
            }
            if (fileLock == null) {
                logger.error("线程" + Thread.currentThread().getName() + "写CSV文件失败：无法获取对文件" + file.toString() + "的锁");
                return flag;
            }

            logger.info("线程" + Thread.currentThread().getName() + "准备将数据写入CSV文件" + file.toString() + " ...");
            ByteBuffer buffer = ByteBuffer.wrap((title + "\r\n").getBytes("utf-8"));
            outChannel.write(buffer);
            buffer.clear();

            int rowNum = 0;
            int colNum = title.split(",").length;
            int index = 0;
            if (data != null && !data.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                for (Map<String, String> row : data) {
                    rowNum++;
                    for (String col : row.values()) {
                        if (index < colNum - 1) {
                            sb.append(col).append(",");
                        } else {
                            sb.append(col).append("\r\n");
                        }
                        index++;
                    }
                    index = 0;
                    if (rowNum % 500 == 0) {
                        buffer = ByteBuffer.wrap(sb.toString().getBytes("utf-8"));
                        outChannel.write(buffer);
                        buffer.clear();
                        sb.setLength(0);
                    }
                    // 测试
//                    Thread.sleep(10);
                }
                buffer = ByteBuffer.wrap(sb.toString().getBytes("utf-8"));
                outChannel.write(buffer);
                buffer.clear();
                sb.setLength(0);

                flag = true;
                logger.info("线程" + Thread.currentThread().getName() + "将数据写入CSV文件成功 -> " + file.getPath() + " " + getFileSize(file.length()));
            } else {
                logger.warn("线程" + Thread.currentThread().getName() + "写CSV文件异常：数据源为空，无数据可写入");
            }
        } catch (Exception e) {
            logger.error("线程" + Thread.currentThread().getName() + "写CSV文件异常：" + e.getMessage());
            return flag;
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("线程" + Thread.currentThread().getName() +
                "写CSV文件" + file.toString() + "耗时" + (System.currentTimeMillis() - startTime) + "ms");
        return flag;
    }


    /**
     * 压缩文件夹
     *
     * @param srcFilePath
     * @param zipFilePath
     * @param zipFileName
     * @return
     */
    public static boolean zip(String srcFilePath, String zipFilePath, String zipFileName) {
        boolean flag = false;
        File srcFile = new File(srcFilePath);
        File zipFile = new File(zipFilePath + File.separator + zipFileName + ".zip");

        if (!srcFile.exists() || srcFile.listFiles().length == 0) {
            return flag;
        }
        if (zipFile.exists()) {
            logger.info("压缩文件已存在 -> " + zipFile.getPath() + " -> 准备删除 ...");
            zipFile.delete();
        }
        if (!zipFile.getParentFile().exists()) {
            zipFile.getParentFile().mkdirs();
        }

        ZipArchiveOutputStream zaos = null;
        BufferedInputStream bis = null;
        try {
            zaos = new ZipArchiveOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32()));
            zaos.setUseZip64(Zip64Mode.AsNeeded);
            zaos.setEncoding("UTF-8");
            Iterator<File> iterator = FileUtils.iterateFiles(srcFile, null, true);
            while (iterator.hasNext()) {
                File file = iterator.next();
                zaos.putArchiveEntry(new ZipArchiveEntry(file.getName()));
                bis = new BufferedInputStream(new FileInputStream(file));
                IOUtils.copy(bis, zaos, 1024 * 2);
                zaos.flush();
                zaos.closeArchiveEntry();
                bis.close();
            }
            zaos.finish();
            flag = true;
            logger.info("线程" + Thread.currentThread().getName() + "压缩文件成功 -> " + zipFile.getPath() + " " + getFileSize(zipFile.length()));
        } catch (Exception e) {
            logger.error("线程" + Thread.currentThread().getName() + "压缩文件异常：" + e.getMessage());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (zaos != null) {
                try {
                    zaos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 删除目录
     *
     * @param delPath
     */
    public static void deleteDir(String delPath) {
        File delFile = new File(delPath);
        try {
            FileUtils.deleteDirectory(delFile);
            logger.info("线程" + Thread.currentThread().getName() + "删除目录成功 -> " + delFile.toString());
        } catch (IOException e) {
            logger.info("线程" + Thread.currentThread().getName() + "删除目录异常：" + e.getMessage());
        }
    }

    private static String getFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1048576) {
            return String.format("%.2f", (double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            return String.format("%.2f", (double) fileSize / 1048576) + "MB";
        } else {
            return String.format("%.2f", (double) fileSize / 1073741824) + "GB";
        }
    }

}
