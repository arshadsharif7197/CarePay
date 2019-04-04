package com.carecloud.carepaylibray.appointments.createappointment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 1/17/19.
 */
public class BaseRequestAppointmentDialogFragment extends BaseDialogFragment {

    protected ScheduleAppointmentInterface callback;
    protected AppointmentsResultModel appointmentModelDto;
    protected AppointmentDTO appointmentDTO;
    protected UserPracticeDTO selectedPractice;
    protected boolean autoScheduleAppointments;
    protected String patientId;
    protected Button requestAppointmentButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
        } else if (context instanceof ScheduleAppointmentInterface) {
            callback = (ScheduleAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement AppointmentViewHandler.");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentModelDto = (AppointmentsResultModel) callback.getDto();
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
        selectedPractice = getPractice(appointmentModelDto.getPayload().getAppointmentAvailability()
                .getMetadata().getPracticeId());
        patientId = getPatientId(selectedPractice.getPracticeId());
    }

    protected void requestAppointment(AppointmentDTO appointmentDto) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());

        ScheduleAppointmentRequestDTO appointmentRequestDto = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = appointmentRequestDto.getAppointment();
        appointment.setStartTime(appointmentDto.getPayload().getStartTime());
        appointment.setEndTime(appointmentDto.getPayload().getEndTime());
        appointment.setLocationId(appointmentDto.getPayload().getLocation().getId());
        appointment.setLocationGuid(appointmentDto.getPayload().getLocation().getGuid());
        appointment.setProviderId(appointmentDto.getPayload().getProvider().getId());
        appointment.setProviderGuid(appointmentDto.getPayload().getProvider().getGuid());
        appointment.setVisitReasonId(appointmentDto.getPayload().getVisitType().getId());
        appointment.setResourceId(appointmentDto.getPayload().getResource().getId());
        appointment.setComplaint(appointmentDto.getPayload().getReasonForVisit());
        appointment.setComments(appointmentDto.getPayload().getReasonForVisit());
        appointment.getPatient().setId(patientId);

        double amount = appointmentDto.getPayload().getVisitType().getAmount();
        if (amount > 0) {
            startPrepaymentProcess(appointmentRequestDto, amount);
        } else {
            requestAppointmentButton.setEnabled(false);
            callRequestAppointmentService(queryMap, appointmentRequestDto);
        }
    }

    private void callRequestAppointmentService(Map<String, String> queryMap,
                                               final ScheduleAppointmentRequestDTO appointmentRequestDto) {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = appointmentModelDto.getMetadata().getTransitions().getMakeAppointment();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        requestAppointmentButton.setEnabled(true);
                        String appointmentRequestSuccessMessage = Label.getLabel(autoScheduleAppointments ?
                                "appointment_schedule_success_message_HTML" :
                                "appointment_request_success_message_HTML");
                        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
                        logMixPanelAppointmentRequestedEvent(appointmentDTO);
                        callback.appointmentScheduledSuccessfully();
                        dismiss();
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        hideProgressDialog();
                        requestAppointmentButton.setEnabled(true);
                        showErrorNotification(exceptionMessage);
                        if (isVisible()) {
                            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
                        }
                    }
                },
                gson.toJson(appointmentRequestDto), queryMap);
    }

    protected void logMixPanelAppointmentRequestedEvent(AppointmentDTO appointmentRequestDto) {
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_provider_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_location_id),
                getString(R.string.param_reason_visit)
        };
        Object[] values = {appointmentDTO.getPayload().getVisitType().getName(),
                appointmentDTO.getMetadata().getPracticeId(),
                getPracticeName(selectedPractice.getPracticeId()),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getMetadata().getPatientId(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                appointmentDTO.getPayload().getComments()
        };

        if (getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE)) {
            MixPanelUtil.logEvent(getString(R.string.event_appointment_scheduled), params, values);
        } else {
            MixPanelUtil.logEvent(getString(R.string.event_appointment_requested), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_appointment_requested), 1);
        }
    }

    private String getPracticeName(String practiceId) {
        return appointmentModelDto.getPayload().getPractice(practiceId).getPracticeName();
    }


    private UserPracticeDTO getPractice(String practiceId) {
        for (UserPracticeDTO practiceDTO : appointmentModelDto.getPayload().getUserPractices()) {
            if (practiceId.equals(practiceDTO.getPracticeId())) {
                return practiceDTO;
            }
        }
        return null;
    }

    private String getPatientId(String practiceId) {
        PracticePatientIdsDTO[] practicePatientIdArray = ApplicationPreferences.getInstance()
                .getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS,
                        PracticePatientIdsDTO[].class);
        if (practicePatientIdArray != null) {
            for (PracticePatientIdsDTO practicePatientId : practicePatientIdArray) {
                if (practicePatientId.getPracticeId().equals(practiceId)) {
                    return practicePatientId.getPatientId();
                }
            }
        }
        //Only valid for patient mode
        return ApplicationPreferences.getInstance().getPatientId();
    }

    private void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto, double amount) {
        dismiss();
        callback.startPrepaymentProcess(appointmentRequestDto, amount, selectedPractice.getPracticeId());
        logMixPanelPrepaymentAppointmentRequestedEvent(appointmentDTO);
    }

    private void logMixPanelPrepaymentAppointmentRequestedEvent(AppointmentDTO appointmentDTO) {
        String[] params = {getString(R.string.param_payment_amount),
                getString(R.string.param_provider_id),
                getString(R.string.param_practice_id),
                getString(R.string.param_location_id)
        };
        Object[] values = {appointmentDTO.getPayload().getVisitType().getAmount(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                selectedPractice.getPracticeId(),
                appointmentDTO.getPayload().getLocation().getGuid()
        };

        MixPanelUtil.logEvent(getString(R.string.event_payment_start_prepayment), params, values);
    }

    protected void onMapView(final String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    protected void onPhoneCall(final String phoneNumber) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        } catch (ActivityNotFoundException ex) {
            Log.e("error", ex.getMessage());
        }
    }

    public AppointmentsSettingDTO getAppointmentSettings(String practiceId) {
        for (AppointmentsSettingDTO settings : appointmentModelDto.getPayload().getAppointmentsSettings()) {
            if (practiceId.equals(settings.getPracticeId())) {
                return settings;
            }
        }
        return null;
    }
}
