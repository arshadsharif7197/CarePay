package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormUpdateConsentDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private ConsentFormQueryStringDTO queryString;
    @SerializedName("data")
    @Expose
    private ConsentFormDataDTO data;

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
     * @return The queryString
     */
    public ConsentFormQueryStringDTO getQueryString() {
        return queryString;
    }

    /**
     * @param queryString The query_string
     */
    public void setQueryString(ConsentFormQueryStringDTO queryString) {
        this.queryString = queryString;
    }

    /**
     * @return The data
     */
    public ConsentFormDataDTO getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(ConsentFormDataDTO data) {
        this.data = data;
    }

}