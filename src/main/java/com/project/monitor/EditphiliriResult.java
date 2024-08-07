package com.project.monitor;

import Tables.ReadinglogModel;
import Tables.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditphiliriResult implements Initializable {

    @FXML
    private ComboBox<Student> LRNfield;

    @FXML
    private Button updatebtn;

    @FXML
    private DatePicker daterecorded;

    @FXML
    private ComboBox<String> languagetypecb;

    @FXML
    private TextField lnameField;

    @FXML
    private ComboBox<String> oralcb;

    @FXML
    private TextArea remarks;

    @FXML
    private ComboBox<String> silentcb;

    private PhiliriResults tableController;
    private static final String DATABASE = Config.DATABASE;
    private static final String USER = Config.USER;
    private static final String PASSWORD = Config.PASSWORD;
    private final dbFunctions db = new dbFunctions();
    private int philiriResultID;

    @FXML
    void clearFields() {
        LRNfield.getSelectionModel().clearSelection();
        lnameField.clear();
        daterecorded.setValue(null);
        languagetypecb.getSelectionModel().clearSelection();
        oralcb.getSelectionModel().clearSelection();
        silentcb.getSelectionModel().clearSelection();
        remarks.clear();
    }

    private boolean validateInputs() {
        return LRNfield.getValue() != null &&
                daterecorded.getValue() != null &&
                languagetypecb.getValue() != null &&
                oralcb.getValue() != null &&
                silentcb.getValue() != null &&
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
        loadLanguageTypes();
        setupLRNSelectionListener();
        setupLRNConverter();
        setupLanguageTypeListener();
        LRNfield.setPromptText("Select a student");
    }

    private void loadLRNs() {
        String lrnQuery = "SELECT LRN, Firstname, Lastname FROM Student_info";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement lrnStmt = conn.prepareStatement(lrnQuery);
             ResultSet lrnRs = lrnStmt.executeQuery()) {

            while (lrnRs.next()) {
                String lrn = lrnRs.getString("LRN");
                String firstName = lrnRs.getString("Firstname");
                String lastName = lrnRs.getString("Lastname");

                Student student = new Student(lrn, firstName, lastName, "N/A", 0, "N/A","N/A");
                LRNfield.getItems().add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading LRN data: " + e.getMessage());
        }
    }

    private void loadLanguageTypes() {
        String query = "SELECT Languagetype FROM LanguageType";
        loadComboBoxData(languagetypecb, query, "Languagetype");
    }

    private void loadComboBoxData(ComboBox<String> comboBox, String query, String columnName) {
        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comboBox.getItems().add(rs.getString(columnName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading data: " + e.getMessage());
        }
    }

    private void setupLRNSelectionListener() {
        LRNfield.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String fullName =  newValue.getFirstname() + " " + newValue.getLastname();
                lnameField.setText(fullName);
            } else {
                lnameField.clear();
            }
        });
    }


    private void setupLRNConverter() {
        StringConverter<Student> studentConverter = new StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student.getFirstname() + " " + student.getLastname();
            }

            @Override
            public Student fromString(String string) {
                return null; // Not used in this case
            }
        };
        LRNfield.setConverter(studentConverter);
    }

    private void setupLanguageTypeListener() {
        languagetypecb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateResultOptions(newValue);
        });
    }

    private void updateResultOptions(String languageType) {
        oralcb.getItems().clear();
        silentcb.getItems().clear();

        String oralQuery = "SELECT oralresult FROM oral";
        String silentQuery = "SELECT silentresult FROM silent";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement oralStmt = conn.prepareStatement(oralQuery);
             PreparedStatement silentStmt = conn.prepareStatement(silentQuery);
             ResultSet oralRs = oralStmt.executeQuery();
             ResultSet silentRs = silentStmt.executeQuery()) {

            while (oralRs.next()) {
                String oralResult = oralRs.getString("oralresult");
                if ("English".equals(languageType)) {
                    if (oralResult.equals("NR") || oralResult.equals("FRUSTRATION") ||
                            oralResult.equals("INSTRUCTIONAL") || oralResult.equals("INDEPENDENT")) {
                        oralcb.getItems().add(oralResult);
                    }
                } else if ("Tagalog".equals(languageType)) {
                    if (oralResult.equals("DM") || oralResult.equals("Pagkabigo") ||
                            oralResult.equals("Pampagkatuto") || oralResult.equals("Malaya")) {
                        oralcb.getItems().add(oralResult);
                    }
                }
            }

            while (silentRs.next()) {
                String silentResult = silentRs.getString("silentresult");
                if ("English".equals(languageType)) {
                    if (silentResult.equals("NR") || silentResult.equals("FRUSTRATION") ||
                            silentResult.equals("INSTRUCTIONAL") || silentResult.equals("INDEPENDENT")) {
                        silentcb.getItems().add(silentResult);
                    }
                } else if ("Tagalog".equals(languageType)) {
                    if (silentResult.equals("DM") || silentResult.equals("Pagkabigo") ||
                            silentResult.equals("Pampagkatuto") || silentResult.equals("Malaya")) {
                        silentcb.getItems().add(silentResult);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading result options: " + e.getMessage());
        }
    }

    @FXML
    void updatePhiliriResult(MouseEvent event) {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
            return;
        }

        String query = "UPDATE Result SET LRN = ?, oralID = (SELECT orallID FROM oral WHERE oralresult = ?), " +
                "silentID = (SELECT SilentID FROM silent WHERE silentresult = ?), " +
                "LanguageID = (SELECT LanguageID FROM LanguageType WHERE Languagetype = ?), " +
                "DateRecorded = ?, Remarks = ? WHERE ResultID = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, LRNfield.getValue().getLrn());
            pstmt.setString(2, oralcb.getValue());
            pstmt.setString(3, silentcb.getValue());
            pstmt.setString(4, languagetypecb.getValue());
            pstmt.setDate(5, java.sql.Date.valueOf(daterecorded.getValue()));
            pstmt.setString(6, remarks.getText());
            pstmt.setInt(7, philiriResultID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Philiri result updated successfully.");
                clearFields();
                if (tableController != null) {
                    tableController.refreshTable();  // Refresh the table
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Philiri result.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating Philiri result: " + e.getMessage());
        }
    }

    public void setTableController(PhiliriResults philiriResults) {
        this.tableController = philiriResults;
    }

    public void setPhiliriResultID(int philiriResultID) {
        this.philiriResultID = philiriResultID;
        loadPhiliriResultData();
    }

    private void loadPhiliriResultData() {
        String query = "SELECT LRN, " +
                "(SELECT oralresult FROM oral WHERE oral.orallID = Result.oralID), " +
                "(SELECT silentresult FROM silent WHERE silent.SilentID = Result.silentID), " +
                "(SELECT Languagetype FROM LanguageType WHERE LanguageType.LanguageID = Result.LanguageID), " +
                "DateRecorded, Remarks FROM Result WHERE ResultID = ?";

        try (Connection conn = db.connect_to_db(DATABASE, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, philiriResultID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String lrn = rs.getString("LRN");
                String oralResult = rs.getString(2);
                String silentResult = rs.getString(3);
                String languageType = rs.getString(4);
                java.sql.Date dateRecorded = rs.getDate("DateRecorded");
                String remarksText = rs.getString("Remarks");

                // Set the LRN field
                for (Student student : LRNfield.getItems()) {
                    if (student.getLrn().equals(lrn)) {
                        LRNfield.setValue(student);
                        break;
                    }
                }

                // Set other fields
                oralcb.setValue(oralResult);
                silentcb.setValue(silentResult);
                languagetypecb.setValue(languageType);
                daterecorded.setValue(dateRecorded.toLocalDate());
                remarks.setText(remarksText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading Philiri result data: " + e.getMessage());
        }
    }

    public void setReadinglog(ReadinglogModel readinglogModel) {

    }
}
