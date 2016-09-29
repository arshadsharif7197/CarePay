package com.carecloud.carepaylibray.demographics.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicLabelsModel implements Parcelable {
    @SerializedName("demographics_update_button")
    @Expose
    private String demographicsUpdateButton;
    @SerializedName("demographics_update_profile_photo_link")
    @Expose
    private String demographicsUpdateProfilePhotoLink;
    @SerializedName("demographics_update_email_and_password_link")
    @Expose
    private String demographicsUpdateEmailAndPasswordLink;
    @SerializedName("demographics_address_section")
    @Expose
    private String demographicsAddressSection;
    @SerializedName("demographics_details_section")
    @Expose
    private String demographicsDetailsSection;
    @SerializedName("demographics_documents_section")
    @Expose
    private String demographicsDocumentsSection;
    @SerializedName("demographics_updates_section")
    @Expose
    private String demographicsUpdatesSection;
    @SerializedName("demographics_add_another_insurance_link")
    @Expose
    private String demographicsAddAnotherInsuranceLink;

    public DemographicLabelsModel() {
    }

    protected DemographicLabelsModel(Parcel in) {
        demographicsUpdateButton = in.readString();
        demographicsUpdateProfilePhotoLink = in.readString();
        demographicsUpdateEmailAndPasswordLink = in.readString();
        demographicsAddressSection = in.readString();
        demographicsDetailsSection = in.readString();
        demographicsDocumentsSection = in.readString();
        demographicsUpdatesSection = in.readString();
        demographicsAddAnotherInsuranceLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(demographicsUpdateButton);
        dest.writeString(demographicsUpdateProfilePhotoLink);
        dest.writeString(demographicsUpdateEmailAndPasswordLink);
        dest.writeString(demographicsAddressSection);
        dest.writeString(demographicsDetailsSection);
        dest.writeString(demographicsDocumentsSection);
        dest.writeString(demographicsUpdatesSection);
        dest.writeString(demographicsAddAnotherInsuranceLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicLabelsModel> CREATOR = new Creator<DemographicLabelsModel>() {
        @Override
        public DemographicLabelsModel createFromParcel(Parcel in) {
            return new DemographicLabelsModel(in);
        }

        @Override
        public DemographicLabelsModel[] newArray(int size) {
            return new DemographicLabelsModel[size];
        }
    };

    /**
     *
     * @return
     * The demographicsUpdateButton
     */
    public String getDemographicsUpdateButton() {
        return demographicsUpdateButton;
    }

    /**
     *
     * @param demographicsUpdateButton
     * The demographics_update_button
     */
    public void setDemographicsUpdateButton(String demographicsUpdateButton) {
        this.demographicsUpdateButton = demographicsUpdateButton;
    }

    /**
     *
     * @return
     * The demographicsUpdateProfilePhotoLink
     */
    public String getDemographicsUpdateProfilePhotoLink() {
        return demographicsUpdateProfilePhotoLink;
    }

    /**
     *
     * @param demographicsUpdateProfilePhotoLink
     * The demographics_update_profile_photo_link
     */
    public void setDemographicsUpdateProfilePhotoLink(String demographicsUpdateProfilePhotoLink) {
        this.demographicsUpdateProfilePhotoLink = demographicsUpdateProfilePhotoLink;
    }

    /**
     *
     * @return
     * The demographicsUpdateEmailAndPasswordLink
     */
    public String getDemographicsUpdateEmailAndPasswordLink() {
        return demographicsUpdateEmailAndPasswordLink;
    }

    /**
     *
     * @param demographicsUpdateEmailAndPasswordLink
     * The demographics_update_email_and_password_link
     */
    public void setDemographicsUpdateEmailAndPasswordLink(String demographicsUpdateEmailAndPasswordLink) {
        this.demographicsUpdateEmailAndPasswordLink = demographicsUpdateEmailAndPasswordLink;
    }

    /**
     *
     * @return
     * The demographicsAddressSection
     */
    public String getDemographicsAddressSection() {
        return demographicsAddressSection;
    }

    /**
     *
     * @param demographicsAddressSection
     * The demographics_address_section
     */
    public void setDemographicsAddressSection(String demographicsAddressSection) {
        this.demographicsAddressSection = demographicsAddressSection;
    }

    /**
     *
     * @return
     * The demographicsDetailsSection
     */
    public String getDemographicsDetailsSection() {
        return demographicsDetailsSection;
    }

    /**
     *
     * @param demographicsDetailsSection
     * The demographics_details_section
     */
    public void setDemographicsDetailsSection(String demographicsDetailsSection) {
        this.demographicsDetailsSection = demographicsDetailsSection;
    }

    /**
     *
     * @return
     * The demographicsDocumentsSection
     */
    public String getDemographicsDocumentsSection() {
        return demographicsDocumentsSection;
    }

    /**
     *
     * @param demographicsDocumentsSection
     * The demographics_documents_section
     */
    public void setDemographicsDocumentsSection(String demographicsDocumentsSection) {
        this.demographicsDocumentsSection = demographicsDocumentsSection;
    }

    /**
     *
     * @return
     * The demographicsUpdatesSection
     */
    public String getDemographicsUpdatesSection() {
        return demographicsUpdatesSection;
    }

    /**
     *
     * @param demographicsUpdatesSection
     * The demographics_updates_section
     */
    public void setDemographicsUpdatesSection(String demographicsUpdatesSection) {
        this.demographicsUpdatesSection = demographicsUpdatesSection;
    }

    /**
     *
     * @return
     * The demographicsAddAnotherInsuranceLink
     */
    public String getDemographicsAddAnotherInsuranceLink() {
        return demographicsAddAnotherInsuranceLink;
    }

    /**
     *
     * @param demographicsAddAnotherInsuranceLink
     * The demographics_add_another_insurance_link
     */
    public void setDemographicsAddAnotherInsuranceLink(String demographicsAddAnotherInsuranceLink) {
        this.demographicsAddAnotherInsuranceLink = demographicsAddAnotherInsuranceLink;
    }
}
