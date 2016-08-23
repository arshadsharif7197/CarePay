package com.carecloud.carepayandroid.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepayandroid.responsibility.ResponsibilityFragment;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        putFragment("responsibility");
    }

    /**
     * Place a fragment in this activity
     */
    private void putFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ResponsibilityFragment fragment = (ResponsibilityFragment) fragmentManager.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = new ResponsibilityFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, fragment, tag)
//                    .addToBackStack(null) // later
                    .commit();
        }
    }
}
