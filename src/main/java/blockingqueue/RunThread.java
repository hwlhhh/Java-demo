package blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by HWL on 2018/8/21
 */
public class RunThread {

    private final static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        // 生产者
        for (int i = 1; i <= 1; i++) {
            Producer producer = new Producer(queue);
            Thread thread = new Thread(producer);
            thread.setName("Producer-" + i);
            thread.start();
        }

        // 消费者
//        for (int i = 1; i <= 3; i++) {
//            Consumer consumer = new Consumer(queue);
//            Thread thread = new Thread(consumer);
//            thread.setName("Consumer-" + i);
//            thread.start();
//        }

        Thread consumer1 = new Thread(new Consumer(queue));
        consumer1.setName("Consumer-" + 1);
        consumer1.start();

        Thread consumer2 = new Thread(new Consumer(queue));
        consumer2.setName("Consumer-" + 2);
        consumer2.start();

        Thread consumer3 = new Thread(new Consumer(queue));
        consumer3.setName("Consumer-" + 3);
        consumer3.start();

//        while (true) {
//            try {
//                Thread.sleep(500);
//                System.out.println("Consumer1 - "+consumer1.getState());
//                System.out.println("Consumer2 - "+consumer2.getState());
//                System.out.println("Consumer3 - "+consumer3.getState());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
