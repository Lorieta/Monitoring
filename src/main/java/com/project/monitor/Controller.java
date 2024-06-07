package com.project.monitor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private String css = this.getClass().getResource("application.css").toExternalForm();
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

    }


