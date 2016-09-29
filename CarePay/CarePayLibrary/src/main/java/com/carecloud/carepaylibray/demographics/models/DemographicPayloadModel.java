package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicPayloadModel implements Parcelable {

    @SerializedName("address")
    @Expose
    private DemographicPayloadAddressModel address;
    @SerializedName("personal_details")
    @Expose
    private DemographicPayloadPersonalDetailsModel personalDetails;
    @SerializedName("drivers_license")
    @Expose
    private DemographicPayloadDriversLicenseModel driversLicense;
    @SerializedName("insurances")
    @Expose
    private List<DemographicPayloadInsuranceModel> insurances = new ArrayList<DemographicPayloadInsuranceModel>();
    @SerializedName("updates")
    @Expose
    private List<String> updates = new ArrayList<String>();

    public DemographicPayloadModel() {

    }

    protected DemographicPayloadModel(Parcel in) {
        address = in.readParcelable(DemographicPayloadAddressModel.class.getClassLoader());
        personalDetails = in.readParcelable(DemographicPayloadPersonalDetailsModel.class.getClassLoader());
        driversLicense = in.readParcelable(DemographicPayloadDriversLicenseModel.class.getClassLoader());
        insurances = in.createTypedArrayList(DemographicPayloadInsuranceModel.CREATOR);
        updates = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
        dest.writeParcelable(personalDetails, flags);
        dest.writeParcelable(driversLicense, flags);
        dest.writeTypedList(insurances);
        dest.writeStringList(updates);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadModel> CREATOR = new Creator<DemographicPayloadModel>() {
        @Override
        public DemographicPayloadModel createFromParcel(Parcel in) {
            return new DemographicPayloadModel(in);
        }

        @Override
        public DemographicPayloadModel[] newArray(int size) {
            return new DemographicPayloadModel[size];
        }
    };

    /**
     *
     * @return
     * The address
     */
    public DemographicPayloadAddressModel getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicPayloadAddressModel address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemographicPayloadPersonalDetailsModel getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemographicPayloadPersonalDetailsModel personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The driversLicense
     */
    public DemographicPayloadDriversLicenseModel getDriversLicense() {
        return driversLicense;
    }

    /**
     *
     * @param driversLicense
     * The drivers_license
     */
    public void setDriversLicense(DemographicPayloadDriversLicenseModel driversLicense) {
        this.driversLicense = driversLicense;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemographicPayloadInsuranceModel> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemographicPayloadInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    /**
     *
     * @return
     * The updates
     */
    public List<String> getUpdates() {
        return updates;
    }

    /**
     *
     * @param updates
     * The updates
     */
    public void setUpdates(List<String> updates) {
        this.updates = updates;
    }
}
