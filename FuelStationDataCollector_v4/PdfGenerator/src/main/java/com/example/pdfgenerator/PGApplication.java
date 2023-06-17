package com.example.pdfgenerator;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class PGApplication {
    public static final String YELLOW_CHANNEL = "yellowChannel";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();

        Channel yellowChannel = connection.createChannel();
        yellowChannel.queueDeclare(YELLOW_CHANNEL, false, false, false, null);

        ExecutorService service = Executors.newSingleThreadExecutor();
        PGQueue pgQueue = new PGQueue(yellowChannel);
        service.submit(pgQueue);
    }
}