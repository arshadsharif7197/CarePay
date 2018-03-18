package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/15/17
 */

public class InsuranceModelProperties {

    @SerializedName("insurance_provider")
    @Expose
    private DemographicsInsuranceField insuranceProvider;

    @SerializedName("insurance_plan")
    @Expose
    private DemographicsField insurancePlan;

    @SerializedName("insurance_member_id")
    @Expose
    private DemographicsField insuranceMemberId;

    @SerializedName("insurance_group_id")
    @Expose
    private DemographicsField insuranceGroupId;

    @SerializedName("insurance_type")
    @Expose
    private DemographicsField insuranceType;

    @SerializedName("relationship")
    @Expose
    private DemographicsField relationship;

    @SerializedName("policy_holder")
    @Expose
    private DemographicsField policyHolder;

    @SerializedName("policy_holder_gender")
    @Expose
    private DemographicsField policyHolderGender;

    public DemographicsInsuranceField getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(DemographicsInsuranceField insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public DemographicsField getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(DemographicsField insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public DemographicsField getInsuranceMemberId() {
        return insuranceMemberId;
    }

    public void setInsuranceMemberId(DemographicsField insuranceMemberId) {
        this.insuranceMemberId = insuranceMemberId;
    }

    public DemographicsField getInsuranceGroupId() {
        return insuranceGroupId;
    }

    public void setInsuranceGroupId(DemographicsField insuranceGroupId) {
        this.insuranceGroupId = insuranceGroupId;
    }

    public DemographicsField getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(DemographicsField insuranceType) {
        this.insuranceType = insuranceType;
    }

    public DemographicsField getRelationship() {
        return relationship;
    }

    public void setRelationship(DemographicsField relationship) {
        this.relationship = relationship;
    }

    public DemographicsField getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(DemographicsField policyHolder) {
        this.policyHolder = policyHolder;
    }

    public DemographicsField getPolicyHolderGender() {
        return policyHolderGender;
    }

    public void setPolicyHolderGender(DemographicsField policyHolderGender) {
        this.policyHolderGender = policyHolderGender;
    }
}
