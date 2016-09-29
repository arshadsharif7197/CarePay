package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;

import com.carecloud.carepaylibray.base.models.BasePersonModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInsuranceModel extends BasePersonModel {

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

    public DemographicPayloadInsuranceModel() {

    }

    protected DemographicPayloadInsuranceModel(Parcel in) {
        super(in);
        insurancePhoto = in.readString();
        insuranceProvider = in.readString();
        insurancePlan = in.readString();
        insuranceMemberId = in.readString();

    }
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(insurancePhoto);
        dest.writeString(insuranceProvider);
        dest.writeString(insurancePlan);
        dest.writeString(insuranceMemberId);
    }

    public static final Creator<DemographicPayloadInsuranceModel> CREATOR = new Creator<DemographicPayloadInsuranceModel>() {
        @Override
        public DemographicPayloadInsuranceModel createFromParcel(Parcel in) {
            return new DemographicPayloadInsuranceModel(in);
        }

        @Override
        public DemographicPayloadInsuranceModel[] newArray(int size) {
            return new DemographicPayloadInsuranceModel[size];
        }
    };

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
