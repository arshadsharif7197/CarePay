package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class BaseTransitionsActionModel {
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("allowed_ops")
    @Expose
    private List<BaseTransitionsActionAllowedOptionsModel> allowedOps = new ArrayList<BaseTransitionsActionAllowedOptionsModel>();
    @SerializedName("path")
    @Expose
    private String path;

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
     * The allowedOps
     */
    public List<BaseTransitionsActionAllowedOptionsModel> getAllowedOps() {
        return allowedOps;
    }

    /**
     *
     * @param allowedOps
     * The allowed_ops
     */
    public void setAllowedOps(List<BaseTransitionsActionAllowedOptionsModel> allowedOps) {
        this.allowedOps = allowedOps;
    }

    /**
     *
     * @return
     * The path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     * The path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
