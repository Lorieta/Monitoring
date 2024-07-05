package com.project.monitor;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        String Database = Config.DATABASE;
        String lUser =  Config.USER;
        String Password = Config.PASSWORD;



        try { dbFunctions db = new dbFunctions();
            Connection conn = db.connect_to_db(Database,lUser,Password);

            App.launch(App.class, args);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
