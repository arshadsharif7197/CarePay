package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dialog.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
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
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class PatientModeCheckinActivity extends BasePracticeActivity implements
        DemographicsReviewLabelsHolder, DemographicsLabelsHolder,
        HealthInsuranceFragment.InsuranceDocumentScannerListener, MedicationsAllergyFragment.MedicationAllergyCallback,
        CheckinDemographicsInterface, MedicationAllergySearchFragment.MedicationAllergySearchCallback,
        PaymentNavigationCallback, CheckinFlowCallback,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        PersonalInfoFragment.UpdateProfilePictureListener,
        CarePayCameraCallback,
        InsuranceEditDialog.InsuranceEditDialogListener,
        CarePayCameraReady {


    public final static int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static int SUBFLOW_CONSENT = 1;
    public final static int SUBFLOW_INTAKE = 2;
    public final static int SUBFLOW_PAYMENTS = 3;
    private static final int NUM_OF_SUBFLOWS = 4;

    //demographics nav
    private int currentDemographicStep = 1;

    private DemographicDTO demographicDTO;
    private CarePayTextView backButton;
    private ImageView logoImageView;
    private ImageView homeClickable;

    private CarePayCameraCallback carePayCameraCallback;
    private InsuranceEditDialog insuranceEditDialog;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private PaymentsModel paymentDTO;

    private View[] checkinFlowViews;
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

    @Override
    public void onDestroy() {
        dismissInsuranceEditDialog();
        super.onDestroy();
    }

    private void dismissInsuranceEditDialog() {
        if (insuranceEditDialog != null) {
            insuranceEditDialog.dismiss();
            insuranceEditDialog = null;
        }
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

    private void initializeCheckinViews() {
        checkinDemographics = findViewById(R.id.checkin_flow_demographics);
        checkinConsent = findViewById(R.id.checkin_flow_consent);
        checkinMedications = findViewById(R.id.checkin_flow_medications);
        checkinIntake = findViewById(R.id.checkin_flow_intake);
        checkinPayment = findViewById(R.id.checkin_flow_payment);

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
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            transaction.remove(prev);
        }

        transaction.replace(R.id.checkInContentHolderId, fragment, tag);
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
//        navigateToFragment(fragment, true);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
//        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
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

        navigateToFragment(fragment, true);
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
    public void navigateToParentFragment() {

    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {

    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {
        insuranceEditDialog = InsuranceEditDialog.newInstance(demographicDTO, editedIndex);

        String tag = InsuranceEditDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        if (showAsDialog) {
            insuranceEditDialog.show(ft, tag);
        } else {
            ft.replace(R.id.checkInContentHolderId, insuranceEditDialog, tag);
            ft.addToBackStack(tag);
            ft.commitAllowingStateLoss();
        }
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
        navigateToFragment(fragment, true);
    }

    @Override
    public void navigateToConsentFlow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
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
        CheckInDemographicsBaseFragment fragment = getDemographicFragment(step);
        if(fragment!=null) {
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demographicDTO);
            fragment.setArguments(args);

            navigateToFragment(fragment, currentDemographicStep != 1);
        }
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCapturedSuccess(bitmap);
        }
    }

    @Override
    public void onInsuranceEdited(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        SystemUtil.hideSoftKeyboard(this);
        dismissInsuranceEditDialog();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.checkInContentHolderId);
        if (fragment != null && fragment instanceof InsuranceEditDialog) {

            navigateToDemographicFragment(5);

        } else {

            // Update Health Insurance Fragment
            String tag = HealthInsuranceFragment.class.getSimpleName();
            HealthInsuranceFragment healthInsuranceFragment = (HealthInsuranceFragment) fm.findFragmentByTag(tag);
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
        }
    }

    @Override
    public void goOneStepBack() {
        onBackPressed();
    }

    @Override
    public void captureImage(CarePayCameraCallback callback) {
        this.carePayCameraCallback = callback;

        String tag = CarePayCameraFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        CarePayCameraFragment dialog = new CarePayCameraFragment();
        dialog.show(ft, tag);
    }

    private CheckInDemographicsBaseFragment getDemographicFragment(int step){
        switch (step){
            case 1:
                return new PersonalInfoFragment();
            case 2:
                return new AddressFragment();
            case 3:
                return new DemographicsFragment();
            case 4:
                return new IdentificationFragment();
            case 5:
                return new HealthInsuranceFragment();
            default:
                return null;
        }
    }
}
