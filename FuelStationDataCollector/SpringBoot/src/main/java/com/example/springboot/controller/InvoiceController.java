package com.example.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class InvoiceController {
    private static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @PostMapping("/invoices/{customerId}")
    public void create(@PathVariable String customerId) throws SQLException, IOException, TimeoutException {
        logger.info("Reached POST function with customer id " + customerId);

        // DONE: Code to start data gathering job
        // DONE: Starts the process by sending a start message with the customer ID to the Data Collection Dispatcher
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                ) {

            channel.queueDeclare("redChannel", false, false, false, null);
            channel.basicPublish("", "redChannel", null, customerId.getBytes(StandardCharsets.UTF_8));
            logger.info("published to red channel: " + customerId);

        } catch (IOException e) {
            logger.warn(e.toString());
        } catch (TimeoutException e) {
            logger.warn(e.toString());
        }
    }

    @GetMapping("/invoices/{customerId}")
    public String getInvoices(@PathVariable String customerId){
        logger.info("Reached GET function with customer id " + customerId);

        // TODO: Code to return invoice PDF including download link and creation time
        // TODO: Return 404 not found if its not available
        /* List results = new List();
        File[] files = new File("patch").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        } */
        return null;
    }
}

