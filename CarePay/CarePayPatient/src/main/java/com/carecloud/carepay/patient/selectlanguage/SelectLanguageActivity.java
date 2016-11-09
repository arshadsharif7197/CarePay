package com.carecloud.carepay.patient.selectlanguage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by Rahul on 10/13/16.
 */

public class SelectLanguageActivity extends BasePatientActivity {


    private SelectLanguageDTO languageDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acctivity_selectlanguage);

        languageDTO = getConvertedDTO(SelectLanguageDTO.class);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.layoutselectlangauge, new SelectLanguageFragment(), SelectLanguageFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(SelectLanguageActivity.this);
            onBackPressed();
            return true;
        }
        return false;
    }

    public SelectLanguageDTO getLanguageDTO() {
        return languageDTO;
    }

    public void setLanguageDTO(SelectLanguageDTO languageDTO) {
        this.languageDTO = languageDTO;
    }
}
