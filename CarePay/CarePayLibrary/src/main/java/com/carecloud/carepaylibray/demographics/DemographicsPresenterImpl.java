package com.carecloud.carepaylibray.demographics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Created by cocampo on 4/3/17.
 */

public class DemographicsPresenterImpl implements DemographicsPresenter {

    private BaseActivity activity;

    private CarePayCameraCallback carePayCameraCallback;
    private InsuranceEditDialog insuranceEditDialog;

    private DemographicDTO demographicDTO;

    //demographics nav
    private int currentDemographicStep = 1;

    private View[] checkinFlowViews;
    private View checkinDemographics;
    private View checkinConsent;
    private View checkinMedications;
    private View checkinIntake;
    private View checkinPayment;

    public DemographicsPresenterImpl(BaseActivity activity) {
        this.activity = activity;
        demographicDTO = activity.getConvertedDTO(DemographicDTO.class);

        initializeCheckinViews();

        navigateToDemographicFragment(1);
    }

    @Override
    public void onStop() {
        if (insuranceEditDialog != null) {
            insuranceEditDialog.dismissAllowingStateLoss();
            insuranceEditDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        activity = null;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        if (activity != null) {
            return activity.getSupportFragmentManager();
        }

        return null;
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCapturedSuccess(bitmap);
        }
    }

    @Override
    public void captureImage(CarePayCameraCallback callback) {
        this.carePayCameraCallback = callback;

        String tag = CarePayCameraFragment.class.getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        CarePayCameraFragment dialog = new CarePayCameraFragment();
        dialog.show(ft, tag);
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

        transaction.replace(R.id.root_layout, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
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

    @Override
    public void onInsuranceEdited(DemographicDTO demographicDTO) {
        if (demographicDTO != null) {
            this.demographicDTO = demographicDTO;
        }

        SystemUtil.hideSoftKeyboard(activity);

        FragmentManager fm = getSupportFragmentManager();

        // Update Health Insurance Fragment
        String tag = HealthInsuranceFragment.class.getSimpleName();
        HealthInsuranceFragment healthInsuranceFragment = (HealthInsuranceFragment) fm.findFragmentByTag(tag);

        if (demographicDTO != null) {
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
        } else {
            healthInsuranceFragment.openNextFragment(this.demographicDTO);
        }
    }

    @Override
    public void goOneStepBack() {
        activity.onBackPressed();
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

    @Override
    public void navigateToConsentFlow(WorkflowDTO workflowDTO) {
        activity.navigateToWorkflow(workflowDTO);
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        return demographicDTO.getMetadata().getLabels();
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {

    }

    @Override
    public void startPartialPayment(double owedAmount) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentPlanAction() {

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

    }

    @Override
    public void completePaymentProcess(UpdatePatientBalancesDTO updatePatientBalancesDTO) {
        Intent intent = activity.getIntent();
        activity.setResult(CarePayConstants.HOME_PRESSED, intent);
        activity.finish();
    }

    @Override
    public void cancelPaymentProcess(PaymentsModel paymentsModel) {

    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {
        this.demographicDTO = demographicDTO;
        insuranceEditDialog = InsuranceEditDialog.newInstance(demographicDTO, editedIndex);

        if (showAsDialog) {
            String tag = "InsuranceEditFloatingDialog";
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }

            insuranceEditDialog.show(ft, tag);
        } else {
            navigateToFragment(insuranceEditDialog, true);
        }
    }

    @Override
    public void navigateToParentFragment() {

    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {

    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {

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

    }

    @Override
    public void medicationSubmitFail(String message) {

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
    public void loadPictureFragment() {
        ProfilePictureFragment fragment = new ProfilePictureFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails());
        args.putBoolean(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, true);
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        fm.beginTransaction().replace(R.id.revdemographicsAddressPicCapturer, fragment, tag)
                .commit();
    }

    private void updateCheckinFlow(View highlightView, int totalPages, int currentPage) {

    }

    /**
     * Launch intake forms
     *
     * @param workflowJson workflowJson
     */
    public void startIntakeForms(String workflowJson) {
        IntakeResponseModel intakeResponseModel = activity.getConvertedDTO(IntakeResponseModel.class, workflowJson);

        Gson gson = new Gson();
        String intakeFormDTO = gson.toJson(intakeResponseModel);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, intakeFormDTO);

        IntakeFormsFragment fragment = new IntakeFormsFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    private void initializeCheckinViews() {
        checkinDemographics = activity.findViewById(R.id.checkin_flow_demographics);
        checkinConsent = activity.findViewById(R.id.checkin_flow_consent);
        checkinMedications = activity.findViewById(R.id.checkin_flow_medications);
        checkinIntake = activity.findViewById(R.id.checkin_flow_intake);
        checkinPayment = activity.findViewById(R.id.checkin_flow_payment);

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
}
