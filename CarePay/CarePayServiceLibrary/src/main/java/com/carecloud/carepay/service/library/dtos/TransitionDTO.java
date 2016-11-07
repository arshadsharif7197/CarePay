package com.carecloud.carepay.service.library.dtos;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class TransitionDTO {
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private  JsonObject queryString;

    /**
     *
     * @return
     * The method
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param method
     * The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The queryString
     */
    public JsonObject getQueryString() {
        return queryString;
    }

    /**
     *
     * @param queryString
     * The query_string
     */
    public void setQueryString(JsonObject queryString) {
        this.queryString = queryString;
    }
}
