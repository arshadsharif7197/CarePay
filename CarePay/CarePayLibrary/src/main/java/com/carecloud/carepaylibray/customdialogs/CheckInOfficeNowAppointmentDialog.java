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
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;

    public CheckInOfficeNowAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
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
        View childActionView = inflater.inflate(R.layout.dialog_checkin_office_now_appointment, null);
        Button checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkOfficeButton);
        Button checkInAtNowButton = (Button) childActionView.findViewById(R.id.checkOfficeNowButton);

        checkInAtOfficeButton.setOnClickListener(this);
        checkInAtNowButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkOfficeButton) {
            onCheckInAtOffice();
        } else if (viewId == R.id.checkOfficeNowButton) {
            onCheckInAtNow();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onCheckInAtOffice() {
        /*To show QR code in dialog*/
        new QrCodeViewDialog(context, appointmentDTO).show();
    }

    /**
     * call check-in at Now api.
     */
    private void onCheckInAtNow() {
        Intent demographicReviewIntent = new Intent(context, DemographicReviewActivity.class);
        context.startActivity(demographicReviewIntent);
    }
}
