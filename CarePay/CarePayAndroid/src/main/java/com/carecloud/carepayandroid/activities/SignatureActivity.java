package com.carecloud.carepayandroid.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.util.Utility;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class SignatureActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //private SignaturePad mSignaturePad;
    //private CheckBox agreementCheckBox;
    private LinearLayout mainLayout;
    private TextView titleTv, descriptionTv, signatureHelpTv;
    private SignaturePad signaturePad;
    private SwitchCompat switchButton;
    private Button agreeBtn;
    private ScreenModel signatureModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Signature");
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(10, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(10, SignatureActivity.this));
        mainLayout.setLayoutParams(params);
        mainLayout.setBackgroundResource(R.color.munsell);

        signatureModel = ApplicationWorkflow.Instance().getSignatureScreenModel();

        if (signatureModel.getComponentModels().get(0).getType().equals("text")) { //title
            mainLayout.addView(getTitleTextView(signatureModel.getComponentModels().get(0).getLabel()));
        }
        if (signatureModel.getComponentModels().get(2).getType().equals("text")) { //description
            mainLayout.addView(getTextView(signatureModel.getComponentModels().get(2).getLabel(), 13));
        }
        if (signatureModel.getComponentModels().get(3).getType().equals("switch")) { //switch
            mainLayout.addView(getSwitch(signatureModel.getComponentModels().get(3).getLabel()));
        }
        if (signatureModel.getComponentModels().get(6).getType().equals("signaturepad")) { //signature pad
            mainLayout.addView(getSignatureView(signatureModel.getComponentModels().get(4).getLabel()));
        }

        if (signatureModel.getComponentModels().get(7).getType().equals("button"))  //button
            mainLayout.addView(getButton(signatureModel.getComponentModels().get(7).getLabel()));


        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchButton.isChecked()) {
                    showData(true);
                } else
                    showData(false);
            }
        });
        setContentView(mainLayout);
    }

    private TextView getTitleTextView(String title) {
        titleTv = new TextView(this);
        titleTv.setText(title);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // params.setMargins(0, Utility.convertDpToPixel(10, SignatureActivity.this), 0, 0);
        titleTv.setPadding(Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(10, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this)
                , 0);
        titleTv.setGravity(Gravity.CENTER);
        titleTv.setTextSize(21);
        titleTv.setTextColor(getResources().getColor(R.color.charcoal));
        titleTv.setLayoutParams(params);
        return titleTv;
    }

    private TextView getTextView(String title, int textSize) {
        descriptionTv = new TextView(this);
        descriptionTv.setText(title);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Utility.convertDpToPixel(10, SignatureActivity.this), 0, 0);
        descriptionTv.setPadding(Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(20, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this)
                , 0);
        descriptionTv.setGravity(Gravity.LEFT);
        descriptionTv.setTextSize(textSize);
        descriptionTv.setTextColor(getResources().getColor(R.color.cadet_gray));
        descriptionTv.setLayoutParams(params);
        return descriptionTv;
    }


    private Button getButton(String title) {
        agreeBtn = new Button(this);
        agreeBtn.setText(title);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(40, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this)
                , 0);
        agreeBtn.setGravity(Gravity.CENTER);
        agreeBtn.setTextSize(18);
        agreeBtn.setLayoutParams(params);
        agreeBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));
        agreeBtn.setTextColor(getResources().getColor(R.color.white));
        agreeBtn.setTextSize(15);

        return agreeBtn;
    }

    private View getSwitch(String title) {
        RelativeLayout layout = new RelativeLayout(this);
        switchButton = new SwitchCompat(this);
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(Utility.convertDpToPixel(30, SignatureActivity.this),
                Utility.convertDpToPixel(30, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this)
                , 0);

        layout.setPadding(0, Utility.convertDpToPixel(20, SignatureActivity.this),0,0);
        switchButton.setPadding(0,0, Utility.convertDpToPixel(30, SignatureActivity.this),0);
        tv.setPadding(Utility.convertDpToPixel(17, SignatureActivity.this),0, Utility.convertDpToPixel(30, SignatureActivity.this),0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        tv.setText(title);
        tv.setTextSize(17);
        tv.setTextColor(getResources().getColor(R.color.charcoal));
        layout.setLayoutParams(layoutParams);
        switchButton.setLayoutParams(params);
        tv.setLayoutParams(tvParams);
        layout.addView(tv);
        layout.addView(switchButton);
        return layout;
    }

    private View getSignatureView(String helperText) {
        View view = LayoutInflater.from(this).inflate(R.layout.signature_view, null);
        signaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        signatureHelpTv = (TextView) view.findViewById(R.id.helperTv);
        signatureHelpTv.setText(helperText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utility.convertDpToPixel(17, SignatureActivity.this),
                Utility.convertDpToPixel(20, SignatureActivity.this),
                Utility.convertDpToPixel(17, SignatureActivity.this)
                , 0);
        //params.gravity = Gravity.CENTER_HORIZONTAL;
        view.setLayoutParams(params);
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
        return view;
    }

    private void showData(boolean isChecked) {
        if (isChecked) {
            titleTv.setText(signatureModel.getComponentModels().get(1).getLabel());
            signatureHelpTv.setText(signatureModel.getComponentModels().get(5).getLabel());
        } else {
            titleTv.setText(signatureModel.getComponentModels().get(0).getLabel());
            signatureHelpTv.setText(signatureModel.getComponentModels().get(4).getLabel());
        }
    }
}