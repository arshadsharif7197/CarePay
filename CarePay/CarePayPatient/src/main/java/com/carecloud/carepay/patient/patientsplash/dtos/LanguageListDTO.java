package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class LanguageListDTO {

    @SerializedName("options")
    @Expose
    private List<OptionsDTO> options = new ArrayList<>();

    /**
     * @return The options
     */
    public List<OptionsDTO> getOptions() {
        return options;
    }

    /**
     * @param options The options
     */
    public void setOptions(List<OptionsDTO> options) {
        this.options = options;
    }

}