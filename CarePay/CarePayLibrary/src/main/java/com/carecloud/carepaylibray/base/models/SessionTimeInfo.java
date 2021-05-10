package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

public class SessionTimeInfo {
    @SerializedName("name")
    private String name;
    @SerializedName("label")
    private String label;
    @SerializedName("default")
    private boolean isDefault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
