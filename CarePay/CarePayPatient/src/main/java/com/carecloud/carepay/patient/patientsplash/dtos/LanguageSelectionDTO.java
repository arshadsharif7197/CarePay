package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageSelectionDTO {

    @SerializedName("language")
    @Expose
    private LanguageListDTO language = new LanguageListDTO();

    /**
     * @return The language
     */
    public LanguageListDTO getLanguage() {
        return language;
    }

    /**
     * @param language The language
     */
    public void setLanguage(LanguageListDTO language) {
        this.language = language;
    }

}