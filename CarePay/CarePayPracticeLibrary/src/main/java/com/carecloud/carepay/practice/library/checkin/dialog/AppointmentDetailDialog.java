package com.carecloud.carepay.practice.library.checkin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;

/**
 * Created by sudhir_pingale on 10/26/2016.
 */

public class AppointmentDetailDialog extends Dialog {

    private Context context;
    private CustomProxyNovaSemiBoldLabel checkingInLabel;
    private CustomProxyNovaSemiBoldLabel patientNameLabel;
    private CustomProxyNovaSemiBoldLabel doctorNameLabel;
    private CustomProxyNovaSemiBoldLabel balanceTextLabel;
    private CustomProxyNovaSemiBoldLabel balanceValueLabel;
    private CheckBox demographicsCheckbox;
    private CheckBox consentFormsCheckbox;
    private CheckBox intakeCheckbox;
    private CheckBox responsibilityCheckbox;
    private CustomGothamRoundedMediumButton paymentButton;
    private CustomGothamRoundedMediumButton assistButton;
    private CustomGothamRoundedMediumButton pageButton;
    private ImageView patientImageView;

    /**
     * Constructor.
     *
     * @param context context
     */
    public AppointmentDetailDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_checkin_detail);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(true);
        onInitialization();
        onSettingStyle();
        onSetValuesFromDTO();
    }

    /**
     * for initialization UI components  .
     */
    private void onInitialization() {
        checkingInLabel = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.checkingInLabel);
        patientNameLabel = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.patientNameLabel);
        doctorNameLabel = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.doctorNameLabel);
        balanceTextLabel = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.balanceTextLabel);
        balanceValueLabel = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.balanceValueLabel);
        demographicsCheckbox = (CheckBox) findViewById(R.id.demographicsCheckbox);
        consentFormsCheckbox = (CheckBox) findViewById(R.id.consentFormsCheckbox);
        intakeCheckbox = (CheckBox) findViewById(R.id.intakeCheckbox);
        responsibilityCheckbox = (CheckBox) findViewById(R.id.responsibilityCheckbox);
        paymentButton = (CustomGothamRoundedMediumButton) findViewById(R.id.paymentButton);
        assistButton = (CustomGothamRoundedMediumButton) findViewById(R.id.assistButton);
        pageButton = (CustomGothamRoundedMediumButton) findViewById(R.id.pageButton);
        patientImageView = (ImageView) findViewById(R.id.patientImageView);

        paymentButton.setOnClickListener(paymentActionListener);
        assistButton.setOnClickListener(assistActionListener);
        pageButton.setOnClickListener(pageActionListener);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal_78));
        patientNameLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        doctorNameLabel.setTextColor(ContextCompat.getColor(context, R.color.taupe_gray_78));
        balanceTextLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        balanceValueLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));

        GradientDrawable bgShapePaymentButton = (GradientDrawable) paymentButton.getBackground();
        bgShapePaymentButton.setColor(ContextCompat.getColor(context, R.color.yellowGreen));

        GradientDrawable bgShapeAssistButton = (GradientDrawable) assistButton.getBackground();
        bgShapeAssistButton.setColor(ContextCompat.getColor(context, R.color.bright_cerulean));

        GradientDrawable bgShapePageButton = (GradientDrawable) pageButton.getBackground();
        bgShapePageButton.setColor(ContextCompat.getColor(context, R.color.rose_madder));
    }

    /**
     * for setting values to UI Component from DTO .
     */
    private void onSetValuesFromDTO(){
        // No DTO available hence setting hardcoded values
        checkingInLabel.setText(R.string.not_defined);
        balanceTextLabel.setText(R.string.not_defined);
        demographicsCheckbox.setText(R.string.not_defined);
        consentFormsCheckbox.setText(R.string.not_defined);
        intakeCheckbox.setText(R.string.not_defined);
        responsibilityCheckbox.setText(R.string.not_defined);
        paymentButton.setText(R.string.not_defined);
        assistButton.setText(R.string.not_defined);
        pageButton.setText(R.string.not_defined);

    }

    private View.OnClickListener paymentActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener assistActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener pageActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}