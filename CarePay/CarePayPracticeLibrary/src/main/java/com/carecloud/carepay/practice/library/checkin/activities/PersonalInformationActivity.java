package com.carecloud.carepay.practice.library.checkin.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationActivity extends BasePracticeActivity {
    private TextView selectDateButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;
    private CarePayButton findMyAppointmentButton;
    private boolean isEmptyFirstName = true;
    private boolean isEmptyLastName = true;
    private boolean isEmptyPhoneNumber = true;
    private boolean isEmptyDate = true;
    private boolean isEmptyGender = true;
    private SignInDTO signInPatientModeDTO;
    private String[] gendersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInPatientModeDTO = getConvertedDTO(SignInDTO.class);
        setContentView(R.layout.activity_personal_information);
        initViews();
    }

    /**
     * Method to initialise view
     */
    void initViews() {
        CarePayButton goBackButton = (CarePayButton) findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(goBackButtonListener);

        TextInputLayout firstNameInputLayout = (TextInputLayout)
                findViewById(R.id.firstNameInputLayout);
        firstNameInputLayout.setTag(Label.getLabel("personal_info_first_name"));

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        firstNameEditText.setTag(firstNameInputLayout);

        TextInputLayout lastNameInputLayout = (TextInputLayout)
                findViewById(R.id.lastNameInputLayout);
        lastNameInputLayout.setTag(Label.getLabel("personal_info_last_name"));

        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        lastNameEditText.setTag(lastNameInputLayout);

        TextInputLayout phoneNumberInputLayout = (TextInputLayout)
                findViewById(R.id.phoneNumberInputLayout);
        phoneNumberInputLayout.setTag(Label.getLabel("personal_info_phone_number"));

        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setTag(phoneNumberInputLayout);

        //setChangeFocusListeners();

        selectDateButton = (TextView) findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(selectDateButtonListener);


        CarePayTextView genderTextView = (CarePayTextView) findViewById(R.id.genderTextView);
        genderTextView.setText(signInPatientModeDTO.getMetadata().getDataModels().getPersonalInfo().getProperties().getGender().getLabel());

        TextView genderButton = (TextView) findViewById(R.id.selectGenderButton);
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
                isEmptyGender = genderValue.equalsIgnoreCase(Label.getLabel("choose_gender_label"));
                enableFindMyAppointmentButton();
            }
        });

        findMyAppointmentButton = (CarePayButton)
                findViewById(R.id.findMyAppointmentButton);
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

        List<OptionDTO> options = signInPatientModeDTO.getMetadata().getDataModels().getPersonalInfo().getProperties().getGender().getOptions();
        List<String> genders = new ArrayList<>();
        for (OptionDTO option : options) {
            genders.add(option.getLabel());
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

                isEmptyDate = dateVal.equalsIgnoreCase(Label.getLabel("personal_info_select"));
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
    View.OnClickListener selectGenderButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectGender((TextView) view);
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
    public void selectGender(final TextView genderButton) {
        SystemUtil.showChooseDialog(this,
                gendersArray, Label.getLabel("gender_label"), Label.getLabel("gender_cancel_label"),
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
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            try {
                if (!selectDateButton.getText().toString().equalsIgnoreCase(Label.getLabel("personal_info_select"))) {
                    String[] selectedDate = selectDateButton.getText().toString().split("/");
                    month = Integer.parseInt(selectedDate[0]) - 1;
                    day = Integer.parseInt(selectedDate[1]);
                    year = Integer.parseInt(selectedDate[2]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInformationActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);

                            DateUtil.getInstance().setDate(calendar);
                            selectDateButton.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
                        }
                    }, year, month, day);

            datePickerDialog.setTitle(Label.getLabel("personal_info_select"));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
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

    private void callGetFindMyAppointments() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("first_name", firstNameEditText.getText().toString());
        queryMap.put("last_name", lastNameEditText.getText().toString());
        queryMap.put("date_of_birth", selectDateButton.getText().toString());
        queryMap.put("phone", phoneNumberEditText.getText().toString());
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
            getApplicationMode().setPatientId(signInDTO.getPayload().getPatientModePersonalInfoCheck().getMetadata().getPatientId());
            if (signInDTO.getPayload().getPatientModePersonalInfoCheck().getPersonalInfoCheckSuccessful()) {
                queryMap.put("language", getApplicationPreferences().getUserLanguage());
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                queryMap.put("patient_id", getApplicationMode().getPatientId());
                Map<String, String> headers = new HashMap<>();

                getAppAuthorizationHelper().setUser(signInDTO.getPayload().getPatientModePersonalInfoCheck().getMetadata().getUsername());

                transitionDTO = signInPatientModeDTO.getMetadata().getTransitions().getAction();
                getWorkflowServiceHelper().execute(transitionDTO, patientModeAppointmentsCallback, queryMap, headers);
            } else {
                String errorMessage = Label.getLabel("sign_in_failed") + ", " + Label.getLabel("personal_info_incorrect_details");
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
}