package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
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

    private boolean isCanceled = false;
    private boolean isMissed = false;

    /**
     * Contractor for   dialog.
     * @param context the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     * @param appointmentLabels Screen labels
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   AppointmentLabelDTO appointmentLabels) {

        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.isMissed = true;
        this.appointmentLabels = appointmentLabels;
    }

    /**
     * Contractor for dialog.
     * @param context context
     * @param appointmentDTO appointment item
     * @param isCanceled isCanceled
     * @param appointmentLabels screen labels
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   boolean isCanceled, AppointmentLabelDTO appointmentLabels) {

        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.isCanceled = isCanceled;
        this.isMissed = false;
        this.appointmentLabels = appointmentLabels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout) getRootView();
        mainLayout = (LinearLayout) getAddActionChildView();

        if (isCanceled || isMissed) {
            setActionButtonCanceled();
        } else {
            setActionButton();
        }
    }

    private void setActionButton() {
        CarePayTextView editAppointmentTextView = (CarePayTextView)
                rootLayout.findViewById(R.id.dialogCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(appointmentLabels.getAppointmentsCancelHeading());
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        editAppointmentTextView.setOnClickListener(this);
    }

    private void setActionButtonCanceled() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        CarePayTextView appointmentStatusLabel = (CarePayTextView)
                childActionView.findViewById(R.id.appointmentStatusLabel);

        if (isMissed) {
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsMissedHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        } else {
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
        if (viewId == R.id.dialogCancelAppointTextView) {
            onCancelAppointment();
            cancel();
        }
    }

    /**
     * call cancel at office api.
     */
    private void onCancelAppointment() {
        new CancelReasonAppointmentDialog(context, appointmentDTO, appointmentLabels).show();
    }

}