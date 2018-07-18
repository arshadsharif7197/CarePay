package com.carecloud.carepaylibray.third_party.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThirdPartyMetadataModel {

    @SerializedName("links")
    @Expose
    private ThirdPartyLinksDTO thirdPartyLinks = new ThirdPartyLinksDTO();
    @SerializedName("transitions")
    @Expose
    private ThirdPartyTransitionsDTO thirdPartyPayload = new ThirdPartyTransitionsDTO();

    public ThirdPartyLinksDTO getThirdPartyLinks() {
        return thirdPartyLinks;
    }

    public void setThirdPartyLinks(ThirdPartyLinksDTO thirdPartyLinks) {
        this.thirdPartyLinks = thirdPartyLinks;
    }

    public ThirdPartyTransitionsDTO getThirdPartyPayload() {
        return thirdPartyPayload;
    }

    public void setThirdPartyPayload(ThirdPartyTransitionsDTO thirdPartyPayload) {
        this.thirdPartyPayload = thirdPartyPayload;
    }
}
