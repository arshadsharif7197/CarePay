package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SigninLanguageDTO {
    @SerializedName("options")
    @Expose
    private List<LanguageOptionDTO> options = new ArrayList<>();

    /**
     *
     * @return
     * The options
     */
    public List<LanguageOptionDTO> getOptions() {
        return options;
    }

    /**
     *
     * @param options
     * The options
     */
    public void setOptions(List<LanguageOptionDTO> options) {
        this.options = options;
    }
}
