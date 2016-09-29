package com.carecloud.carepaylibray.base.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class BaseLinkModel implements Parcelable {
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("url")
    @Expose
    private String url;

    public BaseLinkModel() {

    }

    protected BaseLinkModel(Parcel in) {
        action = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseLinkModel> CREATOR = new Creator<BaseLinkModel>() {
        @Override
        public BaseLinkModel createFromParcel(Parcel in) {
            return new BaseLinkModel(in);
        }

        @Override
        public BaseLinkModel[] newArray(int size) {
            return new BaseLinkModel[size];
        }
    };

    /**
     *
     * @return
     * The action
     */
    public String getAction() {
        return action;
    }

    /**
     *
     * @param action
     * The action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
