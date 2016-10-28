package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBoldLabel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaLightLabel;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PendingAppointmentRequestDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentLabelDTO appointmentLabels;

    public PendingAppointmentRequestDialog(Context context, AppointmentDTO appointmentDTO,
                                           AppointmentLabelDTO appointmentLabels) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentLabels = appointmentLabels;
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

        CustomGothamRoundedMediumLabel pendingRequestTextView = (CustomGothamRoundedMediumLabel)
                childActionView.findViewById(R.id.appointRequestPendingTextView);
        pendingRequestTextView.setText(StringUtil.getLabelForView(
                appointmentLabels.getAppointmentsRequestPendingHeading()));
        pendingRequestTextView.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        pendingRequestTextView.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @SuppressWarnings("deprecation")
    private void onColorHeaderForPending() {
        View view = getRootView();
        view.findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.lightningyellow);
        ((CustomProxyNovaLightLabel) view.findViewById(R.id.appointDateTextView))
                .setTextColor(context.getResources().getColor(R.color.white));
        ((CustomGothamRoundedBoldLabel) view.findViewById(R.id.appointTimeTextView))
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

