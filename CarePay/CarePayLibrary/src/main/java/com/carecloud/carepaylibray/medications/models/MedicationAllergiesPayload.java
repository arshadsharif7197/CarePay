package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepaylibray.appointments.models.CheckinSettingsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/16/17
 */

public class MedicationAllergiesPayload {

    @SerializedName("medications")
    @Expose
    private MedicationsPayload medications = new MedicationsPayload();

    @SerializedName("allergies")
    @Expose
    private AllergiesPayload allergies = new AllergiesPayload();

    @SerializedName("medications_image")
    @Expose
    private MedicationsImage medicationsImage = new MedicationsImage();

    @SerializedName("searched_medications")
    @Expose
    private SearchMedicationsPayload searchMedications = new SearchMedicationsPayload();

    @SerializedName("searched_allergies")
    @Expose
    private SearchedAllergiesPayload searchedAllergies = new SearchedAllergiesPayload();

    @SerializedName("checkin_settings")
    @Expose
    private CheckinSettingsDTO checkinSettings = new CheckinSettingsDTO();

    public MedicationsPayload getMedications() {
        return medications;
    }

    public void setMedications(MedicationsPayload medications) {
        this.medications = medications;
    }

    public AllergiesPayload getAllergies() {
        return allergies;
    }

    public void setAllergies(AllergiesPayload allergies) {
        this.allergies = allergies;
    }

    public SearchMedicationsPayload getSearchMedications() {
        return searchMedications;
    }

    public void setSearchMedications(SearchMedicationsPayload searchMedications) {
        this.searchMedications = searchMedications;
    }

    public MedicationsImage getMedicationsImage() {
        return medicationsImage;
    }

    public void setMedicationsImage(MedicationsImage medicationsImage) {
        this.medicationsImage = medicationsImage;
    }

    public SearchedAllergiesPayload getSearchedAllergies() {
        return searchedAllergies;
    }

    public void setSearchedAllergies(SearchedAllergiesPayload searchedAllergies) {
        this.searchedAllergies = searchedAllergies;
    }

    public CheckinSettingsDTO getCheckinSettings() {
        return checkinSettings;
    }

    public void setCheckinSettings(CheckinSettingsDTO checkinSettings) {
        this.checkinSettings = checkinSettings;
    }
}
