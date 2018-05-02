package com.carecloud.carepay.patient.consentforms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormProviderFormsFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormProvidersListFragment;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormInterface;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.interfaces.DTO;

public class ConsentFormsActivity extends MenuPatientActivity implements ConsentFormInterface {

    private ConsentFormDTO consentFormsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consentFormsDTO = getConvertedDTO(ConsentFormDTO.class);
        if (savedInstanceState == null) {
            replaceFragment(ConsentFormProvidersListFragment.newInstance(), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_forms);
        menuItem.setChecked(true);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, menuItem.getTitle().toString());
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            displayToolbar(true, null);
        }
    }

    @Override
    public DTO getDto() {
        return consentFormsDTO == null ? consentFormsDTO = getConvertedDTO(ConsentFormDTO.class) : consentFormsDTO;
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
    public void onProviderSelected(FormDTO practiceForm, int position) {
        addFragment(ConsentFormProviderFormsFragment.newInstance(position), true);
    }

    @Override
    public void onPendingFormSelected(PracticeForm form, boolean isChecked) {

    }

    @Override
    public void onFilledFormSelected(PracticeForm form) {

    }
}
