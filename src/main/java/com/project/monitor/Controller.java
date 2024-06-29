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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class Controller extends Events {

    @FXML
    private Label toSignUP;
    @FXML
    private TextField teacherIDField;
    @FXML
    private TextField fnameField;
    @FXML
    private  TextField lnameField;
    @FXML
    private  TextField gradeandsectionField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private  PasswordField loginPassField;


    private final String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
    private Stage stage;
    private Scene scene;
    private Parent root;



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
            System.out.println("Missing fields detected.");
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Missing fields");
            message.setHeaderText(null);
            message.setContentText("Fill all fields");
            message.showAndWait();
        }
    }

    public void login(MouseEvent event) throws IOException {
        dbFunctions db = new dbFunctions();
        Connection conn = db.connect_to_db("projectdb","postgres","123");
        int teacherID = Integer.parseInt(loginField.getText());
        String password = loginPassField.getText();

        if(db.login(conn,"teacher",teacherID,password)){
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            stage = ((Stage)((Node)event.getSource()).getScene().getWindow());
            scene = new Scene(root);
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();

        }else {
            System.out.println("error");
        }

    }

    public void switchColor(MouseEvent event)throws IOException{
        this.toSignUP.setTextFill(Color.web("#0076a3"));
    }
    public void defaultColor(MouseEvent event)throws IOException{
        this.toSignUP.setTextFill(Color.web("#000000"));
    }


}


