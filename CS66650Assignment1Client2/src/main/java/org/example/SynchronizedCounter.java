package org.example;

public class SynchronizedCounter {

    private int successfulCount;
    private int unSuccessfulCount;

    public synchronized void incSuccess() {
        this.successfulCount++;
    }

    public synchronized void incUnSuccess() {
        this.unSuccessfulCount++;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public int getUnSuccessfulCount() {
        return unSuccessfulCount;
    }

}
