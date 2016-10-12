package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 10/12/2016.
 */

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout rootLayout,mainLayout;
    private Context context;
    private AppointmentModel appointmentModel;
    private boolean isCanceled = false;
    private CustomGothamRoundedMediumLabel canceledLabel;
    public CancelAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }
    public CancelAppointmentDialog(Context context, AppointmentModel appointmentModel,boolean isCanceled ) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
        this.isCanceled = isCanceled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout)getRootView();
        mainLayout= (LinearLayout)getAddActionChildView();
        if(isCanceled){
            setActionButtonCanceled();
        }else {
            setActionButton();
        }
    }
    private void setActionButton(){

        TextView editAppointmentTextView = (TextView)rootLayout.findViewById(R.id.dialogEditOrCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(R.string.cancel_appointment_dialog);
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context,editAppointmentTextView);
        editAppointmentTextView.setOnClickListener(this);
    }
    private void setActionButtonCanceled(){

        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);
        canceledLabel = (CustomGothamRoundedMediumLabel)childActionView.findViewById(R.id.canceledLabel);
        canceledLabel.setTextColor(ContextCompat.getColor(context,R.color.harvard_crimson));
        findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.Feldgrau);
        ((TextView)findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context,R.color.white));
        ((TextView)findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context,R.color.white));
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if(viewId == R.id.dialogEditOrCancelAppointTextView){
            onCancelAppointment();
            cancel();
        }
    }

    /**
     * call cancel at office api.
     */
    private void onCancelAppointment(){
        new CancelReasonAppointmentDialog(context,appointmentModel).show();
    }

}
