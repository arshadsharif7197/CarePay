package com.carecloud.carepay.practice.library.appointments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.AppointmentsListAdapter;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsActivity extends BasePracticeActivity implements View.OnClickListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;
    private ProgressBar appointmentProgressBar;
    private List<AppointmentDTO> appointmentsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.appointments_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        findViewById(R.id.logoutTextview).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);
        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        try {
            appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            getAppointmentList();
        } catch (JsonSyntaxException ex) {
            SystemUtil.showFailureDialogMessage(this, getString(R.string.alert_title_server_error),
                    getString(R.string.alert_title_server_error));
            ex.printStackTrace();
        }
    }

    private String getToday(String appointmentRawDate) {
        // Current date
        String currentDate = DateUtil.getInstance().setToCurrent().toStringWithFormatMmDashDdDashYyyy();
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).toStringWithFormatMmDashDdDashYyyy();
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String strDay;
        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            strDay = CarePayConstants.DAY_UPCOMING;

        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            strDay = CarePayConstants.DAY_TODAY;
        } else {
            strDay = CarePayConstants.DAY_TODAY;
        }

        return strDay;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.logoutTextview) {
            findViewById(R.id.logoutTextview).setEnabled(false);
            Map<String, String> query = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            query.put("transition", "true");
            WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata()
                    .getTransitions().getLogout(), logOutCall, query, headers);
        } else if (viewId == R.id.btnHome) {
            findViewById(R.id.btnHome).setEnabled(false);
            WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata()
                    .getTransitions().getLogout(), homeCall);
        }
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findViewById(R.id.logoutTextview).setEnabled(true);
            AppointmentsActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findViewById(R.id.logoutTextview).setEnabled(true);
            SystemUtil.showDefaultFailureDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findViewById(R.id.btnHome).setEnabled(true);
            AppointmentsActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            findViewById(R.id.btnHome).setEnabled(false);
            SystemUtil.showDefaultFailureDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        AppointmentLabelDTO labels = appointmentsResultModel.getMetadata().getLabel();
        if (labels != null) {
            ((TextView) findViewById(R.id.logoutTextview)).setText(labels.getAppointmentsBtnLogout());
            ((TextView) findViewById(R.id.titleSelectappointmentsubheader)).setText(labels.getAppointmentsSubHeading());
            ((TextView) findViewById(R.id.titleSelectappointmentcheckin)).setText(labels.getAppointmentsMainHeading());
            ((TextView) findViewById(R.id.no_apt_message_desc)).setText(labels.getNoAppointmentsMessageText());
            ((TextView) findViewById(R.id.no_apt_message_title)).setText(labels.getNoAppointmentsMessageTitle());
        }
    }

    /**
     * Method to update appointment list to UI
     */
    private void getAppointmentList() {
        populateWithLabels();
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                && appointmentsResultModel.getPayload().getAppointments() != null
                && appointmentsResultModel.getPayload().getAppointments().size() > 0) {

            findViewById(R.id.no_appointment_layout).setVisibility(View.GONE);
            appointmentsItems = appointmentsResultModel.getPayload().getAppointments();

//            List<AppointmentDTO> appointmentListWithToday = new ArrayList<>();
//            for (AppointmentDTO appointmentDTO : appointmentsItems) {
//                String title = getToday(appointmentDTO.getPayload().getStartTime());
//                if (title.equalsIgnoreCase(CarePayConstants.DAY_TODAY)) {
//                    appointmentListWithToday.add(appointmentDTO);
//                }
//            }

            AppointmentsListAdapter appointmentsListAdapter = new AppointmentsListAdapter(
                    AppointmentsActivity.this, appointmentsItems, appointmentsResultModel);
            appointmentsRecyclerView.setAdapter(appointmentsListAdapter);

            //Layout manager for the Recycler View
            RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                    AppointmentsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
        } else {
            findViewById(R.id.titleSelectappointmentsubheader).setVisibility(View.INVISIBLE);
            findViewById(R.id.titleSelectappointmentcheckin).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_appointment_layout).setVisibility(View.VISIBLE);
        }
    }

    private void refreshAppointmentList(){

        if (appointmentsItems != null) {
            appointmentsItems.clear();
        }

        // API call to fetch latest appointments
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getAppointments();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, pageRefreshCallback);
    }

    WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            appointmentProgressBar.setVisibility(View.GONE);
            if (appointmentsResultModel != null) {
                Gson gson = new Gson();
                appointmentsResultModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                getAppointmentList();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            appointmentProgressBar.setVisibility(View.GONE);
            SystemUtil.showDefaultFailureDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}
