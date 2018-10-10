package com.carecloud.carepaylibray.survey.model;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyPayload {

    @Expose
    @SerializedName("survey")
    private SurveyModel survey;

    @Expose
    @SerializedName("surveys_settings")
    private SurveySettings surveySettings = new SurveySettings();

    @Expose
    @SerializedName("languages")
    private List<OptionDTO> languages;

    @SerializedName("practice_information")
    @Expose
    private List<UserPracticeDTO> practiceInformation = new ArrayList<>();

    public SurveyModel getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyModel survey) {
        this.survey = survey;
    }

    public SurveySettings getSurveySettings() {
        return surveySettings;
    }

    public void setSurveySettings(SurveySettings surveySettings) {
        this.surveySettings = surveySettings;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    public List<UserPracticeDTO> getPracticeInformation() {
        return practiceInformation;
    }

    public void setPracticeInformation(List<UserPracticeDTO> practiceInformation) {
        this.practiceInformation = practiceInformation;
    }
}
