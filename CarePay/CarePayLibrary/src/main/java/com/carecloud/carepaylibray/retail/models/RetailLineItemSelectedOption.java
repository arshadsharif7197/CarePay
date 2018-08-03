package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailLineItemSelectedOption {
    private static final String DEFAULT_OPTION_TYPE = "CHOICES";

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type = DEFAULT_OPTION_TYPE;

    @SerializedName("selections")
    private List<RetailLineItemSelectionChoice> choices = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RetailLineItemSelectionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<RetailLineItemSelectionChoice> choices) {
        this.choices = choices;
    }
}
