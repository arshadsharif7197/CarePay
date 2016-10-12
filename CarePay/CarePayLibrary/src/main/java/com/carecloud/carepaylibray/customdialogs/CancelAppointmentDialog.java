package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout rootLayout;
    private Context context;

    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout) getRootView();
        setActionButton();
    }

    @SuppressWarnings("deprecation")
    private void setActionButton() {
        TextView editAppointmentTextView = (TextView) rootLayout.findViewById(R.id.dialogEditAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(R.string.cancel_appointment_dialog);
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context, editAppointmentTextView);
        editAppointmentTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.dialogEditAppointTextView) {
            onEditAppointment();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onEditAppointment() {

    }

}
