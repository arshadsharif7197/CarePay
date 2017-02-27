package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/16/17.
 */

public class MedicationAllergiesPayload {

    @SerializedName("medications")
    @Expose
    private MedicationsPayload medications =  new MedicationsPayload();


    @SerializedName("searched_medications")
    @Expose
    private SearchMedicationsPayload searchMedications = new SearchMedicationsPayload();

    public MedicationsPayload getMedications() {
        return medications;
    }

    public void setMedications(MedicationsPayload medications) {
        this.medications = medications;
    }

    public SearchMedicationsPayload getSearchMedications() {
        return searchMedications;
    }

    public void setSearchMedications(SearchMedicationsPayload searchMedications) {
        this.searchMedications = searchMedications;
    }
}
