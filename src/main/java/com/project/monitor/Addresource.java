package com.project.monitor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Addresource implements Initializable {

    private static final String DATABASE_URL = Config.DATABASE;
    private static final String DATABASE_USER = Config.USER;
    private static final String DATABASE_PASSWORD = Config.PASSWORD;

    private dbFunctions db = new dbFunctions();

    @FXML private TextField authorField;
    @FXML private ChoiceBox<String> languageTypeChoice;
    @FXML private ChoiceBox<String> resourceTypeChoice;
    @FXML private TextField titleField;
    @FXML private TextField urlField;
    @FXML private DatePicker datePublishedPicker;

    private ResourceController tableController;

    public void setTableController(ResourceController tableController) {
        this.tableController = tableController;
    }

    @FXML
    void addResource(MouseEvent event) {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields except URL must be filled.");
            return;
        }

        Connection conn = null;
        try {
            conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            conn.setAutoCommit(false);

            java.sql.Date sqlDate = java.sql.Date.valueOf(datePublishedPicker.getValue());

            boolean isAdded = db.addResourceToDatabase(conn,
                    titleField.getText(),
                    urlField.getText(),
                    authorField.getText(),
                    sqlDate,
                    languageTypeChoice.getValue(),
                    resourceTypeChoice.getValue());

            if (isAdded) {
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Resource added successfully.");
                clearFields();

                if (tableController != null) {
                    tableController.refreshTable();
                }
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while adding resource: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void clearFields() {
        titleField.clear();
        authorField.clear();
        datePublishedPicker.setValue(null);
        urlField.clear();
        languageTypeChoice.getSelectionModel().clearSelection();
        resourceTypeChoice.getSelectionModel().clearSelection();
    }

    private boolean validateInputs() {
        return !titleField.getText().trim().isEmpty() &&
                !authorField.getText().trim().isEmpty() &&
                datePublishedPicker.getValue() != null &&
                languageTypeChoice.getValue() != null &&
                resourceTypeChoice.getValue() != null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadChoiceBoxes();
    }

    private void loadChoiceBoxes() {
        String languageQuery = "SELECT LanguageType FROM Languagetype";
        String resourceQuery = "SELECT ResourceType FROM Resourcetype";

        try (Connection conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement languageStmt = conn.prepareStatement(languageQuery);
             PreparedStatement resourceStmt = conn.prepareStatement(resourceQuery);
             ResultSet languageRs = languageStmt.executeQuery();
             ResultSet resourceRs = resourceStmt.executeQuery()) {

            while (languageRs.next()) {
                languageTypeChoice.getItems().add(languageRs.getString("LanguageType"));
            }

            while (resourceRs.next()) {
                resourceTypeChoice.getItems().add(resourceRs.getString("ResourceType"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading choice box data: " + e.getMessage());
        }
    }


}
