package com.carecloud.carepayandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.models.ScreenModel;

public class LauncherActivity extends AppCompatActivity {
    ScreenModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }
}
