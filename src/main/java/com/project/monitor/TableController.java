package com.project.monitor;

import Tables.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ResourceBundle;

public class TableController implements Initializable {

    private final String Database = Config.DATABASE;
    private final String lUser = Config.USER;
    private final String Password = Config.PASSWORD;

    @FXML
    private TableColumn<Student, String> GenderColumn;

    @FXML
    private TableColumn<Student, String> LRNColumn;


    @FXML
    private TableColumn<Student, String> ageColumn;

    @FXML
    private TableColumn<Student, String> editColumn;

    @FXML
    private TableColumn<Student, String> firstnameColumn;

    @FXML
    private TableColumn<Student, String> lastnameColumn;


    @FXML
    private TableView<Student> studentTable;
    private final ObservableList<Student> StudentList = FXCollections.observableArrayList();

    @FXML
    private TextField lrnTb;

    @FXML
    private TextField lnametb;


    @FXML
    private TextField fnametb;

    @FXML
    private TextField gendertb;

    @FXML
    private TextField agetb;



    @FXML
    void addBtn(MouseEvent event) throws  Exception{
        String lrn = lrnTb.getText();
        String fname = fnametb.getText();
        String lname =lnametb.getText();
        String gender = gendertb.getText();
        String age = agetb.getText();
    try{
        dbFunctions db = new dbFunctions();
        Connection conn = db.connect_to_db(Database, lUser, Password);
        db.AddStudent(conn,"student_info",lrn,fname,lname,gender,age);
    }catch (Exception e){


    }


        }



    @FXML
    void refresh(MouseEvent event) {
        refreshTable();
    }

    private void loadDate() {
        LRNColumn.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        studentTable.setItems(StudentList);

        refreshTable();
    }


    private void refreshTable() {
        StudentList.clear();
        try {
            dbFunctions db = new dbFunctions();
            Connection conn = db.connect_to_db(Database, lUser, Password);
            String query = "SELECT * FROM student_info";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                StudentList.add(new Student(
                        resultSet.getString("lrn"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("gender"),
                        resultSet.getString("age")
                ));
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDate();
    }
}


