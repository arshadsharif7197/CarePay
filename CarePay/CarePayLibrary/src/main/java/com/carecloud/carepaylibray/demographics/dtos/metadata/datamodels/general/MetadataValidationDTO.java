package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Metadata DTO for a UI field validation
 */
public class MetadataValidationDTO {
    @SerializedName("type") @Expose
    public String type;

    @SerializedName("value") @Expose
    public String value;

    @SerializedName("error_message") @Expose
    private String errorMessage;

    public String getErrorMessage() {
        return StringUtil.isNullOrEmpty(errorMessage) ? CarePayConstants.NOT_DEFINED : errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}