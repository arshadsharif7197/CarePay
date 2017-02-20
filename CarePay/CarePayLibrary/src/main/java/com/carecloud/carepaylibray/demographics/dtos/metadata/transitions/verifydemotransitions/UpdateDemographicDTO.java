package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.verifydemotransitions;

/**
 * Created by Rahul on 10/31/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateDemographicDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("data")
    @Expose
    private VerifyDemographicDataDTO data = new VerifyDemographicDataDTO();

    /**
     * @return The method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The data
     */
    public VerifyDemographicDataDTO getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(VerifyDemographicDataDTO data) {
        this.data = data;
    }

}