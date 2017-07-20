package com.carecloud.carepay.patient.myhealth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.fragments.AllergyDetailFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MedicationDetailFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthListFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.interfaces.DTO;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthActivity extends MenuPatientActivity implements MyHealthInterface {

    private MyHealthDto myHealthDto;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        myHealthDto = getConvertedDTO(MyHealthDto.class);
        if (icicle == null) {
            replaceFragment(MyHealthMainFragment.newInstance(), false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(com.carecloud.carepaylibrary.R.id.nav_my_health);
        menuItem.setChecked(true);
        displayToolbar(true, menuItem.getTitle().toString());
    }

    @Override
    public DTO getDto() {
        return myHealthDto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void onSeeAllFullMedicalRecordClicked() {

    }

    @Override
    public void onProviderClicked(ProviderDTO provider) {

    }

    @Override
    public void onAllergyClicked(AllergyDto allergy) {
        addFragment(AllergyDetailFragment.newInstance(allergy.getId()), true);
    }

    @Override
    public void addAllergy() {

    }

    @Override
    public void onMedicationClicked(MedicationDto medication) {
        addFragment(MedicationDetailFragment.newInstance(medication.getId()), true);
    }

    @Override
    public void addMedication() {

    }

    @Override
    public void onLabClicked(LabDto lab) {

    }

    @Override
    public void showListFragment(int type) {
        addFragment(MyHealthListFragment.newInstance(type), true);
    }
}
