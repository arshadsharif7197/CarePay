package com.carecloud.carepayandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.ApplicationWorkFlow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.models.WorkFlowModel;

public class LauncherActivity extends AppCompatActivity {
    ScreenModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }
}
