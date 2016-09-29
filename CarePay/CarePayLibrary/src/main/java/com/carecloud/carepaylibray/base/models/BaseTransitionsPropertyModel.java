package com.carecloud.carepaylibray.base.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class BaseTransitionsPropertyModel implements Parcelable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<BaseFieldValidationModel> validations = new ArrayList<BaseFieldValidationModel>();

    public BaseTransitionsPropertyModel() {

    }

    protected BaseTransitionsPropertyModel(android.os.Parcel in) {
        type = in.readString();
        label = in.readString();
        validations = in.createTypedArrayList(BaseFieldValidationModel.CREATOR);
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(label);
        dest.writeTypedList(validations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseTransitionsPropertyModel> CREATOR = new Creator<BaseTransitionsPropertyModel>() {
        @Override
        public BaseTransitionsPropertyModel createFromParcel(android.os.Parcel in) {
            return new BaseTransitionsPropertyModel(in);
        }

        @Override
        public BaseTransitionsPropertyModel[] newArray(int size) {
            return new BaseTransitionsPropertyModel[size];
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
     * The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The validations
     */
    public List<BaseFieldValidationModel> getValidations() {
        return validations;
    }

    /**
     *
     * @param validations
     * The validations
     */
    public void setValidations(List<BaseFieldValidationModel> validations) {
        this.validations = validations;
    }
}
