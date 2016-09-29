package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class DemographicTransitionModel extends BaseTransitionsModel implements Parcelable {

    public DemographicTransitionModel() {

    }

    protected DemographicTransitionModel(Parcel in) {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicTransitionModel> CREATOR = new Creator<DemographicTransitionModel>() {
        @Override
        public DemographicTransitionModel createFromParcel(Parcel in) {
            return new DemographicTransitionModel(in);
        }

        @Override
        public DemographicTransitionModel[] newArray(int size) {
            return new DemographicTransitionModel[size];
        }
    };

    public DemographicTransitionsDataObjectModel getData(){
        return super.getData(DemographicTransitionsDataObjectModel.class);
    }
}
