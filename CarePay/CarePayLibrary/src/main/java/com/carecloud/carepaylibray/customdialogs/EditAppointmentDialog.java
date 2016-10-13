package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/22/2016.
 */

public class EditAppointmentDialog  extends BaseDoctorInfoDialog {

    private LinearLayout rootLayout;
    private Context context;
    private AppointmentModel appointmentModel;

    /**
     * Contractor for   dialog.
     *
     * @param context the String to evaluate
     * @param appointmentModel the DTO to evaluate
     */
    public EditAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout)getRootView();
        setActionButton();
    }

    private void setActionButton(){

        TextView editAppointmentTextView = (TextView)rootLayout.findViewById(R.id.dialogEditOrCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(R.string.edit_appointment_dialog);
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context,editAppointmentTextView);
        editAppointmentTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if(viewId == R.id.dialogEditOrCancelAppointTextView){
            onEditAppointmnent();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onEditAppointmnent(){

    }

}
