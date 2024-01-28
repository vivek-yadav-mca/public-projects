package dummydata.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class ShabadModel {
    private String shabadId;
    private String shabadName;
    private String shabadSubName;
    private String shabadUrl;
    private Integer rank;
    @ServerTimestamp
    private Timestamp createdAt;

    public ShabadModel() {
    }

    public ShabadModel(String shabadId, String shabadName, String shabadSubName, String shabadUrl) {
        this.shabadId = shabadId;
        this.shabadName = shabadName;
        this.shabadSubName = shabadSubName;
        this.shabadUrl = shabadUrl;
    }

    public ShabadModel(String shabadId, String shabadName, String shabadSubName, String shabadUrl, Integer rank) {
        this.shabadId = shabadId;
        this.shabadName = shabadName;
        this.shabadSubName = shabadSubName;
        this.shabadUrl = shabadUrl;
        this.rank = rank;
    }

    public String getShabadId() {
        return shabadId;
    }

    public void setShabadId(String shabadId) {
        this.shabadId = shabadId;
    }

    public String getShabadName() {
        return shabadName;
    }

    public void setShabadName(String shabadName) {
        this.shabadName = shabadName;
    }

    public String getShabadSubName() {
        return shabadSubName;
    }

    public void setShabadSubName(String shabadSubName) {
        this.shabadSubName = shabadSubName;
    }

    public String getShabadUrl() {
        return shabadUrl;
    }

    public void setShabadUrl(String shabadUrl) {
        this.shabadUrl = shabadUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
