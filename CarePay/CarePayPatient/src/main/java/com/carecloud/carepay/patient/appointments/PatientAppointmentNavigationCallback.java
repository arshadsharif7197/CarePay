package com.carecloud.carepay.patient.appointments;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentFlowInterface;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;

/**
 * Created by lmenendez on 5/9/17.
 */

public interface PatientAppointmentNavigationCallback extends AppointmentNavigationCallback,
        AppointmentPrepaymentCallback, AppointmentFlowInterface {

    void onCancelAppointment(AppointmentDTO appointmentDTO);

    void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment);

    void onCheckInStarted(AppointmentDTO appointmentDTO);

    void onCheckInOfficeStarted(AppointmentDTO appointmentDTO);

    void getQueueStatus(AppointmentDTO appointmentDTO, WorkflowServiceCallback callback);

    AppointmentsSettingDTO getAppointmentSettings(String practiceId);

    void callVisitSummaryService(AppointmentDTO appointment, WorkflowServiceCallback callback);

    void callVisitSummaryStatusService(String jobId, String practiceMgmt, WorkflowServiceCallback callback);

    long downloadVisitSummaryFile(String jobId, String practiceMgmt, String title);

}
