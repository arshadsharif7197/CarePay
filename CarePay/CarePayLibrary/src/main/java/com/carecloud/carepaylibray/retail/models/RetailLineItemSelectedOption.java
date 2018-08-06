package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

public class RetailLineItemSelectedOption {
    private static final String DEFAULT_OPTION_TYPE = "CHOICES";

    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private  String value;

    @SerializedName("type")
    private String type = DEFAULT_OPTION_TYPE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
