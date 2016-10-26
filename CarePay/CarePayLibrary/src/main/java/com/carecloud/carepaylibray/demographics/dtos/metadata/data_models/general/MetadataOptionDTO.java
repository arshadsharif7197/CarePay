package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general;

import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for the metadata describing the possible choices of the values of a UI field
 *
 * 'label' is private as its getter is not trivial
 */
public class MetadataOptionDTO {
    @SerializedName("name") @Expose
    public String name;

    @SerializedName("label") @Expose
    private String label;

    /**
     * Getter
     * @return The lable
     */
    public String getLabel() {
        return StringUtil.isNullOrEmpty(label) ? CarePayConstants.NOT_DEFINED : label;
    }

    /**
     * Setter
     * @param label The lsbel
     */
    public void setLabel(String label) {
        this.label = label;
    }
}