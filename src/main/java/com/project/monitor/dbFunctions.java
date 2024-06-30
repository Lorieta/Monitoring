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
    public boolean login(Connection conn, String tablename, String employeeid, String password) {
        String query = String.format("SELECT * FROM %s WHERE employeeid = ? AND password = ?;", tablename);

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, employeeid);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If there is a next row, login is successful
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
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

            String query = "INSERT INTO student_info" + " (lrn,firstname,lastname,gender,age) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, lrn);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, gender);
            statement.setString(5, age);

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
            System.out.println("An error occurred while signing up teacher: " + e.getMessage());
            e.printStackTrace();

        }

    }

    //UPDATE STUDENT







    }

















