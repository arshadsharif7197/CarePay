package com.carecloud.carepaylibray.base.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class BaseTransitionsActionAllowedOptionsModel implements Parcelable {
    @SerializedName("op")
    @Expose
    private String option;
    @SerializedName("path")
    @Expose
    private String path;

    public BaseTransitionsActionAllowedOptionsModel() {

    }

    protected BaseTransitionsActionAllowedOptionsModel(Parcel in) {
        option = in.readString();
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(option);
        dest.writeString(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseTransitionsActionAllowedOptionsModel> CREATOR = new Creator<BaseTransitionsActionAllowedOptionsModel>() {
        @Override
        public BaseTransitionsActionAllowedOptionsModel createFromParcel(Parcel in) {
            return new BaseTransitionsActionAllowedOptionsModel(in);
        }

        @Override
        public BaseTransitionsActionAllowedOptionsModel[] newArray(int size) {
            return new BaseTransitionsActionAllowedOptionsModel[size];
        }
    };

    /**
     *
     * @return
     * The option
     */
    public String getOption() {
        return option;
    }

    /**
     *
     * @param option
     * The option
     */
    public void setOption(String option) {
        this.option = option;
    }

    /**
     *
     * @return
     * The path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     * The path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
