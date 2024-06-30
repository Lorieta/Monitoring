package com.project.monitor;
import Tables.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableController extends Controller implements Initializable {
    String Database = Config.DATABASE;
    String lUser = Config.USER;
    String Password = Config.PASSWORD;

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
    private TableColumn<Student, Void> actionsColumn;
    @FXML
    private TableView<Student> studentTable;
    private final ObservableList<Student> StudentList = FXCollections.observableArrayList();

    private final dbFunctions db = new dbFunctions();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();

    }

    private void setupTableColumns() {
        LRNColumn.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        studentTable.setItems(StudentList);
        refreshTable();

        // Make firstnameColumn editable
        firstnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstnameColumn.setOnEditCommit(event -> {
            Student student = event.getRowValue();
            student.setFirstname(event.getNewValue());
            // Implement update method as per your database handling
        });

        // Set the cell factory for the actions column
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.getStyleClass().add("edit-button");
                        deleteButton.getStyleClass().add("delete-button");

                        editButton.setOnAction(event -> {
                            Student student = getTableView().getItems().get(getIndex());
                            handleEdit(student);
                        });

                        deleteButton.setOnAction(event -> {
                            Student student = getTableView().getItems().get(getIndex());
                            handleDelete(student);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox container = new HBox(editButton, deleteButton);
                            HBox.setHgrow(editButton, Priority.ALWAYS);
                            HBox.setHgrow(deleteButton, Priority.ALWAYS);
                            editButton.setMaxWidth(Double.MAX_VALUE);
                            deleteButton.setMaxWidth(Double.MAX_VALUE);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private void handleEdit(Student student) {
        try {
            dbFunctions db = new dbFunctions();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editStudent.fxml"));
            Parent parent = loader.load();

            EditStudent editStudentController = loader.getController();
            editStudentController.setDialogStage(new Stage());
            editStudentController.setStudent(student);

            Scene scene = new Scene(parent);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setScene(scene);

            editStudentController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (editStudentController.isSaveClicked()) {

                refreshTable();
            }
        } catch (IOException e) {
            Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void handleDelete(Student student) {
        try {
            Connection conn = db.connect_to_db(Database, lUser, Password);
            db.deleteStudent(conn, student.getLrn().toString());
            refreshTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    void addBtn(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addStudent.fxml"));
            Parent parent = loader.load();

            AddStudent addStudentController = loader.getController();
            addStudentController.setTableController(this);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void refresh(MouseEvent event){

        refreshTable();
    }



    }


