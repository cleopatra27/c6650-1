package org.example;

public class CreateSkiers {

    public static void main(String[] args) throws Exception {
        new Processor().process(200000,
                32,
                1000,
                "http://ec2-34-211-48-129.us-west-2.compute.amazonaws.com/CS6650Homework1Server-1.0-SNAPSHOT");
    }
}