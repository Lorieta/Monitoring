package Tables;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class SelectionModel {
    private SimpleIntegerProperty selectionID;
    private SimpleStringProperty LRN;
    private SimpleStringProperty resourceTitle;
    private SimpleStringProperty url;
    private SimpleStringProperty languageType;
    private SimpleIntegerProperty score;
    private SimpleObjectProperty<LocalDate> date;

    public SelectionModel(int selectionID, String LRN, String resourceTitle, String url, String languageType, int score, LocalDate date) {
        this.selectionID = new SimpleIntegerProperty(selectionID);
        this.LRN = new SimpleStringProperty(LRN);
        this.resourceTitle = new SimpleStringProperty(resourceTitle);
        this.url = new SimpleStringProperty(url);
        this.languageType = new SimpleStringProperty(languageType);
        this.score = new SimpleIntegerProperty(score);
        this.date = new SimpleObjectProperty<>(date);
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

    public String getResourceTitle() {
        return resourceTitle.get();
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle.set(resourceTitle);
    }

    public SimpleStringProperty resourceTitleProperty() {
        return resourceTitle;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public String getLanguageType() {
        return languageType.get();
    }

    public void setLanguageType(String languageType) {
        this.languageType.set(languageType);
    }

    public SimpleStringProperty languageTypeProperty() {
        return languageType;
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

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }
}
