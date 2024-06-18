package com.project.monitor;

public class Config {
    public static final String DATABASE = "projectdb";
    public static final String USER = "postgres";
    public static final String PASSWORD = "123";
}

//String user= Config.USER;
//String password = Config.PASSWORD;
//String database = Config.DATABASE;
//
//ObservableList<Student> StudentList = FXCollections.observableArrayList();
//
//public void refresh() {
//    try {
//        dbFunctions db = new dbFunctions();
//        Connection conn = db.connect_to_db(database,user,password);
//        String query = String.format("SELECT * FROM student_info ;");
//        PreparedStatement preparedStatement = conn.prepareStatement(query);
//        ResultSet resultSet = preparedStatement.executeQuery();
//
//        while (resultSet.next()){
//            StudentList.add(new Student(
//                    resultSet.getString()
//            ));
//
//        }
//
//
//
//
//    } catch (SQLException e) {
//        System.out.println("Error during login: " + e.getMessage());
//
//    }
//}
//
//private void loadDate(){
//    String Username =Config.USER;
//    String database = Config.DATABASE;
//    String password = Config.PASSWORD;
//    dbFunctions db = new dbFunctions();
//
//    db.connect_to_db(Username,database,password);
//
//    LRNColumn.setCellValueFactory(new PropertyValueFactory<>("LRN"));
//    firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
//    lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
//    GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
//    age.setCellValueFactory(new PropertyValueFactory<>("age"));
//
//
//
//}
//
//
//@Override
//public void initialize(URL url, ResourceBundle resourceBundle) {
//
//}