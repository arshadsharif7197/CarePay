package com.carecloud.carepaylibray.selectlanguage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by Rahul on 10/13/16.
 */

public class SelectLangaugeActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acctivity_selectlanguage);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.layoutselectlangauge, new SelectLanguageFragment(), SelectLanguageFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(SelectLangaugeActivity.this);
            onBackPressed();
            return true;
        }
        return false;
    }
}
