package com.carecloud.carepayandroid.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.ResponsibilityFragment;
import com.carecloud.carepaylibray.fragments.SelectLanguageFragment;

public class LauncherActivity extends AppCompatActivity {

    private static final String LOG_TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        setSupportActionBar(new Toolbar(this));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String fragName = null;
        if (intent.hasExtra("fragment")) {
            fragName = intent.getStringExtra("fragment");
            Log.v(LOG_TAG, fragName);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("fragName");
        if (fragment == null) {
            fragment = createFragment(fragName);
            fm.beginTransaction().replace(R.id.frag_holder, fragment, fragName).commit();
        }
    }

    private Fragment createFragment(String fragName) {
        if (fragName == null) {
            return null;
        }
        if (fragName.equals(ResponsibilityFragment.class.getSimpleName())) {
            return new ResponsibilityFragment();
        } else if (fragName.equals(SelectLanguageFragment.class.getSimpleName())) {
            return new SelectLanguageFragment();
        } // etc
        return null;
    }
}
