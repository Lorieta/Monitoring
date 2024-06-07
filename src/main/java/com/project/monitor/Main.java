package com.project.monitor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;




public class Main extends Application {

    String css = this.getClass().getResource("application.css").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {


        try{
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));


            Scene scene = new Scene(root);
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("Monitoring");
            stage.setScene(scene);

            stage.show();




        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public static void main(String[] args) {
        dbFunctions db = new dbFunctions();
        db.connect_to_db("projectdb","postgres","123");



        launch(args);
    }


}