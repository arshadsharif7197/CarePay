package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'identity_document'
 */

public class DemographicMetadataPropertiesIdDocDTO {
    @SerializedName("identity_document_photos") @Expose
    public MetadataEntityDTO identityDocumentPhotos;

    @SerializedName("identity_document_number") @Expose
    public MetadataEntityDTO identityDocumentNumber;

    @SerializedName("identity_document_country") @Expose
    public MetadataEntityDTO identityDocumentCountry;

    @SerializedName("identity_document_state") @Expose
    public MetadataEntityDTO identityDocumentState;

    @SerializedName("identity_document_type") @Expose
    public MetadataEntityDTO identityDocumentType;
}
