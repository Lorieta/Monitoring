package Tables;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SelectionModel {
    private SimpleIntegerProperty selectionID;
    private SimpleStringProperty LRN;
    private SimpleIntegerProperty languageTypeID;
    private SimpleIntegerProperty score;
    private SimpleIntegerProperty resultID;
    private SimpleStringProperty title;

    public SelectionModel(int selectionID, String LRN, int languageTypeID, int score, int resultID, String title) {
        this.selectionID = new SimpleIntegerProperty(selectionID);
        this.LRN = new SimpleStringProperty(LRN);
        this.languageTypeID = new SimpleIntegerProperty(languageTypeID);
        this.score = new SimpleIntegerProperty(score);
        this.resultID = new SimpleIntegerProperty(resultID);
        this.title = new SimpleStringProperty(title);
    }

    public int getSelectionID() {
        return selectionID.get();
    }

    public void setSelectionID(int selectionID) {
        this.selectionID.set(selectionID);
    }

    public SimpleIntegerProperty selectionIDProperty() {
        return selectionID;
    }

    public String getLRN() {
        return LRN.get();
    }

    public void setLRN(String LRN) {
        this.LRN.set(LRN);
    }

    public SimpleStringProperty LRNProperty() {
        return LRN;
    }

    public int getLanguageTypeID() {
        return languageTypeID.get();
    }

    public void setLanguageTypeID(int languageTypeID) {
        this.languageTypeID.set(languageTypeID);
    }

    public SimpleIntegerProperty languageTypeIDProperty() {
        return languageTypeID;
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }

    public int getResultID() {
        return resultID.get();
    }

    public void setResultID(int resultID) {
        this.resultID.set(resultID);
    }

    public SimpleIntegerProperty resultIDProperty() {
        return resultID;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }
}
