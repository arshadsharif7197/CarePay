package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 7/04/17.
 */

public class DemographicSettingsNotificationDTO {

    @Expose
    private DemographicNotificationMetadataDTO metadata = new DemographicNotificationMetadataDTO();

    @Expose
    private DemographicNotificationPayloadDTO payload = new DemographicNotificationPayloadDTO();

    public DemographicNotificationMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicNotificationMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicNotificationPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicNotificationPayloadDTO payload) {
        this.payload = payload;
    }
}
