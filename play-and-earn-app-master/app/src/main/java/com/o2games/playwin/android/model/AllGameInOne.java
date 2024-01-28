package dummydata.android.model;

public class AllGameInOne {
    String userId;
    String gameId;
    String chanceLeft;
    String coins;
    String cash;

    public AllGameInOne() {
    }

    public AllGameInOne(String userId, String gameId, String chanceLeft, String coins, String cash) {
        this.userId = userId;
        this.gameId = gameId;
        this.chanceLeft = chanceLeft;
        this.coins = coins;
        this.cash = cash;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getChanceLeft() {
        return chanceLeft;
    }

    public void setChanceLeft(String chanceLeft) {
        this.chanceLeft = chanceLeft;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}
