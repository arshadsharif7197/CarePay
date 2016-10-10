
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsMetadataModel {

    @SerializedName("paymentsLabelModel")
    @Expose
    private PaymentsLabelModel paymentsLabelModel;
    @SerializedName("paymentsLinksModel")
    @Expose
    private PaymentsLinksModel paymentsLinksModel;
    @SerializedName("paymentsTransitionsModel")
    @Expose
    private PaymentsTransitionsModel paymentsTransitionsModel;

    /**
     * 
     * @return
     *     The paymentsLabelModel
     */
    public PaymentsLabelModel getPaymentsLabelModel() {
        return paymentsLabelModel;
    }

    /**
     * 
     * @param paymentsLabelModel
     *     The paymentsLabelModel
     */
    public void setPaymentsLabelModel(PaymentsLabelModel paymentsLabelModel) {
        this.paymentsLabelModel = paymentsLabelModel;
    }

    /**
     * 
     * @return
     *     The paymentsLinksModel
     */
    public PaymentsLinksModel getPaymentsLinksModel() {
        return paymentsLinksModel;
    }

    /**
     * 
     * @param paymentsLinksModel
     *     The paymentsLinksModel
     */
    public void setPaymentsLinksModel(PaymentsLinksModel paymentsLinksModel) {
        this.paymentsLinksModel = paymentsLinksModel;
    }

    /**
     * 
     * @return
     *     The paymentsTransitionsModel
     */
    public PaymentsTransitionsModel getPaymentsTransitionsModel() {
        return paymentsTransitionsModel;
    }

    /**
     * 
     * @param paymentsTransitionsModel
     *     The paymentsTransitionsModel
     */
    public void setPaymentsTransitionsModel(PaymentsTransitionsModel paymentsTransitionsModel) {
        this.paymentsTransitionsModel = paymentsTransitionsModel;
    }

}
