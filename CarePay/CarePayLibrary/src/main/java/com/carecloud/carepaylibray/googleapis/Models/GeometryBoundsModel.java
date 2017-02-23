package com.carecloud.carepaylibray.googleapis.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class GeometryBoundsModel {
    @SerializedName("northeast")
    @Expose
    private LatLangModel northeast = new LatLangModel();
    @SerializedName("southwest")
    @Expose
    private LatLangModel southwest = new LatLangModel();

    /**
     *
     * @return
     * The northeast
     */
    public LatLangModel getNortheast() {
        return northeast;
    }

    /**
     *
     * @param northeast
     * The northeast
     */
    public void setNortheast(LatLangModel northeast) {
        this.northeast = northeast;
    }

    /**
     *
     * @return
     * The southwest
     */
    public LatLangModel getSouthwest() {
        return southwest;
    }

    /**
     *
     * @param southwest
     * The southwest
     */
    public void setSouthwest(LatLangModel southwest) {
        this.southwest = southwest;
    }
}
