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
    public DemographicMetadataEntityAddressDTO address = new DemographicMetadataEntityAddressDTO();

    @SerializedName("personal_details")
    @Expose
    public DemographicMetadataEntityPersDetailsDTO personalDetails = new DemographicMetadataEntityPersDetailsDTO();

    @SerializedName("identity_documents")
    @Expose
    public DemographicMetadataEntityIdDocsDTO identityDocuments = new DemographicMetadataEntityIdDocsDTO();

    @SerializedName("insurances")
    @Expose
    public DemographicMetadataEntityInsurancesDTO insurances = new DemographicMetadataEntityInsurancesDTO();

    @SerializedName("updates")
    @Expose
    public DemographicMetadataEntityUpdatesDTO updates = new DemographicMetadataEntityUpdatesDTO();
}
