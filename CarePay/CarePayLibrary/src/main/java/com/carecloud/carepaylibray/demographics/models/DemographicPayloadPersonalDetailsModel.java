package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;

import com.carecloud.carepaylibray.base.models.BasePersonModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadPersonalDetailsModel extends BasePersonModel {

    @SerializedName("primary_race")
    @Expose
    private String primaryRace;
    @SerializedName("secondary_race")
    @Expose
    private String secondaryRace;
    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;
    @SerializedName("preferred_language")
    @Expose
    private String preferredLanguage;

    public DemographicPayloadPersonalDetailsModel() {
    }

    protected DemographicPayloadPersonalDetailsModel(Parcel in) {
        super(in);
        primaryRace = in.readString();
        secondaryRace = in.readString();
        ethnicity = in.readString();
        preferredLanguage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(primaryRace);
        dest.writeString(secondaryRace);
        dest.writeString(ethnicity);
        dest.writeString(preferredLanguage);
    }

    public static final Creator<DemographicPayloadPersonalDetailsModel> CREATOR
            = new Creator<DemographicPayloadPersonalDetailsModel>() {
        @Override
        public DemographicPayloadPersonalDetailsModel createFromParcel(Parcel in) {
            return new DemographicPayloadPersonalDetailsModel(in);
        }

        @Override
        public DemographicPayloadPersonalDetailsModel[] newArray(int size) {
            return new DemographicPayloadPersonalDetailsModel[size];
        }
    };

    /**
     *
     * @return
     * The primaryRace
     */
    public String getPrimaryRace() {
        return primaryRace;
    }

    /**
     *
     * @param primaryRace
     * The primary_race
     */
    public void setPrimaryRace(String primaryRace) {
        this.primaryRace = primaryRace;
    }

    /**
     *
     * @return
     * The secondaryRace
     */
    public String getSecondaryRace() {
        return secondaryRace;
    }

    /**
     *
     * @param secondaryRace
     * The secondary_race
     */
    public void setSecondaryRace(String secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    /**
     *
     * @return
     * The ethnicity
     */
    public String getEthnicity() {
        return ethnicity;
    }

    /**
     *
     * @param ethnicity
     * The ethnicity
     */
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    /**
     *
     * @return
     * The preferredLanguage
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     *
     * @param preferredLanguage
     * The preferred_language
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
