package com.project.monitor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class OralandSilent implements Initializable {

    @FXML
    private PieChart oralandsilent;

    private String currentAdviserID;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    private dbFunctions db;
    private String currentResultType = "Oral English";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new dbFunctions();
        loadData(currentResultType);
        setUpClickEvents();
        setCurrentAdviserID(currentAdviserID); // Set your adviser ID here
        System.out.println(currentAdviserID);
    }

    private void setUpClickEvents() {
        oralandsilent.setOnMouseClicked(event -> {
            switch (currentResultType) {
                case "Oral English":
                    currentResultType = "Oral Tagalog";
                    break;
                case "Oral Tagalog":
                    currentResultType = "Silent English";
                    break;
                case "Silent English":
                    currentResultType = "Silent Tagalog";
                    break;
                case "Silent Tagalog":
                    currentResultType = "Oral English";
                    break;
            }
            loadData(currentResultType);
        });
    }

    private void loadData(String resultType) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try {
            String query = "";
            switch (resultType) {
                case "Oral English":
                    query = "SELECT o.oralresult AS ResultCategory, COUNT(*) AS Count " +
                            "FROM Result r " +
                            "JOIN oral o ON r.oralID = o.orallID " +
                            "JOIN LanguageType lt ON r.LanguageID = lt.LanguageID " +
                            "JOIN Student_info si ON r.LRN = si.LRN " +
                            "WHERE lt.LanguageType = 'English' " +
                            (currentAdviserID != null ? "AND si.AdviserID = ? " : "") +
                            "GROUP BY o.oralresult " +
                            "ORDER BY o.oralresult";
                    break;
                case "Oral Tagalog":
                    query = "SELECT o.oralresult AS ResultCategory, COUNT(*) AS Count " +
                            "FROM Result r " +
                            "JOIN oral o ON r.oralID = o.orallID " +
                            "JOIN LanguageType lt ON r.LanguageID = lt.LanguageID " +
                            "JOIN Student_info si ON r.LRN = si.LRN " +
                            "WHERE lt.LanguageType = 'Tagalog' " +
                            (currentAdviserID != null ? "AND si.AdviserID = ? " : "") +
                            "GROUP BY o.oralresult " +
                            "ORDER BY o.oralresult";
                    break;
                case "Silent English":
                    query = "SELECT s.silentresult AS ResultCategory, COUNT(*) AS Count " +
                            "FROM Result r " +
                            "JOIN silent s ON r.silentID = s.SilentID " +
                            "JOIN LanguageType lt ON r.LanguageID = lt.LanguageID " +
                            "JOIN Student_info si ON r.LRN = si.LRN " +
                            "WHERE lt.LanguageType = 'English' " +
                            (currentAdviserID != null ? "AND si.AdviserID = ? " : "") +
                            "GROUP BY s.silentresult " +
                            "ORDER BY s.silentresult";
                    break;
                case "Silent Tagalog":
                    query = "SELECT s.silentresult AS ResultCategory, COUNT(*) AS Count " +
                            "FROM Result r " +
                            "JOIN silent s ON r.silentID = s.SilentID " +
                            "JOIN LanguageType lt ON r.LanguageID = lt.LanguageID " +
                            "JOIN Student_info si ON r.LRN = si.LRN " +
                            "WHERE lt.LanguageType = 'Tagalog' " +
                            (currentAdviserID != null ? "AND si.AdviserID = ? " : "") +
                            "GROUP BY s.silentresult " +
                            "ORDER BY s.silentresult";
                    break;
            }
            populateChartData(pieChartData, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        oralandsilent.setData(pieChartData);
        oralandsilent.setTitle(resultType);
    }

    private void populateChartData(ObservableList<PieChart.Data> pieChartData, String query) throws SQLException {
        Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
        if (conn == null) {
            System.out.println("Connection to database failed. Cannot populate chart data.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (currentAdviserID != null) {
                stmt.setString(1, currentAdviserID); // Set the current adviser ID only if it's not null
            }
            try (ResultSet rs = stmt.executeQuery()) {
                int total = 0;
                while (rs.next()) {
                    String category = rs.getString("ResultCategory");
                    int count = rs.getInt("Count");
                    total += count;
                    PieChart.Data data = new PieChart.Data(category + " (" + count + ")", count);
                    pieChartData.add(data);
                }

                // Calculate percentages
                DecimalFormat df = new DecimalFormat("#.##");
                for (PieChart.Data data : pieChartData) {
                    double percentage = (data.getPieValue() / total) * 100;
                    data.setName(data.getName() + " - " + df.format(percentage) + "%");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while populating chart data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
    }

    public void setCurrentAdviserID(String adviserID) {
        this.currentAdviserID = adviserID;
        loadData(currentResultType);
        setUpClickEvents();
        System.out.println(currentAdviserID);
    }

    public String getCurrentAdviserID() {
        return currentAdviserID;
    }
}