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
    @SerializedName("demographics_missing_information")
    @Expose
    private String demographicsMissingInformation;
    @SerializedName("demographics_healthinsurance_2_section")
    @Expose
    private String demographicsHealthinsurance2Section;
    @SerializedName("demographics_healthinsurance_3_section")
    @Expose
    private String demographicsHealthinsurance3Section;
    @SerializedName("demographics_update_demographic_title")
    @Expose
    private String demographicsUpdateDemographicTitle;
    @SerializedName("demographics_identity_text")
    @Expose
    private String demographicsIdentityText;

    @SerializedName("demographics_update_button")
    @Expose
    private String demographicsUpdateButton;

    @SerializedName("demographics_update_profile_photo_link")
    @Expose
    private String demographicsUpdateProfilePhotoLink;

    @SerializedName("demographics_drivers_license_add_state_label")
    @Expose
    private String demographicsDriversLicenseAddStateLabel;

    @SerializedName("demographics_update_email_and_password_link")
    @Expose
    private String demographicsUpdateEmailAndPasswordLink;

    @SerializedName("demographics_drivers_license_number")
    @Expose
    private String demographicsDriversLicenseNumber;

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

    @SerializedName("demographics_health_insurance")
    @Expose
    private String demographicsHealthInsurance;

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

    @SerializedName("demographics_documents_remove")
    @Expose
    private String documentsRemove;

    @SerializedName("demographics_take_pic_option")
    @Expose
    private String demographicsTakePhotoOption;

    @SerializedName("demographics_select_gallery_option")
    @Expose
    private String demographicsChooseFromLibraryOption;

    @SerializedName("demographics_select_capture_option_title")
    @Expose
    private String demographicsCaptureOptionsTitle;

    @SerializedName("demographics_insurance_scan_insurance_card")
    @Expose
    private String demographicsInsuranceScanInsuranceCard;

    @SerializedName("demographics_insurance_scan_msg")
    @Expose
    private String demographicsInsuranceScanMsg;

    @SerializedName("demographics_insurance_scan")
    @Expose
    private String demographicsInsuranceScan;

    @SerializedName("demographics_insurance_save")
    @Expose
    private String demographicsInsuranceSave;

    @SerializedName("demographics_insurance_clear")
    @Expose
    private String demographicsInsuranceClear;

    @SerializedName("demographics_insurance_setup")
    @Expose
    private String demographicsInsuranceSetup;

    @SerializedName("demographics_insurance_setup_health_insurance")
    @Expose
    private String demographicsInsuranceSetupHealthInsurance;

    @SerializedName("practice_checin_edit_clickable_label")
    @Expose
    private String practiceCheckinEditClickableLabel;

    @SerializedName("practice_checkin_demogr_ins_add_another")
    @Expose
    private String practiceCheckinDemogrInsAddAnother;

    @SerializedName("demographics_setup_insurance_title")
    @Expose
    private String demographicsSetupInsuranceTitle;

    @SerializedName("demographics_setup_insurance_label")
    @Expose
    private String demographicsSetupInsuranceLabel;

    @SerializedName("demographics_insurance_label")
    @Expose
    private String demographicsInsuranceTitle;


    /**
     * @return The demographicsUpdateButton
     */
    public String getDemographicsUpdateButton() {

        return StringUtil.getLabelForView(demographicsUpdateButton);
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
        return StringUtil.getLabelForView(demographicsUpdateProfilePhotoLink);
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
        return StringUtil.getLabelForView(demographicsUpdateEmailAndPasswordLink);
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
        return StringUtil.getLabelForView(demographicsAddressSection);
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
        return StringUtil.getLabelForView(demographicsDetailsSection);
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
        return StringUtil.getLabelForView(demographicsDocumentsSection);
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
        return StringUtil.getLabelForView(demographicsAllSetSection);
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
        return StringUtil.getLabelForView(demographicsAddAnotherInsuranceLink);
    }

    /**
     * @param demographicsAddAnotherInsuranceLink The demographics_add_another_insurance_link
     */
    public void setDemographicsAddAnotherInsuranceLink(String demographicsAddAnotherInsuranceLink) {
        this.demographicsAddAnotherInsuranceLink = demographicsAddAnotherInsuranceLink;
    }

    public String getDemographicsNext() {
        return StringUtil.getLabelForView(demographicsNext);
    }

    public void setDemographicsNext(String demographicsNext) {
        this.demographicsNext = demographicsNext;
    }

    public String getDemographicsAddressHeader() {
        return StringUtil.getLabelForView(demographicsAddressHeader);
    }

    public void setDemographicsAddressHeader(String demographicsAddressHeader) {
        this.demographicsAddressHeader = demographicsAddressHeader;
    }

    public String getDemographicsAddressSubheader() {
        return StringUtil.getLabelForView(demographicsAddressSubheader);
    }

    public void setDemographicsAddressSubheader(String demographicsAddressSubheader) {
        this.demographicsAddressSubheader = demographicsAddressSubheader;
    }

    public String getDemographicsRequired() {
        return StringUtil.getLabelForView(demographicsRequired);
    }

    public void setDemographicsRequired(String demographicsRequired) {
        this.demographicsRequired = demographicsRequired;
    }

    public String getDemographicsDetailsHeader() {
        return StringUtil.getLabelForView(demographicsDetailsHeader);
    }

    public void setDemographicsDetailsHeader(String demographicsDetailsHeader) {
        this.demographicsDetailsHeader = demographicsDetailsHeader;
    }

    public String getDemographicsDetailsSubheader() {
        return StringUtil.getLabelForView(demographicsDetailsSubheader);
    }

    public void setDemographicsDetailsSubheader(String demographicsDetailsSubheader) {
        this.demographicsDetailsSubheader = demographicsDetailsSubheader;
    }

    public String getDemographicsDetailsDobHint() {
        return StringUtil.getLabelForView(demographicsDetailsDobHint);
    }

    public void setDemographicsDetailsDobHint(String demographicsDetailsDobHint) {
        this.demographicsDetailsDobHint = demographicsDetailsDobHint;
    }

    public String getDemographicsChooseLabel() {
        return StringUtil.getLabelForView(demographicsChooseLabel);
    }

    public void setDemographicsChooseLabel(String demographicsChooseLabel) {
        this.demographicsChooseLabel = demographicsChooseLabel;
    }

    public String getDemographicsProfileCaptureCaption() {
        return StringUtil.getLabelForView(demographicsProfileCaptureCaption);
    }

    public void setDemographicsProfileCaptureCaption(String demographicsProfileCaptureCaption) {
        this.demographicsProfileCaptureCaption = demographicsProfileCaptureCaption;
    }

    public String getDemographicsProfileReCaptureCaption() {
        return StringUtil.getLabelForView(demographicsProfileReCaptureCaption);
    }

    public void setDemographicsProfileReCaptureCaption(String demographicsProfileReCaptureCaption) {
        this.demographicsProfileReCaptureCaption = demographicsProfileReCaptureCaption;
    }

    public String getDemographicsDetailsAllergiesSection() {
        return StringUtil.getLabelForView(demographicsDetailsAllergiesSection);
    }

    public void setDemographicsDetailsAllergiesSection(String demographicsDetailsAllergiesSection) {
        this.demographicsDetailsAllergiesSection = demographicsDetailsAllergiesSection;
    }

    public String getDemographicsDetailsOptionalHint() {
        return StringUtil.getLabelForView(demographicsDetailsOptionalHint);
    }

    public void setDemographicsDetailsOptionalHint(String demographicsDetailsOptionalHint) {
        this.demographicsDetailsOptionalHint = demographicsDetailsOptionalHint;
    }

    public String getDemographicsDetailsMedicationsSection() {
        return StringUtil.getLabelForView(demographicsDetailsMedicationsSection);
    }

    public void setDemographicsDetailsMedicationsSection(String demographicsDetailsMedicationsSection) {
        this.demographicsDetailsMedicationsSection = demographicsDetailsMedicationsSection;
    }

    public String getDemographicsDetailAllergyLabel() {
        return StringUtil.getLabelForView(demographicsDetailAllergyLabel);
    }

    public void setDemographicsDetailAllergyLabel(String demographicsDetailAllergyLabel) {
        this.demographicsDetailAllergyLabel = demographicsDetailAllergyLabel;
    }

    public String getDemographicsDetailMedicationLabel() {
        return StringUtil.getLabelForView(demographicsDetailMedicationLabel);
    }

    public void setDemographicsDetailMedicationLabel(String demographicsDetailMedicationLabel) {
        this.demographicsDetailMedicationLabel = demographicsDetailMedicationLabel;
    }

    public String getDemographicsDetailsAllergyAddUnlistedLabel() {
        return StringUtil.getLabelForView(demographicsDetailsAllergyAddUnlistedLabel);
    }

    public void setDemographicsDetailsAllergyAddUnlistedLabel(String demographicsDetailsAllergyAddUnlistedLabel) {
        this.demographicsDetailsAllergyAddUnlistedLabel = demographicsDetailsAllergyAddUnlistedLabel;
    }

    public String getDemographicsDetailsMedAddUnlistedLabel() {
        return StringUtil.getLabelForView(demographicsDetailsMedAddUnlistedLabel);
    }

    public void setDemographicsDetailsMedAddUnlistedLabel(String demographicsDetailsMedAddUnlistedLabel) {
        this.demographicsDetailsMedAddUnlistedLabel = demographicsDetailsMedAddUnlistedLabel;
    }

    public String getDemographicsDocumentsSwitchLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsSwitchLabel);
    }

    public void setDemographicsDocumentsSwitchLabel(String demographicsDocumentsSwitchLabel) {
        this.demographicsDocumentsSwitchLabel = demographicsDocumentsSwitchLabel;
    }

    public String getDemographicsDocumentsHeader() {
        return StringUtil.getLabelForView(demographicsDocumentsHeader);
    }

    public void setDemographicsDocumentsHeader(String demographicsDocumentsHeader) {
        this.demographicsDocumentsHeader = demographicsDocumentsHeader;
    }

    public String getDemographicsDocumentsSubheader() {
        return StringUtil.getLabelForView(demographicsDocumentsSubheader);
    }

    public void setDemographicsDocumentsSubheader(String demographicsDocumentsSubheader) {
        this.demographicsDocumentsSubheader = demographicsDocumentsSubheader;
    }

    public String getDemographicsDocumentsMultiInsLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsMultiInsLabel);
    }

    public void setDemographicsDocumentsMultiInsLabel(String demographicsDocumentsMultiInsLabel) {
        this.demographicsDocumentsMultiInsLabel = demographicsDocumentsMultiInsLabel;
    }

    public String getDemographicsDocumentsScanFrontLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsScanFrontLabel);
    }

    public void setDemographicsDocumentsScanFrontLabel(String demographicsDocumentsScanFrontLabel) {
        this.demographicsDocumentsScanFrontLabel = demographicsDocumentsScanFrontLabel;
    }

    public String getDemographicsDocumentsScanBackLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsScanBackLabel);
    }

    public void setDemographicsDocumentsScanBackLabel(String demographicsDocumentsScanBackLabel) {
        this.demographicsDocumentsScanBackLabel = demographicsDocumentsScanBackLabel;
    }

    public String getDemographicsDocumentsRescanFrontLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsRescanFrontLabel);
    }

    public void setDemographicsDocumentsRescanFrontLabel(String demographicsDocumentsRescanFrontLabel) {
        this.demographicsDocumentsRescanFrontLabel = demographicsDocumentsRescanFrontLabel;
    }

    public String getDemographicsDocumentsRescanBackLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsRescanBackLabel);
    }

    public void setDemographicsDocumentsRescanBackLabel(String demographicsDocumentsRescanBackLabel) {
        this.demographicsDocumentsRescanBackLabel = demographicsDocumentsRescanBackLabel;
    }

    public String getDemographicsCancelLabel() {
        return StringUtil.getLabelForView(demographicsCancelLabel);
    }

    public void setDemographicsCancelLabel(String demographicsCancelLabel) {
        this.demographicsCancelLabel = demographicsCancelLabel;
    }

    public String getDemographicsTitleSelectState() {
        return StringUtil.getLabelForView(demographicsTitleSelectState);
    }

    public void setDemographicsTitleSelectState(String demographicsTitleSelectState) {
        this.demographicsTitleSelectState = demographicsTitleSelectState;
    }

    public String getDemographicsTitleSelectPlan() {
        return StringUtil.getLabelForView(demographicsTitleSelectPlan);
    }

    public void setDemographicsTitleSelectPlan(String demographicsTitleSelectPlan) {
        this.demographicsTitleSelectPlan = demographicsTitleSelectPlan;
    }

    public String getDemographicsTitleSelectProvider() {
        return StringUtil.getLabelForView(demographicsTitleSelectProvider);
    }

    public void setDemographicsTitleSelectProvider(String demographicsTitleSelectProvider) {
        this.demographicsTitleSelectProvider = demographicsTitleSelectProvider;
    }

    public String getDemographicsTitleSelectGender() {
        return StringUtil.getLabelForView(demographicsTitleSelectGender);
    }

    public void setDemographicsTitleSelectGender(String demographicsTitleSelectGender) {
        this.demographicsTitleSelectGender = demographicsTitleSelectGender;
    }

    public String getDemographicsTitleSelectEthnicity() {
        return StringUtil.getLabelForView(demographicsTitleSelectEthnicity);
    }

    public void setDemographicsTitleSelectEthnicity(String demographicsTitleSelectEthnicity) {
        this.demographicsTitleSelectEthnicity = demographicsTitleSelectEthnicity;
    }

    public String getDemographicsTitleSelectRace() {
        return StringUtil.getLabelForView(demographicsTitleSelectRace);
    }

    public void setDemographicsTitleSelectRace(String demographicsTitleSelectRace) {
        this.demographicsTitleSelectRace = demographicsTitleSelectRace;
    }

    public String getDemographicsTitleSelectIdType() {
        return StringUtil.getLabelForView(demographicsTitleSelectIdType);
    }

    public void setDemographicsTitleSelectIdType(String demographicsTitleSelectIdType) {
        this.demographicsTitleSelectIdType = demographicsTitleSelectIdType;
    }

    public String getDemographicsDocumentsChoosePlanLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsChoosePlanLabel);
    }

    public void setDemographicsDocumentsChoosePlanLabel(String demographicsDocumentsChoosePlanLabel) {
        this.demographicsDocumentsChoosePlanLabel = demographicsDocumentsChoosePlanLabel;
    }

    public String getDemographicsDocumentsInsTypeLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsInsTypeLabel);
    }

    public void setDemographicsDocumentsInsTypeLabel(String demographicsDocumentsInsTypeLabel) {
        this.demographicsDocumentsInsTypeLabel = demographicsDocumentsInsTypeLabel;
    }

    public String getDemographicsTitleCardType() {
        return StringUtil.getLabelForView(demographicsTitleCardType);
    }

    public void setDemographicsTitleCardType(String demographicsTitleCardType) {
        this.demographicsTitleCardType = demographicsTitleCardType;
    }

    public String getDemographicsAllSetHeader() {
        return StringUtil.getLabelForView(demographicsAllSetHeader);
    }

    public void setDemographicsAllSetHeader(String demographicsAllSetHeader) {
        this.demographicsAllSetHeader = demographicsAllSetHeader;
    }

    public String getDemographicsAllSetGoButton() {
        return StringUtil.getLabelForView(demographicsAllSetGoButton);
    }

    public void setDemographicsAllSetGoButton(String demographicsAllSetGoButton) {
        this.demographicsAllSetGoButton = demographicsAllSetGoButton;
    }

    /**
     * @return The demographicsReviewToolbarTitle
     */
    public String getDemographicsReviewToolbarTitle() {
        return StringUtil.getLabelForView(demographicsReviewToolbarTitle);
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
        return StringUtil.getLabelForView(demographicsUpdateInsuranceToolbarTitle);
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
        return StringUtil.getLabelForView(demographicsReviewUpdateButton);
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
        return StringUtil.getLabelForView(demographicsInsuranceUpdateButton);
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
        return StringUtil.getLabelForView(demographicsReviewCorrectButton);
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
        return StringUtil.getLabelForView(demographicsUpdateInsuranceButton);
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
        return StringUtil.getLabelForView(demographicsReviewPeronsonalinfoSection);
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
        return StringUtil.getLabelForView(demographicsReviewScreenTitle);
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
        return StringUtil.getLabelForView(demographicsUpdateInsuranceScreenTitle);
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
        return StringUtil.getLabelForView(demographicsReviewScreenSubtitle);
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
        return StringUtil.getLabelForView(demographicsHealthinsurance1Section) ;
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
        return StringUtil.getLabelForView(demographicsHealthinsurance2Section);
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
        return StringUtil.getLabelForView(demographicsHealthinsurance3Section);
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
        return StringUtil.getLabelForView(demographicsUpdateDemographicTitle);
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
        return StringUtil.getLabelForView(demographicSectionTitle);
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
        return StringUtil.getLabelForView(documentsRemove);

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
        return StringUtil.getLabelForView(demographicsTakePhotoOption) ;
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
        return StringUtil.getLabelForView(demographicsChooseFromLibraryOption);
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
        return StringUtil.getLabelForView(demographicsCaptureOptionsTitle);
    }

    /**
     * @param demographicsCaptureOptionsTitle The label
     */
    public void setDemographicsCaptureOptionsTitle(String demographicsCaptureOptionsTitle) {
        this.demographicsCaptureOptionsTitle = demographicsCaptureOptionsTitle;
    }

    public String getDemographicsInsuranceScanInsuranceCard() {
        return StringUtil.getLabelForView(demographicsInsuranceScanInsuranceCard);
    }

    public void setDemographicsInsuranceScanInsuranceCard(String demographicsInsuranceScanInsuranceCard) {
        this.demographicsInsuranceScanInsuranceCard = demographicsInsuranceScanInsuranceCard;
    }

    public String getDemographicsInsuranceScanMsg() {
        return StringUtil.getLabelForView(demographicsInsuranceScanMsg);
    }

    public void setDemographicsInsuranceScanMsg(String demographicsInsuranceScanMsg) {
        this.demographicsInsuranceScanMsg = demographicsInsuranceScanMsg;
    }

    public String getDemographicsInsuranceScan() {
        return StringUtil.getLabelForView(demographicsInsuranceScan);
    }

    public void setDemographicsInsuranceScan(String demographicsInsuranceScan) {
        this.demographicsInsuranceScan = demographicsInsuranceScan;
    }

    public String getDemographicsInsuranceSave() {
        return StringUtil.getLabelForView(demographicsInsuranceSave);
    }

    public void setDemographicsInsuranceSave(String demographicsInsuranceSave) {
        this.demographicsInsuranceSave = demographicsInsuranceSave;
    }

    public String getDemographicsInsuranceClear() {
        return StringUtil.getLabelForView(demographicsInsuranceClear);
    }

    public void setDemographicsInsuranceClear(String demographicsInsuranceClear) {
        this.demographicsInsuranceClear = demographicsInsuranceClear;
    }

    public String getDemographicsIdentityText() {
        return StringUtil.getLabelForView(demographicsIdentityText);
    }

    public void setDemographicsIdentityText(String demographicsIdentityText) {
        this.demographicsIdentityText = demographicsIdentityText;
    }

    public String getDemographicsMissingInformation() {
        return StringUtil.getLabelForView(demographicsMissingInformation);
    }

    public void setDemographicsMissingInformation(String demographicsMissingInformation) {
        this.demographicsMissingInformation = demographicsMissingInformation;
    }

    public String getDemographicsHealthInsurance() {
        return StringUtil.getLabelForView(demographicsHealthInsurance);
    }

    public void setDemographicsHealthInsurance(String demographicsHealthInsurance) {
        this.demographicsHealthInsurance = demographicsHealthInsurance;
    }

    public String getDemographicsDriversLicenseNumber() {
        return StringUtil.getLabelForView(demographicsDriversLicenseNumber);
    }

    public void setDemographicsDriversLicenseNumber(String demographicsDriversLicenseNumber) {
        this.demographicsDriversLicenseNumber = demographicsDriversLicenseNumber;
    }

    public String getDemographicsDriversLicenseAddStateLabel() {
        return StringUtil.getLabelForView(demographicsDriversLicenseAddStateLabel);
    }

    public void setDemographicsDriversLicenseAddStateLabel(String demographicsDriversLicenseAddStateLabel) {
        this.demographicsDriversLicenseAddStateLabel = demographicsDriversLicenseAddStateLabel;
    }

    public String getDemographicsInsuranceSetupHealthInsurance() {
        return StringUtil.getLabelForView(demographicsInsuranceSetupHealthInsurance);
    }

    public void setDemographicsInsuranceSetupHealthInsurance(String demographicsInsuranceSetupHealthInsurance) {
        this.demographicsInsuranceSetupHealthInsurance = demographicsInsuranceSetupHealthInsurance;
    }

    public String getDemographicsInsuranceSetup() {
        return StringUtil.getLabelForView(demographicsInsuranceSetup);
    }

    public void setDemographicsInsuranceSetup(String demographicsInsuranceSetup) {
        this.demographicsInsuranceSetup = demographicsInsuranceSetup;
    }

    public String getPracticeCheckinEditClickableLabel() {
        return StringUtil.getLabelForView(practiceCheckinEditClickableLabel);
    }

    public void setPracticeCheckinEditClickableLabel(String practiceCheckinEditClickableLabel) {
        this.practiceCheckinEditClickableLabel = practiceCheckinEditClickableLabel;
    }

    public String getPracticeCheckinDemogrInsAddAnother() {
        return StringUtil.getLabelForView(practiceCheckinDemogrInsAddAnother);
    }

    public void setPracticeCheckinDemogrInsAddAnother(String practiceCheckinDemogrInsAddAnother) {
        this.practiceCheckinDemogrInsAddAnother = practiceCheckinDemogrInsAddAnother;
    }

    public String getDemographicsSetupInsuranceTitle() {
        return StringUtil.getLabelForView(demographicsSetupInsuranceTitle);
    }

    public void setDemographicsSetupInsuranceTitle(String demographicsSetupInsuranceTitle) {
        this.demographicsSetupInsuranceTitle = demographicsSetupInsuranceTitle;
    }

    public String getDemographicsSetupInsuranceLabel() {
        return StringUtil.getLabelForView(demographicsSetupInsuranceLabel);
    }

    public void setDemographicsSetupInsuranceLabel(String demographicsSetupInsuranceLabel) {
        this.demographicsSetupInsuranceLabel = demographicsSetupInsuranceLabel;
    }

    public String getDemographicsInsuranceTitle() {
        return StringUtil.getLabelForView(demographicsInsuranceTitle);
    }

    public void setDemographicsInsuranceTitle(String demographicsInsuranceTitle) {
        this.demographicsInsuranceTitle = demographicsInsuranceTitle;
    }


}