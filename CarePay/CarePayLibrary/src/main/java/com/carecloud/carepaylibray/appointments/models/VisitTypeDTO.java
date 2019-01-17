package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for Visit Type
 * Created by jorge on 17/12/16.
 */
public class VisitTypeDTO {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("is_for_requests")
    @Expose
    private boolean forRequest;
    @SerializedName("video_option")
    @Expose
    private boolean videoOption;

    /**
     * Visit type getter id
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Visit type setter id
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * the name getter
     *
     * @return the name
     */
    public String getName() {
        return StringUtil.getLabelForView(name);
    }

    /**
     * the name setter
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * description getter
     *
     * @return the description
     */
    public String getDescription() {
        return StringUtil.getLabelForView(description);
    }

    /**
     * description setter
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isForRequest() {
        return forRequest;
    }

    public void setForRequest(boolean forRequest) {
        this.forRequest = forRequest;
    }

    public boolean isVideoOption() {
        return videoOption;
    }

    public void setVideoOption(boolean videoOption) {
        this.videoOption = videoOption;
    }
}
