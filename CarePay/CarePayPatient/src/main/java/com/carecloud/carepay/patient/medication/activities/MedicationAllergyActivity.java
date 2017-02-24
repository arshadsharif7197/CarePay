package com.carecloud.carepay.patient.medication.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergyActivity extends BasePatientActivity implements MedicationsAllergyFragment.MedicationAllergyCallback, MedicationAllergySearchFragment.MedicationAllergySearchCallback {

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_medication_allergy);

        medicationsAllergiesDTO = getConvertedDTO(MedicationsAllergiesResultsModel.class);

        loadBaseFragment();


    }


    private void loadBaseFragment(){
        MedicationsAllergyFragment medicationsAllergyFragment = new MedicationsAllergyFragment();
        if(medicationsAllergiesDTO!=null){
            Gson gson = new Gson();
            String jsonExtra = gson.toJson(medicationsAllergiesDTO);
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, jsonExtra);
            medicationsAllergyFragment.setArguments(bundle);

        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout, medicationsAllergyFragment);
        transaction.commit();
    }

    /**
     * Load new fragment
     * @param fragment fragment to load
     */
    public void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void showMedicationSearch() {
        MedicationAllergySearchFragment medicationAllergySearchFragment = new MedicationAllergySearchFragment();
        if(medicationsAllergiesDTO!=null){
            Gson gson = new Gson();
            String jsonExtra = gson.toJson(medicationsAllergiesDTO);
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, jsonExtra);

            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA, MedicationAllergySearchFragment.SearchMode.MEDICATION.name());
            medicationAllergySearchFragment.setArguments(bundle);

        }
        changeFragment(medicationAllergySearchFragment);

    }

    @Override
    public void showAllergiesSearch() {

    }

    @Override
    public void medicationSubmitSuccess(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.getInstance(getContext()).navigateToWorkflow(workflowDTO);
    }

    @Override
    public void medicationSubmitFail(String message) {
        SystemUtil.showDefaultFailureDialog(getContext());
        Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), message);
    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {
        getSupportFragmentManager().popBackStackImmediate(MedicationAllergySearchFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

        MedicationsAllergyFragment medicationsAllergyFragment = (MedicationsAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.root_layout);
        medicationsAllergyFragment.addItem(item);

    }

}
