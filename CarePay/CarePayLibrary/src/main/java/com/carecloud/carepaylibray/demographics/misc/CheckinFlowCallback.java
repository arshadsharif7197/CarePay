package com.carecloud.carepaylibray.demographics.misc;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;

public interface CheckinFlowCallback {
    int MAX_STEPS = 5;
    int PERSONAL_INFO = 1;
    int ADDRESS = 2;
    int DEMOGRAPHICS = 3;
    int IDENTITY = 4;
    int INSURANCE = 5;


    void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step);

    int getCurrentStep();

    int getTotalSteps();

    int hasStep(int step);

    void setCurrentStep(Integer step);

    void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage);

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    void displayCheckInSuccess(WorkflowDTO workflowDTO);

    String getAppointmentId();

    AppointmentDTO getAppointment();

    DemographicDTO getDemographicDTO();

}
