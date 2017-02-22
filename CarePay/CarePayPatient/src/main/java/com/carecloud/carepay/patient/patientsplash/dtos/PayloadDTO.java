package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PayloadDTO {

    @SerializedName("language_selection")
    @Expose
    private LanguageSelectionDTO languageSelection = new LanguageSelectionDTO();

    @SerializedName("languages")
    @Expose
    private List<OptionsDTO> languages = new ArrayList<>();

    /**
     *
     * @return
     * The languages
     */
    public List<OptionsDTO> getLanguages() {
        return languages;
    }

    /**
     *
     * @param languages
     * The languages
     */
    public void setLanguages(List<OptionsDTO> languages) {
        this.languages = languages;
    }



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