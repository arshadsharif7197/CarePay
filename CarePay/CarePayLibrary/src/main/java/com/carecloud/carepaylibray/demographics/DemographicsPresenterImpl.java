package com.carecloud.carepaylibray.demographics;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.IApplicationSession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckinCompletedDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.FormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.IntakeFormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class DemographicsPresenterImpl implements DemographicsPresenter {

    private DemographicsView demographicsView;

    private CarePayCameraCallback carePayCameraCallback;
    private InsuranceEditDialog insuranceEditDialog;

    private DemographicDTO demographicDTO;
    private final boolean isPatientMode;
    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    //demographics nav
    private int currentDemographicStep = 1;
    private static final String SAVED_STEP_KEY = "save_step";

    private boolean startCheckin = false;
    public String appointmentId ;


    /**
     * @param demographicsView Demographics View
     * @param savedInstanceState Bundle
     * @param isPatientMode true if Practice App - Patient Mode
     */
    public DemographicsPresenterImpl(DemographicsView demographicsView, Bundle savedInstanceState, boolean isPatientMode) {
        this.demographicsView = demographicsView;
        demographicDTO = demographicsView.getConvertedDTO(DemographicDTO.class);
        appointmentId = demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId() ;
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
        demographicsView = null;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        if (demographicsView != null) {
            return demographicsView.getSupportFragmentManager();
        }

        return null;
    }

    private void displayStartFragment(WorkflowDTO workflowDTO){
        startCheckin = true;
        switch (workflowDTO.getState()){
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
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            fm.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        transaction.replace(R.id.root_layout, fragment, tag);
        if (addToBackStack && !startCheckin) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
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
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, workflowDTO);

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
    public void displayCheckInSuccess(final WorkflowDTO workflowDTO) {
        if( ((IApplicationSession)demographicsView.getContext()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT ) {
            SystemUtil.showSuccessToast(demographicsView.getContext(), Label.getLabel("confirm_appointment_checkin"));
            completeDemographics(workflowDTO) ;
        } else {
            CheckinCompletedDialogFragment successFragment = new CheckinCompletedDialogFragment();
            successFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    completeDemographics(workflowDTO);
                }
            });
            successFragment.show(getSupportFragmentManager(), successFragment.getClass().getName());
        }
    }

    private void completeDemographics(WorkflowDTO workflowDTO)
    {
        demographicsView.finish();
        demographicsView.navigateToWorkflow(workflowDTO);
    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {
        this.demographicDTO = demographicDTO;
        insuranceEditDialog = InsuranceEditDialog.newInstance(demographicDTO, editedIndex, isPatientMode);

        if (showAsDialog && isPatientMode) {
            String tag = "InsuranceEditFloatingDialog";
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }

            insuranceEditDialog.show(ft, tag);
        } else {
            navigateToFragment(insuranceEditDialog, showAsDialog);
        }
    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {
        MedicationsAllergyFragment medicationsAllergyFragment = (MedicationsAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.root_layout);
        medicationsAllergyFragment.addItem(item);
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
        if (demographicsView != null) {
            demographicsView.navigateToWorkflow(workflowDTO);
        }
    }

    @Override
    public void medicationSubmitFail(String message) {
        if (demographicsView != null) {
            demographicsView.showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(demographicsView.getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), message);
        }
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
        navigateToDemographicFragment(currentDemographicStep);
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

    /**
     * Navigate to fragment
     *
     * @param step fragment
     */
    private void navigateToDemographicFragment(Integer step) {
        CheckInDemographicsBaseFragment fragment = getDemographicFragment(step);
        if(fragment!=null) {
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
    public void onCapturedSuccess(Bitmap bitmap) {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCapturedSuccess(bitmap);
        }
    }

    @Override
    public void onCaptureFail() {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCaptureFail();
        }
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
        HealthInsuranceFragment healthInsuranceFragment = (HealthInsuranceFragment) fm.findFragmentByTag(tag);

        if (demographicDTO == null || proceed) {
            healthInsuranceFragment.openNextFragment(this.demographicDTO);
        } else {
            healthInsuranceFragment.updateInsuranceList(demographicDTO);
        }
    }

    protected String getHealthInsuranceFragmentTag() {
        return HealthInsuranceFragment.class.getSimpleName();
    }

    @Override
    public void goOneStepBack() {
        if (demographicsView != null) {
            demographicsView.onBackPressed();
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

    protected CheckInDemographicsBaseFragment getDemographicFragment(int step){
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

    protected boolean shouldPreventBackNav(){
        return false;
    }
}