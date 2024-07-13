package com.project.monitor;

import Tables.TeacherInfo;
import Tables.rankingScore;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class Admin implements Initializable {
    @FXML
    private Button Averagebtn;

    @FXML
    private TableView<TeacherInfo> Teacher;

    @FXML
    private TableColumn<TeacherInfo, String> actioncol;

    @FXML
    private TableColumn<TeacherInfo, String> emailcol;

    @FXML
    private TableColumn<TeacherInfo, String> gradeandsectioncol;

    @FXML
    private HBox mainContent;

    @FXML
    private Button materialbtn;
    @FXML
    private TableColumn<TeacherInfo, String> lname;
    @FXML
    private TableColumn<TeacherInfo, String> fname;

    @FXML
    private TableColumn<TeacherInfo, String> passwordcol;

    @FXML
    private Button readingResults;

    @FXML
    private Button rlbtn;

    @FXML
    private Button showmvf;

    @FXML
    private VBox slider;

    @FXML
    private TableColumn<TeacherInfo, String> specializationcol;

    @FXML
    private TableColumn<TeacherInfo, String> teachercol;



    @FXML
    private Label username;




    private dbFunctions db = new dbFunctions();

    private boolean isHidden = true;
    private GaussianBlur blur = new GaussianBlur(10);

    private List<TeacherInfo> teacherInfos = new ArrayList<>();
    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.setTranslateX(-190); // Initially hide the slider

        // Add event handlers for mouse enter and exit on the slider
        slider.setOnMouseEntered(event -> showSlider());
        slider.setOnMouseExited(event -> hideSlider());

        // Make the TableView editable
        Teacher.setEditable(true);

        // Initialize table columns
        teachercol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeID()));
        teachercol.setCellFactory(TextFieldTableCell.forTableColumn());
        teachercol.setOnEditCommit(event -> event.getRowValue().setEmployeeID(event.getNewValue()));

        lname.setCellValueFactory(new PropertyValueFactory<>("teacherlname"));
        lname.setCellFactory(TextFieldTableCell.forTableColumn());
        lname.setOnEditCommit(event -> event.getRowValue().setTeacherlname(event.getNewValue()));

        fname.setCellValueFactory(new PropertyValueFactory<>("teacherfname"));
        fname.setCellFactory(TextFieldTableCell.forTableColumn());
        fname.setOnEditCommit(event -> event.getRowValue().setTeacherfname(event.getNewValue()));

        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailcol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailcol.setOnEditCommit(event -> event.getRowValue().setEmail(event.getNewValue()));

        gradeandsectioncol.setCellValueFactory(new PropertyValueFactory<>("GradeSection"));
        gradeandsectioncol.setCellFactory(TextFieldTableCell.forTableColumn());
        gradeandsectioncol.setOnEditCommit(event -> event.getRowValue().setGradeSection(event.getNewValue()));

        specializationcol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        specializationcol.setCellFactory(TextFieldTableCell.forTableColumn());
        specializationcol.setOnEditCommit(event -> event.getRowValue().setSpecialization(event.getNewValue()));

        passwordcol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordcol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordcol.setOnEditCommit(event -> event.getRowValue().setPassword(event.getNewValue()));
        teachercol.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setEmployeeID(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        lname.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setTeacherlname(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        fname.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setTeacherfname(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        emailcol.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setEmail(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        gradeandsectioncol.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setGradeSection(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        specializationcol.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setSpecialization(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        passwordcol.setOnEditCommit(event -> {
            TeacherInfo teacher = event.getRowValue();
            teacher.setPassword(event.getNewValue());
            updateTeacherInDatabase(teacher);
        });

        actioncol.setCellFactory(new Callback<TableColumn<TeacherInfo, String>, TableCell<TeacherInfo, String>>() {
            @Override
            public TableCell<TeacherInfo, String> call(final TableColumn<TeacherInfo, String> param) {
                return new TableCell<TeacherInfo, String>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            TeacherInfo teacherInfo = getTableView().getItems().get(getIndex());
                            deleteTeacher(teacherInfo);
                        });
                        deleteButton.getStyleClass().add("delete-button");
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
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
        // Populate the table
        populateTeacherTable();
    }

    private void showSlider() {
        if (isHidden) {
            animateSlider(0); // Show slider
            isHidden = false;
        }
    }

    private void hideSlider() {
        if (!isHidden) {
            animateSlider(-190); // Hide slider
            isHidden = true;
        }
    }

    private void animateSlider(double targetX) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
        slide.setToX(targetX);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }


    @FXML
    void showResourceView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resource.fxml"));
            Parent root = loader.load();

            ResourceController controller = loader.getController();
            Stage stage = createAndShowStage(root, "Resource View");

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            showAlert("Error", "Failed to load Resource View: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    @FXML
    void showreadingresult(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OralandSilent.fxml"));
            Parent root = loader.load();

            OralandSilent controller = loader.getController();
            System.out.println("Before setting in OralandSilent: " + controller.getCurrentAdviserID());

            System.out.println("After setting in OralandSilent: " + controller.getCurrentAdviserID());

            Stage stage = createAndShowStage(root, "Philiri View");

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Philiri View: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    void showaveragedaily(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("averagedailyscore.fxml"));
            Parent root = loader.load();

            Stage stage = createAndShowStage(root, "Average Daily Score View");

            // Get the controller instance
            Averagedailyscore controller = loader.getController();
            // Set current adviser ID




            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Average Daily Score View: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    void showmvfreading(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("malevsfemale.fxml"));
            Parent root = loader.load();

            Malevsfemale controller = loader.getController();
            System.out.println("Before setting in OralandSilent: " + controller.getCurrentAdviserID());

            System.out.println("After setting in OralandSilent: " + controller.getCurrentAdviserID());

            Stage stage = createAndShowStage(root, "Philiri View");

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Philiri View: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }



    @FXML
    void showteacher(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();

            Controller controller = loader.getController();

            // Set up a callback for when a new teacher is added
            controller.setOnTeacherAddedCallback(this::refreshTeacherTable);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");

            // Set stage to be non-resizable
            stage.setResizable(false);

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> {
                removeBlurEffect();
                refreshTeacherTable();  // Refresh the table when the signup window is closed
            });

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load Sign Up view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }






    private Stage createAndShowStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle(title);

        // Set stage to be non-resizable
        stage.setResizable(false);

        Stage currentStage = (Stage) mainContent.getScene().getWindow();
        double currentWidth = currentStage.getWidth();
        double currentHeight = currentStage.getHeight();
        stage.setWidth(currentWidth * 0.8);
        stage.setHeight(currentHeight * 0.8);
        stage.setX(currentStage.getX() + (currentWidth - stage.getWidth()) / 2);
        stage.setY(currentStage.getY() + (currentHeight - stage.getHeight()) / 2);

        stage.show();
        return stage;
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindowOnClickOutside(Stage stage) {
        Stage currentStage = (Stage) mainContent.getScene().getWindow();

        // Add a mouse click filter to the current stage
        currentStage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!stage.getScene().getRoot().getBoundsInParent().contains(event.getSceneX() - stage.getX(), event.getSceneY() - stage.getY())) {
                stage.close();
            }
        });
    }

    private void applyBlurEffect() {
        // Apply the blur effect to the entire root component of the HomeDashboard
        mainContent.getScene().getRoot().setEffect(blur);
    }

    private void removeBlurEffect() {
        // Remove the blur effect from the entire root component of the HomeDashboard
        mainContent.getScene().getRoot().setEffect(null);
    }

    private void populateTeacherTable() {
        String query = "SELECT * FROM teacher_info";
        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Clear existing items
           Teacher.getItems().clear();

            // Populate table with database results
            // Populate table with database results
            while (resultSet.next()) {
                String teacherId = resultSet.getString("employeeid");
                String teacherFName = resultSet.getString("teacherfname");
                String teacherLName = resultSet.getString("teacherlname");
                String email = resultSet.getString("email");
                String gradeAndSection = resultSet.getString("grade_section");
                String specialization = resultSet.getString("specialization");
                String password = resultSet.getString("password");

                TeacherInfo teacher = new TeacherInfo(teacherId, teacherFName, teacherLName, gradeAndSection, password, email, specialization);
                Teacher.getItems().add(teacher);
            }


        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch teachers: " + e.getMessage(), Alert.AlertType.ERROR);

        }
    }
    private void updateTeacherInDatabase(TeacherInfo teacher) {
        String query = "UPDATE teacher_info SET teacherfname = ?, teacherlname = ?, email = ?, grade_section = ?, specialization = ?, password = ? WHERE employeeid = ?";
        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, teacher.getTeacherfname());
            statement.setString(2, teacher.getTeacherlname());
            statement.setString(3, teacher.getEmail());
            statement.setString(4, teacher.getGradeSection());
            statement.setString(5, teacher.getSpecialization());
            statement.setString(6, teacher.getPassword());
            statement.setString(7, teacher.getEmployeeID());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Teacher updated successfully");
            } else {
                System.out.println("Failed to update teacher");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update teacher: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void deleteTeacher(TeacherInfo teacher) {
        String query = "DELETE FROM teacher_info WHERE employeeid = ?";
        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, teacher.getEmployeeID());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                Teacher.getItems().remove(teacher);
                System.out.println("Teacher deleted successfully");
            } else {
                System.out.println("Failed to delete teacher");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete teacher: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshTeacherTable() {
        Teacher.getItems().clear();
        populateTeacherTable();
    }
}
