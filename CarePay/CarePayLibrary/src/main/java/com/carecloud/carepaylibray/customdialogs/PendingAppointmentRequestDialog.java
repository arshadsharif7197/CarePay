package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.Appointment;

public class PendingAppointmentRequestDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;

    public PendingAppointmentRequestDialog(Context context, Appointment appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
        onColorHeaderForPending();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_pending_request_appointment, null);
        TextView pendingRequestTextView = (TextView) childActionView.findViewById(R.id.appointRequestPendingTextView);
        pendingRequestTextView.setOnClickListener(this);
        mainLayout.addView(childActionView);
    }

    @SuppressWarnings("deprecation")
    private void onColorHeaderForPending() {
        View view = getRootView();
        view.findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.lightningyellow);
        ((TextView) view.findViewById(R.id.appointDateTextView)).setTextColor(context.getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.appointTimeTextView)).setTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.appointRequestPendingTextView) {
            onPendingRequestAppointment();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onPendingRequestAppointment() {
    }

}

