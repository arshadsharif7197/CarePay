package com.carecloud.carepaylibray.base.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/22/2016.
 */
public class BaseFieldValidationModel implements Parcelable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;

    public BaseFieldValidationModel() {

    }

    protected BaseFieldValidationModel(Parcel in) {
        type = in.readString();
        value = in.readString();
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(value);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseFieldValidationModel> CREATOR = new Creator<BaseFieldValidationModel>() {
        @Override
        public BaseFieldValidationModel createFromParcel(Parcel in) {
            return new BaseFieldValidationModel(in);
        }

        @Override
        public BaseFieldValidationModel[] newArray(int size) {
            return new BaseFieldValidationModel[size];
        }
    };

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The value
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return
     * The errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     *
     * @param errorMessage
     * The error_message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
