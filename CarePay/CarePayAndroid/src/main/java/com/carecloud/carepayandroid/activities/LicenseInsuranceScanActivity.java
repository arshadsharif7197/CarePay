package com.carecloud.carepayandroid.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.ScanDocumentFragment;

public class LicenseInsuranceScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_insurance_scan);

        FragmentManager fm = getSupportFragmentManager();
        ScanDocumentFragment fragment = (ScanDocumentFragment) fm.findFragmentByTag("scan_doc");
        if(fragment == null) {
            fragment = new ScanDocumentFragment();
            fm.beginTransaction().replace(R.id.licence_insurance_scan_frag_holder, fragment, "scan_doc")
            .commit();
        }
    }
}
