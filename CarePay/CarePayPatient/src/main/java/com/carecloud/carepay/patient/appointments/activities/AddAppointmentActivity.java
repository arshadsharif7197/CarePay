package com.carecloud.carepay.patient.appointments.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;

public class AddAppointmentActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        //launch first fragment
        FragmentManager fm = getSupportFragmentManager();
        String tag = ChooseProviderFragment.class.getSimpleName();
        ChooseProviderFragment chooseProviderFragment = (ChooseProviderFragment)
                fm.findFragmentByTag(tag);

        if (chooseProviderFragment == null) {
            chooseProviderFragment = new ChooseProviderFragment();
        }

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, chooseProviderFragment,
                tag).addToBackStack(tag).commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();

    }
}
