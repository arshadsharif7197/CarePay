package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for an allowed operation on a metadata entity
 *
 * 'labelMeta' is undefined as
 */
public class MetadataAllowedOpDTO {
    @SerializedName("name") @Expose
    public String operationMeta;

    @SerializedName("label") @Expose
    private String labelMeta;

    /**
     * Getter
     * @return The labelMeta
     */
    public String getLabelMeta() {
        return StringUtil.isNullOrEmpty(labelMeta) ? "undefined labelMeta" : labelMeta;
    }

    /**
     * Setter
     * @param labelMeta The labelMeta
     */
    public void setLabelMeta(String labelMeta) {
        this.labelMeta = labelMeta;
    }
}
