package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionLabels {

    @SerializedName("practice_list_select_a_business")
    @Expose
    private String title;

    @SerializedName("practice_list_continue")
    @Expose
    private String continueButton;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContinueButton() {
        return continueButton;
    }

    public void setContinueButton(String continueButton) {
        this.continueButton = continueButton;
    }
}
