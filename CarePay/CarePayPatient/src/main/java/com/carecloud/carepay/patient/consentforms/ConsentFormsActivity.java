package com.carecloud.carepay.patient.consentforms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormPracticeFormsFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormProvidersListFragment;
import com.carecloud.carepay.patient.consentforms.fragments.FilledFormFragment;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormActivityInterface;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.interfaces.DTO;

import java.util.List;

public class ConsentFormsActivity extends MenuPatientActivity implements ConsentFormActivityInterface {

    private ConsentFormDTO consentFormsDTO;
    private List<PracticeForm> selectedForms;

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
        addFragment(ConsentFormPracticeFormsFragment.newInstance(position), true);
    }

    @Override
    public void showForms(List<PracticeForm> selectedForms, int selectedProviderIndex, boolean showSignButton) {
        this.selectedForms = selectedForms;
        addFragment(FilledFormFragment
                .newInstance(selectedProviderIndex, showSignButton), true);
    }

    @Override
    public List<PracticeForm> getAllFormsToShow() {
        return selectedForms;
    }
}
