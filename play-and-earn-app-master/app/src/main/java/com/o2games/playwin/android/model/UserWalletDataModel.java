package dummydata.android.model;

public class UserWalletDataModel {

    private String userId;
    private String userAuthId;
    private Integer walletAmount;

    public UserWalletDataModel() {
    }

    public UserWalletDataModel(String userId, String userAuthId, Integer walletAmount) {
        this.userId = userId;
        this.userAuthId = userAuthId;
        this.walletAmount = walletAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAuthId() {
        return userAuthId;
    }

    public void setUserAuthId(String userAuthId) {
        this.userAuthId = userAuthId;
    }

    public Integer getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(Integer walletAmount) {
        this.walletAmount = walletAmount;
    }

}
