package com.carecloud.carepay.patient.consentforms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

public class ConsentFormsActivity extends MenuPatientActivity implements FragmentActivityInterface {

    private ConsentFormDTO consentFormsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consentFormsDTO = getConvertedDTO(ConsentFormDTO.class);
        if (savedInstanceState == null) {
            replaceFragment(ConsentFormsListFragment.newInstance(), false);
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
}
