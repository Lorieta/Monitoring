package com.project.monitor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Controller {

    @FXML
    private Label toSignUP;
    @FXML
    private TextField teacherIDField;
    @FXML
    private TextField fnameField;
    @FXML
    private TextField lnameField;
    @FXML
    private TextField gradeandsectionField;
    @FXML
    private TextField spe;
    @FXML
    private TextField Email;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField loginPassField;

    private final String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
    private Stage stage;
    private Scene scene;
    private static String loggedInTeacherID;
    private Runnable onTeacherAddedCallback;
    public static boolean checker(String str) {
        return str == null || str.trim().isEmpty();
    }


    public void switchToSignUp(Stage currentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminhomedashboard.fxml"));
            Parent root = loader.load();

            // Create a new stage for the admin dashboard
            Stage signUpStage = new Stage();
            signUpStage.setScene(new Scene(root));
            signUpStage.setTitle("Admin Dashboard");

            // Get the controller instance
            Admin controller = loader.getController();
            // Set any necessary data or properties on the controller here
            // For example:
            // controller.setCurrentAdminID("admin");

            // Show the admin dashboard stage
            signUpStage.show();

            // Close the current stage
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while opening the admin dashboard: " + e.getMessage(), "Error");
        }
    }


    public void switchToLogin(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void signUpGetter(MouseEvent event) throws SQLException {
        dbFunctions db = new dbFunctions();
        Connection conn = null;
        try {
            conn = db.connect_to_db(Config.DATABASE, Config.USER, Config.PASSWORD);
            String teacherID = teacherIDField.getText().trim();
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String password = passwordField.getText().trim();
            String gradeandsection = gradeandsectionField.getText().trim();
            String email = Email.getText().trim();
            String specialization = spe.getText().trim();

            if (!checker(teacherID) && !checker(fname) && !checker(lname) && !checker(password)
                    && !checker(gradeandsection) && !checker(email) && !checker(specialization)) {

                // Check if grade_section already exists
                if (isGradeSectionExists(conn, gradeandsection)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "A teacher is already assigned to grade and section: " + gradeandsection);
                    return;
                }

                db.signup(conn, "teacher_info", teacherID, fname, lname, gradeandsection, password, email, specialization);
                notifyTeacherAdded();  // Notify that a teacher was added
                clearFields();  // Clear the input fields
                showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher account created successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill in all fields.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
    }

    private boolean isGradeSectionExists(Connection conn, String gradeSection) throws SQLException {
        dbFunctions db = new dbFunctions();
        String query = "SELECT COUNT(*) FROM teacher_info WHERE grade_section = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, gradeSection);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public void setOnTeacherAddedCallback(Runnable callback) {
        this.onTeacherAddedCallback = callback;
    }

    private void notifyTeacherAdded() {
        if (onTeacherAddedCallback != null) {
            onTeacherAddedCallback.run();
        }
    }

    private void clearFields() {
        teacherIDField.clear();
        fnameField.clear();
        lnameField.clear();
        passwordField.clear();
        gradeandsectionField.clear();
        Email.clear();
        spe.clear();
    }

    public void login(MouseEvent event) {
        dbFunctions db = new dbFunctions();
        Connection conn = null;
        try {
            conn = db.connect_to_db(Config.DATABASE, Config.USER, Config.PASSWORD);
            String employeeid = loginField.getText();
            String password = loginPassField.getText();

            if (!checker(employeeid) && !checker(password)) {
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (employeeid.equals("admin") && password.equals("admin")) {
                    switchToSignUp(currentStage); // Switch to admin dashboard
                } else {
                    dbFunctions.Teacher teacher = db.loginAndGetTeacher(conn, "teacher_info", employeeid, password);
                    if (teacher != null) {
                        // Successfully logged in
                        navigateToDashboard(event, teacher);

                        // Close all other windows except the dashboard
                        Platform.runLater(() -> {
                            for (Window window : Window.getWindows()) {
                                if (window instanceof Stage && window != currentStage) {
                                    ((Stage) window).close();
                                }
                            }
                        });
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "Login failed. No account found with the provided credentials.", "Login Failed");
                    }
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Missing fields detected. Fill all fields.", "Missing Fields");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred during login: " + e.getMessage(), "Login Error");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    private void navigateToDashboard(MouseEvent event, dbFunctions.Teacher teacher) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homedashboard.fxml"));
        Parent root = loader.load();

        // Pass the logged-in teacher's ID and full name to the dashboard controller
        Homedashboard controller = loader.getController();
        controller.setTeacherID(teacher.getId()); // Assuming setAdviserID exists in Homedashboard controller
        controller.setTeacherName(teacher.getFullName());


        // Set scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }


    public void showAlert(Alert.AlertType information, String messageText, String s) {
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Error");
        message.setHeaderText(null);
        message.setContentText(messageText);
        message.showAndWait();
    }

    public static String getLoggedInTeacherID() {
        return loggedInTeacherID;
    }
}
