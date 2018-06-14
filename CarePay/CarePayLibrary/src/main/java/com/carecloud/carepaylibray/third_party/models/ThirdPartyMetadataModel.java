package com.carecloud.carepaylibray.third_party.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThirdPartyMetadataModel {

    @SerializedName("links")
    @Expose
    private ThirdPartyLinksDTO paymentsLinks = new ThirdPartyLinksDTO();
    @SerializedName("transitions")
    @Expose
    private ThirdPartyTransitionsDTO paymentsTransitions = new ThirdPartyTransitionsDTO();

    public ThirdPartyLinksDTO getPaymentsLinks() {
        return paymentsLinks;
    }

    public void setPaymentsLinks(ThirdPartyLinksDTO paymentsLinks) {
        this.paymentsLinks = paymentsLinks;
    }

    public ThirdPartyTransitionsDTO getPaymentsTransitions() {
        return paymentsTransitions;
    }

    public void setPaymentsTransitions(ThirdPartyTransitionsDTO paymentsTransitions) {
        this.paymentsTransitions = paymentsTransitions;
    }
}
