package com.example.datacollectiondispatcher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DCDDatabase {
    private final static String url = "jdbc:postgresql://localhost:30002/stationdb";
    public List<String> setAvailableStations() throws SQLException {
        List<String> stationIds = new ArrayList<>();
        Connection stationDbConnection = DriverManager.getConnection(url, "postgres", "postgres");
        String stationsQuery = "SELECT id FROM station;";
        PreparedStatement preparedStationsQuery = stationDbConnection.prepareStatement(stationsQuery);
        ResultSet stations = preparedStationsQuery.executeQuery();
        while (stations.next()) {
            stationIds.add(stations.getString(1));
        }
        return stationIds;
    }
}
