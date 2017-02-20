package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationsDTO;
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
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsSlotsDTO appointmentsSlotsDTO;
    private AppointmentAvailabilityDTO appointmentAvailabilityDTO;
    private TextView visitTypeTextView;

    /**
     * Constructor.
     * @param context activity context
     * @param cancelString String
     * @param appointmentsSlotsDTO AppointmentsSlotsDTO
     */
    public PracticeRequestAppointmentDialog(Context context, String cancelString, AppointmentsSlotsDTO appointmentsSlotsDTO
            , AppointmentAvailabilityDTO appointmentAvailabilityDTO) {

        super(context, cancelString, false);
        this.context = context;
        this.appointmentsSlotsDTO = appointmentsSlotsDTO;
        this.appointmentAvailabilityDTO = appointmentAvailabilityDTO;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onAddContentView(inflater);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_request_appointment_summary, null);
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
        SystemUtil.setGothamRoundedBookTypeface(context,requestAppointmentButton);

        DateUtil.getInstance().setDateRaw(appointmentsSlotsDTO.getStartTime());
//        CarePayTextView appointmentDateTextView = (CarePayTextView)view.findViewById(R.id.appointment_date);
//        appointmentDateTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear());
//        SystemUtil.setProximaNovaSemiboldTypeface(context,appointmentDateTextView);

        setDialogTitle(DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear());


        CarePayTextView appointmentTimeTextView = (CarePayTextView)view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setText(DateUtil.getInstance().getTime12Hour());
        SystemUtil.setGothamRoundedBoldTypeface(context,appointmentTimeTextView);

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
        AppointmentLocationsDTO location = appointmentAvailabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation();
        CarePayTextView appointmentPlaceNameTextView = (CarePayTextView)view.findViewById(R.id.provider_place_name);
        appointmentPlaceNameTextView.setText(location.getName());
        SystemUtil.setProximaNovaExtraboldTypeface(context,appointmentPlaceNameTextView);
        CarePayTextView appointmentAddressTextView = (CarePayTextView)view.findViewById(R.id.provider_place_address);
        appointmentAddressTextView.setText(location.getAddress().getPlaceAddressString());
        CarePayTextView visitTypeLabel = (CarePayTextView)view.findViewById(R.id.visitTypeLabel);
        visitTypeLabel.setText(appointmentsResultModel.getMetadata().getLabel().getVisitTypeHeading());

        visitTypeTextView = (TextView)view.findViewById(R.id.reasonTextView);
//        visitTypeTextView.setTag(reasonInputLayout);

        String visitReason = ((ScheduleAppointmentActivity)context).getSelectedVisitTypeDTO().getName();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            visitTypeTextView.setText(visitReason);
        }

//        SystemUtil.handleHintChange(visitTypeTextView, true);
//        visitTypeTextView.clearFocus();

//        visitTypeTextView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                view.setFocusable(true);
//                view.setFocusableInTouchMode(true);
//                return false;
//            }
//        });

        //setDialogTitle("");
        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);
    }

    /**
     *Click listener for request appointment button
     */
    private View.OnClickListener requestAppointmentClickListener = new View.OnClickListener() {
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
        appointmentJSONObj.addProperty("location_id", appointmentAvailabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", ((ScheduleAppointmentActivity)context).getSelectedResource().getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", ((ScheduleAppointmentActivity)context).getSelectedVisitTypeDTO().getId());
        appointmentJSONObj.addProperty("resource_id", ((ScheduleAppointmentActivity)context).getSelectedResource().getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", ((ScheduleAppointmentActivity)context).getSelectedVisitTypeDTO().getName());
        appointmentJSONObj.addProperty("comments", visitTypeTextView.getText().toString().trim());
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
            ((ScheduleAppointmentActivity) context).showAppointmentConfirmation();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(context).dismiss();
            SystemUtil.showDefaultFailureDialog(context);
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
