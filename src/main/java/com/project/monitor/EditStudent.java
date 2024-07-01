package com.project.monitor;

import Tables.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

public class EditStudent {

    @FXML
    private TextField agetb;


    @FXML
    private TextField fnametb;

    @FXML
    private TextField gendertb;

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
        gendertb.setText(student.getGender());
        agetb.setText(String.valueOf(student.getAge()));
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
            student.setGender(gendertb.getText());
            student.setAge(age);

            db = new dbFunctions();
            Connection conn = db.connect_to_db(Database, lUser, Password);

            if (conn != null) {
                db.updateStudentInDatabase(conn, "student_info", student.getFirstname(), student.getLastname(), student.getGender(), student.getAge(), student.getLrn());
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
}
