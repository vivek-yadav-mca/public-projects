package dummydata.android.model;

import androidx.annotation.Keep;

@Keep
public class WithdrawRequestData {
    String userId;
    String date;
    String time;
    String paymentMode;
    String totalCoins;
    String withdrawAmount;
    String transactionId;

    public WithdrawRequestData() {
    }

    public WithdrawRequestData(String userId, String date, String time, String paymentMode, String totalCoins, String withdrawAmount, String transactionId) {
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.paymentMode = paymentMode;
        this.totalCoins = totalCoins;
        this.withdrawAmount = withdrawAmount;
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(String totalCoins) {
        this.totalCoins = totalCoins;
    }

    public String getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
