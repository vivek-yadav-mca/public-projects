package dummydata.android.model;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class User {

    private String id;
    private String userName;
    private String userEmail;
    private String userGoogleAccountId;
    private String userPhotoUrl;
    private String adverId;
    private String authUid;

    private String created;

    public User() {
    }

    public User(String id, String userName, String userEmail, String userGoogleAccountId, String userPhotoUrl, String adverId) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userGoogleAccountId = userGoogleAccountId;
        this.userPhotoUrl = userPhotoUrl;
        this.adverId = adverId;
    }

    public User(String id, String userName, String userEmail, String userGoogleAccountId, String userPhotoUrl, String adverId, String authUid) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userGoogleAccountId = userGoogleAccountId;
        this.userPhotoUrl = userPhotoUrl;
        this.adverId = adverId;
        this.authUid = authUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserGoogleAccountId() {
        return userGoogleAccountId;
    }

    public void setUserGoogleAccountId(String userGoogleAccountId) {
        this.userGoogleAccountId = userGoogleAccountId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getAuthUid() {
        return authUid;
    }

    public void setAuthUid(String authUid) {
        this.authUid = authUid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreatedDate() {
        Calendar c = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH);
            this.created = sdf.format(c.getTime());
        }
    }
}
