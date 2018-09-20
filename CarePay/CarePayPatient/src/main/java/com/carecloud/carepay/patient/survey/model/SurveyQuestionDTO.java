package com.carecloud.carepay.patient.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 7/09/18.
 */
public class SurveyQuestionDTO {

    @Expose
    @SerializedName("uuid")
    private String uuid;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("order")
    private String order;
    @Expose
    @SerializedName("rate")
    private transient float rate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }
}
