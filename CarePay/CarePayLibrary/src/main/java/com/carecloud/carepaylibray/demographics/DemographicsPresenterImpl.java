package com.carecloud.carepaylibray.demographics;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.EmergencyContactFragment;
import com.carecloud.carepaylibray.demographics.fragments.FormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.IntakeFormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.fragments.SearchPhysicianFragment;
import com.carecloud.carepaylibray.demographics.fragments.ThirdPartyTaskFragment;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactFragmentInterface;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianFragmentInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.medications.fragments.AllergiesFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.third_party.models.ThirdPartyWorkflowDto;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

public class DemographicsPresenterImpl implements DemographicsPresenter {
    private AppointmentDTO appointmentPayload;
    private DemographicsView demographicsView;

    protected DemographicDTO demographicDTO;
    private final boolean isPatientMode;
    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    //demographics nav
    private int currentDemographicStep = 1;

    private boolean startCheckIn = false;
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

        //patient mode will call this manually to allow it to properly init patient user
        if (!isPatientMode) {
            initWorkflow();
        }
    }

    protected void initWorkflow() {
        WorkflowDTO workflowDTO = demographicsView.getConvertedDTO(WorkflowDTO.class);
        displayFragment(workflowDTO);

        MixPanelUtil.setDemographics(demographicsView.getContext(), demographicDTO.getPayload().getDemographics().getPayload());
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        icicle.putInt(SAVED_STEP_KEY, currentDemographicStep);
        if (currentFragment != null) {
            icicle.putString(CURRENT_ICICLE_FRAGMENT, currentFragment.getClass().getName());
        }
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

    @Override
    public void logCheckinCancelled() {
        Fragment currentFragment = getCurrentFragment();
        String currentStep = null;
        if (currentFragment instanceof PersonalInfoFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_personal_info);
        } else if (currentFragment instanceof AddressFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_address);
        } else if (currentFragment instanceof DemographicsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_demographics);
        } else if (currentFragment instanceof IdentificationFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_identity);
        } else if (currentFragment instanceof HealthInsuranceFragment ||
                currentFragment instanceof InsuranceEditDialog) {
            currentStep = demographicsView.getContext().getString(R.string.step_health_insurance);
        } else if (currentFragment instanceof FormsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_consent_forms);
        } else if (currentFragment instanceof MedicationsAllergyFragment ||
                currentFragment instanceof MedicationsFragment ||
                (currentFragment instanceof MedicationsAllergiesEmptyFragment &&
                        ((MedicationsAllergiesEmptyFragment) currentFragment).getSelectedMode() ==
                                MedicationsAllergiesEmptyFragment.MEDICATION_MODE)) {
            currentStep = demographicsView.getContext().getString(R.string.step_medications);
        } else if (currentFragment instanceof AllergiesFragment ||
                (currentFragment instanceof MedicationsAllergiesEmptyFragment &&
                        ((MedicationsAllergiesEmptyFragment) currentFragment).getSelectedMode() ==
                                MedicationsAllergiesEmptyFragment.ALLERGY_MODE)) {
            currentStep = demographicsView.getContext().getString(R.string.step_allegies);
        } else if (currentFragment instanceof IntakeFormsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_intake);
        }
        if (currentStep != null) {
            boolean isGuest = !ValidationHelper.isValidEmail(((ISession) demographicsView.getContext()).getAppAuthorizationHelper().getCurrUser());
            String[] params = {
                    demographicsView.getContext().getString(R.string.param_last_completed_step),
                    demographicsView.getContext().getString(R.string.param_is_guest)
            };
            Object[] values ={
                    currentStep,
                    isGuest,
            };
            MixPanelUtil.logEvent(demographicsView.getContext().getString(R.string.event_checkin_cancelled),
                    params, values);
        }
        if (currentFragment instanceof ResponsibilityBaseFragment) {
            logCheckinCompleted(false,false, null);
        }
    }

    @Override
    public void logCheckinCompleted(boolean paymentPlanCreated, boolean paymentMade, PaymentsModel paymentsModel) {
        if (getAppointment() != null) {
            String[] params = {getString(R.string.param_practice_id),
                    getString(R.string.param_appointment_id),
                    getString(R.string.param_appointment_type),
                    getString(R.string.param_is_guest),
                    getString(R.string.param_provider_id),
                    getString(R.string.param_location_id),
                    getString(R.string.param_payment_made),
                    getString(R.string.param_payment_plan_created),
                    getString(R.string.param_partial_pay_available)
            };
            Object[] values = {getAppointment().getMetadata().getPracticeId(),
                    getAppointmentId(),
                    getAppointment().getPayload().getVisitType().getName(),
                    false,
                    getAppointment().getPayload().getProvider().getGuid(),
                    getAppointment().getPayload().getLocation().getGuid(),
                    !isPatientMode ? paymentMade : null,
                    !isPatientMode ? paymentPlanCreated : null,
                    paymentsModel != null ? paymentsModel.getPaymentPayload().getPaymentSetting(getAppointment().getMetadata().getPracticeId())
                            .getPayload().getRegularPayments().isAllowPartialPayments() : null
            };
            MixPanelUtil.logEvent(getString(R.string.event_checkin_completed), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_checkin_completed), 1);
            MixPanelUtil.endTimer(getString(R.string.timer_checkin));
        }
    }

    public void displayFragment(WorkflowDTO workflowDTO) {
        startCheckIn = true;
        boolean isResume = true;
        boolean isGuest = !ValidationHelper.isValidEmail(((ISession) demographicsView.getContext()).getAppAuthorizationHelper().getCurrUser());
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_appointment_id),
                getString(R.string.param_appointment_type),
                getString(R.string.param_is_guest),
                getString(R.string.param_provider_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_location_id),
                getString(R.string.param_setting_id_available),
                getString(R.string.param_setting_insurance_available)
        };
        Object[] values = {getAppointment().getMetadata().getPracticeId(),
                getAppointmentId(),
                getAppointment().getPayload().getVisitType().getName(),
                isGuest,
                getAppointment().getPayload().getProvider().getGuid(),
                getAppointment().getMetadata().getPatientId(),
                getAppointment().getPayload().getLocation().getGuid(),
                demographicDTO.getPayload().getCheckinSettings().shouldShowIdentityDocs(),
                demographicDTO.getPayload().getCheckinSettings().shouldShowHealthInsurance()
        };
        switch (workflowDTO.getState()) {
            case NavigationStateConstants.CONSENT_FORMS:
                navigateToConsentForms(workflowDTO);
                break;
            case NavigationStateConstants.MEDICATION_ALLERGIES:
                navigateToMedicationsAllergy(workflowDTO);
                break;
            case NavigationStateConstants.MEDICATIONS:
                navigateToMedications(workflowDTO, true);
                break;
            case NavigationStateConstants.ALLERGIES:
                navigateToAllergy(workflowDTO, true);
                break;
            case NavigationStateConstants.INTAKE_FORMS:
                navigateToIntakeForms(workflowDTO);
                break;
            case NavigationStateConstants.THIRD_PARTY_CHECK_IN:
                navigateToThirdParty(workflowDTO);
                break;
            default:
                navigateToDemographicFragment(currentDemographicStep);

                if (currentDemographicStep == PERSONAL_INFO) {
                    isResume = false;
                    //Log Check-in Started
                    if (getAppointment() != null) {
                        MixPanelUtil.logEvent(getString(R.string.event_checkin_started), params, values);
                    }

                    MixPanelUtil.startTimer(getString(R.string.timer_demographics));
                }
                break;
        }
        if (isResume) {
            MixPanelUtil.logEvent(getString(R.string.event_checkin_resumed), params, values);
        }
        MixPanelUtil.startTimer(getString(R.string.timer_checkin));
        startCheckIn = false;
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        navigateToFragment(fragment, addToBackStack, false);
    }

    public void navigateToFragment(Fragment fragment, boolean addToBackStack, boolean clearPrevious) {
        String tag = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            fm.popBackStackImmediate(tag, clearPrevious ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
        }

        transaction.replace(R.id.root_layout, fragment, tag);
        if (addToBackStack && !startCheckIn) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
        currentFragment = fragment;
    }

    @Override
    public void navigateToConsentForms(WorkflowDTO workflowDTO) {
        FormsFragment fragment = FormsFragment.newInstance(workflowDTO);
        navigateToFragment(fragment, true);

        MixPanelUtil.startTimer(getString(R.string.timer_consent_forms));
    }

    @Override
    public void navigateToIntakeForms(WorkflowDTO workflowDTO) {
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowDTO.toString());

        IntakeFormsFragment fragment = new IntakeFormsFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);

        MixPanelUtil.startTimer(getString(R.string.timer_intake_forms));
    }

    @Override
    public void navigateToThirdParty(WorkflowDTO workflowDTO) {
        ThirdPartyWorkflowDto thirdPartyWorkflow = DtoHelper
                .getConvertedDTO(ThirdPartyWorkflowDto.class, workflowDTO);
        ThirdPartyTaskFragment fragment = ThirdPartyTaskFragment.newInstance(thirdPartyWorkflow);
        navigateToFragment(fragment, true);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class,
                workflowDTO);
        MedicationsAllergyFragment fragment = MedicationsAllergyFragment.newInstance(medicationsAllergiesDTO);
        navigateToFragment(fragment, true);
    }

    @Override
    public void navigateToMedications(WorkflowDTO workflowDTO, boolean checkEmpty) {
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class,
                workflowDTO);
        Fragment fragment;
        if (checkEmpty && medicationsAllergiesDTO.getPayload().getMedications().getPayload().isEmpty()) {
            fragment = MedicationsAllergiesEmptyFragment.newInstance(medicationsAllergiesDTO,
                    MedicationsAllergiesEmptyFragment.MEDICATION_MODE);
        } else {
            fragment = MedicationsFragment.newInstance(medicationsAllergiesDTO);
        }
        navigateToFragment(fragment, true);

        if (!checkEmpty) {
            showMedicationAllergySearchFragment(MedicationAllergySearchFragment.MEDICATION_ITEM);
        }

        MixPanelUtil.startTimer(getString(R.string.timer_medications));
    }

    @Override
    public void navigateToAllergy(WorkflowDTO workflowDTO, boolean checkEmpty) {
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class,
                workflowDTO);
        Fragment fragment;
        if (checkEmpty && medicationsAllergiesDTO.getPayload().getAllergies().getPayload().isEmpty()) {
            fragment = MedicationsAllergiesEmptyFragment.newInstance(medicationsAllergiesDTO,
                    MedicationsAllergiesEmptyFragment.ALLERGY_MODE);
        } else {
            fragment = AllergiesFragment.newInstance(medicationsAllergiesDTO);
        }
        navigateToFragment(fragment, true);

        if (!checkEmpty) {
            showMedicationAllergySearchFragment(MedicationAllergySearchFragment.ALLERGY_ITEM);
        }

        MixPanelUtil.startTimer(getString(R.string.timer_allergies));
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
        if (item instanceof MedicationsObject) {
            MedicationsFragment medicationsFragment = (MedicationsFragment) getSupportFragmentManager().
                    findFragmentById(R.id.root_layout);
            medicationsFragment.addItem(item);
        } else {
            AllergiesFragment allergiesFragment = (AllergiesFragment) getSupportFragmentManager().
                    findFragmentById(R.id.root_layout);
            allergiesFragment.addItem(item);
        }
    }

    @Override
    public void promptAddUnlisted(final MedicationsAllergiesObject item, final int mode) {
        String title = Label.getLabel("medications_allergies_prompt_unlisted_title");
        String message = String.format(mode == MedicationAllergySearchFragment.ALLERGY_ITEM ?
                        Label.getLabel("medications_allergies_prompt_message_allergies") :
                        Label.getLabel("medications_allergies_prompt_message_medications"),
                item.getDisplayName());

        ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(title, message);
        dialogFragment.setCallback(new ConfirmationCallback() {
            @Override
            public void onConfirm() {
                addMedicationAllergyItem(item);
            }
        });
        dialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                showMedicationAllergySearchFragment(mode);
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        dialogFragment.show(fm, dialogFragment.getClass().getName());
    }

    @Override
    public void showMedicationAllergySearchFragment(int searchType) {
        MedicationAllergySearchFragment fragment = MedicationAllergySearchFragment
                .newInstance(medicationsAllergiesDTO, searchType);
        showFragmentAsDialogIfNeeded(fragment);
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

        if (step == IDENTITY) {
            MixPanelUtil.startTimer(getString(R.string.timer_identification_docs));
        } else if (step == INSURANCE) {
            MixPanelUtil.startTimer(getString(R.string.timer_health_insurance));
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
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
            fm.popBackStack(InsuranceEditDialog.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigateToFragment(healthInsuranceFragment, true);
            if (proceed) {
                healthInsuranceFragment.setShouldContinue(true);
                fm.executePendingTransactions();
            }
            return;
        }

        if (!healthInsuranceFragment.isAdded()) {
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
            fm.popBackStack(tag, 0);
        } else if (demographicDTO == null || proceed) {
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
    public void showRemovePrimaryInsuranceDialog(ConfirmationCallback callback, DialogInterface.OnCancelListener cancelListener) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(
                        Label.getLabel("demographics_insurance_primary_alert_title"),
                        Label.getLabel("demographics_insurance_primary_alert_message_patient"),
                        Label.getLabel("cancel"),
                        Label.getLabel("ok"));
        confirmDialogFragment.setCallback(callback);
        if (cancelListener != null) {
            confirmDialogFragment.setOnCancelListener(cancelListener);
        }
        String tag = confirmDialogFragment.getClass().getName();
        confirmDialogFragment.show(ft, tag);
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
        return getCurrentStep() == 1;
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
        SearchPhysicianFragment fragment = SearchPhysicianFragment.newInstance(physicianDto, physicianType);
        showFragmentAsDialogIfNeeded(fragment);
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
        EmergencyContactFragment fragment = EmergencyContactFragment.newInstance();
        showFragmentAsDialogIfNeeded(fragment);
    }

    private void showFragmentAsDialogIfNeeded(BaseDialogFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
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

    private String getString(int id) {
        return demographicsView.getContext().getString(id);
    }
}
