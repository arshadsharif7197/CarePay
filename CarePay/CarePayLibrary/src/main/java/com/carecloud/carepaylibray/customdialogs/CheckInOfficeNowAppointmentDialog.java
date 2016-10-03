package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;

/**
 * Created by prem_mourya on 9/22/2016.
 */

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout     mainLayout;
    private Context          context;
    private AppointmentModel appointmentModel;

    public CheckInOfficeNowAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_checkin_office_now_appointment, null);
        Button checkinAtofficeButton = (Button) childActionView.findViewById(R.id.checkOfficeButton);
        Button checkinAtNowButton = (Button) childActionView.findViewById(R.id.checkOfficeNowButton);

        checkinAtofficeButton.setOnClickListener(this);
        checkinAtNowButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkOfficeButton) {
            onCheckInAtOffice();
            cancel();
        } else if (viewId == R.id.checkOfficeNowButton) {
            onCheckInAtNow();
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

    /**
     * call check-in at Now api.
     */
    private void onCheckInAtNow() {
        Intent demographicReviewIntent = new Intent(context, DemographicReviewActivity.class);
        context.startActivity(demographicReviewIntent);
    }
}
