package com.project.monitor;

import java.sql.*;

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
    public boolean login(Connection conn, String tablename, int teacherID, String password) {
        try {
            String query = String.format("SELECT * FROM %s WHERE teacher_id = ? AND password = ?;", tablename);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, teacherID);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean loggedIn = resultSet.next(); // If there is a next row, login successful

            resultSet.close(); // Close resultSet
            preparedStatement.close(); // Close preparedStatement

            return loggedIn;
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }


    //SIGNUP
    public boolean signup(Connection conn, String tablename, int teacherid, String fname, String lname, String gradeAndsection, String password) {
        try {
            String query = "INSERT INTO " + tablename + " (teacher_id, teacherfname, teacherlname, grade_section, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, teacherid);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, gradeAndsection);
            statement.setString(5, password);

            int rowsInserted = statement.executeUpdate();
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

    //INSERT RESOURCES



    //INSERT STUDENT PROFILE


    //INSERT READING LOG


    //UPDATE RESOURCES


    //UPDATE STUDENT PROFILE


    //UPDATE RESOURCES

    //INSERT ASSESSMENTS








}






