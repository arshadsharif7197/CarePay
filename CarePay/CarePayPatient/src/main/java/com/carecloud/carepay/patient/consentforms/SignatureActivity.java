package com.carecloud.carepay.patient.consentforms;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormAuthorizationPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa.ConsentFormHippaPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare.ConsentFormMedicarePayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConseFormsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
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




public class SignatureActivity extends AppCompatActivity {

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
    private String patientSignature;
    private String legalSignature;
    private String legalFirstNameLabel;
    private String legalLastNameLabel;
    private String signatureAsBase64;
    private boolean signedByPatient = true;
    private boolean signedByLegal = false;

    private ConseFormsPayloadDTO conseFormsPayloadDTO;
    private ConsentFormPayloadDTO consentFormPayloadDTO;
    private ConsentFormDTO consentFormDTO;
    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            //ConsentActivity.this.finish();
            PatientNavigationHelper.getInstance(SignatureActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

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
        toolbar.setTitle("");
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
        String subtitle=getIntent().getExtras().getString("Subtitle");
        beforesignWarningTextView.setText(subtitle);
        titleTextView.setText(headerTitle);
        initviewfromModel();

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

    /**
     * On click Listeners
     */
    private void onClickListeners() {
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSignature();
                if (switchButton.isChecked()) {
                    showData(true);
                    signedByLegal = true;
                    signedByPatient = false;
                } else {
                    showData(false);
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
                       /* Intent data = new Intent();
                        data.putExtra("signature_base64", signatureAsBase64);
                        data.putExtra("patient_signed", signedByPatient);
                        data.putExtra("legal_signed", signedByLegal);
                        setResult(9999, data);*/
                        numOfLaunches++;
                        finish();
                    }
                }
            }
        });

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

                signatureAsBase64 = SystemUtil.encodeToBase64(signaturePad.getSignatureBitmap(), Bitmap.CompressFormat.JPEG, 90);
                agreeButton.setEnabled(true);
            }

            @Override
            public void onClear() {
            }
        });


    }


    private void setEditTexts() {

        legalFirstName.setTag(consentFormLabelsDTO.getLegalFirstNameLabel());
        legalFirstNameET.setTag(legalFirstName);

        legalLastName.setTag(consentFormLabelsDTO.getLegalLastNameLabel());
        legalLastNameET.setTag(legalLastName);

        setChangeFocusListeners();

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
            agreeButton.setEnabled(false);
        }
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
            String signature = String.format(Locale.getDefault(), "%s %s %s%s", upToFirstspaceSubstring, fromSecSpaceOnSubstring, "\n", uptothirdSpace);
            signatureHelpTextView.setText(signature);
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


        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        String body = gson.toJson(consentFormPayloadDTO);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
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