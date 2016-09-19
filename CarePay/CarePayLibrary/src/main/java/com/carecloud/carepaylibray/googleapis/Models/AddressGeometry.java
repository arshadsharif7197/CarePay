package com.carecloud.carepaylibray.googleapis.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class AddressGeometry {
    @SerializedName("bounds")
    @Expose
    private GeometryBounds bounds;
    @SerializedName("location")
    @Expose
    private GeometryLocation location;
    @SerializedName("location_type")
    @Expose
    private String locationType;
    @SerializedName("viewport")
    @Expose
    private GeometryViewport viewport;

    /**
     *
     * @return
     * The bounds
     */
    public GeometryBounds getBounds() {
        return bounds;
    }

    /**
     *
     * @param bounds
     * The bounds
     */
    public void setBounds(GeometryBounds bounds) {
        this.bounds = bounds;
    }

    /**
     *
     * @return
     * The location
     */
    public GeometryLocation getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(GeometryLocation location) {
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
    public GeometryViewport getViewport() {
        return viewport;
    }

    /**
     *
     * @param viewport
     * The viewport
     */
    public void setViewport(GeometryViewport viewport) {
        this.viewport = viewport;
    }

}
