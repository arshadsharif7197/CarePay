package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Main activity for Settings workflow
 */
public class DemographicsSettingsActivity extends BasePatientActivity {
    DemographicsSettingsDTO demographicsSettingsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics_settings);

        Intent intent = getIntent();
        demographicsSettingsDTO = getConvertedDTO(DemographicsSettingsDTO.class);
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

        FragmentManager fm = getSupportFragmentManager();
        DemographicsSettingsFragment fragment = (DemographicsSettingsFragment)
                fm.findFragmentByTag(DemographicsSettingsFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new DemographicsSettingsFragment();
       }
        if(fragment.getArguments() !=null){
            fragment.getArguments().putAll(bundle);
        }else{
            fragment.setArguments(bundle);
        }

        Fragment demographicsSettingsFragment = fm.findFragmentByTag(DemographicsSettingsFragment.class.getSimpleName());
        if(!(null!= demographicsSettingsFragment && demographicsSettingsFragment instanceof DemographicsSettingsFragment)) {
            fm.beginTransaction().add(R.id.activity_demographics_settings, fragment,
                    DemographicsSettingsFragment.class.getSimpleName()).commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }else {
            getFragmentManager().popBackStack();
        }
    }
}
