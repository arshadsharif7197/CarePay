package com.carecloud.carepaylibray.third_party.models;

import com.google.gson.annotations.SerializedName;

public class ThirdPartyPayloadDto {

    @SerializedName("third_party_process")
    private ThirdPartyProcess thirdPartyProcess = new ThirdPartyProcess();

    public ThirdPartyProcess getThirdPartyProcess() {
        return thirdPartyProcess;
    }

    public void setThirdPartyProcess(ThirdPartyProcess thirdPartyProcess) {
        this.thirdPartyProcess = thirdPartyProcess;
    }
}
