package com.carecloud.carepaylibray.demographics.dtos.metadata.labels;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * MOdel for label.
 */
public class DemographicLabelsDTO {
    @SerializedName("demographics_update_button") @Expose
    private String demographicsUpdateButton;

    @SerializedName("demographics_update_profile_photo_link") @Expose
    private String demographicsUpdateProfilePhotoLink;

    @SerializedName("demographics_update_email_and_password_link") @Expose
    private String demographicsUpdateEmailAndPasswordLink;

    @SerializedName("demographics_address_section") @Expose
    private String demographicsAddressSection;


    @SerializedName("demographics_details_section") @Expose
    private String demographicsDetailsSection;

    @SerializedName("demographics_documents_section") @Expose
    private String demographicsDocumentsSection;

    @SerializedName("demographics_updates_section") @Expose
    private String demographicsUpdatesSection;

    @SerializedName("demographics_add_another_insurance_link") @Expose
    private String demographicsAddAnotherInsuranceLink;

//    @SerializedName("demographics_next") @Expose
    private String demographicsNext;

//    @SerializedName("demographics_address_header") @Expose
    private String demographicsAddressHeader;

//    @SerializedName("demographics_address_subheader") @Expose
    private String demographicsAddressSubheader;

//    @SerializedName("demographics_required") @Expose
    private String demographicsRequired;

//    @SerializedName("demographics_details_header") @Expose
    private String demographicsDetailsHeader;

    //    @SerializedName("demographics_details_subheader") @Expose
    private String demographicsDetailsSubheader;

//    @SerializedName("demographics_details_dob_hint") @Expose
    private String demographicsDetailsDobHint;

//    @SerializedName("demographics_choose") @Expose
    private String choose;

//    @SerializedName("demographics_details_capture_picture_caption") @Expose
    private String demographicsProfileCaptureCaption;

    //    @SerializedName("demographics_details_capture_picture_caption") @Expose
    private String demographicsProfileReCaptureCaption;

//    @SerializedName("demographics_details_allergies_section") @Expose
    private String demographicsDetailsAllergiesSection;

    //    @SerializedName("demographics_details_optional_hint") @Expose
    private String demographicsDetailsOptionalHint;

    //    @SerializedName("demographics_details_medications_section") @Expose
    private String demographicsDetailsMedicationsSection;

//    @SerializedName("demographics_details_allergy") @Expose
    private String demographicsDetailAllergyLabel;

    //    @SerializedName("demographics_details_medication") @Expose
    private String demographicsDetailMedicationLabel;

    //    @SerializedName("demographics_details_allergy_add_unlisted") @Expose
    private String demographicsDetailsAllergyAddUnlistedLabel;

    //    @SerializedName("demographics_details_medication_add_unlisted") @Expose
    private String demographicsDetailsMedAddUnlistedLabel;

    /**
     *
     * @return The demographicsUpdateButton
     */
    public String getDemographicsUpdateButton() {
        return demographicsUpdateButton;
    }

    /**
     *
     * @param demographicsUpdateButton The demographics_update_button
     */
    public void setDemographicsUpdateButton(String demographicsUpdateButton) {
        this.demographicsUpdateButton = demographicsUpdateButton;
    }

    /**
     *
     * @return The demographicsUpdateProfilePhotoLink
     */
    public String getDemographicsUpdateProfilePhotoLink() {
        return demographicsUpdateProfilePhotoLink;
    }

    /**
     *
     * @param demographicsUpdateProfilePhotoLink The demographics_update_profile_photo_link
     */
    public void setDemographicsUpdateProfilePhotoLink(String demographicsUpdateProfilePhotoLink) {
        this.demographicsUpdateProfilePhotoLink = demographicsUpdateProfilePhotoLink;
    }

    /**
     *
     * @return The demographicsUpdateEmailAndPasswordLink
     */
    public String getDemographicsUpdateEmailAndPasswordLink() {
        return demographicsUpdateEmailAndPasswordLink;
    }

    /**
     *
     * @param demographicsUpdateEmailAndPasswordLink The demographics_update_email_and_password_link
     */
    public void setDemographicsUpdateEmailAndPasswordLink(String demographicsUpdateEmailAndPasswordLink) {
        this.demographicsUpdateEmailAndPasswordLink = demographicsUpdateEmailAndPasswordLink;
    }

