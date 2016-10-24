package com.carecloud.carepaylibray.demographics.models.transitions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 * Model for transition data objet.
 */
class DemographicTransitionsDataObjectDTO {
    @SerializedName("address")
    @Expose
    private DemographicTransitionsDataDTO address;

    @SerializedName("personal_details")
    @Expose
    private DemographicTransitionsDataDTO personalDetails;

    @SerializedName("drivers_license")
    @Expose
    private DemographicTransitionsDataDTO driversLicense;

    @SerializedName("insurances")
    @Expose
    private DemographicTransitionsDataDTO insurances;

    @SerializedName("updates")
    @Expose
    // TODO
    private Object updates;



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

    public Object getUpdates() {
        return updates;
    }

    public void setUpdates(Object updates) {
        this.updates = updates;
    }
}
