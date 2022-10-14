package org.example;

import java.util.concurrent.BlockingQueue;

import static org.example.SkierBean.generate;

public class SkierProducer implements Runnable {

    private final BlockingQueue<SkierBean> SkierQueue;

    public SkierProducer(BlockingQueue<SkierBean> SkierQueue){
        this.SkierQueue = SkierQueue;
    }

    @Override
    public void run() {
        try {
            produce();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void produce() throws InterruptedException {
        while (SkierQueue.remainingCapacity() > 0) {
            SkierQueue.put(generate());
        }

    }
}
