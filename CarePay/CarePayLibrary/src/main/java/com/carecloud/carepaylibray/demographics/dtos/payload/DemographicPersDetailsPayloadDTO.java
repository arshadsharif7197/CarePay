package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * MOdel for personal details payload.
 */
public class DemographicPersDetailsPayloadDTO extends PatientModel {


    @SerializedName("add_unlisted_allergies") @Expose
    private String addUnlistedAllergies;

    @SerializedName("add_unlisted_medications") @Expose
    private String addUnlistedMedications;

    /**
     * @return The addUnlistedAllergies
     */
    public String getAddUnlistedAllergies() {
        return addUnlistedAllergies;
    }

    /**
     * @param addUnlistedAllergies The add_unlisted_allergies
     */
    public void setAddUnlistedAllergies(String addUnlistedAllergies) {
        this.addUnlistedAllergies = addUnlistedAllergies;
    }

    /**
     * @return The addUnlistedMedications
     */
    public String getAddUnlistedMedications() {
        return addUnlistedMedications;
    }

    /**
     * @param addUnlistedMedications The add_unlisted_medications
     */
    public void setAddUnlistedMedications(String addUnlistedMedications) {
        this.addUnlistedMedications = addUnlistedMedications;
    }
}
