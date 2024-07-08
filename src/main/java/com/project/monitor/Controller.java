package com.project.monitor;

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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class Controller  {

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
    private TextField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField loginPassField;

    private final String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
    private Stage stage;
    private Scene scene;
    private static String loggedInTeacherID;

    public static boolean checker(String str) {
        return str == null || str.trim().isEmpty();
    }

    public void switchToSignUp(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
        stage = ((Stage)((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLogin(MouseEvent event)throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        stage = ((Stage)((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void signUpGetter(MouseEvent event) {
        dbFunctions db = new dbFunctions();
        Connection conn = db.connect_to_db("projectdb","postgres","123");
        String teacherID = teacherIDField.getText();
        String fname = fnameField.getText();
        String lname = lnameField.getText();
        String password = passwordField.getText();
        String gradeandsection = gradeandsectionField.getText();

        if (!checker(teacherID)&&!checker(fname)&&!checker(lname)&&!checker(password)&&!checker(gradeandsection)){
            db.signup(conn,"teacher_info", teacherID,fname,lname,gradeandsection,password);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Fill all Fields", "Reading log entry deleted successfully.");
        }
    }

    public void login(MouseEvent event) throws IOException, SQLException {
        dbFunctions db = new dbFunctions();
        Connection conn = null;
        try {
            conn = db.connect_to_db("projectdb", "postgres", "123");
            String employeeid = loginField.getText();
            String password = loginPassField.getText();

            if (!checker(employeeid) && !checker(password)) {
                dbFunctions.Teacher teacher = db.loginAndGetTeacher(conn, "teacher_info", employeeid, password);
                if (teacher != null) {
                    // Successfully logged in
                   String adviserID = employeeid; // Set adviser ID
                    navigateToDashboard(event, teacher);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Login failed. No account found with the provided credentials.", "Login Failed");
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Missing fields detected. Fill all fields.", "Missing Fields");
            }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Homedashboard.fxml"));
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
