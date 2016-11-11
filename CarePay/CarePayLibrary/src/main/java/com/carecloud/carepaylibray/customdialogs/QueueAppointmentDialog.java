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
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

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
        super(context, appointmentDTO);
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
        queueLabel.setTextColor(ContextCompat.getColor(context, R.color.slateGray));

        CarePayTextView queueValue = (CarePayTextView)
                childActionView.findViewById(R.id.appointRequestQueueTextView);
        queueValue.setText(StringUtil.getLabelForView("You are 3rd")); // Remove once available in endpoint
        queueValue.setTextColor(ContextCompat.getColor(context, R.color.dark_green));

        mainLayout.addView(childActionView);
    }
}