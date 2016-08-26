package com.carecloud.carepayandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.SelectLanguageFragment;

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

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(SelectLanguageFragment.class.getSimpleName());
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.demograph_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, DemographicsDocumentsScanActivity.class);
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

    }

    private void launchFragment(String fragName) {
        if(fragName != null && !fragName.equals("")) {
            Intent intent = new Intent(this, LauncherActivity.class);
            intent.putExtra("fragment", fragName);
            startActivity(intent);
        }
    }
}
