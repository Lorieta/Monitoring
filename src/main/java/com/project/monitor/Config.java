package com.project.monitor;

public class Config {
    public static final String DATABASE = "projectdb";
    public static final String USER = "postgres";
    public static final String PASSWORD = "123";
}

//private void loadDate() {
//    LRNColumn.setCellValueFactory(new PropertyValueFactory<>("lrn"));
//    firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
//    lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
//    GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
//    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
//
//    studentTable.setItems(StudentList);
//
//    refreshTable();
//}
//    private ObservableList<Student> StudentList = FXCollections.observableArrayList();
//private void refreshTable() {
//    StudentList.clear();
//    try {
//        dbFunctions db = new dbFunctions();
//        Connection conn = db.connect_to_db(Database, lUser, Password);
//        String query = "SELECT * FROM student_info";
//        PreparedStatement preparedStatement = conn.prepareStatement(query);
//        ResultSet resultSet = preparedStatement.executeQuery();
//
//        while (resultSet.next()) {
//            StudentList.add(new Student(
//                    resultSet.getString("lrn"),
//                    resultSet.getString("firstname"),
//                    resultSet.getString("lastname"),
//                    resultSet.getString("gender"),
//                    resultSet.getString("age")
//            ));
//        }
//
//        resultSet.close();
//        preparedStatement.close();
//        conn.close();
//
//    } catch (Exception e) {
//        System.out.println("Error during data refresh: " + e.getMessage());
//        e.printStackTrace();
//    }
//}
//
//@Override
//public void initialize(URL url, ResourceBundle resourceBundle) {
//    loadDate();
//    refreshTable();
//}