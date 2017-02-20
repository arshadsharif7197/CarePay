package com.carecloud.carepay.patient.consentforms;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormAuthorizationPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa.ConsentFormHippaPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare.ConsentFormMedicarePayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConseFormsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setTypefaceFromAssets;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;




public class SignatureActivity extends BaseActivity {

    public static boolean isBackButtonClicked = false;
    public static int numOfLaunches = 0;
    static SignatureActivity signatureActivity;
    private static ConseFormsPayloadDTO payloadDTO;
    private TextView titleTextView;
    private TextView beforesignWarningTextView;
    private TextView signatureHelpTextView;
    private SignaturePad signaturePad;
    private SwitchCompat switchButton;
    private Button agreeButton;
    private Button clearButton;
    private EditText legalFirstNameET;
    private EditText legalLastNameET;
    private TextInputLayout legalFirstName;
    private TextInputLayout legalLastName;

    private ConsentFormLabelsDTO consentFormLabelsDTO;


    private Map<Integer, List<String>> stringMap = new HashMap<>();

    private String signatureAsBase64;
    private boolean isLegalFirstNameEmpty;
    private boolean isLegalLastNameEmpty;
    private boolean isSignatureEmpty;
    private boolean signedByPatient = true;
    private boolean signedByLegal = false;


    private ConseFormsPayloadDTO conseFormsPayloadDTO;
    private ConsentFormPayloadDTO consentFormPayloadDTO;
    private ConsentFormDTO consentFormDTO;
    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            //ConsentActivity.this.finish();
            PatientNavigationHelper.getInstance(SignatureActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(SignatureActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);


        Intent intent = getIntent();
        consentFormLabelsDTO = (ConsentFormLabelsDTO) getIntent().getExtras().get("consentFormLabelsDTO");
        intent.getExtras();
        if (intent.hasExtra("consentform_model")) {
            String consentFormDTOString = intent.getStringExtra("consentform_model");
            Gson gson = new Gson();
            consentFormDTO = gson.fromJson(consentFormDTOString, ConsentFormDTO.class);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        title.setText(consentFormLabelsDTO.getSignatureActivityTitleText());
        setTypefaceFromAssets(this, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle(consentFormLabelsDTO.getSignatureActivityTitleText());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackButtonClicked = true;
                Intent intentsign = getIntent();
                setResult(CarePayConstants.SIGNATURE_REQ_CODE, intentsign);
                finish();
            }
        });
        initViews();
        setTypefaces();
        setEditTexts();
        onClickListeners();
        signatureActivity = this;

