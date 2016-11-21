package com.carecloud.carepaylibray.demographics.dtos.metadata.labels;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * MOdel for label.
 */

public class DemographicLabelsDTO {

    @SerializedName("demographics_review_toolbar_title")
    @Expose
    private String demographicsReviewToolbarTitle;
    @SerializedName("demographics_section")
    @Expose
    private String demographicSectionTitle;
    @SerializedName("demographics_update_insurance_toolbar_title")
    @Expose
    private String demographicsUpdateInsuranceToolbarTitle;
    @SerializedName("demographics_review_update_button")
    @Expose
    private String demographicsReviewUpdateButton;
    @SerializedName("demographics_insurance_update_button")
    @Expose
    private String demographicsInsuranceUpdateButton;
    @SerializedName("demographics_review_correct_button")
    @Expose
    private String demographicsReviewCorrectButton;
    @SerializedName("demographics_update_insurance_button")
    @Expose
    private String demographicsUpdateInsuranceButton;
    @SerializedName("demographics_review_peronsonalinfo_section")
    @Expose
    private String demographicsReviewPeronsonalinfoSection;
    @SerializedName("demographics_review_screen_title")
    @Expose
    private String demographicsReviewScreenTitle;
    @SerializedName("demographics_upadte_insurance_screen_title")
    @Expose
    private String demographicsUpdateInsuranceScreenTitle;
    @SerializedName("demographics_review_screen_subtitle")
    @Expose
    private String demographicsReviewScreenSubtitle;
    @SerializedName("demographics_healthinsurance_1_section")
    @Expose
    private String demographicsHealthinsurance1Section;
    @SerializedName("demographics_healthinsurance_2_section")
    @Expose
    private String demographicsHealthinsurance2Section;
    @SerializedName("demographics_healthinsurance_3_section")
    @Expose
    private String demographicsHealthinsurance3Section;
    @SerializedName("demographics_update_demographic_title")
    @Expose
    private String demographicsUpdateDemographicTitle;
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

    @SerializedName("demographics_allset_section")
    @Expose
    private String demographicsAllSetSection;

    @SerializedName("demographics_add_another_insurance_link")
    @Expose
    private String demographicsAddAnotherInsuranceLink;

    @SerializedName("demographics_next")
    @Expose
    private String demographicsNext;

    @SerializedName("demographics_address_header")
    @Expose
    private String demographicsAddressHeader;

    @SerializedName("demographics_address_subheader")
    @Expose
    private String demographicsAddressSubheader;

    @SerializedName("demographics_required")
    @Expose
    private String demographicsRequired;

    @SerializedName("demographics_details_header")
    @Expose
    private String demographicsDetailsHeader;

    @SerializedName("demographics_details_subheader")
    @Expose
    private String demographicsDetailsSubheader;

    @SerializedName("demographics_details_dob_hint")
    @Expose
    private String demographicsDetailsDobHint;

    @SerializedName("demographics_choose")
    @Expose
    private String demographicsChooseLabel;

    @SerializedName("demographics_details_capture_picture_caption")
    @Expose
    private String demographicsProfileCaptureCaption;

    @SerializedName("demographics_details_recapture_picture_caption")
    @Expose
    private String demographicsProfileReCaptureCaption;

    @SerializedName("demographics_details_allergies_section")
    @Expose
    private String demographicsDetailsAllergiesSection;

    @SerializedName("demographics_details_optional_hint")
    @Expose
    private String demographicsDetailsOptionalHint;

    @SerializedName("demographics_details_medications_section")
    @Expose
    private String demographicsDetailsMedicationsSection;

    @SerializedName("demographics_details_allergy")
    @Expose
    private String demographicsDetailAllergyLabel;

    @SerializedName("demographics_details_medication")
    @Expose
    private String demographicsDetailMedicationLabel;

    @SerializedName("demographics_details_allergy_add_unlisted")
    @Expose
    private String demographicsDetailsAllergyAddUnlistedLabel;

    @SerializedName("demographics_details_medication_add_unlisted")
    @Expose
    private String demographicsDetailsMedAddUnlistedLabel;

