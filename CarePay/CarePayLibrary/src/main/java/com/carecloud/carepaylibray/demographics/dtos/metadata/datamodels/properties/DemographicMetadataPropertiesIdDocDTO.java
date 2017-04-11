package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'identity_document'
 */

public class DemographicMetadataPropertiesIdDocDTO {
    @SerializedName("identity_document_photos")
    @Expose
    private MetadataEntityDTO identityDocumentPhotos = new MetadataEntityDTO();

    @SerializedName("identity_document_number")
    @Expose
    private MetadataEntityDTO identityDocumentNumber = new MetadataEntityDTO();

    @SerializedName("identity_document_country")
    @Expose
    private MetadataEntityDTO identityDocumentCountry = new MetadataEntityDTO();

    @SerializedName("identity_document_state")
    @Expose
    private MetadataEntityDTO identityDocumentState = new MetadataEntityDTO();

    @SerializedName("identity_document_type")
    @Expose
    private MetadataEntityDTO identityDocumentType = new MetadataEntityDTO();

    @SerializedName("required")
    @Expose
    private List<MetadataEntityDTO> required = new ArrayList<>();

    public MetadataEntityDTO getIdentityDocumentPhotos() {
        return identityDocumentPhotos;
    }

    public void setIdentityDocumentPhotos(MetadataEntityDTO identityDocumentPhotos) {
        this.identityDocumentPhotos = identityDocumentPhotos;
    }

    public MetadataEntityDTO getIdentityDocumentNumber() {
        return identityDocumentNumber;
    }

    public void setIdentityDocumentNumber(MetadataEntityDTO identityDocumentNumber) {
        this.identityDocumentNumber = identityDocumentNumber;
    }

    public MetadataEntityDTO getIdentityDocumentCountry() {
        return identityDocumentCountry;
    }

    public void setIdentityDocumentCountry(MetadataEntityDTO identityDocumentCountry) {
        this.identityDocumentCountry = identityDocumentCountry;
    }

    public MetadataEntityDTO getIdentityDocumentState() {
        return identityDocumentState;
    }

    public void setIdentityDocumentState(MetadataEntityDTO identityDocumentState) {
        this.identityDocumentState = identityDocumentState;
    }

    public MetadataEntityDTO getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(MetadataEntityDTO identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public List<MetadataEntityDTO> getRequired() {
        return required;
    }

    public void setRequired(List<MetadataEntityDTO> required) {
        this.required = required;
    }
}
