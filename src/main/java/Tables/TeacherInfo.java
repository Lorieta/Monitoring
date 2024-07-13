package Tables;

public class TeacherInfo {
    private String employeeID;
    private String teacherfname;
    private String teacherlname;
    private String gradeSection;
    private String password;
    private String email;
    private String specialization;

    // Constructors
    public TeacherInfo(String teacherId, String teacherName, String email, String gradeAndSection, String specialization, String password) {
    }

    public TeacherInfo(String employeeID, String teacherfname, String teacherlname, String gradeSection, String password, String email, String specialization) {
        this.employeeID = employeeID;
        this.teacherfname = teacherfname;
        this.teacherlname = teacherlname;
        this.gradeSection = gradeSection;
        this.password = password;
        this.email = email;
        this.specialization = specialization;
    }

    // Getters and Setters
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getTeacherfname() {
        return teacherfname;
    }

    public void setTeacherfname(String teacherfname) {
        this.teacherfname = teacherfname;
    }

    public String getTeacherlname() {
        return teacherlname;
    }

    public void setTeacherlname(String teacherlname) {
        this.teacherlname = teacherlname;
    }

    public String getGradeSection() {
        return gradeSection;
    }

    public void setGradeSection(String gradeSection) {
        this.gradeSection = gradeSection;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "TeacherInfo{" +
                "employeeID='" + employeeID + '\'' +
                ", teacherfname='" + teacherfname + '\'' +
                ", teacherlname='" + teacherlname + '\'' +
                ", gradeSection='" + gradeSection + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
