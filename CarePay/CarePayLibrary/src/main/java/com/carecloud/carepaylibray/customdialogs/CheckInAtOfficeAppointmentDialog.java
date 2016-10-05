package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;

public class CheckInAtOfficeAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;

    public CheckInAtOfficeAppointmentDialog(Context context, Appointment appointmentModel) {
        super(context, appointmentModel);
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
        View childActionView = inflater.inflate(R.layout.dialog_checkin_at_office_appointment, null);
        Button checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkAtOfficeButton);
        checkInAtOfficeButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkAtOfficeButton) {
            onCheckInAtOffice();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onCheckInAtOffice() {
        Intent demographicReviewIntent = new Intent(context, DemographicReviewActivity.class);
        context.startActivity(demographicReviewIntent);
    }

}
