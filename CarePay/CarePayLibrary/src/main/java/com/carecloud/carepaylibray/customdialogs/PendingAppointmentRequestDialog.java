package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;

/**
 * Created by prem_mourya on 9/22/2016.
 */

public class PendingAppointmentRequestDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    public PendingAppointmentRequestDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout)getAddActionChildView();
        setActionButton();
        onColorHeaderForPending();
    }
    private void setActionButton(){
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_pending_request_appointment, null);
        TextView pendingrequesttextView = (TextView) childActionView.findViewById(R.id.appointRequestPendingTextView);
        pendingrequesttextView.setOnClickListener(this);
        mainLayout.addView(childActionView);
    }
    private void onColorHeaderForPending(){
         View view =  getRootView();
         view.findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.lightningyellow);
        ((TextView) view.findViewById(R.id.appointDateTextView)).setTextColor(context.getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.appointTimeTextView)).setTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if(viewId == R.id.appointRequestPendingTextView){
            onPendingRequestAppointment();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onPendingRequestAppointment(){
    }

}

