package com.carecloud.carepay.patient.appointments.presenter;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.patient.appointments.createappointment.RequestAppointmentDialogFragment;
import com.carecloud.carepay.patient.appointments.dialog.CancelAppointmentFeeDialog;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.models.PracticeInformationMiniPayload;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.checkout.AllDoneDialogFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.patient.rate.RateDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.dtos.RefreshDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.createappointment.availabilityhour.BaseAvailabilityHourFragment;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityMetadataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VideoVisitModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentConnectivityHandler;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.signinsignup.dto.SignInPayloadMetadata;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
//import com.google.android.gms.wallet.MaskedWallet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 5/15/17
 */

public class PatientAppointmentPresenter extends AppointmentPresenter
        implements PatientAppointmentNavigationCallback, PatientPaymentMethodInterface {

    public static final int CHECK_IN_FLOW_REQUEST_CODE = 201;

    private String patientId;
    private String practiceId;
    private String practiceMgmt;
    private String practiceName;
    private AppointmentDTO appointmentDTO;

    private Fragment androidPayTargetFragment;

    private boolean startCancelationFeePayment = false;
    private String cancellationReasonString;
    private AppointmentDTO cancelAppointmentDTO;

    public PatientAppointmentPresenter(AppointmentConnectivityHandler viewHandler,
                                       AppointmentsResultModel appointmentsResultModel,
                                       PaymentsModel paymentsModel) {
        super(viewHandler, appointmentsResultModel, paymentsModel);
    }

    @Override
    public void startVideoVisit(AppointmentDTO appointmentDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Gson gson = new Gson();
        String jsonBody = gson.toJson(new RefreshDTO(ApplicationPreferences.getInstance().getRefreshToken()));

        TransitionDTO videoVisitTransition = appointmentsResultModel.getMetadata().getLinks().getVideoVisit();
        viewHandler.getWorkflowServiceHelper().execute(videoVisitTransition, startVideoVisitCallback, jsonBody, queryMap);
    }

    @Override
    public void onCancelAppointment(final AppointmentDTO appointmentDTO) {
        final AppointmentCancellationFee cancellationFee = getCancellationFee(appointmentDTO);
        if (cancellationFee == null) {
            showCancellationReasons(appointmentDTO, null);
        } else {
            CancelAppointmentFeeDialog fragment = CancelAppointmentFeeDialog.newInstance(cancellationFee);
            fragment.setCancelFeeDialogListener(() -> showCancellationReasons(appointmentDTO, cancellationFee));
            fragment.setOnCancelListener(dialog -> AppointmentDetailDialog.getInstance().showDialog());
            viewHandler.displayDialogFragment(fragment, false);
        }
    }

    private void showCancellationReasons(AppointmentDTO appointmentDTO,
                                         final AppointmentCancellationFee cancellationFee) {
        CancelReasonAppointmentDialog dialog = CancelReasonAppointmentDialog
                .newInstance(appointmentDTO, appointmentsResultModel);
        dialog.setsCancelReasonAppointmentDialogListener(new CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener() {
            @Override
            public void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO1, int cancellationReason, String cancellationReasonComment) {
                cancellationReasonString = getCancelReason(cancellationReason, cancellationReasonComment);
                cancelAppointmentDTO = appointmentDTO1;
                practiceName = getPracticeInfo(appointmentsResultModel.getPayload().getUserPractices(),
                        appointmentDTO.getMetadata().getPracticeId()).getPracticeName();
                if (cancellationFee == null) {
                    onCancelAppointment(appointmentDTO1, cancellationReason, cancellationReasonComment);
                } else {
                    startCancelationFeePayment = true;
                    CancelReasonAppointmentDialog.isCancelReasonRequired = true;

                    PatientAppointmentPresenter.this.appointmentDTO = appointmentDTO1;
                    IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
                    postModel.setAmount(Double.parseDouble(cancellationFee.getAmount()));
                    IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
                    paymentLineItem.setAmount(Double.parseDouble(cancellationFee.getAmount()));
                    paymentLineItem.setProviderID(appointmentDTO1.getPayload().getProvider().getGuid());
                    paymentLineItem.setLocationID(appointmentDTO1.getPayload().getLocation().getGuid());
                    paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_CANCELLATION);


                    postModel.addLineItem(paymentLineItem);
                    postModel.getMetadata().setAppointmentId(appointmentDTO1.getPayload().getId());
                    postModel.getMetadata().setCancellationReasonId(String.valueOf(cancellationReason));

                    SignInPayloadMetadata queryMetadata = new SignInPayloadMetadata();
                    queryMetadata.setPracticeId(appointmentDTO.getMetadata().getPracticeId());
                    queryMetadata.setPatientId(appointmentDTO.getMetadata().getPatientId());
                    queryMetadata.setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
                    postModel.setQueryMetadata(queryMetadata);

                    paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
                    PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                            .newInstance(paymentsModel, Double.parseDouble(cancellationFee.getAmount()),
                                    Label.getLabel("appointment_cancellation_fee_title"));
                    viewHandler.addFragment(prepaymentFragment, true);

                    String[] params = {getString(R.string.param_payment_amount),
                            getString(R.string.param_provider_id),
                            getString(R.string.param_practice_id),
                            getString(R.string.param_location_id)
                    };
                    Object[] values = {cancellationFee.getAmount(),
                            appointmentDTO1.getPayload().getProvider().getGuid(),
                            appointmentDTO1.getMetadata().getPracticeId(),
                            appointmentDTO1.getPayload().getLocation().getGuid()
                    };
                    MixPanelUtil.logEvent(getString(R.string.event_payment_cancellation_started), params, values);
                }
            }

            @Override
            public void onBackClick() {
                if (cancellationFee != null) {
                    CancelAppointmentFeeDialog.getInstance().showDialog();
                } else {
                    AppointmentDetailDialog.getInstance().showDialog();
                }
                displayToolbar(true, null);
            }
        });
        viewHandler.addFragment(dialog, true);
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
            logApptCancelMixpanel();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.hideProgressDialog();
            viewHandler.showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onCheckInStarted(AppointmentDTO appointmentDTO) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        if (appointmentsResultModel.getPayload().getDelegate() != null) {
            queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        }

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
                if (workflowDTO.getState().equals(NavigationStateConstants.APPOINTMENTS)) {
                    bundle.putBoolean(CarePayConstants.REFRESH, true);
                    AllDoneDialogFragment fragment = AllDoneDialogFragment.newInstance(workflowDTO);
                    displayDialogFragment(fragment, true);
                    return;
                }
                if (workflowDTO.getState().equals(NavigationStateConstants.PATIENT_PAY_CHECKOUT)) {
                    DtoHelper.bundleDto(bundle, appointmentDTO);
                }
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                viewHandler.hideProgressDialog();
                viewHandler.showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, queries, header);
    }

    @Override
    public void onCheckInOfficeStarted(AppointmentDTO appointmentDTO) {
        QrCodeViewDialog fragment = QrCodeViewDialog.newInstance(appointmentDTO, appointmentsResultModel.getMetadata());
        fragment.setQRCodeViewDialogListener(errorMessage -> {
            viewHandler.showErrorNotification(null);
            Log.e(getContext().getString(R.string.alert_title_server_error), errorMessage);
        });
        viewHandler.displayDialogFragment(fragment, false);
    }

    @Override
    public AppointmentsSettingDTO getAppointmentSettings(String practiceId) {
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
    public void callVisitSummaryService(AppointmentDTO appointment, WorkflowServiceCallback callback) {
        TransitionDTO transition = appointmentsResultModel.getMetadata().getLinks().getVisitSummary();
        JsonObject body = new JsonObject();
        body.addProperty("appointment_id", appointment.getPayload().getId());
        body.addProperty("practice_mgmt", appointment.getMetadata().getPracticeMgmt());
        body.addProperty("practice_id", appointment.getMetadata().getPracticeId());
        body.addProperty("patient_id", appointment.getMetadata().getPatientId());
        body.addProperty("format", "pdf");
        viewHandler.getWorkflowServiceHelper().execute(transition, callback, body.toString());
    }

    @Override
    public void callVisitSummaryStatusService(String jobId, String practiceMgmt, WorkflowServiceCallback callback) {
        TransitionDTO transition = appointmentsResultModel.getMetadata().getLinks().getVisitSummaryStatus();
        HashMap<String, String> query = new HashMap<>();
        query.put("job_id", jobId);
        query.put("practice_mgmt", practiceMgmt);
        viewHandler.getWorkflowServiceHelper().execute(transition, callback, query);
    }

    @Override
    public long downloadVisitSummaryFile(String jobId, String practiceMgmt, String title) {
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getVisitSummaryStatus();
        String url = String.format("%s?%s=%s&%s=%s", transitionDTO.getUrl(), "job_id",
                jobId, "practice_mgmt", practiceMgmt);
        return FileDownloadUtil.downloadPdf(getContext(), url, title, ".pdf", "Visit Summary");
    }

    private String getCancelReason(int cancellationReasonId, String cancellationReasonComment) {
        for (CancellationReasonDTO cancellationReason : appointmentsResultModel.getPayload().getCancellationReasons()) {
            if (cancellationReason.getAppointmentCancellationReason().getId().equals(cancellationReasonId)) {
                return cancellationReason.getAppointmentCancellationReason().getName() +
                        (StringUtil.isNullOrEmpty(cancellationReasonComment) ? "" : (" " + cancellationReasonComment));
            }
        }
        return null;
    }

    private AppointmentCancellationFee getCancellationFee(AppointmentDTO appointmentDTO) {
        AppointmentsSettingDTO practiceSettings = getAppointmentSettings(appointmentDTO.getMetadata().getPracticeId());
        if (practiceSettings.shouldChargeCancellationFees()) {
            for (AppointmentCancellationFee cancellationFee : practiceSettings.getCancellationFees()) {
                if (appointmentDTO.getPayload().getVisitType().getId().equals(cancellationFee.getVisitType())) {
                    return cancellationFee;
                }
            }
        }
        return null;
    }

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
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, true, CHECK_IN_FLOW_REQUEST_CODE, info);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                viewHandler.hideProgressDialog();
                viewHandler.showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {
        if (StringUtil.isNullOrEmpty(patientId)) {
            patientId = appointmentRequestDTO.getAppointment().getPatient().getId();
        }
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
        startCancelationFeePayment = false;
        if (appointmentDTO != null) {
            viewHandler.refreshAppointments();
        }
    }

    @Override
    public void onPaymentCancel() {
        startCancelationFeePayment = false;
        CancelReasonAppointmentDialog.getInstance().showDialog();
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsModel, amount);
        viewHandler.replaceFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount, Label.getLabel("appointments_prepayment_title"));
        viewHandler.addFragment(prepaymentFragment, true);
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    Label.getLabel("credit_card_heading"), amount);
            viewHandler.replaceFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        if (appointmentDTO != null) {
            return appointmentDTO.getPayload().getId();
        }
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return appointmentDTO;
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        clearDialogs();
        if (workflowDTO == null) {
            viewHandler.onAppointmentScheduleFlowFailure();
        } else {
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
                String paymentType = Label.getLabel("appointment.confirmationScreen.type.label.paymentType");
                try {
                    if (workflowDTO.getPayload().getAsJsonObject("appointments") != null) {
                        paymentType = Label.getLabel("appointment.confirmationScreen.paymentType.label.cancellationType");
                    }
                } catch (Exception e) {
                    //Using this try catch because middleware returns different structure for the appointment.
                    //In appointment prepayment it returns an array with 1 appointment and for cancellation it returns an object.
                }
                PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                        .newInstance(workflowDTO, paymentType,
                                Label.getLabel("add_appointment_back_to_appointments_button"));
                viewHandler.displayDialogFragment(confirmationFragment, false);

                if (paymentType.equals(Label.getLabel("appointment.confirmationScreen.type.label.paymentType"))) {
                    //this is a prepayment
                    MixPanelUtil.incrementPeopleProperty(getString(R.string.count_prepayments_completed), 1);
                }
            }
        }
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel, String practiceId) {
        clearDialogs();
        new CustomMessageToast(getContext(), Label.getLabel("payment_queued_patient"),
                CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
        viewHandler.confirmAppointment(false,
                getAppointmentSettings(practiceId).getRequests().getAutomaticallyApproveRequests());
        appointmentDTO = null;//clear this
    }

    @Override
    public void setAndroidPayTargetFragment(Fragment fragment) {
        androidPayTargetFragment = fragment;
    }

    @Override
    public Fragment getAndroidPayTargetFragment() {
        return androidPayTargetFragment;
    }

    private UserPracticeDTO getPracticeInfo(List<UserPracticeDTO> userPractices,
                                            String practiceId) {
        for (UserPracticeDTO userPracticeDTO : userPractices) {
            if (userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO;
            }
        }
        UserPracticeDTO userPracticeDTO = new UserPracticeDTO();
        for (UserPracticeDTO resourcesPracticeDTO : userPractices) {
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
    public void onPaymentCashFinished() {
        //NA
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        clearDialogs();
        if (startCancelationFeePayment) {
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("appointment_cancellation_success_message_HTML"));
            viewHandler.confirmAppointment(false, false);
            logApptCancelMixpanel();
        } else {
            showRateDialogFragment();
            PracticeInformationMiniPayload payload = DtoHelper
                    .getConvertedDTO(PracticeInformationMiniPayload.class, workflowDTO.getPayload());
            viewHandler.confirmAppointment(true,
                    getAppointmentSettings(payload.getAppointmentsSettingDTOList().get(0).getPracticeId())
                            .getRequests().getAutomaticallyApproveRequests());
            this.appointmentDTO = null;//clear this
        }
        startCancelationFeePayment = false;
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getString(int id) {
        return getContext().getString(id);
    }

    @Override
    public void showFragment(DialogFragment fragment) {
        addFragment(fragment, true);
    }

    @Override
    public void displayToolbar(boolean display, String title) {
        ((MenuPatientActivity) viewHandler).displayToolbar(display, title);
    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setResourceProvider(resource);
        }
    }

    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setVisitType(visitTypeDTO);
        }
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setLocation(locationDTO);
        }
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO, BaseAvailabilityHourFragment baseAvailabilityHourFragment) {
        RequestAppointmentDialogFragment.newInstance(appointmentDTO).show(getSupportFragmentManager(), "confirmation");
    }

    @Override
    public void appointmentScheduledSuccessfully() {
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager().popBackStackImmediate();
        showRateDialogFragment();
        viewHandler.refreshAppointments();
    }

    private void showRateDialogFragment() {
        if (ApplicationPreferences.getInstance().shouldShowRateDialog()) {
            displayDialogFragment(RateDialog.newInstance(), false);
        }
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        this.appointmentDTO = appointmentDTO;

        AppointmentAvailabilityPayloadDTO payload = new AppointmentAvailabilityPayloadDTO();
        payload.setLocation(appointmentDTO.getPayload().getLocation());

        AppointmentResourcesItemDTO selectedResource = new AppointmentResourcesItemDTO();
        selectedResource.setId(appointmentDTO.getPayload().getResourceId());
        selectedResource.setProvider(appointmentDTO.getPayload().getProvider());
        payload.setResource(selectedResource);

        VisitTypeDTO selectedVisitType = appointmentDTO.getPayload().getVisitType();
        payload.setVisitReason(selectedVisitType);

        AppointmentAvailabilityDataDTO appointmentAvailabilityDataDTO = new AppointmentAvailabilityDataDTO();
        ArrayList<AppointmentAvailabilityPayloadDTO> payloadList = new ArrayList<>();
        payloadList.add(payload);
        AppointmentAvailabilityMetadataDTO metadataDTO = new AppointmentAvailabilityMetadataDTO();
        metadataDTO.setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        metadataDTO.setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentAvailabilityDataDTO.setMetadata(metadataDTO);
        appointmentAvailabilityDataDTO.setPayload(payloadList);
        appointmentsResultModel.getPayload().setAppointmentAvailability(appointmentAvailabilityDataDTO);
        showFragment(AvailabilityHourFragment.newInstance(AvailabilityHourFragment.SCHEDULE_MODE));
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto,
                                       double amount,
                                       String practiceId) {
        AppointmentsSlotsDTO slot = new AppointmentsSlotsDTO();
        slot.setStartTime(appointmentRequestDto.getAppointment().getStartTime());
        slot.setEndTime(appointmentRequestDto.getAppointment().getEndTime());
        this.practiceId = practiceId;
        startPrepaymentProcess(appointmentRequestDto, slot, amount);
    }

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        //NA for this flow
    }

    @Override
    public void setDateRange(Date newStartDate, Date newEndDate) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment instanceof DateCalendarRangeInterface) {
            ((DateCalendarRangeInterface) fragment).setDateRange(newStartDate, newEndDate);
        }
    }

    private FragmentManager getSupportFragmentManager() {
        return ((BaseActivity) viewHandler).getSupportFragmentManager();
    }

    private void logApptCancelMixpanel() {
        AppointmentCancellationFee cancellationFee = getCancellationFee(cancelAppointmentDTO);
        //log appt cancellation to mixpanel
        String[] params = {getString(R.string.param_appointment_cancel_reason),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_provider_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_location_id),
                getString(R.string.param_appointment_type),
                getString(R.string.param_payment_amount)
        };
        Object[] values = {
                cancellationReasonString,
                cancelAppointmentDTO.getMetadata().getPracticeId(),
                practiceName,
                cancelAppointmentDTO.getPayload().getProvider().getGuid(),
                patientId,
                cancelAppointmentDTO.getPayload().getLocation().getGuid(),
                cancelAppointmentDTO.getPayload().getVisitType().getName(),
                cancellationFee != null ? cancellationFee.getAmount() : null
        };
        MixPanelUtil.logEvent(getString(R.string.event_appointment_cancelled), params, values);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_appointment_cancelled), 1);
    }

    private WorkflowServiceCallback startVideoVisitCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            viewHandler.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            viewHandler.hideProgressDialog();
            AppointmentsResultModel appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            VideoVisitModel.Urls url = appointmentsResultModel.getPayload().getVideoVisitModel().getUrls();

            Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.getAndroid()));
            String chromePackageName = "com.android.chrome";

            if (isAppInstalled(chromeIntent, chromePackageName)) {
                if (isAppEnable(chromePackageName)) {
                    chromeIntent.setPackage(chromePackageName);
                    chromeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chromeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(chromeIntent);
                } else {
                    Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + chromePackageName));
                    appStoreIntent.setPackage("com.android.vending");
                    getContext().startActivity(appStoreIntent);
                }
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.getAndroid()));
                if (browserIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    chromeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chromeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(browserIntent);
                } else {
                    Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + chromePackageName));
                    appStoreIntent.setPackage("com.android.vending");
                    getContext().startActivity(appStoreIntent);
                }
            }
        }

        public boolean isAppInstalled(Intent intent, String packageName) {
            List<ResolveInfo> activities = getContext().getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : activities) {
                if (info.activityInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isAppEnable(String chromePackageName) {
            boolean isAppEnable = false;
            try {
                ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(chromePackageName, 0);
                isAppEnable = applicationInfo.enabled;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return isAppEnable;
        }


        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.hideProgressDialog();
            viewHandler.showErrorNotification(exceptionMessage);
        }
    };

    private void clearDialogs() {
        ((BaseActivity) viewHandler).clearFragments();

        if (CancelReasonAppointmentDialog.getInstance() != null) {
            CancelReasonAppointmentDialog.getInstance().dismiss();
        }
        if (CancelAppointmentFeeDialog.getInstance() != null) {
            CancelAppointmentFeeDialog.getInstance().dismiss();
        }
        if (AppointmentDetailDialog.getInstance() != null) {
            AppointmentDetailDialog.getInstance().dismiss();
        }
    }

}
