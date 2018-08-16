package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailItemOptionDto {

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("choices")
    private List<RetailItemOptionChoiceDto> choices = new ArrayList<>();

    @SerializedName("default_choice")
    private int defaultChoice;

    @SerializedName("required")
    private boolean required;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RetailItemOptionChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(List<RetailItemOptionChoiceDto> choices) {
        this.choices = choices;
    }

    public int getDefaultChoice() {
        return defaultChoice;
    }

    public void setDefaultChoice(int defaultChoice) {
        this.defaultChoice = defaultChoice;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
