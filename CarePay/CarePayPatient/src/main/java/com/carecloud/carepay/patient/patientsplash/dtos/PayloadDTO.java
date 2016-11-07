package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadDTO {

    @SerializedName("language_selection")
    @Expose
    private LanguageSelectionDTO languageSelection;

    /**
     * @return The languageSelection
     */
    public LanguageSelectionDTO getLanguageSelection() {
        return languageSelection;
    }

    /**
     * @param languageSelection The language_selection
     */
    public void setLanguageSelection(LanguageSelectionDTO languageSelection) {
        this.languageSelection = languageSelection;
    }

}