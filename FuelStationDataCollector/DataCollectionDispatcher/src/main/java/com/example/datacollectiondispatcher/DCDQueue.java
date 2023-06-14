package com.example.datacollectiondispatcher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;


public class DCDQueue extends DCDDatabase implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(DCDQueue.class);
    private Channel redChannel;
    private Channel greenChannel;
    private Channel purpleChannel;
    private List<String> stationIds = new ArrayList<>();
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public DCDQueue(Channel redChannel, Channel greenChannel, Channel purpleChannel) throws IOException, SQLException {
        this.redChannel = redChannel;
        this.greenChannel = greenChannel;
        this.purpleChannel = purpleChannel;
        this.stationIds = setAvailableStations();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String customer = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("received from " + DCDApplication.RED_CHANNEL + " message " + customer);
            queue.add(customer);
            logger.debug(queue.toString());
        };
        redChannel.basicConsume(DCDApplication.RED_CHANNEL, true, deliverCallback, consumerTag -> {});
    }

    @Override
    public void run() {
        while ( true ) {
            String customer;
            String message;
            do {
                try {
                    customer = queue.take();
                    System.out.println(customer);

                    // TODO: Sends a message for every charging station to the Station Data Collector
                    for (String stationId : stationIds) {
                        message = customer + "-" + stationId;

                        greenChannel.basicPublish("",
                                DCDApplication.GREEN_CHANNEL,
                                null,
                                message.getBytes(StandardCharsets.UTF_8));

                        System.out.println("Sent message to green channel with customer " + customer + " and station " + stationId);
                    }

                    // TODO: Sends a message to the Data Collection Receiver, that a new job started
                    message = "tbd";
                    purpleChannel.basicPublish("",
                            DCDApplication.PURPLE_CHANNEL,
                            null,
                            message.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Sent message to purple channel to inform about started job");

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while(customer != null);
        }
    }
}
