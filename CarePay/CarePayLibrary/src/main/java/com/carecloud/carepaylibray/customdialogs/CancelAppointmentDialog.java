package com.carecloud.carepaylibray.customdialogs;

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

    private LinearLayout rootLayout;
    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentLabelDTO appointmentLabels;
    private AppointmentsResultModel appointmentInfo;

    private boolean isCanceled = false;
    private boolean isMissed = false;

    /**
     * Contractor for   dialog.
     * @param context the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   AppointmentsResultModel appointmentInfo) {

        super(context, appointmentDTO);
        this.isMissed = true;
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
    }

    /**
     * Contractor for dialog.
     * @param context context
     * @param appointmentDTO appointment item
     * @param isCanceled isCanceled
     * @param appointmentInfo Appointment info data
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   boolean isCanceled, AppointmentsResultModel appointmentInfo) {

        super(context, appointmentDTO);
        this.isMissed = false;
        this.context = context;
        this.isCanceled = isCanceled;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout) getRootView();
        mainLayout = (LinearLayout) getAddActionChildView();
        appointmentLabels = appointmentInfo.getMetadata().getLabel();

        if (isCanceled || isMissed) {
            setActionButtonCanceled();
        } else {
            setActionButton();
        }
    }

    private void setActionButton() {
        /*CarePayTextView editAppointmentTextView = (CarePayTextView)
                rootLayout.findViewById(R.id.dialogCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(appointmentLabels.getAppointmentsCancelHeading());
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        editAppointmentTextView.setOnClickListener(this);*/
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);
        Button cancelAppointmentButton = (Button)childActionView.findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setVisibility(View.VISIBLE);
        cancelAppointmentButton.setText(appointmentLabels.getAppointmentsCancelHeading());
        //cancelAppointmentButton.setTextColor(context.getResources().getColor(R.color.glitter));
        cancelAppointmentButton.setOnClickListener(this);

        //findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.Feldgrau);
        //((CarePayTextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        //((CarePayTextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        mainLayout.addView(childActionView);

    }

    private void setActionButtonCanceled() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        CarePayTextView appointmentStatusLabel = (CarePayTextView)
                childActionView.findViewById(R.id.appointmentStatusLabel);

        if (isMissed) {
            appointmentStatusLabel.setVisibility(View.VISIBLE);
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsMissedHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        } else {
            appointmentStatusLabel.setVisibility(View.GONE);
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsCanceledHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        }

        findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.Feldgrau);
        ((CarePayTextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        ((CarePayTextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.cancelAppointmentButton) {
            new CancelReasonAppointmentDialog(context, appointmentDTO, appointmentInfo).show();
            cancel();
        }
    }
}