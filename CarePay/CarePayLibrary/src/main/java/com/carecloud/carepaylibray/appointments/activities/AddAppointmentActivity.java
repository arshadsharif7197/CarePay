package com.carecloud.carepaylibray.appointments.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.ChooseProviderFragment;

public class AddAppointmentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        //launch first fragment
        FragmentManager fm = getSupportFragmentManager();
        ChooseProviderFragment chooseProviderFragment = (ChooseProviderFragment)
                fm.findFragmentByTag(ChooseProviderFragment.class.getSimpleName());

        if (chooseProviderFragment == null) {
            chooseProviderFragment = new ChooseProviderFragment();
        }

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, chooseProviderFragment,
                ChooseProviderFragment.class.getSimpleName()).commit();
    }
}
