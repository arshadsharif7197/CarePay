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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentDTO appointmentDTO;
    private AppointmentsResultModel appointmentInfo;

    /**
     * @param context           context
     * @param appointmentDTO    appointment dto
     * @param appointmentInfo   transition dto
     */
    public CheckInOfficeNowAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                             AppointmentsResultModel appointmentInfo) {
        super(context, appointmentDTO);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        AppointmentLabelDTO appointmentLabels = appointmentInfo.getMetadata().getLabel();

        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_checkin_office_now_appointment, null);

        Button checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkInAtOfficeButton);
        checkInAtOfficeButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInAtOfficeButtonText()));
        checkInAtOfficeButton.setOnClickListener(this);

        Button checkInNowButton = (Button) childActionView.findViewById(R.id.checkInNowButton);
        checkInNowButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInNow()));
        checkInNowButton.setOnClickListener(this);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkInAtOfficeButton) {
            onCheckInAtOffice();
            cancel();
        } else if (viewId == R.id.checkInNowButton) {
            onCheckInNow();
            cancel();
        }
    }

    /**
     * call check-in at office api.
     */
    private void onCheckInAtOffice() {
        /*To show QR code in dialog*/
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckinAtOffice();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToQRCodeCallback, queries, header);
    }

    /**
     * call check-in Now api.
     */
    private void onCheckInNow() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckin();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToDemographicsVerifyCallback, queries, header);
    }

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(context).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };

    private WorkflowServiceCallback transitionToQRCodeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            AppointmentMetadataModel appointmentMetadataModel = appointmentInfo.getMetadata();
            new QrCodeViewDialog(context, appointmentDTO, appointmentMetadataModel).show();
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };
}
