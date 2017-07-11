package com.carecloud.carepay.practice.library.adhocforms;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 5/07/17.
 */

public class SelectedAdHocForms implements Serializable {

    @SerializedName("adhoc_forms")
    private List<String> forms = new ArrayList<>();

    public List<String> getForms() {
        return forms;
    }

    public void setForms(List<String> forms) {
        this.forms = forms;
    }
}
