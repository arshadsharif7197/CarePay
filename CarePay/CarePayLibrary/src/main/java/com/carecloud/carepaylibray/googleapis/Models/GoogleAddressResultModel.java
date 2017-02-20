package com.carecloud.carepaylibray.googleapis.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/16/2016.
 */
public class GoogleAddressResultModel {
    @SerializedName("address_components")
    @Expose
    private List<AddressComponentModel> addressComponents = new ArrayList<>();
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("geometry")
    @Expose
    private Object geometry;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("postcode_localities")
    @Expose
    private List<String> postcodeLocalities = new ArrayList<>();
    @SerializedName("types")
    @Expose
    private List<String> types = new ArrayList<>();

    /**
     *
     * @return
     * The addressComponents
     */
    public List<AddressComponentModel> getAddressComponents() {
        return addressComponents;
    }

    /**
     *
     * @param addressComponents
     * The address_components
     */
    public void setAddressComponents(List<AddressComponentModel> addressComponents) {
        this.addressComponents = addressComponents;
    }

    /**
     *
     * @return
     * The formattedAddress
     */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    /**
     *
     * @param formattedAddress
     * The formatted_address
     */
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    /**
     *
     * @return
     * The geometry
     */
    public Object getGeometry() {
        return geometry;
    }

    /**
     *
     * @param geometry
     * The geometry
     */
    public void setGeometry(Object geometry) {
        this.geometry = geometry;
    }

    /**
     *
     * @return
     * The placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     *
     * @param placeId
     * The place_id
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     *
     * @return
     * The postcodeLocalities
     */
    public List<String> getPostcodeLocalities() {
        return postcodeLocalities;
    }

    /**
     *
     * @param postcodeLocalities
     * The postcode_localities
     */
    public void setPostcodeLocalities(List<String> postcodeLocalities) {
        this.postcodeLocalities = postcodeLocalities;
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
