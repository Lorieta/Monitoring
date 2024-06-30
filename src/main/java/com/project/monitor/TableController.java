package com.project.monitor;

import Tables.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableController extends Controller implements Initializable {

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
    private TableColumn<Student, String> firstnameColumn;

    @FXML
    private TableColumn<Student, String> lastnameColumn;

    @FXML
    private TableColumn<Student, String> editColumn;

    @FXML
    private TableView<Student> studentTable;
    private final ObservableList<Student> StudentList = FXCollections.observableArrayList();



    @FXML
    void addBtn(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addStudent.fxml"));
            Parent parent = loader.load();


            AddStudent addStudentController = loader.getController();
            addStudentController.setTableController(this); // Pass reference to TableController

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void refresh(MouseEvent event) {
        refreshTable();
    }

    public void loadDate() {
        LRNColumn.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        studentTable.setItems(StudentList);

        refreshTable();
    }

    public void refreshTable() {
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
