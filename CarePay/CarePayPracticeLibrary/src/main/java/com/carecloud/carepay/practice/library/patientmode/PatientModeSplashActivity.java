package com.carecloud.carepay.practice.library.patientmode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;

import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLabelsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeOptionDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModePayloadDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSplashDTO;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

import java.util.ArrayList;

import java.util.List;

public class PatientModeSplashActivity extends BasePracticeActivity {

    private TextView  getStartedButton;
    private TextView  praticewelcomeText;
    private ImageView practicelogo;
    private List<String> languages = new ArrayList<String>();

    PatientModeSplashDTO  patientModeSplashDTO;
    PatientModePayloadDTO patientModePayloadDTO;
    private Spinner langSpinner;

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
        praticewelcomeText = (TextView) findViewById(R.id.welcomeTitleTextview);
        practicelogo = (ImageView) findViewById(R.id.practicelogo);
        langSpinner = (Spinner) findViewById(R.id.splashPatientLangSpinner);
    }

    private void initializeLebals() {
        if (patientModeSplashDTO != null) {
            PatientModeLabelsDTO patientModeLabelsDTO = patientModeSplashDTO.getMetadata().getLabel();
            patientModePayloadDTO = patientModeSplashDTO.getPayload();

            if (patientModeLabelsDTO != null) {
                getStartedButton.setText(patientModeLabelsDTO.getGetStartedHeading());
                praticewelcomeText.setText(patientModeLabelsDTO.getWelcomeHeading());

            }

            if (patientModePayloadDTO != null) {
                // set the languages spinner
                int langaugelistsize = patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().size();
                PatientModeOptionDTO defaultLangOption = null;
                int indexDefault = 0;
                for (int i = 0; i < langaugelistsize; i++) {
                    PatientModeOptionDTO languageOption = patientModePayloadDTO.getPatientModeStart().getLanguage().getOptions().get(i);
                    languages.add(i, languageOption.getCode().toUpperCase());
                    if (languageOption.getDefault()) {
                        defaultLangOption = languageOption;
                        indexDefault = i;
                    }
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.home_spinner_item, languages);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                langSpinner.setAdapter(spinnerArrayAdapter);
                if (defaultLangOption != null) { // this should be always true, as there's always a default option
                    langSpinner.setSelection(indexDefault);
                    ApplicationPreferences.Instance.setUserLanguage(defaultLangOption.getCode());
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

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // save selected in preferences
                if(languages != null && languages.size() > position) {
                    ApplicationPreferences.Instance.setUserLanguage(languages.get(position).toLowerCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
