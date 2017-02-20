package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentDTO appointmentDTO;
    public static boolean isAppointmentAdded = false;

    /**
     * Constructor.
     * @param context activity context
     * @param appointmentDTO appointment model
     * @param appointmentsResultModel appointments result model
     */
    public RequestAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                    AppointmentsResultModel appointmentsResultModel) {

        super(context, appointmentDTO, true);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentsResultModel = appointmentsResultModel;
        isAppointmentAdded=false;
    }

    @SuppressWarnings("ConstantConditions")
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
        appointmentRequestButton.setText(appointmentsResultModel.getMetadata().getLabel().getAppointmentsRequestHeading());
        appointmentRequestButton.setOnClickListener(this);
        appointmentRequestButton.requestFocus();

        CarePayTextView reasonTypeTextView = (CarePayTextView)
                childActionView.findViewById(R.id.reasonTypeTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, reasonTypeTextView);
        reasonTypeTextView.setText(appointmentsResultModel.getMetadata().getLabel().getVisitTypeHeading());

        CarePayTextView reasonTextView = (CarePayTextView)
                childActionView.findViewById(R.id.reasonTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, reasonTextView);

        String visitReason = (String) appointmentDTO.getPayload().getChiefComplaint();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            reasonTextView.setText(visitReason);
        }

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
     * call make_appointment api.
     */
    private void onRequestAppointment() {

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ((ISession) context).getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", appointmentsResultModel.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", appointmentsResultModel.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", appointmentDTO.getPayload().getPatient().getId());
        appointmentJSONObj.addProperty("start_time", appointmentDTO.getPayload().getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentDTO.getPayload().getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", appointmentDTO.getPayload().getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", appointmentDTO.getPayload().getProviderId());
        appointmentJSONObj.addProperty("visit_reason_id", appointmentDTO.getPayload().getVisitReasonId());
        appointmentJSONObj.addProperty("resource_id", appointmentDTO.getPayload().getResourceId());
        appointmentJSONObj.addProperty("chief_complaint", appointmentDTO.getPayload().getChiefComplaint().toString());
        appointmentJSONObj.addProperty("comments", "");
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();

        ((ISession) context).getWorkflowServiceHelper().execute(transitionDTO, getMakeAppointmentCallback,makeAppointmentJSONObj.toString(), queryMap);
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            isAppointmentAdded = true;
            ((AppCompatActivity) context).finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
