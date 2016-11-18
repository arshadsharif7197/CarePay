package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class SignatureDialog extends Dialog {

    private Context context;
    private SignaturePad signaturePad;
    private ConsentFormDTO consentFormDTO;

    public SignatureDialog(Context context, ConsentFormDTO consentFormDTO) {
        super(context);
        this.context = context;
        this.consentFormDTO = consentFormDTO;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_consent_signing);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.50);
        getWindow().setAttributes(params);

        setLabels();

        final Button confirmSign = (Button) findViewById(R.id.signature_confirmation_button);
        confirmSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        confirmSign.setEnabled(false);

        final Button clearButton = (Button) findViewById(R.id.patient_mode_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
                confirmSign.setEnabled(false);
                clearButton.setVisibility(View.GONE);
            }
        });

        signaturePad = (SignaturePad) findViewById(R.id.patient_mode_signature_pad);
        signaturePad.setMinWidth(1);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                confirmSign.setEnabled(true);
                clearButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClear() {
            }
        });

        findViewById(R.id.signature_view_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setLabels() {
        if (consentFormDTO != null) {
            ConsentFormLabelsDTO labels = consentFormDTO.getMetadata().getLabel();
            ((CarePayTextView) findViewById(R.id.sign_consent_close_label)).setText(labels.getSignConsentCloseLabel());
            ((CarePayTextView) findViewById(R.id.sign_view_title)).setText(labels.getSignConsentForMedicareTitle());
            ((CarePayTextView) findViewById(R.id.sign_consent_warning_text)).setText(labels.getBeforeSignatureWarningText());
            ((CarePayTextView) findViewById(R.id.patient_mode_helper_view)).setText(labels.getPatientSignatureHeading());
            ((SwitchCompat) findViewById(R.id.switch_button)).setText(labels.getUnableToSignText());
            ((Button) findViewById(R.id.patient_mode_clear_button)).setText(labels.getSignClearButton());
            ((Button) findViewById(R.id.signature_confirmation_button)).setText(labels.getConfirmSignatureButton());
        }
    }
}
