package com.carecloud.carepay.practice.library.patientmode;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModePayloadDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSplashDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModeSplashActivity extends BasePracticeActivity {

    private TextView getStartedButton;
    private TextView praticeWelcomeText;
    private ImageView practiceLogo;
    private ImageView lockIcnImageView;
    private List<String> languages = new ArrayList<>();

    PatientModeSplashDTO patientModeSplashDTO;
    PatientModePayloadDTO patientModePayloadDTO;
    private Spinner langSpinner;
    private Context context;

    private boolean isUserInteraction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_splash);
        this.context = this;
        patientModeSplashDTO = getConvertedDTO(PatientModeSplashDTO.class);
        initViews();
        initializeLabels();
        setClickables();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteraction = true;
    }

    private void initViews() {
        getStartedButton = findViewById(R.id.getstartedTextview);
        praticeWelcomeText = findViewById(R.id.welcomeTitleTextview);
        practiceLogo = findViewById(R.id.practicelogo);
        lockIcnImageView = findViewById(R.id.lockicnimageView);
        langSpinner = findViewById(R.id.splashPatientLangSpinner);
    }

    private void initializeLabels() {
        if (patientModeSplashDTO != null) {
            patientModePayloadDTO = patientModeSplashDTO.getPayload();
            getStartedButton.setText(Label.getLabel("get_started_heading"));

            praticeWelcomeText.setText(patientModeSplashDTO.getPayload().getPractice()
                    .getWelcomeScreen().getMessage());

            String welcomeLogoUrl = patientModeSplashDTO.getPayload().getPractice()
                    .getWelcomeScreen().getWelcomePhoto();
            if (welcomeLogoUrl != null) {
                Picasso.with(this).load(welcomeLogoUrl).error(R.drawable.ic_splash_logo).into(practiceLogo);
            } else {
                practiceLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_splash_logo));
            }

            if (patientModePayloadDTO != null) {
                // set the languages spinner
                int langaugelistsize = patientModePayloadDTO.getLanguages().size();
                OptionDTO defaultLangOption = null;
                int indexDefault = 0;
                for (int i = 0; i < langaugelistsize; i++) {
                    OptionDTO languageOption = patientModePayloadDTO.getLanguages().get(i);
                    if (languageOption != null && languageOption.getCode() != null) {
                        languages.add(i, languageOption.getCode().toUpperCase());
                        if (languageOption.isDefault() != null && languageOption.isDefault()) {
                            defaultLangOption = languageOption;
                            indexDefault = i;
                        }
                    }
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                        R.layout.home_spinner_item, languages);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                langSpinner.setAdapter(spinnerArrayAdapter);
                if (defaultLangOption != null) { // this should be always true, as there's always a default option
                    langSpinner.setSelection(indexDefault);
                    //getApplicationPreferences().setUserLanguage(defaultLangOption.getCode());
                }
            }
        }
    }

    private void setClickables() {
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStartedButton.setEnabled(false);

                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("language", getApplicationPreferences().getUserLanguage());
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                Map<String, String> headers = new HashMap<>();
                headers.put("transition", "true");
                TransitionDTO transitionDTO = patientModeSplashDTO.getMetadata().getTransitions().getStart();
                getWorkflowServiceHelper().execute(transitionDTO, patientHomeCallback, queryMap, headers);
            }
        });

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserInteraction) {
                    return;
                }
                // save selected in preferences
                if (languages != null && languages.size() > position) {
                    getApplicationPreferences().setUserLanguage(languages.get(position).toLowerCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lockIcnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmationPinDialog confirmationPinDialog = ConfirmationPinDialog.newInstance(
                        patientModeSplashDTO.getMetadata().getLinks().getPinpad(),
                        false,
                        patientModeSplashDTO.getMetadata().getLinks().getLanguage());
                displayDialogFragment(confirmationPinDialog, false);
            }
        });
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        TransitionDTO transitionDTO = patientModeSplashDTO.getMetadata().getTransitions().getPracticeMode();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, patientHomeCallback, query);
    }

    WorkflowServiceCallback patientHomeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            getStartedButton.setEnabled(true);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            getStartedButton.setEnabled(true);
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };


    @Override
    public void onBackPressed() {
        //prevent going back
    }


}