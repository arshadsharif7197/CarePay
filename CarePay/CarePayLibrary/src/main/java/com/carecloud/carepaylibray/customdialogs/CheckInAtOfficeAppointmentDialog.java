package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;

public class CheckInAtOfficeAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private Class nextActivityClass;

    /**
     * Constructor.
     *
     * @param context           context
     * @param appointmentDTO    appointment Item
     * @param appointmentLabels Screen labels
     */
    public CheckInAtOfficeAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                            Class nextActivity) {
        super(context, appointmentDTO, false);
        this.context = context;
        this.nextActivityClass = nextActivity;
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
        checkInAtOfficeButton.setText(Label.getLabel("appointments_check_in_at_office"));
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
        Intent demographicReviewIntent = new Intent(context, nextActivityClass); //DemographicReviewActivity.class);
        context.startActivity(demographicReviewIntent);
    }

}
