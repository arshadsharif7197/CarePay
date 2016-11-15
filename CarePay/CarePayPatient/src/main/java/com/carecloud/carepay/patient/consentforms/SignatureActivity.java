package com.carecloud.carepay.patient.consentforms;

import android.content.Intent;
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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setTypefaceFromAssets;


public class SignatureActivity extends AppCompatActivity {

    public static boolean isBackButtonClicked = false;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        consentFormLabelsDTO = (ConsentFormLabelsDTO) getIntent().getExtras().get("consentFormLabelsDTO");

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
        titleTextView.setText(headerTitle);
        initviewfromModel();

    }


    private void initviewfromModel() {

        agreeButton.setText(consentFormLabelsDTO.getConfirmSignatureButton());
        switchButton.setText(consentFormLabelsDTO.getUnableToSignText());

        legalFirstNameET.setHint(consentFormLabelsDTO.getLegalFirstNameLabel());
        legalLastNameET.setHint(consentFormLabelsDTO.getLegalLastNameLabel());
        beforesignWarningTextView.setText(consentFormLabelsDTO.getBeforeSignatureWarningText());
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
                if (getIntent().hasExtra("consentform")) {
                    finish();
                }
            }
        });

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

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
            signatureHelpTextView.setText(consentFormLabelsDTO.getLegalSignatureLabel());
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
}