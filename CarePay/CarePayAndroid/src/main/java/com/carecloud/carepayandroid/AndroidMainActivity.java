package com.carecloud.carepayandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;

public class AndroidMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_android);
//        Intent intent = new Intent(this, LibraryMainActivity.class);
        Intent intent = new Intent(this, DemographicsActivity.class);
        startActivity(intent);
        finish();
    }
}
