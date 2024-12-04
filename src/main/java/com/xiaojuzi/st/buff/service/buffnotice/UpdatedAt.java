package com.xiaojuzi.st.buff.service.buffnotice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-generated: 2023-10-07 22:3:21
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
public class UpdatedAt {

    @JsonProperty("to_accept_offer_order")
    private int toAcceptOfferOrder;
    @JsonProperty("to_deliver_order")
    private int toDeliverOrder;
    @JsonProperty("to_send_offer_order")
    private int toSendOfferOrder;

    public int getToAcceptOfferOrder() {
        return toAcceptOfferOrder;
    }

    public void setToAcceptOfferOrder(int toAcceptOfferOrder) {
        this.toAcceptOfferOrder = toAcceptOfferOrder;
    }

    public int getToDeliverOrder() {
        return toDeliverOrder;
    }

    public void setToDeliverOrder(int toDeliverOrder) {
        this.toDeliverOrder = toDeliverOrder;
    }

    public int getToSendOfferOrder() {
        return toSendOfferOrder;
    }

    public void setToSendOfferOrder(int toSendOfferOrder) {
        this.toSendOfferOrder = toSendOfferOrder;
    }

}
