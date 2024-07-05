package Tables;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class ReadinglogModel {
    private final SimpleIntegerProperty logid;
    private final SimpleIntegerProperty lrn;
    private final SimpleStringProperty fname;
    private final SimpleStringProperty lname;
    private final SimpleStringProperty resourceTitle;
    private final SimpleStringProperty url;
    private final SimpleStringProperty languageType;
    private final SimpleStringProperty resourceType;
    private final SimpleStringProperty duration;
    private final SimpleObjectProperty<Date> dateStarted;
    private final SimpleObjectProperty<Date> dateFinished;
    private final SimpleStringProperty comment;

    public ReadinglogModel(int logid, int lrn, String fname, String lname, String resourceTitle, String url, String languageType, String resourceType, String duration, Date dateStarted, Date dateFinished, String comment) {
        this.logid = new SimpleIntegerProperty(logid);
        this.lrn = new SimpleIntegerProperty(lrn);
        this.fname = new SimpleStringProperty(fname);
        this.lname = new SimpleStringProperty(lname);
        this.resourceTitle = new SimpleStringProperty(resourceTitle);
        this.url = new SimpleStringProperty(url);
        this.languageType = new SimpleStringProperty(languageType);
        this.resourceType = new SimpleStringProperty(resourceType);
        this.duration = new SimpleStringProperty(duration);
        this.dateStarted = new SimpleObjectProperty<>(dateStarted);
        this.dateFinished = new SimpleObjectProperty<>(dateFinished);
        this.comment = new SimpleStringProperty(comment);
    }

    // Getters for properties

    public int getLogid() {
        return logid.get();
    }

    public SimpleIntegerProperty logidProperty() {
        return logid;
    }

    public int getLrn() {
        return lrn.get();
    }

    public SimpleIntegerProperty lrnProperty() {
        return lrn;
    }

    public String getFname() {
        return fname.get();
    }

    public SimpleStringProperty fnameProperty() {
        return fname;
    }

    public String getLname() {
        return lname.get();
    }

    public SimpleStringProperty lnameProperty() {
        return lname;
    }

    public String getResourceTitle() {
        return resourceTitle.get();
    }

    public SimpleStringProperty resourceTitleProperty() {
        return resourceTitle;
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public String getLanguageType() {
        return languageType.get();
    }

    public SimpleStringProperty languageTypeProperty() {
        return languageType;
    }

    public String getResourceType() {
        return resourceType.get();
    }

    public SimpleStringProperty resourceTypeProperty() {
        return resourceType;
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public Date getDateStarted() {
        return dateStarted.get();
    }

    public SimpleObjectProperty<Date> dateStartedProperty() {
        return dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished.get();
    }

    public SimpleObjectProperty<Date> dateFinishedProperty() {
        return dateFinished;
    }

    public String getComment() {
        return comment.get();
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }
}
