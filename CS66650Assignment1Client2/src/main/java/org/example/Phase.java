package org.example;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Phase {

    private Consumer consumer;
    private CountDownLatch completed;
    private int runCount;
    private int threadCount;

    Phase(Consumer consumer, CountDownLatch completed, int threadCount, int runCount){
        this.consumer = consumer;
        this.completed = completed;
        this.threadCount = threadCount;
        this.runCount = runCount;
    }

    public void start(){
        for (int i = 1; i <= threadCount; i++) {
            new Thread(() ->{
                StartThreads t1=new StartThreads(runCount);
                try {
                    t1.run(consumer);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void waitForPhaseFinish() throws InterruptedException {
        completed.await();
    }
}
