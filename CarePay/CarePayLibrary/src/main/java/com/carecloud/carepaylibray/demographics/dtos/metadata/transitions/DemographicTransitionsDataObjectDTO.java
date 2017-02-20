package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 * Model for transition data objet.
 */

class DemographicTransitionsDataObjectDTO {
    @SerializedName("address")
    @Expose
    private DemographicTransitionsDataDTO address = new DemographicTransitionsDataDTO();

    @SerializedName("personal_details")
    @Expose
    private DemographicTransitionsDataDTO personalDetails = new DemographicTransitionsDataDTO();

    @SerializedName("drivers_license")
    @Expose
    private DemographicTransitionsDataDTO driversLicense = new DemographicTransitionsDataDTO();

    @SerializedName("insurances")
    @Expose
    private DemographicTransitionsDataDTO insurances = new DemographicTransitionsDataDTO();

    @SerializedName("updates")
    @Expose
    private String updates;

    /**
     *
     * @return
     * The address
     */
    public DemographicTransitionsDataDTO getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicTransitionsDataDTO address) {
        this.address = address;
    }

    public DemographicTransitionsDataDTO getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(DemographicTransitionsDataDTO personalDetails) {
        this.personalDetails = personalDetails;
    }

    public DemographicTransitionsDataDTO getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(DemographicTransitionsDataDTO driversLicense) {
        this.driversLicense = driversLicense;
    }

    public DemographicTransitionsDataDTO getInsurances() {
        return insurances;
    }

    public void setInsurances(DemographicTransitionsDataDTO insurances) {
        this.insurances = insurances;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }
}
