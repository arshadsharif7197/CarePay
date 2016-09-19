package com.carecloud.carepaylibray.googleapis.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class GeometryBounds {
    @SerializedName("northeast")
    @Expose
    private LatLang northeast;
    @SerializedName("southwest")
    @Expose
    private LatLang southwest;

    /**
     *
     * @return
     * The northeast
     */
    public LatLang getNortheast() {
        return northeast;
    }

    /**
     *
     * @param northeast
     * The northeast
     */
    public void setNortheast(LatLang northeast) {
        this.northeast = northeast;
    }

    /**
     *
     * @return
     * The southwest
     */
    public LatLang getSouthwest() {
        return southwest;
    }

    /**
     *
     * @param southwest
     * The southwest
     */
    public void setSouthwest(LatLang southwest) {
        this.southwest = southwest;
    }
}
