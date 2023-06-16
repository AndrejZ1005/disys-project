package com.example.stationdatacollector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SDCQueue extends SDCDatabase implements Runnable {
    private final Channel blueChannel;
    private final BlockingQueue<String> greenQueue = new LinkedBlockingQueue<>();

    public SDCQueue(Channel greenChannel, Channel blueChannel) throws IOException {
        this.blueChannel = blueChannel;

        DeliverCallback deliverCallbackGreen = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received from green: " + message);
            greenQueue.add(message);
        };
        greenChannel.basicConsume(SDCApplication.GREEN_CHANNEL, true, deliverCallbackGreen, consumerTag -> {});
    }

    @Override
    public void run() {
        String[] newQueryForJobSplit;
        String newQueryForJob;
        String customer;
        String station;
        String consumption;

        while (true) {
            try {
                newQueryForJob = greenQueue.take();
                newQueryForJobSplit = newQueryForJob.split("-");
                customer = newQueryForJobSplit[0];
                station = newQueryForJobSplit[1];

                consumption = getConsumption(customer, station);
                blueChannel.basicPublish("",
                        SDCApplication.BLUE_CHANNEL,
                        null,
                        consumption.getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent to blue: " + consumption);

            } catch (InterruptedException | IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
