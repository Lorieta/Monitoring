package com.project.monitor;

import Tables.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

public class EditStudent {

    @FXML
    private TextField agetb;

    @FXML
    private Button cancelbtn;

    @FXML
    private Button confirmbtn;

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
    private dbFunctions db; // Add an instance of dbFunctions

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
        agetb.setText(student.getAge());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    public void saveChanges() {
        student.setLrn(lrntb.getText());
        student.setFirstname(fnametb.getText());
        student.setLastname(lnametb.getText());
        student.setGender(gendertb.getText());
        student.setAge(agetb.getText());

        // Initialize dbFunctions
        db = new dbFunctions();

        // Get database connection
        Connection conn = db.connect_to_db(Database, lUser, Password);

        // Update student in the database
        db.updateStudentInDatabase(conn, "student_info", student.getFirstname(), student.getLastname(), student.getGender(), student.getAge(), student.getLrn());

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void cancelEdit() {
        dialogStage.close();
    }
}
