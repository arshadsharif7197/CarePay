package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsDemographicPayloadDTO {

    @SerializedName("drivers_license")
    @Expose
    private DemographicsSettingsDriversLicenseDTO driversLicense = new DemographicsSettingsDriversLicenseDTO();
    @SerializedName("insurances")
    @Expose
    //private List<DemographicsSettingsPayloadInsurancesDTO> insurances = null;
    private List<DemographicInsurancePayloadDTO> insurances = new ArrayList<>();
    @SerializedName("personal_details")
    @Expose
    private DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = new DemographicsSettingsPersonalDetailsPayloadDTO();
    @SerializedName("address")
    @Expose
    private DemographicsSettingsPayloadAddressDTO address = new DemographicsSettingsPayloadAddressDTO();
    @SerializedName("identity_documents")
    @Expose
    //private List<DemographicsSettingsPayloadIdentityDocumentDTO> identityDocuments = null;
    private List<DemographicIdDocPayloadDTO> identityDocuments = new ArrayList<>();

    public DemographicsSettingsDriversLicenseDTO getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(DemographicsSettingsDriversLicenseDTO driversLicense) {
        this.driversLicense = driversLicense;
    }

    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
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

    public List<DemographicIdDocPayloadDTO> getIdentityDocuments() {
        return identityDocuments;
    }

    public void setIdentityDocuments(List<DemographicIdDocPayloadDTO> identityDocuments) {
        this.identityDocuments = identityDocuments;
    }

}
