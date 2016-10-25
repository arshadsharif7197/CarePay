package com.carecloud.carepaylibray.consentforms.models.links;

/**
 * Created by Rahul on 10/21/16.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormDemographicsDTO {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     *
     * @return
     * The action
     */
    public String getAction() {
        return action;
    }

    /**
     *
     * @param action
     * The action
     */
    public void setAction(String action) {
        this.action = action;
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

}
