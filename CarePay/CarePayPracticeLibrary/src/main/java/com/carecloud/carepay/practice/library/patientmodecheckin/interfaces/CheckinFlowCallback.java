
package com.carecloud.carepay.practice.library.patientmodecheckin.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

/**
 * Created by lmenendez on 3/5/17.
 */

public interface CheckinFlowCallback {
    void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage);
    void displayCheckinSuccess(WorkflowDTO workflowDTO);
}