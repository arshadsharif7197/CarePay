package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/23/17
 */

public class RegistrationDataModel {

    @SerializedName("metadata")
    private MetadataDTO metadata = new MetadataDTO();

    @SerializedName("payload")
    private RegistrationPayloadDTO payloadDTO = new RegistrationPayloadDTO();

    public MetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDTO metadata) {
        this.metadata = metadata;
    }


    public RegistrationPayloadDTO getPayloadDTO() {
        return payloadDTO;
    }

    public void setPayloadDTO(RegistrationPayloadDTO payloadDTO) {
        this.payloadDTO = payloadDTO;
    }

    public void merge(RegistrationDataModel registrationDataModel){
        setPayloadDTO(registrationDataModel.getPayloadDTO());
        this.metadata.merge(registrationDataModel.getMetadata());
    }
}
