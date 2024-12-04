package com.xiaojuzi.st.steam.service.steamtrade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Auto-generated: 2023-10-08 15:18:45
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
public class Offer {

    private String tradeofferid;
    @JsonProperty("accountid_other")
    private int accountidOther;
    private String message;
    @JsonProperty("expiration_time")
    private int expirationTime;
    @JsonProperty("trade_offer_state")
    private int tradeOfferState;
    @JsonProperty("items_to_give")
    private List<ItemsToGive> itemsToGive;
    @JsonProperty("is_our_offer")
    private boolean isOurOffer;
    @JsonProperty("time_created")
    private int timeCreated;
    @JsonProperty("time_updated")
    private int timeUpdated;
    @JsonProperty("from_real_time_trade")
    private boolean fromRealTimeTrade;
    @JsonProperty("escrow_end_date")
    private int escrowEndDate;
    @JsonProperty("confirmation_method")
    private int confirmationMethod;
    private int eresult;

    public String getTradeofferid() {
        return tradeofferid;
    }

    public void setTradeofferid(String tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public int getAccountidOther() {
        return accountidOther;
    }

    public void setAccountidOther(int accountidOther) {
        this.accountidOther = accountidOther;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getTradeOfferState() {
        return tradeOfferState;
    }

    public void setTradeOfferState(int tradeOfferState) {
        this.tradeOfferState = tradeOfferState;
    }

    public List<ItemsToGive> getItemsToGive() {
        return itemsToGive;
    }

    public void setItemsToGive(List<ItemsToGive> itemsToGive) {
        this.itemsToGive = itemsToGive;
    }

    public boolean getIsOurOffer() {
        return isOurOffer;
    }

    public void setIsOurOffer(boolean isOurOffer) {
        this.isOurOffer = isOurOffer;
    }

    public int getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(int timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(int timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public boolean getFromRealTimeTrade() {
        return fromRealTimeTrade;
    }

    public void setFromRealTimeTrade(boolean fromRealTimeTrade) {
        this.fromRealTimeTrade = fromRealTimeTrade;
    }

    public int getEscrowEndDate() {
        return escrowEndDate;
    }

    public void setEscrowEndDate(int escrowEndDate) {
        this.escrowEndDate = escrowEndDate;
    }

    public int getConfirmationMethod() {
        return confirmationMethod;
    }

    public void setConfirmationMethod(int confirmationMethod) {
        this.confirmationMethod = confirmationMethod;
    }

    public int getEresult() {
        return eresult;
    }

    public void setEresult(int eresult) {
        this.eresult = eresult;
    }

}
