package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/5/17.
 */

public class InsuranceMetadataEntityDTO {
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
    private List<MetadataInsuranceOptionDTO> insuranceOptions = new ArrayList<>();

    @SerializedName("validations")
    @Expose
    private List<MetadataValidationDTO> validations = new ArrayList<>();

    public List<MetadataInsuranceOptionDTO> getOptions() {
        return insuranceOptions;
    }

    public void setInsuranceOptions(List<MetadataInsuranceOptionDTO> insuranceOptions) {
        this.insuranceOptions = insuranceOptions;
    }

    public String getLabel() {
        return label;
    }

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

    public List<MetadataValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<MetadataValidationDTO> validations) {
        this.validations = validations;
    }
}