    @SerializedName("demographics_documents_header")
    @Expose
    private String demographicsDocumentsHeader;

    @SerializedName("demographics_documents_subheader")
    @Expose
    private String demographicsDocumentsSubheader;

    @SerializedName("demographics_documents_switch_insurance")
    @Expose
    private String demographicsDocumentsSwitchLabel;

    @SerializedName("demographics_documents_multiple_insurances")
    @Expose
    private String demographicsDocumentsMultiInsLabel;

    @SerializedName("demographics_documents_scan_front")
    @Expose
    private String demographicsDocumentsScanFrontLabel;

    @SerializedName("demographics_documents_scan_back")
    @Expose
    private String demographicsDocumentsScanBackLabel;

    @SerializedName("demographics_documents_rescan_front")
    @Expose
    private String demographicsDocumentsRescanFrontLabel;

    @SerializedName("demographics_documents_rescan_back")
    @Expose
    private String demographicsDocumentsRescanBackLabel;

    @SerializedName("demographics_cancel_label")
    @Expose
    private String demographicsCancelLabel;

    @SerializedName("demographics_documents_title_select_state")
    @Expose
    private String demographicsTitleSelectState;

    @SerializedName("demographics_documents_title_select_plan")
    @Expose
    private String demographicsTitleSelectPlan;

    @SerializedName("demographics_documents_title_select_provider")
    @Expose
    private String demographicsTitleSelectProvider;

    @SerializedName("demographics_documents_title_select_gender")
    @Expose
    private String demographicsTitleSelectGender;

    @SerializedName("demographics_documents_title_select_ethnicity")
    @Expose
    private String demographicsTitleSelectEthnicity;

    @SerializedName("demographics_documents_title_select_race")
    @Expose
    private String demographicsTitleSelectRace;

    @SerializedName("demographics_documents_title_select_id_type")
    @Expose
    private String demographicsTitleSelectIdType;

    @SerializedName("demographics_documents_choose_plan")
    @Expose
    private String demographicsDocumentsChoosePlanLabel;

    @SerializedName("demographics_documents_ins_type_label")
    @Expose
    private String demographicsDocumentsInsTypeLabel;

    @SerializedName("demographics_documents_title_card_type")
    @Expose
    private String demographicsTitleCardType;

    @SerializedName("demographics_allset_header")
    @Expose
    private String demographicsAllSetHeader;

    @SerializedName("demographics_allset_go_button")
    @Expose
    private String demographicsAllSetGoButton;

    @SerializedName("demographics_documents_remove") @Expose
    private String documentsRemove;

    @SerializedName("demographics_take_pic_option") @Expose
    private String demographicsTakePhotoOption;

    @SerializedName("demographics_select_gallery_option") @Expose
    private String demographicsChooseFromLibraryOption;

    @SerializedName("demographics_select_capture_option_title") @Expose
    private String demographicsCaptureOptionsTitle;

    /**
     * @return The demographicsUpdateButton
     */
    public String getDemographicsUpdateButton() {
        return demographicsUpdateButton;
    }

    /**
     * @param demographicsUpdateButton The demographics_update_button
     */
    public void setDemographicsUpdateButton(String demographicsUpdateButton) {
        this.demographicsUpdateButton = demographicsUpdateButton;
    }

    /**
     * @return The demographicsUpdateProfilePhotoLink
     */
    public String getDemographicsUpdateProfilePhotoLink() {
        return demographicsUpdateProfilePhotoLink;
    }

    /**
     * @param demographicsUpdateProfilePhotoLink The demographics_update_profile_photo_link
     */
    public void setDemographicsUpdateProfilePhotoLink(String demographicsUpdateProfilePhotoLink) {
        this.demographicsUpdateProfilePhotoLink = demographicsUpdateProfilePhotoLink;
    }

    /**
     * @return The demographicsUpdateEmailAndPasswordLink
     */
    public String getDemographicsUpdateEmailAndPasswordLink() {
        return demographicsUpdateEmailAndPasswordLink;
    }

