package com.carecloud.carepaylibray.demographics.misc;

import android.content.DialogInterface;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;

public interface CheckinFlowCallback {
    void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step);

    Integer getCurrentStep();

    void setCurrentStep(Integer step);

    void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage);

    void navigateToConsentFlow(WorkflowDTO workflowDTO);

    void displayCheckInSuccess(WorkflowDTO workflowDTO);

}
