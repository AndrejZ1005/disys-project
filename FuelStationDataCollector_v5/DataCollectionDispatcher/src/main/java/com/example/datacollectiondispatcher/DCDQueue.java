package com.example.datacollectiondispatcher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DCDQueue extends DCDDatabase implements Runnable {
    private final Channel greenChannel;
    private final Channel purpleChannel;
    private final List<String> stationIds;
    private final BlockingQueue<String> redQueue = new LinkedBlockingQueue<>();

    public DCDQueue(Channel redChannel, Channel greenChannel, Channel purpleChannel) throws IOException, SQLException {
        this.greenChannel = greenChannel;
        this.purpleChannel = purpleChannel;
        this.stationIds = setAvailableStations();

        DeliverCallback deliverCallbackRed = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received from red: " + message);
            redQueue.add(message);
        };
        redChannel.basicConsume(DCDApplication.RED_CHANNEL, true, deliverCallbackRed, consumerTag -> {});
    }

    @Override
    public void run() {
        String customer;
        String message;
        while (true) {
            try {
                customer = redQueue.take();

                // Sends a message to the Data Collection Receiver to inform about the started job
                message = customer + "-" + stationIds.size();
                purpleChannel.basicPublish("",
                        DCDApplication.PURPLE_CHANNEL,
                        null,
                        message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent to purple: " + message);

                // Sends a message for every charging station to the Station Data Collector
                for (String stationId : stationIds) {
                    message = customer + "-" + stationId;
                    greenChannel.basicPublish("",
                            DCDApplication.GREEN_CHANNEL,
                            null,
                            message.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Sent to green: " + customer + "-" + stationId);
                }

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
