package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormDataModelDTO {

    @SerializedName("post")
    @Expose
    private ConsentFormPostDTO post;

    /**
     *
     * @return
     * The post
     */
    public ConsentFormPostDTO getPost() {
        return post;
    }

    /**
     *
     * @param post
     * The post
     */
    public void setPost(ConsentFormPostDTO post) {
        this.post = post;
    }

}
