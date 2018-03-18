package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/16/17.
 */

public class SearchMedicationsPayload {

    @SerializedName("payload")
    @Expose
    private List<MedicationsObject> medicationsObjects = new ArrayList<>();

    public List<MedicationsObject> getMedicationsObjects() {
        return medicationsObjects;
    }

    public void setMedicationsObjects(List<MedicationsObject> medicationsObjects) {
        this.medicationsObjects = medicationsObjects;
    }
}
