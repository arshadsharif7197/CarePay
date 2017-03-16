package com.carecloud.carepaylibray.demographics.dtos.metadata.labels;

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

    @SerializedName("practice_checkin_demogr_scan_ins_card_label")
    @Expose
    private String demographicsInsuranceScanInsuranceCard;

    @SerializedName("practice_checkin_demogr_scan_ins_card_instructions")
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

    @SerializedName("demographics_i_dont_have_health_insurance_label")
    @Expose
    private String demographicsDontHaveHealthInsuranceLabel;

    @SerializedName("demographics_i_have_health_insurance_label")
    @Expose
    private String demographicsHaveHealthInsuranceLabel;

    @SerializedName("demographics_add_health_insurance_button_title")
    @Expose
    private String demographicsAddHealthInsuranceButtonTitle;
    @SerializedName("demographics_setup_insurance_title")
    @Expose
    private String demographicsSetupInsuranceTitle;

    @SerializedName("demographics_setup_insurance_label")
    @Expose
    private String demographicsSetupInsuranceLabel;

    @SerializedName("demographics_consent_forms_title")
    @Expose
    private String demographicsConsentFormsTitle;

    @SerializedName("demographics_meds_allergies_title")
    @Expose
    private String demographicsMedsAllergiesTitle;

    @SerializedName("demographics_payment_title")
    @Expose
    private String demographicsPaymentTitle;

    @SerializedName("practice_chekin_section_intake_forms")
    @Expose
    private String practiceChekinSectionIntakeForms;

    @SerializedName("demographics_patient_information_title")
    @Expose
    private String demographicsPatientInformationTitle;

    @SerializedName("demographics_insurance_label")
    @Expose
    private String demographicsInsuranceTitle;

    @SerializedName("demographics_insurance_type_label")
    @Expose
    private String demographicsInsuranceTypeLabel;

    @SerializedName("demographics_state")
    @Expose
    private String demographicsState;

    @SerializedName("demographics_save_changes_button")
    @Expose
    private String demographicsSaveChangesButton;

    @SerializedName("demographics_insurance_group_number")
    @Expose
    private String demographicsInsuranceGroupNumber;

    @SerializedName("practice_checkin_demogr_scan_ins_card_scan_button_label")
    @Expose
    private String demographicsScanButtonLabel;

    @SerializedName("practice_checkin_demogr_scan_ins_card_clear_button_label")
    @Expose
    private String demographicsClearButtonLabel;

    @SerializedName("practice_checkin_demogr_scan_ins_card_save_button_label")
    @Expose
    private String demographicsSaveButtonLabel;

    @SerializedName("demographics_insurance_payer_label")
    @Expose
    private String demographicsInsurancePayerLabel;

    @SerializedName("demographics_insurance_card_number_label")
    @Expose
    private String demographicsInsuranceCardNumberLabel;

    @SerializedName("demographics_insurance_group_number_label")
    @Expose
    private String demographicsInsuranceGroupNumberLabel;

    @SerializedName("demographics_insurance_photo_of_card_front")
    @Expose
    private String demographicsInsurancePhotoOfCardFront;

    @SerializedName("demographics_insurance_photo_of_card_back")
    @Expose
    private String demographicsInsurancePhotoOfCardBack;

    @SerializedName("demographics_documents_identification")
    @Expose
    private String demographicsDocumentsIdentification;

    @SerializedName("demographics_documents_picture_of_front")
    @Expose
    private String demographicsDocumentsPictureOfFront;

    @SerializedName("demographics_documents_picture_of_back")
    @Expose
    private String demographicsDocumentsPictureOfBack;

    @SerializedName("demographics_review_next_button")
    @Expose
    private String demographicsReviewNextButton;

    @SerializedName("demographics_insurance_card_number")
    @Expose
    private String demographicsInsuranceCardNumber;
    @SerializedName("demographics_insurance_take_front_photo")
    @Expose
    private String demographicsInsuranceTakeFrontPhotoLabel;
    @SerializedName("demographics_insurance_take_back_photo")
    @Expose
    private String demographicsInsuranceTakeBackPhotoLabel;
    @SerializedName("practice_checkin_demogr_ins_dont_have_one_button_label")
    @Expose
    private String practiceCheckinDemogrInsDontHaveOneButtonLabel;
    @SerializedName("practice_checkin_demogr_ins_add_new_button_label")
    @Expose
    private String practiceCheckinDemogrInsAddNewButtonLabel;
    @SerializedName("demographics_review_identification")
    @Expose
    private String demographicsReviewIdentification;
    @SerializedName("demographics_review_take_photo_of_front")
    @Expose
    private String demographicsReviewTakePhotoOfFront;
    @SerializedName("demographics_review_take_photo_of_back")
    @Expose
    private String demographicsReviewTakePhotoOfBack;
    @SerializedName("demographics_insurance_retake_front_photo")
    @Expose
    private String demographicsInsuranceRetakeFrontPhoto;
    @SerializedName("demographics_insurance_retake_back_photo")
    @Expose
    private String demographicsInsuranceRetakeBackPhoto;

    public String getDemographicsInsuranceRetakeFrontPhoto() {
        return StringUtil.getLabelForView(demographicsInsuranceRetakeFrontPhoto);
    }

    public void setDemographicsInsuranceRetakeFrontPhoto(String demographicsInsuranceRetakeFrontPhoto) {
        this.demographicsInsuranceRetakeFrontPhoto = demographicsInsuranceRetakeFrontPhoto;
    }

    public String getDemographicsInsuranceRetakeBackPhoto() {
        return StringUtil.getLabelForView(demographicsInsuranceRetakeBackPhoto);
    }

    public void setDemographicsInsuranceRetakeBackPhoto(String demographicsInsuranceRetakeBackPhoto) {
        this.demographicsInsuranceRetakeBackPhoto = demographicsInsuranceRetakeBackPhoto;
    }
    @SerializedName("demographics_review_demographics")
    @Expose
    private String demographicsReviewDemographics;

    public String getDemographicsInsuranceCardNumber() {
        return StringUtil.getLabelForView(demographicsInsuranceCardNumber);
    }

    public void setDemographicsInsuranceCardNumber(String demographicsInsuranceCardNumber) {
        this.demographicsInsuranceCardNumber = demographicsInsuranceCardNumber;
    }

    public String getDemographicsInsuranceTakeFrontPhotoLabel() {
        return StringUtil.getLabelForView(demographicsInsuranceTakeFrontPhotoLabel);
    }

    public void setDemographicsInsuranceTakeFrontPhotoLabel(String demographicsInsuranceTakeFrontPhotoLabel) {
        this.demographicsInsuranceTakeFrontPhotoLabel = demographicsInsuranceTakeFrontPhotoLabel;
    }

    public String getDemographicsInsuranceTakeBackPhotoLabel() {
        return StringUtil.getLabelForView(demographicsInsuranceTakeBackPhotoLabel);
    }

    public void setDemographicsInsuranceTakeBackPhotoLabel(String demographicsInsuranceTakeBackPhotoLabel) {
        this.demographicsInsuranceTakeBackPhotoLabel = demographicsInsuranceTakeBackPhotoLabel;
    }

    public String getPracticeCheckinDemogrInsDontHaveOneButtonLabel() {
        return StringUtil.getLabelForView(practiceCheckinDemogrInsDontHaveOneButtonLabel);
    }

    public void setPracticeCheckinDemogrInsDontHaveOneButtonLabel(String practiceCheckinDemogrInsDontHaveOneButtonLabel) {
        this.practiceCheckinDemogrInsDontHaveOneButtonLabel = practiceCheckinDemogrInsDontHaveOneButtonLabel;
    }

    public String getPracticeCheckinDemogrInsAddNewButtonLabel() {
        return StringUtil.getLabelForView(practiceCheckinDemogrInsAddNewButtonLabel);
    }

    public void setPracticeCheckinDemogrInsAddNewButtonLabel(String practiceCheckinDemogrInsAddNewButtonLabel) {
        this.practiceCheckinDemogrInsAddNewButtonLabel = practiceCheckinDemogrInsAddNewButtonLabel;
    }

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

    /**
     * Gets demographics insurance scan insurance card.
     *
     * @return the demographics insurance scan insurance card
     */
    public String getDemographicsInsuranceScanInsuranceCard() {
        return StringUtil.getLabelForView(demographicsInsuranceScanInsuranceCard);
    }

    /**
     * Sets demographics insurance scan insurance card.
     *
     * @param demographicsInsuranceScanInsuranceCard the demographics insurance scan insurance card
     */
    public void setDemographicsInsuranceScanInsuranceCard(String demographicsInsuranceScanInsuranceCard) {
        this.demographicsInsuranceScanInsuranceCard = demographicsInsuranceScanInsuranceCard;
    }

    /**
     * Gets demographics insurance scan msg.
     *
     * @return the demographics insurance scan msg
     */
    public String getDemographicsInsuranceScanMsg() {
        return StringUtil.getLabelForView(demographicsInsuranceScanMsg);
    }

    /**
     * Sets demographics insurance scan msg.
     *
     * @param demographicsInsuranceScanMsg the demographics insurance scan msg
     */
    public void setDemographicsInsuranceScanMsg(String demographicsInsuranceScanMsg) {
        this.demographicsInsuranceScanMsg = demographicsInsuranceScanMsg;
    }

    /**
     * Gets demographics insurance scan.
     *
     * @return the demographics insurance scan
     */
    public String getDemographicsInsuranceScan() {
        return StringUtil.getLabelForView(demographicsInsuranceScan);
    }

    /**
     * Sets demographics insurance scan.
     *
     * @param demographicsInsuranceScan the demographics insurance scan
     */
    public void setDemographicsInsuranceScan(String demographicsInsuranceScan) {
        this.demographicsInsuranceScan = demographicsInsuranceScan;
    }

    /**
     * Gets demographics insurance save.
     *
     * @return the demographics insurance save
     */
    public String getDemographicsInsuranceSave() {
        return StringUtil.getLabelForView(demographicsInsuranceSave);
    }

    /**
     * Sets demographics insurance save.
     *
     * @param demographicsInsuranceSave the demographics insurance save
     */
    public void setDemographicsInsuranceSave(String demographicsInsuranceSave) {
        this.demographicsInsuranceSave = demographicsInsuranceSave;
    }

    /**
     * Gets demographics insurance clear.
     *
     * @return the demographics insurance clear
     */
    public String getDemographicsInsuranceClear() {
        return StringUtil.getLabelForView(demographicsInsuranceClear);
    }

    /**
     * Sets demographics insurance clear.
     *
     * @param demographicsInsuranceClear the demographics insurance clear
     */
    public void setDemographicsInsuranceClear(String demographicsInsuranceClear) {
        this.demographicsInsuranceClear = demographicsInsuranceClear;
    }

    /**
     * Gets demographics identity text.
     *
     * @return the demographics identity text
     */
    public String getDemographicsIdentityText() {
        return StringUtil.getLabelForView(demographicsIdentityText);
    }

    /**
     * Sets demographics identity text.
     *
     * @param demographicsIdentityText the demographics identity text
     */
    public void setDemographicsIdentityText(String demographicsIdentityText) {
        this.demographicsIdentityText = demographicsIdentityText;
    }

    /**
     * Gets demographics missing information.
     *
     * @return the demographics missing information
     */
    public String getDemographicsMissingInformation() {
        return StringUtil.getLabelForView(demographicsMissingInformation);
    }

    /**
     * Sets demographics missing information.
     *
     * @param demographicsMissingInformation the demographics missing information
     */
    public void setDemographicsMissingInformation(String demographicsMissingInformation) {
        this.demographicsMissingInformation = demographicsMissingInformation;
    }

    /**
     * Gets demographics health insurance.
     *
     * @return the demographics health insurance
     */
    public String getDemographicsHealthInsurance() {
        return StringUtil.getLabelForView(demographicsHealthInsurance);
    }

    /**
     * Sets demographics health insurance.
     *
     * @param demographicsHealthInsurance the demographics health insurance
     */
    public void setDemographicsHealthInsurance(String demographicsHealthInsurance) {
        this.demographicsHealthInsurance = demographicsHealthInsurance;
    }

    /**
     * Gets demographics drivers license number.
     *
     * @return the demographics drivers license number
     */
    public String getDemographicsDriversLicenseNumber() {
        return StringUtil.getLabelForView(demographicsDriversLicenseNumber);
    }

    /**
     * Sets demographics drivers license number.
     *
     * @param demographicsDriversLicenseNumber the demographics drivers license number
     */
    public void setDemographicsDriversLicenseNumber(String demographicsDriversLicenseNumber) {
        this.demographicsDriversLicenseNumber = demographicsDriversLicenseNumber;
    }

    /**
     * Gets demographics drivers license add state label.
     *
     * @return the demographics drivers license add state label
     */
    public String getDemographicsDriversLicenseAddStateLabel() {
        return StringUtil.getLabelForView(demographicsDriversLicenseAddStateLabel);
    }

    /**
     * Sets demographics drivers license add state label.
     *
     * @param demographicsDriversLicenseAddStateLabel the demographics drivers license add state label
     */
    public void setDemographicsDriversLicenseAddStateLabel(String demographicsDriversLicenseAddStateLabel) {
        this.demographicsDriversLicenseAddStateLabel = demographicsDriversLicenseAddStateLabel;
    }

    /**
     * Gets demographics insurance setup health insurance.
     *
     * @return the demographics insurance setup health insurance
     */
    public String getDemographicsInsuranceSetupHealthInsurance() {
        return StringUtil.getLabelForView(demographicsInsuranceSetupHealthInsurance);
    }

    /**
     * Sets demographics insurance setup health insurance.
     *
     * @param demographicsInsuranceSetupHealthInsurance the demographics insurance setup health insurance
     */
    public void setDemographicsInsuranceSetupHealthInsurance(String demographicsInsuranceSetupHealthInsurance) {
        this.demographicsInsuranceSetupHealthInsurance = demographicsInsuranceSetupHealthInsurance;
    }

    /**
     * Gets demographics insurance setup.
     *
     * @return the demographics insurance setup
     */
    public String getDemographicsInsuranceSetup() {
        return StringUtil.getLabelForView(demographicsInsuranceSetup);
    }

    /**
     * Sets demographics insurance setup.
     *
     * @param demographicsInsuranceSetup the demographics insurance setup
     */
    public void setDemographicsInsuranceSetup(String demographicsInsuranceSetup) {
        this.demographicsInsuranceSetup = demographicsInsuranceSetup;
    }

    /**
     * Gets practice checkin edit clickable label.
     *
     * @return the practice checkin edit clickable label
     */
    public String getPracticeCheckinEditClickableLabel() {
        return StringUtil.getLabelForView(practiceCheckinEditClickableLabel);
    }

    /**
     * Sets practice checkin edit clickable label.
     *
     * @param practiceCheckinEditClickableLabel the practice checkin edit clickable label
     */
    public void setPracticeCheckinEditClickableLabel(String practiceCheckinEditClickableLabel) {
        this.practiceCheckinEditClickableLabel = practiceCheckinEditClickableLabel;
    }

    /**
     * Gets practice checkin demogr ins add another.
     *
     * @return the practice checkin demogr ins add another
     */
    public String getPracticeCheckinDemogrInsAddAnother() {
        return StringUtil.getLabelForView(practiceCheckinDemogrInsAddAnother);
    }

    /**
     * Sets practice checkin demogr ins add another.
     *
     * @param practiceCheckinDemogrInsAddAnother the practice checkin demogr ins add another
     */
    public void setPracticeCheckinDemogrInsAddAnother(String practiceCheckinDemogrInsAddAnother) {
        this.practiceCheckinDemogrInsAddAnother = practiceCheckinDemogrInsAddAnother;
    }

    /**
     * Gets demographics dont have health insurance label.
     *
     * @return the demographics dont have health insurance label
     */
    public String getDemographicsDontHaveHealthInsuranceLabel() {
        return StringUtil.getLabelForView(demographicsDontHaveHealthInsuranceLabel);
    }

    /**
     * Gets demographics setup insurance title.
     *
     * @return the demographics setup insurance title
     */
    public String getDemographicsSetupInsuranceTitle() {
        return StringUtil.getLabelForView(demographicsSetupInsuranceTitle);
    }

    /**
     * Sets demographics setup insurance title.
     *
     * @param demographicsSetupInsuranceTitle the demographics setup insurance title
     */
    public void setDemographicsSetupInsuranceTitle(String demographicsSetupInsuranceTitle) {
        this.demographicsSetupInsuranceTitle = demographicsSetupInsuranceTitle;
    }

    /**
     * Gets demographics setup insurance label.
     *
     * @return the demographics setup insurance label
     */
    public String getDemographicsSetupInsuranceLabel() {
        return StringUtil.getLabelForView(demographicsSetupInsuranceLabel);
    }

    /**
     * Sets demographics setup insurance label.
     *
     * @param demographicsSetupInsuranceLabel the demographics setup insurance label
     */
    public void setDemographicsSetupInsuranceLabel(String demographicsSetupInsuranceLabel) {
        this.demographicsSetupInsuranceLabel = demographicsSetupInsuranceLabel;
    }

    /**
     * Gets demographics insurance title.
     *
     * @return the demographics insurance title
     */
    public String getDemographicsInsuranceTitle() {
        return StringUtil.getLabelForView(demographicsInsuranceTitle);
    }

    /**
     * Sets demographics insurance title.
     *
     * @param demographicsInsuranceTitle the demographics insurance title
     */
    public void setDemographicsInsuranceTitle(String demographicsInsuranceTitle) {
        this.demographicsInsuranceTitle = demographicsInsuranceTitle;
    }


    /**
     * Sets demographics dont have health insurance label.
     *
     * @param demographicsDontHaveHealthInsuranceLabel the demographics dont have health insurance label
     */
    public void setDemographicsDontHaveHealthInsuranceLabel(String demographicsDontHaveHealthInsuranceLabel) {
        this.demographicsDontHaveHealthInsuranceLabel = demographicsDontHaveHealthInsuranceLabel;
    }

    /**
     * Gets demographics have health insurance label.
     *
     * @return the demographics have health insurance label
     */
    public String getDemographicsHaveHealthInsuranceLabel() {
        return StringUtil.getLabelForView(demographicsHaveHealthInsuranceLabel);
    }

    /**
     * Sets demographics have health insurance label.
     *
     * @param demographicsHaveHealthInsuranceLabel the demographics have health insurance label
     */
    public void setDemographicsHaveHealthInsuranceLabel(String demographicsHaveHealthInsuranceLabel) {
        this.demographicsHaveHealthInsuranceLabel = demographicsHaveHealthInsuranceLabel;
    }

    /**
     * Gets demographics add health insurance button title.
     *
     * @return the demographics add health insurance button title
     */
    public String getDemographicsAddHealthInsuranceButtonTitle() {
        return StringUtil.getLabelForView(demographicsAddHealthInsuranceButtonTitle);
    }

    /**
     * Sets demographics add health insurance button title.
     *
     * @param demographicsAddHealthInsuranceButtonTitle the demographics add health insurance button title
     */
    public void setDemographicsAddHealthInsuranceButtonTitle(String demographicsAddHealthInsuranceButtonTitle) {
        this.demographicsAddHealthInsuranceButtonTitle = demographicsAddHealthInsuranceButtonTitle;
    }

    /**
     * Gets demographics insurance type label.
     *
     * @return the demographics insurance type label
     */
    public String getDemographicsInsuranceTypeLabel() {
        return StringUtil.getLabelForView(demographicsInsuranceTypeLabel);
    }

    /**
     * Sets demographics insurance type label.
     *
     * @param demographicsInsuranceTypeLabel the demographics insurance type label
     */
    public void setDemographicsInsuranceTypeLabel(String demographicsInsuranceTypeLabel) {
        this.demographicsInsuranceTypeLabel = demographicsInsuranceTypeLabel;
    }

    /**
     * Gets demographics state.
     *
     * @return the demographics state
     */
    public String getDemographicsState() {
        return StringUtil.getLabelForView(demographicsState);
    }

    /**
     * Sets demographics state.
     *
     * @param demographicsState the demographics state
     */
    public void setDemographicsState(String demographicsState) {
        this.demographicsState = demographicsState;
    }

    /**
     * Gets demographics save changes button.
     *
     * @return the demographics save changes button
     */
    public String getDemographicsSaveChangesButton() {
        return StringUtil.getLabelForView(demographicsSaveChangesButton);
    }

    /**
     * Sets demographics save changes button.
     *
     * @param demographicsSaveChangesButton the demographics save changes button
     */
    public void setDemographicsSaveChangesButton(String demographicsSaveChangesButton) {
        this.demographicsSaveChangesButton = demographicsSaveChangesButton;
    }

    /**
     * Gets demographics insurance group number.
     *
     * @return the demographics insurance group number
     */
    public String getDemographicsInsuranceGroupNumber() {
        return StringUtil.getLabelForView(demographicsInsuranceGroupNumber);
    }

    /**
     * Sets demographics insurance group number.
     *
     * @param demographicsInsuranceGroupNumber the demographics insurance group number
     */
    public void setDemographicsInsuranceGroupNumber(String demographicsInsuranceGroupNumber) {
        this.demographicsInsuranceGroupNumber = demographicsInsuranceGroupNumber;
    }

    /**
     * Gets demographics scan button label.
     *
     * @return the demographics scan button label
     */
    public String getDemographicsScanButtonLabel() {
        return StringUtil.getLabelForView(demographicsScanButtonLabel);
    }

    /**
     * Sets demographics scan button label.
     *
     * @param demographicsScanButtonLabel the demographics scan button label
     */
    public void setDemographicsScanButtonLabel(String demographicsScanButtonLabel) {
        this.demographicsScanButtonLabel = demographicsScanButtonLabel;
    }

    /**
     * Gets demographics clear button label.
     *
     * @return the demographics clear button label
     */
    public String getDemographicsClearButtonLabel() {
        return StringUtil.getLabelForView(demographicsClearButtonLabel);
    }

    /**
     * Sets demographics clear button label.
     *
     * @param demographicsClearButtonLabel the demographics clear button label
     */
    public void setDemographicsClearButtonLabel(String demographicsClearButtonLabel) {
        this.demographicsClearButtonLabel = demographicsClearButtonLabel;
    }

    /**
     * Gets demographics save button label.
     *
     * @return the demographics save button label
     */
    public String getDemographicsSaveButtonLabel() {
        return StringUtil.getLabelForView(demographicsSaveButtonLabel);
    }

    /**
     * Sets demographics save button label.
     *
     * @param demographicsSaveButtonLabel the demographics save button label
     */
    public void setDemographicsSaveButtonLabel(String demographicsSaveButtonLabel) {
        this.demographicsSaveButtonLabel = demographicsSaveButtonLabel;
    }

    /**
     * Gets demographics insurance payer label.
     *
     * @return the demographics insurance payer label
     */
    public String getDemographicsInsurancePayerLabel() {
        return StringUtil.getLabelForView(demographicsInsurancePayerLabel);
    }

    /**
     * Sets demographics insurance payer label.
     *
     * @param demographicsInsurancePayerLabel the demographics insurance payer label
     */
    public void setDemographicsInsurancePayerLabel(String demographicsInsurancePayerLabel) {
        this.demographicsInsurancePayerLabel = demographicsInsurancePayerLabel;
    }

    /**
     * Gets demographics insurance card number label.
     *
     * @return the demographics insurance card number label
     */
    public String getDemographicsInsuranceCardNumberLabel() {
        return StringUtil.getLabelForView(demographicsInsuranceCardNumberLabel);
    }

    /**
     * Sets demographics insurance card number label.
     *
     * @param demographicsInsuranceCardNumberLabel the demographics insurance card number label
     */
    public void setDemographicsInsuranceCardNumberLabel(String demographicsInsuranceCardNumberLabel) {
        this.demographicsInsuranceCardNumberLabel = demographicsInsuranceCardNumberLabel;
    }

    /**
     * Gets demographics insurance group number label.
     *
     * @return the demographics insurance group number label
     */
    public String getDemographicsInsuranceGroupNumberLabel() {
        return StringUtil.getLabelForView(demographicsInsuranceGroupNumberLabel);
    }

    /**
     * Sets demographics insurance group number label.
     *
     * @param demographicsInsuranceGroupNumberLabel the demographics insurance group number label
     */
    public void setDemographicsInsuranceGroupNumberLabel(String demographicsInsuranceGroupNumberLabel) {
        this.demographicsInsuranceGroupNumberLabel = demographicsInsuranceGroupNumberLabel;
    }

    /**
     * Gets demographics insurance photo of card front.
     *
     * @return the demographics insurance photo of card front
     */
    public String getDemographicsInsurancePhotoOfCardFront() {
        return StringUtil.getLabelForView(demographicsInsurancePhotoOfCardFront);
    }

    /**
     * Sets demographics insurance photo of card front.
     *
     * @param demographicsInsurancePhotoOfCardFront the demographics insurance photo of card front
     */
    public void setDemographicsInsurancePhotoOfCardFront(String demographicsInsurancePhotoOfCardFront) {
        this.demographicsInsurancePhotoOfCardFront = demographicsInsurancePhotoOfCardFront;
    }

    /**
     * Gets demographics insurance photo of card back.
     *
     * @return the demographics insurance photo of card back
     */
    public String getDemographicsInsurancePhotoOfCardBack() {
        return StringUtil.getLabelForView(demographicsInsurancePhotoOfCardBack);
    }

    /**
     * Sets demographics insurance photo of card back.
     *
     * @param demographicsInsurancePhotoOfCardBack the demographics insurance photo of card back
     */
    public void setDemographicsInsurancePhotoOfCardBack(String demographicsInsurancePhotoOfCardBack) {
        this.demographicsInsurancePhotoOfCardBack = demographicsInsurancePhotoOfCardBack;
    }

    /**
     * Gets demographics documents identification.
     *
     * @return the demographics documents identification
     */
    public String getDemographicsDocumentsIdentification() {
        return StringUtil.getLabelForView(demographicsDocumentsIdentification);
    }

    /**
     * Sets demographics documents identification.
     *
     * @param demographicsDocumentsIdentification the demographics documents identification
     */
    public void setDemographicsDocumentsIdentification(String demographicsDocumentsIdentification) {
        this.demographicsDocumentsIdentification = demographicsDocumentsIdentification;
    }

    /**
     * Gets demographics documents picture of front.
     *
     * @return the demographics documents picture of front
     */
    public String getDemographicsDocumentsPictureOfFront() {
        return StringUtil.getLabelForView(demographicsDocumentsPictureOfFront);
    }

    /**
     * Sets demographics documents picture of front.
     *
     * @param demographicsDocumentsPictureOfFront the demographics documents picture of front
     */
    public void setDemographicsDocumentsPictureOfFront(String demographicsDocumentsPictureOfFront) {
        this.demographicsDocumentsPictureOfFront = demographicsDocumentsPictureOfFront;
    }

    /**
     * Gets demographics documents picture of back.
     *
     * @return the demographics documents picture of back
     */
    public String getDemographicsDocumentsPictureOfBack() {
        return StringUtil.getLabelForView(demographicsDocumentsPictureOfBack);
    }

    /**
     * Sets demographics documents picture of back.
     *
     * @param demographicsDocumentsPictureOfBack the demographics documents picture of back
     */
    public void setDemographicsDocumentsPictureOfBack(String demographicsDocumentsPictureOfBack) {
        this.demographicsDocumentsPictureOfBack = demographicsDocumentsPictureOfBack;
    }

    public String getDemographicsConsentFormsTitle() {
        return StringUtil.getLabelForView(demographicsConsentFormsTitle);
    }

    public void setDemographicsConsentFormsTitle(String demographicsConsentFormsTitle) {
        this.demographicsConsentFormsTitle = demographicsConsentFormsTitle;
    }

    public String getDemographicsMedsAllergiesTitle() {
        return StringUtil.getLabelForView(demographicsMedsAllergiesTitle);
    }

    public void setDemographicsMedsAllergiesTitle(String demographicsMedsAllergiesTitle) {
        this.demographicsMedsAllergiesTitle = demographicsMedsAllergiesTitle;
    }

    public String getDemographicsPaymentTitle() {
        return StringUtil.getLabelForView(demographicsPaymentTitle);
    }

    public void setDemographicsPaymentTitle(String demographicsPaymentTitle) {
        this.demographicsPaymentTitle = demographicsPaymentTitle;
    }

    public String getPracticeChekinSectionIntakeForms() {
        return StringUtil.getLabelForView(practiceChekinSectionIntakeForms);
    }

    public void setPracticeChekinSectionIntakeForms(String practiceChekinSectionIntakeForms) {
        this.practiceChekinSectionIntakeForms = practiceChekinSectionIntakeForms;
    }

    public String getDemographicsPatientInformationTitle() {
        return StringUtil.getLabelForView(demographicsPatientInformationTitle);
    }

    public void setDemographicsPatientInformationTitle(String demographicsPatientInformationTitle) {
        this.demographicsPatientInformationTitle = demographicsPatientInformationTitle;
    }

    public String getDemographicsReviewNextButton() {
        return StringUtil.getLabelForView(demographicsReviewNextButton);
    }

    public void setDemographicsReviewNextButton(String demographicsReviewNextButton) {
        this.demographicsReviewNextButton = demographicsReviewNextButton;
    }

    public String getDemographicsReviewIdentification() {
        return StringUtil.getLabelForView(demographicsReviewIdentification);
    }

    public void setDemographicsReviewIdentification(String demographicsReviewIdentification) {
        this.demographicsReviewIdentification = demographicsReviewIdentification;
    }

    public String getDemographicsReviewTakePhotoOfFront() {
        return StringUtil.getLabelForView(demographicsReviewTakePhotoOfFront);
    }

    public void setDemographicsReviewTakePhotoOfFront(String demographicsReviewTakePhotoOfFront) {
        this.demographicsReviewTakePhotoOfFront = demographicsReviewTakePhotoOfFront;
    }

    public String getDemographicsReviewTakePhotoOfBack() {
        return StringUtil.getLabelForView(demographicsReviewTakePhotoOfBack);
    }

    public void setDemographicsReviewTakePhotoOfBack(String demographicsReviewTakePhotoOfBack) {
        this.demographicsReviewTakePhotoOfBack = demographicsReviewTakePhotoOfBack;
    }

    public String getDemographicsReviewDemographics() {
        return demographicsReviewDemographics;
    }

    public void setDemographicsReviewDemographics(String demographicsReviewDemographics) {
        this.demographicsReviewDemographics = demographicsReviewDemographics;
    }
}