package com.carecloud.carepaylibray.googleapis.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class AddressComponentModel {

    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("types")
    @Expose
    private List<String> types = new ArrayList<>();

    /**
     *
     * @return
     * The longName
     */
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @param longName
     * The long_name
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     *
     * @return
     * The shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     *
     * @param shortName
     * The short_name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     *
     * @return
     * The types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     *
     * @param types
     * The types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }
}
