package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsIdentityDocumentPhotoDTO {
    @SerializedName("identity_document_photo")
    @Expose
    private String identityDocumentPhoto;

    public String getIdentityDocumentPhoto() {
        return identityDocumentPhoto;
    }

    public void setIdentityDocumentPhoto(String identityDocumentPhoto) {
        this.identityDocumentPhoto = identityDocumentPhoto;
    }
}
