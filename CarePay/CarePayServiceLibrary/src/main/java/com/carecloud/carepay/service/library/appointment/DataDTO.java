package com.carecloud.carepay.service.library.appointment;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataDTO {

    @SerializedName("cancellation_comments")
    @Expose
    private CancellationCommentsDTO cancellationComments = new CancellationCommentsDTO();
    @SerializedName("cancellation_reason_id")
    @Expose
    private CancellationReasonIdDTO cancellationReasonId = new CancellationReasonIdDTO();

    /**
     * @return The cancellationComments
     */
    public CancellationCommentsDTO getCancellationComments() {
        return cancellationComments;
    }

    /**
     * @param cancellationComments The cancellation_comments
     */
    public void setCancellationComments(CancellationCommentsDTO cancellationComments) {
        this.cancellationComments = cancellationComments;
    }

    /**
     * @return The cancellationReasonId
     */
    public CancellationReasonIdDTO getCancellationReasonId() {
        return cancellationReasonId;
    }

    /**
     * @param cancellationReasonId The cancellation_reason_id
     */
    public void setCancellationReasonId(CancellationReasonIdDTO cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
    }
}
