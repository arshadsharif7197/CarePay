package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityUpdatesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Object holding metadata entities (eg 'address', 'personal_details', 'id_document')
 */
public class DemographicMetadataEntitiesDTO {
    @SerializedName("address")
    @Expose
    private DemographicMetadataEntityAddressDTO address = new DemographicMetadataEntityAddressDTO();

    @SerializedName("personal_details")
    @Expose
    private DemographicMetadataEntityPersDetailsDTO personalDetails = new DemographicMetadataEntityPersDetailsDTO();

    @SerializedName("identity_documents")
    @Expose
    private DemographicMetadataEntityIdDocsDTO identityDocuments = new DemographicMetadataEntityIdDocsDTO();

    @SerializedName("insurances")
    @Expose
    private DemographicMetadataEntityInsurancesDTO insurances = new DemographicMetadataEntityInsurancesDTO();

    @SerializedName("updates")
    @Expose
    private DemographicMetadataEntityUpdatesDTO updates = new DemographicMetadataEntityUpdatesDTO();

    public DemographicMetadataEntityAddressDTO getAddress() {
        return address;
    }

    public void setAddress(DemographicMetadataEntityAddressDTO address) {
        this.address = address;
    }

    public DemographicMetadataEntityPersDetailsDTO getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(DemographicMetadataEntityPersDetailsDTO personalDetails) {
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

    public DemographicMetadataEntityUpdatesDTO getUpdates() {
        return updates;
    }

    public void setUpdates(DemographicMetadataEntityUpdatesDTO updates) {
        this.updates = updates;
    }
}
