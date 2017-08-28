package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

public class PendingAppointmentRequestDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;

    /**
     * Constructor.
     *
     * @param context        activity context
     * @param appointmentDTO appointment model
     */
    public PendingAppointmentRequestDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO, false);
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

        CarePayTextView pendingRequestTextView = (CarePayTextView)
                childActionView.findViewById(R.id.appointRequestPendingTextView);
        pendingRequestTextView.setText(Label.getLabel("appointments_request_pending_heading"));
        pendingRequestTextView.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
        pendingRequestTextView.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @SuppressWarnings("deprecation")
    private void onColorHeaderForPending() {
        View view = getRootView();
        view.findViewById(R.id.dialogHeaderLayout).setBackgroundResource(R.color.lightning_yellow);
        ((TextView) view.findViewById(R.id.appointDateTextView))
                .setTextColor(context.getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.appointTimeTextView))
                .setTextColor(context.getResources().getColor(R.color.white));
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

