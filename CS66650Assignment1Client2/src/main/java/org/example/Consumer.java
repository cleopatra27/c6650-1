package org.example;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Consumer implements Runnable {

    private final BlockingQueue<SkierBean> SkierQueue;
    private Queue<String[]> record = new ConcurrentLinkedQueue<>();
    private String baseURL;
    private CountDownLatch completed;
    private  ConnectionService connectionService;
    public Consumer(BlockingQueue<SkierBean> skierQueue, String baseURL, CountDownLatch completed) {
        this.SkierQueue = skierQueue;
        this.baseURL = baseURL;
        this.completed = completed;
        this.connectionService = new ConnectionService();
    }


    @Override
    public void run() {
        try {
            consume();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int send(SkierBean skierBean) {
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

    public void consume() throws InterruptedException {
        SkierBean skierBean = SkierQueue.take();

        //before sending the POST, take a timestamp
        Long start = System.currentTimeMillis();

        int response = send(skierBean);

        //if response is not 200, send to retry
        if (response != 200 ) {
            retry(skierBean);
        }

        //when the HTTP response is received, take another timestamp
        CSVGenerate(start, System.currentTimeMillis(), "POST", response);

        this.completed.countDown();
    }

    public void retry(SkierBean skierBean) {
        // it should retry the request up to 5 times before counting it as a failed request
        for (int i = 0; i < 5; i++) {
            int response = send(skierBean);
            if (response == 200) {
               break;
            }
        }
    }

    public void  CSVGenerate(Long start, Long end, String requestType, int responseCode){
        getRecord().add(new String[]{start.toString(), String.valueOf(end), requestType, String.valueOf(calculateLatency(start, end)), String.valueOf(responseCode)});
    }

    private Long calculateLatency(Long start, Long end){
        return end - start;
    }

    public Queue<String[]> getRecord() {
        return record;
    }
}
