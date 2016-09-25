package com.carecloud.carepaylibray.appointments.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.ChooseProviderFragment;

import static com.carecloud.carepaylibray.utils.SystemUtil.setTypefaceFromAssets;

public class AddAppointmentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView titleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        //set title
        toolbar = (Toolbar) findViewById(R.id.add_appointment_toolbar);
        titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        setTypefaceFromAssets(this, "fonts/gotham_rounded_medium.otf", titleView);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

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

    public void setTitle(String newTitle) {
        titleView.setText(newTitle);
    }

    public void setToolbarNavigationIcon(Drawable icon) {
        toolbar.setNavigationIcon(icon);
    }
}
