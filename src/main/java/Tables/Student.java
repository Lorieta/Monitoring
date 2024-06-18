package Tables;

import java.sql.Date;

public class Student {

    String LRN;
    String firstname;
    String lastname;
    String gender;
    Date dateOfBirth;

    public Student(Date dateOfBirth, String gender, String lastname, String firstname, String LRN) {
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastname = lastname;
        this.firstname = firstname;
        this.LRN = LRN;
    }

    public String getLRN() {
        return LRN;
    }

    public void setLRN(String LRN) {
        this.LRN = LRN;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
