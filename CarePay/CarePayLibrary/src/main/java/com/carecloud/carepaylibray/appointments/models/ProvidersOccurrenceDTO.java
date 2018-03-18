
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProvidersOccurrenceDTO {

    @SerializedName("start_at")
    @Expose
    private String startAt;
    @SerializedName("end_at")
    @Expose
    private String endAt;

    /**
     * 
     * @return
     *     The startAt
     */
    public String getStartAt() {
        return startAt;
    }

    /**
     * 
     * @param startAt
     *     The start_at
     */
    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    /**
     * 
     * @return
     *     The endAt
     */
    public String getEndAt() {
        return endAt;
    }

    /**
     * 
     * @param endAt
     *     The end_at
     */
    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

}
