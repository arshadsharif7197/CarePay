package com.carecloud.carepay.practice.library.patientmodecheckin.interfaces;

/**
 * Created by lmenendez on 3/5/17.
 */

public interface CheckinFlowCallback {
    void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage);
}
