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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Addselection implements Initializable {

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
    public void initialize(URL location, ResourceBundle resources) {
        loadLRNComboBox();
        loadSelectionComboBox();
        setupLRNSelectionListener();
        setupSelectionListener();
    }

    public void setSelectionController(Selection selectionController) {
        this.selectionController = selectionController;
        // Ensure to load data based on the current adviser ID once the controller is set
        loadLRNComboBox();
    }

    public void loadLRNComboBox() {
        lrncb.getItems().clear();
        List<String> lrnList = new ArrayList<>();

        if (selectionController == null || selectionController.getCurrentAdviserID() == null) {

            return;
        }

        String currentAdviser = selectionController.getCurrentAdviserID();

        String query = "SELECT LRN FROM Student_info WHERE AdviserID = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, currentAdviser);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                lrnList.add(resultSet.getString("LRN"));
            }
            lrncb.getItems().addAll(lrnList);

        } catch (SQLException e) {
            showAlert("Error", "Error loading LRNs: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadSelectionComboBox() {
        selectioncb.getItems().clear();

        String query = "SELECT ResourceTitle FROM Materials";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                selectioncb.getItems().add(resultSet.getString("ResourceTitle"));
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
                name.setText(resultSet.getString("Firstname") + " " + resultSet.getString("Lastname"));
            } else {
                name.setText("");
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
                url.setText(resultSet.getString("url"));
                languagetype.setText(resultSet.getString("languagetype"));
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
        if (selectionController == null || selectionController.getCurrentAdviserID() == null) {
            showAlert("Error", "No current adviser ID found.", Alert.AlertType.ERROR);
            return;
        }

        String currentAdviser = selectionController.getCurrentAdviserID();

        String selectedLRN = lrncb.getValue();
        String selectedSelection = selectioncb.getValue();

        if (isValidForm(selectedLRN, selectedSelection)) {
            LocalDate currentDate = LocalDate.now();

            fetchResourceDetails(selectedSelection);
            String resourceUrl = url.getText();
            String languageType = languagetype.getText();

            String query = "INSERT INTO DailySelection (LRN, AdviserID, LanguageTypeID, MaterialsID, Score, Date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
                 PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, selectedLRN);
                preparedStatement.setString(2, currentAdviser);
                preparedStatement.setInt(3, 1); // Assuming LanguageTypeID is hardcoded or managed elsewhere
                preparedStatement.setInt(4, 31); // Assuming MaterialsID is hardcoded or managed elsewhere
                preparedStatement.setInt(5, Integer.parseInt(scoretb.getText()));
                preparedStatement.setDate(6, Date.valueOf(currentDate));

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int selectionID = generatedKeys.getInt(1);
                        System.out.println("Inserted Selection ID: " + selectionID);
                    }

                    showAlert("Success", "Selection added successfully.", Alert.AlertType.INFORMATION);

                    if (selectionController != null) {
                        selectionController.refreshTable();
                    } else {
                        System.err.println("Selection controller is null");
                    }

                    clearForm();
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

    private boolean isValidForm(String selectedLRN, String selectedSelection) {
        return selectedLRN != null && !selectedLRN.isEmpty() &&
                selectedSelection != null && !selectedSelection.isEmpty() &&
                isValidScore(scoretb.getText());
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
