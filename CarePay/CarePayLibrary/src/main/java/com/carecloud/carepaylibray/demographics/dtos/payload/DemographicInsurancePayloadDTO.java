package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepaylibray.base.models.PatientModel;
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
    private  String insuranceProvider;

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

    @SerializedName("delete")
    @Expose
    private boolean delete = false;

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

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
