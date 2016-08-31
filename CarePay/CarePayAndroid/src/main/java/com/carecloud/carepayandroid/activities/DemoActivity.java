package com.carecloud.carepayandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.activities.DemographicsActivity;

public class DemoActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        setTitle("Screens Demo");

        findViewById(R.id.sel_lang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SelectLanguageActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.resp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, ResponsibilityActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SignatureActivity.class);
                startActivity(intent);
            }
        });
    }
}
