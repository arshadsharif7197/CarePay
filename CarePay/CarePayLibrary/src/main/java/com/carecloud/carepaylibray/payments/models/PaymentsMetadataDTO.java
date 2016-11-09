
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsMetadataDTO {

    @SerializedName("label")
    @Expose
    private PaymentsLabelDTO paymentsLabel;
    @SerializedName("links")
    @Expose
    private PaymentsLinksDTO paymentsLinks;
    @SerializedName("transitions")
    @Expose
    private PaymentsTransitionsDTO paymentsTransitions;

    /**
     * 
     * @return
     *     The paymentsLabel
     */
    public PaymentsLabelDTO getPaymentsLabel() {
        return paymentsLabel;
    }

    /**
     * 
     * @param paymentsLabel
     *     The paymentsLabel
     */
    public void setPaymentsLabel(PaymentsLabelDTO paymentsLabel) {
        this.paymentsLabel = paymentsLabel;
    }

    /**
     * 
     * @return
     *     The paymentsLinks
     */
    public PaymentsLinksDTO getPaymentsLinks() {
        return paymentsLinks;
    }

    /**
     * 
     * @param paymentsLinks
     *     The paymentsLinks
     */
    public void setPaymentsLinksDTO(PaymentsLinksDTO paymentsLinks) {
        this.paymentsLinks = paymentsLinks;
    }

    /**
     * 
     * @return
     *     The paymentsTransitions
     */
    public PaymentsTransitionsDTO getPaymentsTransitions() {
        return paymentsTransitions;
    }

    /**
     * 
     * @param paymentsTransitions
     *     The paymentsTransitions
     */
    public void setPaymentsTransitions(PaymentsTransitionsDTO paymentsTransitions) {
        this.paymentsTransitions = paymentsTransitions;
    }

}
