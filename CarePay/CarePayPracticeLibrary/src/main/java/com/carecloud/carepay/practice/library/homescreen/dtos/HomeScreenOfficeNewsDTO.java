package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeScreenOfficeNewsDTO {

    @SerializedName("payload")
    @Expose
    private HomeScreenOfficeNewsPayloadDTO payload;

    public HomeScreenOfficeNewsPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(HomeScreenOfficeNewsPayloadDTO payload) {
        this.payload = payload;
    }
}
