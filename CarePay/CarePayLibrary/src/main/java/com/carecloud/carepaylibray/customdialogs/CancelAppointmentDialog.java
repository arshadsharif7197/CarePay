package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 10/12/2016.
 */

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout rootLayout;
    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private boolean isCanceled = false;
    private boolean isMissed = false;
    private CustomGothamRoundedMediumLabel appointmentStatusLabel;

    /**
     * Contractor for   dialog.
     *
     * @param context          the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.isMissed = true;
    }

    /**
     * Contractor for   dialog.
     *
     * @param context          the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO, boolean isCanceled) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.isCanceled = isCanceled;
        this.isMissed = false;
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

        TextView editAppointmentTextView = (TextView) rootLayout.findViewById(R.id.dialogEditOrCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(R.string.cancel_appointment_dialog);
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context, editAppointmentTextView);
        editAppointmentTextView.setOnClickListener(this);
    }

    private void setActionButtonCanceled() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        appointmentStatusLabel = (CustomGothamRoundedMediumLabel) childActionView.findViewById(R.id.appointmentStatusLabel);
        if (isMissed) {
            appointmentStatusLabel.setText(StringUtil.getLabelForView(""));
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        } else {
            appointmentStatusLabel.setText(StringUtil.getLabelForView(""));
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        }

        findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.Feldgrau);
        ((TextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        ((TextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.dialogEditOrCancelAppointTextView) {
            onCancelAppointment();
            cancel();
        }
    }

    /**
     * call cancel at office api.
     */
    private void onCancelAppointment() {
        new CancelReasonAppointmentDialog(context, appointmentDTO).show();
    }

}