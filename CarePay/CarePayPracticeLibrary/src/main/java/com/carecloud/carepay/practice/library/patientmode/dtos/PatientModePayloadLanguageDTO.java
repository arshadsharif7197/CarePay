package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class PatientModePayloadLanguageDTO {

    @SerializedName("options")
    @Expose
    private List<PatientModeOptionDTO> options = new ArrayList<PatientModeOptionDTO>();

    /**
     *
     * @return
     * The options
     */
    public List<PatientModeOptionDTO> getOptions() {
        return options;
    }

    /**
     *
     * @param options
     * The options
     */
    public void setOptions(List<PatientModeOptionDTO> options) {
        this.options = options;
    }

}