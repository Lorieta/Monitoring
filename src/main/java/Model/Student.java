package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



public class Student {

    private StringProperty LRN;
    private StringProperty firstname;
    private StringProperty lastname;
    private StringProperty gender;
    private StringProperty age;

    public Student() {
        this.LRN = new SimpleStringProperty();
        this.firstname = new SimpleStringProperty();
        this.lastname = new SimpleStringProperty();
        this.gender = new SimpleStringProperty();
        this.age = new SimpleStringProperty();
    }

    public String getLRN() {
        return LRN.get();
    }

    public StringProperty LRNProperty() {
        return LRN;
    }

    public void setLRN(String LRN) {
        this.LRN.set(LRN);
    }

    public String getFirstname() {
        return firstname.get();
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }

    public String getLastname() {
        return lastname.get();
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getAge() {
        return age.get();
    }

    public StringProperty ageProperty() {
        return age;
    }

    public void setAge(String age) {
        this.age.set(age);
    }
}
