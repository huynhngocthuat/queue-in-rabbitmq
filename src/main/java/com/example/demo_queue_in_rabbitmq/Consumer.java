package com.example.demo_queue_in_rabbitmq;

import com.rabbitmq.client.*;

public class Consumer implements Runnable{
    private final static String QUEUE_NAME = "gpcoder-queue";
    private int numberConsumedMessage = 0;
    private String name;
    private int timeToFinishATask;

    public Consumer(String name, int timeToFinishATask) {
        this.name = name;
        this.timeToFinishATask = timeToFinishATask;
    }

    @Override
    public void run() {
        try {
            System.out.println("Create a ConnectionFactory for " + name);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            System.out.println("Start receiving messages ... ");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] " + name + " Received: '" + message + "'");
                consume(message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println(" [-] " + name + " Already consumed: " + (++numberConsumedMessage) + " Tasks");

            };
            CancelCallback cancelCallback = consumerTag -> { };
            boolean autoAck = false;
            String consumerTag = channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
            System.out.println("Tag for " + name + ": " + consumerTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consume(String message) {
        try {
            Thread.sleep(timeToFinishATask); // simulate time to produce the data
            System.out.println(" [-] " + name + " Consumed for " + message + " in " + timeToFinishATask + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
