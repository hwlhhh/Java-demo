package blockingqueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by HWL on 2018/8/21
 */
public class Consumer implements Runnable {

    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            consume();
        }
    }

    private void consume() {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss.SSS");
        String startTime = df.format(new Date());
        try {
            System.out.println(Thread.currentThread().getName() + "消费了" + queue.take() + " - " + startTime);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(startTime + "->" + df.format(new Date()));
//        System.out.println("线程" + Thread.currentThread().getName() + "耗时" + (endtime - startTime) + "ms");
    }
}
