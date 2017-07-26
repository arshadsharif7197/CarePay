package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthDto implements DTO {

    @SerializedName("metadata")
    @Expose
    private MyHealthMetadataDto metadata = new MyHealthMetadataDto();

    @SerializedName("payload")
    @Expose
    private MyHealthPayloadDto payload = new MyHealthPayloadDto();

    public MyHealthMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(MyHealthMetadataDto metadata) {
        this.metadata = metadata;
    }

    public MyHealthPayloadDto getPayload() {
        return payload;
    }

    public void setPayload(MyHealthPayloadDto payload) {
        this.payload = payload;
    }
}
