package dummydata.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class SatsangModel {
    private String satsangId;
    private String satsangName;
    private String satsangSubName;
    private String satsangUrl;
    private Integer rank;
    @ServerTimestamp
    private Timestamp createdAt;

    public SatsangModel() {
    }

    public SatsangModel(String satsangId, String satsangName, String satsangSubName, String satsangUrl) {
        this.satsangId = satsangId;
        this.satsangName = satsangName;
        this.satsangSubName = satsangSubName;
        this.satsangUrl = satsangUrl;
    }

    public SatsangModel(String satsangId, String satsangName, String satsangSubName, String satsangUrl, Integer rank) {
        this.satsangId = satsangId;
        this.satsangName = satsangName;
        this.satsangSubName = satsangSubName;
        this.satsangUrl = satsangUrl;
        this.rank = rank;
    }

    public String getSatsangId() {
        return satsangId;
    }

    public void setSatsangId(String satsangId) { this.satsangId = satsangId; }

    public String getSatsangName() {
        return satsangName;
    }

    public void setSatsangName(String satsangName) {
        this.satsangName = satsangName;
    }

    public String getSatsangSubName() {
        return satsangSubName;
    }

    public void setSatsangSubName(String satsangSubName) {
        this.satsangSubName = satsangSubName;
    }

    public String getSatsangUrl() {
        return satsangUrl;
    }

    public void setSatsangUrl(String satsangUrl) {
        this.satsangUrl = satsangUrl;
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
