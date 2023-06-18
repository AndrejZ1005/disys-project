package com.example.pdfgenerator;
import java.sql.*;

public class PGDatabase {
    private final static String url = "jdbc:postgresql://localhost:30001/customerdb";
    public String getFirstName(String id) throws SQLException {
        String firstName = null;
        Connection customerDbConnection = DriverManager.getConnection(url, "postgres", "postgres");
        String customerQuery = "SELECT first_name FROM customer WHERE id="+ id + ";";
        PreparedStatement preparedStationsQuery = customerDbConnection.prepareStatement(customerQuery);

        ResultSet rs = preparedStationsQuery.executeQuery();
        while (rs.next()) {
            firstName = rs.getString(1);
        }
        return firstName;
    }
    public String getLastName(String id) throws SQLException {
        String lastName = null;
        Connection customerDbConnection = DriverManager.getConnection(url, "postgres", "postgres");
        String customerQuery = "SELECT last_name FROM customer WHERE id="+ id + ";";
        PreparedStatement preparedStationsQuery = customerDbConnection.prepareStatement(customerQuery);

        ResultSet rs = preparedStationsQuery.executeQuery();
        while (rs.next()) {
            lastName = rs.getString(1);
        }
        return lastName;
    }
}
