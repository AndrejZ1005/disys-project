package com.example.pdfgenerator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PGQueue extends PGDatabase implements Runnable {
    private final BlockingQueue<String> yellowQueue = new LinkedBlockingQueue<>();

    public PGQueue(Channel yellowChannel) throws IOException {
        DeliverCallback deliverCallbackYellow = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received from yellow: " + message);
            yellowQueue.add(message);
        };
        yellowChannel.basicConsume(PGApplication.YELLOW_CHANNEL, true, deliverCallbackYellow, consumerTag -> {});
    }

    @Override
    public void run() {
        String[] newPdfRequestSplit;
        String newPdfRequest;
        String customerId;
        String consumptionSum;
        String firstName;
        String lastName;

        while (true) {
            try {
                newPdfRequest = yellowQueue.take();
                newPdfRequestSplit = newPdfRequest.split("-");
                customerId = newPdfRequestSplit[0];
                consumptionSum = newPdfRequestSplit[1];
                firstName = getFirstName(customerId);
                lastName = getLastName(customerId);

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(
                        "InvoiceRepository\\Invoice_" + firstName + "_" + lastName + ".pdf"));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 18, BaseColor.BLACK);
                document.add(new Paragraph("Invoice", font));
                document.add(new Paragraph("\n", font));

                font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
                document.add(new Paragraph("First name: " + firstName, font));
                document.add(new Paragraph("Last name: " + lastName, font));
                document.add(new Paragraph("Consumption: " + consumptionSum + " kwh", font));
                document.add(new Paragraph("Date: test", font));
                document.add(new Paragraph("Time: test", font));
                document.close();

            } catch (InterruptedException | SQLException | DocumentException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
