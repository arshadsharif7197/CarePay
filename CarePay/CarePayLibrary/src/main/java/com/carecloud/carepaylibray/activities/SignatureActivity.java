package com.carecloud.carepaylibray.activities;

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
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setTypefaceFromAssets;

public class SignatureActivity extends AppCompatActivity {

    private TextView titleTextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        initViews();

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
                setResult(CarePayConstants.SIGNATURE_REQ_CODE,mIntent);
                finish();
            }});
        setTypefaces();
        onClickListeners();
        setTextListeners();
    }

    /** Initializing the view
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
        clearButton = (Button) findViewById(R.id.clearBtn);
        legalFirstName=(TextInputLayout)findViewById(R.id.legalFirstName);
        legalLastName=(TextInputLayout)findViewById(R.id.legalLastName);
        legalFirstNameET = (EditText) findViewById(R.id.legalFirstNameET);
        legalLastNameET = (EditText) findViewById(R.id.legalLastNameET);
        String headerTitle = getIntent().getExtras().getString("Header_Title");
        titleTextView.setText(headerTitle);


    }

    /** On click Listeners
     */
    private void onClickListeners() {
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSignature();
                if (switchButton.isChecked()) {
                    showData(true);
                } else
                    showData(false);
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
                if (getIntent().hasExtra("consentform"))
                    finish();
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

    /** Text change Listeners
     *
     */
    private void setTextListeners() {
      legalFirstNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String hint = "Legal Representative First Name";
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    legalFirstName.setHint(hintCaps);
                    setProximaNovaSemiboldTextInputLayout(getApplicationContext(),legalFirstName);
                } else {
                    if (StringUtil.isNullOrEmpty(legalFirstNameET.getText().toString())) {
                        // change hint to lower
                        legalFirstName.setHint(hint);

                    } else {
                       legalFirstNameET.setHint(hint);
                    }
                }
            }

        });
        legalLastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String hint = "Legal Representative Last Name";
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    legalLastName.setHint(hintCaps);
                    setProximaNovaSemiboldTextInputLayout(getApplicationContext(),legalLastName);
                } else {
                    if (StringUtil.isNullOrEmpty(legalLastNameET.getText().toString())) {
                        // change hint to lower
                        legalLastName.setHint(hint);

                    } else {
                      legalLastNameET.setHint(hint);
                    }
                }

            }

        });
    }

    private void clearSignature() {
        if (signaturePad != null) {
            signaturePad.clear();
            agreeButton.setEnabled(false);
        }
    }

    private void showData(boolean isChecked) {
        if (!isChecked) {
            titleTextView.setText(stringMap.get(0).get(0));
            signatureHelpTextView.setText(stringMap.get(0).get(1));
            legalFirstNameET.setVisibility(View.GONE);
            legalLastNameET.setVisibility(View.GONE);
        } else {
            titleTextView.setText(stringMap.get(1).get(0));
            signatureHelpTextView.setText(stringMap.get(1).get(1));
            legalFirstNameET.setVisibility(View.VISIBLE);
            legalLastNameET.setVisibility(View.VISIBLE);
        }
    }

    private void setTypefaces() {
        setGothamRoundedMediumTypeface(this, titleTextView);
        setProximaNovaRegularTypeface(this, (TextView) findViewById(R.id.descriptionTv));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackButtonClicked = true;
        Intent mIntent = getIntent();
        setResult(CarePayConstants.SIGNATURE_REQ_CODE,mIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}