package com.example.stationdatacollector;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.Map;

public class SDCDatabase {
    private final static String url = "jdbc:postgresql://localhost:";
    private final static Map<String, String> ports = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("1", "30011"),
            new AbstractMap.SimpleEntry<>("2", "30012"),
            new AbstractMap.SimpleEntry<>("3", "30013"));
    private final static String dbName = "/stationdb";

    public String getConsumption(String customer, String station) throws SQLException {
        String consumptionQuery = "SELECT SUM(kwh) from charge where customer_id=" + customer + ";";
        String fullUrl = url + ports.get(station) + dbName;

        Connection stationDbConnection = DriverManager.getConnection(fullUrl, "postgres", "postgres");
        PreparedStatement preparedStationsQuery = stationDbConnection.prepareStatement(consumptionQuery);
        ResultSet consumption = preparedStationsQuery.executeQuery();

        String consumptionString = null;
        DecimalFormat df = new DecimalFormat("#.##");
        for (; consumption.next(); ) {
             consumptionString = df.format(consumption.getFloat(1));
        }
        return consumptionString;
    }
}
