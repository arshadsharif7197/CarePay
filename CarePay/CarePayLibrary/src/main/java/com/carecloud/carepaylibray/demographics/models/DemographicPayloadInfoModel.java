package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInfoModel implements Parcelable {

    @SerializedName("metadata")
    @Expose
    private DemographicPayloadInfoMetaDataModel metadata;
    @SerializedName("payload")
    @Expose
    private DemographicPayloadInfoPayloadModel payload;

    public DemographicPayloadInfoModel() {

    }

    protected DemographicPayloadInfoModel(Parcel in) {
        metadata = in.readParcelable(DemographicPayloadInfoMetaDataModel.class.getClassLoader());
        payload = in.readParcelable(DemographicPayloadInfoPayloadModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(metadata, flags);
        dest.writeParcelable(payload, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadInfoModel> CREATOR = new Creator<DemographicPayloadInfoModel>() {
        @Override
        public DemographicPayloadInfoModel createFromParcel(Parcel in) {
            return new DemographicPayloadInfoModel(in);
        }

        @Override
        public DemographicPayloadInfoModel[] newArray(int size) {
            return new DemographicPayloadInfoModel[size];
        }
    };

    /**
     *
     * @return
     * The metadata
     */
    public DemographicPayloadInfoMetaDataModel getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(DemographicPayloadInfoMetaDataModel metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public DemographicPayloadInfoPayloadModel getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(DemographicPayloadInfoPayloadModel payload) {
        this.payload = payload;
    }
}
