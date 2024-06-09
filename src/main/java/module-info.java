module com.project.monitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.postgresql.jdbc;


    opens com.project.monitor to javafx.fxml;
    exports com.project.monitor;
}