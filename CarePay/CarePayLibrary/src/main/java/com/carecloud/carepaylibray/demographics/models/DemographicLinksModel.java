package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.carecloud.carepaylibray.base.models.BaseLinkModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicLinksModel implements Parcelable {
    @SerializedName("self")
    @Expose
    private BaseLinkModel self;
    @SerializedName("demographics")
    @Expose
    private BaseLinkModel demographics;

    public DemographicLinksModel() {

    }

    protected DemographicLinksModel(Parcel in) {
        self = in.readParcelable(BaseLinkModel.class.getClassLoader());
        demographics = in.readParcelable(BaseLinkModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(self, flags);
        dest.writeParcelable(demographics, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicLinksModel> CREATOR = new Creator<DemographicLinksModel>() {
        @Override
        public DemographicLinksModel createFromParcel(Parcel in) {
            return new DemographicLinksModel(in);
        }

        @Override
        public DemographicLinksModel[] newArray(int size) {
            return new DemographicLinksModel[size];
        }
    };

    /**
     *
     * @return
     * The self
     */
    public BaseLinkModel getSelf() {
        return self;
    }

    /**
     *
     * @param self
     * The self
     */
    public void setSelf(BaseLinkModel self) {
        this.self = self;
    }

    /**
     *
     * @return
     * The demographics
     */
    public BaseLinkModel getDemographics() {
        return demographics;
    }

    /**
     *
     * @param demographics
     * The demographics
     */
    public void setDemographics(BaseLinkModel demographics) {
        this.demographics = demographics;
    }

}
