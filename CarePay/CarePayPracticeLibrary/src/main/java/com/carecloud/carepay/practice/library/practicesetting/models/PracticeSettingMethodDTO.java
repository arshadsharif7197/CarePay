package com.carecloud.carepay.practice.library.practicesetting.models;

/**
 * Created by prem_mourya on 10/24/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PracticeSettingMethodDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;

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


}
