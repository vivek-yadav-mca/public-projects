package dummydata.android.model;

public class SpUserModel {

    private String userId;
    private String userName;
    private String userEmail;
    private String userGoogleId;
    private String userPhotoUrl;
    private String userAuthUid;
    private String userAdverId;

    public SpUserModel() {
    }

    public SpUserModel(String userId, String userName, String userEmail, String userGoogleId, String userPhotoUrl, String userAuthUid, String userAdverId) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userGoogleId = userGoogleId;
        this.userPhotoUrl = userPhotoUrl;
        this.userAuthUid = userAuthUid;
        this.userAdverId = userAdverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(String userGoogleId) {
        this.userGoogleId = userGoogleId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserAuthUid() {
        return userAuthUid;
    }

    public void setUserAuthUid(String userAuthUid) {
        this.userAuthUid = userAuthUid;
    }

    public String getUserAdverId() {
        return userAdverId;
    }

    public void setUserAdverId(String userAdverId) {
        this.userAdverId = userAdverId;
    }
}
