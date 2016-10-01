package com.carecloud.carepayandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;

public class AndroidMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_android);
//        Intent intent = new Intent(this, LibraryMainActivity.class);
//        Intent intent = new Intent(this, InTakeActivity.class);
//        Intent intent = new Intent(this, PaymentActivity.class);
//        Intent intent = new Intent(this, AppointmentsActivity.class);
//        Intent intent = new Intent(this, ConsentActivity.class);
        Intent intent = new Intent(this, DemographicsActivity.class);
//        Intent intent = new Intent(this, SigninSignupActivity.class);
//        Intent intent = new Intent(this, SignUpConfirmActivity.class);

        startActivity(intent);
        finish();
    }
}
