package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataPropertiesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'identity_document'
 */

public class DemographicMetadataPropertiesIdDocDTO extends MetadataPropertiesDTO {
    @SerializedName("identity_document_photos") @Expose
    public MetadataEntityDTO idDocPhotosMetaDTO;

    @SerializedName("identity_document_number") @Expose
    public MetadataEntityDTO idDocNumberMetaDTO;

    @SerializedName("identity_document_country") @Expose
    public MetadataEntityDTO idDocCountryMetaDTO;

    @SerializedName("identity_document_state") @Expose
    public MetadataEntityDTO idDocStateMetaDTO;

    @SerializedName("identity_document_type") @Expose
    public MetadataEntityDTO idDocTypeMetaDTO;
}
