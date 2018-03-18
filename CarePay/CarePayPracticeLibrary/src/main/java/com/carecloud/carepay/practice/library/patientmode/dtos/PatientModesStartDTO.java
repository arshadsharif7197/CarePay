package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModesStartDTO {


    @SerializedName("language")
    @Expose
    private PatientModePayloadLanguageDTO language = new PatientModePayloadLanguageDTO();

    /**
     * @return The language
     */
    public PatientModePayloadLanguageDTO getLanguage() {
        return language;
    }

    /**
     * @param language The language
     */
    public void setLanguage(PatientModePayloadLanguageDTO language) {
        this.language = language;
    }

}