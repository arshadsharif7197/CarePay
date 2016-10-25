package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Metadata DTO for a UI field validation
 */
public class MetadataValidationDTO {
    @SerializedName("type") @Expose
    public String typeMeta;

    @SerializedName("value") @Expose
    public String valueMeta;

    @SerializedName("error_message") @Expose
    public String errorMessageMeta;
}