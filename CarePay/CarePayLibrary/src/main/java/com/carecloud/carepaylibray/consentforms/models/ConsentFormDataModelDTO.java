package com.carecloud.carepaylibray.consentforms.models;


import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 10/21/16
 */
public class ConsentFormDataModelDTO {

    @SerializedName("practice_forms")
    @Expose
    private List<PracticeForm> practiceForms = new ArrayList<>();

    public List<PracticeForm> getPracticeForms() {
        return practiceForms;
    }

    public void setPracticeForms(List<PracticeForm> practiceForms) {
        this.practiceForms = practiceForms;
    }


}
