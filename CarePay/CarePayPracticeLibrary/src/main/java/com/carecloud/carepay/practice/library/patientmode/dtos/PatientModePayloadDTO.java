package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/26/16.
 */

import com.carecloud.carepay.practice.library.signin.dtos.LanguageOptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PatientModePayloadDTO {

    @SerializedName("patient_mode_start")
    @Expose
    private PatientModesStartDTO patientModeStart = new PatientModesStartDTO();
    @SerializedName("languages")
    @Expose
    private List<LanguageOptionDTO> languages = new ArrayList<>();
    @SerializedName("practice")
    @Expose
    private PracticeWelcomeDTO practice = new PracticeWelcomeDTO();

    /**
     *
     * @return
     * The languages
     */
    public List<LanguageOptionDTO> getLanguages() {
        return languages;
    }

    /**
     *
     * @param languages
     * The languages
     */
    public void setLanguages(List<LanguageOptionDTO> languages) {
        this.languages = languages;
    }

    /**
     * @return The patientModeStart
     */
    public PatientModesStartDTO getPatientModeStart() {
        return patientModeStart;
    }

    /**
     * @param patientModeStart The patient_mode_start
     */
    public void setPatientModeStart(PatientModesStartDTO patientModeStart) {
        this.patientModeStart = patientModeStart;
    }

    public PracticeWelcomeDTO getPractice() {
        return practice;
    }

    public void setPractice(PracticeWelcomeDTO practice) {
        this.practice = practice;
    }
}