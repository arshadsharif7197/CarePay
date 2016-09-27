package com.carecloud.carepaylibray.demographics.models;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInfoPayloadModel {

    @SerializedName("address")
    @Expose
    private DemographicPayloadAddressModel address;
    @SerializedName("personal_details")
    @Expose
    private DemographicPayloadPersonalDetailsModel personalDetails;
    @SerializedName("drivers_license")
    @Expose
    private DemographicPayloadDriversLicenseModel driversLicense;
    @SerializedName("insurances")
    @Expose
    private List<DemographicPayloadInsuranceModel> insurances = new ArrayList<DemographicPayloadInsuranceModel>();
    @SerializedName("updates")
    @Expose
    private List<String> updates = new ArrayList<String>();

    /**
     *
     * @return
     * The address
     */
    public DemographicPayloadAddressModel getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicPayloadAddressModel address) {
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
    public DemographicPayloadDriversLicenseModel getDriversLicense() {
        return driversLicense;
    }

    /**
     *
     * @param driversLicense
     * The drivers_license
     */
    public void setDriversLicense(DemographicPayloadDriversLicenseModel driversLicense) {
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

    /**
     *
     * @return
     * The updates
     */
    public List<String> getUpdates() {
        return updates;
    }

    /**
     *
     * @param updates
     * The updates
     */
    public void setUpdates(List<String> updates) {
        this.updates = updates;
    }
}
