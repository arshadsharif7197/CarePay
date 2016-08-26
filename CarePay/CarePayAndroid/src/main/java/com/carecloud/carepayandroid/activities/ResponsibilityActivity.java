package com.carecloud.carepayandroid.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.ResponsibilityFragment;

public class
ResponsibilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsibility);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("resp");
        if (fragment == null) {
            fragment = new ResponsibilityFragment();
            fm.beginTransaction().replace(R.id.resp_frag_holder, fragment, "resp").commit();
        }
    }
}
