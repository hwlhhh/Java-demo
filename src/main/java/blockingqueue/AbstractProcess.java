package blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by HWL on 2018/8/21
 */
public class AbstractProcess {

    protected BlockingQueue queue;
    protected String threadName;

    public AbstractProcess(String threadName) {
        this.queue = new LinkedBlockingQueue();
        this.threadName = threadName;



    }
}
