package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/21/2017.
 */

public class DemographicsSettingsHeaderDTO {

        @SerializedName("maintenance")
        @Expose
        private DemographicsSettingsMaintainanceDTO maintenance = new DemographicsSettingsMaintainanceDTO();

        public DemographicsSettingsMaintainanceDTO getMaintenance() {
            return maintenance;
        }

        public void setMaintenance(DemographicsSettingsMaintainanceDTO maintenance) {
            this.maintenance = maintenance;
        }
}


