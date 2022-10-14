package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Callable<Integer> {

    private final BlockingQueue<SkierBean> SkierQueue;
    private final AtomicInteger successfulCount;
    private final AtomicInteger unSuccessfulCount;
    private String baseURL;
    public Consumer(BlockingQueue<SkierBean> skierQueue, AtomicInteger successfulCount, AtomicInteger unSuccessfulCount, String baseURL) {
        this.SkierQueue = skierQueue;
        this.successfulCount = successfulCount;
        this.unSuccessfulCount = unSuccessfulCount;
        this.baseURL = baseURL;
    }


    @Override
    public Integer call() throws Exception {
        consume();
        return 0;
    }

    public int send(SkierBean skierBean) {
        return new ConnectionService().connect(
                this.baseURL+"/skiers/"
                        + skierBean.getResortID() + "/seasons/"
                        + skierBean.getSeasonID() + "/days/"
                        + skierBean.getDayID() + "/skiers/"
                        + skierBean.getSkierID() + "",
                null,
                null,
                new CreateSkiersBean(skierBean.getTime(), skierBean.getLiftID()).toString(),
                "application/json").getStatusLine().getStatusCode();
    }

    public void consume() throws InterruptedException {
        SkierBean skierBean = SkierQueue.take();

        int response = send(skierBean);

        //if response is not 200, send to retry
        if (response != 200) {
            retry(skierBean);
        } else {
            //count as success
            this.successfulCount.incrementAndGet();
        }
    }

    public void retry(SkierBean skierBean) {
        // it should retry the request up to 5 times before counting it as a failed request
        for (int i = 0; i < 5; i++) {
            int response = send(skierBean);
            if (response == 200) {
                this.successfulCount.incrementAndGet();
                break;
            }
        }
        this.unSuccessfulCount.incrementAndGet();
    }


}
