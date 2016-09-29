package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */
public class DemographicPayloadResponseModel implements Parcelable {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoModel demographics;

    public DemographicPayloadResponseModel() {

    }

    protected DemographicPayloadResponseModel(Parcel in) {
        demographics = in.readParcelable(DemographicPayloadInfoModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(demographics, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadResponseModel> CREATOR = new Creator<DemographicPayloadResponseModel>() {
        @Override
        public DemographicPayloadResponseModel createFromParcel(Parcel in) {
            return new DemographicPayloadResponseModel(in);
        }

        @Override
        public DemographicPayloadResponseModel[] newArray(int size) {
            return new DemographicPayloadResponseModel[size];
        }
    };

    public void setDemographics(DemographicPayloadInfoModel demographics) {
        this.demographics = demographics;
    }

    public DemographicPayloadInfoModel getDemographics() {
        return demographics;
    }
}
