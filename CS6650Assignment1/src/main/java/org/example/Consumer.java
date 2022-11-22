package org.example;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

//public class Consumer implements Callable<Integer> {
public class Consumer implements Runnable {

    private final BlockingQueue<SkierBean> SkierQueue;
    private final AtomicInteger successfulCount;
    private final AtomicInteger unSuccessfulCount;

//    private  final CloseableHttpClient httpClient;
    private String baseURL;
    private  ConnectionService connectionService;
    private CountDownLatch completed;
//    public Consumer(BlockingQueue<SkierBean> skierQueue, AtomicInteger successfulCount, AtomicInteger unSuccessfulCount, String baseURL, CloseableHttpClient httpClient) {
public Consumer(BlockingQueue<SkierBean> skierQueue, AtomicInteger successfulCount, AtomicInteger unSuccessfulCount, String baseURL, CountDownLatch completed) {
        this.SkierQueue = skierQueue;
        this.successfulCount = successfulCount;
        this.unSuccessfulCount = unSuccessfulCount;
        this.baseURL = baseURL;
//        this.httpClient = httpClient;
        this.connectionService = new ConnectionService();
        this.completed = completed;
    }


    @Override
    public void run() {
        System.out.println("started consumer class thread -> " + Thread.currentThread().threadId());
        try {
            consume();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int send(SkierBean skierBean){
    return connectionService.connect(
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

    public void consume() throws InterruptedException, IOException {
        SkierBean skierBean = SkierQueue.take();
        int response = send(skierBean);

        //if response is not 200, send to retry
        if (response != 200) {
            retry(skierBean);
        } else {
            //count as success
            this.successfulCount.incrementAndGet();
        }
        this.completed.countDown();
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
