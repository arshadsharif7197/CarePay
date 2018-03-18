package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeQueryStringDTO {

    @SerializedName("language")
    @Expose
    private PatientModeLanguageDTO language = new PatientModeLanguageDTO();

    /**
     *
     * @return
     * The language
     */
    public PatientModeLanguageDTO getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(PatientModeLanguageDTO language) {
        this.language = language;
    }

}