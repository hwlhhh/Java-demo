package blockingqueue;

/**
 * Created by HWL on 2018/8/21
 */
public class TaskProcess extends AbstractProcess implements Runnable {

    private String threadName;

    public TaskProcess(String threadName) {
        super(threadName);
    }


    @Override
    public void run() {

    }
}
