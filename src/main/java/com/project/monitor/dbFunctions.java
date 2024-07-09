package com.project.monitor;

import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDate;

public class dbFunctions extends Controller {



    public Connection connect_to_db(String dbname,String user, String pass) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){

                System.out.println("Connection is Established");
            }else{
                System.out.println("Connection Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return conn;
    }

    //LOGIN
    public Teacher loginAndGetTeacher(Connection conn, String tablename, String employeeid, String password) {
        String query = String.format("SELECT * FROM %s WHERE employeeid = ? AND password = ?", tablename);

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, employeeid);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Teacher found, retrieve teacher's information
                    String fname = resultSet.getString("teacherfname");
                    String lname = resultSet.getString("teacherlname");

                    // Construct and return a Teacher object with ID, full name, etc.
                    return new Teacher(employeeid, fname + " " + lname);
                } else {
                    // No teacher found with the given credentials
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }


    //SIGNUP
    public boolean signup(Connection conn, String tablename, String teacherid, String fname, String lname, String section, String password) {
        try {
            String query = "INSERT INTO " + tablename + " (employeeid, teacherfname, teacherlname, grade_section, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, teacherid);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, section);
            statement.setString(5, password);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted);
            if (rowsInserted > 0) {
                System.out.println("Teacher signed up successfully.");
                return true;
            } else {
                System.out.println("Failed to sign up teacher.");
                return false;
            }



        } catch (SQLException e) {
            System.out.println("An error occurred while signing up teacher: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




    //INSERT STUDENT PROFILE
    public void AddStudent(Connection conn, String tablename, String lrn, String fname, String lname, String gender, String age) {
        try {
            // Get the logged-in teacher's ID
            String adviserID = Controller.getLoggedInTeacherID();

            // Prepare the SQL query with the adviserID (employeeID of the teacher)
            String query = "INSERT INTO student_info (lrn, firstname, lastname, gender, age, adviserID) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, lrn);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, gender);
            statement.setString(5, age);
            statement.setString(6, adviserID);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted);
            if (rowsInserted > 0) {
                System.out.println("ADDED STUDENT");
            } else {
                System.out.println("Failed ADD STUDENT.");
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("An error occurred while adding student: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void updateStudentInDatabase(Connection conn, String tablename, String firstname, String lastname, String gender, int age, String lrn) {
        try {
            // Get the logged-in teacher's ID
            String adviserID = Controller.getLoggedInTeacherID();

            // Prepare the SQL query with the adviserID (employeeID of the teacher)
            String query = "UPDATE " + tablename + " SET firstname = ?, lastname = ?, gender = ?, age = ?, adviserID = ? WHERE lrn = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setString(3, gender);
            preparedStatement.setInt(4, age);
            preparedStatement.setString(5, adviserID);
            preparedStatement.setString(6, lrn);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Updated student successfully.");
            } else {
                System.out.println("Failed to update student.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void deleteStudent(Connection conn, String lrn) throws SQLException {
        String query = "DELETE FROM student_info WHERE lrn = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, lrn);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Student deleted successfully");
        } else {
            System.out.println("Failed to delete student");
        }

        preparedStatement.close();
    }


    public boolean addResourceToDatabase(Connection conn, String title, String url, String author, java.sql.Date datePublished, String languageType, String resourceType) throws SQLException {
        String query = "INSERT INTO Materials (ResourceTitle, URL, AuthorPublisher, Date_Published, TypeID, ResourceID) " +
                "VALUES (?, ?, ?, ?, " +
                "(SELECT LanguageID FROM Languagetype WHERE LanguageType = ?), " +
                "(SELECT ResourceID FROM Resourcetype WHERE ResourceType = ?))";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, (url != null && !url.trim().isEmpty()) ? url : null);
            pstmt.setString(3, author);
            pstmt.setDate(4, datePublished);
            pstmt.setString(5, languageType);
            pstmt.setString(6, resourceType);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating resource failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("New resource added with ID: " + generatedKeys.getInt(1));
                    return true;
                } else {
                    throw new SQLException("Creating resource failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while adding resource: " + e.getMessage());
            throw e;
        }
    }


    public boolean updateResourceInDatabase(Connection conn, int materialID, String title, String url, String author, java.sql.Date datePublished, String languageType, String resourceType) {
        try {
            String query = "UPDATE Materials SET ResourceTitle = ?, URL = ?, AuthorPublisher = ?, Date_Published = ?, TypeID = (SELECT LanguageID FROM Languagetype WHERE LanguageType = ?), ResourceID = (SELECT ResourceID FROM Resourcetype WHERE ResourceType = ?) WHERE MaterialsId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, url != null ? url : "");
            preparedStatement.setString(3, author);
            preparedStatement.setDate(4, datePublished);
            preparedStatement.setString(5, languageType);
            preparedStatement.setString(6, resourceType);
            preparedStatement.setInt(7, materialID);

            int rowsUpdated = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addReadingLogToDatabase(Connection conn, String lrn, int materialId, Date dateStarted, Date dateFinished, int duration, String comments) {
        String insertQuery = "INSERT INTO reading_log (lrn, materialid, datestarted, datefinished, duration, comment, adviserid) " +
                "VALUES (?, ?, ?, ?, ?, ?, (SELECT adviserid FROM student_info WHERE lrn = ?))";

        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, lrn);
            pstmt.setInt(2, materialId);
            pstmt.setDate(3, dateStarted);
            pstmt.setDate(4, dateFinished);
            pstmt.setInt(5, duration);
            pstmt.setString(6, comments);
            pstmt.setString(7, lrn); // Set LRN for the subquery

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateReadinglogInDatabase(Connection conn, int logId, String lrn, int materialId, java.sql.Date dateStarted, java.sql.Date dateFinished, String duration, String comment) {
        String updateQuery = "UPDATE reading_log SET lrn = ?, materialid = ?, datestarted = ?, datefinished = ?, duration = ?, comment = ?, " +
                "adviserid = (SELECT adviserid FROM student_info WHERE lrn = ?) WHERE logid = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, lrn);
            pstmt.setInt(2, materialId);
            pstmt.setDate(3, dateStarted);
            pstmt.setDate(4, dateFinished);
            pstmt.setString(5, duration);
            pstmt.setString(6, comment);
            pstmt.setString(7, lrn); // Set LRN for the subquery
            pstmt.setInt(8, logId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }






    public static class Teacher {
        private String id;
        private String fullName;

        public Teacher(String id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        public String getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }
    }



}


































