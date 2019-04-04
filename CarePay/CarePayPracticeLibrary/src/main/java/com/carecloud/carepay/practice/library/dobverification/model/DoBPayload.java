package com.carecloud.carepay.practice.library.dobverification.model;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBPayload {

    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographicDTO = new DemographicsSettingsDemographicsDTO();
    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();

    public DemographicsSettingsDemographicsDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicsSettingsDemographicsDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }
}
