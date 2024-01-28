package dummydata.android.model;

import androidx.annotation.Keep;

public class LeaderboardModel {
    private String userId;
    private String authUid;
    private String userName;
    private String userPhotoUrl;

    private long userTotalCOIN;

    public LeaderboardModel() {
    }

    public LeaderboardModel(String userId, String authUid, String userName, String userPhotoUrl, long userTotalCOIN) {
        this.userId = userId;
        this.authUid = authUid;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.userTotalCOIN = userTotalCOIN;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthUid() {
        return authUid;
    }

    public void setAuthUid(String authUid) {
        this.authUid = authUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public long getUserTotalCOIN() {
        return userTotalCOIN;
    }

    public void setUserTotalCOIN(long userTotalCOIN) {
        this.userTotalCOIN = userTotalCOIN;
    }

}
