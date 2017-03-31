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

    @SerializedName("demographics_identity_text")
    @Expose
    private String demographicsIdentityText;

    @SerializedName("demographics_update_profile_photo_link")
    @Expose
    private String demographicsUpdateProfilePhotoLink;

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

    @SerializedName("practice_checin_edit_clickable_label")
    @Expose
    private String practiceCheckinEditClickableLabel;

    @SerializedName("demographics_insurance_label")
    @Expose
    private String demographicsInsuranceTitle;

    @SerializedName("demographics_insurance_payer_label")
    @Expose
    private String demographicsInsurancePayerLabel;

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

    @SerializedName("demographics_review_identification")
    @Expose
    private String demographicsReviewIdentification;

    /**
     * @return The demographicsAddressSection
     */
    public String getDemographicsAddressSection() {
        return StringUtil.getLabelForView(demographicsAddressSection);
    }

    /**
     * @return The demographicsDetailsSection
     */
    public String getDemographicsDetailsSection() {
        return StringUtil.getLabelForView(demographicsDetailsSection);
    }

    /**
     * @return The demographicsDocumentsSection
     */
    public String getDemographicsDocumentsSection() {
        return StringUtil.getLabelForView(demographicsDocumentsSection);
    }

    /**
     * @return The demographicsAllSetSection
     */
    public String getDemographicsAllSetSection() {
        return StringUtil.getLabelForView(demographicsAllSetSection);
    }

    public String getDemographicsNext() {
        return StringUtil.getLabelForView(demographicsNext);
    }

    public String getDemographicsAddressHeader() {
        return StringUtil.getLabelForView(demographicsAddressHeader);
    }

    public String getDemographicsAddressSubheader() {
        return StringUtil.getLabelForView(demographicsAddressSubheader);
    }

    public String getDemographicsRequired() {
        return StringUtil.getLabelForView(demographicsRequired);
    }

    public String getDemographicsDetailsHeader() {
        return StringUtil.getLabelForView(demographicsDetailsHeader);
    }

    public String getDemographicsDetailsSubheader() {
        return StringUtil.getLabelForView(demographicsDetailsSubheader);
    }

    public String getDemographicsDetailsDobHint() {
        return StringUtil.getLabelForView(demographicsDetailsDobHint);
    }

    public String getDemographicsChooseLabel() {
        return StringUtil.getLabelForView(demographicsChooseLabel);
    }

    public void setDemographicsChooseLabel(String demographicsChooseLabel) {
        this.demographicsChooseLabel = demographicsChooseLabel;
    }

    public String getDemographicsDocumentsSwitchLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsSwitchLabel);
    }

    public String getDemographicsDocumentsHeader() {
        return StringUtil.getLabelForView(demographicsDocumentsHeader);
    }

    public String getDemographicsDocumentsSubheader() {
        return StringUtil.getLabelForView(demographicsDocumentsSubheader);
    }

    public String getDemographicsDocumentsMultiInsLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsMultiInsLabel);
    }

    public String getDemographicsDocumentsScanFrontLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsScanFrontLabel);
    }

    public String getDemographicsDocumentsScanBackLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsScanBackLabel);
    }

    public String getDemographicsDocumentsRescanFrontLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsRescanFrontLabel);
    }

    public String getDemographicsDocumentsRescanBackLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsRescanBackLabel);
    }

    public String getDemographicsCancelLabel() {
        return StringUtil.getLabelForView(demographicsCancelLabel);
    }

    public String getDemographicsTitleSelectState() {
        return StringUtil.getLabelForView(demographicsTitleSelectState);
    }

    public String getDemographicsTitleSelectPlan() {
        return StringUtil.getLabelForView(demographicsTitleSelectPlan);
    }

    public String getDemographicsTitleSelectProvider() {
        return StringUtil.getLabelForView(demographicsTitleSelectProvider);
    }

    public String getDemographicsTitleSelectGender() {
        return StringUtil.getLabelForView(demographicsTitleSelectGender);
    }

    public String getDemographicsTitleSelectEthnicity() {
        return StringUtil.getLabelForView(demographicsTitleSelectEthnicity);
    }

    public String getDemographicsTitleSelectRace() {
        return StringUtil.getLabelForView(demographicsTitleSelectRace);
    }

    public String getDemographicsTitleSelectIdType() {
        return StringUtil.getLabelForView(demographicsTitleSelectIdType);
    }

    public String getDemographicsDocumentsChoosePlanLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsChoosePlanLabel);
    }

    public String getDemographicsDocumentsInsTypeLabel() {
        return StringUtil.getLabelForView(demographicsDocumentsInsTypeLabel);
    }

    public String getDemographicsTitleCardType() {
        return StringUtil.getLabelForView(demographicsTitleCardType);
    }

    public String getDemographicsAllSetHeader() {
        return StringUtil.getLabelForView(demographicsAllSetHeader);
    }

    public String getDemographicsAllSetGoButton() {
        return StringUtil.getLabelForView(demographicsAllSetGoButton);
    }

    /**
     * @return The demographicsUpdateInsuranceToolbarTitle
     */
    public String getDemographicsUpdateInsuranceToolbarTitle() {
        return StringUtil.getLabelForView(demographicsUpdateInsuranceToolbarTitle);
    }

    /**
     * @return The demographicsReviewUpdateButton
     */
    public String getDemographicsReviewUpdateButton() {
        return StringUtil.getLabelForView(demographicsReviewUpdateButton);
    }

    /**
     * @return The demographicsInsuranceUpdateButton
     */
    public String getDemographicsInsuranceUpdateButton() {
        return StringUtil.getLabelForView(demographicsInsuranceUpdateButton);
    }

    /**
     * @return The demographicsReviewCorrectButton
     */
    public String getDemographicsReviewCorrectButton() {
        return StringUtil.getLabelForView(demographicsReviewCorrectButton);
    }

    /**
     * @return The demographicsReviewPeronsonalinfoSection
     */
    public String getDemographicsReviewPeronsonalinfoSection() {
        return StringUtil.getLabelForView(demographicsReviewPeronsonalinfoSection);
    }

    /**
     * @return The demographicsReviewScreenTitle
     */
    public String getDemographicsReviewScreenTitle() {
        return StringUtil.getLabelForView(demographicsReviewScreenTitle);
    }

    /**
     * @return The demographicsUpadteInsuranceScreenTitle
     */
    public String getDemographicsUpdateInsuranceScreenTitle() {
        return StringUtil.getLabelForView(demographicsUpdateInsuranceScreenTitle);
    }

    /**
     * @return The demographicsReviewScreenSubtitle
     */
    public String getDemographicsReviewScreenSubtitle() {
        return StringUtil.getLabelForView(demographicsReviewScreenSubtitle);
    }

    /**
     * @return The demographicsHelathinsurance1Section
     */
    public String getDemographicsHealthinsurance1Section() {
        return StringUtil.getLabelForView(demographicsHealthinsurance1Section) ;
    }

    /**
     * @return The demographicsHelathinsurance2Section
     */
    public String getDemographicsHealthinsurance2Section() {
        return StringUtil.getLabelForView(demographicsHealthinsurance2Section);
    }

    /**
     * @return The demographicsHelathinsurance3Section
     */
    public String getDemographicsHealthinsurance3Section() {
        return StringUtil.getLabelForView(demographicsHealthinsurance3Section);
    }

    /**
     * @return The section title
     */
    public String getDemographicSectionTitle() {
        return StringUtil.getLabelForView(demographicSectionTitle);
    }

    /**
     * @return The label
     */
    public String getDocumentsRemove() {
        return StringUtil.getLabelForView(documentsRemove);

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
     * Gets practice checkin edit clickable label.
     *
     * @return the practice checkin edit clickable label
     */
    public String getPracticeCheckinEditClickableLabel() {
        return StringUtil.getLabelForView(practiceCheckinEditClickableLabel);
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
     * Gets demographics insurance payer label.
     *
     * @return the demographics insurance payer label
     */
    public String getDemographicsInsurancePayerLabel() {
        return StringUtil.getLabelForView(demographicsInsurancePayerLabel);
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
     * Gets demographics insurance photo of card front.
     *
     * @return the demographics insurance photo of card front
     */
    public String getDemographicsInsurancePhotoOfCardFront() {
        return StringUtil.getLabelForView(demographicsInsurancePhotoOfCardFront);
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
     * Gets demographics documents identification.
     *
     * @return the demographics documents identification
     */
    public String getDemographicsDocumentsIdentification() {
        return StringUtil.getLabelForView(demographicsDocumentsIdentification);
    }

    public String getDemographicsReviewIdentification() {
        return StringUtil.getLabelForView(demographicsReviewIdentification);
    }
}