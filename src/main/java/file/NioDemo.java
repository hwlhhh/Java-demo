package file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by HWL on 2018/8/20
 */
public class NioDemo {

    static void testReadAndWriteNIO() {
        String pathname = "C:\\Users\\adew\\Desktop\\test.txt";
        FileInputStream fin = null;

        String filename = "test-out.txt";
        FileOutputStream fos = null;
        try {
            fin = new FileInputStream(new File(pathname));
            FileChannel channel = fin.getChannel();

            int capacity = 1024 * 20;// 字节
            ByteBuffer bf = ByteBuffer.allocate(capacity);
            System.out.println("limit: " + bf.limit() + ", capacity: " + bf.capacity() + ", position: " + bf.position());

            fos = new FileOutputStream(new File(filename));
            FileChannel outchannel = fos.getChannel();

            // ***************************************
            while (channel.read(bf) != -1) {
                bf.flip();
                outchannel.write(bf);
                bf.clear();
            }
            // ***************************************
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
