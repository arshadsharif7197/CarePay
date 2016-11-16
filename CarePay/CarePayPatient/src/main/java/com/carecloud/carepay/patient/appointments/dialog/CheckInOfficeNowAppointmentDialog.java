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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
            new QrCodeViewDialog(context, appointmentDTO, appointmentInfo.getMetadata()).show();
            cancel();
        } else if (viewId == R.id.checkInNowButton) {
            TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckin();
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

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        WorkflowServiceHelper.getInstance().execute(transitionDTO, callback, queries, header);
    }

    private WorkflowServiceCallback demographicsVerifyCallback = new WorkflowServiceCallback() {
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
}
