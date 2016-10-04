package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/22/2016.
 */
public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    private EditText reasonEdittext;
    private AppointmentModel appointmentModel;

    public RequestAppointmentDialog(Context context, AppointmentModel appointmentModel) {
        super(context, appointmentModel);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mainLayout = (LinearLayout) getAddActionChildView();
        TextView dateTextView = ((TextView) findViewById(R.id.appointDateTextView));
        TextView timeTextView = ((TextView) findViewById(R.id.appointTimeTextView));
        String[] timeDateString = DateUtil.getInstance().parseStringToDateTime(appointmentModel.getAppointmentDate());
        dateTextView.setText(timeDateString[0]);
        timeTextView.setText(timeDateString[1]);
        setActionButton();
    }

    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_request_appointment, null);
        reasonEdittext = (EditText) childActionView.findViewById(R.id.reasonEditText);
        TextView optionaltextView = (TextView) childActionView.findViewById(R.id.optionalTextView);
        Button appointmentRequestButton = (Button) childActionView.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setOnClickListener(this);
        appointmentRequestButton.requestFocus();
        SystemUtil.setProximaNovaSemiboldTypeface(context, optionaltextView);
        reasonEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.requestAppointmentButton) {
            onRequestAppointment();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onRequestAppointment() {
        // String reasonString = reasonEdittext.getText().toString();
        AppointmentsListFragment.showNewAddedAppointment = true;
        ((AddAppointmentActivity) context).finish();
    }

}
