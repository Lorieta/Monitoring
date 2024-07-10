package com.project.monitor;

import Tables.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EditStudent implements Initializable {

    @FXML
    private TextField agetb;

    @FXML
    private TextField email; // Added email field

    @FXML
    private TextField fnametb;

    @FXML
    private ComboBox<String> gendertb; // Changed ComboBox type to String

    @FXML
    private TextField lnametb;

    @FXML
    private TextField lrntb;

    private Stage dialogStage;
    private Student student;
    private boolean saveClicked = false;
    private dbFunctions db;

    // Database connection parameters
    String Database = Config.DATABASE;
    String lUser = Config.USER;
    String Password = Config.PASSWORD;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudent(Student student) {
        this.student = student;

        lrntb.setText(student.getLrn());
        fnametb.setText(student.getFirstname());
        lnametb.setText(student.getLastname());
        gendertb.setValue(student.getGender()); // Set value for ComboBox
        agetb.setText(String.valueOf(student.getAge()));
        email.setText(student.getEmail()); // Set email field
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    public void saveChanges() {
        try {
            int age = Integer.parseInt(agetb.getText());

            student.setLrn(lrntb.getText());
            student.setFirstname(fnametb.getText());
            student.setLastname(lnametb.getText());
            student.setGender(gendertb.getValue()); // Get selected value from ComboBox
            student.setAge(age);
            student.setEmail(email.getText()); // Set email

            db = new dbFunctions();
            Connection conn = db.connect_to_db(Database, lUser, Password);

            if (conn != null) {
                db.updateStudentInDatabase(conn, "student_info", student.getFirstname(), student.getLastname(), student.getGender(), student.getAge(), student.getLrn(),student.getEmail());
                saveClicked = true;
                conn.close(); // Close the connection
                dialogStage.close();
            } else {
                showAlert("Failed to connect to the database.");
            }
        } catch (NumberFormatException e) {
            showAlert("Only integers are allowed for age.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error occurred while saving changes: " + e.getMessage());
        }
    }

    @FXML
    private void cancelEdit() {
        dialogStage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> genders = Arrays.asList("Male", "Female");
      gendertb.getItems().addAll(genders);
    }
}
