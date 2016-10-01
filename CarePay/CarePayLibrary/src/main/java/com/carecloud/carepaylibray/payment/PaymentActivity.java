package com.carecloud.carepaylibray.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        FragmentManager fm = getSupportFragmentManager();
        ResponsibilityFragment fragment = (ResponsibilityFragment) fm.findFragmentByTag(ResponsibilityFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new ResponsibilityFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CarePayConstants.INTAKE_BUNDLE, intent.getSerializableExtra(CarePayConstants.INTAKE_BUNDLE));
        fragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.payment_frag_holder, fragment,
                ResponsibilityFragment.class.getSimpleName()).commit();
    }
}