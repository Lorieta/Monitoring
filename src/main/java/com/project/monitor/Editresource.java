package com.project.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Tables.ResourceModel;

import java.sql.Connection;
import java.sql.SQLException;

public class Editresource {

    private static final String DATABASE_URL = Config.DATABASE;
    private static final String DATABASE_USER = Config.USER;
    private static final String DATABASE_PASSWORD = Config.PASSWORD;

    private dbFunctions db = new dbFunctions();
    private Stage dialogStage;
    private ResourceModel resource;
    private boolean saveClicked = false;

    @FXML
    private Button updateBtn;

    @FXML
    private TextField authorField;

    @FXML
    private Button clearBtn;

    @FXML
    private DatePicker datePublishedPicker;

    @FXML
    private ChoiceBox<String> languageTypeChoice;

    @FXML
    private ChoiceBox<String> resourceTypeChoice;

    @FXML
    private TextField titleField;

    @FXML
    private TextField urlField;

    @FXML
    void initialize() {
        // Initialize choice boxes if necessary
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setResource(ResourceModel resource) {
        this.resource = resource;
        titleField.setText(resource.getResourceTitle());
        authorField.setText(resource.getAuthor_publisher());
        urlField.setText(resource.getURL());
        datePublishedPicker.setValue(resource.getDate_published().toLocalDate());
        languageTypeChoice.setValue(resource.getLanguageType());
        resourceTypeChoice.setValue(resource.getResourceType());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    void updateResource(MouseEvent event) {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields except URL must be filled.");
            return;
        }

        Connection conn = null;
        try {
            conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            java.sql.Date sqlDate = java.sql.Date.valueOf(datePublishedPicker.getValue());

            boolean isUpdated = db.updateResourceInDatabase(conn,
                    resource.getMaterialid(),  // Assuming you need the material ID to update the correct record
                    titleField.getText(),
                    urlField.getText(),
                    authorField.getText(),
                    sqlDate,
                    languageTypeChoice.getValue(),
                    resourceTypeChoice.getValue());

            if (isUpdated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Resource updated successfully.");
                saveClicked = true;
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update resource.");
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
}
