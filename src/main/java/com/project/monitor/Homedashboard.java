package com.project.monitor;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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
    private Stage stage;
    private Scene scene;
    private String css; // Assuming this is defined somewhere in your class

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
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(0);
            slide.setInterpolator(Interpolator.EASE_BOTH);
            slide.play();

            TranslateTransition mainSlide = new TranslateTransition(Duration.seconds(0.4), mainContent);
            mainSlide.setToX(0);
            mainSlide.setInterpolator(Interpolator.EASE_BOTH);
            mainSlide.play();

            isHidden = false;
        }
    }

    private void hideSlider() {
        if (!isHidden) {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), slider);
            slide.setToX(-190);
            slide.setInterpolator(Interpolator.EASE_BOTH);
            slide.play();

            TranslateTransition mainSlide = new TranslateTransition(Duration.seconds(0.4), mainContent);
            mainSlide.setToX(0);
            mainSlide.setInterpolator(Interpolator.EASE_BOTH);
            mainSlide.play();

            isHidden = true;
        }
    }

    @FXML
    void showSTD(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
        Parent root = loader.load();

        // Get the controller and set the teacher ID
        TableController controller = loader.getController();
        controller.setTeacherID(this.teacherID);

        // Create a new stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Student View");

        // Get the current stage (Homedashboard)
        Stage currentStage = (Stage) mainContent.getScene().getWindow();

        // Set the new stage size to be 80% of the current stage size
        double newWidth = currentStage.getWidth() * 0.8;
        double newHeight = currentStage.getHeight() * 0.8;

        stage.setWidth(newWidth);
        stage.setHeight(newHeight);

        // Center the new stage on the screen
        stage.setX(currentStage.getX() + (currentStage.getWidth() - newWidth) / 2);
        stage.setY(currentStage.getY() + (currentStage.getHeight() - newHeight) / 2);

        // Apply the blur effect to the main content
        mainContent.setEffect(blur);

        // Show the stage
        stage.show();

        // Remove the blur effect when the new window is closed
        stage.setOnHidden(e -> mainContent.setEffect(null));

        // Close the new window when clicking outside of it
        currentStage.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (!stage.getScene().getWindow().getScene().getRoot().getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                stage.close();
            }
        });
    }
}