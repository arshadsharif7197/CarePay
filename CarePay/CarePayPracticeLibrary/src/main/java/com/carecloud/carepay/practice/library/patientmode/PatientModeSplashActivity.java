package com.carecloud.carepay.practice.library.patientmode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;

import java.util.Arrays;

public class PatientModeSplashActivity extends AppCompatActivity {

    private String[] language = {"EN", "SP"};
    private TextView getStartededButton;
    private TextView languageButton;
    private TextView praticewelcomeText;
    private ImageView practicelogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_splash);

        initViews();
        setClicables();

    }


    private void initViews() {

        getStartededButton = (TextView) findViewById(R.id.getstartedTextview);
        languageButton = (TextView) findViewById(R.id.languageTextview);
        praticewelcomeText = (TextView) findViewById(R.id.welcomeTitleTextview);
        practicelogo = (ImageView) findViewById(R.id.practicelogo);

    }

    private void setClicables() {

        getStartededButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(PatientModeSplashActivity.this);
                dialog.setTitle("Select Language");
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                View customView = LayoutInflater.from(PatientModeSplashActivity.this).inflate(R.layout.alert_list_practice_layout, null, false);
                ListView listView = (ListView) customView.findViewById(R.id.dialoglist_practice);
                CustomAlertAdapter adapter = new CustomAlertAdapter(PatientModeSplashActivity.this, Arrays.asList(language));
                listView.setAdapter(adapter);
                dialog.setView(customView);
                final AlertDialog alert = dialog.create();
                alert.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        languageButton.setText(language[position]);
                        alert.dismiss();
                    }
                });

            }
        });


    }


}
