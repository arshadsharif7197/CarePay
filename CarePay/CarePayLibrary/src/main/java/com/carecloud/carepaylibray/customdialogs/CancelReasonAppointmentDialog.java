package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 10/12/2016.
 */

public class CancelReasonAppointmentDialog extends Dialog implements
        View.OnClickListener {
    private Context context;
    private AppointmentModel appointmentModel;
    private RadioGroup cancelReasonRadioGroup;
    private Button cancelAppointmentButton;
    private EditText reasonEditText;
    private TextInputLayout reasonTextInputLayout;
    private AppCompatRadioButton rescheduleAppointmentRadioButtom, officeRescheduleAppointmentRadioButtom,
            forgotAppointmentRadioButtom, noLongerAppointmentRadioButtom, otherAppointmentRadioButtom;

    public CancelReasonAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_reason_appointment);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);
        onInitialization();
        onSetListner();
    }

    private void onInitialization() {
        cancelReasonRadioGroup = (RadioGroup) findViewById(R.id.cancelReasonRadioGroup);
        cancelAppointmentButton = (Button) findViewById(R.id.cancelAppointmentButton);
        reasonEditText = (EditText) findViewById(R.id.reasonEditText);
        reasonTextInputLayout = (TextInputLayout) findViewById(R.id.reasonTextInputLayout);
        rescheduleAppointmentRadioButtom = (AppCompatRadioButton) findViewById(R.id.rescheduleAppointmentRadioButtom);
        officeRescheduleAppointmentRadioButtom = (AppCompatRadioButton) findViewById(R.id.officeRescheduleAppointmentRadioButtom);
        forgotAppointmentRadioButtom = (AppCompatRadioButton) findViewById(R.id.forgotAppointmentRadioButtom);
        noLongerAppointmentRadioButtom = (AppCompatRadioButton) findViewById(R.id.noLongerAppointmentRadioButtom);
        otherAppointmentRadioButtom = (AppCompatRadioButton) findViewById(R.id.otherAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, rescheduleAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, officeRescheduleAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, forgotAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, noLongerAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, otherAppointmentRadioButtom);
        SystemUtil.setProximaNovaRegularTypeface(context, cancelAppointmentButton);

    }

    private void onSetListner() {
        findViewById(R.id.dialogCloseHeaderImageView).setOnClickListener(this);
        cancelReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton checkedRadioButton = (AppCompatRadioButton) group.findViewById(checkedId);
                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                onSetColorStateForRadioButton(checkedRadioButton);
                onSelectionRadioCancel(checkedRadioButton.isChecked(), checkedRadioButtonId);
                if (checkedRadioButton.isChecked()) {

                } else {

                }
            }
        });
        reasonEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        cancelAppointmentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogCloseHeaderImageView) {
            cancel();
        } else if (viewId == R.id.cancelAppointmentButton) {
            new CancelAppointmentDialog(context,appointmentModel,true).show();
            cancel();
        }
    }

    private void onSelectionRadioCancel(boolean isSelected, int id) {
        if (isSelected) {
            cancelAppointmentButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_red_border));
            cancelAppointmentButton.setEnabled(true);
            cancelAppointmentButton.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        }
        if (id == R.id.otherAppointmentRadioButtom) {
            reasonEditText.setEnabled(true);
            reasonEditText.setTextColor(ContextCompat.getColor(context, R.color.blue_cerulian));
        } else {
            reasonEditText.setEnabled(false);
            reasonEditText.setTextColor(ContextCompat.getColor(context, R.color.Munsell));
        }
    }

    private void onSetColorStateForRadioButton(AppCompatRadioButton appCompatRadioButton) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{

                        ContextCompat.getColor(context, R.color.lightSlateGray)
                        , ContextCompat.getColor(context, R.color.blue_cerulian),
                }
        );
        appCompatRadioButton.setSupportButtonTintList(colorStateList);
        appCompatRadioButton.setTextColor(colorStateList);
    }


}
