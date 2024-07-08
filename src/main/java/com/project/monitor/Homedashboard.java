package com.project.monitor;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
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

    private boolean isHidden = true;
    private GaussianBlur blur = new GaussianBlur(10);
    private String teacherID;
    private String teacherName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.setTranslateX(-190); // Initially hide the slider

        // Add event handlers for mouse enter and exit on the main content area
        slider.setOnMouseMoved(event -> showSlider());
        slider.setOnMouseExited(event -> hideSlider());
    }

    public void setTeacherID(String id) {
        this.teacherID = id;
    }

    public void setTeacherName(String name) {
        this.teacherName = name;
        username.setText(name); // Set the name in the Label
    }

    private void showSlider() {
        if (isHidden) {
            animateSlider(0); // Show slider
            animateMainContent(0); // Slide main content to make space for slider
            isHidden = false;
        }
    }

    private void hideSlider() {
        if (!isHidden) {
            animateSlider(-190); // Hide slider
            animateMainContent(0); // Slide main content back to its original position
            isHidden = true;
        }
    }

    private void animateSlider(double targetX) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
        slide.setToX(targetX);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    private void animateMainContent(double targetX) {
        TranslateTransition mainSlide = new TranslateTransition(Duration.seconds(0.4), mainContent);
        mainSlide.setToX(targetX);
        mainSlide.setInterpolator(Interpolator.EASE_BOTH);
        mainSlide.play();
    }

    @FXML
    void showStudentView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
            Parent root = loader.load();

            TableController controller = loader.getController();
            controller.setTeacherID(this.teacherID);

            Stage stage = createAndShowStage(root, "Student View");

            // Apply blur effect to main content
            mainContent.setEffect(blur);

            // Remove blur effect on stage close
            stage.setOnHidden(e -> mainContent.setEffect(null));

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

            // Refresh the table after setting the currentAdviserID
            controller.refreshTable();

            System.out.println(teacherID);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Selection View");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Selection View: " + e.getMessage(), Alert.AlertType.ERROR);
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
        double newWidth = currentStage.getWidth() * 0.8;
        double newHeight = currentStage.getHeight() * 0.8;
        stage.setWidth(newWidth);
        stage.setHeight(newHeight);
        stage.setX(currentStage.getX() + (currentStage.getWidth() - newWidth) / 2);
        stage.setY(currentStage.getY() + (currentStage.getHeight() - newHeight) / 2);

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
}
