package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadDriversLicenseModel implements Parcelable{

    @SerializedName("license_photo")
    @Expose
    private String licensePhoto;
    @SerializedName("license_number")
    @Expose
    private String licenseNumber;
    @SerializedName("license_state")
    @Expose
    private String licenseState;

    public DemographicPayloadDriversLicenseModel() {

    }

    protected DemographicPayloadDriversLicenseModel(android.os.Parcel in) {
        licensePhoto = in.readString();
        licenseNumber = in.readString();
        licenseState = in.readString();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(licensePhoto);
        dest.writeString(licenseNumber);
        dest.writeString(licenseState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadDriversLicenseModel> CREATOR = new Creator<DemographicPayloadDriversLicenseModel>() {
        @Override
        public DemographicPayloadDriversLicenseModel createFromParcel(android.os.Parcel in) {
            return new DemographicPayloadDriversLicenseModel(in);
        }

        @Override
        public DemographicPayloadDriversLicenseModel[] newArray(int size) {
            return new DemographicPayloadDriversLicenseModel[size];
        }
    };

    /**
     *
     * @return
     * The licensePhoto
     */
    public String getLicensePhoto() {
        return licensePhoto;
    }

    /**
     *
     * @param licensePhoto
     * The license_photo
     */
    public void setLicensePhoto(String licensePhoto) {
        this.licensePhoto = licensePhoto;
    }

    /**
     *
     * @return
     * The licenseNumber
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     *
     * @param licenseNumber
     * The license_number
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     *
     * @return
     * The licenseState
     */
    public String getLicenseState() {
        return licenseState;
    }

    /**
     *
     * @param licenseState
     * The license_state
     */
    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }
}
