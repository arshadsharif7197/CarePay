package com.carecloud.carepaylibray.base.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public abstract class BaseTransitionsModel {
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("data")
    @Expose
    private JsonObject data;

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
     * The data
     */

    /*public <S>S getData(Class<S> convertTo) {
        return gson.fromJson(data, convertTo);
    }*/

    public <S>S getData(Class<S> convertTo){
        Gson gson = new Gson();
        return gson.fromJson(data, convertTo);
    }

    /*public JsonObject getData() {
        return data;
    }*/

    /**
     *
     * @param data
     * The data
     */
    public void setData(JsonObject data) {
        this.data = data;
    }


}
