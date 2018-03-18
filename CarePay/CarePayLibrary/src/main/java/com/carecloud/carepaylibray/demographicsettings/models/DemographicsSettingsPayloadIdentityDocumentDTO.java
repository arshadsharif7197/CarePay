package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsPayloadIdentityDocumentDTO {
    @SerializedName("identity_document_type")
    @Expose
    private String identityDocumentType;
    @SerializedName("identity_document_state")
    @Expose
    private String identityDocumentState;
    @SerializedName("identity_document_photos")
    @Expose
    private List<DemographicsSettingsIdentityDocumentPhotoDTO> identityDocumentPhotos = new ArrayList<>();

    public String getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(String identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public String getIdentityDocumentState() {
        return identityDocumentState;
    }

    public void setIdentityDocumentState(String identityDocumentState) {
        this.identityDocumentState = identityDocumentState;
    }

    public List<DemographicsSettingsIdentityDocumentPhotoDTO> getIdentityDocumentPhotos() {
        return identityDocumentPhotos;
    }

    public void setIdentityDocumentPhotos(List<DemographicsSettingsIdentityDocumentPhotoDTO> identityDocumentPhotos) {
        this.identityDocumentPhotos = identityDocumentPhotos;
    }


}
