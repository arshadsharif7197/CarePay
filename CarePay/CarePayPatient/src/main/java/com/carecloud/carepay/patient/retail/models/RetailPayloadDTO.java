package com.carecloud.carepay.patient.retail.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailPayloadDTO {

    @SerializedName("demographics")
    private DemographicPayloadInfoDTO demographicDTO = new DemographicPayloadInfoDTO();

    @SerializedName("practice_patient_ids")
    private List<RetailPracticeDTO> retailPracticeList = new ArrayList<>();

    public DemographicPayloadInfoDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicPayloadInfoDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<RetailPracticeDTO> getRetailPracticeList() {
        return retailPracticeList;
    }

    public void setRetailPracticeList(List<RetailPracticeDTO> retailPracticeList) {
        this.retailPracticeList = retailPracticeList;
    }

}