    /**
     * @param demographicsUpdateEmailAndPasswordLink The demographics_update_email_and_password_link
     */
    public void setDemographicsUpdateEmailAndPasswordLink(String demographicsUpdateEmailAndPasswordLink) {
        this.demographicsUpdateEmailAndPasswordLink = demographicsUpdateEmailAndPasswordLink;
    }

    /**
     * @return The demographicsAddressSection
     */
    public String getDemographicsAddressSection() {
        return StringUtil.isNullOrEmpty(demographicsAddressSection) ?
                CarePayConstants.NOT_DEFINED : demographicsAddressSection;
    }

    /**
     * @param demographicsAddressSection The demographics_address_section
     */
    public void setDemographicsAddressSection(String demographicsAddressSection) {
        this.demographicsAddressSection = demographicsAddressSection;
    }

    /**
     * @return The demographicsDetailsSection
     */
    public String getDemographicsDetailsSection() {
        return StringUtil.isNullOrEmpty(demographicsDetailsSection) ?
                CarePayConstants.NOT_DEFINED : demographicsDetailsSection;
    }

    /**
     * @param demographicsDetailsSection The demographics_details_section
     */
    public void setDemographicsDetailsSection(String demographicsDetailsSection) {
        this.demographicsDetailsSection = demographicsDetailsSection;
    }

