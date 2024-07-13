package com.project.monitor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Malevsfemale {

    @FXML
    private AreaChart<String, Number> areachart;

    private dbFunctions db = new dbFunctions();
    private String currentAdviserID; // Adviser ID to specify

    @FXML
    public void initialize() {
        // Create axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Sessions");

        areachart.setTitle("Daily Session Comparison by Gender");
        areachart.getXAxis().setTickLabelRotation(90); // Rotate x-axis labels if necessary

        // Fetch data from database and populate chart
        fetchDataAndPopulateChart(xAxis, yAxis);
    }

    private void fetchDataAndPopulateChart(CategoryAxis xAxis, NumberAxis yAxis) {
        ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();
        String query;

        if (currentAdviserID == null || currentAdviserID.trim().isEmpty()) {
            // Query for all records when no adviserID is specified
            query = "SELECT " +
                    "rl.datestarted AS \"Date\", " +
                    "si.gender AS \"Gender\", " +
                    "COUNT(rl.logid) AS \"TotalSessions\" " +
                    "FROM reading_log rl " +
                    "JOIN student_info si ON rl.lrn = si.lrn " +
                    "GROUP BY rl.datestarted, si.gender " +
                    "ORDER BY rl.datestarted, si.gender";
        } else {
            // Query for specific adviserID
            query = "SELECT " +
                    "rl.datestarted AS \"Date\", " +
                    "si.gender AS \"Gender\", " +
                    "COUNT(rl.logid) AS \"TotalSessions\" " +
                    "FROM reading_log rl " +
                    "JOIN student_info si ON rl.lrn = si.lrn " +
                    "WHERE si.adviserid = ? " +
                    "GROUP BY rl.datestarted, si.gender " +
                    "ORDER BY rl.datestarted, si.gender";
        }

        try (Connection connection = db.connect_to_db(Config.DATABASE, Config.USER, Config.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (currentAdviserID != null && !currentAdviserID.trim().isEmpty()) {
                preparedStatement.setString(1, currentAdviserID); // Set the adviser ID parameter only if it's specified
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create series for each gender
            XYChart.Series<String, Number> maleSeries = new XYChart.Series<>();
            maleSeries.setName("Male");

            XYChart.Series<String, Number> femaleSeries = new XYChart.Series<>();
            femaleSeries.setName("Female");

            // Populate series with data from result set
            while (resultSet.next()) {
                String gender = resultSet.getString("Gender");
                String date = resultSet.getString("Date");
                int totalSessions = resultSet.getInt("TotalSessions");

                if ("Male".equals(gender)) {
                    maleSeries.getData().add(new XYChart.Data<>(date, totalSessions));
                } else if ("Female".equals(gender)) {
                    femaleSeries.getData().add(new XYChart.Data<>(date, totalSessions));
                }
            }

            // Add series to chart
            seriesList.addAll(maleSeries, femaleSeries);
            areachart.setData(seriesList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    public void setCurrentAdviserID(String adviserID) {
        this.currentAdviserID = adviserID;
        initialize(); // Reinitialize with the new adviser ID
    }

    public String getCurrentAdviserID() {
        return currentAdviserID;
    }
}
