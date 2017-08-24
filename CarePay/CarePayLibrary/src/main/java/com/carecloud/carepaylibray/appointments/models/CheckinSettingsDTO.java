package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 8/23/17
 */

public class CheckinSettingsDTO {

    @SerializedName("ask_medications")
    private boolean showMedications = true;

    @SerializedName("ask_allergies")
    private boolean showAllergies = true;

    @SerializedName("identity_documents_required")
    private boolean showIdentityDocs = true;

    @SerializedName("health_insurance_required")
    private boolean showHealthInsurance = true;

    public boolean shouldShowMedications() {
        return showMedications;
    }

    public void setShowMedications(boolean showMedications) {
        this.showMedications = showMedications;
    }

    public boolean shouldShowAllergies() {
        return showAllergies;
    }

    public void setShowAllergies(boolean showAllergies) {
        this.showAllergies = showAllergies;
    }

    public boolean shouldShowIdentityDocs() {
        return showIdentityDocs;
    }

    public void setShowIdentityDocs(boolean showIdentityDocs) {
        this.showIdentityDocs = showIdentityDocs;
    }

    public boolean shouldShowHealthInsurance() {
        return showHealthInsurance;
    }

    public void setShowHealthInsurance(boolean showHealthInsurance) {
        this.showHealthInsurance = showHealthInsurance;
    }
}
