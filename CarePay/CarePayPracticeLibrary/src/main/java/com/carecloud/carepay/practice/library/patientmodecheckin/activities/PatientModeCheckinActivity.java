package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.demographics.fragments.HomeAlertDialogFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.PatientModeDemographicsPresenter;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckInFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.IcicleInterface;
import com.carecloud.carepaylibray.media.MediaResultListener;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

public class PatientModeCheckinActivity extends BasePracticeActivity implements
        DemographicsView, PaymentNavigationCallback, PaymentMethodDialogInterface {

    public final static int SUBFLOW_PAYMENTS = 3;

    private PatientModeDemographicsPresenter presenter;

    private PaymentsModel paymentDTO;

    private View[] checkInFlowViews;

    private MediaResultListener resultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if(savedInstanceState != null){
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if(tag != null){
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment instanceof IcicleInterface){
                    icicle = ((IcicleInterface) fragment).popData();
                    icicle.putAll(savedInstanceState);
                }
            }
        }
        super.onCreate(icicle);
        setContentView(R.layout.activity_demographic_review);

        initializeHomeButton();
        initializeLeftNavigation();
        presenter = new PatientModeDemographicsPresenter(this, icicle, this);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if(savedInstanceState != null){
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if(tag != null){
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment instanceof IcicleInterface){
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
        if(fragment != null &&  fragment instanceof IcicleInterface){
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
        extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
        intent.putExtra(CarePayConstants.EXTRA_BUNDLE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        findViewById(R.id.checkinHomeClickable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!presenter.handleHomeButtonClick()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    HomeAlertDialogFragment homeAlertDialogFragment = HomeAlertDialogFragment.newInstance(null, null);
                    homeAlertDialogFragment.setCallback(new HomeAlertDialogFragment.HomeAlertInterface() {
                        @Override
                        public void onAcceptExit() {
                            setResult(CarePayConstants.HOME_PRESSED);
                            finish();
                        }
                    });
                    String tag = homeAlertDialogFragment.getClass().getName();
                    homeAlertDialogFragment.show(ft, tag);
                }
            }
        });
    }

    private void initializeLeftNavigation() {
        checkInFlowViews = new View[]{
                findViewById(R.id.checkin_flow_demographics),
                findViewById(R.id.checkin_flow_consent),
                findViewById(R.id.checkin_flow_medications),
                findViewById(R.id.checkin_flow_intake),
                findViewById(R.id.checkin_flow_payment)
        };

        String[] labels = new String[]{
                Label.getLabel("demographics_patient_information_title"),
                Label.getLabel("demographics_consent_forms_title"),
                Label.getLabel("demographics_meds_allergies_title"),
                Label.getLabel("practice_chekin_section_intake_forms"),
                Label.getLabel("demographics_payment_title")
        };

        for (int i = 0; i < checkInFlowViews.length; i++) {
            View view = checkInFlowViews[i];
            ((TextView) view.findViewById(R.id.checkin_flow_title)).setText(labels[i]);
        }
    }

    /**
     * Consent form navigation
     *
     * @param workflowJson intake DTO
     */
    public void getPaymentInformation(final String workflowJson) {
        ResponsibilityCheckInFragment responsibilityFragment = new ResponsibilityCheckInFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PaymentsModel.class.getSimpleName(), workflowJson);
        responsibilityFragment.setArguments(bundle);
        presenter.navigateToFragment(responsibilityFragment, true);
        updateCheckInFlow(CheckinFlowState.PAYMENT, 1, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);

            }
        }).start();
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
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }
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
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        Intent intent = getIntent();
        String workflowString = intent.getStringExtra(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
        if (workflowString != null) {
            WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
            workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

            Bundle extra = new Bundle();
            extra.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
//            extra.putString(CarePayConstants.EXTRA_WORKFLOW, workflowDTO.toString());
            extra.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, true);
            DtoHelper.bundleDto(extra, presenter.getAppointmentPayload());
            //get the appointment transitions from the Demo payload
            AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
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
    }

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
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        presenter.navigateToFragment(fragment, true);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
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
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        for (int i = 0; i < checkInFlowViews.length; i++) {
            View flowView = checkInFlowViews[i];
            CarePayTextView section = (CarePayTextView) flowView.findViewById(R.id.checkin_flow_title);
            TextView progress = (TextView) flowView.findViewById(R.id.checkin_flow_progress);

            if (flowState.ordinal() == i) {
                if (section.getFontAttribute() == CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT) {
                    section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
                }
                section.setSelected(true);
                section.setEnabled(true);
                progress.setVisibility(View.VISIBLE);
                progress.setText(currentPage + " of " + totalPages); //TODO label for "of"
            } else {
                if (section.getFontAttribute() == CustomAssetStyleable.GOTHAM_ROUNDED_BOLD) {
                    section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
                }
                if (flowState.ordinal() > i) {
                    section.setEnabled(false);
                }
                section.setSelected(false);
                progress.setVisibility(View.GONE);
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
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        presenter.navigateToMedicationsAllergy(workflowDTO);
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
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {

    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {

    }
}
