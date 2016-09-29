package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicTransitionsModel implements Parcelable {
    @SerializedName("confirm_demographics")
    @Expose
    private DemographicTransitionModel  confirmDemographics;

    public DemographicTransitionsModel() {

    }

    protected DemographicTransitionsModel(Parcel in) {
        confirmDemographics = in.readParcelable(DemographicTransitionModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(confirmDemographics, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicTransitionsModel> CREATOR = new Creator<DemographicTransitionsModel>() {
        @Override
        public DemographicTransitionsModel createFromParcel(Parcel in) {
            return new DemographicTransitionsModel(in);
        }

        @Override
        public DemographicTransitionsModel[] newArray(int size) {
            return new DemographicTransitionsModel[size];
        }
    };

    /**
     *
     * @return
     * The confirmDemographics
     */
    public DemographicTransitionModel getConfirmDemographics() {
        return confirmDemographics;
    }

    /**
     *
     * @param confirmDemographics
     * The confirm_demographics
     */
    public void setConfirmDemographics(DemographicTransitionModel confirmDemographics) {
        this.confirmDemographics = confirmDemographics;
    }
}
