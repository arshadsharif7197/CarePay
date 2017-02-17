package com.carecloud.carepaylibray.demographics.dtos.payload;

/**
 * Created by Rahul on 11/11/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentPayloadDTO {


    @SerializedName("metadata")
    @Expose
    private AppointmentMetaDataDTO metadata = new AppointmentMetaDataDTO();


    /**
     * @return The metadata
     */
    public AppointmentMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(AppointmentMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */


}