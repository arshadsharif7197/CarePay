package com.carecloud.carepay.patient.appointments.presenter;

import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 5/15/17
 */

public class PatientAppointmentPresenter extends AppointmentPresenter implements PatientAppointmentNavigationCallback {
    private String patientId;
    private String practiceId;
    private String practiceMgmt;
    private AppointmentResourcesDTO selectedAppointmentResourcesDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentDTO appointmentDTO;


    public PatientAppointmentPresenter(AppointmentViewHandler viewHandler, AppointmentsResultModel appointmentsResultModel) {
        super(viewHandler, appointmentsResultModel);
    }

    @Override
    public void newAppointment() {
        Bundle args = new Bundle();
        Gson gson = new Gson();

        args.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentsResultModel));

        ChooseProviderFragment chooseProviderFragment = new ChooseProviderFragment();
        chooseProviderFragment.setArguments(args);

        viewHandler.navigateToFragment(chooseProviderFragment, true);
    }

    @Override
    public void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        ResourcesPracticeDTO selectedResourcesPracticeDTO = appointmentsResultModel.getPayload().getResourcesToSchedule().get(0).getPractice();
        setPatientID(selectedResourcesPracticeDTO.getPracticeId());
        practiceId = selectedResourcesPracticeDTO.getPracticeId();
        practiceMgmt = selectedResourcesPracticeDTO.getPracticeMgmt();

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO, appointmentsResultModel);
        viewHandler.displayDialogFragment(dialog, true);
    }

    @Override
    public void selectTime(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO,
                           AppointmentsResultModel appointmentsResultModel) {
        selectedAppointmentResourcesDTO = appointmentResourcesDTO;
        selectedVisitTypeDTO = visitTypeDTO;
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel,
                        appointmentResourcesDTO.getResource(), null, null, visitTypeDTO);
        viewHandler.navigateToFragment(availableHoursFragment, true);
    }

    @Override
    public void selectTime(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource,
                           AppointmentsResultModel appointmentsResultModel) {
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResource,
                        startDate, endDate, visitTypeDTO);
        viewHandler.navigateToFragment(availableHoursFragment, false);
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));

        AppointmentDateRangeFragment appointmentDateRangeFragment = new AppointmentDateRangeFragment();
        appointmentDateRangeFragment.setArguments(bundle);

        viewHandler.navigateToFragment(appointmentDateRangeFragment, false);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        this.appointmentDTO = appointmentDTO;
        selectedVisitTypeDTO = appointmentDTO.getPayload().getVisitType();
        AppointmentResourcesItemDTO resourcesItemDTO = new AppointmentResourcesItemDTO();
        resourcesItemDTO.setId(appointmentDTO.getPayload().getResourceId());
        resourcesItemDTO.setProvider(appointmentDTO.getPayload().getProvider());

        selectedAppointmentResourcesDTO = new AppointmentResourcesDTO();
        selectedAppointmentResourcesDTO.setResource(resourcesItemDTO);

        ResourcesToScheduleDTO resourcesToSchedule = new ResourcesToScheduleDTO();
        resourcesToSchedule.getPractice().setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        resourcesToSchedule.getPractice().setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentsResultModel.getPayload().getResourcesToSchedule().add(resourcesToSchedule);

        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment.newInstance(appointmentsResultModel, appointmentDTO);

        viewHandler.navigateToFragment(availableHoursFragment, true);
    }

    @Override
    public void confirmAppointment(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        ProviderDTO providersDTO;
        if (selectedAppointmentResourcesDTO != null) {
            providersDTO = selectedAppointmentResourcesDTO.getResource().getProvider();
        } else {
            providersDTO = appointmentDTO.getPayload().getProvider();
            patientId = appointmentDTO.getMetadata().getPatientId();
            practiceId = appointmentDTO.getMetadata().getPracticeId();
            practiceMgmt =appointmentDTO.getMetadata().getPracticeMgmt();
        }

        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(appointmentsSlot.getStartTime());
        payloadDTO.setEndTime(appointmentsSlot.getEndTime());
        payloadDTO.setLocation(appointmentsSlot.getLocation());
        payloadDTO.setVisitReasonId(selectedVisitTypeDTO.getId());
        payloadDTO.setChiefComplaint(selectedVisitTypeDTO.getName());

        payloadDTO.setProvider(providersDTO);
        payloadDTO.setProviderId(providersDTO.getId().toString());

        PatientModel patientDTO = new PatientModel();
        patientDTO.setPatientId(patientId);
        payloadDTO.setPatient(patientDTO);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);

        final RequestAppointmentDialog requestAppointmentDialog = new RequestAppointmentDialog(getContext(),
                appointmentDTO, appointmentsSlot);
        requestAppointmentDialog.show();
    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments) {
        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", practiceMgmt);
        queryMap.put("practice_id", practiceId);

        JsonObject patientJSONObj = new JsonObject();
        patientJSONObj.addProperty("id", patientId);

        JsonObject appointmentJSONObj = new JsonObject();
        appointmentJSONObj.addProperty("start_time", appointmentSlot.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentSlot.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", appointmentSlot.getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", selectedAppointmentResourcesDTO.getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", selectedVisitTypeDTO.getId());
        appointmentJSONObj.addProperty("resource_id", selectedAppointmentResourcesDTO.getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", selectedVisitTypeDTO.getName());
        appointmentJSONObj.addProperty("comments", comments);

        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();

        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, getMakeAppointmentCallback, makeAppointmentJSONObj.toString(), queryMap);
    }

    @Override
    public void onAppointmentUnconfirmed() {

    }

    @Override
    public void onAppointmentRequestSuccess() {
        viewHandler.confirmAppointment();
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO) {
        new CancelReasonAppointmentDialog(getContext(), appointmentDTO, appointmentsResultModel, new CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener() {
            @Override
            public void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment) {
                onCancelAppointment(appointmentDTO, cancellationReason, cancellationReasonComment);
            }
        }).show();
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        DataDTO data = appointmentsResultModel.getMetadata().getTransitions().getCancel().getData();
        JsonObject postBodyObj = new JsonObject();
        if (!StringUtil.isNullOrEmpty(cancellationReasonComment)) {
            postBodyObj.addProperty(data.getCancellationComments().getName(), cancellationReasonComment);
        }
        if (cancellationReason != -1) {
            postBodyObj.addProperty(data.getCancellationReasonId().getName(), cancellationReason);
        }

        String body = postBodyObj.toString();

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCancel();
        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, cancelCallback, body, queries);
    }

    @Override
    public void onCheckInStarted(AppointmentDTO appointmentDTO) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = viewHandler.getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingIn();
        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, checkinCallback, queries, header);
    }

    @Override
    public void onCheckOutStarted(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onCheckInOfficeStarted(AppointmentDTO appointmentDTO) {
        new QrCodeViewDialog(getContext(), appointmentDTO, appointmentsResultModel.getMetadata(), new QrCodeViewDialog.QRCodeViewDialogListener() {
            @Override
            public void onGenerateQRCodeError(String errorMessage) {
                viewHandler.showErrorNotification(null);
                Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), errorMessage);
            }
        }).show();
    }

    @Override
    public void onRescheduleAppointment(AppointmentDTO appointmentDTO) {
        rescheduleAppointment(appointmentDTO);
    }

    @Override
    public void getQueueStatus(AppointmentDTO appointmentDTO, WorkflowServiceCallback callback) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getQueueStatus();
        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, callback, queryMap);
    }

    @Override
    public void displayAppointmentDetails(AppointmentDTO appointmentDTO) {
        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointmentDTO);
        viewHandler.displayDialogFragment(detailDialog, false);
//        detailDialog.show(getSupportFragmentManager(), detailDialog.getClass().getName());
    }


    private void setPatientID(String practiceID){
        PracticePatientIdsDTO[] practicePatientIdArray = viewHandler.getApplicationPreferences().getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS, PracticePatientIdsDTO[].class);
        for(PracticePatientIdsDTO practicePatientId : practicePatientIdArray){
            if(practicePatientId.getPracticeId().equals(practiceID)){
                patientId = practicePatientId.getPatientId();
                return;
            }
        }
    }


    private WorkflowServiceCallback cancelCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            viewHandler.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            viewHandler.hideProgressDialog();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("appointment_cancellation_success_message_HTML"));
            viewHandler.refreshAppointments();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.hideProgressDialog();
            viewHandler.showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback checkinCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            viewHandler.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            viewHandler.hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.hideProgressDialog();
            viewHandler.showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            viewHandler.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            viewHandler.hideProgressDialog();
            onAppointmentRequestSuccess();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.hideProgressDialog();
            viewHandler.showErrorNotification(exceptionMessage);
//            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
            onAppointmentUnconfirmed();
        }
    };



}
