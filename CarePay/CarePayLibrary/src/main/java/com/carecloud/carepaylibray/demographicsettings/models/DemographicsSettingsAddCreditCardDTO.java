
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsAddCreditCardDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private DemographicsSettingsQueryStringDTO queryString = new DemographicsSettingsQueryStringDTO();
    @SerializedName("post_model")
    @Expose
    private DemographicsSettingsPostModelDTO demographicsSettingsPostModelDTO = new DemographicsSettingsPostModelDTO();

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
        return queryString;
    }

    public void setQueryString(DemographicsSettingsQueryStringDTO queryString) {
        this.queryString = queryString;
    }

    public DemographicsSettingsPostModelDTO getPostModel() {
        return demographicsSettingsPostModelDTO;
    }

    public void setPostModel(DemographicsSettingsPostModelDTO demographicsSettingsPostModelDTO) {
        this.demographicsSettingsPostModelDTO = demographicsSettingsPostModelDTO;
    }

}
