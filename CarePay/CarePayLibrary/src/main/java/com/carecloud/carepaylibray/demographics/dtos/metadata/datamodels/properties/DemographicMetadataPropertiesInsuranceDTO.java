package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.InsuranceMetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific properties DTO for insurance.
 */

public class DemographicMetadataPropertiesInsuranceDTO {
    @SerializedName("insurance_photos")
    @Expose
    private MetadataEntityDTO insurancePhotos = new MetadataEntityDTO();

    @SerializedName("insurance_provider")
    @Expose
    private InsuranceMetadataEntityDTO insuranceProvider = new InsuranceMetadataEntityDTO();

    @SerializedName("insurance_plan")
    @Expose
    private MetadataEntityDTO insurancePlan = new MetadataEntityDTO();

    @SerializedName("insurance_member_id")
    @Expose
    private MetadataEntityDTO insuranceMemberId = new MetadataEntityDTO();

    @SerializedName("insurance_group_id")
    @Expose
    private MetadataEntityDTO insuranceGroupId = new MetadataEntityDTO();

    @SerializedName("insurance_type")
    @Expose
    private MetadataEntityDTO insuranceType = new MetadataEntityDTO();

    @SerializedName("required")
    @Expose
    private List<MetadataEntityDTO> required = new ArrayList<>();

    public MetadataEntityDTO getInsurancePhotos() {
        return insurancePhotos;
    }

    public void setInsurancePhotos(MetadataEntityDTO insurancePhotos) {
        this.insurancePhotos = insurancePhotos;
    }

    public InsuranceMetadataEntityDTO getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(InsuranceMetadataEntityDTO insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public MetadataEntityDTO getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(MetadataEntityDTO insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public MetadataEntityDTO getInsuranceMemberId() {
        return insuranceMemberId;
    }

    public void setInsuranceMemberId(MetadataEntityDTO insuranceMemberId) {
        this.insuranceMemberId = insuranceMemberId;
    }

    public MetadataEntityDTO getInsuranceGroupId() {
        return insuranceGroupId;
    }

    public void setInsuranceGroupId(MetadataEntityDTO insuranceGroupId) {
        this.insuranceGroupId = insuranceGroupId;
    }

    public MetadataEntityDTO getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(MetadataEntityDTO insuranceType) {
        this.insuranceType = insuranceType;
    }

    public List<MetadataEntityDTO> getRequired() {
        return required;
    }

    public void setRequired(List<MetadataEntityDTO> required) {
        this.required = required;
    }
}
