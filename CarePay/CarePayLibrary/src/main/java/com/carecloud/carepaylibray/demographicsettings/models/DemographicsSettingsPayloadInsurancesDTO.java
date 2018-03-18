package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsPayloadInsurancesDTO {

    @SerializedName("insurance_member_id")
    @Expose
    private String insuranceMemberId;
    @SerializedName("insurance_provider")
    @Expose
    private String insuranceProvider;
    @SerializedName("insurance_type")
    @Expose
    private String insuranceType;
    @SerializedName("insurance_plan")
    @Expose
    private String insurancePlan;
    @SerializedName("insurance_photos")
    @Expose
    private List<DemographicsSettingsInsurancePhotoDTO> insurancePhotos = new ArrayList<>();

    public String getInsuranceMemberId() {
        return insuranceMemberId;
    }

    public void setInsuranceMemberId(String insuranceMemberId) {
        this.insuranceMemberId = insuranceMemberId;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public List<DemographicsSettingsInsurancePhotoDTO> getInsurancePhotos() {
        return insurancePhotos;
    }

    public void setInsurancePhotos(List<DemographicsSettingsInsurancePhotoDTO> insurancePhotos) {
        this.insurancePhotos = insurancePhotos;
    }

}
