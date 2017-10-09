package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialogFragment;
import com.carecloud.carepay.practice.library.appointments.dtos.PracticeAppointmentDTO;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckOutFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodPrepaymentFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AvailableHoursInterface;
import com.carecloud.carepaylibray.appointments.interfaces.DateRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.checkout.CheckOutFormFragment;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 6/13/17
 */

public class PatientModeCheckoutActivity extends BasePracticeActivity implements CheckOutInterface, VisitTypeInterface,
        AvailableHoursInterface, DateRangeInterface, PaymentNavigationCallback,
        PaymentMethodDialogInterface, DateRangePickerDialog.DateRangePickerDialogListener,
        AppointmentPrepaymentCallback {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private String appointmentId;
    private AppointmentDTO selectedAppointment;
    private AppointmentsResultModel resourcesToSchedule;

    private Date startDate;
    private Date endDate;
    private AppointmentResourcesDTO appointmentResourcesDTO;
    private VisitTypeDTO visitTypeDTO;

    private boolean shouldAddBackStack = false;

    private WorkflowDTO paymentConfirmationWorkflow;
    private boolean paymentStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_checkout);
        initViews();

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentId = extra.getString(CarePayConstants.APPOINTMENT_ID);
        if (savedInstanceState == null) {
            WorkflowDTO workflowDTO = getConvertedDTO(WorkflowDTO.class);
            initDto(workflowDTO);
        }

        initAppMode();

        shouldAddBackStack = true;
    }

    /**
     * Init current fragment based on the received workflow
     *
     * @param workflowDTO workflow dto
     */
    public void initDto(WorkflowDTO workflowDTO) {
        if (NavigationStateConstants.PATIENT_APP_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            showNextAppointmentFragment(appointmentId);
        } else if (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(workflowDTO.getState())) {
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            showResponsibilityFragment();
        } else if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            showCheckOutFormFragment();
        }

    }

    private void initViews() {
        View logout = findViewById(R.id.logoutTextview);
        logout.setOnClickListener(homeClick);

        View home = findViewById(R.id.btnHome);
        home.setOnClickListener(homeClick);
    }

    private void initAppMode() {
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            //need to switch to PatientMode
            getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
            String username = null;
            if (appointmentsResultModel != null) {
                for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
                    if (appointmentDTO.getPayload().getId().equals(appointmentId)) {
                        username = appointmentDTO.getMetadata().getUsername();
                        break;
                    }
                }
            } else if (paymentsModel != null) {
                username = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances()
                        .get(0).getMetadata().getUsername();
            }

            if (username != null) {
                getAppAuthorizationHelper().setUser(username);
            }
        }
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance(appointmentsResultModel), shouldAddBackStack);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        replaceFragment(NextAppointmentFragment.newInstance(appointmentId), shouldAddBackStack);
    }

    private void showResponsibilityFragment() {
        paymentStarted = true;
        ResponsibilityCheckOutFragment responsibilityFragment = new ResponsibilityCheckOutFragment();
        Bundle bundle = new Bundle();
        DtoHelper.bundleDto(bundle, paymentsModel);
        responsibilityFragment.setArguments(bundle);

        replaceFragment(responsibilityFragment, shouldAddBackStack);
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowNavigateBack()) {
            super.onBackPressed();
        }
    }


    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        //can be ignored
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        //todo whenever payment plans are ready
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {

        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesDTO appointmentResourcesDTO,
                                    AppointmentsResultModel appointmentsResultModel) {
        this.visitTypeDTO = visitTypeDTO;
        this.appointmentResourcesDTO = appointmentResourcesDTO;
//        this.appointmentsResultModel = appointmentsResultModel;
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if ((fragment != null) && fragment.setVisitType(visitTypeDTO)) {
            showAvailableHoursFragment(null, null, appointmentsResultModel,
                    appointmentResourcesDTO.getResource(), visitTypeDTO);
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        if(paymentStarted){
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queryMap.put("practice_id", userPracticeDTO.getPracticeId());
            queryMap.put("appointment_id", appointmentId);
            queryMap.put("patient_id", userPracticeDTO.getPatientId());
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("transition", "true");

            TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(continueTransition, continueCallback, queryMap, header);
        }else {
            showAllDone(workflowDTO);
        }
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesItemDTO appointmentResource,
                                    AppointmentsResultModel appointmentsResultModel) {
        if (getSupportFragmentManager().findFragmentByTag(PracticeAvailableHoursDialogFragment.class
                .getCanonicalName()) != null) {
            getSupportFragmentManager().popBackStack();//close select date fragment
            getSupportFragmentManager().popBackStack();//close available hours fragment
        }
        showAvailableHoursFragment(startDate, endDate, appointmentsResultModel,
                appointmentResource, visitTypeDTO);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        String state = workflowDTO.getState();
        if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(state)||
                (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(state) && !paymentStarted)) {
            navigateToWorkflow(workflowDTO);
        } else {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                StringBuilder builder = new StringBuilder();
                for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                    builder.append(processingError.getError());
                    builder.append("\n");
                }
                int last = builder.lastIndexOf("\n");
                builder.replace(last, builder.length(), "");
                showErrorNotification(builder.toString());
            }else {
                paymentConfirmationWorkflow = workflowDTO;
                completePaymentProcess(workflowDTO);
            }
        }
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }

        UserPracticeDTO userPracticeDTO = new UserPracticeDTO();
        userPracticeDTO.setPracticeId(getAppointment().getMetadata().getPracticeId());
        userPracticeDTO.setPracticeMgmt(getAppointment().getMetadata().getPracticeMgmt());
        userPracticeDTO.setPatientId(getAppointment().getMetadata().getPatientId());
        return userPracticeDTO;
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return appointmentId;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        if (selectedAppointment == null && appointmentsResultModel != null) {
            for (AppointmentDTO appointment : appointmentsResultModel.getPayload().getAppointments()) {
                if (appointment.getPayload().getId().equals(getAppointmentId())) {
                    selectedAppointment = appointment;
                    return selectedAppointment;
                }
            }
        }
        return selectedAppointment;
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentId);
        queryMap.put("patient_id", pendingBalanceDTO.getMetadata().getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, header);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentsModel, owedAmount);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if (fragment != null) {
            fragment.setLocationAndTime(appointmentsSlot);
        }
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                AppointmentResourcesItemDTO appointmentResource,
                                AppointmentsResultModel resourcesToSchedule) {
        this.resourcesToSchedule = resourcesToSchedule;
        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                Label.getLabel("date_range_picker_dialog_title"),
                Label.getLabel("datepicker_cancel_option"),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this
        );

        displayDialogFragment(dialog, false);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {

    }

    private View.OnClickListener homeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        }
    };

    @Override
    public DTO getDto() {
        return appointmentsResultModel != null ? appointmentsResultModel : paymentsModel;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public void showAvailableHoursFragment(Date start, Date end,
                                           AppointmentsResultModel appointmentsResultModel,
                                           AppointmentResourcesItemDTO resourcesItemDTO,
                                           VisitTypeDTO visitTypeDTO) {
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment
                .newInstance(appointmentsResultModel, resourcesItemDTO, start, end, visitTypeDTO);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void showAllDone(WorkflowDTO workflowDTO) {
        PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper
                .getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);
        if (practiceAppointmentDTO == null) {
            showErrorNotification("Error Checking-out Appointment");
            return;
        }
        Bundle extra = new Bundle();
        if (paymentConfirmationWorkflow != null) {
            extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, true);

            WorkFlowRecord workFlowRecord = new WorkFlowRecord(paymentConfirmationWorkflow);
            workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));
            extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
        } else {
            WorkflowDTO appointmentWorkflowDTO = getAppointmentWorkflowDto(workflowDTO);
            WorkFlowRecord workFlowRecord = new WorkFlowRecord(appointmentWorkflowDTO);
            workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));
            extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
        }

        appointmentsResultModel.getMetadata().getLinks().setPinpad(practiceAppointmentDTO
                .getMetadata().getLinks().getPinpad());
        appointmentsResultModel.getMetadata().getTransitions()
                .setPracticeMode(practiceAppointmentDTO.getMetadata().getTransitions().getPracticeMode());
        extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                DtoHelper.getStringDTO(appointmentsResultModel));
        DtoHelper.bundleDto(extra, appointmentsResultModel.getPayload().getAppointments().get(0));
        extra.putBoolean("isCheckOut", true);
        Intent intent = new Intent(this, CompleteCheckActivity.class);
        intent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
        startActivity(intent);
    }

    @Override
    public boolean shouldAllowNavigateBack() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        onDateRangeSelected(startDate, endDate, visitTypeDTO, appointmentResourcesDTO.getResource(),
                resourcesToSchedule);
    }

    WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            paymentStarted = false;
            hideProgressDialog();
            completePaymentProcess(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {

    }

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
                    WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                    showPaymentConfirmation(workflowDTO);
                }
                break;
            }
            default:
                //nothing
        }
    }


    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            PaymentQueuedDialogFragment dialogFragment = new PaymentQueuedDialogFragment();
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onPayLaterClicked(paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0));
                }
            };
            dialogFragment.setOnDismissListener(dismissListener);
            displayDialogFragment(dialogFragment, false);

        }
    }

    private WorkflowDTO getAppointmentWorkflowDto(WorkflowDTO workflowDTO) {
        PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper
                .getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);
        String id = practiceAppointmentDTO.getPayload().getPracticeAppointments().getPayload().getId();
        for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
            if (appointmentDTO.getPayload().getId().equals(id)) {
                appointmentDTO.setPayload(practiceAppointmentDTO.getPayload().getPracticeAppointments()
                        .getPayload());
                break;
            }
        }
        String appointmentWorkflowString = DtoHelper.getStringDTO(appointmentsResultModel);
        WorkflowDTO appointmentWorkflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class,
                appointmentWorkflowString);
        if (appointmentWorkflowDTO != null) {
            appointmentWorkflowDTO.setState(workflowDTO.getState());
        }
        return appointmentWorkflowDTO;
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(amount);
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setProviderID(appointmentRequestDTO.getAppointment().getProviderGuid());
        paymentLineItem.setLocationID(appointmentSlot.getLocation().getGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_PREPAYMENT);
        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentRequestDTO(appointmentRequestDTO.getAppointment());

        paymentsModel = new PaymentsModel();
        paymentsModel.getPaymentPayload().setPaymentSettings(appointmentsResultModel.getPayload()
                .getPaymentSettings());
        paymentsModel.getPaymentPayload().setMerchantServices(appointmentsResultModel.getPayload()
                .getMerchantServices());
        paymentsModel.getPaymentPayload().setPatientCreditCards(appointmentsResultModel.getPayload()
                .getPatientCreditCards());
        paymentsModel.getPaymentsMetadata().getPaymentsTransitions().setMakePayment(appointmentsResultModel
                .getMetadata().getTransitions().getMakePayment());
        paymentsModel.getPaymentsMetadata().getPaymentsTransitions().setAddCreditCard(appointmentsResultModel
                .getMetadata().getTransitions().getAddCreditCard());
        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
        PracticePaymentMethodPrepaymentFragment prepaymentFragment = PracticePaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(prepaymentFragment, true);
    }

    @Override
    public void onPaymentDismissed() {

    }
}
