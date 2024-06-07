package com.project.monitor;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;

public class dbFunctions {


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
            System.out.println(e);

        }
        return conn;
    }



}






