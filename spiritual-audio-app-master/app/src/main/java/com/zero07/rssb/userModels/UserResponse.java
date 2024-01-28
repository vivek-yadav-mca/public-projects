package dummydata.userModels;

public class UserResponse {
    private String userName;
    private String userEmail;
    private String userGoogleAccountId;
    private String userPhotoUrl;

    public UserResponse() {
    }

    public UserResponse(String userName, String userEmail, String userGoogleAccountId, String userPhotoUrl) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userGoogleAccountId = userGoogleAccountId;
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserGoogleAccountId() {
        return userGoogleAccountId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public String getId() {
        return userEmail.replaceAll("[.#$\\[\\]]", "");
    }
}
