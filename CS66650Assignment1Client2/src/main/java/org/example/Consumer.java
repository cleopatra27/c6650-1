package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Callable<Integer> {

    private final BlockingQueue<SkierBean> SkierQueue;
    private final List<String[]> record = new ArrayList<>();
    public Consumer(BlockingQueue<SkierBean> skierQueue) {
        this.SkierQueue = skierQueue;
    }


    @Override
    public Integer call() throws Exception {
        consume();
        return 0;
    }

    public int send(SkierBean skierBean) {
        return new ConnectionService().connect(
                "https://webhook.site/7762acd9-4201-4fc9-bd4a-0eee5d56e134",
//                "http://ec2-34-222-77-71.us-west-2.compute.amazonaws.com/CS6650Homework1Server/skiers/"
//                        + skierBean.getResortID() + "/seasons/"
//                        + skierBean.getSeasonID() + "/days/"
//                        + skierBean.getDayID() + "/skiers/"
//                        + skierBean.getSkierID() + "",
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
        if (response != 200) {
            retry(skierBean);
        }

        //when the HTTP response is received, take another timestamp
        CSVGenerate(start, System.currentTimeMillis(), "POST", response);
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

    public List<String[]> getRecord() {
        return record;
    }
}
