package com.project.monitor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Addresource {

    @FXML
    private TextField Datepublished;

    @FXML
    private Button addbtn;

    @FXML
    private TextField authorField;

    @FXML
    private Button clearbtn;

    @FXML
    private ChoiceBox<String> languageType;

    @FXML
    private ChoiceBox<String> resourceType;

    @FXML
    private TextField titleField;

    @FXML
    private TextField urlField;

    private String selectedLanguage;
    private String selectedResourceType;

    @FXML
    void addResource(MouseEvent event) {
        // Collect data from fields
        String title = titleField.getText();
        String author = authorField.getText();
        String datePublished = Datepublished.getText();
        String url = urlField.getText();

        // Use the selected values from ChoiceBoxes
        if (selectedLanguage != null && selectedResourceType != null) {
            // TODO: Add logic to save the resource with all collected data
            System.out.println("Adding resource: " + title + " by " + author);
            System.out.println("Language: " + selectedLanguage);
            System.out.println("Resource Type: " + selectedResourceType);
        } else {
            System.out.println("Please select both language and resource type.");
        }
    }

    @FXML
    void clearFields(MouseEvent event) {
        titleField.clear();
        authorField.clear();
        Datepublished.clear();
        urlField.clear();
        languageType.getSelectionModel().clearSelection();
        resourceType.getSelectionModel().clearSelection();
        selectedLanguage = null;
        selectedResourceType = null;
    }

    @FXML
    public void initialize() {
        // Initialize ChoiceBoxes
        languageType.setItems(FXCollections.observableArrayList("English", "French", "Spanish"));
        resourceType.setItems(FXCollections.observableArrayList("Book", "Video", "Article"));

        // Add listeners to ChoiceBoxes
        languageType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedLanguage = newValue;
            System.out.println("Selected language: " + selectedLanguage);
        });

        resourceType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedResourceType = newValue;
            System.out.println("Selected resource type: " + selectedResourceType);
        });
    }
}