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
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("value")
    @Expose
    private Object value;

    @SerializedName("error_message")
    @Expose
    private String errorMessage;

    public String getErrorMessage() {
        return StringUtil.isNullOrEmpty(errorMessage) ? CarePayConstants.NOT_DEFINED : errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}