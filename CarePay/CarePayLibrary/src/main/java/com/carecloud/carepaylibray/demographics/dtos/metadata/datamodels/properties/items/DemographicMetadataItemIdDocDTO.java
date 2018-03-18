package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata for id doc
 */
public class DemographicMetadataItemIdDocDTO {
    @SerializedName("identity_document")
    @Expose
    private DemographicMetadataEntityItemIdDocDTO identityDocument = new DemographicMetadataEntityItemIdDocDTO();

    public DemographicMetadataEntityItemIdDocDTO getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(DemographicMetadataEntityItemIdDocDTO identityDocument) {
        this.identityDocument = identityDocument;
    }
}
