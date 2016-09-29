package com.carecloud.carepayclover;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class CloverMainActivity extends AppCompatActivity {

    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);
        mainLayout= (LinearLayout) findViewById(R.id.mainLayout);
    }

}
