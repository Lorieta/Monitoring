package com.project.monitor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Addselection extends Selection implements Initializable {

    @FXML
    private Button addbtn;

    @FXML
    private Button close;

    @FXML
    private TextField languagetype;

    @FXML
    private ComboBox<String> lrncb;

    @FXML
    private TextField name;

    @FXML
    private TextField scoretb;

    @FXML
    private ComboBox<String> selectioncb;

    @FXML
    private TextField url;
    private Selection selectionController;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    private final dbFunctions db = new dbFunctions();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLRNComboBox();
        loadSelectionComboBox();
        setupLRNSelectionListener();
        setupSelectionListener();
    }

    public void setSelectionController(Selection selectionController) {
        this.selectionController = selectionController;
    }

    private void loadLRNComboBox() {
        lrncb.getItems().clear(); // Clear existing items
        List<String> lrnList = new ArrayList<>();

        String query = "SELECT LRN FROM Student_info";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String lrn = resultSet.getString("LRN");
                lrnList.add(lrn);
            }

            lrncb.getItems().addAll(lrnList);

        } catch (SQLException e) {
            showAlert("Error", "Error loading LRNs: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadSelectionComboBox() {
        selectioncb.getItems().clear(); // Clear existing items

        String query = "SELECT ResourceTitle FROM Materials"; // Adjust as per your database schema

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String resourceTitle = resultSet.getString("ResourceTitle");
                selectioncb.getItems().add(resourceTitle);
            }

        } catch (SQLException e) {
            showAlert("Error", "Error loading selection options: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupLRNSelectionListener() {
        lrncb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                fetchStudentName(newValue);
            } else {
                name.clear();
            }
        });
    }

    private void fetchStudentName(String lrn) {
        String query = "SELECT Firstname, Lastname FROM Student_info WHERE LRN = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, lrn);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstname = resultSet.getString("Firstname");
                String lastname = resultSet.getString("Lastname");
                name.setText(firstname + " " + lastname);
            } else {
                name.setText(""); // Clear name if LRN is not found
            }

        } catch (SQLException e) {
            showAlert("Error", "Error fetching name: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupSelectionListener() {
        selectioncb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                fetchResourceDetails(newValue);
            } else {
                clearResourceDetails();
            }
        });
    }

    private void fetchResourceDetails(String resourceTitle) {
        String query = "SELECT m.url, lt.languagetype " +
                "FROM materials m " +
                "JOIN languagetype lt ON m.typeid = lt.languageid " +
                "WHERE m.resourcetitle = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, resourceTitle);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String resourceURL = resultSet.getString("url");
                String langType = resultSet.getString("languagetype");
                url.setText(resourceURL);
                languagetype.setText(langType);
            } else {
                clearResourceDetails();
            }

        } catch (SQLException e) {
            showAlert("Error", "Error fetching resource details: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void clearResourceDetails() {
        url.clear();
        languagetype.clear();
    }

    @FXML
    void addStudent(MouseEvent event) {
        String selectedLRN = lrncb.getValue();
        String selectedSelection = selectioncb.getValue();

        if (selectedLRN != null && !selectedLRN.isEmpty() &&
                selectedSelection != null && !selectedSelection.isEmpty()) {

            if (!isValidScore(scoretb.getText())) {
                showAlert("Invalid Input", "Please enter a valid score.", Alert.AlertType.WARNING);
                return;
            }

            String query = "INSERT INTO DailySelection (LRN, LanguageTypeID, MaterialsId, Score) " +
                    "VALUES (?, " +
                    "(SELECT LanguageID FROM LanguageType WHERE LanguageType = ? LIMIT 1), " +
                    "(SELECT MaterialsID FROM Materials WHERE ResourceTitle = ? LIMIT 1), " +
                    "?)";

            try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
                 PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                preparedStatement.setString(1, selectedLRN);
                preparedStatement.setString(2, languagetype.getText()); // Assuming this field is populated
                preparedStatement.setString(3, selectedSelection);
                preparedStatement.setInt(4, Integer.parseInt(scoretb.getText())); // Assuming this field is populated with score

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    showAlert("Success", "Selection added successfully.", Alert.AlertType.INFORMATION);
                    if (selectionController != null) {
                        selectionController.refreshTable();
                    } else {
                        System.err.println("Selection controller is null");
                    }
                    clearForm(); // Clear the form after successful addition
                } else {
                    showAlert("Error", "Failed to add selection.", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                showAlert("Error", "Error adding selection: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }

        } else {
            showAlert("Incomplete Data", "Please select LRN, Selection, and enter Score.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void exit(MouseEvent event) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    private boolean isValidScore(String score) {
        try {
            Integer.parseInt(score);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        lrncb.getSelectionModel().clearSelection();
        selectioncb.getSelectionModel().clearSelection();
        name.clear();
        url.clear();
        languagetype.clear();
        scoretb.clear();
    }
}

