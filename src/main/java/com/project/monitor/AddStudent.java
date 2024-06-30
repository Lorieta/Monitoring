package com.project.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddStudent {

    private final String Database = Config.DATABASE;
    private final String lUser = Config.USER;
    private final String Password = Config.PASSWORD;

    @FXML
    private Button addbtn;

    @FXML
    private TextField ageTb;

    @FXML
    private Button clearbtn;

    @FXML
    private TextField fnameTB;

    @FXML
    private TextField genderTb;

    @FXML
    private TextField lnameTB;

    @FXML
    private TextField lrnTb;

    // Reference to TableController to access refreshTable method
    private TableController tableController;

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

    public static boolean checker(String str) {
        return str == null || str.trim().isEmpty();
    }

    @FXML
    void addStudent(MouseEvent event) {
        String lrn = lrnTb.getText();
        String fname = fnameTB.getText();
        String lname = lnameTB.getText();
        String gender = genderTb.getText();
        String age = ageTb.getText();

        if (!checker(lrn) && !checker(fname) && !checker(lname) && !checker(gender) && !checker(age)) {
            try {
                // Check if LRN already exists
                if (isLRNUnique(lrn)) {
                    dbFunctions db = new dbFunctions();
                    Connection conn = db.connect_to_db(Database, lUser, Password);
                    db.AddStudent(conn, "student_info", lrn, fname, lname, gender, age);
                    conn.close();
                    clearFields();

                    // Refresh table after successful addition
                    if (tableController != null) {
                        tableController.loadDate();
                    }
                } else {
                    showAlert("LRN already exists. Please enter a unique LRN.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error occurred while adding student.");
            }
        } else {
            showAlert("All fields are required.");
        }
    }

    @FXML
    void clearFields() {
        lrnTb.clear();
        fnameTB.clear();
        lnameTB.clear();
        genderTb.clear();
        ageTb.clear();
    }

    @FXML
    void exit(MouseEvent event) {
        // Handle exit action
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
