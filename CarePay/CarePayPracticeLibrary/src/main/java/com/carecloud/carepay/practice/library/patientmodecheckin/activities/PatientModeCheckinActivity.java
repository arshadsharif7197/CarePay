package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinMedicationsAllergyFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IntakeFormsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.PracticeFormsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckInFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticePartialPaymentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */
public class PatientModeCheckinActivity extends BasePracticeActivity implements
//        IFragmentCallback,
        DemographicsReviewLabelsHolder, DemographicsLabelsHolder,
        HealthInsuranceFragment.InsuranceDocumentScannerListener, MedicationsAllergyFragment.MedicationAllergyCallback,
        CheckinDemographicsInterface, MedicationAllergySearchFragment.MedicationAllergySearchCallback,
        PaymentNavigationCallback, CheckinFlowCallback,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        PersonalInfoFragment.UpdateProfilePictureListener {


    public final static int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static int SUBFLOW_CONSENT = 1;
    public final static int SUBFLOW_INTAKE = 2;
    public final static int SUBFLOW_PAYMENTS = 3;
    private static final int NUM_OF_SUBFLOWS = 4;

    //demographics nav
    private Map<Integer, CheckInDemographicsBaseFragment> demographicFragMap = new HashMap<>();
    private int currentDemographicStep = 1;
    //

    private DemographicDTO demographicDTO;
    private CarePayTextView backButton;
    private ImageView logoImageView;
    private ImageView homeClickable;

    private View[] sectionTitleTextViews;
//    private TextView consentCounterTextView;
//    private TextView intakeCounterTextView;

    // Using for consent form
    // TODO : Will be create separate fragment in next sprint
//    private ConsentFormLabelsDTO consentFormLabelsDTO;
//
//    private AppointmentsPayloadDTO appointmentsPayloadDTO;
//
//    private FormId showingForm = FormId.FORM1;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private PaymentsModel paymentDTO;

    // Intake Form
    private IntakeResponseModel intakeResponseModel;
    /**
     * IntakeForm Navigation
     */

//    private FlowStateInfo currentFlowStateInfo;


    private View[] checkinFlowViews;
    private String[] checkinFlowLabels;
    private View checkinDemographics;
    private View checkinConsent;
    private View checkinMedications;
    private View checkinIntake;
    private View checkinPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);
        demographicDTO = getConvertedDTO(DemographicDTO.class);

        instantiateViewsRefs();
        initializeViews();

        initializeCheckinViews();

        // place the initial fragment
        demographicFragMap.put(1, new PersonalInfoFragment());
        demographicFragMap.put(2, new AddressFragment());
        demographicFragMap.put(3, new DemographicsFragment());
        demographicFragMap.put(4, new IdentificationFragment());
        demographicFragMap.put(5, new HealthInsuranceFragment());

        navigateToDemographicFragment(1);
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

    private void instantiateViewsRefs() {
        backButton = (CarePayTextView) findViewById(R.id.checkinBack);
        logoImageView = (ImageView) findViewById(R.id.checkinLogo);
        homeClickable = (ImageView) findViewById(R.id.checkinHomeClickable);

        sectionTitleTextViews = new View[NUM_OF_SUBFLOWS];
        sectionTitleTextViews[SUBFLOW_DEMOGRAPHICS_INS] = findViewById(R.id.checkinSectionDemogrInsTitle);
        sectionTitleTextViews[SUBFLOW_CONSENT] = findViewById(R.id.checkinSectionConsentFormsTitle);
        sectionTitleTextViews[SUBFLOW_INTAKE] = findViewById(R.id.checkinSectionIntakeFormsTitle);
        sectionTitleTextViews[SUBFLOW_PAYMENTS] = findViewById(R.id.checkinSectionPaymentsTitle);

//        consentCounterTextView = (TextView) findViewById(R.id.checkinConsentFormCounterTextView);
//        intakeCounterTextView = (TextView) findViewById(R.id.checkinIntakeFormCounterTextView);
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
                //PatientModeCheckinActivity.this.finish();
                setResult(CarePayConstants.HOME_PRESSED);
                finish();
            }
        });
    }

    private void initializeCheckinViews() {
        checkinDemographics = findViewById(R.id.checkin_flow_demographics);
        checkinConsent = findViewById(R.id.checkin_flow_consent);
        checkinMedications = findViewById(R.id.checkin_flow_medications);
        checkinIntake = findViewById(R.id.checkin_flow_intake);
        checkinPayment = findViewById(R.id.checkin_flow_payment);

        checkinFlowViews = new View[]{checkinDemographics, checkinConsent, checkinMedications, checkinIntake, checkinPayment};
        checkinFlowLabels = new String[]{demographicDTO.getMetadata().getLabels().getDemographicsPatientInformationTitle(),
                Label.getLabel("demographics_consent_forms_title"), Label.getLabel("demographics_meds_allergies_title"), Label.getLabel("practice_chekin_section_intake_forms"), Label.getLabel("demographics_payment_title")};
        for (int i = 0; i < checkinFlowViews.length; i++) {
            View view = checkinFlowViews[i];
            TextView textView = (TextView) view.findViewById(R.id.checkin_flow_title);
            textView.setText(checkinFlowLabels[i]);
        }
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.checkInContentHolderId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * Getter
     *
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }

    /**
     * Re-sets the global DTO from a string JSON
     *
     * @param jsonString The main DTO as string
     */
    public void resetDemographicDTO(String jsonString) {
        this.demographicDTO = getConvertedDTO(DemographicDTO.class, jsonString);
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        return demographicDTO.getMetadata().getLabels();
    }

