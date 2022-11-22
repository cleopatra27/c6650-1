package org.example;

public class StartThreads extends Thread
{
    private int runCount;

    public StartThreads(int runCount) {
        this.runCount = runCount;
    }

    public void run(Consumer consumer) throws InterruptedException {
        for (int i = 0; i < runCount; i++) {
            System.out.println("go -->");
            consumer.consume();
        }
    }
}