package org.example;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueHandler {
    public static void send(String message, Channel channel, String QUEUE_NAME) throws Exception {
        Runnable runnable = () -> {
            try {
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" Message  Sent '");
            } catch (IOException ex) {
                Logger.getLogger(QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        // start threads and wait for completion
        Thread t1 = new Thread(runnable);
        t1.start();
        t1.join();
    }
}