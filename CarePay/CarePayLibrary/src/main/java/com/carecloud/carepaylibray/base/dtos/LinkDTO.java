package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Link
 */
public class LinkDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private QueryStrings queryStrings = new QueryStrings();

    /**
     * 
     * @return
     *     The method
     */
    public String getMethod() {
        return method;
    }

    /**
     * 
     * @param method
     *     The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The queryStrings
     */
    public QueryStrings getQueryStrings() {
        return queryStrings;
    }

    /**
     * 
     * @param queryStrings
     *     The query_string
     */
    public void setQueryStrings(QueryStrings queryStrings) {
        this.queryStrings = queryStrings;
    }

}
