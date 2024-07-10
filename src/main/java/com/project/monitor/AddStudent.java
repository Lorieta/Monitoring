package com.project.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class AddStudent {

    private final String Database = Config.DATABASE;
    private final String lUser = Config.USER;
    private final String Password = Config.PASSWORD;

    @FXML
    private TextField ageTb;

    @FXML
    private TextField fnameTB;

    @FXML
    private TextField lnameTB;

    @FXML
    private TextField lrnTb;

    @FXML
    private TextField emailtb;

    @FXML
    private ComboBox<String> genderTb;

    // Reference to TableController to access refreshTable method
    private TableController tableController;

    private String adviserID;

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

    public void setAdviserID(String adviserID) {
        this.adviserID = adviserID;
        System.out.println("AddStudent - adviserID set: " + this.adviserID); // Debug print
    }

    @FXML
    void initialize() {
        // Populate gender ComboBox
        List<String> genders = Arrays.asList("Male", "Female");
        genderTb.getItems().addAll(genders);
    }

    @FXML
    void addStudent(MouseEvent event) {
        String lrn = lrnTb.getText();
        String fname = fnameTB.getText();
        String lname = lnameTB.getText();
        String gender = genderTb.getValue(); // Get selected gender
        String age = ageTb.getText();
        String email = emailtb.getText(); // Get email from the text field

        if (!isNumeric(lrn) || !isNumeric(age)) {
            showAlert("LRN and age must be valid numbers.");
            return;
        }

        try {
            // Check if LRN already exists
            if (isLRNUnique(lrn)) {
                dbFunctions db = new dbFunctions();
                Connection conn = db.connect_to_db(Database, lUser, Password);
                db.AddStudent(conn, "student_info", lrn, fname, lname, gender, age, adviserID, email); // Pass email to db function
                conn.close();
                clearFields();

                // Refresh table after successful addition
                if (tableController != null) {
                    tableController.refreshTable();
                }
            } else {
                showAlert("LRN already exists. Please enter a unique LRN.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error occurred while adding student.");
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void clearFields() {
        lrnTb.clear();
        fnameTB.clear();
        lnameTB.clear();
        genderTb.getSelectionModel().clearSelection(); // Clear selection for gender ComboBox
        ageTb.clear();
        emailtb.clear(); // Clear email field as well
    }

    private boolean isLRNUnique(String lrn) {
        try {
            dbFunctions db = new dbFunctions();
            Connection conn = db.connect_to_db(Database, lUser, Password);
            String query = "SELECT * FROM student_info WHERE lrn = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, lrn);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean unique = !resultSet.next(); // true if LRN is unique (no results found)
            resultSet.close();
            preparedStatement.close();
            conn.close();
            return unique;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handle the error appropriately based on your application's requirements
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
