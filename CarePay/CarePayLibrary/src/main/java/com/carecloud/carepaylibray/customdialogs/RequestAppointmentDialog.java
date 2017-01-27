package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
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

    private EditText reasonEditText;

    /**
     * Constructor.
     * @param context activity context
     * @param appointmentDTO appointment model
     * @param appointmentsResultModel appointments result model
     */
    public RequestAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                    AppointmentsResultModel appointmentsResultModel) {

        super(context, appointmentDTO);
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

        CarePayTextView optionalTextView = (CarePayTextView)
                childActionView.findViewById(R.id.optionalTextView);
        optionalTextView.setText(appointmentsResultModel.getMetadata().getLabel().getAppointmentsOptionalHeading());

        TextInputLayout reasonInputLayout = (TextInputLayout) childActionView.findViewById(R.id.reasonTextInputLayout);
        reasonInputLayout.setTag(appointmentsResultModel.getMetadata().getLabel().getAppointmentsReasonForVisitHeading());

        reasonEditText = (EditText) childActionView.findViewById(R.id.reasonEditText);
        reasonEditText.setTag(reasonInputLayout);

        String visitReason = (String) appointmentDTO.getPayload().getChiefComplaint();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            reasonEditText.setText(visitReason);
        }

        SystemUtil.handleHintChange(reasonEditText, true);
        reasonEditText.clearFocus();

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
     * call make_appointment api.
     */
    private void onRequestAppointment() {

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
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
        appointmentJSONObj.addProperty("comments", reasonEditText.getText().toString().trim());
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getMakeAppointmentCallback,makeAppointmentJSONObj.toString(), queryMap);
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            isAppointmentAdded = true;
            ((AppCompatActivity) context).finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
