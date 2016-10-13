package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;

public class CheckInEarlyAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;

    public CheckInEarlyAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_checkin_early_appointment, null);
        Button checkIn = (Button) childActionView.findViewById(R.id.checkinEarlyButton);
        checkIn.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkinEarlyButton) {
            onCheckInEarly();
            cancel();
        }
    }

    /**
     * call check-in Now api.
     */
    private void onCheckInEarly() {
    }
}