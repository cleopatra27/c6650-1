package org.example;


public class CreateSkiers {

    public static void main(String[] args) throws Exception {
        new Processor().process(200000,
                32,
                1000, "http://ec2-54-186-102-9.us-west-2.compute.amazonaws.com/CS6650Homework1Server-1.0-SNAPSHOT");
    }
}
