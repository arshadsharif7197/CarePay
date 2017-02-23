package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */


import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ConsentFormDataModelDTO {

    @SerializedName("post")
    @Expose
    private ConsentFormPostDTO post = new ConsentFormPostDTO();

    @SerializedName("practice_forms")
    @Expose
    private List<PracticeForm> practiceForms = new ArrayList<>();

    public List<PracticeForm> getPracticeForms() {
        return practiceForms;
    }

    public void setPracticeForms(List<PracticeForm> practiceForms) {
        this.practiceForms = practiceForms;
    }


    /**
     * @return The post
     */
    public ConsentFormPostDTO getPost() {
        return post;
    }

    /**
     * @param post The post
     */
    public void setPost(ConsentFormPostDTO post) {
        this.post = post;
    }

}
