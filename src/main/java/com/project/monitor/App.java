package com.project.monitor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;



public class App extends Application {
    private String css = this.getClass().getResource("application.css").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("student.fxml"));
            Scene scene = new Scene(root);
            String css = this.getClass().getResource("application.css").toExternalForm();

            scene.getStylesheets().add(css);
            stage.setTitle("Monitoring");
            stage.setScene(scene);
            stage.show();

        }catch (Exception e){
            e.printStackTrace();
            //

        }

    }
}