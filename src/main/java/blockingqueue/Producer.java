package blockingqueue;

import java.util.concurrent.BlockingQueue;

/**
 * Created by HWL on 2018/8/21
 */
public class Producer implements Runnable {

    private BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int i = 1;
        while (true) {
            try {
                Thread.sleep(10);
                produce("task" + i);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void produce(String task) {
        queue.offer(task);
        String threadName = Thread.currentThread().getName();
//        System.out.println(threadName + " 生产 " + task + " - " + Thread.currentThread().getState());
    }


}
