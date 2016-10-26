package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityUpdatesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Object holding metadata entities (eg 'address', 'personal_details', 'id_document')
 */
public class DemographicMetadataEntitiesDTO {
    @SerializedName("address") @Expose
    public DemographicMetadataEntityAddressDTO address;

    @SerializedName("personal_details") @Expose
    public DemographicMetadataEntityPersDetailsDTO personalDetails;

    @SerializedName("identity_documents") @Expose
    public DemographicMetadataEntityIdDocsDTO identityDocuments;

    @SerializedName("insurances") @Expose
    public DemographicMetadataEntityInsurancesDTO insurances;

    @SerializedName("updates") @Expose
    public DemographicMetadataEntityUpdatesDTO updates;
}
