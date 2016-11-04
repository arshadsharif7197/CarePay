package com.carecloud.carepay.practice.library.checkin.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldButton;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Calendar;
import org.joda.time.LocalDate;

public class PersonalInformationActivity extends AppCompatActivity {
    private CustomProxyNovaSemiBoldButton selectDateButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;
    private CustomGothamRoundedMediumButton findMyAppointmentButton;
    private boolean isEmptyFirstName;
    private boolean isEmptyLastName;
    private boolean isEmptyPhoneNumber;
    private boolean isEmptyDate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_personal_information);
        
        /*Initialise views*/
        initViews();
    }

    /**
     * Method to initialise view
     */
    void initViews() {
        CustomGothamRoundedMediumButton goBackButton = (CustomGothamRoundedMediumButton)
                findViewById(R.id.goBackButton);
        goBackButton.setText(getResources().getString(R.string.not_defined));
        goBackButton.setOnClickListener(goBackButtonListener);

        CustomGothamRoundedMediumLabel personalInformationTextView =
                (CustomGothamRoundedMediumLabel) findViewById(R.id.personalInformationTextView);
        personalInformationTextView.setText(getResources().getString(R.string.not_defined));
        personalInformationTextView.setTextColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));

        CustomProxyNovaRegularLabel identifyYourselfTextView = (CustomProxyNovaRegularLabel)
                findViewById(R.id.identifyYourselfTextView);
        identifyYourselfTextView.setText(getResources().getString(R.string.not_defined));
        identifyYourselfTextView.setTextColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));

        TextInputLayout firstNameInputLayout = (TextInputLayout)
                findViewById(R.id.firstNameInputLayout);
        firstNameInputLayout.setTag(getResources().getString(R.string.not_defined));

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        firstNameEditText.setHint(getResources().getString(R.string.not_defined));
        firstNameEditText.setTag(firstNameInputLayout);

        TextInputLayout lastNameInputLayout = (TextInputLayout)
                findViewById(R.id.lastNameInputLayout);
        lastNameInputLayout.setTag(getResources().getString(R.string.not_defined));

        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        lastNameEditText.setHint(getResources().getString(R.string.not_defined));
        lastNameEditText.setTag(lastNameInputLayout);

        TextInputLayout phoneNumberInputLayout = (TextInputLayout)
                findViewById(R.id.phoneNumberInputLayout);
        phoneNumberInputLayout.setTag(getResources().getString(R.string.not_defined));

        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setHint(getResources().getString(R.string.not_defined));
        phoneNumberEditText.setTag(phoneNumberInputLayout);

        setChangeFocusListeners();

        CustomProxyNovaRegularLabel dateOfBirthTextView = (CustomProxyNovaRegularLabel)
                findViewById(R.id.dateOfBirthTextView);
        dateOfBirthTextView.setText(getResources().getString(R.string.not_defined));

        selectDateButton = (CustomProxyNovaSemiBoldButton) findViewById(R.id.selectDateButton);
        selectDateButton.setText(getResources().getString(R.string.not_defined));
        selectDateButton.setOnClickListener(selectDateButtonListener);

        findMyAppointmentButton = (CustomGothamRoundedMediumButton)
                findViewById(R.id.findMyAppointmentButton);
        findMyAppointmentButton.setText(getResources().getString(R.string.not_defined));
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

                if (dateVal.equalsIgnoreCase(getResources().getString(R.string.not_defined))) {
                    isEmptyDate = true;
                } else {
                    isEmptyDate = false;
                }
                enableFindMyAppointmentButton();
            }
        });
    }

    /**
     * Method to enable or disable Find My Appointment button
     */
    private void enableFindMyAppointmentButton() {
        boolean areAllNonEmpty = !(isEmptyFirstName || isEmptyLastName || isEmptyPhoneNumber || isEmptyDate);

        findMyAppointmentButton.setEnabled(areAllNonEmpty);
        if (!findMyAppointmentButton.isEnabled()) {
            findMyAppointmentButton.setAlpha(0.3F);
        } else {
            findMyAppointmentButton.setAlpha(1);
        }
    }

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

            DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInformationActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    LocalDate date = new LocalDate(year,month+1,day);
                    DateUtil.getInstance().setDate(date.toDate());
                    selectDateButton.setText(DateUtil.getInstance().getDateAsMMddyyyyWithSlash());
                }
            }, year, month, day);

            datePickerDialog.setTitle(getResources().getString(R.string.not_defined));
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
        }
    };

    /**
     * Click listener for home icon
     */
    View.OnClickListener homeImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PersonalInformationActivity.this, CloverMainActivity.class);
            startActivity(intent);
        }
    };
}