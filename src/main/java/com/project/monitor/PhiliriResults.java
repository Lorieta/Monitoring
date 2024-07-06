package com.project.monitor;

import Tables.PhiliriResultsModel;
import Tables.ReadinglogModel;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhiliriResults extends Controller implements Initializable {

    @FXML
    private TableColumn<PhiliriResultsModel, String> LRNcol;

    @FXML
    private TableView<PhiliriResultsModel> PhiliriTable;

    @FXML
    private TableColumn<PhiliriResultsModel, String> actioncol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> datecol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> languagecol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> lastnamecol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> oralcol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> remarkcol;

    @FXML
    private TableColumn<PhiliriResultsModel, String> resultID;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<PhiliriResultsModel, String> silentcol;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    private PhiliriResults tableController;
    private final ObservableList<PhiliriResultsModel>  philiriResults = FXCollections.observableArrayList();

    private final dbFunctions db = new dbFunctions();

    public void setTableController(PhiliriResults tableController) {
        this.tableController = tableController;
    }
    private void setupTableColumns() {
        resultID.setCellValueFactory(new PropertyValueFactory<>("resultID"));
        LRNcol.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        lastnamecol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        oralcol.setCellValueFactory(new PropertyValueFactory<>("oralResult"));
        silentcol.setCellValueFactory(new PropertyValueFactory<>("silentResult"));
        languagecol.setCellValueFactory(new PropertyValueFactory<>("languageType"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("dateRecorded"));
        remarkcol.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        actioncol.setCellFactory(new Callback<TableColumn<PhiliriResultsModel, String>, TableCell<PhiliriResultsModel, String>>() {
            @Override
            public TableCell<PhiliriResultsModel, String> call(TableColumn<PhiliriResultsModel, String> param) {
                return new TableCell<PhiliriResultsModel, String>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.getStyleClass().add("edit-button");
                        deleteButton.getStyleClass().add("delete-button");

                        editButton.setOnAction(event -> {
                            PhiliriResultsModel philiriResultsModel = getTableView().getItems().get(getIndex());
                            handleEdit(philiriResultsModel);

                        });

                        deleteButton.setOnAction(event -> {
                            PhiliriResultsModel philiriResultsModel = getTableView().getItems().get(getIndex());
                            handleDelete(philiriResultsModel);
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
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


    private void handleEdit(PhiliriResultsModel philiriResultsModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editphiliriresult.fxml"));
            Parent parent = loader.load();


            EditphiliriResult editPhiliriResultController = loader.getController();
            editPhiliriResultController.setPhiliriResultID(philiriResultsModel.getResultID());
            editPhiliriResultController.setTableController(this);

            // Show the edit window
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY); // You can customize the stage style here
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(PhiliriResults.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleDelete(PhiliriResultsModel philiriResultsModel) {
        String query = "DELETE FROM Result WHERE ResultID = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, philiriResultsModel.getResultID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Philiri result deleted successfully.");
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete Philiri result.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting Philiri result: " + e.getMessage());
        }
    }

    public void refreshTable() {
        philiriResults.clear();

        String query = "SELECT r.ResultID, r.LRN, si.LastName, o.oralresult AS OralResult, " +
                "s.silentresult AS SilentResult, lt.Languagetype AS LanguageType, " +
                "r.DateRecorded, r.Remarks " +
                "FROM Result r " +
                "JOIN oral o ON r.oralID = o.orallID " +
                "JOIN silent s ON r.silentID = s.SilentID " +
                "JOIN LanguageType lt ON r.LanguageID = lt.LanguageID " +
                "JOIN Student_info si ON r.LRN = si.LRN " +
                "WHERE lt.Languagetype IN ('English', 'Tagalog') " +
                "ORDER BY r.LRN, lt.Languagetype, r.DateRecorded";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                philiriResults.add(new PhiliriResultsModel(
                        resultSet.getInt("resultid"),
                        resultSet.getString("lrn"),
                        resultSet.getString("LastName"),
                        resultSet.getString("OralResult"),
                        resultSet.getString("SilentResult"),
                        resultSet.getString("LanguageType"),
                        resultSet.getDate("DateRecorded"),
                        resultSet.getString("Remarks")
                ));
            }

            PhiliriTable.setItems(philiriResults);

        } catch (Exception e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void addBtn(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addphiliriResults.fxml"));
            Parent parent = loader.load();

            AddPhiliriResult addPhiliriResultController = loader.getController();
            addPhiliriResultController.setTableController(this);  // Pass the PhiliriResults controller

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Readinglog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        refreshTable();
    }
}
