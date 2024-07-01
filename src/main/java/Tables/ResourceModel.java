package Tables;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class ResourceModel {

    private final SimpleIntegerProperty materialid;
    private final SimpleStringProperty resourceTitle;
    private final SimpleStringProperty URL;
    private final SimpleStringProperty author_publisher;
    private Date date_published;
    private final SimpleStringProperty  languageType;
    private final SimpleStringProperty resourceType;

    public ResourceModel(SimpleIntegerProperty materialid, SimpleStringProperty resourceTitle, SimpleStringProperty url, SimpleStringProperty authorPublisher, Date datePublished, SimpleStringProperty languageType, SimpleStringProperty resourceType) {
        this.materialid = materialid;
        this.resourceTitle = resourceTitle;
        this.URL = url;
        this.author_publisher = authorPublisher;
        this.date_published = datePublished;
        this.languageType = languageType;
        this.resourceType = resourceType;
    }

    public int getMaterialid() {
        return materialid.get();
    }

    public SimpleIntegerProperty materialidProperty() {
        return materialid;
    }

    public void setMaterialid(int materialid) {
        this.materialid.set(materialid);
    }

    public String getResourceTitle() {
        return resourceTitle.get();
    }

    public SimpleStringProperty resourceTitleProperty() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle.set(resourceTitle);
    }

    public String getURL() {
        return URL.get();
    }

    public SimpleStringProperty URLProperty() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL.set(URL);
    }

    public String getAuthor_publisher() {
        return author_publisher.get();
    }

    public SimpleStringProperty author_publisherProperty() {
        return author_publisher;
    }

    public void setAuthor_publisher(String author_publisher) {
        this.author_publisher.set(author_publisher);
    }

    public Date getDate_published() {
        return date_published;
    }

    public void setDate_published(Date date_published) {
        this.date_published = date_published;
    }

    public String getLanguageType() {
        return languageType.get();
    }

    public SimpleStringProperty languageTypeProperty() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType.set(languageType);
    }

    public String getResourceType() {
        return resourceType.get();
    }

    public SimpleStringProperty resourceTypeProperty() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType.set(resourceType);
    }
}
