package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.carecloud.carepaylibray.base.models.BaseTransitionsPropertyModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/22/2016.
 */
public class DemographicTransitionsPropertiesModel implements Parcelable {

    @SerializedName("phone")
    @Expose
    private BaseTransitionsPropertyModel phone;

    public DemographicTransitionsPropertiesModel() {

    }

    protected DemographicTransitionsPropertiesModel(Parcel in) {
        phone = in.readParcelable(BaseTransitionsPropertyModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(phone, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicTransitionsPropertiesModel> CREATOR = new Creator<DemographicTransitionsPropertiesModel>() {
        @Override
        public DemographicTransitionsPropertiesModel createFromParcel(Parcel in) {
            return new DemographicTransitionsPropertiesModel(in);
        }

        @Override
        public DemographicTransitionsPropertiesModel[] newArray(int size) {
            return new DemographicTransitionsPropertiesModel[size];
        }
    };

    public BaseTransitionsPropertyModel getPhone() {
        return phone;
    }

    public void setPhone(BaseTransitionsPropertyModel phone) {
        this.phone = phone;
    }
}
