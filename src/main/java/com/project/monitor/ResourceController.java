package com.project.monitor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import Tables.ResourceModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceController extends Controller implements Initializable {

    String Database = Config.DATABASE;
    String lUser = Config.USER;
    String Password = Config.PASSWORD;

    @FXML
    private TableView<ResourceModel> MaterialTable;

    @FXML
    private TableColumn<ResourceModel, Void> actioncol;

    @FXML
    private TableColumn<ResourceModel, String> authorcol;

    @FXML
    private TableColumn<ResourceModel, Date> datecol;

    @FXML
    private TableColumn<ResourceModel, String> languagecol;

    @FXML
    private TableColumn<ResourceModel, Integer> materialIDcol;

    @FXML
    private TableColumn<ResourceModel, String> resourcetitlecol;

    @FXML
    private TableColumn<ResourceModel, String> resourcetypecol;

    @FXML
    private TableColumn<ResourceModel, String> urlcol;

    @FXML
    private TextField searchField;

    private final ObservableList<ResourceModel> ResourceList = FXCollections.observableArrayList();
    private final dbFunctions db = new dbFunctions();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        refreshTable();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        materialIDcol.setCellValueFactory(new PropertyValueFactory<>("materialid"));
        resourcetitlecol.setCellValueFactory(new PropertyValueFactory<>("resourceTitle"));
        urlcol.setCellValueFactory(new PropertyValueFactory<>("URL"));
        authorcol.setCellValueFactory(new PropertyValueFactory<>("author_publisher"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("date_published"));
        languagecol.setCellValueFactory(new PropertyValueFactory<>("languageType"));
        resourcetypecol.setCellValueFactory(new PropertyValueFactory<>("resourceType"));

        urlcol.setCellFactory(column -> new TableCell<ResourceModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    setOnMouseClicked(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: blue; -fx-underline: true;");
                    setOnMouseClicked(event -> openWebPage(item));
                }
            }
        });

        actioncol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ResourceModel, Void> call(final TableColumn<ResourceModel, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.getStyleClass().add("edit-button");
                        deleteButton.getStyleClass().add("delete-button");

                        editButton.setOnAction(event -> {
                            ResourceModel resource = getTableView().getItems().get(getIndex());
                            handleEdit(resource);
                        });

                        deleteButton.setOnAction(event -> {
                            ResourceModel resource = getTableView().getItems().get(getIndex());
                            handleDelete(resource);
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

    private void handleEdit(ResourceModel resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editResource.fxml"));
            Parent parent = loader.load();

            Editresource editResourceController = loader.getController();
            Stage dialogStage = new Stage();
            editResourceController.setDialogStage(dialogStage);
            editResourceController.setResource(resource);

            Scene scene = new Scene(parent);

            dialogStage.setTitle("Edit Resource");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            if (editResourceController.isSaveClicked()) {
                refreshTable();
            }
        } catch (IOException e) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    private void handleDelete(ResourceModel resource) {
        try {
            Connection conn = db.connect_to_db(Database, lUser, Password);
            String query = "DELETE FROM Materials WHERE MaterialsId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, resource.getMaterialid());
            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            refreshTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshTable() {
        ResourceList.clear();
        try {
            Connection conn = db.connect_to_db(Database, lUser, Password);
            String query = "SELECT m.MaterialsId, m.ResourceTitle, m.URL, m.AuthorPublisher, m.Date_Published, lt.LanguageType, rt.ResourceType " +
                    "FROM Materials m " +
                    "JOIN Languagetype lt ON m.TypeID = lt.LanguageID " +
                    "JOIN Resourcetype rt ON m.ResourceID = rt.ResourceID";

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ResourceList.add(new ResourceModel(
                        new SimpleIntegerProperty(resultSet.getInt("MaterialsId")),
                        new SimpleStringProperty(resultSet.getString("ResourceTitle")),
                        new SimpleStringProperty(resultSet.getString("URL")),
                        new SimpleStringProperty(resultSet.getString("AuthorPublisher")),
                        resultSet.getDate("Date_Published"),
                        new SimpleStringProperty(resultSet.getString("LanguageType")),
                        new SimpleStringProperty(resultSet.getString("ResourceType"))
                ));
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

            MaterialTable.setItems(ResourceList);

        } catch (Exception e) {
            System.out.println("Error during data refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        FilteredList<ResourceModel> filteredData = new FilteredList<>(ResourceList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(resource -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(resource.getMaterialid()).contains(lowerCaseFilter)) {
                    return true;
                } else if (resource.getResourceTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (resource.getURL().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (resource.getAuthor_publisher().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(resource.getDate_published()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        MaterialTable.setItems(filteredData);
    }

    @FXML
    void searchtb(ActionEvent event) {
        setupSearchFilter();
    }

    private void openWebPage(String url) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("xdg-open " + url);
            }
        } catch (Exception e) {
            System.out.println("Error opening URL: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening URL");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open the URL. Please check your internet connection and try again.");
            alert.showAndWait();
        }
    }

    @FXML
    void addBtn(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addResource.fxml"));
            Parent parent = loader.load();

            Addresource addResourceController = loader.getController();
            addResourceController.setTableController(this);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
