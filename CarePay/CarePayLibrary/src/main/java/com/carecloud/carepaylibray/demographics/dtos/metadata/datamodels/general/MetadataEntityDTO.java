package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lsoco_user on 10/24/2016.
 * Blueprint of a demographic metadata entity; optionally contains the following fields:
 * label, action, type, properties, validations, options.
 * In demographics.json, it corresponds to each fields under 'data_models' -> 'demographic' (eg, 'address' etc)
 * Note: All fields except label are public to reduce the boiler-plate code of the (trivial) getters and setters;
 * 'label' is private because its getter is not trivial (ie, a test is performed in it)
 */

public class MetadataEntityDTO {

    @SerializedName("label") @Expose
    private String label;

    @SerializedName("type") @Expose
    public String type;

    @SerializedName("method") @Expose
    public MetadataActionDTO method;

    @SerializedName("options") @Expose
    public List<MetadataOptionDTO> options;

    @SerializedName("validations") @Expose
    public List<MetadataValidationDTO> validations;

    /**
     * Getter
     * @return The label
     */
    public String getLabel() {
        return StringUtil.isNullOrEmpty(label) ? CarePayConstants.NOT_DEFINED : label;
    }

    /**
     * Setter
     * @param label The label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
