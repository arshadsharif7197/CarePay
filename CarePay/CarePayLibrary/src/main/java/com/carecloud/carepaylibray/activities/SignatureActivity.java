package com.carecloud.carepaylibray.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;

public class SignatureActivity extends AppCompatActivity {

    private TextView titleTv, signatureHelpTv;
    private SignaturePad signaturePad;
    private SwitchCompat switchButton;
    private Button agreeBtn, clearBtn;
    private EditText legalFirstNameET, legalLastNameET;
    private Map<Integer, List<String>> stringMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        title.setText("Signature");
        setTypefaceFromAssets(this, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);
    }

    private void init() {
        //init data
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
        legalFirstNameET = (EditText) findViewById(R.id.legalFirstName);
        legalLastNameET = (EditText) findViewById(R.id.legalLastName);

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
                if(getIntent().hasExtra("consentform"))
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
                agreeBtn.setBackgroundColor(getResources().getColor(R.color.blue_cerulian));
            }

            @Override
            public void onClear() {
            }
        });
    }

    private void clearSignature(){
        if(signaturePad != null){
            signaturePad.clear();
            agreeBtn.setEnabled(false);
            agreeBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));
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
}