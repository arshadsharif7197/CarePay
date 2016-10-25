package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Object holding metadata entities (eg 'address', 'personal_details', 'id_document')
 */
public class DemographicMetadataEntitiesDTO {
    @SerializedName("address") @Expose
    public MetadataEntityDTO addressMetaDTO;

    @SerializedName("personal_details") @Expose
    public MetadataEntityDTO persDetailsMetaDTO;

    @SerializedName("identity_documents") @Expose
    public MetadataEntityDTO idDocsMetaDTO;

    @SerializedName("insurances") @Expose
    public MetadataEntityDTO insurancesMetaDTO;

    @SerializedName("updates") @Expose
    public MetadataEntityDTO updatesMetaDTO;
}
