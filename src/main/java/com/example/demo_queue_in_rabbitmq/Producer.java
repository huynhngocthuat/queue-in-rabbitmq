package com.example.demo_queue_in_rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer implements Runnable{
    private final static String QUEUE_NAME = "gpcoder-queue";

    private String name;
    private int numOfMessage;

    public Producer(String name, int numOfMessage) {
        this.name = name;
        this.numOfMessage = numOfMessage;
    }
    @Override
    public void run() {
        System.out.println("Create a ConnectionFactory for " + name);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel() ) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Start sending messages ... ");
            int index = 1;
            while (index <= numOfMessage) {
                String message = " Task #" + index++;
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] " + name + " Sent: '" + message + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Close connection and free resources");
        }
    }
}
