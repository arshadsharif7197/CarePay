package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentLabelDTO labelsDto;
    private AppointmentsResultModel appointmentInfo;
    private AppointmentType appointmentType;
    private boolean isCancelSuccess;

    public interface CancelAppointmentDialogListener {
        void onRefreshAppointmentList(AppointmentDTO appointmentDTO);

        void onPreRegisterTapped(AppointmentDTO appointmentDTO, AppointmentsResultModel appointmentInfo);

        void onCancelAppointmentButtonClicked(AppointmentDTO appointmentDTO, AppointmentsResultModel appointmentInfo);

    }

    private CancelAppointmentDialogListener callback;

    /**
     * Contractor for   dialog.
     * @param context the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   AppointmentsResultModel appointmentInfo,
                                   AppointmentType appointmentType,
                                   CancelAppointmentDialogListener callback) {

        super(context, appointmentDTO,false);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
        this.appointmentType = appointmentType;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        labelsDto = appointmentInfo.getMetadata().getLabel();

        initializeViews((LinearLayout) getAddActionChildView());
    }

    @SuppressLint("InflateParams")
    private void initializeViews(LinearLayout mainLayout) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        switch (appointmentType) {
            case MISSED_APPOINTMENT:
                initializeOff(view, labelsDto.getAppointmentsMissedHeading(), R.color.lightningyellow, R.drawable.appointment_dialog_dark_gray_bg);
                break;
            case CANCELLED_APPOINTMENT:
                initializeOff(view, labelsDto.getAppointmentsCanceledHeading(), R.color.harvard_crimson, R.drawable.appointment_dialog_dark_gray_bg);
                break;
            case REQUESTED_APPOINTMENT:
                initializeOff(view, labelsDto.getAppointmentsRequestPendingHeading(), R.color.colorPrimary, R.drawable.appointment_dialog_yellow_bg);
                break;
            default:
                initializeOn(view);
        }

        mainLayout.addView(view);
    }

    private void initializeOn(View view) {
        Button cancelAppointmentButton = (Button)view.findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setVisibility(View.VISIBLE);
        cancelAppointmentButton.setText(labelsDto.getAppointmentsCancelHeading());
        cancelAppointmentButton.setOnClickListener(this);

        Button preRegisterButton = (Button) view.findViewById(R.id.preRegisterButton);
        preRegisterButton.setVisibility(View.VISIBLE);
        preRegisterButton.setText(labelsDto.getAppointmentsPreRegister());
        preRegisterButton.setOnClickListener(getPreRegisterButtonClickListener());
    }

    @SuppressWarnings("deprecation")
    private void initializeOff(View view, String status, int statusColor, int headerColor) {
        CarePayTextView appointmentStatusLabel = (CarePayTextView) view.findViewById(R.id.appointmentStatusLabel);
        appointmentStatusLabel.setVisibility(View.VISIBLE);
        appointmentStatusLabel.setText(status);
        appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, statusColor));

        findViewById(R.id.dialogHeaderlayout).setBackground(context.getResources().getDrawable(headerColor));

        ((CarePayTextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        ((CarePayTextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    void setCancelledSuccess(boolean cancelSuccess) {
        isCancelSuccess = cancelSuccess;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.cancelAppointmentButton) {
            callback.onCancelAppointmentButtonClicked(appointmentDTO, appointmentInfo) ;
            cancel();
        } else if (viewId == R.id.dialogAppointHeaderTextView
                && isCancelSuccess && callback != null) {
            callback.onRefreshAppointmentList(appointmentDTO);
        }
    }

    private View.OnClickListener getPreRegisterButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onPreRegisterTapped(appointmentDTO, appointmentInfo);
                }
                dismiss();
            }
        };
    }
}