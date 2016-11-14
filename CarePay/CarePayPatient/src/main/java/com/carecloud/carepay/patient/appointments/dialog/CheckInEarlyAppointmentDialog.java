package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;

public class CheckInEarlyAppointmentDialog extends BaseDoctorInfoDialog {


    private Context context;
    private LinearLayout mainLayout;
    private AppointmentDTO appointmentDTO;
    private AppointmentLabelDTO appointmentLabels;
    private TransitionDTO transitionDTO;

    /**
     * Constructor.
     *
     * @param context           context
     * @param appointmentDTO    appointment Item
     * @param appointmentLabels screen labels
     */
    public CheckInEarlyAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                         AppointmentLabelDTO appointmentLabels,
                                         TransitionDTO transitionDTO) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentLabels = appointmentLabels;
        this.appointmentDTO = appointmentDTO;
        this.transitionDTO = transitionDTO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_checkin_early_appointment, null);

        Button checkIn = (Button) childActionView.findViewById(R.id.checkinEarlyButton);
        checkIn.setText(appointmentLabels.getAppointmentsCheckInEarly());
        checkIn.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkinEarlyButton) {
            onCheckInEarly();
            cancel();
        }
    }

    /**
     * call check-in Now api.
     */
    private void onCheckInEarly() {
        WorkflowServiceHelper.getInstance().execute(transitionDTO, logincallback);
    }

    WorkflowServiceCallback logincallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(context).navigateToWorkflow(workflowDTO);

            // end-splash activity and transition
            // SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}