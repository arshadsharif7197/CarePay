package com.carecloud.carepayandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepaylibray.activities.MainActivityLibrary;

public class MainActivityAndroid extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_android);
        Intent intent = new Intent(this, MainActivityLibrary.class);
        startActivity(intent);
        finish();
    }
}
