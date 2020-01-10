package com.carecloud.carepay.practice.library.checkin.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationActivity extends BasePracticeActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;
    private TextView genderButton;
    private EditText dobEditText;
    private CarePayButton findMyAppointmentButton;
    private SignInDTO signInPatientModeDTO;
    private String[] gendersArray;
    private TextInputLayout dobInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInPatientModeDTO = getConvertedDTO(SignInDTO.class);
        setContentView(R.layout.activity_personal_information);
        initViews();
    }

    private void initViews() {
        CarePayButton goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(homeImageViewListener);
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(homeImageViewListener);

        setUpFirstName();
        setUpLastName();
        setUpPhone();
        setUpGender();
        setUpDoB();
    }

    private void setUpFirstName() {
        TextInputLayout firstNameInputLayout = findViewById(R.id.firstNameInputLayout);
        firstNameInputLayout.setTag(Label.getLabel("personal_info_first_name"));
        firstNameEditText = findViewById(R.id.firstNameEditText);
        firstNameEditText.setTag(firstNameInputLayout);
        firstNameEditText.addTextChangedListener(commonTextWatcher);
    }

    private void setUpLastName() {
        TextInputLayout lastNameInputLayout = findViewById(R.id.lastNameInputLayout);
        lastNameInputLayout.setTag(Label.getLabel("personal_info_last_name"));
        lastNameEditText = findViewById(R.id.lastNameEditText);
        lastNameEditText.setTag(lastNameInputLayout);
        lastNameEditText.addTextChangedListener(commonTextWatcher);
    }

    private void setUpPhone() {
        TextInputLayout phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        phoneNumberInputLayout.setTag(Label.getLabel("personal_info_phone_number"));
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setTag(phoneNumberInputLayout);
        phoneNumberEditText.addTextChangedListener(phoneInputFormatter);
        phoneNumberEditText.setOnClickListener(selectEndOnClick);
    }

    private void setUpGender() {
        CarePayTextView genderTextView = findViewById(R.id.genderTextView);
        genderTextView.setText(signInPatientModeDTO.getMetadata().getDataModels().getPersonalInfo()
                .getProperties().getGender().getLabel());
        genderButton = findViewById(R.id.selectGenderButton);
        genderButton.setOnClickListener(selectGenderButtonListener);
        genderButton.addTextChangedListener(commonTextWatcher);

        findMyAppointmentButton = findViewById(R.id.findMyAppointmentButton);
        findMyAppointmentButton.setOnClickListener(findMyAppointmentButtonListener);

        List<OptionDTO> options = signInPatientModeDTO.getMetadata().getDataModels()
                .getPersonalInfo().getProperties().getGender().getOptions();
        List<String> genders = new ArrayList<>();
        for (OptionDTO option : options) {
            genders.add(option.getLabel());
        }
        gendersArray = genders.toArray(new String[0]);
    }

    private void setUpDoB() {
        dobInputLayout = findViewById(R.id.dobInputLayout);
        dobInputLayout.setTag(Label.getLabel("personal_info_date_of_birth"));
        dobEditText = findViewById(R.id.dobEditText);
        dobEditText.setTag(dobInputLayout);
        dobEditText.setOnClickListener(selectEndOnClick);
        dobEditText.addTextChangedListener(new TextWatcher() {
            int lastLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                lastLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringUtil.autoFormatDateOfBirth(editable, lastLength);
                enableFindMyAppointmentButton();
            }
        });
    }

    /**
     * Method to enable or disable Find My Appointment button
     */
    private void enableFindMyAppointmentButton() {
        boolean areAllNonEmpty = !(StringUtil.isNullOrEmpty(firstNameEditText.getText().toString())
                || StringUtil.isNullOrEmpty(lastNameEditText.getText().toString())
                || StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString())
                || genderButton.getText().toString().equalsIgnoreCase(Label.getLabel("choose_gender_label")));

        if (!StringUtil.isNullOrEmpty(dobEditText.getText().toString())) {
            String dateValidationResult = DateUtil.getDateOfBirthValidationResultMessage(dobEditText.getText().toString());
            if (dateValidationResult != null) {
                dobInputLayout.setErrorEnabled(true);
                dobInputLayout.setError(dateValidationResult);
                areAllNonEmpty = false;
            } else {
                dobInputLayout.setErrorEnabled(false);
                dobInputLayout.setError(null);
            }

        } else {
            dobInputLayout.setErrorEnabled(false);
            dobInputLayout.setError(null);
            areAllNonEmpty = false;
        }

        findMyAppointmentButton.setEnabled(areAllNonEmpty);
    }


    /**
     * Listener to select gender
     */
    View.OnClickListener selectGenderButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView genderButton = (TextView) view;
            SystemUtil.showChooseDialog(PersonalInformationActivity.this,
                    gendersArray, Label.getLabel("gender_label"), Label.getLabel("gender_cancel_label"),
                    genderButton,
                    (destination, selectedOption) -> genderButton.setText(selectedOption));
        }
    };

    /**
     * Click listener for Find My Appointment button
     */
    View.OnClickListener findMyAppointmentButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            findMyAppointmentButton.setEnabled(false);
            callGetFindMyAppointments();
        }
    };

    /**
     * Click listener for home icon
     */
    View.OnClickListener homeImageViewListener = view -> onBackPressed();

    private void callGetFindMyAppointments() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("first_name", firstNameEditText.getText().toString());
        queryMap.put("last_name", lastNameEditText.getText().toString());
        queryMap.put("date_of_birth", DateUtil.getInstance().setDateRaw(dobEditText.getText().toString(),
                true).toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("phone", StringUtil.revertToRawFormat(phoneNumberEditText.getText().toString()));
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        queryMap.put("gender", ((TextView) findViewById(R.id.selectGenderButton)).getText().toString());
        TransitionDTO transitionDTO;
        transitionDTO = signInPatientModeDTO.getMetadata().getLinks().getPersonalInfo();
        getWorkflowServiceHelper().execute(transitionDTO, findMyAppointmentsCallback, queryMap);
    }

    WorkflowServiceCallback findMyAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            findMyAppointmentButton.setEnabled(true);
            Map<String, String> queryMap = new HashMap<>();
            TransitionDTO transitionDTO;
            Gson gson = new Gson();
            SignInDTO signInDTO = gson.fromJson(workflowDTO.toString(), SignInDTO.class);
            getApplicationMode().setPatientId(signInDTO.getPayload().getPatientModePersonalInfoCheck()
                    .getMetadata().getPatientId());
            if (signInDTO.getPayload().getPatientModePersonalInfoCheck().getPersonalInfoCheckSuccessful()) {
                queryMap.put("language", getApplicationPreferences().getUserLanguage());
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                queryMap.put("patient_id", getApplicationMode().getPatientId());
                Map<String, String> headers = new HashMap<>();

                getAppAuthorizationHelper().setUser(signInDTO.getPayload().getPatientModePersonalInfoCheck()
                        .getMetadata().getUsername());

                MixPanelUtil.setUser(getContext(), signInDTO.getPayload().getPatientModePersonalInfoCheck()
                        .getMetadata().getUserId(), null);

                String[] params = {getString(R.string.param_login_type), getString(R.string.param_app_mode)};
                Object[] values = {getString(R.string.login_manual), getString(R.string.app_mode_patient)};
                MixPanelUtil.logEvent(getString(R.string.event_signin_loginSuccess), params, values);

                transitionDTO = signInPatientModeDTO.getMetadata().getTransitions().getAction();
                getWorkflowServiceHelper().execute(transitionDTO, patientModeAppointmentsCallback, queryMap, headers);
            } else {
                String errorMessage = Label.getLabel("sign_in_failed") + ", "
                        + Label.getLabel("personal_info_incorrect_details");
                showErrorNotification(errorMessage);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findMyAppointmentButton.setEnabled(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback patientModeAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            findMyAppointmentButton.setEnabled(true);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findMyAppointmentButton.setEnabled(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    TextWatcher commonTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            enableFindMyAppointmentButton();
        }
    };

    protected View.OnClickListener selectEndOnClick = view -> {
        EditText editText = (EditText) view;
        editText.setSelection(editText.length());
    };

    protected TextWatcher phoneInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatPhone(editable, lastLength);
            enableFindMyAppointmentButton();
        }
    };
}