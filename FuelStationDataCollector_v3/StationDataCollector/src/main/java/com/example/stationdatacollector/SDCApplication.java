package com.example.stationdatacollector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SDCApplication {
    public static final String GREEN_CHANNEL = "greenChannel";
    public static final String BLUE_CHANNEL = "blueChannel";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();

        Channel greenChannel = connection.createChannel();
        greenChannel.queueDeclare(GREEN_CHANNEL, false, false, false, null);

        Channel blueChannel = connection.createChannel();
        blueChannel.queueDeclare(BLUE_CHANNEL, false, false, false, null);

        ExecutorService service = Executors.newSingleThreadExecutor();
        SDCQueue sdcQueue = new SDCQueue(greenChannel, blueChannel);
        service.submit(sdcQueue);
    }
}