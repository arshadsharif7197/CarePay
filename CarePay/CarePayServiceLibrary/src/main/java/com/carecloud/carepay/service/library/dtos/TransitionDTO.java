package com.carecloud.carepay.service.library.dtos;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class TransitionDTO {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private JsonObject queryString;
    @SerializedName("post_model")
    @Expose
    private JsonObject postModel;
    @SerializedName("header")
    @Expose
    private DemographicsSettingsHeaderDTO header = new DemographicsSettingsHeaderDTO();

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
    public JsonObject getQueryString() {
        return queryString;
    }

    /**
     * @param queryString The query_string
     */
    public void setQueryString(JsonObject queryString) {
        this.queryString = queryString;
    }

    /**
     * Gets post model.
     *
     * @return the post model
     */
    public JsonObject getPostModel() {
        return postModel;
    }

    /**
     * Sets post model.
     *
     * @param postModel the post model
     */
    public void setPostModel(JsonObject postModel) {
        this.postModel = postModel;
    }

    /**
     * @return header
     */
    public DemographicsSettingsHeaderDTO getHeader() {
        return header;
    }

    /**
     * @param header the header
     */
    public void setHeader(DemographicsSettingsHeaderDTO header) {
        this.header = header;
    }

    /**
     * @return true is method is GET
     */
    public boolean isGet() {
        return METHOD_GET.equalsIgnoreCase(method);
    }

    /**
     * @return true is method is POST
     */
    public boolean isPost() {
        return METHOD_POST.equalsIgnoreCase(method);
    }

    /**
     * @return true is method is DELETE
     */
    public boolean isDelete() {
        return METHOD_DELETE.equalsIgnoreCase(method);
    }
}
