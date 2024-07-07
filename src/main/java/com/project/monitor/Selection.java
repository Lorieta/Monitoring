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
        datecol.setCellValueFactory(new PropertyValueFactory<>("date"));

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

        // Make the score column editable
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        scoreCol.setOnEditCommit(event -> {
            SelectionModel selectionModel = event.getRowValue();
            selectionModel.setScore(event.getNewValue());
            updateScoreInDatabase(selectionModel);
        });

        Selectiontable.setEditable(true);
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
                    refreshTable(); // Refresh the table after deletion
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

        String query = "SELECT ds.SelectionID, ds.LRN, m.ResourceTitle, m.URL, lt.LanguageType, ds.Score, ds.date " +
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
