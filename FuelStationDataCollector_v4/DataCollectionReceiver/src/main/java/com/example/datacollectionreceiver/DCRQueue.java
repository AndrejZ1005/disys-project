package com.example.datacollectionreceiver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DCRQueue implements Runnable {
    private final Channel yellowChannel;
    private final BlockingQueue<String> purpleQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> blueQueue = new LinkedBlockingQueue<>();

    public DCRQueue(Channel purpleChannel, Channel blueChannel, Channel yellowChannel) throws IOException {
        this.yellowChannel = yellowChannel;

        DeliverCallback deliverCallbackPurple = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received from purple: " + message);
            purpleQueue.add(message);

        };
        purpleChannel.basicConsume(DCRApplication.PURPLE_CHANNEL, true, deliverCallbackPurple, consumerTag -> {});

        DeliverCallback deliverCallbackBlue = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received from blue: " + message);
            blueQueue.add(message);
        };
        blueChannel.basicConsume(DCRApplication.BLUE_CHANNEL, true, deliverCallbackBlue, consumerTag -> {});
    }

    @Override
    public void run() {
        String[] jobMessageSplit;
        String newJob;
        String customer;
        String message;
        float consumptionSum;
        float consumption;
        int checkSum;
        int i;

        while (true) {
            consumptionSum = 0;

            try {
                newJob = purpleQueue.take();
                jobMessageSplit = newJob.split("-");
                customer = jobMessageSplit[0];
                checkSum = Integer.parseInt(jobMessageSplit[1]);

                for (i=0; i<checkSum; i++) {
                    consumption = Float.parseFloat(blueQueue.take().replace(",", "."));
                    consumptionSum += consumption;
                }

                DecimalFormat df = new DecimalFormat("#.##");
                message = customer + "-" + df.format(consumptionSum);
                yellowChannel.basicPublish("",
                        DCRApplication.YELLOW_CHANNEL,
                        null,
                        message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent to yellow: " + message);

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
