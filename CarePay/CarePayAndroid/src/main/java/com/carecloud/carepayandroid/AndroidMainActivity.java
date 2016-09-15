package com.carecloud.carepayandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.intake.InTakeActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;

public class AndroidMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_android);
//        Intent intent = new Intent(this, LibraryMainActivity.class);
//        Intent intent = new Intent(this, InTakeActivity.class);
//        Intent intent = new Intent(this, PaymentActivity.class);
//        Intent intent = new Intent(this, AppointmentsActivity.class);
        Intent intent = new Intent(this, ConsentActivity.class);
        startActivity(intent);
        finish();
    }
}
