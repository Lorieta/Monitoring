package com.project.monitor;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Averagedailyscore {

    @FXML
    private LineChart<String, Number> averagescoredaily;
    @FXML
    private Button dailyButton;
    @FXML
    private Button monthlyButton;
    @FXML
    private Button yearlyButton;

    private dbFunctions db = new dbFunctions();
    private String currentAdviserID;
    private String currentViewType = "daily";

    @FXML
    public void initialize() {
        System.out.println("Initializing Averagedailyscore...");
        if (averagescoredaily == null) {
            System.err.println("Error: averagescoredaily is null in initialize()");
            return;
        }

        // Configure X and Y axis
        NumberAxis yAxis = (NumberAxis) averagescoredaily.getYAxis();
        yAxis.setLabel("Average Score");
        yAxis.setForceZeroInRange(false);

        averagescoredaily.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        averagescoredaily.setCreateSymbols(false);
        averagescoredaily.setTitle("Average Score");
        averagescoredaily.getXAxis().setLabel("Date");

        System.out.println("Chart initialized successfully.");
        updateChart();
    }

    @FXML
    private void switchToDaily() {
        currentViewType = "daily";
        updateChart();
    }

    @FXML
    private void switchToMonthly() {
        currentViewType = "monthly";
        updateChart();
    }

    @FXML
    private void switchToYearly() {
        currentViewType = "yearly";
        updateChart();
    }

    public void updateChart() {
        System.out.println("Updating chart...");
        System.out.println("Current Adviser ID: " + currentAdviserID);
        System.out.println("Current View Type: " + currentViewType);

        if (averagescoredaily == null) {
            System.err.println("Error: averagescoredaily is null in updateChart()");
            return;
        }

        fetchData();
    }

    private void fetchData() {
        System.out.println("Fetching data" + (currentAdviserID != null ? " for adviser: " + currentAdviserID : " for all advisers"));

        ObservableList<XYChart.Series<String, Number>> seriesList = averagescoredaily.getData();
        seriesList.clear(); // Clear existing data

        String query = getQueryForViewType();

        try (Connection connection = db.connect_to_db(Config.DATABASE, Config.USER, Config.PASSWORD)) {
            System.out.println("Database connection established.");

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                int parameterIndex = 1; // Start parameter index
                if (currentAdviserID != null && !currentAdviserID.isEmpty()) {
                    preparedStatement.setString(parameterIndex++, currentAdviserID);
                }

                System.out.println("Executing query: " + preparedStatement);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    int rowCount = 0;
                    while (resultSet.next()) {
                        rowCount++;
                        String date = resultSet.getString("Date");
                        double averageScore = resultSet.getDouble("AverageScore");
                        try {
                            String formattedDate = formatDate(date);
                            series.getData().add(new XYChart.Data<>(formattedDate, averageScore));
                            System.out.println("Data point: Date = " + formattedDate + ", Average Score = " + averageScore);
                        } catch (Exception e) {
                            System.err.println("Error formatting date: " + date + ". Error: " + e.getMessage());
                        }
                    }

                    if (series.getData().isEmpty()) {
                        System.out.println("No data found" + (currentAdviserID != null ? " for adviser ID: " + currentAdviserID : ""));
                    } else {
                        seriesList.add(series);
                        System.out.println("Added series with " + rowCount + " data points to the chart.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Data fetching completed.");
    }

    private String getQueryForViewType() {
        String baseQuery = "";
        switch (currentViewType) {
            case "daily":
                baseQuery = "SELECT ds.date::date AS Date, AVG(ds.Score) AS AverageScore " +
                        "FROM dailyselection ds ";
                break;
            case "monthly":
                baseQuery = "SELECT DATE_TRUNC('month', ds.date)::date AS Date, AVG(ds.Score) AS AverageScore " +
                        "FROM dailyselection ds ";
                break;
            case "yearly":
                baseQuery = "SELECT DATE_TRUNC('year', ds.date)::date AS Date, AVG(ds.Score) AS AverageScore " +
                        "FROM dailyselection ds ";
                break;
            default:
                return "";
        }

        if (currentAdviserID != null && !currentAdviserID.isEmpty()) {
            baseQuery += "WHERE ds.adviserid = ? ";
        }

        baseQuery += "GROUP BY Date ORDER BY Date";

        return baseQuery;
    }


    private String formatDate(String date) {
        LocalDate localDate;
        if (date.contains(" ")) {
            // If the date string contains time information, parse only the date part
            localDate = LocalDate.parse(date.split(" ")[0]);
        } else {
            localDate = LocalDate.parse(date);
        }

        switch (currentViewType) {
            case "daily":
                return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case "monthly":
                return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case "yearly":
                return localDate.format(DateTimeFormatter.ofPattern("yyyy"));
            default:
                return date;
        }
    }

    public void setCurrentAdviserID(String adviserID) {
        System.out.println("Setting current adviser ID to: " + adviserID);
        this.currentAdviserID = adviserID;
        updateChart(); // Update the chart with the new adviser ID
    }

    public String getCurrentAdviserID() {
        return currentAdviserID;
    }
}