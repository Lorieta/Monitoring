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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Readinglog extends Controller{

    private final String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
    private Stage stage;
    private Scene scene;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    @FXML
    private TableColumn<ReadinglogModel, Void> actionsColumn;
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
    private TableColumn<ReadinglogModel, String> lrncol;
    @FXML
    private TableView<ReadinglogModel> readinglogtable;
    @FXML
    private TableColumn<ReadinglogModel, String> resourcetypecol;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<ReadinglogModel, String> urlcol;

    private String currentAdviserID;

    private Readinglog tableController;
    private final ObservableList<ReadinglogModel> readinglog = FXCollections.observableArrayList();
    private final dbFunctions db = new dbFunctions();

    @FXML
    void initialize() {
        // Ensure this returns the correct ID

        setupTableColumns();
        refreshTable();
        getLoggedInTeacherID();

    }


    @FXML
    void addBtn(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addreadlogs.fxml"));
            Parent parent = loader.load();

            AddReadinglog addReadinglogController = loader.getController();
            addReadinglogController.setTableController(this);
            addReadinglogController.setCurrentAdviserID(this.currentAdviserID);
            System.out.println("Current Adviser ID in Homedashboard: " + this.currentAdviserID);

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

        actionsColumn.setCellFactory(new Callback<TableColumn<ReadinglogModel, Void>, TableCell<ReadinglogModel, Void>>() {
            @Override
            public TableCell<ReadinglogModel, Void> call(final TableColumn<ReadinglogModel, Void> param) {
                return new TableCell<ReadinglogModel, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.getStyleClass().add("edit-button");
                        deleteButton.getStyleClass().add("delete-button");

                        editButton.setOnAction(event -> {
                            ReadinglogModel readinglogModel = getTableView().getItems().get(getIndex());
                            handleEdit(readinglogModel);

                        });

                        deleteButton.setOnAction(event -> {
                            ReadinglogModel readinglogModel = getTableView().getItems().get(getIndex());
                            handleDelete(readinglogModel);
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

    private void handleEdit(ReadinglogModel readinglogModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editreadlog.fxml"));
            Parent parent = loader.load();

            Editreadlog editReadinglogController = loader.getController();
            editReadinglogController.setReadinglog(readinglogModel);
            editReadinglogController.setDialogStage(new Stage()); // Create a new stage for editing
            editReadinglogController.setTableController(this);  // Set the table controller

            // Show the edit window
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);  // You can customize the stage style here
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(Readinglog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        readinglog.clear();

        String query = "SELECT rl.LogID, si.LRN, si.Firstname, si.Lastname, m.ResourceTitle, m.URL, " +
                "lt.LanguageType, rt.ResourceType, rl.Duration, rl.DateStarted, rl.DateFinished, rl.Comment " +
                "FROM reading_log rl " +
                "JOIN Student_info si ON rl.LRN = si.LRN " +
                "JOIN Materials m ON rl.MaterialID = m.MaterialsId " +
                "JOIN Languagetype lt ON m.TypeID = lt.LanguageID " +
                "JOIN Resourcetype rt ON m.ResourceID = rt.ResourceID " +
                "WHERE si.AdviserID = ? " +  // Add the condition for AdviserID
                "ORDER BY rl.DateStarted DESC";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, getCurrentAdviserID()); // Set the AdviserID parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    readinglog.add(new ReadinglogModel(
                            resultSet.getInt("LogID"),
                            resultSet.getString("LRN"),
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
            }

            readinglogtable.setItems(readinglog);

        } catch (SQLException e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleDelete(ReadinglogModel readinglogModel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this reading log entry?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deleteQuery = "DELETE FROM reading_log WHERE LogID = ?";
            try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                stmt.setInt(1, readinglogModel.getLogid());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reading log entry deleted successfully.");
                    refreshTable(); // Refresh the table after deletion
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Failed to delete reading log entry.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while deleting reading log: " + e.getMessage());

        }
    }

}
    public void switchStudent(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("student.fxml"));
        stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMaterials(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resource.fxml"));
        stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
    public void setCurrentAdviserID(String adviserID) {
        this.currentAdviserID = adviserID;
        refreshTable();
    }


    public String getCurrentAdviserID() {
        return currentAdviserID;
    }
}
