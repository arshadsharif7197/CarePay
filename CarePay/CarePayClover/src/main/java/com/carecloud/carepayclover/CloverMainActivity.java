package com.carecloud.carepayclover;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CloverMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ((TextView) findViewById(R.id.checkinTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckedInActivity.class);
                checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(checkedInIntent);
            }
        });
    }

}