//    /**
//     * Consent form
//     */
//    @Override
//    public void signButtonClicked() {
//        Intent intent = new Intent(this, PracticeAppSignatureActivity.class);
//
//        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
//        intent.putExtra("consentform", showingForm);
//        if (PracticeAppSignatureActivity.numOfLaunches == 2) {
//            // pass the whole DTO
//            Gson gson = new Gson();
//            String consentformDTO = gson.toJson(consentFormDTO);
//            intent.putExtra("consentform_model", consentformDTO);
//        }
//        if (showingForm == FormId.FORM1) {
//            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());
//            intent.putExtra("Subtitle", consentFormLabelsDTO.getConsentReadCarefullyWarning());
//        } else if (showingForm == FormId.FORM2) {
//            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
//            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
//        } else {
//            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignHipaaAgreementTitle());
//            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
//        }
//        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);
//    }
//


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

        navigateToFragment(fragment, true);
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
        navigateToFragment(responsibilityFragment, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);

            }
        }).start();
    }


    @Override
    public void showMedicationSearch() {
        MedicationAllergySearchFragment medicationAllergySearchFragment = new MedicationAllergySearchFragment();
        if (medicationsAllergiesDTO != null) {
            Gson gson = new Gson();
            String jsonExtra = gson.toJson(medicationsAllergiesDTO);
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, jsonExtra);

            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA, MedicationAllergySearchFragment.SearchMode.MEDICATION.name());
            medicationAllergySearchFragment.setArguments(bundle);

        }

        medicationAllergySearchFragment.show(getSupportFragmentManager(), medicationAllergySearchFragment.getClass().getSimpleName());
    }

    @Override
    public void showAllergiesSearch() {

    }

    @Override
    public void medicationSubmitSuccess(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }

    @Override
    public void medicationSubmitFail(String message) {
        showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
        Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), message);
    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {
        MedicationsAllergyFragment medicationsAllergyFragment = (MedicationsAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        medicationsAllergyFragment.addItem(item);
    }


    @Override
    public void startPartialPayment(double owedAmount) {
        new PracticePartialPaymentDialog(this, paymentDTO).show();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodFragment fragment = PracticePaymentMethodFragment
                .newInstance(paymentDTO, amount);
        navigateToFragment(fragment, true);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (!isCloverDevice && paymentDTO.getPaymentPayload().getPatientCreditCards() != null && !paymentDTO.getPaymentPayload().getPatientCreditCards().isEmpty()) {
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
        DialogFragment fragment = new AddNewCreditCardFragment();
        fragment.setArguments(args);
//        navigateToFragment(fragment, true);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentPlanAction() {
        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        navigateToFragment(fragment, true);
    }

    @Override
    public void showReceipt(PaymentsModel paymentsModel) {
        PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(this, paymentsModel, paymentsModel);
        receiptDialog.show();
    }

    @Override
    public void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model) {

    }

    @Override
    public void navigateToParentFragment() {

    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {

    }

    @Override
    public void disableMainButton(boolean isDisabled) {

    }


//    /**
//     * Enum to identify the forms
//     */
//    public enum FormId {
//        FORM1, FORM2, FORM3, NONE;
//
//        /**
//         * Go to next
//         */
//        public FormId next() {
//            if (this.equals(FORM1)) {
//                return FORM2;
//            } else if (this.equals(FORM2)) {
//                return FORM3;
//            }
//            return NONE;
//        }
//
//        /**
//         * Go to prev
//         */
//        public FormId prev() {
//            if (this.equals(FORM3)) {
//                return FORM2;
//            } else if (this.equals(FORM2)) {
//                return FORM1;
//            }
//            return NONE;
//        }
//    }


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


    @Override
    public void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
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

        updateCheckinFlow(view, totalPages, currentPage);
    }

    private void updateCheckinFlow(View highlightView, int totalPages, int currentPage) {
        if (highlightView == null) {
            return;
        }

        for (View flowView : checkinFlowViews) {
            CarePayTextView section = (CarePayTextView) flowView.findViewById(R.id.checkin_flow_title);
            TextView progress = (TextView) flowView.findViewById(R.id.checkin_flow_progress);

            if (flowView == highlightView) {
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


    @Override
    public void onBackPressed() {
        setCurrentStep(currentDemographicStep - 1);
        try {
            BaseCheckinFragment fragment = (BaseCheckinFragment) getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
            if (fragment != null && !fragment.navigateBack()) {
                super.onBackPressed();
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            super.onBackPressed();
        }
    }

//    private void popFragStack() {
//        if (getFragmentManager().getBackStackEntryCount() > 1) {
//            getFragmentManager().popBackStack();
//            return;
//        }
//        super.onBackPressed();
//    }


    /**
     * Launch intake forms
     *
     * @param workflowJson workflowJson
     */
    public void startIntakeForms(String workflowJson) {
        intakeResponseModel = getConvertedDTO(IntakeResponseModel.class, workflowJson);

        Gson gson = new Gson();
        String intakeFormDTO = gson.toJson(intakeResponseModel);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, intakeFormDTO);

        IntakeFormsFragment fragment = new IntakeFormsFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }


    @Override
    public void navigateToConsentFlow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
    }

    private DemographicIdDocPayloadDTO getDemographicIdDocPayloadDTO() {
        DemographicIdDocPayloadDTO demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();

        if (demographicDTO.getPayload().getDemographics() != null) {
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i < size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

        return demographicIdDocPayloadDTO;
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        if (data.hasExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA)) {
            Intent intent = getIntent();
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA, data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA));
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    /**
     * Entry point for navigating to medication fragment
     *
     * @param workflowDTO navigation dto
     */
    public void loadMedicationsAllergy(String workflowDTO) {
        CheckinMedicationsAllergyFragment medicationsAllergyFragment = new CheckinMedicationsAllergyFragment();
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, workflowDTO);
        Bundle args = new Bundle();
        args.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, workflowDTO);

        medicationsAllergyFragment.setArguments(args);
        navigateToFragment(medicationsAllergyFragment, true);
    }

    /**
     * Entry point for navigating to medication fragment
     *
     * @param globalLabelDTO global dto
     * @param persDetailsDTO personal details dto
     */
    public void initializeProfilePictureFragment(DemographicLabelsDTO globalLabelDTO,
                                                 PatientModel persDetailsDTO) {

        ProfilePictureFragment fragment = new ProfilePictureFragment();
        fragment.setGlobalLabelsDTO(globalLabelDTO);

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, persDetailsDTO);
        args.putBoolean(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, true);
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        fm.beginTransaction().replace(R.id.revdemographicsAddressPicCapturer, fragment, tag)
                .commit();

    }

    @Override
    public String getProfilePicture() {
        ProfilePictureFragment fragment = (ProfilePictureFragment)
                getSupportFragmentManager().findFragmentById(R.id.revdemographicsAddressPicCapturer);

        if (fragment != null) {
            PatientModel demographicPersDetailsPayloadDTO = fragment.getDemographicPersDetailsPayloadDTO();
            if (demographicPersDetailsPayloadDTO != null) {
                return demographicPersDetailsPayloadDTO.getProfilePhoto();
            }
        }
        return null;
    }

    @Override
    public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step) {
        currentDemographicStep = step;
        this.demographicDTO = demographicDTO;
        navigateToDemographicFragment(step);
    }

    @Override
    public Integer getCurrentStep() {
        return currentDemographicStep;
    }

    @Override
    public void setCurrentStep(Integer step) {
        if (step > 0) {
            currentDemographicStep = step;
        }
    }

    @Override
    public void loadPictureFragment() {
        initializeProfilePictureFragment(demographicDTO.getMetadata().getLabels(),
                demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails());
    }


    /**
     * Navigate to fragment
     *
     * @param step fragment
     */
    public void navigateToDemographicFragment(Integer step) {
        CheckInDemographicsBaseFragment fragment = demographicFragMap.get(step);
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        navigateToFragment(fragment, currentDemographicStep == 1 ? false : true);
    }
}
