package com.carecloud.carepay.patient.menu;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.HelpFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

public class PatientHelpActivity extends BasePatientActivity implements FragmentActivityInterface {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_patient_help);

        addFragment(new HelpFragment(), true);
    }


    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getFragments().size() == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void setToolbar(Toolbar toolbar) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, com.carecloud.carepaylibrary.R.drawable.icn_nav_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }
}
