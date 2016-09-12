package com.carecloud.carepaylibray.appointments.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;

public class AppointmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment fragment = (AppointmentsListFragment) fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new AppointmentsListFragment();
        }
        fm.beginTransaction().replace(R.id.appointments_list_frag_holder, fragment,
                AppointmentsListFragment.class.getSimpleName()).commit();
    }
}
