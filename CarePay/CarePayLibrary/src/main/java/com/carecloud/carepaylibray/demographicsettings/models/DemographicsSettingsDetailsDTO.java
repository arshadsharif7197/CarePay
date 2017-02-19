package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsDetailsDTO {
    @SerializedName("address")
    @Expose
    private DemographicsSettingsAddressInfoDTO address = new DemographicsSettingsAddressInfoDTO();
    @SerializedName("personal_details")
    @Expose
    private DemographicsSettingsPersonalDetailsPropertiesDTO personalDetails = new DemographicsSettingsPersonalDetailsPropertiesDTO();
    @SerializedName("identity_documents")
    @Expose
    private DemographicMetadataEntityIdDocsDTO identityDocuments = new DemographicMetadataEntityIdDocsDTO();
    @SerializedName("insurances")
    @Expose
    private DemographicMetadataEntityInsurancesDTO insurances = new DemographicMetadataEntityInsurancesDTO();
    @SerializedName("updates")
    @Expose
    private DemographicsSettingsUpdatesDTO updates = new DemographicsSettingsUpdatesDTO();

    public DemographicsSettingsPersonalDetailsPropertiesDTO getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(DemographicsSettingsPersonalDetailsPropertiesDTO personalDetails) {
        this.personalDetails = personalDetails;
    }

    public DemographicMetadataEntityIdDocsDTO getIdentityDocuments() {
        return identityDocuments;
    }

    public void setIdentityDocuments(DemographicMetadataEntityIdDocsDTO identityDocuments) {
        this.identityDocuments = identityDocuments;
    }

    public DemographicMetadataEntityInsurancesDTO getInsurances() {
        return insurances;
    }

    public void setInsurances(DemographicMetadataEntityInsurancesDTO insurances) {
        this.insurances = insurances;
    }

    public DemographicsSettingsUpdatesDTO getUpdates() {
        return updates;
    }

    public void setUpdates(DemographicsSettingsUpdatesDTO updates) {
        this.updates = updates;
    }

    public DemographicsSettingsAddressInfoDTO getAddress() {
        return address;
    }

    public void setAddress(DemographicsSettingsAddressInfoDTO address) {
        this.address = address;
    }
}
