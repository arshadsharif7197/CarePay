package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicMetadataModel implements Parcelable {
    @SerializedName("labels")
    @Expose
    private DemographicLabelsModel labels;

    @SerializedName("links")
    @Expose
    private DemographicLinksModel links;

    @SerializedName("transitions")
    @Expose
    private DemographicTransitionsModel transitions;


    public DemographicMetadataModel() {

    }

    protected DemographicMetadataModel(Parcel in) {
        labels = in.readParcelable(DemographicLabelsModel.class.getClassLoader());
        links = in.readParcelable(DemographicLinksModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(labels, flags);
        dest.writeParcelable(links, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicMetadataModel> CREATOR = new Creator<DemographicMetadataModel>() {
        @Override
        public DemographicMetadataModel createFromParcel(Parcel in) {
            return new DemographicMetadataModel(in);
        }

        @Override
        public DemographicMetadataModel[] newArray(int size) {
            return new DemographicMetadataModel[size];
        }
    };

    /**
     *
     * @return
     * The labels
     */
    public DemographicLabelsModel getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     * The labels
     */
    public void setLabels(DemographicLabelsModel labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     * The links
     */
    public DemographicLinksModel getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(DemographicLinksModel links) {
        this.links = links;
    }

    /**
     *
     * @return
     * The transitions
     */
    public DemographicTransitionsModel getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(DemographicTransitionsModel transitions) {
        this.transitions = transitions;
    }
}
