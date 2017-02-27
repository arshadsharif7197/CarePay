package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesLabelsDTO {

    @SerializedName("medication_allergies_titlebar_text")
    @Expose
    private String medicationAllergiesTitlebarText;
    @SerializedName("allergy_medication_header")
    @Expose
    private String allergyMedicationHeader;
    @SerializedName("allergy_medication_header_message")
    @Expose
    private String allergyMedicationHeaderMessage;
    @SerializedName("allergy_section_header")
    @Expose
    private String allergySectionHeader;
    @SerializedName("allergy_title")
    @Expose
    private String allergyTitle;
    @SerializedName("allergy_choose_button")
    @Expose
    private String allergyChooseButton;
    @SerializedName("allergy_none_placeholder_text")
    @Expose
    private String allergyNonePlaceholderText;
    @SerializedName("medications_section_header")
    @Expose
    private String medicationsSectionHeader;
    @SerializedName("medications_title")
    @Expose
    private String medicationsTitle;
    @SerializedName("medication_choose_button")
    @Expose
    private String medicationChooseButton;
    @SerializedName("medication_none_placeholder_text")
    @Expose
    private String medicationNonePlaceholderText;
    @SerializedName("medication_allergies_continue_button")
    @Expose
    private String medicationAllergiesContinueButton;
    @SerializedName("medicstion_allergies_delete_button")
    @Expose
    private String medicationAllergiesDeleteButton;

    public String getMedicationAllergiesTitlebarText() {
        return medicationAllergiesTitlebarText;
    }

    public void setMedicationAllergiesTitlebarText(String medicationAllergiesTitlebarText) {
        this.medicationAllergiesTitlebarText = medicationAllergiesTitlebarText;
    }

    public String getAllergyMedicationHeader() {
        return allergyMedicationHeader;
    }

    public void setAllergyMedicationHeader(String allergyMedicationHeader) {
        this.allergyMedicationHeader = allergyMedicationHeader;
    }

    public String getAllergyMedicationHeaderMessage() {
        return allergyMedicationHeaderMessage;
    }

    public void setAllergyMedicationHeaderMessage(String allergyMedicationHeaderMessage) {
        this.allergyMedicationHeaderMessage = allergyMedicationHeaderMessage;
    }

    public String getAllergySectionHeader() {
        return allergySectionHeader;
    }

    public void setAllergySectionHeader(String allergySectionHeader) {
        this.allergySectionHeader = allergySectionHeader;
    }

    public String getAllergyTitle() {
        return allergyTitle;
    }

    public void setAllergyTitle(String allergyTitle) {
        this.allergyTitle = allergyTitle;
    }

    public String getAllergyChooseButton() {
        return allergyChooseButton;
    }

    public void setAllergyChooseButton(String allergyChooseButton) {
        this.allergyChooseButton = allergyChooseButton;
    }

    public String getAllergyNonePlaceholderText() {
        return allergyNonePlaceholderText;
    }

    public void setAllergyNonePlaceholderText(String allergyNonePlaceholderText) {
        this.allergyNonePlaceholderText = allergyNonePlaceholderText;
    }

    public String getMedicationsSectionHeader() {
        return medicationsSectionHeader;
    }

    public void setMedicationsSectionHeader(String medicationsSectionHeader) {
        this.medicationsSectionHeader = medicationsSectionHeader;
    }

    public String getMedicationsTitle() {
        return medicationsTitle;
    }

    public void setMedicationsTitle(String medicationsTitle) {
        this.medicationsTitle = medicationsTitle;
    }

    public String getMedicationChooseButton() {
        return medicationChooseButton;
    }

    public void setMedicationChooseButton(String medicationChooseButton) {
        this.medicationChooseButton = medicationChooseButton;
    }

    public String getMedicationNonePlaceholderText() {
        return medicationNonePlaceholderText;
    }

    public void setMedicationNonePlaceholderText(String medicationNonePlaceholderText) {
        this.medicationNonePlaceholderText = medicationNonePlaceholderText;
    }

    public String getMedicationAllergiesContinueButton() {
        return medicationAllergiesContinueButton;
    }

    public void setMedicationAllergiesContinueButton(String medicationAllergiesContinueButton) {
        this.medicationAllergiesContinueButton = medicationAllergiesContinueButton;
    }

    public String getMedicationAllergiesDeleteButton() {
        return medicationAllergiesDeleteButton;
    }

    public void setMedicationAllergiesDeleteButton(String medicationAllergiesDeleteButton) {
        this.medicationAllergiesDeleteButton = medicationAllergiesDeleteButton;
    }
}
