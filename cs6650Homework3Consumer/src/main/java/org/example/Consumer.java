package org.example;

import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer {

    private final String QUEUE_NAME;
    private DatabaseHandler databaseHandler;
    private final Connection connection;

    private java.sql.Connection dbConnection;
    Consumer(Connection connection, String QUEUE_NAME) throws SQLException {
        this.connection = connection;
        this.QUEUE_NAME = QUEUE_NAME;
        this.dbConnection = DriverManager.getConnection(
                "jdbc:mysql://cs6650.cjrovla2rbi4.us-west-2.rds.amazonaws.com:3306/HW3",
                "admin",
                "cs6650data");
        this.databaseHandler = new DatabaseHandler(this.dbConnection);
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

                    //save to db in new thread
                   // new Thread(() -> {
                        SkiersBean skiersBean = new GsonBuilder().create().
                                fromJson(message, SkiersBean.class);
                        databaseHandler.handle(skiersBean);
                    //}).start();

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
        for (int i = 0; i < 200; i++) {
            new Thread(runnable).start();
        }

    }
}