    /**
     * @return The demographicsDocumentsSection
     */
    public String getDemographicsDocumentsSection() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsSection) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsSection;
    }

    /**
     * @param demographicsDocumentsSection The demographics_documents_section
     */
    public void setDemographicsDocumentsSection(String demographicsDocumentsSection) {
        this.demographicsDocumentsSection = demographicsDocumentsSection;
    }

    /**
     * @return The demographicsAllSetSection
     */
    public String getDemographicsAllSetSection() {
        return StringUtil.isNullOrEmpty(demographicsAllSetSection) ?
                CarePayConstants.NOT_DEFINED : demographicsAllSetSection;
    }

    /**
     * @param demographicsAllSetSection The demographics_updates_section
     */
    public void setDemographicsAllSetSection(String demographicsAllSetSection) {
        this.demographicsAllSetSection = demographicsAllSetSection;
    }

    /**
     * @return The demographicsAddAnotherInsuranceLink
     */
    public String getDemographicsAddAnotherInsuranceLink() {
        return demographicsAddAnotherInsuranceLink;
    }

    /**
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

    public String getDemographicsChooseLabel() {
        return StringUtil.isNullOrEmpty(demographicsChooseLabel) ? CarePayConstants.NOT_DEFINED : demographicsChooseLabel;
    }

    public void setDemographicsChooseLabel(String demographicsChooseLabel) {
        this.demographicsChooseLabel = demographicsChooseLabel;
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

    public String getDemographicsDocumentsSwitchLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsSwitchLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsSwitchLabel;
    }

    public void setDemographicsDocumentsSwitchLabel(String demographicsDocumentsSwitchLabel) {
        this.demographicsDocumentsSwitchLabel = demographicsDocumentsSwitchLabel;
    }

    public String getDemographicsDocumentsHeader() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsHeader) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsHeader;
    }

    public void setDemographicsDocumentsHeader(String demographicsDocumentsHeader) {
        this.demographicsDocumentsHeader = demographicsDocumentsHeader;
    }

    public String getDemographicsDocumentsSubheader() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsSubheader) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsSubheader;
    }

    public void setDemographicsDocumentsSubheader(String demographicsDocumentsSubheader) {
        this.demographicsDocumentsSubheader = demographicsDocumentsSubheader;
    }

    public String getDemographicsDocumentsMultiInsLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsMultiInsLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsMultiInsLabel;
    }

    public void setDemographicsDocumentsMultiInsLabel(String demographicsDocumentsMultiInsLabel) {
        this.demographicsDocumentsMultiInsLabel = demographicsDocumentsMultiInsLabel;
    }

    public String getDemographicsDocumentsScanFrontLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsScanFrontLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsScanFrontLabel;
    }

    public void setDemographicsDocumentsScanFrontLabel(String demographicsDocumentsScanFrontLabel) {
        this.demographicsDocumentsScanFrontLabel = demographicsDocumentsScanFrontLabel;
    }

    public String getDemographicsDocumentsScanBackLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsScanBackLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsScanBackLabel;
    }

    public void setDemographicsDocumentsScanBackLabel(String demographicsDocumentsScanBackLabel) {
        this.demographicsDocumentsScanBackLabel = demographicsDocumentsScanBackLabel;
    }

    public String getDemographicsDocumentsRescanFrontLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsRescanFrontLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsRescanFrontLabel;
    }

    public void setDemographicsDocumentsRescanFrontLabel(String demographicsDocumentsRescanFrontLabel) {
        this.demographicsDocumentsRescanFrontLabel = demographicsDocumentsRescanFrontLabel;
    }

    public String getDemographicsDocumentsRescanBackLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsRescanBackLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsRescanBackLabel;
    }

    public void setDemographicsDocumentsRescanBackLabel(String demographicsDocumentsRescanBackLabel) {
        this.demographicsDocumentsRescanBackLabel = demographicsDocumentsRescanBackLabel;
    }

    public String getDemographicsCancelLabel() {
        return StringUtil.isNullOrEmpty(demographicsCancelLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsCancelLabel;
    }

    public void setDemographicsCancelLabel(String demographicsCancelLabel) {
        this.demographicsCancelLabel = demographicsCancelLabel;
    }

    public String getDemographicsTitleSelectState() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectState) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectState;
    }

    public void setDemographicsTitleSelectState(String demographicsTitleSelectState) {
        this.demographicsTitleSelectState = demographicsTitleSelectState;
    }

    public String getDemographicsTitleSelectPlan() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectPlan) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectPlan;
    }

    public void setDemographicsTitleSelectPlan(String demographicsTitleSelectPlan) {
        this.demographicsTitleSelectPlan = demographicsTitleSelectPlan;
    }

    public String getDemographicsTitleSelectProvider() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectProvider) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectProvider;
    }

    public void setDemographicsTitleSelectProvider(String demographicsTitleSelectProvider) {
        this.demographicsTitleSelectProvider = demographicsTitleSelectProvider;
    }

    public String getDemographicsTitleSelectGender() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectGender) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectGender;
    }

    public void setDemographicsTitleSelectGender(String demographicsTitleSelectGender) {
        this.demographicsTitleSelectGender = demographicsTitleSelectGender;
    }

    public String getDemographicsTitleSelectEthnicity() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectEthnicity) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectEthnicity;
    }

    public void setDemographicsTitleSelectEthnicity(String demographicsTitleSelectEthnicity) {
        this.demographicsTitleSelectEthnicity = demographicsTitleSelectEthnicity;
    }

    public String getDemographicsTitleSelectRace() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectRace) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectRace;
    }

    public void setDemographicsTitleSelectRace(String demographicsTitleSelectRace) {
        this.demographicsTitleSelectRace = demographicsTitleSelectRace;
    }

    public String getDemographicsTitleSelectIdType() {
        return StringUtil.isNullOrEmpty(demographicsTitleSelectIdType) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleSelectIdType;
    }

    public void setDemographicsTitleSelectIdType(String demographicsTitleSelectIdType) {
        this.demographicsTitleSelectIdType = demographicsTitleSelectIdType;
    }

    public String getDemographicsDocumentsChoosePlanLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsChoosePlanLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsChoosePlanLabel;
    }

    public void setDemographicsDocumentsChoosePlanLabel(String demographicsDocumentsChoosePlanLabel) {
        this.demographicsDocumentsChoosePlanLabel = demographicsDocumentsChoosePlanLabel;
    }

    public String getDemographicsDocumentsInsTypeLabel() {
        return StringUtil.isNullOrEmpty(demographicsDocumentsInsTypeLabel) ?
                CarePayConstants.NOT_DEFINED : demographicsDocumentsInsTypeLabel;
    }

    public void setDemographicsDocumentsInsTypeLabel(String demographicsDocumentsInsTypeLabel) {
        this.demographicsDocumentsInsTypeLabel = demographicsDocumentsInsTypeLabel;
    }

    public String getDemographicsTitleCardType() {
        return StringUtil.isNullOrEmpty(demographicsTitleCardType) ?
                CarePayConstants.NOT_DEFINED : demographicsTitleCardType;
    }

    public void setDemographicsTitleCardType(String demographicsTitleCardType) {
        this.demographicsTitleCardType = demographicsTitleCardType;
    }

    public String getDemographicsAllSetHeader() {
        return StringUtil.isNullOrEmpty(demographicsAllSetHeader) ?
                CarePayConstants.NOT_DEFINED : demographicsAllSetHeader;
    }

    public void setDemographicsAllSetHeader(String demographicsAllSetHeader) {
        this.demographicsAllSetHeader = demographicsAllSetHeader;
    }

    public String getDemographicsAllSetGoButton() {
        return StringUtil.isNullOrEmpty(demographicsAllSetGoButton) ?
                CarePayConstants.NOT_DEFINED : demographicsAllSetGoButton;
    }

    public void setDemographicsAllSetGoButton(String demographicsAllSetGoButton) {
        this.demographicsAllSetGoButton = demographicsAllSetGoButton;
    }

    /**
     * @return The demographicsReviewToolbarTitle
     */
    public String getDemographicsReviewToolbarTitle() {
        return StringUtil.isNullOrEmpty(demographicsReviewToolbarTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewToolbarTitle;
    }

    /**
     * @param demographicsReviewToolbarTitle The demographics_review_toolbar_title
     */
    public void setDemographicsReviewToolbarTitle(String demographicsReviewToolbarTitle) {
        this.demographicsReviewToolbarTitle = demographicsReviewToolbarTitle;
    }

    /**
     * @return The demographicsUpdateInsuranceToolbarTitle
     */
    public String getDemographicsUpdateInsuranceToolbarTitle() {
        return StringUtil.isNullOrEmpty(demographicsUpdateInsuranceToolbarTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsUpdateInsuranceToolbarTitle;
    }

    /**
     * @param demographicsUpdateInsuranceToolbarTitle The demographics_update_insurance_toolbar_title
     */
    public void setDemographicsUpdateInsuranceToolbarTitle(String demographicsUpdateInsuranceToolbarTitle) {
        this.demographicsUpdateInsuranceToolbarTitle = demographicsUpdateInsuranceToolbarTitle;
    }

    /**
     * @return The demographicsReviewUpdateButton
     */
    public String getDemographicsReviewUpdateButton() {
        return StringUtil.isNullOrEmpty(demographicsReviewUpdateButton) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewUpdateButton;
    }

    /**
     * @param demographicsReviewUpdateButton The demographics_review_update_button
     */
    public void setDemographicsReviewUpdateButton(String demographicsReviewUpdateButton) {
        this.demographicsReviewUpdateButton = demographicsReviewUpdateButton;
    }

    /**
     * @return The demographicsInsuranceUpdateButton
     */
    public String getDemographicsInsuranceUpdateButton() {
        return StringUtil.isNullOrEmpty(demographicsInsuranceUpdateButton) ?
                CarePayConstants.NOT_DEFINED : demographicsInsuranceUpdateButton;
    }

    /**
     * @param demographicsInsuranceUpdateButton The demographics_insurance_update_button
     */
    public void setDemographicsInsuranceUpdateButton(String demographicsInsuranceUpdateButton) {
        this.demographicsInsuranceUpdateButton = demographicsInsuranceUpdateButton;
    }

    /**
     * @return The demographicsReviewCorrectButton
     */
    public String getDemographicsReviewCorrectButton() {
        return StringUtil.isNullOrEmpty(demographicsReviewCorrectButton) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewCorrectButton;
    }

    /**
     * @param demographicsReviewCorrectButton The demographics_review_correct_button
     */
    public void setDemographicsReviewCorrectButton(String demographicsReviewCorrectButton) {
        this.demographicsReviewCorrectButton = demographicsReviewCorrectButton;
    }

    /**
     * @return The demographicsUpdateInsuranceButton
     */
    public String getDemographicsUpdateInsuranceButton() {
        return StringUtil.isNullOrEmpty(demographicsUpdateInsuranceButton) ?
                CarePayConstants.NOT_DEFINED : demographicsUpdateInsuranceButton;
    }

    /**
     * @param demographicsUpdateInsuranceButton The demographics_update_insurance_button
     */
    public void setDemographicsUpdateInsuranceButton(String demographicsUpdateInsuranceButton) {
        this.demographicsUpdateInsuranceButton = demographicsUpdateInsuranceButton;
    }

    /**
     * @return The demographicsReviewPeronsonalinfoSection
     */
    public String getDemographicsReviewPeronsonalinfoSection() {
        return StringUtil.isNullOrEmpty(demographicsReviewPeronsonalinfoSection) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewPeronsonalinfoSection;
    }

    /**
     * @param demographicsReviewPeronsonalinfoSection The demographics_review_peronsonalinfo_section
     */
    public void setDemographicsReviewPeronsonalinfoSection(String demographicsReviewPeronsonalinfoSection) {
        this.demographicsReviewPeronsonalinfoSection = demographicsReviewPeronsonalinfoSection;
    }

    /**
     * @return The demographicsReviewScreenTitle
     */
    public String getDemographicsReviewScreenTitle() {
        return StringUtil.isNullOrEmpty(demographicsReviewScreenTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewScreenTitle;
    }

    /**
     * @param demographicsReviewScreenTitle The demographics_review_screen_title
     */
    public void setDemographicsReviewScreenTitle(String demographicsReviewScreenTitle) {
        this.demographicsReviewScreenTitle = demographicsReviewScreenTitle;
    }

    /**
     * @return The demographicsUpadteInsuranceScreenTitle
     */
    public String getDemographicsUpdateInsuranceScreenTitle() {
        return StringUtil.isNullOrEmpty(demographicsUpdateInsuranceScreenTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsUpdateInsuranceScreenTitle;
    }

    /**
     * @param demographicsUpdateInsuranceScreenTitle The demographics_upadte_insurance_screen_title
     */
    public void setDemographicsUpdateInsuranceScreenTitle(String demographicsUpdateInsuranceScreenTitle) {
        this.demographicsUpdateInsuranceScreenTitle = demographicsUpdateInsuranceScreenTitle;
    }

    /**
     * @return The demographicsReviewScreenSubtitle
     */
    public String getDemographicsReviewScreenSubtitle() {
        return StringUtil.isNullOrEmpty(demographicsReviewScreenSubtitle) ?
                CarePayConstants.NOT_DEFINED : demographicsReviewScreenSubtitle;
    }

    /**
     * @param demographicsReviewScreenSubtitle The demographics_review_screen_subtitle
     */
    public void setDemographicsReviewScreenSubtitle(String demographicsReviewScreenSubtitle) {
        this.demographicsReviewScreenSubtitle = demographicsReviewScreenSubtitle;
    }

    /**
     * @return The demographicsHelathinsurance1Section
     */
    public String getDemographicsHealthinsurance1Section() {
        return StringUtil.isNullOrEmpty(demographicsHealthinsurance1Section) ?
                CarePayConstants.NOT_DEFINED : demographicsHealthinsurance1Section;
    }

    /**
     * @param demographicsHelathinsurance1Section The demographics_helathinsurance_1_section
     */
    public void setDemographicsHealthinsurance1Section(String demographicsHelathinsurance1Section) {
        this.demographicsHealthinsurance1Section = demographicsHealthinsurance1Section;
    }

    /**
     * @return The demographicsHelathinsurance2Section
     */
    public String getDemographicsHealthinsurance2Section() {
        return StringUtil.isNullOrEmpty(demographicsHealthinsurance2Section) ?
                CarePayConstants.NOT_DEFINED : demographicsHealthinsurance2Section;
    }

    /**
     * @param demographicsHelathinsurance2Section The demographics_helathinsurance_2_section
     */
    public void setDemographicsHealthinsurance2Section(String demographicsHelathinsurance2Section) {
        this.demographicsHealthinsurance2Section = demographicsHelathinsurance2Section;
    }

    /**
     * @return The demographicsHelathinsurance3Section
     */
    public String getDemographicsHealthinsurance3Section() {
        return StringUtil.isNullOrEmpty(demographicsHealthinsurance3Section) ?
                CarePayConstants.NOT_DEFINED : demographicsHealthinsurance3Section;
    }

    /**
     * @param demographicsHealthinsurance3Section The demographics_healthinsurance_3_section
     */
    public void setDemographicsHealthinsurance3Section(String demographicsHealthinsurance3Section) {
        this.demographicsHealthinsurance3Section = demographicsHealthinsurance3Section;
    }

    /**
     * @return The demographicsUpdateDemographicTitle
     */
    public String getDemographicsUpdateDemographicTitle() {
        return StringUtil.isNullOrEmpty(demographicsUpdateDemographicTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsUpdateDemographicTitle;
    }

    /**
     * @param demographicsUpdateDemographicTitle The demographics_update_demographic_title
     */
    public void setDemographicsUpdateDemographicTitle(String demographicsUpdateDemographicTitle) {
        this.demographicsUpdateDemographicTitle = demographicsUpdateDemographicTitle;
    }

    /**
     * @return The section title
     */
    public String getDemographicSectionTitle() {
        return StringUtil.isNullOrEmpty(demographicSectionTitle) ?
                CarePayConstants.NOT_DEFINED : demographicSectionTitle;
    }

    /**
     * @param demographicSectionTitle The section title
     */
    public void setDemographicSectionTitle(String demographicSectionTitle) {
        this.demographicSectionTitle = demographicSectionTitle;
    }

    /**
     * @return The label
     */
    public String getDocumentsRemove() {
        return StringUtil.isNullOrEmpty(documentsRemove) ?
                CarePayConstants.NOT_DEFINED : documentsRemove;

    }

    /**
     *
     * @param documentsRemove The label
     */
    public void setDocumentsRemove(String documentsRemove) {
        this.documentsRemove = documentsRemove;
    }

    /**
      * @return The label
     */
    public String getDemographicsTakePhotoOption() {
        return StringUtil.isNullOrEmpty(demographicsTakePhotoOption) ?
                CarePayConstants.NOT_DEFINED : demographicsTakePhotoOption;
    }

    /**
     * @param demographicsTakePhotoOption The new label
     */
    public void setDemographicsTakePhotoOption(String demographicsTakePhotoOption) {
        this.demographicsTakePhotoOption = demographicsTakePhotoOption;
    }

    /**
     * @return The label
     */
    public String getDemographicsChooseFromLibraryOption() {
        return StringUtil.isNullOrEmpty(demographicsChooseFromLibraryOption) ?
                CarePayConstants.NOT_DEFINED : demographicsChooseFromLibraryOption;
    }

    /**
     * @param demographicsChooseFromLibraryOption The new label
     */
    public void setDemographicsChooseFromLibraryOption(String demographicsChooseFromLibraryOption) {
        this.demographicsChooseFromLibraryOption = demographicsChooseFromLibraryOption;
    }

    /**
     * @return The label
     */
    public String getDemographicsCaptureOptionsTitle() {
        return StringUtil.isNullOrEmpty(demographicsCaptureOptionsTitle) ?
                CarePayConstants.NOT_DEFINED : demographicsCaptureOptionsTitle;
    }

    /**
     * @param demographicsCaptureOptionsTitle The label
     */
    public void setDemographicsCaptureOptionsTitle(String demographicsCaptureOptionsTitle) {
        this.demographicsCaptureOptionsTitle = demographicsCaptureOptionsTitle;
    }
}