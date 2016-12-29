package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Main activity for Settings workflow
 */
public class DemographicsSettingsActivity extends BasePatientActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics_settings);

        Intent intent = getIntent();

        FragmentManager fm = getSupportFragmentManager();
        DemographicsSettingsFragment fragment = (DemographicsSettingsFragment)
                fm.findFragmentByTag(DemographicsSettingsFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new DemographicsSettingsFragment();
        }
        Bundle bundle = new Bundle();

        Gson gson = new Gson();

        fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                DemographicsSettingsFragment.class.getSimpleName()).commit();
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
        super.onBackPressed();
    }

}
