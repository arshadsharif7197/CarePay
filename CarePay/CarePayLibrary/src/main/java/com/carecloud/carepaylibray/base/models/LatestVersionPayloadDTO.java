package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

public class LatestVersionPayloadDTO {

    @SerializedName("client_version")
    private LatestVersionModel versionModel = new LatestVersionModel();


    public LatestVersionModel getVersionModel() {
        return versionModel;
    }

    public void setVersionModel(LatestVersionModel versionModel) {
        this.versionModel = versionModel;
    }
}
