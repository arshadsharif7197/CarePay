package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;

    public RequestAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context, appointmentDTO);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_request_appointment, null);

        Button appointmentRequestButton = (Button) childActionView.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setOnClickListener(this);
        appointmentRequestButton.requestFocus();

        TextView optionalTextView = (TextView) childActionView.findViewById(R.id.optionalTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(context, optionalTextView);

        EditText reasonEditText = (EditText) childActionView.findViewById(R.id.reasonEditText);
        reasonEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
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
        ((AddAppointmentActivity) context).finish();
    }
}
