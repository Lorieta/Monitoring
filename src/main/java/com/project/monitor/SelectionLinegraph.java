package com.project.monitor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SelectionLinegraph implements Initializable {

    @FXML
    private LineChart<String, Number> linegraph;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;
    private final dbFunctions db = new dbFunctions();
    private String currentLRN;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        linegraph.setTitle("Score History");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Score");

        linegraph.setAnimated(false); // Disable animation for smoother updates

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score");

        // Fetch data and populate the chart
        fetchAndPopulateData(series);

        linegraph.getData().add(series);
    }

    private void fetchAndPopulateData(XYChart.Series<String, Number> series) {
        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD)) {
            String sql = "SELECT score, date FROM dailyselection WHERE lrn = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentLRN);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int score = rs.getInt("score");
                String date = rs.getString("date"); // Assuming date is stored as a string, adjust as per your database type

                // Add data to the series
                series.getData().add(new XYChart.Data<>(date, score));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLRN(String LRN) {
        this.currentLRN = LRN;
        // When LRN is set, update the chart with new data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score");
        fetchAndPopulateData(series);
        linegraph.getData().clear(); // Clear previous data
        linegraph.getData().add(series); // Add updated data
    }

    public String getLRN() {
        return currentLRN;
    }
}
