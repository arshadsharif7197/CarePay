package com.carecloud.carepay.practice.library.checkin.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.signin.dtos.GenderOptionDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationActivity extends BasePracticeActivity {
    private CarePayButton selectDateButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;
    private CarePayButton findMyAppointmentButton;
    private boolean isEmptyFirstName = true;
    private boolean isEmptyLastName = true;
    private boolean isEmptyPhoneNumber = true;
    private boolean isEmptyDate = true;
    private boolean isEmptyGender = true;
    private SigninPatientModeDTO signinPatientModeDTO;
    private SigninPatientModeLabelsDTO labelsDTO;
    private String[] gendersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);
        labelsDTO = signinPatientModeDTO.getMetadata().getLabels();
        setContentView(R.layout.activity_personal_information);
        setNavigationBarVisibility();
        /*Initialise views*/
        initViews();
    }

    /**
     * Method to initialise view
     */
    void initViews() {
        CarePayButton goBackButton = (CarePayButton)
                findViewById(R.id.goBackButton);
        goBackButton.setText(labelsDTO.getSiginHowCheckInGoBack());
        goBackButton.setOnClickListener(goBackButtonListener);

        CarePayTextView personalInformationTextView =
                (CarePayTextView) findViewById(R.id.personalInformationTextView);
        personalInformationTextView.setText(labelsDTO.getPersonalInfoPersonalInformation());
        personalInformationTextView.setTextColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));

        CarePayTextView identifyYourselfTextView = (CarePayTextView)
                findViewById(R.id.identifyYourselfTextView);
        identifyYourselfTextView.setText(labelsDTO.getPersonalInfoIdentifyYourself());
        identifyYourselfTextView.setTextColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));

        TextInputLayout firstNameInputLayout = (TextInputLayout)
                findViewById(R.id.firstNameInputLayout);
        firstNameInputLayout.setTag(labelsDTO.getPersonalInfoFirstName());

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        firstNameEditText.setHint(labelsDTO.getPersonalInfoFirstName());
        firstNameEditText.setTag(firstNameInputLayout);

        TextInputLayout lastNameInputLayout = (TextInputLayout)
                findViewById(R.id.lastNameInputLayout);
        lastNameInputLayout.setTag(labelsDTO.getPersonalInfoLastName());

        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        lastNameEditText.setHint(labelsDTO.getPersonalInfoLastName());
        lastNameEditText.setTag(lastNameInputLayout);

        TextInputLayout phoneNumberInputLayout = (TextInputLayout)
                findViewById(R.id.phoneNumberInputLayout);
        phoneNumberInputLayout.setTag(labelsDTO.getPersonalInfoPhoneNumber());

        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setHint(labelsDTO.getPersonalInfoPhoneNumber());
        phoneNumberEditText.setTag(phoneNumberInputLayout);

        setChangeFocusListeners();

        CarePayTextView dateOfBirthTextView = (CarePayTextView)
                findViewById(R.id.dateOfBirthTextView);
        dateOfBirthTextView.setText(labelsDTO.getPersonalInfoDateOfBirth());

        selectDateButton = (CarePayButton) findViewById(R.id.selectDateButton);
        selectDateButton.setText(labelsDTO.getPersonalInfoSelect());
        selectDateButton.setOnClickListener(selectDateButtonListener);


        CarePayTextView genderTextView = (CarePayTextView) findViewById(R.id.genderTextView);
        genderTextView.setText(signinPatientModeDTO.getMetadata().getLoginDataModels().getPersonalInfo().getProperties().getGender().getLabel());

        CarePayButton genderButton =(CarePayButton) findViewById(R.id.selectGenderButton);
        genderButton.setText(labelsDTO.getChooseGenderLabel());
        genderButton.setOnClickListener(selectGenderButtonListener);
        genderButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String genderValue = editable.toString();
                isEmptyGender = genderValue.equalsIgnoreCase(labelsDTO.getChooseGenderLabel());
                enableFindMyAppointmentButton();

            }
        });

        findMyAppointmentButton = (CarePayButton)
                findViewById(R.id.findMyAppointmentButton);
        findMyAppointmentButton.setText(labelsDTO.getPersonalInfoFindMyAppointments());
        findMyAppointmentButton.setEnabled(false);
        findMyAppointmentButton.setOnClickListener(findMyAppointmentButtonListener);

        if (!findMyAppointmentButton.isEnabled()) {
            findMyAppointmentButton.setAlpha(0.3F);
        } else {
            findMyAppointmentButton.setAlpha(1);
        }

        setTextListeners();

        ImageView homeImageView = (ImageView) findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(homeImageViewListener);

        List<GenderOptionDTO> options = signinPatientModeDTO.getMetadata().getLoginDataModels().getPersonalInfo().getProperties().getGender().getOptions();
        List<String> genders = new ArrayList<>();
        for (GenderOptionDTO o : options) {
            genders.add(o.getLabel());
        }
        gendersArray = genders.toArray(new String[0]);
    }

    /**
     * On focus changes listener for Edit Text.
     */
    private void setChangeFocusListeners() {
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(PersonalInformationActivity.this);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(PersonalInformationActivity.this);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(PersonalInformationActivity.this);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }

    /**
     * Text change listeners
     */
    private void setTextListeners() {
        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmptyFirstName = StringUtil.isNullOrEmpty(firstNameEditText.getText().toString());
                enableFindMyAppointmentButton();
            }
        });
        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmptyLastName = StringUtil.isNullOrEmpty(lastNameEditText.getText().toString());
                enableFindMyAppointmentButton();
            }
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmptyPhoneNumber = StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString());
                enableFindMyAppointmentButton();
            }
        });
        selectDateButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dateVal = selectDateButton.getText().toString();

                isEmptyDate = dateVal.equalsIgnoreCase(labelsDTO.getPersonalInfoSelect());
                enableFindMyAppointmentButton();
            }
        });



    }

    /**
     * Method to enable or disable Find My Appointment button
     */
    private void enableFindMyAppointmentButton() {
        boolean areAllNonEmpty = !(isEmptyFirstName || isEmptyLastName || isEmptyPhoneNumber || isEmptyDate || isEmptyGender);

        findMyAppointmentButton.setEnabled(areAllNonEmpty);
        if (!findMyAppointmentButton.isEnabled()) {
            findMyAppointmentButton.setAlpha(0.3F);
        } else {
            findMyAppointmentButton.setAlpha(1);
        }
    }


    /**
     * Listener to select gender
     */
    View.OnClickListener selectGenderButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            selectGender((CarePayButton) view);
        }
    };


    /**
     * Click listener for go back button
     */
    View.OnClickListener goBackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };


    /**
     * Selects gender
     */
    public void selectGender(final CarePayButton genderButton){
        SystemUtil.showChooseDialog(this,
                gendersArray, labelsDTO.getGenderLabel(), labelsDTO.getGenderCancelLabel(),
                genderButton,
                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        genderButton.setText(selectedOption);
                    }
                });
    }


    /**
     * Click listener for select date button
     */
    View.OnClickListener selectDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            // Use the current date as the default date in the picker
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            try {
                if(!selectDateButton.getText().toString().equalsIgnoreCase(labelsDTO.getPersonalInfoSelect())){
                    String [] selectedDate = selectDateButton.getText().toString().split("/");
                    month = Integer.parseInt(selectedDate[0])-1;
                    day = Integer.parseInt(selectedDate[1]);
                    year = Integer.parseInt(selectedDate[2]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInformationActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    LocalDate date = new LocalDate(year, month + 1, day);
                    DateUtil.getInstance().setDate(date.toDate());
                    selectDateButton.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
                }
            }, year, month, day);

            datePickerDialog.setTitle(labelsDTO.getPersonalInfoSelect());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
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
    View.OnClickListener homeImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private void callGetFindMyAppointments(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("first_name", firstNameEditText.getText().toString());
        queryMap.put("last_name", lastNameEditText.getText().toString());
        queryMap.put("date_of_birth", selectDateButton.getText().toString());
        queryMap.put("phone", phoneNumberEditText.getText().toString());
        queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
        queryMap.put("gender",  ((CarePayButton) findViewById(R.id.selectGenderButton)).getText().toString());
        TransitionDTO transitionDTO;
        transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getPersonalInfo();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, findMyAppointmentsCallback, queryMap);
    }

    WorkflowServiceCallback findMyAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findMyAppointmentButton.setEnabled(true);
            Map<String, String> queryMap = new HashMap<>();
            TransitionDTO transitionDTO;
            Gson gson = new Gson();
            SigninPatientModeDTO signinPatientModeDTOLocal = gson.fromJson(workflowDTO.toString(), SigninPatientModeDTO.class);
            if(signinPatientModeDTOLocal.getPayload().getPatientModePersonalInfoCheck().getPersonalInfoCheckSuccessful()){
                queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
                queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
                queryMap.put("patient_id", signinPatientModeDTOLocal.getPayload().getPatientModePersonalInfoCheck().getMetadata().getPatientId());
                Map<String, String> headers = new HashMap<>();
                CognitoAppHelper.setUser(signinPatientModeDTOLocal.getPayload().getPatientModePersonalInfoCheck().getMetadata().getUsername());
                transitionDTO = signinPatientModeDTO.getMetadata().getTransitions().getAction();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, patientModeAppointmentsCallback, queryMap, headers);
            } else {
                SystemUtil.showFailureDialogMessage(PersonalInformationActivity.this, StringUtil.getLabelForView(labelsDTO.getSignInFailed()),
                        StringUtil.getLabelForView(labelsDTO.getPersonalInfoIncorrectDetails()));
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findMyAppointmentButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(PersonalInformationActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback patientModeAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findMyAppointmentButton.setEnabled(true);
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findMyAppointmentButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(PersonalInformationActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}