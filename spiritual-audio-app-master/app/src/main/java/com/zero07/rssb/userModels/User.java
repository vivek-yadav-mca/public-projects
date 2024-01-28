package dummydata.userModels;

public class User {
        private String id;
        private String userName;
        private String userEmail;
        private String userGoogleAccountId;
        private String userPhotoUrl;

        public User() {
        }

        public User(String id, String userName, String userEmail, String userGoogleAccountId, String userPhotoUrl) {
            this.id = id;
            this.userName = userName;
            this.userEmail = userEmail;
            this.userGoogleAccountId = userGoogleAccountId;
            this.userPhotoUrl = userPhotoUrl;
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

        public String getId() {
            return userEmail.replaceAll("[.#$\\[\\]]", "");
//            return userGoogleAccountId;
        }


}