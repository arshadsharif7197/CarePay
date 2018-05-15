package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialogFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeChooseProviderDialog;
import com.carecloud.carepay.practice.library.appointments.dtos.PracticeAppointmentDTO;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckOutFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModeAddExistingPaymentPlanFullFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanFullFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodPrepaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAddCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAmountFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeValidPlansFragment;
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
import com.carecloud.carepaylibray.appointments.interfaces.ProviderInterface;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.TransitionsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.checkout.CheckOutFormFragment;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragment;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragmentInterface;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.translation.TranslatableFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
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
        AppointmentPrepaymentCallback, ProviderInterface,
        PaymentPlanCompletedInterface, PaymentPlanCreateInterface {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private String appointmentId;
    private AppointmentDTO selectedAppointment;
    private AppointmentsResultModel resourcesToSchedule;

    private Date startDate;
    private Date endDate;
    private AppointmentResourcesDTO appointmentResourcesDTO;
    private VisitTypeDTO visitTypeDTO;

    private WorkflowDTO paymentConfirmationWorkflow;
    private boolean paymentStarted = false;

    private boolean isUserInteraction = false;

    private WorkflowDTO continuePaymentsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentId = extra.getString(CarePayConstants.APPOINTMENT_ID);
        if (savedInstanceState == null || savedInstanceState.getBoolean("shouldReload", false)) {
            WorkflowDTO workflowDTO = getConvertedDTO(WorkflowDTO.class);
            initDto(workflowDTO);

            if (getAppointment() != null) {
                //Log Check-out Started
                String[] params = {getString(R.string.param_practice_id), getString(R.string.param_appointment_id), getString(R.string.param_appointment_type), getString(R.string.param_is_guest)};
                Object[] values = {getAppointment().getMetadata().getPracticeId(), getAppointmentId(), getAppointment().getPayload().getVisitType().getName(), false};
                MixPanelUtil.logEvent(getString(R.string.event_checkout_started), params, values);
                MixPanelUtil.startTimer(getString(R.string.timer_checkout));
            }
        }
        initAppMode();
        setContentView(R.layout.activity_patient_checkout);
        initViews();
        initializeLanguageSpinner();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteraction = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.languageContainer).setVisibility(View.GONE);
            }
        }, 25);
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
            LinksDTO linksDTO = appointmentsResultModel.getMetadata().getLinks();
            TransitionsDTO transitionsDTO = appointmentsResultModel.getMetadata().getTransitions();
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            appointmentsResultModel.getMetadata().getLinks().setResourcesToSchedule(linksDTO.getResourcesToSchedule());
            appointmentsResultModel.getMetadata().getTransitions().setContinueTransition(transitionsDTO.getContinueTransition());
            showCheckOutFormFragment();
        }

    }

    private void initViews() {
        View logout = findViewById(R.id.logoutTextview);
        logout.setOnClickListener(homeClick);
        View home = findViewById(R.id.btnHome);
        home.setOnClickListener(homeClick);
    }

    private void initializeLanguageSpinner() {
        String selectedLanguageStr = getApplicationPreferences().getUserLanguage();
        OptionDTO selectedLanguage = appointmentsResultModel.getPayload().getLanguages().get(0);
        for (OptionDTO language : appointmentsResultModel.getPayload().getLanguages()) {
            if (selectedLanguageStr.equals(language.getCode())) {
                selectedLanguage = language;
            }
        }

        final TextView languageSwitch = (TextView) findViewById(R.id.languageSpinner);
        final View languageContainer = findViewById(R.id.languageContainer);
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageContainer.setVisibility(languageContainer.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        RecyclerView languageList = (RecyclerView) findViewById(R.id.languageList);
        LanguageAdapter languageAdapter = new LanguageAdapter(appointmentsResultModel.getPayload().getLanguages(),
                selectedLanguage);
        languageList.setAdapter(languageAdapter);
        languageList.setLayoutManager(new LinearLayoutManager(getContext()));
        languageAdapter.setCallback(new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                languageContainer.setVisibility(View.GONE);
                if (!isUserInteraction) {
                    return;
                }
                TransitionDTO transition;
                if (appointmentsResultModel != null) {
                    transition = appointmentsResultModel.getMetadata().getLinks().getLanguage();
                } else {
                    transition = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getLanguage();
                }
                changeLanguage(transition, language.getCode().toLowerCase(), headers, new SimpleCallback() {
                    @Override
                    public void callback() {
                        changeLeftMenuLabels();
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
                        getSupportFragmentManager().popBackStackImmediate();
                        if (fragment instanceof NextAppointmentFragment) {
                            showNextAppointmentFragment(appointmentId);
                        } else if (fragment instanceof ResponsibilityCheckOutFragment) {
                            showResponsibilityFragment();
                        } else if (fragment instanceof CheckOutFormFragment) {
                            showCheckOutFormFragment();
                        }
                        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
                    }
                });
            }
        });
    }

    private void changeLeftMenuLabels() {
        ((TextView) findViewById(R.id.checkoutMessage)).setText(Label.getLabel("practice_checkout_header_label"));
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

                        MixPanelUtil.setUser(getContext(), appointmentDTO.getMetadata().getUserId(), null);

                        break;
                    }
                }
            } else if (paymentsModel != null) {
                PendingBalanceDTO pendingBalanceDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances()
                        .get(0);
                username = pendingBalanceDTO.getMetadata().getUsername();

                MixPanelUtil.setUser(getContext(), pendingBalanceDTO.getMetadata().getUserId(), null);
            }

            if (username != null) {
                getAppAuthorizationHelper().setUser(username);
            }
        }
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance(appointmentsResultModel), true);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        addFragment(NextAppointmentFragment.newInstance(appointmentId), true);
    }

    private void showResponsibilityFragment() {
        paymentStarted = true;
        ResponsibilityCheckOutFragment responsibilityFragment = new ResponsibilityCheckOutFragment();
        Bundle bundle = new Bundle();
        DtoHelper.bundleDto(bundle, paymentsModel);
        responsibilityFragment.setArguments(bundle);

        replaceFragment(responsibilityFragment, true);
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
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment.newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedBalancesItem = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);//this should be a safe assumption for checkin
        PendingBalanceDTO reducedBalancesItem = paymentsModel.getPaymentPayload().reduceBalanceItems(selectedBalancesItem, false);
        PracticePaymentPlanAmountFragment fragment = PracticePaymentPlanAmountFragment.newInstance(paymentsModel, reducedBalancesItem);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment.newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        onBackPressed();
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod, PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel);
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanTermsFragment fragment = PaymentPlanTermsFragment.newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put("payment_plan", "true");

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transition, getCompletePlanAction(workflowDTO, PaymentPlanConfirmationFragment.MODE_CREATE, false), queryMap);
    }

    @Override
    public void onAddBalanceToExitingPlan(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        if (paymentsModel.getPaymentPayload().getValidPlans(practiceId,
                selectedBalance.getPayload().get(0).getAmount()).size() == 1) {
            onSelectedPlanToAdd(paymentsModel,
                    selectedBalance,
                    paymentsModel.getPaymentPayload().getValidPlans(practiceId,
                            selectedBalance.getPayload().get(0).getAmount()).get(0),
                    amount);
        } else {
            PracticeValidPlansFragment fragment = PracticeValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
            displayDialogFragment(fragment, false);
        }
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO selectedPlan, double amount) {
        PatientModeAddExistingPaymentPlanFullFragment fragment = PatientModeAddExistingPaymentPlanFullFragment.newInstance(paymentsModel, selectedBalance, selectedPlan, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put("payment_plan", "true");

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transition, getCompletePlanAction(workflowDTO, PaymentPlanConfirmationFragment.MODE_ADD, false), queryMap);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {
        boolean addExisting = false;
        if(paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)){
            onAddBalanceToExitingPlan(paymentsModel, selectedBalance, amount);
            addExisting = true;
        } else {
            PatientModePaymentPlanFullFragment fragment = PatientModePaymentPlanFullFragment.newInstance(paymentsModel, selectedBalance, amount);
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
        if ((fragment != null) && fragment.setVisitType(visitTypeDTO, true)) {
            showAvailableHoursFragment(null, null, appointmentsResultModel,
                    appointmentResourcesDTO.getResource(), visitTypeDTO);
        }
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

            TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(continueTransition, continueCallback, queryMap, header);
        } else {
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
        if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(state) ||
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
            } else {
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

        MixPanelUtil.logEvent(getString(R.string.event_payment_skipped));
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentsModel, owedAmount);
        displayDialogFragment(dialog, false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment));
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if (fragment != null) {
            fragment.setLocationAndTime(appointmentsSlot, true);
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

            logCheckoutCancelled();
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
        return getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    @Override
    public void showChooseProviderFragment() {
        if (appointmentsResultModel.getPayload().getResourcesToSchedule().isEmpty()) {
            selectedAppointment = getAppointment();
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
            queries.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
            TransitionDTO transitionDTO = appointmentsResultModel.getMetadata()
                    .getLinks().getResourcesToSchedule();
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            getWorkflowServiceHelper().execute(transitionDTO, resourcesToScheduleCallback, queries, header);
        } else {
            showChooseProviderDialogFragment();
        }

    }

    @Override
    public void completeCheckout() {
        //Log Check-out Completed
        if (getAppointment() != null) {
            String[] params = {getString(R.string.param_practice_id), getString(R.string.param_appointment_id), getString(R.string.param_appointment_type), getString(R.string.param_is_guest)};
            Object[] values = {getAppointment().getMetadata().getPracticeId(), getAppointmentId(), getAppointment().getPayload().getVisitType().getName(), false};
            MixPanelUtil.logEvent(getString(R.string.event_checkout_completed), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_checkout_completed), 1);
            MixPanelUtil.endTimer(getString(R.string.timer_checkout));
        }
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

    WorkflowServiceCallback resourcesToScheduleCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            String resourcesToScheduleString = gson.toJson(workflowDTO);
            AppointmentsResultModel resultModel = gson.fromJson(resourcesToScheduleString,
                    AppointmentsResultModel.class);
            appointmentsResultModel.getPayload().setResourcesToSchedule(resultModel.getPayload()
                    .getResourcesToSchedule());
            showChooseProviderDialogFragment();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void showChooseProviderDialogFragment() {
        PracticeChooseProviderDialog fragment = PracticeChooseProviderDialog
                .newInstance(appointmentsResultModel,
                        Label.getLabel("practice_list_select_a_provider"),
                        Label.getLabel("practice_list_continue"));
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
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

        MixPanelUtil.logEvent(getString(R.string.event_payment_start_prepayment));
    }

    @Override
    public void onPaymentDismissed() {

    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                                   AppointmentsResultModel appointmentsResultModel,
                                   ResourcesToScheduleDTO resourcesToScheduleDTO) {
        if (getSupportFragmentManager().findFragmentById(R.id.root_layout)
                instanceof NextAppointmentFragmentInterface) {
            ((NextAppointmentFragmentInterface) getSupportFragmentManager()
                    .findFragmentById(R.id.root_layout))
                    .setSelectedProvider(appointmentResourcesDTO.getResource().getProvider(), true);
        }
    }

    private void logCheckoutCancelled() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        String currentStep = null;
        if (currentFragment instanceof NextAppointmentFragment) {
            currentStep = getString(R.string.step_appointment);
        } else if (currentFragment instanceof ResponsibilityCheckOutFragment) {
            currentStep = getString(R.string.step_payment);
        } else if (currentFragment instanceof CheckOutFormFragment) {
            currentStep = getString(R.string.step_checkout_forms);
        }
        MixPanelUtil.logEvent(getString(R.string.event_checkout_cancelled), getString(R.string.param_last_completed_step), currentStep);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("shouldReload", true);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (currentFragment instanceof TranslatableFragment) {
            ((TranslatableFragment) currentFragment).saveInstanceForTranslation(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (currentFragment instanceof TranslatableFragment) {
            ((TranslatableFragment) currentFragment).restoreInstanceForTranslation(savedInstanceState);
        }
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        if(continuePaymentsDTO != null) {
            PracticeNavigationHelper.navigateToWorkflow(getContext(), continuePaymentsDTO);
            continuePaymentsDTO = null;
        }else{
            //this may be a result of a fail on the continue call so lets try to continue again
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            AppointmentDTO appointmentDTO = getAppointment();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
            queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
            queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
            queryMap.put("payment_plan", "true");

            TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(transition, getCompletePlanAction(workflowDTO, 0, true), queryMap);
        }
    }

    private WorkflowServiceCallback getCompletePlanAction(final WorkflowDTO paymentPlanWorkflowDTO, final int mode, final boolean hasDisplayedConfirm){
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                if(NavigationStateConstants.PAYMENTS.equals(workflowDTO.getState())){
                    if(!hasDisplayedConfirm) {
                        //need to display payments again, so we need to display this payment plan confirmation first
                        //hold the workflow response so that we can navigate to is after they click ok in confirm
                        continuePaymentsDTO = workflowDTO;
                        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, paymentPlanWorkflowDTO);
                        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment.newInstance(paymentPlanWorkflowDTO, getPracticeInfo(paymentsModel), mode);
                        displayDialogFragment(confirmationFragment, false);
                    }else {
                        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                    }
                }else{
                    //done with checkin.. need to display success and pass the payment plan success
                    PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper
                            .getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);

                    WorkFlowRecord workFlowRecord = new WorkFlowRecord(paymentPlanWorkflowDTO);
                    workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

                    Bundle extra = new Bundle();
                    extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
                    extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, true);
                    extra.putInt(CarePayConstants.EXTRA_CONFIRMATION_MODE, mode);

                    appointmentsResultModel.getMetadata().getLinks().setPinpad(practiceAppointmentDTO
                            .getMetadata().getLinks().getPinpad());
                    appointmentsResultModel.getMetadata().getTransitions()
                            .setPracticeMode(practiceAppointmentDTO.getMetadata().getTransitions().getPracticeMode());
                    extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                            DtoHelper.getStringDTO(appointmentsResultModel));
                    DtoHelper.bundleDto(extra, appointmentsResultModel.getPayload().getAppointments().get(0));
                    extra.putBoolean("isCheckOut", true);

                    //get the appointment transitions from the Demo payload
                    AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
                    extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                            DtoHelper.getStringDTO(appointmentsResultModel));
                    Intent completeIntent = new Intent(getContext(), CompleteCheckActivity.class);
                    completeIntent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
                    completeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(completeIntent);

                    completeCheckout();

                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                //could not continue for some reason so lets show the confirmation dialog anyway
                PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, paymentPlanWorkflowDTO);
                PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment.newInstance(paymentPlanWorkflowDTO, getPracticeInfo(paymentsModel), mode);
                displayDialogFragment(confirmationFragment, false);
            }
        };
    }

}
