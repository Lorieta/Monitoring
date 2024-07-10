package com.project.monitor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LineChartExample extends Application {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/projectdb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize JavaFX components
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Score History");

        // Define series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score");

        // Fetch data from database
        fetchAndPopulateData(series);

        // Add series to the line chart
        lineChart.getData().add(series);

        // Create scene and show stage
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Line Chart Example");
        primaryStage.show();
    }

    private void fetchAndPopulateData(XYChart.Series<String, Number> series) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT score, date FROM dailyselection WHERE lrn = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "10895634");

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

    public static void main(String[] args) {
        launch(args);
    }
}
