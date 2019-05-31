package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * Model for insurance payload.
 */
public class DemographicInsurancePayloadDTO extends PatientModel {

    @SerializedName("insurance_photos")
    @Expose
    private List<DemographicInsurancePhotoDTO> insurancePhotos = new ArrayList<>();

    @SerializedName("insurance_provider")
    @Expose
    private String insuranceProvider;

    @SerializedName("insurance_plan")
    @Expose
    private String insurancePlan;

    @SerializedName("insurance_member_id")
    @Expose
    private String insuranceMemberId;

    @SerializedName("insurance_group_id")
    @Expose
    private String insuranceGroupId;

    @SerializedName("insurance_type")
    @Expose
    private String insuranceType;

    @SerializedName("relationship")
    @Expose
    private String relationship;

    @SerializedName("policy_holder_first_name")
    @Expose
    private String policyFirstNameHolder;

    @SerializedName("policy_holder_middle_name")
    @Expose
    private String policyMiddleNameHolder;

    @SerializedName("policy_holder_last_name")
    @Expose
    private String policyLastNameHolder;

    @SerializedName("policy_holder_date_of_birth")
    @Expose
    private String policyDateOfBirthHolder;

    @SerializedName("policy_holder_gender")
    @Expose
    private String policyGenderHolder;

    @SerializedName("delete")
    @Expose
    private boolean deleted = false;

    @SerializedName("insurance_id")
    @Expose
    private String insuranceId;

    /**
     * @return The insuranceProvider
     */
    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public List<DemographicInsurancePhotoDTO> getInsurancePhotos() {
        return insurancePhotos;
    }

    public void setInsurancePhotos(List<DemographicInsurancePhotoDTO> insurancePhotos) {
        this.insurancePhotos = insurancePhotos;
    }

    /**
     * @param insuranceProvider The insurance_provider
     */
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    /**
     * @return The insurancePlan
     */
    public String getInsurancePlan() {
        return insurancePlan;
    }

    /**
     * @param insurancePlan The insurance_plan
     */
    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    /**
     * @return The insuranceMemberId
     */
    public String getInsuranceMemberId() {
        return insuranceMemberId;
    }

    /**
     * @param insuranceMemberId The insurance_member_id
     */
    public void setInsuranceMemberId(String insuranceMemberId) {
        this.insuranceMemberId = insuranceMemberId;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuranceGroupId() {
        return insuranceGroupId;
    }

    public void setInsuranceGroupId(String insuranceGroupId) {
        this.insuranceGroupId = insuranceGroupId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPolicyFirstNameHolder() {
        return policyFirstNameHolder;
    }

    public void setPolicyFirstNameHolder(String policyFirstNameHolder) {
        this.policyFirstNameHolder = policyFirstNameHolder;
    }

    public String getPolicyLastNameHolder() {
        return policyLastNameHolder;
    }

    public void setPolicyLastNameHolder(String policyLastNameHolder) {
        this.policyLastNameHolder = policyLastNameHolder;
    }

    public String getPolicyDateOfBirthHolder() {
        return policyDateOfBirthHolder;
    }

    public String getFormattedPolicyDateOfBirthHolder() {
        if (StringUtil.isNullOrEmpty(policyDateOfBirthHolder)) {
            return "";
        }
        return DateUtil.getInstance().setDateRaw(policyDateOfBirthHolder).toStringWithFormatMmSlashDdSlashYyyy();
    }

    public void setPolicyDateOfBirthHolder(String policyDateOfBirthHolder) {
        this.policyDateOfBirthHolder = policyDateOfBirthHolder;
    }

    public String getPolicyGenderHolder() {
        return policyGenderHolder;
    }

    public void setPolicyGenderHolder(String policyGenderHolder) {
        this.policyGenderHolder = policyGenderHolder;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getPolicyMiddleNameHolder() {
        return policyMiddleNameHolder;
    }

    public void setPolicyMiddleNameHolder(String policyMiddleNameHolder) {
        this.policyMiddleNameHolder = policyMiddleNameHolder;
    }
}
