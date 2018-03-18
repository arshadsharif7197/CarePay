
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsMetadataModel {

    @SerializedName("links")
    @Expose
    private PaymentsLinksDTO paymentsLinks = new PaymentsLinksDTO();
    @SerializedName("transitions")
    @Expose
    private PaymentsTransitionsDTO paymentsTransitions = new PaymentsTransitionsDTO();

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
     * @return
     *     The paymentsTransitions
     */
    public PaymentsTransitionsDTO getPaymentsTransitions() {
        return paymentsTransitions;
    }
}
