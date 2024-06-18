package com.project.monitor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;


public class Main extends Application {



    private String css = this.getClass().getResource("application.css").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
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
        String Database = System.getenv("POSTGRESQL_DATABASE");
        String lUser = System.getenv("POSTGRESQL_USER");
        String Password = System.getenv("POSTGRESQL_PASSWORD");

        dbFunctions db = new dbFunctions();
        Connection conn = db.connect_to_db("projectdb","postgres","123");

        launch(args);

    }


}