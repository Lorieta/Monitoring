module com.project.monitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.project.monitor to javafx.fxml;
    exports com.project.monitor;
}