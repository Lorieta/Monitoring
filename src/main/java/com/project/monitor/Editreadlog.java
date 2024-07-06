package com.project.monitor;

import Tables.ReadinglogModel;
import Tables.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Editreadlog implements Initializable {

    private static final String DATABASE_URL = Config.DATABASE;
    private static final String DATABASE_USER = Config.USER;
    private static final String DATABASE_PASSWORD = Config.PASSWORD;

    private final dbFunctions db = new dbFunctions();

    @FXML
    private ComboBox<Student> LRNfield;
    @FXML
    private TextField fnameField;
    @FXML
    private TextField lnameField;
    @FXML
    private ComboBox<String> resourcecb;
    @FXML
    private DatePicker datestarted;
    @FXML
    private DatePicker datefinished;
    @FXML
    private TextArea remarks;

    private ReadinglogModel readinglog;
    private Stage dialogStage;
    private boolean saveClicked = false;

    private Readinglog tableController;  // Reference to the Readinglog controller

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLRNs();
        loadResources();
        setupLRNSelectionListener();
        setupLRNConverter(); // Add this line
        LRNfield.setPromptText("Select a student");
    }

    @FXML
    void updateReadinglog(MouseEvent event) {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All required fields must be filled.");
            return;
        }

        Connection conn = null;
        try {
            conn = db.connect_to_db(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            java.sql.Date sqlStartDate = java.sql.Date.valueOf(datestarted.getValue());
            java.sql.Date sqlEndDate = datefinished.getValue() != null ? java.sql.Date.valueOf(datefinished.getValue()) : null;

            Student selectedStudent = LRNfield.getValue();
            int materialId = fetchMaterialId(resourcecb.getValue());
            String duration = sqlEndDate != null ? String.valueOf(calculateDuration(sqlStartDate, sqlEndDate)) : "0";

            boolean isUpdated = db.updateReadinglogInDatabase(conn,
                    readinglog.getLogid(),  // Assuming you need the log ID to update the correct record
                    selectedStudent.getLrn(),
                    materialId,
                    sqlStartDate,
                    sqlEndDate,
                    duration,
                    remarks.getText());

            if (isUpdated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reading log updated successfully.");
                if (tableController != null) {
                    tableController.refreshTable();
                }
                saveClicked = true;
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update reading log.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                // Log the resourceTitle that was not found
                System.out.println("ResourceTitle not found in Materials table: " + resourceTitle);
                throw new SQLException("Material ID not found for the given resource title.");
            }
        }
    }


    private int calculateDuration(java.sql.Date startDate, java.sql.Date endDate) {
        long differenceInMillis = endDate.getTime() - startDate.getTime();
        return (int) (differenceInMillis / (1000 * 60 * 60 * 24));
    }

    public void setReadinglog(ReadinglogModel readinglog) {
        this.readinglog = readinglog;
        // You might need to load LRN from database again to match it with the combo box items
        LRNfield.setValue(new Student(readinglog.getLrn(), readinglog.getFname(), readinglog.getLname(), "", 0, ""));
        resourcecb.setValue(readinglog.getResourceType());
        datestarted.setValue(readinglog.getDateStarted().toLocalDate());
        if (readinglog.getDateFinished() != null) {
            datefinished.setValue(readinglog.getDateFinished().toLocalDate());
        }
        remarks.setText(readinglog.getComment());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTableController(Readinglog tableController) {
        this.tableController = tableController;
    }
}
