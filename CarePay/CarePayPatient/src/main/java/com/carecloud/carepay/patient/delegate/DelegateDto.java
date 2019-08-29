package com.carecloud.carepay.patient.delegate;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-13.
 */
public class DelegateDto implements DTO {

    @SerializedName("payload")
    @Expose
    DelegatePayload payload = new DelegatePayload();

    public DelegatePayload getPayload() {
        return payload;
    }

    public void setPayload(DelegatePayload payload) {
        this.payload = payload;
    }
}
