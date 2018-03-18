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
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("headline_photo")
    @Expose
    private String headlinePhoto;
    @SerializedName("url")
    @Expose
    private String newsUrl;

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHeadlinePhoto() {
        return headlinePhoto;
    }

    public void setHeadlinePhoto(String headlinePhoto) {
        this.headlinePhoto = headlinePhoto;
    }
}
