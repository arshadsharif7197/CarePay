package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicModel implements Parcelable {

    @SerializedName("metadata")
    @Expose
    private DemographicMetadataModel metadata;

    @SerializedName("payload")
    @Expose
    private DemographicPayloadResponseModel payload;

    @SerializedName("state")
    @Expose
    private String state;

    public DemographicModel() {

    }

    protected DemographicModel(Parcel in) {
        metadata = in.readParcelable(DemographicMetadataModel.class.getClassLoader());
        payload = in.readParcelable(DemographicPayloadResponseModel.class.getClassLoader());
        state = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(metadata, flags);
        dest.writeParcelable(payload, flags);
        dest.writeString(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicModel> CREATOR = new Creator<DemographicModel>() {
        @Override
        public DemographicModel createFromParcel(Parcel in) {
            return new DemographicModel(in);
        }

        @Override
        public DemographicModel[] newArray(int size) {
            return new DemographicModel[size];
        }
    };

    /**
     * @return The metadata
     */
    public DemographicMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(DemographicMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public DemographicPayloadResponseModel getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(DemographicPayloadResponseModel payload) {
        this.payload = payload;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }
}
