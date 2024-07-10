package com.project.monitor;

import Tables.SelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class Selection {

    @FXML
    private TableColumn<SelectionModel, Void> action;
    @FXML
    private TableColumn<SelectionModel, Date> datecol;
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

    private String currentAdviserID; // Placeholder for current adviser ID
    private final ObservableList<SelectionModel> selectionModelList = FXCollections.observableArrayList();
    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;
    private final dbFunctions db = new dbFunctions();

    @FXML
    public void initialize() {
        System.out.println(currentAdviserID);
        setupTableColumns();
        refreshTable();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    private void setupTableColumns() {
        selectionidCol.setCellValueFactory(new PropertyValueFactory<>("selectionID"));
        lrnCol.setCellValueFactory(new PropertyValueFactory<>("LRN"));
        materialtitleCol.setCellValueFactory(new PropertyValueFactory<>("resourceTitle"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        languageTypeCol.setCellValueFactory(new PropertyValueFactory<>("languageType"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Action column with delete button
        action.setCellFactory(new Callback<TableColumn<SelectionModel, Void>, TableCell<SelectionModel, Void>>() {
            @Override
            public TableCell<SelectionModel, Void> call(final TableColumn<SelectionModel, Void> param) {
                return new TableCell<SelectionModel, Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            SelectionModel selectionModel = getTableView().getItems().get(getIndex());
                            handleDelete(selectionModel);
                            deleteButton.getStyleClass().add("delete-button");
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox container = new HBox(deleteButton);
                            HBox.setHgrow(deleteButton, Priority.ALWAYS);
                            deleteButton.setMaxWidth(Double.MAX_VALUE);
                            setGraphic(container);
                        }
                    }
                };
            }
        });

        // Enable editing for score column
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        scoreCol.setOnEditCommit(event -> {
            SelectionModel selectionModel = event.getRowValue();
            selectionModel.setScore(event.getNewValue());
            updateScoreInDatabase(selectionModel);
        });

        Selectiontable.setEditable(true);

        // Set LRN column click event handler
        lrnCol.setCellFactory(column -> {
            return new TableCell<SelectionModel, String>() {
                @Override
                protected void updateItem(String lrn, boolean empty) {
                    super.updateItem(lrn, empty);
                    if (lrn == null || empty) {
                        setText(null);
                    } else {
                        setText(lrn);
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1) { // Single click
                                openSelectionLinegraph(lrn);
                            }
                        });
                    }
                }
            };
        });
    }

    private void openSelectionLinegraph(String lrn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionLinegraph.fxml"));
            Parent root = loader.load();

            SelectionLinegraph controller = loader.getController();
            controller.setLRN(lrn); // Pass LRN to SelectionLinegraph controller

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(SelectionModel selectionModel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this selection entry?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deleteQuery = "DELETE FROM DailySelection WHERE SelectionID = ?";
            try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                stmt.setInt(1, selectionModel.getSelectionID());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Selection entry deleted successfully.");
                    refreshTable();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Failed to delete selection entry.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while deleting selection entry: " + e.getMessage());
            }
        }
    }

    private void updateScoreInDatabase(SelectionModel selectionModel) {
        String updateQuery = "UPDATE DailySelection SET Score = ? WHERE SelectionID = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {

            preparedStatement.setInt(1, selectionModel.getScore());
            preparedStatement.setInt(2, selectionModel.getSelectionID());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error updating score in database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void refreshTable() {
        selectionModelList.clear();

        String query = "SELECT ds.SelectionID, ds.LRN, si.Firstname, si.Lastname, ti.TeacherFname, ti.TeacherLname, lt.LanguageType, m.ResourceTitle, m.URL, ds.Score, ds.date " +
                "FROM DailySelection ds " +
                "JOIN Materials m ON ds.MaterialsId = m.MaterialsId " +
                "JOIN Languagetype lt ON ds.LanguageTypeID = lt.LanguageID " +
                "JOIN student_info si ON ds.LRN = si.LRN " +
                "JOIN teacher_info ti ON ds.AdviserID = ti.EmployeeID " +
                "WHERE ds.AdviserID = ?";  // Filter by adviser ID

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, currentAdviserID); // Set the current adviser ID
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                selectionModelList.add(new SelectionModel(
                        resultSet.getInt("SelectionID"),
                        resultSet.getString("LRN"),
                        resultSet.getString("Firstname") + " " + resultSet.getString("Lastname"),
                        resultSet.getString("TeacherFname") + " " + resultSet.getString("TeacherLname"),
                        resultSet.getString("ResourceTitle"),
                        resultSet.getString("LanguageType"),
                        resultSet.getString("URL"), // Include URL here
                        resultSet.getInt("Score"),
                        resultSet.getDate("date").toLocalDate()
                ));
            }

            Selectiontable.setItems(selectionModelList);

        } catch (Exception e) {
            System.err.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<SelectionModel> filteredList = selectionModelList.filtered(selection ->
                selection.getLRN().toLowerCase().contains(searchText) ||
                        selection.getStudentName().toLowerCase().contains(searchText) ||
                        selection.getResourceTitle().toLowerCase().contains(searchText) ||
                        selection.getLanguageType().toLowerCase().contains(searchText)
        );
        Selectiontable.setItems(filteredList);
    }

    @FXML
    private void openAddSelection(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addselection.fxml"));
            Parent root = loader.load();
            Addselection controller = loader.getController();
            controller.setSelectionController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentAdviserID(String adviserID) {
        this.currentAdviserID = adviserID;
    }

    public String getCurrentAdviserID() {
        return currentAdviserID;
    }
}
