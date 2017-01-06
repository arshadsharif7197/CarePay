package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;


/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsDemographicPayloadDTO {

    @SerializedName("drivers_license")
    @Expose
    private DemographicsSettingsDriversLicenseDTO driversLicense;
    @SerializedName("insurances")
    @Expose
    private List<DemographicsSettingsPayloadInsurancesDTO> insurances = null;
    @SerializedName("personal_details")
    @Expose
    private DemographicsSettingsPersonalDetailsPayloadDTO personalDetails;
    @SerializedName("address")
    @Expose
    private DemographicsSettingsPayloadAddressDTO address;
    @SerializedName("identity_documents")
    @Expose
    private List<DemographicsSettingsPayloadIdentityDocumentDTO> identityDocuments = null;

    public DemographicsSettingsDriversLicenseDTO getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(DemographicsSettingsDriversLicenseDTO driversLicense) {
        this.driversLicense = driversLicense;
    }

    public List<DemographicsSettingsPayloadInsurancesDTO> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<DemographicsSettingsPayloadInsurancesDTO> insurances) {
        this.insurances = insurances;
    }

    public DemographicsSettingsPersonalDetailsPayloadDTO getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(DemographicsSettingsPersonalDetailsPayloadDTO personalDetails) {
        this.personalDetails = personalDetails;
    }

    public DemographicsSettingsPayloadAddressDTO getAddress() {
        return address;
    }

    public void setAddress(DemographicsSettingsPayloadAddressDTO address) {
        this.address = address;
    }

    public List<DemographicsSettingsPayloadIdentityDocumentDTO> getIdentityDocuments() {
        return identityDocuments;
    }

    public void setIdentityDocuments(List<DemographicsSettingsPayloadIdentityDocumentDTO> identityDocuments) {
        this.identityDocuments = identityDocuments;
    }

}
