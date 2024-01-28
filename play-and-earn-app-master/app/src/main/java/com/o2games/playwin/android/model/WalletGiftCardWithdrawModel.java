package dummydata.android.model;

public class WalletGiftCardWithdrawModel {

    private String userName;
    private String userEmailID;
    private String userMobileNumber;
    private String paymentMode;
    private String paymentRegEmail;
    private String paymentAmount;
    private String paymentTime;
    private String transactionID;
    private String cashBalance_BeforeDeduction;
    private String cashBalance_AfterDeduction;
    private String coinBalance_At_Deduction;

    public WalletGiftCardWithdrawModel() {
    }

    public WalletGiftCardWithdrawModel(String userName, String userEmailID, String userMobileNumber, String paymentMode, String paymentRegEmail, String paymentAmount, String paymentTime, String transactionID, String cashBalance_BeforeDeduction, String cashBalance_AfterDeduction, String coinBalance_At_Deduction) {
        this.userName = userName;
        this.userEmailID = userEmailID;
        this.userMobileNumber = userMobileNumber;
        this.paymentMode = paymentMode;
        this.paymentRegEmail = paymentRegEmail;
        this.paymentAmount = paymentAmount;
        this.paymentTime = paymentTime;
        this.transactionID = transactionID;
        this.cashBalance_BeforeDeduction = cashBalance_BeforeDeduction;
        this.cashBalance_AfterDeduction = cashBalance_AfterDeduction;
        this.coinBalance_At_Deduction = coinBalance_At_Deduction;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmailID() {
        return userEmailID;
    }

    public void setUserEmailID(String userEmailID) {
        this.userEmailID = userEmailID;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentRegEmail() {
        return paymentRegEmail;
    }

    public void setPaymentRegEmail(String paymentRegEmail) {
        this.paymentRegEmail = paymentRegEmail;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getCashBalance_BeforeDeduction() {
        return cashBalance_BeforeDeduction;
    }

    public void setCashBalance_BeforeDeduction(String cashBalance_BeforeDeduction) {
        this.cashBalance_BeforeDeduction = cashBalance_BeforeDeduction;
    }

    public String getCashBalance_AfterDeduction() {
        return cashBalance_AfterDeduction;
    }

    public void setCashBalance_AfterDeduction(String cashBalance_AfterDeduction) {
        this.cashBalance_AfterDeduction = cashBalance_AfterDeduction;
    }

    public String getCoinBalance_At_Deduction() {
        return coinBalance_At_Deduction;
    }

    public void setCoinBalance_At_Deduction(String coinBalance_At_Deduction) {
        this.coinBalance_At_Deduction = coinBalance_At_Deduction;
    }
}