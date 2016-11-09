package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Transition
 */
public class TransitionDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("data")
    @Expose
    private DataDTO data;
    @SerializedName("query_string")
    @Expose
    private QueryStrings queryStrings;

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
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
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
     *     The data
     */
    public DataDTO getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(DataDTO data) {
        this.data = data;
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
     *     The queryStrings
     */
    public void setQueryStrings(QueryStrings queryStrings) {
        this.queryStrings = queryStrings;
    }
}
