package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

public class QueueAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;

    /**
     * Constructor.
     *
     * @param context        context
     * @param appointmentDTO appointment item
     */
    public QueueAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO, false);
        this.context = context;
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
        queueLabel.setText(Label.getLabel("appointments_queue_heading"));

        CarePayTextView queueValue = (CarePayTextView)
                childActionView.findViewById(R.id.appointRequestQueueTextView);
        queueValue.setText(StringUtil.getLabelForView("You are 3rd")); // Remove once available in endpoint

        Button checkoutNow = (Button) childActionView.findViewById(R.id.appointmentRequestCheckoutNow);
        checkoutNow.setText(Label.getLabel("appointment_request_checkout_now"));
        checkoutNow.setVisibility(View.VISIBLE);

        mainLayout.addView(childActionView);
    }
}