package com.carecloud.carepaylibray.googleapis.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class AddressGeometryModel {
    @SerializedName("bounds")
    @Expose
    private GeometryBoundsModel bounds = new GeometryBoundsModel();
    @SerializedName("location")
    @Expose
    private GeometryLocationModel location = new GeometryLocationModel();
    @SerializedName("location_type")
    @Expose
    private String locationType;
    @SerializedName("viewport")
    @Expose
    private GeometryViewportModel viewport = new GeometryViewportModel();

    /**
     *
     * @return
     * The bounds
     */
    public GeometryBoundsModel getBounds() {
        return bounds;
    }

    /**
     *
     * @param bounds
     * The bounds
     */
    public void setBounds(GeometryBoundsModel bounds) {
        this.bounds = bounds;
    }

    /**
     *
     * @return
     * The location
     */
    public GeometryLocationModel getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(GeometryLocationModel location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The locationType
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     *
     * @param locationType
     * The location_type
     */
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     *
     * @return
     * The viewport
     */
    public GeometryViewportModel getViewport() {
        return viewport;
    }

    /**
     *
     * @param viewport
     * The viewport
     */
    public void setViewport(GeometryViewportModel viewport) {
        this.viewport = viewport;
    }

}
