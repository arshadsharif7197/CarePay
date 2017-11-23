package com.carecloud.carepaylibray.demographics;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.EmergencyContactFragment;
import com.carecloud.carepaylibray.demographics.fragments.FormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.HomeAlertDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.IntakeFormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.fragments.PhysicianFragment;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactFragmentInterface;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianFragmentInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class DemographicsPresenterImpl implements DemographicsPresenter {
    private AppointmentDTO appointmentPayload;
    private DemographicsView demographicsView;

    private DemographicDTO demographicDTO;
    private final boolean isPatientMode;
    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    //demographics nav
    private int currentDemographicStep = 1;

    private boolean startCheckin = false;
    public String appointmentId;

    private Fragment currentFragment;

    /**
     * @param demographicsView   Demographics View
     * @param savedInstanceState Bundle
     * @param isPatientMode      true if Practice App - Patient Mode
     */
    public DemographicsPresenterImpl(DemographicsView demographicsView, Bundle savedInstanceState,
                                     boolean isPatientMode) {
        this.demographicsView = demographicsView;
        demographicDTO = demographicsView.getConvertedDTO(DemographicDTO.class);
        if (!demographicDTO.getPayload().getAppointmentpayloaddto().isEmpty()) {
            appointmentPayload = demographicDTO.getPayload().getAppointmentpayloaddto().get(0);
            appointmentId = appointmentPayload.getMetadata().getAppointmentId();
        }

        this.isPatientMode = isPatientMode;

        if (savedInstanceState != null) {
            currentDemographicStep = savedInstanceState.getInt(SAVED_STEP_KEY, 1);
        }

        WorkflowDTO workflowDTO = demographicsView.getConvertedDTO(WorkflowDTO.class);
        displayStartFragment(workflowDTO);
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        icicle.putInt(SAVED_STEP_KEY, currentDemographicStep);
        icicle.putString(CURRENT_ICICLE_FRAGMENT, currentFragment.getClass().getName());
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        demographicsView = null;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        if (demographicsView != null) {
            return demographicsView.getSupportFragmentManager();
        }

        return null;
    }

    @Override
    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    private void displayStartFragment(WorkflowDTO workflowDTO) {
        startCheckin = true;
        switch (workflowDTO.getState()) {
            case NavigationStateConstants.CONSENT_FORMS:
                navigateToConsentForms(workflowDTO);
                break;
            case NavigationStateConstants.MEDICATION_ALLERGIES:
                navigateToMedicationsAllergy(workflowDTO);
                break;
            case NavigationStateConstants.INTAKE_FORMS:
                navigateToIntakeForms(workflowDTO);
                break;
            default:
                navigateToDemographicFragment(currentDemographicStep);
        }
        startCheckin = false;
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            fm.popBackStackImmediate(tag, 0);
            return;
        }

        transaction.replace(R.id.root_layout, fragment, tag);
        if (addToBackStack && !startCheckin) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
        currentFragment = fragment;
    }

    @Override
    public void navigateToConsentForms(WorkflowDTO workflowDTO) {
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowDTO.toString());

        FormsFragment fragment = new FormsFragment();
        fragment.setArguments(bundle);

        navigateToFragment(fragment, true);
    }

    @Override
    public void navigateToIntakeForms(WorkflowDTO workflowDTO) {
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowDTO.toString());

        IntakeFormsFragment fragment = new IntakeFormsFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class,
                workflowDTO);

        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, workflowDTO.toString());

        MedicationsAllergyFragment fragment = new MedicationsAllergyFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    @Override
    public void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        if (demographicsView != null) {
            demographicsView.updateCheckInFlow(flowState, totalPages, currentPage);
        }
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        demographicsView.navigateToWorkflow(workflowDTO);
    }

    @Override
    public String getAppointmentId() {
        return appointmentId;
    }

    @Override
    public AppointmentDTO getAppointment() {
        return appointmentPayload;
    }

    @Override
    public void displayCheckInSuccess(final WorkflowDTO workflowDTO) {
        demographicsView.completeCheckIn(workflowDTO);
    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {
        this.demographicDTO = demographicDTO;
        InsuranceEditDialog insuranceEditDialog = InsuranceEditDialog.newInstance(demographicDTO,
                editedIndex, isPatientMode, true);

        if (showAsDialog && isPatientMode) {
            String tag = "InsuranceEditFloatingDialog";
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }

            insuranceEditDialog.show(ft, tag);
            demographicsView.setMediaResultListener(insuranceEditDialog);
        } else {
            navigateToFragment(insuranceEditDialog, true);
        }
    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {
        MedicationsAllergyFragment medicationsAllergyFragment =
                (MedicationsAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.root_layout);
        medicationsAllergyFragment.addItem(item);
    }

    @Override
    public void showMedicationSearch() {
        MedicationAllergySearchFragment fragment = new MedicationAllergySearchFragment();
        if (medicationsAllergiesDTO != null) {
            Gson gson = new Gson();
            String jsonExtra = gson.toJson(medicationsAllergiesDTO);
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, jsonExtra);

            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA,
                    MedicationAllergySearchFragment.SearchMode.MEDICATION.name());
            fragment.setArguments(bundle);

        }

        fragment.show(getSupportFragmentManager(),
                fragment.getClass().getName());
    }

    @Override
    public void showAllergiesSearch() {

    }

    @Override
    public void medicationSubmitSuccess(WorkflowDTO workflowDTO) {
        if (demographicsView != null) {
            demographicsView.navigateToWorkflow(workflowDTO);
        }
    }

    @Override
    public void medicationSubmitFail(String message) {
        if (demographicsView != null) {
            demographicsView.showErrorNotification(message);
            Log.e(demographicsView.getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), message);
        }
    }

    @Override
    public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step) {
        currentDemographicStep = step;
        this.demographicDTO = demographicDTO;
        navigateToDemographicFragment(currentDemographicStep);
    }

    @Override
    public int getCurrentStep() {
        return currentDemographicStep;
    }

    @Override
    public int getTotalSteps() {
        int countSteps = 0;
        for (int i = 0; i < MAX_STEPS; i++) {
            countSteps += hasStep(i + 1);
        }
        return countSteps;
    }

    @Override
    public int hasStep(int step) {
        switch (step) {
            case PERSONAL_INFO:
            case ADDRESS:
            case DEMOGRAPHICS:
                return 1;
            case IDENTITY:
                if (demographicDTO.getPayload().getCheckinSettings().shouldShowIdentityDocs()) {
                    return 1;
                }
                return 0;
            case INSURANCE:
                if (demographicDTO.getPayload().getCheckinSettings().shouldShowHealthInsurance()) {
                    return 1;
                }
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public void setCurrentStep(Integer step) {
        if (step > 0) {
            currentDemographicStep = step;
        }
    }

    /**
     * Navigate to fragment
     *
     * @param step fragment
     */
    private void navigateToDemographicFragment(Integer step) {
        CheckInDemographicsBaseFragment fragment = getDemographicFragment(step);
        if (fragment != null) {
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demographicDTO);
            args.putBoolean(CheckInDemographicsBaseFragment.PREVENT_NAV_BACK, shouldPreventBackNav());
            fragment.setArguments(args);

            navigateToFragment(fragment, currentDemographicStep != 1);
        }
    }

    @Override
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }

    @Override
    public void onInsuranceEdited(DemographicDTO demographicDTO, boolean proceed) {
        if (demographicDTO != null) {
            this.demographicDTO = demographicDTO;
        }

        SystemUtil.hideSoftKeyboard((Activity) demographicsView.getContext());

        FragmentManager fm = getSupportFragmentManager();

        // Update Health Insurance Fragment
        String tag = getHealthInsuranceFragmentTag();
        HealthInsuranceFragment healthInsuranceFragment
                = (HealthInsuranceFragment) fm.findFragmentByTag(tag);
        if (healthInsuranceFragment == null) {//may need to recreate it if no insurances
            healthInsuranceFragment = new HealthInsuranceFragment();
        }

        if (!healthInsuranceFragment.isAdded()) {
            fm.popBackStack();
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
            navigateToFragment(healthInsuranceFragment, true);
        }

        if (demographicDTO == null || proceed) {
            fm.executePendingTransactions();
            healthInsuranceFragment.openNextFragment(this.demographicDTO);
        } else {
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
        }
    }

    protected String getHealthInsuranceFragmentTag() {
        return HealthInsuranceFragment.class.getName();
    }

    @Override
    public void goOneStepBack() {
        if (demographicsView != null) {
            demographicsView.onBackPressed();
        }
    }

    @Override
    public void showRemovePrimaryInsuranceDialog(HomeAlertDialogFragment.HomeAlertInterface callback) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeAlertDialogFragment homeAlertDialogFragment = HomeAlertDialogFragment
                .newInstance(Label.getLabel("demographics_insurance_primary_alert_title"),
                        Label.getLabel("demographics_insurance_primary_alert_message"));
        homeAlertDialogFragment.setCallback(callback);
        String tag = homeAlertDialogFragment.getClass().getName();
        homeAlertDialogFragment.show(ft, tag);
    }

    protected CheckInDemographicsBaseFragment getDemographicFragment(int step) {
        if (hasStep(step) != 1 && step <= MAX_STEPS) {
            return getDemographicFragment(++step);
        }
        switch (step) {
            case PERSONAL_INFO:
                return new PersonalInfoFragment();
            case ADDRESS:
                return new AddressFragment();
            case DEMOGRAPHICS:
                return new DemographicsFragment();
            case IDENTITY:
                return new IdentificationFragment();
            case INSURANCE:
                return new HealthInsuranceFragment();
            default:
                return null;
        }
    }

    public AppointmentDTO getAppointmentPayload() {
        return appointmentPayload;
    }

    protected boolean shouldPreventBackNav() {
        return false;
    }

    @Override
    public DTO getDto() {
        return demographicDTO;
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        ((BaseActivity) demographicsView).setToolbar(toolbar);
    }

    @Override
    public void showSearchPhysicianFragmentDialog(PhysicianDto physicianDto, int physicianType) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        PhysicianFragment fragment = PhysicianFragment.newInstance(physicianDto, physicianType);
        showFragmentAsDialogIfNeeded(transaction, fragment);
    }

    @Override
    public void onPhysicianSelected(PhysicianDto physician, int physicianType) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (fragment instanceof PhysicianFragmentInterface) {
            ((PhysicianFragmentInterface) fragment).setPhysician(physician, physicianType);
        }
    }

    @Override
    public void showAddEditEmergencyContactDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        EmergencyContactFragment fragment = EmergencyContactFragment.newInstance();
        showFragmentAsDialogIfNeeded(transaction, fragment);
    }

    private void showFragmentAsDialogIfNeeded(FragmentTransaction transaction, BaseDialogFragment fragment) {
        if (isPatientMode) {
            fragment.show(transaction, fragment.getClass().getCanonicalName());
        } else {
            transaction.add(R.id.root_layout, fragment, fragment.getClass().getCanonicalName());
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (fragment instanceof EmergencyContactFragmentInterface) {
            ((EmergencyContactFragmentInterface) fragment).updateEmergencyContact(emergencyContact);
        }
    }
}
