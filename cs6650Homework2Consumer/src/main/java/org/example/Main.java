package org.example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private final static String QUEUE_NAME = "threadExQ";
    private final static String QUEUE_URI = "amqp://guest:guest@ec2-35-91-102-39.us-west-2.compute.amazonaws.com:5672";
    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(QUEUE_URI);
        final Connection connection = factory.newConnection();
        ConcurrentHashMap<Long, String> concurrentHashMap = new ConcurrentHashMap<>();
        new Consumer(concurrentHashMap, connection, QUEUE_NAME).receive();
    }

}