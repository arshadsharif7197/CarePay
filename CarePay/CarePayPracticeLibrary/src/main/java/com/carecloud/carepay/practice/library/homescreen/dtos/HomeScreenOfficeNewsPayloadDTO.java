package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeScreenOfficeNewsPayloadDTO {
    @SerializedName("headline")
    @Expose
    private String headline;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("publish_date")
    @Expose
    private String publishDate;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
