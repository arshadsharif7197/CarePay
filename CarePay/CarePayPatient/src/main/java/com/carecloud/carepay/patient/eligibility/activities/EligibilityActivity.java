package com.carecloud.carepay.patient.eligibility.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepaylibray.utils.SystemUtil;

public class EligibilityActivity extends AppCompatActivity {
    private Toolbar eligibilityFormsToolbar;
    private TextView headerTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_eligibility);

        eligibilityFormsToolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.intakeToolbar);
        eligibilityFormsToolbar.setTitle("");
        eligibilityFormsToolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                com.carecloud.carepaylibrary.R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(eligibilityFormsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        headerTitleTextView = (TextView) eligibilityFormsToolbar.findViewById(com.carecloud.carepaylibrary.R.id.eligibilityToolbarTitle);
        SystemUtil.setGothamRoundedMediumTypeface(this, headerTitleTextView);
        headerTitleTextView.setText(String.format("Eligibility"));

    }
}
