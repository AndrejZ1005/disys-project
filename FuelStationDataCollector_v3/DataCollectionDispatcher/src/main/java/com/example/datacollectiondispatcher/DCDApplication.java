package com.example.datacollectiondispatcher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DCDApplication {
    public static final String RED_CHANNEL = "redChannel";
    public static final String GREEN_CHANNEL = "greenChannel";
    public static final String PURPLE_CHANNEL = "purpleChannel";

    public static void main(String[] args) throws IOException, TimeoutException, SQLException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();

        Channel redChannel = connection.createChannel();
        redChannel.queueDeclare(RED_CHANNEL, false, false, false, null);

        Channel greenChannel = connection.createChannel();
        greenChannel.queueDeclare(GREEN_CHANNEL, false, false, false, null);

        Channel purpleChannel = connection.createChannel();
        purpleChannel.queueDeclare(PURPLE_CHANNEL, false, false, false, null);

        ExecutorService service = Executors.newSingleThreadExecutor();
        DCDQueue dcdQueue = new DCDQueue(redChannel, greenChannel, purpleChannel);
        service.submit(dcdQueue);
    }
}