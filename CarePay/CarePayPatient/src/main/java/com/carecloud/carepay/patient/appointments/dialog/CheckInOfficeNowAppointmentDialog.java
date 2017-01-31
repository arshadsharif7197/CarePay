package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentDTO appointmentDTO;
    private AppointmentsResultModel appointmentInfo;
    private Boolean enableCheckin;
    private Button checkInNowButton;
    private Button checkInAtOfficeButton;

    /**
     * @param context           context
     * @param appointmentDTO    appointment dto
     * @param appointmentInfo   transition dto
     */
    public CheckInOfficeNowAppointmentDialog(Context context, Boolean enableCheckin, AppointmentDTO appointmentDTO,
                                             AppointmentsResultModel appointmentInfo) {
        super(context, appointmentDTO, false);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
        this.enableCheckin = enableCheckin;
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

        checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkInAtOfficeButton);
        checkInAtOfficeButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInAtOfficeButtonText()));
        checkInAtOfficeButton.setOnClickListener(this);

        checkInNowButton = (Button) childActionView.findViewById(R.id.checkInNowButton);
        checkInNowButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInNow()));
        checkInNowButton.setOnClickListener(this);
        if(enableCheckin == true){
            checkInAtOfficeButton.setEnabled(false);
            checkInAtOfficeButton.setClickable(false);
            checkInNowButton.setEnabled(false);
            checkInNowButton.setClickable(false);
            checkInAtOfficeButton.setTextColor(Color.WHITE);
            checkInAtOfficeButton.setBackgroundColor(getContext().getResources().getColor(R.color.light_gray));
            checkInNowButton.setBackgroundColor(getContext().getResources().getColor(R.color.light_gray));
        }else{
            checkInAtOfficeButton.setEnabled(true);
            checkInAtOfficeButton.setClickable(true);
            checkInNowButton.setEnabled(true);
            checkInNowButton.setClickable(true);
        }

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkInAtOfficeButton) {
            checkInAtOfficeButton.setEnabled(false);
            new QrCodeViewDialog(context, appointmentDTO, appointmentInfo.getMetadata()).show();
            checkInAtOfficeButton.setEnabled(true);
            cancel();
        } else if (viewId == R.id.checkInNowButton) {
            checkInNowButton.setEnabled(false);
            TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckingIn();
            doTransition(transitionDTO, demographicsVerifyCallback);
            cancel();
        }
    }

    /**
     * call to do transition.
     */
    private void doTransition(TransitionDTO transitionDTO, WorkflowServiceCallback callback) {
        Gson gson = new Gson();
        Map<String, String> queries = new HashMap<>();
        JsonObject queryStringObject = appointmentInfo.getMetadata().getTransitions().getCheckinAtOffice().getQueryString();
        QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

        queries.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());
        queries.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
        header.put("transition", "true");

        WorkflowServiceHelper.getInstance().execute(transitionDTO, callback, queries, header);
    }

    private WorkflowServiceCallback demographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            checkInNowButton.setEnabled(true);
            PatientNavigationHelper.getInstance(context).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            checkInNowButton.setEnabled(true);
            SystemUtil.showFaultDialog(getContext());
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
