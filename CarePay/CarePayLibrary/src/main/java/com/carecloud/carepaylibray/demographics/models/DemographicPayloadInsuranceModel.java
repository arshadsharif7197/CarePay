package com.carecloud.carepaylibray.demographics.models;
import com.carecloud.carepaylibray.base.models.BasePersonModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInsuranceModel extends BasePersonModel{

    @SerializedName("insurance_photo")
    @Expose
    private String insurancePhoto;
    @SerializedName("insurance_provider")
    @Expose
    private String insuranceProvider;
    @SerializedName("insurance_plan")
    @Expose
    private String insurancePlan;
    @SerializedName("insurance_member_id")
    @Expose
    private String insuranceMemberId;

    /**
     *
     * @return
     * The insurancePhoto
     */
    public String getInsurancePhoto() {
        return insurancePhoto;
    }

    /**
     *
     * @param insurancePhoto
     * The insurance_photo
     */
    public void setInsurancePhoto(String insurancePhoto) {
        this.insurancePhoto = insurancePhoto;
    }

    /**
     *
     * @return
     * The insuranceProvider
     */
    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    /**
     *
     * @param insuranceProvider
     * The insurance_provider
     */
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    /**
     *
     * @return
     * The insurancePlan
     */
    public String getInsurancePlan() {
        return insurancePlan;
    }

    /**
     *
     * @param insurancePlan
     * The insurance_plan
     */
    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    /**
     *
     * @return
     * The insuranceMemberId
     */
    public String getInsuranceMemberId() {
        return insuranceMemberId;
    }

    /**
     *
     * @param insuranceMemberId
     * The insurance_member_id
     */
    public void setInsuranceMemberId(String insuranceMemberId) {
        this.insuranceMemberId = insuranceMemberId;
    }
}
