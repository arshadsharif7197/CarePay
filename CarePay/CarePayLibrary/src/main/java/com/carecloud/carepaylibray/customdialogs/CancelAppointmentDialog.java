package com.carecloud.carepaylibray.customdialogs;

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

/**
 * Created by prem_mourya on 10/12/2016.
 */

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    public interface CancelAppointmentCallback {
        void onCancelAppointment(AppointmentDTO appointmentDTO);
    }

    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentLabelDTO appointmentLabels;
    private AppointmentsResultModel appointmentInfo;

    private AppointmentType appointmentType;
    private CancelAppointmentCallback cancelAppointmentCallback;

    /**
     * Contractor for   dialog.
     * @param context the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   AppointmentsResultModel appointmentInfo, AppointmentType appointmentType,
                                   CancelAppointmentCallback cancelAppointmentCallback) {

        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
        this.appointmentType = appointmentType;
        this.cancelAppointmentCallback = cancelAppointmentCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        appointmentLabels = appointmentInfo.getMetadata().getLabel();

        if (appointmentType == AppointmentType.CANCELLED_APPOINTMENT
                || appointmentType == AppointmentType.MISSED_APPOINTMENT
                || appointmentType == AppointmentType.REQUESTED_APPOINTMENT) {
            setActionButtonCanceled();
        }

        if (appointmentType == AppointmentType.CANCEL_APPOINTMENT) {
            setActionButton();
        }
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        Button cancelAppointmentButton = (Button)childActionView.findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setVisibility(View.VISIBLE);
        cancelAppointmentButton.setText(appointmentLabels.getAppointmentsCancelHeading());
        cancelAppointmentButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void setActionButtonCanceled() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        CarePayTextView appointmentStatusLabel = (CarePayTextView)
                childActionView.findViewById(R.id.appointmentStatusLabel);

        if (appointmentType == AppointmentType.MISSED_APPOINTMENT) {
            appointmentStatusLabel.setVisibility(View.VISIBLE);
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsMissedHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        } else if (appointmentType == AppointmentType.CANCELLED_APPOINTMENT) {
            appointmentStatusLabel.setVisibility(View.VISIBLE);
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsCanceledHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        } else if (appointmentType == AppointmentType.REQUESTED_APPOINTMENT) {
            appointmentStatusLabel.setVisibility(View.VISIBLE);
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsRequestPendingHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        if (appointmentType == AppointmentType.REQUESTED_APPOINTMENT) {
            findViewById(R.id.dialogHeaderlayout).setBackground(context.getResources()
                    .getDrawable(R.drawable.appointment_dialog_yellow_bg));
        } else {
            findViewById(R.id.dialogHeaderlayout).setBackground(context.getResources()
                    .getDrawable(R.drawable.appointment_dialog_dark_gray_bg));
        }

        ((CarePayTextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        ((CarePayTextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.cancelAppointmentButton) {
            new CancelReasonAppointmentDialog(context, appointmentDTO, appointmentInfo, cancelAppointmentCallback).show();
            cancel();
        }

        if (viewId == R.id.dialogAppointHeaderTextView
                && appointmentType == AppointmentType.CANCELLED_APPOINTMENT
                && cancelAppointmentCallback != null) {
            cancelAppointmentCallback.onCancelAppointment(appointmentDTO);
        }
    }
}