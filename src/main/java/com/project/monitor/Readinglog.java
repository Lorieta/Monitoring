package com.project.monitor;

import Tables.ReadinglogModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class Readinglog {
    private final String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
    private Stage stage;
    private Scene scene;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    @FXML
    private TableColumn<ReadinglogModel, String> actionsColumn;
    @FXML
    private Button addButton;
    @FXML
    private TableColumn<ReadinglogModel, String> commentCol;
    @FXML
    private TableColumn<ReadinglogModel, Date> datefinishedcol;
    @FXML
    private TableColumn<ReadinglogModel, Date> datestartedcol;
    @FXML
    private TableColumn<ReadinglogModel, Integer> durationcol;
    @FXML
    private TableColumn<ReadinglogModel, String> fnamecol;
    @FXML
    private TableColumn<ReadinglogModel, String> languagetypecol;
    @FXML
    private TableColumn<ReadinglogModel, String> lnamecol;
    @FXML
    private TableColumn<ReadinglogModel, Integer> logidcol;
    @FXML
    private TableColumn<ReadinglogModel, Integer> lrncol;
    @FXML
    private TableView<ReadinglogModel> readinglogtable;
    @FXML
    private TableColumn<ReadinglogModel, String> resourcetypecol;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<ReadinglogModel, String> urlcol;

    private final ObservableList<ReadinglogModel> readinglog = FXCollections.observableArrayList();
    private final dbFunctions db = new dbFunctions();

    @FXML
    void initialize() {
        setupTableColumns();
        refreshTable();
    }

    @FXML
    void addBtn(MouseEvent event) {
        // Implement add functionality
    }

    @FXML
    void search(ActionEvent event) {
        // Implement search functionality
    }

    private void setupTableColumns() {
        logidcol.setCellValueFactory(new PropertyValueFactory<>("logid"));
        lrncol.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        fnamecol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        lnamecol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        urlcol.setCellValueFactory(new PropertyValueFactory<>("url"));
        languagetypecol.setCellValueFactory(new PropertyValueFactory<>("languageType"));
        resourcetypecol.setCellValueFactory(new PropertyValueFactory<>("resourceType"));
        durationcol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        datestartedcol.setCellValueFactory(new PropertyValueFactory<>("dateStarted"));
        datefinishedcol.setCellValueFactory(new PropertyValueFactory<>("dateFinished"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }

    @FXML
    public void refreshTable() {
        readinglog.clear();

        String query = "SELECT rl.LogID, si.LRN, si.Firstname, si.Lastname, m.ResourceTitle, m.URL, " +
                "lt.LanguageType, rt.ResourceType, rl.Duration, rl.DateStarted, rl.DateFinished, rl.Comment " +
                "FROM reading_log rl " +
                "JOIN Student_info si ON rl.LRN = si.LRN " +
                "JOIN Materials m ON rl.MaterialID = m.MaterialsId " +
                "JOIN Languagetype lt ON m.TypeID = lt.LanguageID " +
                "JOIN Resourcetype rt ON m.ResourceID = rt.ResourceID " +
                "ORDER BY rl.DateStarted DESC";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                readinglog.add(new ReadinglogModel(
                        resultSet.getInt("LogID"),
                        resultSet.getInt("LRN"),
                        resultSet.getString("Firstname"),
                        resultSet.getString("Lastname"),
                        resultSet.getString("ResourceTitle"),
                        resultSet.getString("URL"),
                        resultSet.getString("LanguageType"),
                        resultSet.getString("ResourceType"),
                        resultSet.getString("Duration"),
                        resultSet.getDate("DateStarted"),
                        resultSet.getDate("DateFinished"),
                        resultSet.getString("Comment")
                ));
            }

            readinglogtable.setItems(readinglog);

        } catch (Exception e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void switchStudent(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("student.fxml"));
        stage = ((Stage)((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMaterials(MouseEvent event)throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Resource.fxml"));
        stage = ((Stage)((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

}
