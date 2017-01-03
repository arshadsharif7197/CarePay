
package com.carecloud.carepaylibray.demographicsettings.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsExpireDateDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("required")
    @Expose
    private Boolean required;
    @SerializedName("enum")
    @Expose
    private List<String> _enum = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<String> getEnum() {
        return _enum;
    }

    public void setEnum(List<String> _enum) {
        this._enum = _enum;
    }

}
