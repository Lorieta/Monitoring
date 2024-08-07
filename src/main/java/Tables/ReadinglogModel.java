package Tables;

import java.sql.Date;
import javafx.beans.property.*;

public class ReadinglogModel {
    private final IntegerProperty logid;
    private final StringProperty lrn;
    private final StringProperty fname;
    private final StringProperty lname;
    private final StringProperty resourceTitle;
    private final StringProperty url;
    private final StringProperty languageType;
    private final StringProperty resourceType;
    private final StringProperty duration;
    private final ObjectProperty<Date> dateStarted;
    private final ObjectProperty<Date> dateFinished;
    private final StringProperty comment;

    public ReadinglogModel(int logid, String lrn, String fname, String lname, String resourceTitle, String url,
                           String languageType, String resourceType, String duration, Date dateStarted,
                           Date dateFinished, String comment) {
        this.logid = new SimpleIntegerProperty(logid);
        this.lrn = new SimpleStringProperty(lrn);
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

    // Getters and setters for all properties
    public int getLogid() { return logid.get(); }
    public void setLogid(int value) { logid.set(value); }
    public IntegerProperty logidProperty() { return logid; }

    public String getLrn() { return lrn.get(); }
    public void setLrn(String value) { lrn.set(value); }
    public StringProperty lrnProperty() { return lrn; }

    public String getFname() { return fname.get(); }
    public void setFname(String value) { fname.set(value); }
    public StringProperty fnameProperty() { return fname; }

    public String getLname() { return lname.get(); }
    public void setLname(String value) { lname.set(value); }
    public StringProperty lnameProperty() { return lname; }

    public String getResourceTitle() { return resourceTitle.get(); }
    public void setResourceTitle(String value) { resourceTitle.set(value); }
    public StringProperty resourceTitleProperty() { return resourceTitle; }

    public String getUrl() { return url.get(); }
    public void setUrl(String value) { url.set(value); }
    public StringProperty urlProperty() { return url; }

    public String getLanguageType() { return languageType.get(); }
    public void setLanguageType(String value) { languageType.set(value); }
    public StringProperty languageTypeProperty() { return languageType; }

    public String getResourceType() { return resourceType.get(); }
    public void setResourceType(String value) { resourceType.set(value); }
    public StringProperty resourceTypeProperty() { return resourceType; }

    public String getDuration() { return duration.get(); }
    public void setDuration(String value) { duration.set(value); }
    public StringProperty durationProperty() { return duration; }

    public Date getDateStarted() { return dateStarted.get(); }
    public void setDateStarted(Date value) { dateStarted.set(value); }
    public ObjectProperty<Date> dateStartedProperty() { return dateStarted; }

    public Date getDateFinished() { return dateFinished.get(); }
    public void setDateFinished(Date value) { dateFinished.set(value); }
    public ObjectProperty<Date> dateFinishedProperty() { return dateFinished; }

    public String getComment() { return comment.get(); }
    public void setComment(String value) { comment.set(value); }
    public StringProperty commentProperty() { return comment; }
}