package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/16/17
 */

public class MedicationsPostModel {

    @SerializedName("medications_array")
    private List<MedicationsObject> medicationsList = new ArrayList<>();

    @SerializedName("allergies_array")
    private List<AllergiesObject> allergiesList = new ArrayList<>();

    @SerializedName("medications_image")
    private MedicationsImagePostModel medicationsImage;

    public List<MedicationsObject> getMedicationsList() {
        return medicationsList;
    }

    public void setMedicationsList(List<MedicationsObject> medicationsList) {
        this.medicationsList = medicationsList;
    }

    public List<AllergiesObject> getAllergiesList() {
        return allergiesList;
    }

    public void setAllergiesList(List<AllergiesObject> allergiesList) {
        this.allergiesList = allergiesList;
    }

    public MedicationsImagePostModel getMedicationsImage() {
        return medicationsImage;
    }

    public void setMedicationsImage(MedicationsImagePostModel medicationsImage) {
        this.medicationsImage = medicationsImage;
    }
}
