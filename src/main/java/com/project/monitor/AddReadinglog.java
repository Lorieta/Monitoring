
        package com.project.monitor;

import Tables.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.util.StringConverter;

public class AddReadinglog implements Initializable {

    private static final String DATABASE_URL = Config.DATABASE;
    private static final String DATABASE_USER = Config.USER;
    private static final String DATABASE_PASSWORD = Config.PASSWORD;

    private final dbFunctions db = new dbFunctions();

    @FXML private ComboBox<Student> LRNfield;
    @FXML private TextField fnameField;
    @FXML private TextField lnameField;
    @FXML private ComboBox<String> resourcecb;
    @FXML private DatePicker datestarted;
    @FXML private DatePicker datefinished;
    @FXML private TextArea remarks;

    private Readinglog tableController;
    private Homedashboard control;

    public void setTableController(Readinglog tableController) {
        this.tableController = tableController;
    }

    @FXML
    void addbtn(MouseEvent event) {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All required fields must be filled.");
            return;
        }

        Connection conn = null;
        try {
            conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            conn.setAutoCommit(false);

            java.sql.Date sqlStartDate = java.sql.Date.valueOf(datestarted.getValue());
            java.sql.Date sqlEndDate = datefinished.getValue() != null ? java.sql.Date.valueOf(datefinished.getValue()) : null;

            Student selectedStudent = LRNfield.getValue();

            int materialId = fetchMaterialId(resourcecb.getValue());
            int duration = sqlEndDate != null ? calculateDuration(sqlStartDate, sqlEndDate) : 0;

            boolean isAdded = db.addReadingLogToDatabase(conn,
                    selectedStudent.getLrn(),
                    materialId,
                    sqlStartDate,
                    sqlEndDate,
                    duration,
                    remarks.getText());

            if (isAdded) {
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reading log added successfully.");
                clearFields();

                if (tableController != null) {
                    tableController.refreshTable();
                }
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while adding reading log: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @FXML
    void clearFields() {
        LRNfield.getSelectionModel().clearSelection();
        fnameField.clear();
        lnameField.clear();
        datestarted.setValue(null);
        datefinished.setValue(null);
        remarks.clear();
        resourcecb.getSelectionModel().clearSelection();
    }

    private boolean validateInputs() {
        return LRNfield.getValue() != null &&
                datestarted.getValue() != null &&
                resourcecb.getValue() != null &&
                !remarks.getText().trim().isEmpty();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLRNs();
        loadResources();
        setupLRNSelectionListener();
        setupLRNConverter(); // Add this line
        LRNfield.setPromptText("Select a student");
    }

    private void loadLRNs() {
        String lrnQuery = "SELECT LRN, Firstname, Lastname FROM Student_info";

        try (Connection conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement lrnStmt = conn.prepareStatement(lrnQuery);
             ResultSet lrnRs = lrnStmt.executeQuery()) {

            while (lrnRs.next()) {
                String lrn = lrnRs.getString("LRN");
                String firstName = lrnRs.getString("Firstname");
                String lastName = lrnRs.getString("Lastname");

                // Use placeholder values for the unused fields
                String placeholder = "N/A";
                int placeholderInt = 0;

                Student student = new Student(lrn, firstName, lastName, placeholder, placeholderInt, placeholder);
                LRNfield.getItems().add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading LRN data: " + e.getMessage());
        }
    }


    private void loadResources() {
        String resourceQuery = "SELECT ResourceTitle FROM Materials";

        try (Connection conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement resourceStmt = conn.prepareStatement(resourceQuery);
             ResultSet resourceRs = resourceStmt.executeQuery()) {

            while (resourceRs.next()) {
                resourcecb.getItems().add(resourceRs.getString("ResourceTitle"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading resource data: " + e.getMessage());
        }
    }

    private void setupLRNSelectionListener() {
        LRNfield.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fnameField.setText(newValue.getFirstname());
                lnameField.setText(newValue.getLastname());
            } else {
                fnameField.clear();
                lnameField.clear();
            }
        });
    }
    private void setupLRNConverter() {
        // Create a StringConverter for Student
        StringConverter<Student> studentConverter = new StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student.getLrn() + " - " + student.getFirstname() + " " + student.getLastname();
            }

            @Override
            public Student fromString(String string) {
                return null; // Not used in this case
            }
        };

        // Set the StringConverter to the ComboBox
        LRNfield.setConverter(studentConverter);
    }



    private int fetchMaterialId(String resourceTitle) throws SQLException {
        String materialQuery = "SELECT MaterialsId FROM Materials WHERE ResourceTitle = ?";
        try (Connection conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(materialQuery)) {
            stmt.setString(1, resourceTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MaterialsId");
            } else {
                throw new SQLException("Material ID not found for the given resource title.");
            }
        }
    }

    private int calculateDuration(java.sql.Date startDate, java.sql.Date endDate) {
        long differenceInMillis = endDate.getTime() - startDate.getTime();
        return (int) (differenceInMillis / (1000 * 60 * 60 * 24));
    }
}