package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinMedicationsAllergyFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IntakeFormsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.PracticeFormsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckInFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

public class PatientModeCheckinActivity extends BasePracticeActivity implements
        DemographicsView,
        PaymentNavigationCallback {

    public final static int SUBFLOW_PAYMENTS = 3;

    private DemographicsPresenter presenter;

    private PaymentsModel paymentDTO;

    private View[] checkInFlowViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);

        initializeHomeButton();
        initializeLeftNavigation();

        presenter = new DemographicsPresenterImpl(this, savedInstanceState, true);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        presenter.onSaveInstanceState(icicle);
        super.onSaveInstanceState(icicle);
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
                setResult(CarePayConstants.HOME_PRESSED);
                finish();
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
     * @param workflowJson consent DTO
     */
    public void getConsentFormInformation(String workflowJson) {
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowJson);

        PracticeFormsFragment fragment = new PracticeFormsFragment();
        fragment.setArguments(bundle);

        presenter.navigateToFragment(fragment, true);
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
    public void startPartialPayment(double owedAmount) {
        String tag = PracticePartialPaymentDialogFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentDTO, owedAmount);
        dialog.show(ft, tag);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentDTO, amount);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentDTO.getPaymentPayload().getPatientCreditCards() != null && !paymentDTO.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void completePaymentProcess(UpdatePatientBalancesDTO updatePatientBalancesDTO) {
        Intent intent = getIntent();
        setResult(CarePayConstants.HOME_PRESSED, intent);
        finish();
    }

    @Override
    public void cancelPaymentProcess(PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentPlanAction() {
        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        presenter.navigateToFragment(fragment, true);
    }

    @Override
    public void showPaymentConfirmation(PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);
        confirmationFragment.show(getSupportFragmentManager(), confirmationFragment.getClass().getSimpleName());
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        for (int i = 0; i < checkInFlowViews.length; i++) {
            View flowView = checkInFlowViews[i];
            CarePayTextView section = (CarePayTextView) flowView.findViewById(R.id.checkin_flow_title);
            TextView progress = (TextView) flowView.findViewById(R.id.checkin_flow_progress);

            if (flowState.ordinal() == i) {
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
                progress.setVisibility(View.VISIBLE);
                progress.setText(currentPage + " of " + totalPages); //TODO label for "of"
            } else {
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
                progress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BaseCheckinFragment fragment = (BaseCheckinFragment) fragmentManager.findFragmentById(R.id.root_layout);
            if (fragment == null || !fragment.navigateBack()) {
                super.onBackPressed();
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            super.onBackPressed();
        }
    }

    /**
     * Launch intake forms
     *
     * @param workflowJson workflowJson
     */
    public void startIntakeForms(String workflowJson) {
        IntakeResponseModel intakeResponseModel = getConvertedDTO(IntakeResponseModel.class, workflowJson);

        Gson gson = new Gson();
        String intakeFormDTO = gson.toJson(intakeResponseModel);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, intakeFormDTO);

        IntakeFormsFragment fragment = new IntakeFormsFragment();
        fragment.setArguments(bundle);
        presenter.navigateToFragment(fragment, true);
    }

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
                    PaymentsModel paymentsModel = gson.fromJson(jsonPayload, PaymentsModel.class);
                    showPaymentConfirmation(paymentsModel);
                }
                break;
            }
            default:
                //nothing
                return;
        }
    }

    /**
     * Entry point for navigating to medication fragment
     *
     * @param workflowDTO navigation dto
     */
    public void loadMedicationsAllergy(String workflowDTO) {
        CheckinMedicationsAllergyFragment medicationsAllergyFragment = new CheckinMedicationsAllergyFragment();
        presenter.setMedicationsAllergiesDto(DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, workflowDTO));
        Bundle args = new Bundle();
        args.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, workflowDTO);

        medicationsAllergyFragment.setArguments(args);
        presenter.navigateToFragment(medicationsAllergyFragment, true);
    }
}
