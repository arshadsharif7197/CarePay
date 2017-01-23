package com.carecloud.carepaylibray.demographicsettings.models;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsPatientCreditCardsDTO {

    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsCreditCardMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private List<DemographicsSettingsCreditCardsPayloadDTO> payload = null;

    public DemographicsSettingsCreditCardMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicsSettingsCreditCardMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<DemographicsSettingsCreditCardsPayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<DemographicsSettingsCreditCardsPayloadDTO> payload) {
        this.payload = payload;
    }

}