    /**
     *
     * @return The demographicsAddressSection
     */
    public String getDemographicsAddressSection() {
        return demographicsAddressSection;
    }

    /**
     *
     * @param demographicsAddressSection The demographics_address_section
     */
    public void setDemographicsAddressSection(String demographicsAddressSection) {
        this.demographicsAddressSection = demographicsAddressSection;
    }

    /**
     *
     * @return The demographicsDetailsSection
     */
    public String getDemographicsDetailsSection() {
        return demographicsDetailsSection;
    }

    /**
     *
     * @param demographicsDetailsSection The demographics_details_section
     */
    public void setDemographicsDetailsSection(String demographicsDetailsSection) {
        this.demographicsDetailsSection = demographicsDetailsSection;
    }

    /**
     *
     * @return The demographicsDocumentsSection
     */
    public String getDemographicsDocumentsSection() {
        return demographicsDocumentsSection;
    }

    /**
     *
     * @param demographicsDocumentsSection The demographics_documents_section
     */
    public void setDemographicsDocumentsSection(String demographicsDocumentsSection) {
        this.demographicsDocumentsSection = demographicsDocumentsSection;
    }

    /**
     *
     * @return The demographicsUpdatesSection
     */
    public String getDemographicsUpdatesSection() {
        return demographicsUpdatesSection;
    }

    /**
     *
     * @param demographicsUpdatesSection The demographics_updates_section
     */
    public void setDemographicsUpdatesSection(String demographicsUpdatesSection) {
        this.demographicsUpdatesSection = demographicsUpdatesSection;
    }

    /**
     *
     * @return The demographicsAddAnotherInsuranceLink
     */
    public String getDemographicsAddAnotherInsuranceLink() {
        return demographicsAddAnotherInsuranceLink;
    }

    /**
     *
     * @param demographicsAddAnotherInsuranceLink The demographics_add_another_insurance_link
     */
    public void setDemographicsAddAnotherInsuranceLink(String demographicsAddAnotherInsuranceLink) {
        this.demographicsAddAnotherInsuranceLink = demographicsAddAnotherInsuranceLink;
    }

    public String getDemographicsNext() {
        return StringUtil.isNullOrEmpty(demographicsNext) ? CarePayConstants.NOT_DEFINED : demographicsNext;
    }

    public void setDemographicsNext(String demographicsNext) {
        this.demographicsNext = demographicsNext;
    }

    public String getDemographicsAddressHeader() {
        return StringUtil.isNullOrEmpty(demographicsAddressHeader) ?
                CarePayConstants.NOT_DEFINED : demographicsAddressHeader;
    }

    public void setDemographicsAddressHeader(String demographicsAddressHeader) {
        this.demographicsAddressHeader = demographicsAddressHeader;
    }

    public String getDemographicsAddressSubheader() {
        return StringUtil.isNullOrEmpty(demographicsAddressSubheader) ?
                CarePayConstants.NOT_DEFINED : demographicsAddressSubheader;
    }

    public void setDemographicsAddressSubheader(String demographicsAddressSubheader) {
        this.demographicsAddressSubheader = demographicsAddressSubheader;
    }

    public String getDemographicsRequired() {
        return StringUtil.isNullOrEmpty(demographicsRequired)
                ? CarePayConstants.NOT_DEFINED : demographicsRequired;
    }

    public void setDemographicsRequired(String demographicsRequired) {
        this.demographicsRequired = demographicsRequired;
    }

    public String getDemographicsDetailsHeader() {
        return StringUtil.isNullOrEmpty(demographicsDetailsHeader) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsHeader;
    }

    public void setDemographicsDetailsHeader(String demographicsDetailsHeader) {
        this.demographicsDetailsHeader = demographicsDetailsHeader;
    }

    public String getDemographicsDetailsSubheader() {
        return StringUtil.isNullOrEmpty(demographicsDetailsSubheader) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsSubheader;
    }

    public void setDemographicsDetailsSubheader(String demographicsDetailsSubheader) {
        this.demographicsDetailsSubheader = demographicsDetailsSubheader;
    }

