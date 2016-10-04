package com.carecloud.carepaylibray.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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

    private TextView titleTv, signatureHelpTv;
    private SignaturePad signaturePad;
    private SwitchCompat switchButton;
    private Button agreeBtn, clearBtn;
    private EditText legalFirstNameET, legalLastNameET;
    private TextInputLayout legalFirstName,legalLastName;
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
        setTypefaces();
        onClickListeners();
        setTextListeners();
    }

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

        titleTv = (TextView) findViewById(R.id.titleTv);
        signatureHelpTv = (TextView) findViewById(R.id.helperTv);
        switchButton = (SwitchCompat) findViewById(R.id.switchButton);
        agreeBtn = (Button) findViewById(R.id.agreeBtn);
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        legalFirstName=(TextInputLayout)findViewById(R.id.legalFirstName);
        legalLastName=(TextInputLayout)findViewById(R.id.legalLastName);
        legalFirstNameET = (EditText) findViewById(R.id.legalFirstNameET);
        legalLastNameET = (EditText) findViewById(R.id.legalLastNameET);
        String headerTitle = getIntent().getExtras().getString("Header_Title");
        titleTv.setText(headerTitle);


    }

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

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSignature();
            }
        });

        agreeBtn.setOnClickListener(new View.OnClickListener() {
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
                agreeBtn.setEnabled(true);
            }

            @Override
            public void onClear() {
            }
        });


    }
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
            agreeBtn.setEnabled(false);
        }
    }

    private void showData(boolean isChecked) {
        if (!isChecked) {
            titleTv.setText(stringMap.get(0).get(0));
            signatureHelpTv.setText(stringMap.get(0).get(1));
            legalFirstNameET.setVisibility(View.GONE);
            legalLastNameET.setVisibility(View.GONE);
        } else {
            titleTv.setText(stringMap.get(1).get(0));
            signatureHelpTv.setText(stringMap.get(1).get(1));
            legalFirstNameET.setVisibility(View.VISIBLE);
            legalLastNameET.setVisibility(View.VISIBLE);
        }
    }

    private void setTypefaces() {
        setGothamRoundedMediumTypeface(this, titleTv);
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

}