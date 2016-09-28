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
 * Created by prem_mourya on 9/27/2016.
 */

public class QueueAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    private AppointmentModel appointmentModel;
    public QueueAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout)getAddActionChildView();
        setChildView();
    }
    private void setChildView(){
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_queue_appointment, null);

        mainLayout.addView(childActionView);
    }

}