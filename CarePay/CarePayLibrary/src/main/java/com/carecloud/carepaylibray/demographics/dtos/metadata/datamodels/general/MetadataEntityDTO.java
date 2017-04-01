package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("method")
    @Expose
    private MetadataActionDTO method = new MetadataActionDTO();

    @SerializedName("options")
    @Expose
    private List<MetadataOptionDTO> options = new ArrayList<>();

    @SerializedName("validations")
    @Expose
    private List<MetadataValidationDTO> validations = new ArrayList<>();

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MetadataActionDTO getMethod() {
        return method;
    }

    public void setMethod(MetadataActionDTO method) {
        this.method = method;
    }

    public List<MetadataOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<MetadataOptionDTO> options) {
        this.options = options;
    }

    public List<MetadataValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<MetadataValidationDTO> validations) {
        this.validations = validations;
    }
}
