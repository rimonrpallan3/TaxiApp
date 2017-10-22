package metrotaxi.project.in.metrotaxi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kripa on 01-10-2017.
 */

public class RechargeModel {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(int rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    String username;
    @SerializedName("card_name")
    String cardName;
    @SerializedName("card_number")
    String cardNumber;
    String cvv;
    @SerializedName("amount")
    int rechargeAmount;
    @SerializedName("expiry")
    String expiryDate;

}
