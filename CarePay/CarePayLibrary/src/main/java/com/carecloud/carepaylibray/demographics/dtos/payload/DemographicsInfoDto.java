package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DemographicsInfoDto {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("required")
    private Boolean required;

    @Expose
    @SerializedName("optional_either_fields")
    private List<String> optionalEitherFields = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<String> getOptionalEitherFields() {
        return optionalEitherFields;
    }

    public void setOptionalEitherFields(List<String> optionalEitherFields) {
        this.optionalEitherFields = optionalEitherFields;
    }
}
