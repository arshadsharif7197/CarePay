package com.carecloud.carepay.patient.selectlanguage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by Rahul on 10/13/16.
 */
@Deprecated
public class SelectLanguageActivity extends BasePatientActivity implements FragmentActivityInterface {


    private SelectLanguageDTO languageDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acctivity_selectlanguage);
        languageDTO = getConvertedDTO(SelectLanguageDTO.class);
        if (savedInstanceState == null) {
            replaceFragment(SelectLanguageFragment.newInstance(), false);
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

    @Override
    public DTO getDto() {
        return languageDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.layoutselectlangauge, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.layoutselectlangauge, fragment, addToBackStack);
    }
}
