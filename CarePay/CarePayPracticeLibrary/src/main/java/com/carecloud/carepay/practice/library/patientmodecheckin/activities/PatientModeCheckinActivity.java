package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

public class PatientModeCheckinActivity extends BasePracticeActivity implements PaymentNavigationCallback,DemographicsView {

    private DemographicsPresenter presenter;

    public final static int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static int SUBFLOW_PAYMENTS = 3;

    private View[] checkinFlowViews;
    private View checkinDemographics;
    private View checkinConsent;
    private View checkinMedications;
    private View checkinIntake;
    private View checkinPayment;

    private CarePayTextView backButton;
    private ImageView logoImageView;
    private ImageView homeClickable;

    private PaymentsModel paymentDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);

        instantiateViewsRefs();
        initializeViews();
        initializeCheckInViews();

        presenter = new DemographicsPresenterImpl(this);
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

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    private void instantiateViewsRefs() {
        backButton = (CarePayTextView) findViewById(R.id.checkinBack);
        logoImageView = (ImageView) findViewById(R.id.checkinLogo);
        homeClickable = (ImageView) findViewById(R.id.checkinHomeClickable);
    }

    private void initializeViews() {
        toggleVisibleBackButton(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        homeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(CarePayConstants.HOME_PRESSED);
                finish();
            }
        });
    }

    /**
     * Getter
     *
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return presenter.getDemographicDTO();
    }

    /**
     * Toogle visible the back button (and hides/shows accordingly the logo)
     *
     * @param isVisible Whether to toggle visible
     */
    public void toggleVisibleBackButton(boolean isVisible) {
        if (isVisible) {
            backButton.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onBackPressed() {
//        try {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            BaseCheckinFragment fragment = (BaseCheckinFragment) fragmentManager.findFragmentById(R.id.checkInContentHolderId);
//            if (fragment != null && !fragment.navigateBack()) {
//                super.onBackPressed();
//            }
//        } catch (ClassCastException cce) {
//            cce.printStackTrace();
//            super.onBackPressed();
//        }
//    }

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

    private void initializeCheckInViews() {
        checkinDemographics = findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_demographics);
        checkinConsent = findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_consent);
        checkinMedications = findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_medications);
        checkinIntake = findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_intake);
        checkinPayment = findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_payment);

        checkinFlowViews = new View[]{checkinDemographics, checkinConsent, checkinMedications, checkinIntake, checkinPayment};
        String[] checkinFlowLabels = new String[]{
                Label.getLabel("demographics_patient_information_title"),
                Label.getLabel("demographics_consent_forms_title"),
                Label.getLabel("demographics_meds_allergies_title"),
                Label.getLabel("practice_chekin_section_intake_forms"),
                Label.getLabel("demographics_payment_title")
        };
        for (int i = 0; i < checkinFlowViews.length; i++) {
            View view = checkinFlowViews[i];
            TextView textView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_title);
            textView.setText(checkinFlowLabels[i]);
        }
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        View view = null;
        switch (flowState) {
            case DEMOGRAPHICS:
                view = checkinDemographics;
                break;
            case CONSENT:
                view = checkinConsent;
                break;
            case MEDICATIONS_AND_ALLERGIES:
                view = checkinMedications;
                break;
            case INTAKE:
                view = checkinIntake;
                break;
            case PAYMENT:
                view = checkinPayment;
                break;
            default:
                return;
        }

        for (View flowView : checkinFlowViews) {
            CarePayTextView section = (CarePayTextView) flowView.findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_title);
            TextView progress = (TextView) flowView.findViewById(com.carecloud.carepaylibrary.R.id.checkin_flow_progress);

            if (flowView == view) {
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
                if (totalPages > 1) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setText(String.format("%d %s %d", currentPage, "of", totalPages));//todo label for "of"
                } else {
                    progress.setVisibility(View.GONE);
                }
            } else {
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
                progress.setVisibility(View.GONE);
            }
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
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowJson);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, workflowJson);
        responsibilityFragment.setArguments(bundle);
        presenter.navigateToFragment(responsibilityFragment, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);

            }
        }).start();
    }

    /**
     * Entry point for navigating to medication fragment
     *
     * @param workflowDTO navigation dto
     */
    public void loadMedicationsAllergy(String workflowDTO) {
        presenter.setMedicationsAllergiesDto(DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, workflowDTO));
        CheckinMedicationsAllergyFragment medicationsAllergyFragment = new CheckinMedicationsAllergyFragment();
        Bundle args = new Bundle();
        args.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, workflowDTO);

        medicationsAllergyFragment.setArguments(args);
        presenter.navigateToFragment(medicationsAllergyFragment, true);
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
    public void startPaymentProcess(PaymentsModel paymentsModel) {

    }

    @Override
    public void startPartialPayment(double owedAmount) {
        String tag = PracticePartialPaymentDialogFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment.newInstance(paymentDTO, owedAmount);
        dialog.show(ft, tag);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment.newInstance(paymentDTO, amount);
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
}
