package org.example;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class StartThreads extends Thread
{
    private int runCount;

    public StartThreads(int runCount) {
        this.runCount = runCount;
    }

    public void run(Consumer consumer) throws InterruptedException, IOException {
        for (int i = 0; i < runCount; i++) {
            System.out.println("go -->");
            consumer.consume();
        }
    }

}