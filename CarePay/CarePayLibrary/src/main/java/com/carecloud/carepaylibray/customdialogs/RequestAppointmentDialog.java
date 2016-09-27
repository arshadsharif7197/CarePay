package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;

/**
 * Created by prem_mourya on 9/22/2016.
 */

public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    private EditText reasonEdittext;
    public RequestAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout)getAddActionChildView();
        setActionButton();
    }
    private void setActionButton(){
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_request_appointment, null);
        reasonEdittext = (EditText) childActionView.findViewById(R.id.reasonEditText);
        Button appointmentRequestButton = (Button) childActionView.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if(viewId == R.id.requestAppointmentButton){
            onRequestAppointment();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onRequestAppointment(){
       // String reasonString = reasonEdittext.getText().toString();
        AppointmentsListFragment.showCheckedInView = true;
        ((AddAppointmentActivity) context).finish();
    }

}
