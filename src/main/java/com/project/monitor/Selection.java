package com.project.monitor;

import Tables.SelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Selection {

    @FXML
    private TableView<SelectionModel> Selectiontable;

    @FXML
    private TableColumn<SelectionModel, Integer> selectionidCol;

    @FXML
    private TableColumn<SelectionModel, String> lrnCol;

    @FXML
    private TableColumn<SelectionModel, String> materialtitleCol;

    @FXML
    private TableColumn<SelectionModel, String> urlCol;

    @FXML
    private TableColumn<SelectionModel, String> languageTypeCol;

    @FXML
    private TableColumn<SelectionModel, Integer> scoreCol;

    @FXML
    private TextField searchField;

    private final ObservableList<SelectionModel> selectionModelList = FXCollections.observableArrayList();
    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    private final dbFunctions db = new dbFunctions();

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshTable();
    }

    private void setupTableColumns() {
        selectionidCol.setCellValueFactory(new PropertyValueFactory<>("selectionID"));
        lrnCol.setCellValueFactory(new PropertyValueFactory<>("LRN"));
        materialtitleCol.setCellValueFactory(new PropertyValueFactory<>("resourceTitle"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        languageTypeCol.setCellValueFactory(new PropertyValueFactory<>("languageType"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    @FXML
    public void refreshTable() {
        selectionModelList.clear();

        String query = "SELECT ds.SelectionID, ds.LRN, m.ResourceTitle, m.URL, lt.LanguageType, ds.Score " +
                "FROM DailySelection ds " +
                "JOIN Materials m ON ds.MaterialsId = m.MaterialsId " +
                "JOIN Languagetype lt ON ds.LanguageTypeID = lt.LanguageID " +
                "JOIN student_info si ON ds.LRN = si.LRN";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                selectionModelList.add(new SelectionModel(
                        resultSet.getInt("SelectionID"),
                        resultSet.getString("LRN"),
                        resultSet.getString("ResourceTitle"),
                        resultSet.getString("URL"),
                        resultSet.getString("LanguageType"),
                        resultSet.getInt("Score")
                ));
            }

            Selectiontable.setItems(selectionModelList);

        } catch (Exception e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inside Selection controller
    @FXML
    private void openAddSelection(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Addselection.fxml"));
            Parent root = loader.load();
            Addselection controller = loader.getController();
            controller.setSelectionController(this); // Set the current controller as the selectionController
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
