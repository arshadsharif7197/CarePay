package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicPayloadModel {

    @SerializedName("address")
    @Expose
    private DemAddressPayloadPojo address;

    @SerializedName("personal_details")
    @Expose
    private DemPersDetailsPayloadPojo personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemIdDocPayloadPojo driversLicense;

    @SerializedName("insurances")
    @Expose
    private List<DemInsurancePayloadPojo> insurances = new ArrayList<DemInsurancePayloadPojo>();

    @SerializedName("updates")
    @Expose
    private List<DemographicUpdateModel> updates = new ArrayList<>();

    /**
     *
     * @return
     * The address
     */
    public DemAddressPayloadPojo getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemAddressPayloadPojo address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemPersDetailsPayloadPojo getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemPersDetailsPayloadPojo personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The driversLicense
     */
    public DemIdDocPayloadPojo getDriversLicense() {
        return driversLicense;
    }

    /**
     *
     * @param driversLicense
     * The drivers_license
     */
    public void setDriversLicense(DemIdDocPayloadPojo driversLicense) {
        this.driversLicense = driversLicense;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemInsurancePayloadPojo> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemInsurancePayloadPojo> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicUpdateModel> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemographicUpdateModel> updates) {
        this.updates = updates;
    }
}
