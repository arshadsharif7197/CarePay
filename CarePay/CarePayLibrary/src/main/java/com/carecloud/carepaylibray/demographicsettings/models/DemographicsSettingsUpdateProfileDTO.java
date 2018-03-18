
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsUpdateProfileDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private DemographicsSettingsQueryStringDTO demographicsSettingsQueryStringDTO = new DemographicsSettingsQueryStringDTO();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DemographicsSettingsQueryStringDTO getQueryString() {
        return demographicsSettingsQueryStringDTO;
    }

    public void setQueryString(DemographicsSettingsQueryStringDTO demographicsSettingsQueryStringDTO) {
        this.demographicsSettingsQueryStringDTO = demographicsSettingsQueryStringDTO;
    }

}
