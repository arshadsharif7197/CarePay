package com.carecloud.carepay.patient.medication.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergyActivity extends BasePatientActivity implements MedicationsAllergyFragment.MedicationAllergyCallback {

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_medication_allergy);

        loadBaseFragment();

        medicationsAllergiesDTO = getConvertedDTO(MedicationsAllergiesResultsModel.class);

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
        transaction.replace(R.id.root_layout, new MedicationsAllergyFragment());
        transaction.commit();
    }

    public void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showMedicationSearch() {

    }

    @Override
    public void showAllergiesSearch() {

    }
}
