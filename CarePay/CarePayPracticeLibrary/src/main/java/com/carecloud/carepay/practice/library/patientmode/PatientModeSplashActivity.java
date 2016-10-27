package com.carecloud.carepay.practice.library.patientmode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;

import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLabelsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModePayloadDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSplashDTO;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;

import java.util.ArrayList;

import java.util.List;

public class PatientModeSplashActivity extends BasePracticeActivity {

    private TextView getStartedButton;
    private TextView languageButton;
    private TextView praticewelcomeText;
    private ImageView practicelogo;
    private List<String> language = new ArrayList<String>();

    PatientModeSplashDTO patientModeSplashDTO;
PatientModePayloadDTO patientModePayloadDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_splash);
        patientModeSplashDTO = getConvertedDTO(PatientModeSplashDTO.class);
        initViews();
        initializeLebals();
        setClicables();

    }

    private void initViews() {

        getStartedButton = (TextView) findViewById(R.id.getstartedTextview);
        languageButton = (TextView) findViewById(R.id.languageTextview);
        praticewelcomeText = (TextView) findViewById(R.id.welcomeTitleTextview);
        practicelogo = (ImageView) findViewById(R.id.practicelogo);

    }
    private void initializeLebals() {
        if (patientModeSplashDTO != null) {
            PatientModeLabelsDTO patientModeLabelsDTO = patientModeSplashDTO.getMetadata().getLabel();
            patientModePayloadDTO=patientModeSplashDTO.getPayload();

            if (patientModeLabelsDTO != null) {
                getStartedButton.setText(patientModeLabelsDTO.getGetStartedHeading());
                praticewelcomeText.setText(patientModeLabelsDTO.getWelcomeHeading());

                }

            if(patientModePayloadDTO != null) {
                int langaugelistsize = patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().size();

                for (int i = 0; i < langaugelistsize; i++) {
                    language.add(i, patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().get(i).getLabel());
                   if( patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().get(i).getDefault().equals(true))
                   {
                       languageButton.setText(patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().get(i).getCode().toUpperCase());
                   }
                }
            }

        }
    }

    private void setClicables() {

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkedInIntent = new Intent(PatientModeSplashActivity.this, HowToCheckInActivity.class);
                checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(checkedInIntent);


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
                CustomAlertAdapter adapter = new CustomAlertAdapter(PatientModeSplashActivity.this,language);
                listView.setAdapter(adapter);
                dialog.setView(customView);
                final AlertDialog alert = dialog.create();
                alert.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        languageButton.setText(patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().get(position).getCode().toUpperCase());
                        alert.dismiss();
                    }
                });

            }
        });


    }


}
