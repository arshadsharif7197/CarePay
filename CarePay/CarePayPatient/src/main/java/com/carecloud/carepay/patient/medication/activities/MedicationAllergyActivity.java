package com.carecloud.carepay.patient.medication.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergyActivity extends BasePatientActivity {

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_medication_allergy);

        loadBaseFragment();
    }


    private void loadBaseFragment(){
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
}
