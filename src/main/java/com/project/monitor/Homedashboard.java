package com.project.monitor;

import Tables.Student;
import Tables.rankingScore;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

public class Homedashboard implements Initializable {

    @FXML
    private Label username;

    @FXML
    private Button asbtn;

    @FXML
    private Button dcbtn;

    @FXML
    private Button hbtn;

    @FXML
    private Button logout;

    @FXML
    private HBox mainContent;

    @FXML
    private Button phbtn;

    @FXML
    private Button rlbtn;

    @FXML
    private Button sdbtn;

    @FXML
    private VBox slider;

    @FXML
    private Label lblmaterials;

    @FXML
    private Label lblstudent;

    @FXML
    private Button readingResults;

    @FXML
    private TableColumn<rankingScore, String> lrncol;

    @FXML
    private TableColumn<rankingScore, String> namecol;

    @FXML
    private TableColumn<rankingScore, Integer> totalscorecol;

    @FXML
    private TableView<rankingScore> scoreRanking;

    private List<rankingScore> students = new ArrayList<>();
    private dbFunctions db = new dbFunctions();

    private boolean isHidden = true;
    private GaussianBlur blur = new GaussianBlur(10);
    private String teacherID;
    private String teacherName;

    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        slider.setTranslateX(-190); // Initially hide the slider

        // Add event handlers for mouse enter and exit on the slider
        slider.setOnMouseEntered(event -> showSlider());
        slider.setOnMouseExited(event -> hideSlider());

        // Refresh the table when initializing
        refresh();
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
    void showStudentView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
            Parent root = loader.load();

            TableController controller = loader.getController();
            controller.setTeacherID(this.teacherID);

            Stage stage = createAndShowStage(root, "Student View");

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            showAlert("Error", "Failed to load Student View: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
    void showSelectionView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selection.fxml"));
            Parent root = loader.load();

            Selection controller = loader.getController();
            controller.setCurrentAdviserID(this.teacherID);
            System.out.println("Current Adviser ID in Homedashboard: " + this.teacherID);
            // Refresh the table after setting the currentAdviserID
            controller.refreshTable();

            Stage stage = createAndShowStage(root, "Selection View");

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Selection View: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void readlogsShow(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reaadinglog.fxml"));
            Parent root = loader.load();

            Stage stage = createAndShowStage(root, "Reading Log View");

            Readinglog controller = loader.getController();
            System.out.println("Before setting in OralandSilent: " + controller.getCurrentAdviserID());
            controller.setCurrentAdviserID(this.teacherID);
            System.out.println("After setting in OralandSilent: " + controller.getCurrentAdviserID());

            // Apply blur effect to the entire window
            applyBlurEffect();

            // Remove blur effect on stage close
            stage.setOnHidden(e -> removeBlurEffect());

            // Close the new window when clicking outside of it
            closeWindowOnClickOutside(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Reading Log View: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void showphiliri(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("philiri.fxml"));
            Parent root = loader.load();

            Stage stage = createAndShowStage(root, "Philiri View");

            PhiliriResults controller = loader.getController();
            controller.setCurrentAdviserID(this.teacherID);
            System.out.println("Current Adviser ID in Homedashboard: " + this.teacherID);

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
            controller.setCurrentAdviserID(this.teacherID);
            System.out.println("Current Adviser ID in Homedashboard: " + this.teacherID);

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
    void logout(MouseEvent event) {
        // Implement logout functionality
    }

    private Stage createAndShowStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);

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

    @FXML
    void showreadingresult(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OralandSilent.fxml"));
            Parent root = loader.load();

            OralandSilent controller = loader.getController();
            System.out.println("Before setting in OralandSilent: " + controller.getCurrentAdviserID());
            controller.setCurrentAdviserID(this.teacherID);
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

    public void setTeacherID(String id) {
        this.teacherID = id;
        System.out.println("Teacher ID set in Homedashboard: " + this.teacherID);
        refresh();
        setupTableColumns();
    }

    public void setTeacherName(String name) {
        this.teacherName = name;
    }

    public void refresh() {
        students.clear(); // Clear previous data

        String query = "SELECT ds.LRN, ds.adviserid, CONCAT(si.Firstname, ' ', si.Lastname) AS Name, SUM(ds.Score) AS TotalScore " +
                "FROM dailyselection ds " +
                "JOIN student_info si ON ds.LRN = si.LRN " +
                "JOIN result pr ON ds.LRN = pr.LRN " +
                "WHERE ds.adviserid = ? " +
                "GROUP BY ds.LRN, ds.adviserid, si.Firstname, si.Lastname";

        try (Connection connection = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, teacherID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String lrn = resultSet.getString("LRN");
                String adviserid = resultSet.getString("adviserid");
                String name = resultSet.getString("Name");
                int totalScore = resultSet.getInt("TotalScore");

                students.add(new rankingScore(lrn, adviserid, name, totalScore));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scoreRanking.getItems().setAll(students); // Update the table with the new data
    }

    private void setupTableColumns() {
        lrncol.setCellValueFactory(new PropertyValueFactory<>("lrn"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalscorecol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        totalscorecol.setComparator(Comparator.reverseOrder());


        scoreRanking.getSortOrder().add(totalscorecol);
    }


    @FXML
    void showmvfreading(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("malevsfemale.fxml"));
            Parent root = loader.load();

            Malevsfemale controller = loader.getController();
            System.out.println("Before setting in OralandSilent: " + controller.getCurrentAdviserID());
            controller.setCurrentAdviserID(this.teacherID);
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
}
