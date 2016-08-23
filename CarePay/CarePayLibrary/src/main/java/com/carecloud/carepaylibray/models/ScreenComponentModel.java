package com.carecloud.carepaylibray.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class ScreenComponentModel {

    @SerializedName("label")
    private String Label;

    @SerializedName("type")
    private String Type;

    @SerializedName("required")
    private boolean isRequired;

    @SerializedName("value")
    private Object Value;

    public ScreenComponentModel(String label, String type, boolean isRequired) {
        Label = label;
        Type = type;
        this.isRequired = isRequired;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }
}


