package com.carecloud.carepay.service.library.dtos;

/**
 * Created by harshal_patil on 1/21/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DemographicsSettingsMaintainanceDTO {

    @SerializedName("$schema")
    @Expose
    private String schema;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsEmailProperties properties = new DemographicsSettingsEmailProperties();

    @SerializedName("required")
    @Expose
    private List<String> required = null;

    public String getschema() {
        return schema;
    }

    public void setschema(String schema) {
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DemographicsSettingsEmailProperties getProperties() {
        return properties;
    }

    public void setProperties(DemographicsSettingsEmailProperties properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

}
