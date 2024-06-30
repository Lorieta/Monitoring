package Tables;

import javafx.beans.property.SimpleStringProperty;

public class Student {
    private final SimpleStringProperty lrn;
    private final SimpleStringProperty firstname;
    private final SimpleStringProperty lastname;
    private final SimpleStringProperty gender;
    private final SimpleStringProperty age;

    public Student(String lrn, String firstname, String lastname, String gender, String age) {
        this.lrn = new SimpleStringProperty(lrn);
        this.firstname = new SimpleStringProperty(firstname);
        this.lastname = new SimpleStringProperty(lastname);
        this.gender = new SimpleStringProperty(gender);
        this.age = new SimpleStringProperty(age);
    }

    public String getLrn() {
        return lrn.get();
    }

    public void setLrn(String lrn) {
        this.lrn.set(lrn);
    }

    public SimpleStringProperty lrnProperty() {
        return lrn;
    }

    public String getFirstname() {
        return firstname.get();
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }

    public SimpleStringProperty firstnameProperty() {
        return firstname;
    }

    public String getLastname() {
        return lastname.get();
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public SimpleStringProperty lastnameProperty() {
        return lastname;
    }

    public String getGender() {
        return gender.get();
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public SimpleStringProperty genderProperty() {
        return gender;
    }

    public String getAge() {
        return age.get();
    }

    public void setAge(String age) {
        this.age.set(age);
    }

    public SimpleStringProperty ageProperty() {
        return age;
    }
}
