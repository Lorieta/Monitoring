package Tables;

import javafx.beans.property.*;
import java.sql.Date;

public class PhiliriResultsModel {
    private final IntegerProperty resultID;
    private final StringProperty lrn;
    private final StringProperty lastName;
    private final StringProperty oralResult;
    private final StringProperty silentResult;
    private final StringProperty languageType;
    private final ObjectProperty<Date> dateRecorded;
    private final StringProperty remarks;

    public PhiliriResultsModel(int resultID, String lrn, String lastName, String oralResult,
                               String silentResult, String languageType, Date dateRecorded,
                               String remarks) {
        this.resultID = new SimpleIntegerProperty(resultID);
        this.lrn = new SimpleStringProperty(lrn);
        this.lastName = new SimpleStringProperty(lastName);
        this.oralResult = new SimpleStringProperty(oralResult);
        this.silentResult = new SimpleStringProperty(silentResult);
        this.languageType = new SimpleStringProperty(languageType);
        this.dateRecorded = new SimpleObjectProperty<>(dateRecorded);
        this.remarks = new SimpleStringProperty(remarks);
    }

    public int getResultID() {
        return resultID.get();
    }

    public IntegerProperty resultIDProperty() {
        return resultID;
    }

    public String getLrn() {
        return lrn.get();
    }

    public StringProperty lrnProperty() {
        return lrn;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getOralResult() {
        return oralResult.get();
    }

    public StringProperty oralResultProperty() {
        return oralResult;
    }

    public String getSilentResult() {
        return silentResult.get();
    }

    public StringProperty silentResultProperty() {
        return silentResult;
    }

    public String getLanguageType() {
        return languageType.get();
    }

    public StringProperty languageTypeProperty() {
        return languageType;
    }

    public Date getDateRecorded() {
        return dateRecorded.get();
    }

    public ObjectProperty<Date> dateRecordedProperty() {
        return dateRecorded;
    }

    public String getRemarks() {
        return remarks.get();
    }

    public StringProperty remarksProperty() {
        return remarks;
    }
}