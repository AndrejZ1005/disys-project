package com.example.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaFXController {
    @FXML
    private TextField customerId;
    @FXML
    private ListView invoices;

    @FXML
    private void btnGenerateInvoice() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/invoices/" + customerId.getText()))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Failed to call REST api: \n" + e.toString());
            alert.showAndWait();
        }
    }

    @FXML
    // TODO: Update every few seconds automatically without a button
    private void updateList() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/invoices/" + customerId.getText()))
                    .GET().build();

            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // TODO: Code to return invoice PDF including download link and creation time
            // TODO: Return 404 not found if its not available
            /* System.out.println(response.body());
            String[] pdf = response.body().split(",");
            System.out.println(pdf[0] + "," + pdf[1]);
            invoices.getItems().clear();
            for (int i=0;i< pdf.length;i++){
                invoices.getItems().add(pdf[i]);
            } */
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Failed to call REST api: \n" + e.toString());
            alert.showAndWait();
        }
    }
}