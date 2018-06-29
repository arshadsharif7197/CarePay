package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationReasonDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class CancelReasonAppointmentDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentsResultModel appointmentInfo;

    private RadioGroup cancelReasonRadioGroup;
    private Button cancelAppointmentButton;
    private EditText reasonEditText;
    private TextInputLayout reasonTextInputLayout;

    private int selectedReasonId = -1;
    private List<CancellationReasonDTO> cancellationReasons;

    public interface CancelReasonAppointmentDialogListener {
        void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment);
    }

    private CancelReasonAppointmentDialogListener callback;

    /**
     * Contractor for dialog.
     *
     * @param context         context
     * @param appointmentDTO  appointment Item
     * @param appointmentInfo Appointment Info data
     */
    public CancelReasonAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                         AppointmentsResultModel appointmentInfo,
                                         CancelReasonAppointmentDialogListener callback) {
        super(context);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
        this.callback = callback;
    }

    @SuppressWarnings("ConstantConditions")
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
        onSetListener();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void onInitialization() {
        ((CarePayTextView) findViewById(R.id.heading_text))
                .setText(Label.getLabel("cancel_appointment_reasons_title"));
        reasonTextInputLayout = (TextInputLayout) findViewById(R.id.reasonTextInputLayout);
        reasonEditText = (EditText) findViewById(R.id.reasonEditText);
        reasonEditText.setHint(Label.getLabel("cancel_appointment_other_reason_hint"));
        reasonEditText.setHintTextColor(context.getResources().getColor(R.color.light_gray_dialog));

        cancelReasonRadioGroup = (RadioGroup) findViewById(R.id.cancelReasonRadioGroup);
        cancelAppointmentButton = (Button) findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setText(Label.getLabel("cancel_appointments_heading"));
        SystemUtil.setProximaNovaRegularTypeface(context, cancelAppointmentButton);

        cancellationReasons = appointmentInfo.getPayload().getCancellationReasons();
        if (cancellationReasons != null) {
            for (int count = 0; count < cancellationReasons.size(); count++) {
                AppointmentCancellationReasonDTO cancellationReason
                        = cancellationReasons.get(count).getAppointmentCancellationReason();
                addCancelReason(cancellationReason.getName(), cancellationReason.getId());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void addCancelReason(String cancelReason, int id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AppCompatRadioButton cancelReasonView = (AppCompatRadioButton) inflater.inflate(R.layout.cancel_appointment_reason_item, null);
        RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.cancel_radio_button_height));
        param.setMargins(context.getResources().getDimensionPixelSize(R.dimen.apt_popup_parent_padding), 0,
                context.getResources().getDimensionPixelSize(R.dimen.apt_popup_parent_padding), 0);
        cancelReasonView.setLayoutParams(param);
        cancelReasonView.setText(cancelReason);
        cancelReasonView.setId(id);
        SystemUtil.setProximaNovaRegularTypeface(context, cancelReasonView);
        cancelReasonRadioGroup.addView(cancelReasonView);

        // Add divider
        ImageView divider = new ImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.apt_lst_img_elevation));
        divider.setLayoutParams(layoutParams);
        divider.setBackgroundColor(context.getResources().getColor(R.color.cadet_gray));
        cancelReasonRadioGroup.addView(divider);
    }

    private void onSetListener() {
        findViewById(R.id.dialogCloseHeaderImageView).setOnClickListener(this);

        cancelReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton checkedRadioButton = (AppCompatRadioButton) group.findViewById(checkedId);
                selectedReasonId = group.getCheckedRadioButtonId();
                onSetColorStateForRadioButton(checkedRadioButton);
                onSelectionRadioCancel(checkedRadioButton.isChecked());
            }
        });

        reasonEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
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
            onCancelAppointment();
            cancel();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectionRadioCancel(boolean isSelected) {
        if (isSelected) {
//            cancelAppointmentButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_red_border));
            cancelAppointmentButton.setEnabled(true);
//            cancelAppointmentButton.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        }

        // Check for other cancellation reason
        if (cancellationReasons.get(cancellationReasons.size() - 1).getAppointmentCancellationReason().getId() == selectedReasonId) {
            reasonTextInputLayout.setVisibility(View.VISIBLE);
            reasonEditText.setEnabled(true);
            reasonEditText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            reasonTextInputLayout.setVisibility(View.GONE);
            reasonEditText.setEnabled(false);
            reasonEditText.setTextColor(ContextCompat.getColor(context, R.color.light_gray_dialog));
        }
    }

    private void onSetColorStateForRadioButton(AppCompatRadioButton appCompatRadioButton) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        ContextCompat.getColor(context, R.color.lightSlateGray),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                }
        );
        appCompatRadioButton.setSupportButtonTintList(colorStateList);
        appCompatRadioButton.setTextColor(colorStateList);
    }

    private int getSelectedCancellationIndex() {
        for (int index = 0; index < cancellationReasons.size(); index++) {
            AppointmentCancellationReasonDTO cancellationReason
                    = cancellationReasons.get(index).getAppointmentCancellationReason();
            if (cancellationReason.getId() == selectedReasonId) {
                return index;
            }
        }
        return -1;
    }

    /**
     * call cancel appointment api.
     */
    private void onCancelAppointment() {
        int selectedIndex = getSelectedCancellationIndex();
        if (selectedIndex != -1) {
            AppointmentCancellationReasonDTO cancellationReason
                    = cancellationReasons.get(selectedIndex).getAppointmentCancellationReason();
            callback.onCancelReasonAppointmentDialogCancelClicked(appointmentDTO, cancellationReason.getId(), reasonEditText.getText().toString());
        }
    }

}