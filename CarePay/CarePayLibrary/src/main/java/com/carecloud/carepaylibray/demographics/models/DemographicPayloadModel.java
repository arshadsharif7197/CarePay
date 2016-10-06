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
    private DemographicPayloadPersonalDetailsModel personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemographicPayloadIdDocumentModel driversLicense;

    @SerializedName("insurances")
    @Expose
    private List<DemographicPayloadInsuranceModel> insurances = new ArrayList<DemographicPayloadInsuranceModel>();

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
    public DemographicPayloadPersonalDetailsModel getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemographicPayloadPersonalDetailsModel personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The driversLicense
     */
    public DemographicPayloadIdDocumentModel getDriversLicense() {
        return driversLicense;
    }

    /**
     *
     * @param driversLicense
     * The drivers_license
     */
    public void setDriversLicense(DemographicPayloadIdDocumentModel driversLicense) {
        this.driversLicense = driversLicense;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemographicPayloadInsuranceModel> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemographicPayloadInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicUpdateModel> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemographicUpdateModel> updates) {
        this.updates = updates;
    }
}
