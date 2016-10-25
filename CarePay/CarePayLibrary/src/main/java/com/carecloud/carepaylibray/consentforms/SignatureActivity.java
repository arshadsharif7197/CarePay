package com.carecloud.carepaylibray.consentforms;

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
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormMetadataDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setTypefaceFromAssets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignatureActivity extends AppCompatActivity {

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
    private Map<Integer, List<String>> stringMap = new HashMap<>();
    public static boolean isBackButtonClicked = false;


    private String patientSignature;
    private String legalSignature;
    private String legalFirstNameLabel;
    private String legalLastNameLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        Intent intent = getIntent();

        intent.getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        title.setText("Signature");
        setTypefaceFromAssets(this, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackButtonClicked = true;
                Intent mIntent = getIntent();
                setResult(CarePayConstants.SIGNATURE_REQ_CODE, mIntent);
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
        List<String> dataList = new ArrayList<>();
        dataList.add("Sign HIPAA Confidentiality Agreement");
        dataList.add("PATIENT SIGNATURE");

        List<String> dataList2 = new ArrayList<>();
        dataList2.add("Sign Consent for Medical Care");
        dataList2.add("Legal Representative Signature");

        stringMap.put(0, dataList);
        stringMap.put(1, dataList2);

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

        agreeButton.setText(getIntent().getExtras().getString("confirmsign"));
        switchButton.setText(getIntent().getExtras().getString("unabletosign"));

        legalFirstNameLabel = getIntent().getExtras().getString("legalFirstName");
        legalFirstNameET.setHint(legalFirstNameLabel);
        legalLastNameLabel = getIntent().getExtras().getString("legalLastName");
        legalLastNameET.setHint(legalLastNameLabel);
        beforesignWarningTextView.setText(getIntent().getExtras().getString("beforesignwarnig"));
        clearButton.setText(getIntent().getExtras().getString("signclearbutton"));
        agreeButton.setText(getIntent().getExtras().getString("confirmsign"));
        legalSignature = getIntent().getExtras().getString("legalsign");
        patientSignature = getIntent().getExtras().getString("patientsign");
        signatureHelpTextView.setText(patientSignature);

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

        legalFirstName.setTag(legalFirstNameLabel);
        legalFirstNameET.setTag(legalFirstName);

        legalLastName.setTag(legalLastNameLabel);
        legalLastNameET.setTag(legalLastName);

        setChangeFocusListeners();

    }

    private void setChangeFocusListeners() {

        legalFirstNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(SignatureActivity.this);
                }
                SystemUtil.handleHintChange(v, hasFocus);

            }
        });


        legalLastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(SignatureActivity.this);
                }
                SystemUtil.handleHintChange(v, hasFocus);

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
            signatureHelpTextView.setText(patientSignature);
            legalFirstName.setVisibility(View.GONE);
            legalLastName.setVisibility(View.GONE);
            legalFirstNameET.setVisibility(View.GONE);
            legalLastNameET.setVisibility(View.GONE);
        } else {
            signatureHelpTextView.setText(legalSignature);
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