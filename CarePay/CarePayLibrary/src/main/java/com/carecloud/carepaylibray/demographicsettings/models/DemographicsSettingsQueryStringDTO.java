
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsQueryStringDTO {

    @SerializedName("practice_mgmt")
    @Expose
    private DemographicsSettingsPracticeMgmtDTO demographicsSettingsPracticeMgmtDTO = new DemographicsSettingsPracticeMgmtDTO();
    @SerializedName("practice_id")
    @Expose
    private DemographicsSettingsPracticeIdDTO demographicsSettingsPracticeIdDTO = new DemographicsSettingsPracticeIdDTO();

    public DemographicsSettingsPracticeMgmtDTO getPracticeMgmt() {
        return demographicsSettingsPracticeMgmtDTO;
    }

    public void setPracticeMgmt(DemographicsSettingsPracticeMgmtDTO demographicsSettingsPracticeMgmtDTO) {
        this.demographicsSettingsPracticeMgmtDTO = demographicsSettingsPracticeMgmtDTO;
    }

    public DemographicsSettingsPracticeIdDTO getPracticeId() {
        return demographicsSettingsPracticeIdDTO;
    }

    public void setPracticeId(DemographicsSettingsPracticeIdDTO demographicsSettingsPracticeIdDTO) {
        this.demographicsSettingsPracticeIdDTO = demographicsSettingsPracticeIdDTO;
    }

}
