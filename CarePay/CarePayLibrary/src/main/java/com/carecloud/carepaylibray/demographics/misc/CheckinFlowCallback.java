package com.carecloud.carepaylibray.demographics.misc;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;

public interface CheckinFlowCallback {
    void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step);

    Integer getCurrentStep();

    void setCurrentStep(Integer step);

    void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage);

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    void displayCheckInSuccess(WorkflowDTO workflowDTO);

    String getAppointmentId();

    AppointmentDTO getAppointment();

}
