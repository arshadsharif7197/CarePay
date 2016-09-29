package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInfoMetaDataModel implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("updated_dt")
    @Expose
    private String updatedDt;

    @SerializedName("created_dt")
    @Expose
    private String createdDt;

    public DemographicPayloadInfoMetaDataModel() {

    }

    protected DemographicPayloadInfoMetaDataModel(Parcel in) {
        userId = in.readString();
        username = in.readString();
        updatedDt = in.readString();
        createdDt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(updatedDt);
        dest.writeString(createdDt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadInfoMetaDataModel> CREATOR = new Creator<DemographicPayloadInfoMetaDataModel>() {
        @Override
        public DemographicPayloadInfoMetaDataModel createFromParcel(Parcel in) {
            return new DemographicPayloadInfoMetaDataModel(in);
        }

        @Override
        public DemographicPayloadInfoMetaDataModel[] newArray(int size) {
            return new DemographicPayloadInfoMetaDataModel[size];
        }
    };

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The updatedDt
     */
    public String getUpdatedDt() {
        return updatedDt;
    }

    /**
     *
     * @param updatedDt
     * The updated_dt
     */
    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }
}
