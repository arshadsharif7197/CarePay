package com.carecloud.carepaylibray.base.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class BaseTransitionsActionAllowedOptionsModel {
    @SerializedName("op")
    @Expose
    private String option;
    @SerializedName("path")
    @Expose
    private String path;

    /**
     *
     * @return
     * The option
     */
    public String getOption() {
        return option;
    }

    /**
     *
     * @param option
     * The option
     */
    public void setOption(String option) {
        this.option = option;
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