        isLegalFirstNameEmpty = true;
        isLegalLastNameEmpty = true;
        isSignatureEmpty = true;

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                if(switchButton.isChecked()) {
                    clearButton.setVisibility(View.VISIBLE);
                    signatureAsBase64 = SystemUtil.encodeToBase64(signaturePad.getSignatureBitmap(), Bitmap.CompressFormat.JPEG, 90);

                    isSignatureEmpty = false;
                    enableAgreeButton();
                } else {
                    clearButton.setVisibility(View.VISIBLE);
                    signatureAsBase64 = SystemUtil.encodeToBase64(signaturePad.getSignatureBitmap(), Bitmap.CompressFormat.JPEG, 90);
                    agreeButton.setEnabled(true);
                }
            }

            @Override
            public void onClear() {
                clearButton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Initializing the view
     */
    private void initViews() {
        //initViews data

        titleTextView = (TextView) findViewById(R.id.titleTv);
        signatureHelpTextView = (TextView) findViewById(R.id.helperTv);
        switchButton = (SwitchCompat) findViewById(R.id.switchButton);
        agreeButton = (Button) findViewById(R.id.agreeBtn);
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        signaturePad.setMinWidth(1);
        clearButton = (Button) findViewById(R.id.clearBtn);
        legalFirstName = (TextInputLayout) findViewById(R.id.legalFirstName);
        legalLastName = (TextInputLayout) findViewById(R.id.legalLastName);
        legalFirstNameET = (EditText) findViewById(R.id.legalFirstNameET);
        legalLastNameET = (EditText) findViewById(R.id.legalLastNameET);
        beforesignWarningTextView = (TextView) findViewById(R.id.beforesignwarnigTextView);
        String headerTitle = getIntent().getExtras().getString("Header_Title");
        String subtitle = getIntent().getExtras().getString("Subtitle");
        beforesignWarningTextView.setText(subtitle);
        titleTextView.setText(headerTitle);
        initviewfromModel();
        switchButton.setChecked(false);
        setTextWatchers();
    }

    private void initviewfromModel() {

        agreeButton.setText(consentFormLabelsDTO.getConfirmSignatureButton());
        switchButton.setText(consentFormLabelsDTO.getUnableToSignText());

        legalFirstNameET.setHint(consentFormLabelsDTO.getLegalFirstNameLabel());
        legalLastNameET.setHint(consentFormLabelsDTO.getLegalLastNameLabel());

        clearButton.setText(consentFormLabelsDTO.getSignClearButton());
        agreeButton.setText(consentFormLabelsDTO.getConfirmSignatureButton());
        signatureHelpTextView.setText(consentFormLabelsDTO.getPatientSignatureHeading());

    }

    private void setTextWatchers() {
        legalFirstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLegalFirstNameEmpty = StringUtil.isNullOrEmpty(legalFirstNameET.getText().toString());
                if (!isLegalFirstNameEmpty) {
                    legalFirstName.setError(null);
                    legalFirstName.setErrorEnabled(false);
                }
                // enableNextButton();
            }
        });

        legalLastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLegalLastNameEmpty = StringUtil.isNullOrEmpty(legalLastNameET.getText().toString());
                if (!isLegalLastNameEmpty) {
                    legalLastName.setError(null);
                    legalLastName.setErrorEnabled(false);
                }
                // enableNextButton();
            }
        });

    }

    /**
     * On click Listeners
     */
    private void onClickListeners() {

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clearSignature();
                if (isChecked) {
                    showData(true);
                    signedByLegal = true;
                    signedByPatient = false;
                } else { // turn off
                    showData(false);
                    signedByLegal = false;
                    signedByPatient = true;

                    legalFirstNameET.setText("");
                    legalFirstNameET.clearFocus();
                    legalLastNameET.setText("");
                    legalLastNameET.clearFocus();
                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSignature();
            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numOfLaunches == 2) {
                    addToPayload();
                    navigateToNext();
                    numOfLaunches = 0;
                } else {
                    Intent intent = getIntent();
                    if (intent.hasExtra("consentform")) {
                        numOfLaunches++;
                        finish();
                    }
                }
            }
        });
    }

    private void setEditTexts() {

        legalFirstName.setTag(consentFormLabelsDTO.getLegalFirstNameLabel());
        legalFirstNameET.setTag(legalFirstName);

        legalLastName.setTag(consentFormLabelsDTO.getLegalLastNameLabel());
        legalLastNameET.setTag(legalLastName);

        setChangeFocusListeners();

        setOnChangeTextListeners();

    }

    private void setOnChangeTextListeners() {
        legalFirstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence seq, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence seq, int start, int before, int count) {
                isLegalFirstNameEmpty = seq.length() == 0;
                enableAgreeButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        legalLastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence seq, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence seq, int start, int before, int count) {
                isLegalLastNameEmpty = seq.length() == 0;
                enableAgreeButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setChangeFocusListeners() {

        legalFirstNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View changeListener, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(SignatureActivity.this);
                }
                SystemUtil.handleHintChange(changeListener, hasFocus);
            }
        });

        legalLastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View changeListener, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(SignatureActivity.this);
                }
                SystemUtil.handleHintChange(changeListener, hasFocus);
            }
        });
    }


    /**
     * Text change Listeners
     */
    private void clearSignature() {
        if (signaturePad != null) {
            signaturePad.clear();
            isSignatureEmpty = true;
            enableAgreeButton();
        }
    }

    private void enableAgreeButton() {
        agreeButton.setEnabled(!isLegalFirstNameEmpty && !isLegalLastNameEmpty && !isSignatureEmpty);
    }

    private void showData(boolean isChecked) {
        if (!isChecked) {
            signatureHelpTextView.setText(consentFormLabelsDTO.getPatientSignatureHeading());
            legalFirstName.setVisibility(View.GONE);
            legalLastName.setVisibility(View.GONE);
            legalFirstNameET.setVisibility(View.GONE);
            legalLastNameET.setVisibility(View.GONE);
        } else {
            String leagalrepresentative = consentFormLabelsDTO.getLegalSignatureLabel();
            int indexFirstPercent = leagalrepresentative.indexOf(' ');
            String upToFirstspaceSubstring = leagalrepresentative.substring(0, indexFirstPercent);
            String fromSecSpaceOnSubstring = leagalrepresentative.substring(indexFirstPercent + 1, indexFirstPercent + 15);
            String uptothirdSpace = leagalrepresentative.substring(leagalrepresentative.indexOf(' ', indexFirstPercent + 2), leagalrepresentative.length());
            String signature = String.format(Locale.getDefault(), "%s %s%s%s", upToFirstspaceSubstring, fromSecSpaceOnSubstring, "", uptothirdSpace);            signatureHelpTextView.setText(signature);
            legalFirstName.setVisibility(View.VISIBLE);
            legalLastName.setVisibility(View.VISIBLE);
            legalFirstNameET.setVisibility(View.VISIBLE);
            legalLastNameET.setVisibility(View.VISIBLE);
        }
    }

    private void setTypefaces() {
        setGothamRoundedMediumTypeface(this, titleTextView);
        setProximaNovaRegularTypeface(this, beforesignWarningTextView);
        setProximaNovaRegularTypeface(this, legalFirstNameET);
        setProximaNovaRegularTypeface(this, legalLastNameET);
        setProximaNovaRegularTypeface(this, switchButton);
        setProximaNovaSemiboldTypeface(this, signatureHelpTextView);
        setGothamRoundedMediumTypeface(this, agreeButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackButtonClicked = true;
        Intent intent = getIntent();
        setResult(CarePayConstants.SIGNATURE_REQ_CODE, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void navigateToNext() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeMgmt());
        queries.put("practice_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeId());
        queries.put("appointment_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());


        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        Gson gson = new Gson();
        String body = gson.toJson(consentFormPayloadDTO);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        getWorkflowServiceHelper().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
    }

    private void addToPayload() {
        conseFormsPayloadDTO = new ConseFormsPayloadDTO();
        consentFormPayloadDTO = new ConsentFormPayloadDTO();

        switch (numOfLaunches) {
            case 1:
                ConsentFormMedicarePayloadDTO consentFormMedicarePayloadDTO = new ConsentFormMedicarePayloadDTO();
                consentFormMedicarePayloadDTO.setSignature(signatureAsBase64);
                consentFormMedicarePayloadDTO.setSignedByLegal(signedByLegal);
                consentFormMedicarePayloadDTO.setSignedByPatient(signedByPatient);
                conseFormsPayloadDTO.setConsentFormMedicarePayload(consentFormMedicarePayloadDTO);
                consentFormPayloadDTO.setConsentforms(conseFormsPayloadDTO);
                break;

            case 2:
                ConsentFormAuthorizationPayloadDTO consentFormAuthorizationPayloadDTO = new ConsentFormAuthorizationPayloadDTO();
                consentFormAuthorizationPayloadDTO.setSignature(signatureAsBase64);
                consentFormAuthorizationPayloadDTO.setSignedByLegal(signedByLegal);
                consentFormAuthorizationPayloadDTO.setSignedByPatient(signedByPatient);
                conseFormsPayloadDTO.setConsentFormAuthorizationPayloadDTO(consentFormAuthorizationPayloadDTO);
                consentFormPayloadDTO.setConsentforms(conseFormsPayloadDTO);
                break;

            case 3:
                ConsentFormHippaPayloadDTO consentFormHippaPayloadDTO = new ConsentFormHippaPayloadDTO();
                consentFormHippaPayloadDTO.setSignature(signatureAsBase64);
                consentFormHippaPayloadDTO.setSignedByLegal(signedByLegal);
                consentFormHippaPayloadDTO.setSignedByPatient(signedByPatient);
                conseFormsPayloadDTO.setConsentFormHippaPayload(consentFormHippaPayloadDTO);
                consentFormPayloadDTO.setConsentforms(conseFormsPayloadDTO);
                break;

            default:
                break;
        }
    }
}