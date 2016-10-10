
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsModel {

    @SerializedName("paymentsMetadataModel")
    @Expose
    private PaymentsMetadataModel paymentsMetadataModel;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * 
     * @return
     *     The paymentsMetadataModel
     */
    public PaymentsMetadataModel getPaymentsMetadataModel() {
        return paymentsMetadataModel;
    }

    /**
     * 
     * @param paymentsMetadataModel
     *     The paymentsMetadataModel
     */
    public void setPaymentsMetadataModel(PaymentsMetadataModel paymentsMetadataModel) {
        this.paymentsMetadataModel = paymentsMetadataModel;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
