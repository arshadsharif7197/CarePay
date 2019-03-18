package com.carecloud.carepay.patient.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.patient.survey.SurveyActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.checkout.CheckOutFormFragment;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragment;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragmentInterface;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAmountDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.android.gms.wallet.MaskedWallet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppointmentCheckoutActivity extends BasePatientActivity implements CheckOutInterface,
        PaymentNavigationCallback,
        AppointmentPrepaymentCallback, PatientPaymentMethodInterface,
        PaymentPlanCompletedInterface, PaymentPlanCreateInterface, ConfirmationCallback {

    private String appointmentId;
    private AppointmentDTO selectedAppointment;

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;

    private boolean shouldAddBackStack = false;
    private boolean paymentStarted = false;
    private boolean completedPaymentPlan = false;

    private Fragment androidPayTargetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_appointment);

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentId = extra.getString(CarePayConstants.APPOINTMENT_ID);
        if (savedInstanceState == null) {
            initDto(getConvertedDTO(WorkflowDTO.class));

            if (getAppointment() != null) {
                logCheckOutStartedMixPanel();
            }
        }

        shouldAddBackStack = true;
    }

    private void logCheckOutStartedMixPanel() {
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_appointment_id),
                getString(R.string.param_appointment_type),
                getString(R.string.param_is_guest),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id)
        };
        Object[] values = {getAppointment().getMetadata().getPracticeId(),
                getAppointmentId(),
                getAppointment().getPayload().getVisitType().getName(),
                false,
                getAppointment().getPayload().getProvider().getGuid(),
                getAppointment().getPayload().getLocation().getGuid()};
        MixPanelUtil.logEvent(getString(R.string.event_checkout_started), params, values);
        MixPanelUtil.startTimer(getString(R.string.timer_checkout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_in_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exitFlow) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("checkin_confirm_exit_title"),
                            Label.getLabel("checkin_confirm_exit_message"),
                            Label.getLabel("button_no"),
                            Label.getLabel("button_yes"));
            fragment.setNegativeAction(true);
            fragment.setCallback(this);
            displayDialogFragment(fragment, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm() {
        MixPanelUtil.logEvent(getString(R.string.event_checkout_cancelled), getString(R.string.param_last_completed_step), getString(R.string.step_appointment));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Init current fragment based on the received workflow
     *
     * @param workflowDTO workflow dto
     */
    public void initDto(WorkflowDTO workflowDTO) {
        if (NavigationStateConstants.PATIENT_APP_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            showNextAppointmentFragment(appointmentId);
        } else if (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(workflowDTO.getState())) {
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            showResponsibilityFragment();
        } else if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            showCheckOutFormFragment();
        }
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance(appointmentsResultModel), shouldAddBackStack);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        replaceFragment(NextAppointmentFragment.newInstance(appointmentId), shouldAddBackStack);
        MixPanelUtil.startTimer(getString(R.string.timer_next_appt));
    }

    private void showResponsibilityFragment() {
        paymentStarted = true;
        replaceFragment(ResponsibilityFragment
                .newInstance(paymentsModel, null, false,
                        Label.getLabel("checkout_responsibility_title")), shouldAddBackStack);

        MixPanelUtil.startTimer(getString(R.string.timer_payment_checkout));
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel != null ? appointmentsResultModel : paymentsModel;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", pendingBalanceDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", pendingBalanceDTO.getMetadata().getPracticeId());
        queryMap.put("appointment_id", appointmentId);
        queryMap.put("patient_id", pendingBalanceDTO.getMetadata().getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, getContinueCallback(false, 0D), queryMap, header);

        double amount = 0D;
        for (PendingBalancePayloadDTO balancePayloadDTO : pendingBalanceDTO.getPayload()) {
            amount += balancePayloadDTO.getAmount();
        }

        MixPanelUtil.logEvent(getString(R.string.event_payment_skipped), getString(R.string.param_balance_amount), amount);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment
                .newInstance(paymentsModel, amount, false), true);

        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };
        Object[] values = {amount,
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        displayDialogFragment(PartialPaymentDialog.newInstance(paymentsModel, selectedBalance), false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                getString(R.string.param_practice_id),
                selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        showResponsibilityFragment();
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedBalancesItem = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);//this should be a safe assumption for checkin
        PendingBalanceDTO reducedBalancesItem = paymentsModel.getPaymentPayload()
                .reduceBalanceItems(selectedBalancesItem, false);
        displayDialogFragment(PaymentPlanAmountDialog.newInstance(paymentsModel, reducedBalancesItem), false);
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        replaceFragment(fragment, true);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        onBackPressed();
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel,
                                          boolean onlySelectMode) {
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PaymentPlanChooseCreditCardFragment fragment = PaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel, onlySelectMode);
            replaceFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                     PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel, onlySelectMode);
        replaceFragment(fragment, true);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanTermsFragment fragment = PaymentPlanTermsFragment.newInstance(paymentsModel, paymentPlanPostModel);
        replaceFragment(fragment, true);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getPracticeInfo(paymentsModel), PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                           PendingBalanceDTO selectedBalance,
                                           double amount) {
        ValidPlansFragment fragment = ValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    PaymentPlanDTO selectedPlan, double amount) {
        AddExistingPaymentPlanFragment fragment = AddExistingPaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance, selectedPlan, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel,
                                      PendingBalancePayloadDTO paymentLineItem,
                                      PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, selectedBalance, false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getPracticeInfo(paymentsModel),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {
        boolean addExisting = false;
        if (paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)) {
            onAddBalanceToExistingPlan(paymentsModel, selectedBalance, amount);
            addExisting = true;
        } else {
            PaymentPlanFragment fragment = PaymentPlanFragment.newInstance(paymentsModel, selectedBalance, amount);
            replaceFragment(fragment, true);
        }

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                addExisting};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null &&
                !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            replaceFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsModel, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError
                    : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            showErrorNotification(builder.toString());
        } else {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment.newInstance(workflowDTO);
            displayDialogFragment(confirmationFragment, false);

            //this is a prepayment
            if(!paymentStarted) {
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_prepayments_completed), 1);
            }
        }
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(this, Label.getLabel("payment_queued_patient"),
                CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();

        UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        queryMap.put("appointment_id", appointmentId);
        queryMap.put("patient_id", userPracticeDTO.getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");

        TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(continueTransition,
                getContinueCallback(true,
                        paymentsModel.getPaymentPayload().getPaymentPostModel().getAmount()),
                queryMap,
                header);

    }

    @Override
    public void setAndroidPayTargetFragment(Fragment fragment) {
        androidPayTargetFragment = fragment;
    }

    @Override
    public Fragment getAndroidPayTargetFragment() {
        return androidPayTargetFragment;
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        String patientId = getAppointment().getMetadata().getPatientId();
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPatientId() != null && userPracticeDTO.getPatientId().equals(patientId)) {
                return userPracticeDTO;
            }
        }

        UserPracticeDTO userPracticeDTO = new UserPracticeDTO();
        userPracticeDTO.setPracticeId(getAppointment().getMetadata().getPracticeId());
        userPracticeDTO.setPracticeMgmt(getAppointment().getMetadata().getPracticeMgmt());
        userPracticeDTO.setPatientId(patientId);
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
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        if (paymentStarted) {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queryMap.put("practice_id", userPracticeDTO.getPracticeId());
            queryMap.put("appointment_id", appointmentId);
            queryMap.put("patient_id", userPracticeDTO.getPatientId());
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("transition", "true");

            TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata()
                    .getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(continueTransition,
                    getContinueCallback(true,
                            paymentsModel.getPaymentPayload().getPatientPayments().getPayload().getAmount()),
                    queryMap,
                    header);
        } else {
            Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
            extra.putBoolean(CarePayConstants.REFRESH, true);
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, extra);
        }
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
    }

    public void showAllDone(WorkflowDTO workflowDTO) {
        AllDoneDialogFragment fragment = AllDoneDialogFragment.newInstance(workflowDTO);
        displayDialogFragment(fragment, true);
    }

    @Override
    public boolean shouldAllowNavigateBack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.getBackStackEntryCount() > 0;
    }

    @Override
    public void completeCheckout(boolean paymentMade, double paymentAmount, boolean surveyAvailable, boolean paymentPlanCreated) {
        //Log Check-out Completed
        if (getAppointment() != null) {
            String[] params = {getString(R.string.param_practice_id),
                    getString(R.string.param_appointment_id),
                    getString(R.string.param_appointment_type),
                    getString(R.string.param_is_guest),
                    getString(R.string.param_payment_made),
                    getString(R.string.param_survey_available),
                    getString(R.string.param_payment_plan_created),
                    getString(R.string.param_payment_amount),
                    getString(R.string.param_partial_pay_available)
            };
            Object[] values = {getAppointment().getMetadata().getPracticeId(),
                    getAppointmentId(),
                    getAppointment().getPayload().getVisitType().getName(),
                    false,
                    paymentMade,
                    surveyAvailable,
                    paymentPlanCreated,
                    paymentAmount,
                    getPaymentSettings(getAppointment().getMetadata().getPracticeId())
                            .getPayload().getRegularPayments().isAllowPartialPayments()
            };
            MixPanelUtil.logEvent(getString(R.string.event_checkout_completed), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_checkout_completed), 1);
            MixPanelUtil.endTimer(getString(R.string.timer_checkout));
            if (paymentMade) {
                MixPanelUtil.endTimer(getString(R.string.timer_payment_checkout));
            }
        }
    }

    @Override
    public void startSurveyFlow(WorkflowDTO workflowDTO) {
        navigateToWorkflow(workflowDTO);
    }

    WorkflowServiceCallback getContinueCallback(final boolean paymentMade, final double paymentAmount) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                paymentStarted = false;
                hideProgressDialog();
                boolean expectsResult = false;
                if (workflowDTO.getState().equals(NavigationStateConstants.SURVEYS_CHECKOUT)) {
                    expectsResult = true;
                }

                Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
                extra.putBoolean(CarePayConstants.REFRESH, true);

                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, expectsResult,
                        SurveyActivity.FLAG_SURVEY_FLOW, extra);

                completeCheckout(paymentMade, paymentAmount, expectsResult, completedPaymentPlan);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {

        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(amount);
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setProviderID(appointmentRequestDTO.getAppointment().getProviderGuid());
        paymentLineItem.setLocationID(appointmentRequestDTO.getAppointment().getLocationGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_PREPAYMENT);
        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentRequestDTO(appointmentRequestDTO.getAppointment());

        if (paymentsModel == null) {
            paymentsModel = new PaymentsModel();
        }
        paymentsModel.getPaymentPayload().setPaymentSettings(appointmentsResultModel.getPayload()
                .getPaymentSettings());
        paymentsModel.getPaymentPayload().setMerchantServices(appointmentsResultModel.getPayload()
                .getMerchantServices());
        paymentsModel.getPaymentPayload().setPatientCreditCards(appointmentsResultModel.getPayload()
                .getPatientCreditCards());
        paymentsModel.getPaymentsMetadata().getPaymentsTransitions().setMakePayment(appointmentsResultModel
                .getMetadata().getTransitions().getMakePayment());
        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
        PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount, Label.getLabel("appointments_prepayment_title"));
        addFragment(prepaymentFragment, true);

        String[] params = {getString(R.string.param_payment_amount),
                getString(R.string.param_provider_id),
                getString(R.string.param_practice_id),
                getString(R.string.param_location_id)
        };
        Object[] values = {amount,
                appointmentRequestDTO.getAppointment().getProviderGuid(),
                selectedAppointment.getMetadata().getPracticeId(),
                appointmentRequestDTO.getAppointment().getLocationGuid()
        };

        MixPanelUtil.logEvent(getString(R.string.event_payment_start_prepayment), params, values);
    }

    @Override
    public void onPaymentDismissed() {

    }

    @Override
    public void createWalletFragment(MaskedWallet maskedWallet, Double amount) {
        replaceFragment(AndroidPayDialogFragment.newInstance(maskedWallet, paymentsModel, amount), true);
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put("payment_plan", "true");

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transition, completePlanCallback, queryMap);
    }


    private WorkflowServiceCallback completePlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            completedPaymentPlan = true;
            Bundle info = new Bundle();
            if (getAppointment() != null) {
                DtoHelper.bundleDto(info, getAppointment());
            }
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, info);

            boolean surveyAvailable = NavigationStateConstants.SURVEYS_CHECKOUT.equals(workflowDTO.getState());
            if (!workflowDTO.getState().contains("checkout") || surveyAvailable) {
                completeCheckout(false, 0D, surveyAvailable, completedPaymentPlan);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    private PaymentsPayloadSettingsDTO getPaymentSettings(String practiceId) {
        for (PaymentsPayloadSettingsDTO settingsDTO : appointmentsResultModel.getPayload().getPaymentSettings()) {
            if (settingsDTO.getMetadata().getPracticeId().equals(practiceId)) {
                return settingsDTO;
            }
        }
        return new PaymentsPayloadSettingsDTO();
    }

    @Override
    public void showFragment(DialogFragment fragment) {
        addFragment(fragment, true);
    }

    @Override
    public void displayToolbar(boolean display, String title) {

    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setResourceProvider(resource);
        }
    }

    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setVisitType(visitTypeDTO);
        }
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setLocation(locationDTO);
        }
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void appointmentScheduledSuccessfully() {

    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto, double amount, String practiceId) {
        AppointmentsSlotsDTO slot = new AppointmentsSlotsDTO();
        slot.setStartTime(appointmentRequestDto.getAppointment().getStartTime());
        slot.setEndTime(appointmentRequestDto.getAppointment().getEndTime());
        startPrepaymentProcess(appointmentRequestDto, slot, amount);
    }

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setAppointmentSlot(slot);
        }
    }

    @Override
    public void setDateRange(Date newStartDate, Date newEndDate) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(AvailabilityHourFragment.class.getName());
        if (fragment instanceof DateCalendarRangeInterface) {
            ((DateCalendarRangeInterface) fragment).setDateRange(newStartDate, newEndDate);
        }
    }
}