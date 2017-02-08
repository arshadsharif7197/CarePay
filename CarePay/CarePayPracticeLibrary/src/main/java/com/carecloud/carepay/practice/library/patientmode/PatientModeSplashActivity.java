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
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLabelsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModePayloadDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSplashDTO;
import com.carecloud.carepay.practice.library.signin.dtos.LanguageOptionDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModeSplashActivity extends BasePracticeActivity {

    private TextView  getStartedButton;
    private TextView  praticewelcomeText;
    private ImageView practicelogo;
    private ImageView lockIcnImageView;
    private List<String> languages = new ArrayList<>();

    PatientModeSplashDTO  patientModeSplashDTO;
    PatientModePayloadDTO patientModePayloadDTO;
    private Spinner langSpinner;
    private Context context;

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

    private void initViews() {
        getStartedButton = (TextView) findViewById(R.id.getstartedTextview);
        praticewelcomeText = (TextView) findViewById(R.id.welcomeTitleTextview);
        practicelogo = (ImageView) findViewById(R.id.practicelogo);
        lockIcnImageView = (ImageView) findViewById(R.id.lockicnimageView);
        langSpinner = (Spinner) findViewById(R.id.splashPatientLangSpinner);
    }

    private void initializeLabels() {
        if (patientModeSplashDTO != null) {
            PatientModeLabelsDTO patientModeLabelsDTO = patientModeSplashDTO.getMetadata().getLabels();
            patientModePayloadDTO = patientModeSplashDTO.getPayload();

            if (patientModeLabelsDTO != null) {
                getStartedButton.setText(patientModeLabelsDTO.getGetStartedHeading());
                praticewelcomeText.setText(patientModeLabelsDTO.getWelcomeHeading());
            }

            if (patientModePayloadDTO != null) {
                // set the languages spinner
                int langaugelistsize = patientModePayloadDTO.getLanguages().size();
                LanguageOptionDTO defaultLangOption = null;
                int indexDefault = 0;
                for (int i = 0; i < langaugelistsize; i++) {
                    LanguageOptionDTO languageOption = patientModePayloadDTO.getLanguages().get(i);
                    if (languageOption != null && languageOption.getCode() != null) {
                        languages.add(i, languageOption.getCode().toUpperCase());
                        if (languageOption.getDefault() != null && languageOption.getDefault()) {
                            defaultLangOption = languageOption;
                            indexDefault = i;
                        }
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

    private void setClickables() {
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStartedButton.setEnabled(false);

                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
                queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
                Map<String, String> headers = new HashMap<>();
                headers.put("transition", "true");
                TransitionDTO transitionDTO = patientModeSplashDTO.getMetadata().getTransitions().getStart();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, patientHomeCallback, queryMap, headers);
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

        lockIcnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(context,patientModeSplashDTO.getMetadata().getLinks().getPinpad(),patientModeSplashDTO.getMetadata().getLabels(), false);
                confirmationPinDialog.show();
            }
        });
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin,  String pin) {
        TransitionDTO transitionDTO = patientModeSplashDTO.getMetadata().getTransitions().getPracticeMode();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
        WorkflowServiceHelper.getInstance().execute(transitionDTO, patientHomeCallback, query);
    }

    WorkflowServiceCallback patientHomeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            getStartedButton.setEnabled(true);
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            getStartedButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(PatientModeSplashActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // log out previous user from Cognito
        Log.v(this.getClass().getSimpleName(), "sign out Cognito");
        CognitoAppHelper.getPool().getUser().signOut();
        CognitoAppHelper.setUser(null);
        ApplicationMode.getInstance().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
    }


}