package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for an allowed operation on a metadata entity
 * 'labelMeta' is undefined as
 */
class MetadataAllowedOpDTO {
    @SerializedName("name") @Expose
    public String name;

    @SerializedName("label") @Expose
    private String label;

    /**
     * Getter
     * @return The labelMeta
     */
    public String getLabel() {
        return StringUtil.isNullOrEmpty(label) ? CarePayConstants.NOT_DEFINED : label;
    }

    /**
     * Setter
     * @param labelMeta The labelMeta
     */
    public void setLabelMeta(String labelMeta) {
        this.label = labelMeta;
    }
}
