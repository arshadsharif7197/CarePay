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
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class QueueAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentLabelDTO appointmentLabels;

    /**
     * Constructor.
     * @param context context
     * @param appointmentDTO appointment item
     * @param appointmentLabels screen labels
     */
    public QueueAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                  AppointmentLabelDTO appointmentLabels) {
        super(context, appointmentDTO, false);
        this.context = context;
        this.appointmentLabels = appointmentLabels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setChildView();
    }

    @SuppressLint("InflateParams")
    private void setChildView() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_queue_appointment, null);

        CarePayTextView queueLabel = (CarePayTextView)
                childActionView.findViewById(R.id.appointRequestQueueLabel);
        queueLabel.setText(appointmentLabels.getAppointmentsQueueHeading());

        CarePayTextView queueValue = (CarePayTextView)
                childActionView.findViewById(R.id.appointRequestQueueTextView);
        queueValue.setText(StringUtil.getLabelForView("You are 3rd")); // Remove once available in endpoint

        Button checkoutNow = (Button) childActionView.findViewById(R.id.appointmentRequestCheckoutNow);
        checkoutNow.setText(appointmentLabels.getAppointmentRequestCheckoutNow());
        SystemUtil.setGothamRoundedMediumTypeface(context, checkoutNow);
        checkoutNow.setVisibility(View.VISIBLE);

        mainLayout.addView(childActionView);
    }
}