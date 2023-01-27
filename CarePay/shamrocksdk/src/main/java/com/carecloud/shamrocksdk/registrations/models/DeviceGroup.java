package com.carecloud.shamrocksdk.registrations.models;

/**
 * Model Class for handling device Grouping
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceGroup {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}