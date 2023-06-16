package com.example.datacollectionreceiver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Slf4j
public class DCRApplication {
    public static final String PURPLE_CHANNEL = "purpleChannel";
    public static final String BLUE_CHANNEL = "blueChannel";
    public static final String YELLOW_CHANNEL = "yellowChannel";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();

        Channel purpleChannel = connection.createChannel();
        purpleChannel.queueDeclare(PURPLE_CHANNEL, false, false, false, null);

        Channel blueChannel = connection.createChannel();
        blueChannel.queueDeclare(BLUE_CHANNEL, false, false, false, null);

        Channel yellowChannel = connection.createChannel();
        yellowChannel.queueDeclare(YELLOW_CHANNEL, false, false, false, null);

        ExecutorService service = Executors.newSingleThreadExecutor();
        DCRQueue dcrQueue = new DCRQueue(purpleChannel, blueChannel, yellowChannel);
        service.submit(dcrQueue);
    }
}