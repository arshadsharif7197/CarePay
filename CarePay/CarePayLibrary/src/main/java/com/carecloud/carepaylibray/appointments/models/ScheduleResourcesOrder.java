package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ScheduleResourcesOrder implements Serializable {

    @SerializedName("order")
    @Expose
    private String order;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
