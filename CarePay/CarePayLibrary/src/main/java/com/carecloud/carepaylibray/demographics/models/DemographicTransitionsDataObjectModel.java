package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class DemographicTransitionsDataObjectModel implements Parcelable {
    @SerializedName("address")
    @Expose
    private DemographicTransitionsDataModel address;

    @SerializedName("personal_details")
    @Expose
    private DemographicTransitionsDataModel personalDetails;

    @SerializedName("drivers_license")
    @Expose
    private DemographicTransitionsDataModel driversLicense;

    @SerializedName("insurances")
    @Expose
    private DemographicTransitionsDataModel insurances;

    @SerializedName("updates")
    @Expose
    // TODO
    private Object updates;

    public DemographicTransitionsDataObjectModel() {

    }

    protected DemographicTransitionsDataObjectModel(Parcel in) {
        address = in.readParcelable(DemographicTransitionsDataModel.class.getClassLoader());
        personalDetails = in.readParcelable(DemographicTransitionsDataModel.class.getClassLoader());
        driversLicense = in.readParcelable(DemographicTransitionsDataModel.class.getClassLoader());
        insurances = in.readParcelable(DemographicTransitionsDataModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
        dest.writeParcelable(personalDetails, flags);
        dest.writeParcelable(driversLicense, flags);
        dest.writeParcelable(insurances, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicTransitionsDataObjectModel> CREATOR = new Creator<DemographicTransitionsDataObjectModel>() {
        @Override
        public DemographicTransitionsDataObjectModel createFromParcel(Parcel in) {
            return new DemographicTransitionsDataObjectModel(in);
        }

        @Override
        public DemographicTransitionsDataObjectModel[] newArray(int size) {
            return new DemographicTransitionsDataObjectModel[size];
        }
    };

    /**
     *
     * @return
     * The address
     */
    public DemographicTransitionsDataModel getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicTransitionsDataModel address) {
        this.address = address;
    }

    public DemographicTransitionsDataModel getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(DemographicTransitionsDataModel personalDetails) {
        this.personalDetails = personalDetails;
    }

    public DemographicTransitionsDataModel getDriversLicense() {
        return driversLicense;
    }

    public void setDriversLicense(DemographicTransitionsDataModel driversLicense) {
        this.driversLicense = driversLicense;
    }

    public DemographicTransitionsDataModel getInsurances() {
        return insurances;
    }

    public void setInsurances(DemographicTransitionsDataModel insurances) {
        this.insurances = insurances;
    }

    public Object getUpdates() {
        return updates;
    }

    public void setUpdates(Object updates) {
        this.updates = updates;
    }
}