    public String getDemographicsDetailsDobHint() {
        return StringUtil.isNullOrEmpty(demographicsDetailsDobHint) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsDobHint;
    }

    public void setDemographicsDetailsDobHint(String demographicsDetailsDobHint) {
        this.demographicsDetailsDobHint = demographicsDetailsDobHint;
    }

    public String getChoose() {
        return StringUtil.isNullOrEmpty(choose) ? CarePayConstants.NOT_DEFINED : choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }

    public String getDemographicsProfileCaptureCaption() {
        return StringUtil.isNullOrEmpty(demographicsProfileCaptureCaption) ?
                CarePayConstants.NOT_DEFINED : demographicsProfileCaptureCaption;
    }

    public void setDemographicsProfileCaptureCaption(String demographicsProfileCaptureCaption) {
        this.demographicsProfileCaptureCaption = demographicsProfileCaptureCaption;
    }

    public String getDemographicsProfileReCaptureCaption() {
        return StringUtil.isNullOrEmpty(demographicsProfileReCaptureCaption) ?
                CarePayConstants.NOT_DEFINED : demographicsProfileReCaptureCaption;
    }

    public void setDemographicsProfileReCaptureCaption(String demographicsProfileReCaptureCaption) {
        this.demographicsProfileReCaptureCaption = demographicsProfileReCaptureCaption;
    }

    public String getDemographicsDetailsAllergiesSection() {
        return StringUtil.isNullOrEmpty(demographicsDetailsAllergiesSection) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsAllergiesSection;
    }

    public void setDemographicsDetailsAllergiesSection(String demographicsDetailsAllergiesSection) {
        this.demographicsDetailsAllergiesSection = demographicsDetailsAllergiesSection;
    }

    public String getDemographicsDetailsOptionalHint() {
        return StringUtil.isNullOrEmpty(demographicsDetailsOptionalHint) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsOptionalHint;
    }

    public void setDemographicsDetailsOptionalHint(String demographicsDetailsOptionalHint) {
        this.demographicsDetailsOptionalHint = demographicsDetailsOptionalHint;
    }

    public String getDemographicsDetailsMedicationsSection() {
        return StringUtil.isNullOrEmpty(demographicsDetailsMedicationsSection) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsMedicationsSection;
    }

    public void setDemographicsDetailsMedicationsSection(String demographicsDetailsMedicationsSection) {
        this.demographicsDetailsMedicationsSection = demographicsDetailsMedicationsSection;
    }

    public String getDemographicsDetailAllergyLabel() {
        return StringUtil.isNullOrEmpty(demographicsDetailAllergyLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailAllergyLabel;
    }

    public void setDemographicsDetailAllergyLabel(String demographicsDetailAllergyLabel) {
        this.demographicsDetailAllergyLabel = demographicsDetailAllergyLabel;
    }

    public String getDemographicsDetailMedicationLabel() {
        return StringUtil.isNullOrEmpty(demographicsDetailMedicationLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailMedicationLabel;
    }

    public void setDemographicsDetailMedicationLabel(String demographicsDetailMedicationLabel) {
        this.demographicsDetailMedicationLabel = demographicsDetailMedicationLabel;
    }

    public String getDemographicsDetailsAllergyAddUnlistedLabel() {
        return StringUtil.isNullOrEmpty(demographicsDetailsAllergyAddUnlistedLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsAllergyAddUnlistedLabel;
    }

    public void setDemographicsDetailsAllergyAddUnlistedLabel(String demographicsDetailsAllergyAddUnlistedLabel) {
        this.demographicsDetailsAllergyAddUnlistedLabel = demographicsDetailsAllergyAddUnlistedLabel;
    }

    public String getDemographicsDetailsMedAddUnlistedLabel() {
        return StringUtil.isNullOrEmpty(demographicsDetailsMedAddUnlistedLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsMedAddUnlistedLabel;
    }

    public void setDemographicsDetailsMedAddUnlistedLabel(String demographicsDetailsMedAddUnlistedLabel) {
        this.demographicsDetailsMedAddUnlistedLabel = demographicsDetailsMedAddUnlistedLabel;
    }
}