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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

public class PhiliriResults implements Initializable {

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
        // actioncol.setCellValueFactory(new PropertyValueFactory<>("action"));
        // The 'action' column might need special handling, commented out for now
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


    @FXML
    void searchtb(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        refreshTable();
    }
}
