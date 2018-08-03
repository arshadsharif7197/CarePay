package com.carecloud.carepaylibray.consentforms.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author pjohnson on 2/08/18.
 */
public class PendingFormsDTO {

    @Expose
    @SerializedName("forms")
    private ArrayList<PendingFormDTO> forms = new ArrayList<>();

    public ArrayList<PendingFormDTO> getForms() {
        return forms;
    }

    public void setForms(ArrayList<PendingFormDTO> forms) {
        this.forms = forms;
    }
}
