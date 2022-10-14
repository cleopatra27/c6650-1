package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Processor {

    public static void main(String[] args) {
        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(1000);
        new SkierProducer(SkierQueue).run();
        final AtomicInteger successfulCount = new AtomicInteger(0);
        final AtomicInteger unSuccessfulCount= new AtomicInteger(0);
        Consumer consumer = new Consumer(SkierQueue, successfulCount, unSuccessfulCount, "http://ec2-54-189-97-214.us-west-2.compute.amazonaws.com//CS6650Homework1Server");
        System.out.println("start ==>" + System.currentTimeMillis());
        while(SkierQueue.remainingCapacity() < 1000){
            try {
                consumer.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("done ==>" + System.currentTimeMillis());
        Thread.currentThread().interrupt();

    }

    public  void process(int skierCount, int startThreadCount, int runCount, String baseURL) throws Exception {

        //initialize variables
        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(skierCount);
        List<Long> timeList = new ArrayList<>();
        final AtomicInteger successfulCount = new AtomicInteger(0);
        final AtomicInteger unSuccessfulCount= new AtomicInteger(0);


        //phase 1
        //produce skier
        timeList.add(System.currentTimeMillis());
        new SkierProducer(SkierQueue).run();


        //phase 2
        //run the start threads
        Consumer consumer = new Consumer(SkierQueue, successfulCount, unSuccessfulCount, baseURL);
        CountDownLatch completed = new CountDownLatch(runCount);
        for (int i = 0; i < startThreadCount; i++) {
            timeList.add(System.currentTimeMillis());
            StartThreads t1=new StartThreads(runCount);
            t1.run(consumer);
            completed.countDown();
        }
        completed.await();


        //phase 3
        //run more threads to complete POSTs
        CountDownLatch completedNext = new CountDownLatch((skierCount - SkierQueue.remainingCapacity()));
        while(SkierQueue.remainingCapacity() < skierCount){
            timeList.add(System.currentTimeMillis());
            new Thread(() ->
            {
                try {
                    consumer.consume();
                    completedNext.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        completedNext.await();

        //all done
        timeList.add(System.currentTimeMillis());
        done(successfulCount, unSuccessfulCount, timeList);

        Thread.currentThread().getThreadGroup().interrupt();

    }
    private void done(AtomicInteger successfulCount, AtomicInteger unsuccessfulCount, List<Long> timeList){
        System.out.println("************************ COMPLETION LOG START *************************");
        //number of successful requests sent
        System.out.println("number of successful requests sent ==>" + successfulCount);

        //number of unsuccessful requests (should be 0)
        System.out.println("number of unsuccessful requests ==>" + unsuccessfulCount);

        //the total run time (wall time) for all phases to complete.
        Long wallTime = timeList.stream().reduce(0L, Long::sum);
        System.out.println("total run time (wall time) ==>" + wallTime);

        //the total throughput in requests per second
        double throughput = (1.0 * (successfulCount.get() + unsuccessfulCount.get())) /wallTime;
        System.out.println("total throughput in requests per second ==>" + throughput);

        System.out.println("************************ COMPLETION LOG END *************************");
    }
}
