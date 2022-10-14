package org.example;


public class CreateSkiers {

    public static void main(String[] args) throws Exception {
        new Processor().process(200000,
                32,
                1000, "http://ec2-34-213-101-153.us-west-2.compute.amazonaws.com/CS6650Homework1Server");
    }
}
