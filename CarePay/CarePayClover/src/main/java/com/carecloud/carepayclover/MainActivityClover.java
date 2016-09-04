package com.carecloud.carepayclover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepaylibray.activities.MainActivityLibrary;

public class MainActivityClover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);
        Intent intent = new Intent(this, MainActivityLibrary.class);
        startActivity(intent);
        finish();
    }
}
