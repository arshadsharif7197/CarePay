package com.carecloud.carepayandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.SelectLanguageFragment;

public class SelectLanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        if (savedInstanceState == null) {
            SelectLanguageFragment mSelectLanguageFragment = new SelectLanguageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSelectLanguageFragment).commit();
        }
    }
}
