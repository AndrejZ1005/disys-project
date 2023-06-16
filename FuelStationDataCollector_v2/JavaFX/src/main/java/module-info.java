module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.rabbitmq.client;


    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
}