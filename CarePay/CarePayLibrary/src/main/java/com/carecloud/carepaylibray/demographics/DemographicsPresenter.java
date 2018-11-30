package com.carecloud.carepaylibray.demographics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.interfaces.DemographicExtendedInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.medications.interfaces.MedicationAllergyCallback;

public interface DemographicsPresenter extends HealthInsuranceFragment.InsuranceDocumentScannerListener,
        MedicationAllergyCallback,
        CheckinFlowCallback,
        InsuranceEditDialog.InsuranceEditDialogListener,
        DemographicExtendedInterface {

    String SAVED_STEP_KEY = "save_step";
    String CURRENT_ICICLE_FRAGMENT = "current_icicle_fragment";


    void onSaveInstanceState(Bundle icicle);

    void onStop();

    void onDestroy();

    FragmentManager getSupportFragmentManager();

    DemographicDTO getDemographicDTO();

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    void navigateToFragment(Fragment fragment, boolean addToBackStack);

    /**
     * Consent form navigation
     *
     * @param workflowDTO consent DTO
     */
    void navigateToConsentForms(WorkflowDTO workflowDTO);

    void navigateToIntakeForms(WorkflowDTO workflowDTO);

    void navigateToMedicationsAllergy(WorkflowDTO workflowDTO);

    void navigateToMedications(WorkflowDTO workflowDTO, boolean checkEmpty);

    void navigateToAllergy(WorkflowDTO workflowDTO, boolean checkEmpty);

    void navigateToThirdParty(WorkflowDTO workflowDTO);

    Fragment getCurrentFragment();

}
