package com.carecloud.carepay.practice.library.dobverification;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextInputLayout;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBVerificationActivity extends BasePracticeActivity {

    private static final int DOB_LENGTH = 10;
    private static final int MAX_INTENTS = 3;
    private AppointmentsResultModel appointmentsResultModel;
    private EditText dobEditText;
    private int retries = 0;
    private CarePayTextInputLayout dobTextInputLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dob_verification);
        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);

        getAppAuthorizationHelper().setUser(appointmentsResultModel.getPayload().getDemographicDTO().getMetadata().getUsername());

        setUpUi();
    }

    private void setUpUi() {
        final Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(view -> onVerifyButtonClick());
        dobTextInputLayout = findViewById(R.id.dobTextInputLayout);
        dobEditText = findViewById(R.id.dobEditText);
        dobEditText.addTextChangedListener(new TextWatcher() {
            int lastLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringUtil.autoFormatDateOfBirth(editable, lastLength);
                if (validateDoBValue(editable.toString())) {
                    verifyButton.setEnabled(true);
                } else {
                    verifyButton.setEnabled(false);
                }
            }
        });

        dobEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (view.length() == DOB_LENGTH) {
                onVerifyButtonClick();
                return true;
            }
            return false;
        });

        dobEditText.setOnClickListener(selectEndOnClick);

        findViewById(R.id.lockImageView).setOnClickListener(view -> showPasswordFragment());

        TextView subTitleTextView = findViewById(R.id.subTitleTextView);
        subTitleTextView.setText(String.format(Label.getLabel("dobVerification.main.subTitle.label.message"),
                getPatientName()));

        populateLanguageSpinner();
        if (retries == 3) {
            showGameOverScreen();
        }
        dobEditText.postDelayed(() -> {
            SystemUtil.hideSoftKeyboard(DoBVerificationActivity.this);
            dobEditText.setFocusable(true);
            dobEditText.setFocusableInTouchMode(true);
        }, 100);
    }

    private boolean validateDoBValue(String dateString) {
        if (!StringUtil.isNullOrEmpty(dateString)) {
            String dateValidationResult = DateUtil.getDateOfBirthValidationResultMessage(dateString);
            if (dateValidationResult != null) {
                dobTextInputLayout.setErrorEnabled(true);
                dobTextInputLayout.setError(dateValidationResult);
                return false;
            } else {
                dobTextInputLayout.setError(null);
                dobTextInputLayout.setErrorEnabled(false);
            }
        } else {
            dobTextInputLayout.setError(null);
            dobTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private String getPatientName() {
        String firstName;
        if (StringUtil.isNullOrEmpty(appointmentsResultModel.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getFirstName())) {
            firstName = "";
        } else {
            firstName = appointmentsResultModel.getPayload().getDemographicDTO().getPayload()
                    .getPersonalDetails().getFirstName().substring(0, 1);
        }
        String lastName = appointmentsResultModel.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getLastName();
        return StringUtil.capitalize(String.format("%s. %s", firstName, lastName));
    }

    private void onVerifyButtonClick() {
        if (validateDoB(dobEditText.getText().toString())) {
            finish();
        } else {
            retries++;
            if (retries == MAX_INTENTS) {
                showGameOverScreen();
            } else {
                showErrorDialog();
            }
        }
        SystemUtil.hideSoftKeyboard(this);
    }

    private void showPasswordFragment() {
        ConfirmationPinDialog confirmationPinDialog = ConfirmationPinDialog.newInstance(
                appointmentsResultModel.getMetadata().getLinks().getPinpad(),
                false,
                appointmentsResultModel.getMetadata().getLinks().getLanguage());
        displayDialogFragment(confirmationPinDialog, false);
    }

    private void showErrorDialog() {
        String message = Label.getLabel("dobVerification.gameOver.dialog.title.message");
        LargeAlertDialogFragment fragment = LargeAlertDialogFragment
                .newInstance(message, Label.getLabel("ok"), 36);
        fragment.show(getSupportFragmentManager(), ConfirmDialogFragment.class.getName());
    }

    private boolean validateDoB(String dobInput) {
        String dob;
        boolean isGuest = !ValidationHelper.isValidEmail(getAppAuthorizationHelper().getCurrUser());
        if (isGuest) {
            dob = DateUtil.getInstance().setDateRaw(appointmentsResultModel.getPayload().getAppointments().get(0)
                    .getPayload().getPatient().getDateOfBirth()).toStringWithFormatMmSlashDdSlashYyyy();
        } else {
            dob = appointmentsResultModel.getPayload().getDemographicDTO().getPayload().getPersonalDetails()
                    .getFormattedDateOfBirth();
        }
        return dobInput.equals(dob);
    }

    private void showGameOverScreen() {
        findViewById(R.id.gameOverScreen).setVisibility(View.VISIBLE);
        findViewById(R.id.mainScreen).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        //Disable back
    }

    private void populateLanguageSpinner() {
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());

        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), false,
                appointmentsResultModel.getPayload().getLanguages(), language -> changeLanguage(appointmentsResultModel.getMetadata().getLinks().getLanguage(),
                language.getCode().toLowerCase(), headers));
        TextView languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinner.setOnClickListener(popupPickerLanguage::showAsDropDown);
        languageSpinner.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin, TransitionDTO transitionDTO) {
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, practiceModeCallback, query);
    }

    WorkflowServiceCallback practiceModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        retries = savedInstanceState.getInt("retries");
        if (retries == MAX_INTENTS) {
            showGameOverScreen();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("retries", retries);
    }

    protected View.OnClickListener selectEndOnClick = view -> {
        EditText editText = (EditText) view;
        editText.setSelection(editText.length());
    };
}
