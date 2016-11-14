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
import com.carecloud.carepaylibray.appointments.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.utils.StringUtil;

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentMetadataModel appointmentMetadataModel;
    private AppointmentLabelDTO appointmentLabels;
    private TransitionDTO transitionDTO;
    private Class nextActivityClass;

    /**
     *
     * @param context context
     * @param appointmentDTO appointment dto
     * @param appointmentLabels appointment labels
     * @param transitionDTO transition dto
     * @param nextActivity next Activity
     */
    public CheckInOfficeNowAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                             AppointmentLabelDTO appointmentLabels,
                                             TransitionDTO transitionDTO,
                                             Class nextActivity) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentLabels=appointmentLabels;
        this.nextActivityClass = nextActivity;
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
        View childActionView = inflater.inflate(R.layout.dialog_checkin_office_now_appointment, null);

        Button checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkOfficeButton);
        checkInAtOfficeButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInAtOfficeButtonText()));
        checkInAtOfficeButton.setOnClickListener(this);

        Button checkInNowButton = (Button) childActionView.findViewById(R.id.checkOfficeNowButton);
        checkInNowButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInNow()));
        checkInNowButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkOfficeButton) {
            onCheckInAtOffice();
            cancel();
        } else if (viewId == R.id.checkOfficeNowButton) {
            onCheckInAtNow();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onCheckInAtOffice() {
        /*To show QR code in dialog*/
        new QrCodeViewDialog(context, appointmentDTO, appointmentMetadataModel).show();
    }

    /**
     * call check-in at Now api.
     */
    private void onCheckInAtNow() {
        WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToDemographicsVerifyCallback);
    }

    WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
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
