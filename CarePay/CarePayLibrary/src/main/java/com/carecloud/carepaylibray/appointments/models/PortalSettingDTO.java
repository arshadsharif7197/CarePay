package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 11/8/18.
 */
public class PortalSettingDTO {

    @Expose
    @SerializedName("payload")
    private List<PortalSetting> payload;

    @Expose
    @SerializedName("metadata")
    private PortalSettingMetadata metadata;

    public List<PortalSetting> getPayload() {
        return payload;
    }

    public void setPayload(List<PortalSetting> payload) {
        this.payload = payload;
    }

    public PortalSettingMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PortalSettingMetadata metadata) {
        this.metadata = metadata;
    }
}
