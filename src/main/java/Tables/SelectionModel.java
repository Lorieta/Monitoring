package Tables;

import java.time.LocalDate;

public class SelectionModel {

    private int selectionID;
    private String LRN;
    private String studentName;
    private String teacherName;
    private String resourceTitle;
    private String languageType;
    private String url;
    private int score;
    private LocalDate date;

    public SelectionModel(int selectionID, String LRN, String studentName, String teacherName, String resourceTitle, String languageType, String url, int score, LocalDate date) {
        this.selectionID = selectionID;
        this.LRN = LRN;
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.resourceTitle = resourceTitle;
        this.languageType = languageType;
        this.url = url;
        this.score = score;
        this.date = date;
    }

    public int getSelectionID() {
        return selectionID;
    }

    public void setSelectionID(int selectionID) {
        this.selectionID = selectionID;
    }

    public String getLRN() {
        return LRN;
    }

    public void setLRN(String LRN) {
        this.LRN = LRN;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SelectionModel{" +
                "selectionID=" + selectionID +
                ", LRN='" + LRN + '\'' +
                ", studentName='" + studentName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", resourceTitle='" + resourceTitle + '\'' +
                ", languageType='" + languageType + '\'' +
                ", url='" + url + '\'' +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}
