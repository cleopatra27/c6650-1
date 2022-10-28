package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer {

    private final String QUEUE_NAME;
    private final ConcurrentHashMap<Long, String> concurrentHashMap;
    private final Connection connection;

    Consumer(ConcurrentHashMap<Long, String> concurrentHashMap, Connection connection, String QUEUE_NAME) {
        this.concurrentHashMap = concurrentHashMap;
        this.connection = connection;
        this.QUEUE_NAME = QUEUE_NAME;
    }

    public void receive() {

        Runnable runnable = () -> {
            try {
                final Channel channel = connection.createChannel();
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                // max one message per receiver
                channel.basicQos(1);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    //add to concurrent hashmap
                    concurrentHashMap.put(Thread.currentThread().getId(), message);
                    System.out.println("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
                };
                // process messages
                channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
                });
            } catch (IOException ex) {
                Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        // start threads and block to receive messages
        Thread recv1 = new Thread(runnable);
        recv1.start();
    }
}
