package com.carecloud.carepay.patient.appointments.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.dialog.CancelAppointmentFeeDialog;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 5/15/17
 */

public class PatientAppointmentPresenter extends AppointmentPresenter
        implements PatientAppointmentNavigationCallback {
    private String patientId;
    private String practiceId;
    private String practiceMgmt;
    private AppointmentResourcesDTO selectedAppointmentResourcesDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentDTO appointmentDTO;
    private AppointmentsSlotsDTO appointmentSlot;


    public PatientAppointmentPresenter(AppointmentViewHandler viewHandler,
                                       AppointmentsResultModel appointmentsResultModel,
                                       PaymentsModel paymentsModel) {
        super(viewHandler, appointmentsResultModel, paymentsModel);
    }

    @Override
    public void newAppointment() {
        ChooseProviderFragment fragment = ChooseProviderFragment.newInstance(appointmentsResultModel,
                null, null);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                                   AppointmentsResultModel appointmentsResultModel,
                                   ResourcesToScheduleDTO resourcesToScheduleDTO) {
        UserPracticeDTO selectedResourcesPracticeDTO;
        if (resourcesToScheduleDTO != null) {
            selectedResourcesPracticeDTO = resourcesToScheduleDTO.getPractice();
        } else {
            selectedResourcesPracticeDTO = appointmentsResultModel.getPayload()
                    .getResourcesToSchedule().get(0).getPractice();
        }
        setPatientID(selectedResourcesPracticeDTO.getPracticeId());
        practiceId = selectedResourcesPracticeDTO.getPracticeId();
        practiceMgmt = selectedResourcesPracticeDTO.getPracticeMgmt();

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO,
                appointmentsResultModel, getPracticeSettings());
        viewHandler.displayDialogFragment(dialog, true);
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesDTO appointmentResourcesDTO,
                                    AppointmentsResultModel appointmentsResultModel) {
        selectedAppointmentResourcesDTO = appointmentResourcesDTO;
        selectedVisitTypeDTO = visitTypeDTO;
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel,
                        appointmentResourcesDTO.getResource(), null, null, visitTypeDTO);
        viewHandler.navigateToFragment(availableHoursFragment, true);
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesItemDTO appointmentResource,
                                    AppointmentsResultModel appointmentsResultModel) {
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResource,
                        startDate, endDate, visitTypeDTO);
        viewHandler.navigateToFragment(availableHoursFragment, false);
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                AppointmentResourcesItemDTO appointmentResource,
                                AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE,
                gson.toJson(appointmentsResultModel));

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
        practiceId = appointmentDTO.getMetadata().getPracticeId();
        practiceMgmt = appointmentDTO.getMetadata().getPracticeMgmt();
        patientId = appointmentDTO.getMetadata().getPatientId();

        ResourcesToScheduleDTO resourcesToSchedule = new ResourcesToScheduleDTO();
        resourcesToSchedule.getPractice().setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        resourcesToSchedule.getPractice().setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentsResultModel.getPayload().getResourcesToSchedule().add(resourcesToSchedule);

        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentDTO);

        viewHandler.navigateToFragment(availableHoursFragment, true);
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot,
                                           AppointmentAvailabilityDTO availabilityDTO) {
        ProviderDTO providersDTO;
        if (selectedAppointmentResourcesDTO != null) {
            providersDTO = selectedAppointmentResourcesDTO.getResource().getProvider();
        } else {
            providersDTO = appointmentDTO.getPayload().getProvider();
            patientId = appointmentDTO.getMetadata().getPatientId();
            practiceId = appointmentDTO.getMetadata().getPracticeId();
            practiceMgmt = appointmentDTO.getMetadata().getPracticeMgmt();
        }

        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(appointmentsSlot.getStartTime());
        payloadDTO.setEndTime(appointmentsSlot.getEndTime());
        payloadDTO.setLocation(appointmentsSlot.getLocation());

        payloadDTO.setProvider(providersDTO);
        payloadDTO.setProviderId(providersDTO.getId().toString());

        PatientModel patientDTO = new PatientModel();
        patientDTO.setPatientId(patientId);
        payloadDTO.setPatient(patientDTO);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);

        final RequestAppointmentDialog requestAppointmentDialog = new RequestAppointmentDialog(getContext(),
                appointmentDTO, appointmentsSlot, selectedVisitTypeDTO);
        requestAppointmentDialog.show();
    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", practiceMgmt);
        queryMap.put("practice_id", practiceId);

        ScheduleAppointmentRequestDTO scheduleAppointmentRequestDTO = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = scheduleAppointmentRequestDTO.getAppointment();
        appointment.setStartTime(appointmentSlot.getStartTime());
        appointment.setEndTime(appointmentSlot.getEndTime());
        appointment.setLocationId(appointmentSlot.getLocation().getId());
        appointment.setLocationGuid(appointmentSlot.getLocation().getGuid());
        appointment.setProviderId(selectedAppointmentResourcesDTO.getResource().getProvider().getId());
        appointment.setProviderGuid(selectedAppointmentResourcesDTO.getResource().getProvider().getGuid());
        appointment.setVisitReasonId(selectedVisitTypeDTO.getId());
        appointment.setResourceId(selectedAppointmentResourcesDTO.getResource().getId());
        appointment.setComplaint(selectedVisitTypeDTO.getName());
        appointment.setComments(comments);

        appointment.getPatient().setId(patientId);

        double amount = selectedVisitTypeDTO.getAmount();
        if (amount > 0 && paymentsModel != null) {
            startPrepaymentProcess(scheduleAppointmentRequestDTO, appointmentSlot, amount);
        } else {
            Gson gson = new Gson();
            TransitionDTO transitionDTO = appointmentsResultModel.getMetadata()
                    .getTransitions().getMakeAppointment();
            viewHandler.getWorkflowServiceHelper().execute(transitionDTO, getMakeAppointmentCallback,
                    gson.toJson(scheduleAppointmentRequestDTO), queryMap);
        }
    }

    @Override
    public void onAppointmentUnconfirmed() {

    }

    @Override
    public void onAppointmentRequestSuccess() {
        viewHandler.confirmAppointment();
    }

    @Override
    public void onCancelAppointment(final AppointmentDTO appointmentDTO) {
        final AppointmentCancellationFee cancellationFee = getCancellationFee(appointmentDTO);
        if (cancellationFee == null) {
            showCancellationReasons(appointmentDTO, null);
        } else {
            practiceId = appointmentDTO.getMetadata().getPracticeId();
            practiceMgmt = appointmentDTO.getMetadata().getPracticeMgmt();
            patientId = appointmentDTO.getMetadata().getPatientId();
            new CancelAppointmentFeeDialog(getContext(), cancellationFee,
                    new CancelAppointmentFeeDialog.CancelAppointmentFeeDialogListener() {
                        @Override
                        public void onCancelAppointmentFeeAccepted() {
                            showCancellationReasons(appointmentDTO, cancellationFee);
                        }
                    }).show();
        }
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReason,
                                    String cancellationReasonComment) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        DataDTO data = appointmentsResultModel.getMetadata().getTransitions().getCancel().getData();
        JsonObject postBodyObj = new JsonObject();
        if (!StringUtil.isNullOrEmpty(cancellationReasonComment)) {
            postBodyObj.addProperty(data.getCancellationComments().getName() != null ?
                    data.getCancellationComments().getName() : "cancellation_comments", cancellationReasonComment);
        }
        if (cancellationReason != -1) {
            postBodyObj.addProperty(data.getCancellationReasonId().getName() != null ?
                    data.getCancellationReasonId().getName() : "cancellation_reason_id", cancellationReason);
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
        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, getCheckInCallback(appointmentDTO), queries, header);
    }

    @Override
    public void onCheckOutStarted(AppointmentDTO appointmentDTO) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        Map<String, String> header = viewHandler.getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingOut();
        final Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.APPOINTMENT_ID, appointmentDTO.getPayload().getId());
        viewHandler.getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                viewHandler.showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                viewHandler.hideProgressDialog();
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                viewHandler.hideProgressDialog();
                viewHandler.showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error),
                        exceptionMessage);
            }
        }, queries, header);
    }

    @Override
    public void onCheckInOfficeStarted(AppointmentDTO appointmentDTO) {
        new QrCodeViewDialog(getContext(), appointmentDTO, appointmentsResultModel.getMetadata(),
                new QrCodeViewDialog.QRCodeViewDialogListener() {
                    @Override
                    public void onGenerateQRCodeError(String errorMessage) {
                        viewHandler.showErrorNotification(null);
                        Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error),
                                errorMessage);
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
    public AppointmentsSettingDTO getPracticeSettings() {
        List<AppointmentsSettingDTO> appointmentsSettingsList = appointmentsResultModel
                .getPayload().getAppointmentsSettings();
        if (practiceId != null) {
            for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
                if (appointmentsSettingDTO.getPracticeId().equals(practiceId)) {
                    return appointmentsSettingDTO;
                }
            }
        }
        return appointmentsSettingsList.get(0);
    }

    @Override
    public void displayAppointmentDetails(AppointmentDTO appointmentDTO) {
        practiceId = appointmentDTO.getMetadata().getPracticeId();
        practiceMgmt = appointmentDTO.getMetadata().getPracticeMgmt();
        patientId = appointmentDTO.getMetadata().getPatientId();
        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointmentDTO);
        viewHandler.displayDialogFragment(detailDialog, false);
    }

    private void showCancellationReasons(AppointmentDTO appointmentDTO, final AppointmentCancellationFee cancellationFee) {
        new CancelReasonAppointmentDialog(getContext(), appointmentDTO, appointmentsResultModel,
                new CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener() {
                    @Override
                    public void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO,
                                                                             int cancellationReason,
                                                                             String cancellationReasonComment) {
                        if (cancellationFee == null) {
                            onCancelAppointment(appointmentDTO, cancellationReason, cancellationReasonComment);
                        } else {
                            IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
                            postModel.setAmount(Double.parseDouble(cancellationFee.getAmount()));
                            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
                            paymentLineItem.setAmount(Double.parseDouble(cancellationFee.getAmount()));
                            paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                            paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_CANCELLATION);


                            postModel.addLineItem(paymentLineItem);
                            postModel.getMetadata().setAppointmentId(appointmentDTO.getPayload().getId());
                            postModel.getMetadata().setCancellationReasonId(String.valueOf(cancellationReason));
                            paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);

                            PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                                    .newInstance(paymentsModel, Double.parseDouble(cancellationFee.getAmount()));
                            viewHandler.navigateToFragment(prepaymentFragment, true);
                        }
                    }
                }).show();
    }

    private AppointmentCancellationFee getCancellationFee(AppointmentDTO appointmentDTO) {
        AppointmentsSettingDTO practiceSettings = getPracticeSettings();
        if (practiceSettings.shouldChargeCancellationFees()) {
            for (AppointmentCancellationFee cancellationFee : practiceSettings.getCancellationFees()) {
                if (appointmentDTO.getPayload().getVisitType().getId().equals(cancellationFee.getVisitType())) {
                    return cancellationFee;
                }
            }
        }
        return null;
    }


    private void setPatientID(String practiceID) {
        PracticePatientIdsDTO[] practicePatientIdArray = viewHandler.getApplicationPreferences()
                .getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS,
                        PracticePatientIdsDTO[].class);
        for (PracticePatientIdsDTO practicePatientId : practicePatientIdArray) {
            if (practicePatientId.getPracticeId().equals(practiceID)) {
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
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error),
                    exceptionMessage);
        }
    };

    private WorkflowServiceCallback getCheckInCallback(final AppointmentDTO appointmentDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                viewHandler.showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                viewHandler.hideProgressDialog();
                Bundle info = new Bundle();
                DtoHelper.bundleDto(info, appointmentDTO);
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, info);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                viewHandler.hideProgressDialog();
                viewHandler.showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error),
                        exceptionMessage);
            }
        };
    }

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
            onAppointmentUnconfirmed();
        }
    };


    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {
        this.appointmentSlot = appointmentSlot;
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(amount);

        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setProviderID(appointmentRequestDTO.getAppointment().getProviderGuid());
        paymentLineItem.setLocationID(appointmentRequestDTO.getAppointment().getLocationGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_PREPAYMENT);

        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentRequestDTO(appointmentRequestDTO.getAppointment());

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);

        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void onPaymentDismissed() {
        if (appointmentDTO != null) {
            onHoursAndLocationSelected(appointmentSlot, null);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        Fragment fragment = new AddNewCreditCardFragment();
        fragment.setArguments(args);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount);
        viewHandler.navigateToFragment(prepaymentFragment, true);
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PaymentPlanFragment fragment = new PaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            viewHandler.navigateToFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty()
                && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            ((ISession) viewHandler.getContext()).showErrorNotification(builder.toString());
        } else {
            Bundle args = new Bundle();
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

            PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
            confirmationFragment.setArguments(args);
            viewHandler.displayDialogFragment(confirmationFragment, false);
        }
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPatientId() != null && userPracticeDTO.getPatientId().equals(patientId)) {
                return userPracticeDTO;
            }
        }

        UserPracticeDTO userPracticeDTO = new UserPracticeDTO();
        for (UserPracticeDTO resourcesPracticeDTO : appointmentsResultModel
                .getPayload().getUserPractices()) {
            if (resourcesPracticeDTO.getPracticeId().equals(practiceId)) {
                userPracticeDTO.setPracticeMgmt(resourcesPracticeDTO.getPracticeMgmt());
                userPracticeDTO.setPracticeId(resourcesPracticeDTO.getPracticeId());
                userPracticeDTO.setPracticeName(resourcesPracticeDTO.getPracticeName());
                userPracticeDTO.setPracticePhoto(resourcesPracticeDTO.getPracticePhoto());
                userPracticeDTO.setPatientId(patientId);

                return userPracticeDTO;
            }
        }

        userPracticeDTO.setPatientId(patientId);
        userPracticeDTO.setPracticeId(practiceId);
        userPracticeDTO.setPracticeMgmt(practiceMgmt);
        return userPracticeDTO;
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        onAppointmentRequestSuccess();
    }
}
