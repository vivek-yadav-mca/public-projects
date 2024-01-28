package dummydata.android.model;


public class WinnerDataModel {

    private WinnerDataModel winnerDataModel;

    private String userId;
    private String userName;
    private String userPhotoUrl;
    private Integer prize;
    private Integer rank;

    public WinnerDataModel() {
    }

    public WinnerDataModel(WinnerDataModel winnerDataModel) {
        this.userId = winnerDataModel.userId;
        this.userName = winnerDataModel.userName;
        this.userPhotoUrl = winnerDataModel.userPhotoUrl;
        this.prize = winnerDataModel.prize;
        this.rank = winnerDataModel.rank;
        this.winnerDataModel = winnerDataModel;
    }

    public WinnerDataModel(String userId, String userName, String userPhotoUrl, Integer prize, Integer rank) {
        this.userId = userId;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.prize = prize;
        this.rank = rank;
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

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
