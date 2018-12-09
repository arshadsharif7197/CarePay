package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 16/07/18.
 */
public class PracticeFormMetadata {

    @Expose
    @SerializedName("version")
    private String version;

    @Expose
    @SerializedName("updated_dt")
    private String updatedDate;

    @Expose
    @SerializedName("created_dt")
    private String createdDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
