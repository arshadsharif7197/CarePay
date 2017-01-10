package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PracticeRequestAppointmentDialog extends BasePracticeDialog {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsSlotsDTO appointmentsSlotsDTO;
    private EditText visitTypeEditText;
    public static boolean isAppointmentAdded=false;

    /**
     * Constructor.
     * @param context activity context
     * @param cancelString String
     * @param appointmentsSlotsDTO AppointmentsSlotsDTO
     */
    public PracticeRequestAppointmentDialog(Context context, String cancelString, AppointmentsSlotsDTO appointmentsSlotsDTO) {

        super(context, cancelString, false);
        this.context = context;
        this.appointmentsSlotsDTO = appointmentsSlotsDTO;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isAppointmentAdded=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onAddContentView(inflater);
    }

    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dialog_request_appointment_summary, null);
        ((FrameLayout)findViewById(com.carecloud.carepay.practice.library.R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    private void inflateUIComponents(View view){
        appointmentsResultModel = ((ScheduleAppointmentActivity)context).getResourcesToSchedule();
        Button requestAppointmentButton = (Button)
                view.findViewById(R.id.requestAppointmentButton);
        requestAppointmentButton.setText(appointmentsResultModel.getMetadata().getLabel().getAppointmentsRequestHeading());
        requestAppointmentButton.setOnClickListener(requestAppointmentClickListener);
        requestAppointmentButton.requestFocus();
        SystemUtil.setGothamRoundedBoldTypeface(context,requestAppointmentButton);

        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(appointmentsSlotsDTO.getStartTime());
        CarePayTextView appointmentDateTextView = (CarePayTextView)view.findViewById(R.id.appointment_date);
        appointmentDateTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
        CarePayTextView appointmentTimeTextView = (CarePayTextView)view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setText(DateUtil.getInstance().getTime12Hour());

        CarePayTextView providerImageTextView = (CarePayTextView)view.findViewById(R.id.provider_short_name);
        providerImageTextView.setText(StringUtil.onShortDrName(((ScheduleAppointmentActivity)context)
                .getSelectedResource().getResource().getProvider().getName()));
        CarePayTextView appointmentDoctorNameTextView = (CarePayTextView)view.findViewById(R.id.provider_doctor_name);
        appointmentDoctorNameTextView.setText(((ScheduleAppointmentActivity)context).getSelectedResource()
                .getResource().getProvider().getName());
        CarePayTextView appointmentDoctorSpecialityTextView = (CarePayTextView)view.findViewById(R.id.provider_doctor_speciality);
        appointmentDoctorSpecialityTextView.setText(((ScheduleAppointmentActivity)context).getSelectedResource()
                .getResource().getProvider().getSpecialty().getName());

        //Endpoint not support location for individual resource,
        //Hence used 0th item from location array
        AppointmentLocationDTO location = appointmentsResultModel.getPayload().getResourcesToSchedule()
                .get(0).getLocations().get(0);
        CarePayTextView appointmentPlaceNameTextView = (CarePayTextView)view.findViewById(R.id.provider_place_name);
        appointmentPlaceNameTextView.setText(location.getName());
        CarePayTextView appointmentAddressTextView = (CarePayTextView)view.findViewById(R.id.provider_place_address);
        appointmentAddressTextView.setText(location.getAddress().getPlaceAddressString());
        CarePayTextView appointmentNewPatientTextView = (CarePayTextView)view.findViewById(R.id.optionalTextView);
        appointmentNewPatientTextView.setText(appointmentsResultModel.getMetadata().getLabel().getRequestAppointmentNewPatient());

        visitTypeEditText = (EditText)view.findViewById(R.id.reasonEditText);
        visitTypeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        //setDialogTitle("");
        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);
    }

    /**
     *Click listener for request appointment button
     */
    View.OnClickListener requestAppointmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Call request appointment endpoint from here
            onRequestAppointment();
            dismiss();
        }
    };

    /**
     * call make_appointment api.
     */
    private void onRequestAppointment() {

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", ((ScheduleAppointmentActivity)context).getPatientId());
        appointmentJSONObj.addProperty("start_time", appointmentsSlotsDTO.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentsSlotsDTO.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", appointmentsResultModel.getPayload().getResourcesToSchedule()
                .get(0).getLocations().get(0).getId());
        appointmentJSONObj.addProperty("provider_id", ((ScheduleAppointmentActivity)context).getSelectedResource()
                .getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", ((ScheduleAppointmentActivity)context).getSelectedVisitTypeDTO()
                .getId());
        appointmentJSONObj.addProperty("resource_id", ((ScheduleAppointmentActivity)context).getSelectedResource()
                .getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", ((ScheduleAppointmentActivity)context).getSelectedVisitTypeDTO()
                .getName());
        appointmentJSONObj.addProperty("comments", visitTypeEditText.getText().toString().trim());
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getMakeAppointmentCallback,makeAppointmentJSONObj
                .toString(), queryMap);
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(context).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(context).dismiss();
            ((ScheduleAppointmentActivity) context).finish();
            AppointmentsActivity.setIsNewAppointmentScheduled(true);
            PracticeNavigationHelper.getInstance().setIsPatientModeAppointments(false);
            PracticeNavigationHelper.getInstance().navigateToWorkflow(context, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(context).dismiss();
            SystemUtil.showFaultDialog(context);
            Log.e(context.getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void onDialogCancel() {
        super.onDialogCancel();
        new PracticeAvailableHoursDialog(context,((ScheduleAppointmentActivity)context).getResourcesToSchedule()
                .getMetadata().getLabel().getAvailableHoursBack()).show();
        dismiss();
    }
}
