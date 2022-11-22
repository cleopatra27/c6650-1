package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Processor {

    public static CloseableHttpClient client = HttpClientBuilder.create()
            .build();

//    public static void main(String[] args) throws InterruptedException {
//        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(1000);
//        new SkierProducer(SkierQueue).run();
//        final AtomicInteger successfulCount = new AtomicInteger(0);
//        final AtomicInteger unSuccessfulCount= new AtomicInteger(0);
//        CountDownLatch completed = new CountDownLatch(1000);
//        Consumer consumer = new Consumer(SkierQueue, successfulCount, unSuccessfulCount, "http://ec2-52-12-53-174.us-west-2.compute.amazonaws.com/CS6650Homework1Server-1.0-SNAPSHOT", completed);
//        System.out.println("start ==>" + System.currentTimeMillis());
//        Phase phase = new Phase(consumer, completed, 5, 100);
//        phase.start();
//        Phase phase2 = new Phase(consumer, completed, 5, 100);
//        phase2.start();
//        phase.waitForPhaseFinish();
//        phase2.waitForPhaseFinish();
//        System.out.println("done ==>" + System.currentTimeMillis());
//
//    }

    public  void process(int skierCount, int startThreadCount, int runCount, String baseURL) throws Exception {

        //initialize variables
        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(skierCount);
        final AtomicInteger successfulCount = new AtomicInteger(0);
        final AtomicInteger unSuccessfulCount= new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        //phase 1
        //produce skier
        new SkierProducer(SkierQueue).run();

        //phase 2
        //run the start threads
        CountDownLatch completed = new CountDownLatch(skierCount);
        Consumer consumer = new Consumer(SkierQueue, successfulCount, unSuccessfulCount, baseURL, completed);
        Phase phase = new Phase(consumer, completed, startThreadCount, runCount);
        phase.start();

        //phase 3
        //run more threads to complete POSTs
        Phase phase2 = new Phase(consumer, completed, 168, 1000);
        phase2.start();

        phase.waitForPhaseFinish();
        phase2.waitForPhaseFinish();
        long endTime = System.currentTimeMillis();

        //all done
        done(successfulCount, unSuccessfulCount, startTime, endTime);

    }
    private void done(AtomicInteger successfulCount, AtomicInteger unsuccessfulCount, long startTime, long endTime){
        System.out.println("************************ COMPLETION LOG START *************************");
        //number of successful requests sent
        System.out.println("number of successful requests sent ==>" + successfulCount);

        //number of unsuccessful requests (should be 0)
        System.out.println("number of unsuccessful requests ==>" + unsuccessfulCount);

        //the total run time (wall time) for all phases to complete.
        long wallTime = endTime - startTime;
        System.out.println("total run time (wall time) ==>" + wallTime);

        //the total throughput in requests per second
        double throughput = (1.0 * (successfulCount.get() + unsuccessfulCount.get())) /wallTime;
        System.out.println("total throughput in requests per second ==>" + throughput);

        System.out.println("************************ COMPLETION LOG END *************************");
    }
}
