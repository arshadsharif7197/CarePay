package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dtos.PracticeAppointmentDTO;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.dobverification.DoBVerificationActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.PatientModeDemographicsPresenter;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckInFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.IcicleInterface;
import com.carecloud.carepaylibray.media.MediaResultListener;
import com.carecloud.carepaylibray.medications.fragments.AllergiesFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class PatientModeCheckinActivity extends BasePracticeActivity implements
        DemographicsView, PaymentNavigationCallback, PaymentMethodDialogInterface,
        PaymentPlanCompletedInterface, PaymentPlanCreateInterface {

    public final static int SUBFLOW_PAYMENTS = 3;
    private PatientModeDemographicsPresenter presenter;
    private PaymentsModel paymentDTO;
    private View[] checkInFlowViews;
    private MediaResultListener resultListener;
    private WorkflowDTO continuePaymentsDTO;
    private boolean paymentStarted = false;
    private WorkflowDTO paymentConfirmationWorkflow;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;
    private String practiceManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if (tag != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof IcicleInterface) {
                    icicle = ((IcicleInterface) fragment).popData();
                    icicle.putAll(savedInstanceState);
                }
            }
        }
        super.onCreate(icicle);
        setContentView(R.layout.activity_demographic_review);
        practiceManagement = getApplicationPreferences().getStartPracticeManagement();


        presenter = new PatientModeDemographicsPresenter(this, icicle, this);
        patientResponsibilityViewModel = new ViewModelProvider(this).get(PatientResponsibilityViewModel.class);

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        if (extra.getBoolean(CarePayConstants.HANDLE_HOME, false)) {
            presenter.setHandleHomeButton(true);
        }

        initializeHomeButton();
        initializeLeftNavigation();
        initializeLanguageSpinner();

        if (ApplicationPreferences.getInstance().isDobRequired()) {
            startDOBVerification();
        }
    }

    private void startDOBVerification() {
        WorkFlowRecord workFlowRecord = new WorkFlowRecord(getConvertedDTO(WorkflowDTO.class));
        workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

        Intent intent = new Intent(getContext(), DoBVerificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(WorkflowDTO.class.getName(), workFlowRecord.save(getContext()));
        intent.putExtras(bundle);

        startActivityForResult(intent, CarePayConstants.DOB_VERIFICATION_REQUEST);
    }

    private void initializeLanguageSpinner() {
        final TextView languageSwitch = findViewById(R.id.languageSpinner);
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), true,
                presenter.getLanguages(), language -> changeLanguage(presenter.getLanguageLink(),
                language.getCode().toLowerCase(), headers,
                () -> callSelfService(languageSwitch)));
        languageSwitch.setOnClickListener(view -> {
            int offsetX = view.getWidth() / 2 - popupPickerLanguage.getWidth() / 2;
            int offsetY = -view.getHeight() - popupPickerLanguage.getHeight();
            popupPickerLanguage.showAsDropDown(view, offsetX, offsetY);
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
    }

    private void callSelfService(final TextView languageSwitch) {
        Map<String, String> query = new HashMap<>();
        query.put("appointment_id", presenter.appointmentId);
        getWorkflowServiceHelper().execute(presenter.getDemographicDTO().getMetadata().getLinks().getSelf(),
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        presenter.changeLanguage(workflowDTO);
                        changeMenuLanguage();
                        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        hideProgressDialog();
                        showErrorToast(exceptionMessage);
                    }
                }, query);
    }

    private void changeMenuLanguage() {
        initializeLeftNavigation();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if (tag != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof IcicleInterface) {
                    icicle = ((IcicleInterface) fragment).popData();
                    icicle.putAll(savedInstanceState);
                }
            }
        }
        super.onRestoreInstanceState(icicle);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        Fragment fragment = presenter.getCurrentFragment();
        if (fragment instanceof IcicleInterface) {
            ((IcicleInterface) fragment).pushData((Bundle) icicle.clone());
        }
        icicle.clear();
        presenter.onSaveInstanceState(icicle);
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public DemographicsPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setMediaResultListener(MediaResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void completeCheckIn(WorkflowDTO workflowDTO) {
        Intent intent = new Intent(this, CompleteCheckActivity.class);
        WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
        workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

        Bundle extra = new Bundle();
        extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save(getContext()));
        intent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        presenter.logCheckinCompleted(false, false, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultListener != null) {
            resultListener.handleActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initializeHomeButton() {
        findViewById(R.id.checkinHomeClickable).setOnClickListener(view -> {
            if (!presenter.handleHomeButtonClick()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(null, null);
                confirmDialogFragment.setNegativeAction(true);
                confirmDialogFragment.setCallback(() -> {
                    setResult(CarePayConstants.HOME_PRESSED);
                    presenter.logCheckinCancelled();
                    finish();
                });
                String tag = confirmDialogFragment.getClass().getName();
                confirmDialogFragment.show(ft, tag);
            }
        });
    }

    private void initializeLeftNavigation() {
        TextView title = findViewById(R.id.checkInLeftNavigationTitle);
        if (title != null) {
            title.setText(Label.getLabel("practice_checkin_header_label"));
        }
        checkInFlowViews = new View[]{
                findViewById(R.id.checkin_flow_demographics),
                findViewById(R.id.checkin_flow_consent),
                findViewById(R.id.checkin_flow_medications),
                findViewById(R.id.checkin_flow_allergies),
                findViewById(R.id.checkin_flow_intake),
                findViewById(R.id.checkin_flow_payment)
        };

        String[] labels = new String[]{
                Label.getLabel("demographics_patient_information_title"),
                Label.getLabel("demographics_consent_forms_title"),
                Label.getLabel("demographics_meds_title"),
                Label.getLabel("demographics_allergies_title"),
                Label.getLabel("practice_chekin_section_intake_forms"),
                Label.getLabel("demographics_payment_title")
        };

        for (int i = 0; i < checkInFlowViews.length; i++) {
            View view = checkInFlowViews[i];
            ((TextView) view.findViewById(R.id.checkin_flow_title)).setText(labels[i]);
        }

        hideIntakeView();

    }

    private void hideIntakeView() {
        // Hide intakeView for talkEHR Practices on Phase 1 integration
        if (practiceManagement.equalsIgnoreCase(Defs.START_PM_TALKEHR))
            findViewById(R.id.checkin_flow_intake).setVisibility(View.GONE);
    }

    /**
     * Consent form navigation
     *
     * @param workflowJson intake DTO
     */
    public void getPaymentInformation(final String workflowJson) {
        patientResponsibilityViewModel.setPaymentsModel(DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson));
        paymentStarted = true;
        ResponsibilityCheckInFragment responsibilityFragment = new ResponsibilityCheckInFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(PaymentsModel.class.getName(), workflowJson);
        responsibilityFragment.setArguments(bundle);
        presenter.navigateToFragment(responsibilityFragment, true, true);
        updateCheckInFlow(CheckinFlowState.PAYMENT, 1, 1);

        new Thread(() -> paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson)).start();
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        //nothing to do here
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentDTO, owedAmount);
        displayDialogFragment(dialog, false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                getString(R.string.param_practice_id),
                selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);

        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };
        Object[] values = {amount,
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
//                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
//            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
//                    selectedPaymentMethod.getLabel(), amount);
//            displayDialogFragment(fragment, false);
//        } else {
//            showAddCard(amount, paymentsModel);
//        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment.newInstance(paymentsModel, amount);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        if (paymentStarted) {
            paymentConfirmationWorkflow = workflowDTO;
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queryMap.put("practice_id", userPracticeDTO.getPracticeId());
            queryMap.put("appointment_id", getAppointmentId());
            queryMap.put("patient_id", userPracticeDTO.getPatientId());
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("transition", "true");

            TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(continueTransition, continueCallback, queryMap, header);
        } else {
            showAllDone(workflowDTO);
        }
    }

    private void showAllDone(WorkflowDTO workflowDTO) {
        Intent intent = getIntent();
        PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper
                .getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);
        if (practiceAppointmentDTO == null) {
            showErrorNotification("Error Checking-in Appointment");
            return;
        }
        if (paymentConfirmationWorkflow != null) {
            WorkFlowRecord workFlowRecord = new WorkFlowRecord(paymentConfirmationWorkflow);
            workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

            Bundle extra = new Bundle();
            extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save(getContext()));
            extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, true);
            DtoHelper.bundleDto(extra, presenter.getAppointmentPayload());
            //get the appointment transitions from the Demo payload
            AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            appointmentsResultModel.getMetadata().getLinks().setShop(practiceAppointmentDTO
                    .getMetadata().getLinks().getShop());
            extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                    DtoHelper.getStringDTO(appointmentsResultModel));
            Intent completeIntent = new Intent(this, CompleteCheckActivity.class);
            completeIntent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
            completeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(completeIntent);
        } else {
            setResult(CarePayConstants.HOME_PRESSED, intent);
            finish();
        }

        presenter.logCheckinCompleted(false, false, null);
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
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }

    @Override
    public void onPaymentCashFinished() {

    }

    @Nullable
    @Override
    public String getAppointmentId() {
        if (presenter != null) {
            return presenter.getAppointmentId();
        }
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        if (presenter != null) {
            return presenter.getAppointment();
        }
        return null;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        presenter.navigateToFragment(fragment, addToBackStack);
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
//                .newInstance(paymentsModel, paymentPlanPostModel);
//        displayDialogFragment(fragment, false);
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
        getWorkflowServiceHelper().execute(transition, getCompletePlanAction(workflowDTO,
                PaymentPlanConfirmationFragment.MODE_CREATE, false), queryMap);
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
        getWorkflowServiceHelper().execute(transition, getCompletePlanAction(workflowDTO,
                PaymentPlanConfirmationFragment.MODE_ADD, false), queryMap);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        if (workflowDTO != null) {
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
                getIntent().putExtra(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());
                completePaymentProcess(workflowDTO);
            }
        } else {
            new Handler().postDelayed(() -> {
                getAppAuthorizationHelper().setUser(null);
                startActivity(new Intent(this, CloverMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }, 2000);
        }
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        hideIntakeView();

        for (int i = 0; i < checkInFlowViews.length; i++) {
            View flowView = checkInFlowViews[i];
            CarePayTextView section = flowView.findViewById(R.id.checkin_flow_title);
            TextView progressTextView = flowView.findViewById(R.id.checkin_flow_progress);

            if (flowState.ordinal() == i) {
                if (section.getFontAttribute() == CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT) {
                    section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
                }
                section.setSelected(true);
                section.setEnabled(true);
                if (totalPages > 1) {
                    String progressLabel = currentPage + " " + Label.getLabel("of") + " " + totalPages;
                    progressTextView.setVisibility(View.VISIBLE);
                    progressTextView.setText(progressLabel);
                }
            } else {
                if (section.getFontAttribute() == CustomAssetStyleable.GOTHAM_ROUNDED_BOLD) {
                    section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
                }
                if (flowState.ordinal() > i) {
                    section.setEnabled(false);
                }
                section.setSelected(false);
                progressTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        BaseCheckinFragment fragment;
        try {
            fragment = (BaseCheckinFragment) fragmentManager.findFragmentById(R.id.root_layout);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            super.onBackPressed();
            return;
        }
        if (fragment.getClass().getName().equalsIgnoreCase("com.carecloud.carepaylibray.medications.fragments.MedicationsFragment")) {
            MedicationsAllergiesEmptyFragment medicationsAllergiesEmptyFragment = (MedicationsAllergiesEmptyFragment)
                    fragmentManager.findFragmentByTag("com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment");

            MedicationsFragment medicationsFragment = (MedicationsFragment) fragment;
            if (medicationsFragment.shouldRemove && medicationsAllergiesEmptyFragment != null) {
                fragmentManager.popBackStack(medicationsAllergiesEmptyFragment.getClass().getName(), false ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
            }

        }
        if (fragment.getClass().getName().equalsIgnoreCase("com.carecloud.carepaylibray.medications.fragments.AllergiesFragment")) {
            MedicationsAllergiesEmptyFragment medicationsAllergiesEmptyFragment = (MedicationsAllergiesEmptyFragment)
                    fragmentManager.findFragmentByTag("com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment");

            AllergiesFragment allergiesFragment = (AllergiesFragment) fragment;
            if (allergiesFragment.shouldRemove && medicationsAllergiesEmptyFragment != null) {
                fragmentManager.popBackStack(medicationsAllergiesEmptyFragment.getClass().getName(), false ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
            }

        }
        if (fragment == null || !fragment.navigateBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void navigateToConsentForms(WorkflowDTO workflowDTO) {
        presenter.navigateToConsentForms(workflowDTO);
    }

    @Override
    public void navigateToIntakeForms(WorkflowDTO workflowDTO) {
        presenter.navigateToIntakeForms(workflowDTO);
    }

    @Override
    public void navigateToThirdParty(WorkflowDTO workflowDTO) {
        presenter.navigateToThirdParty(workflowDTO);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        presenter.navigateToMedicationsAllergy(workflowDTO);
    }

    @Override
    public void navigateToMedications(WorkflowDTO workflowDTO) {
        presenter.navigateToMedications(workflowDTO, true);
    }

    @Override
    public void navigateToAllergy(WorkflowDTO workflowDTO) {
        presenter.navigateToAllergy(workflowDTO, true);
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
                    Intent intent = getIntent();
                    setResult(CarePayConstants.HOME_PRESSED, intent);
                    finish();
                }
            };
            dialogFragment.setOnDismissListener(dismissListener);
            displayDialogFragment(dialogFragment, false);

        }
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transition, getCashPaymentCallback(paymentsModel), queryMap);
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        if (continuePaymentsDTO != null) {
            PracticeNavigationHelper.navigateToWorkflow(getContext(), continuePaymentsDTO);
            continuePaymentsDTO = null;
        } else {
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

    private WorkflowServiceCallback getCompletePlanAction(final WorkflowDTO paymentPlanWorkflowDTO,
                                                          final int mode,
                                                          final boolean hasDisplayedConfirm) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                if (NavigationStateConstants.PAYMENTS.equals(workflowDTO.getState())) {
                    if (!hasDisplayedConfirm) {
                        //need to display payments again, so we need to display this payment plan confirmation first
                        //hold the workflow response so that we can navigate to is after they click ok in confirm
                        continuePaymentsDTO = workflowDTO;
                        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, paymentPlanWorkflowDTO);
                        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                                .newInstance(paymentPlanWorkflowDTO, getPracticeInfo(paymentsModel), mode);
                        displayDialogFragment(confirmationFragment, false);
                    } else {
                        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                    }
                } else {
                    //done with checkin.. need to display success and pass the payment plan success
                    WorkFlowRecord workFlowRecord = new WorkFlowRecord(paymentPlanWorkflowDTO);
                    workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

                    Bundle extra = new Bundle();
                    extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save(getContext()));
                    extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, true);
                    extra.putInt(CarePayConstants.EXTRA_CONFIRMATION_MODE, mode);
                    DtoHelper.bundleDto(extra, presenter.getAppointmentPayload());

                    //get the appointment transitions from the Demo payload
                    AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
                    extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                            DtoHelper.getStringDTO(appointmentsResultModel));
                    Intent completeIntent = new Intent(getContext(), CompleteCheckActivity.class);
                    completeIntent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
                    completeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(completeIntent);

                    presenter.logCheckinCompleted(false, false, null);

                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                //could not continue for some reason so lets show the confirmation dialog anyway
                PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, paymentPlanWorkflowDTO);
                PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                        .newInstance(paymentPlanWorkflowDTO, getPracticeInfo(paymentsModel), mode);
                displayDialogFragment(confirmationFragment, false);
            }
        };
    }

    private WorkflowServiceCallback getCashPaymentCallback(final PaymentsModel paymentsModel) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();

                //need payment workflow to make it to checkout activity
                WorkflowDTO paymentWorkflow = DtoHelper.getConvertedDTO(WorkflowDTO.class, DtoHelper.getStringDTO(paymentsModel));
                paymentWorkflow.setState(workflowDTO.getState());

                Intent intent = new Intent(getContext(), CompleteCheckActivity.class);
                WorkFlowRecord workFlowRecord = new WorkFlowRecord(paymentWorkflow);
                workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

                Bundle extra = new Bundle();
                extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save(getContext()));
                extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, false);
                extra.putBoolean(CarePayConstants.EXTRA_PAYMENT_CASH, true);

                DtoHelper.bundleDto(extra, presenter.getAppointmentPayload());
                //get the appointment transitions from the Demo payload
                AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
                extra.putString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS,
                        DtoHelper.getStringDTO(appointmentsResultModel));

                intent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                presenter.logCheckinCompleted(false, false, null);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public DTO getDto() {
        return null;
    }

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return presenter.getLogoutTransition();
    }
}
