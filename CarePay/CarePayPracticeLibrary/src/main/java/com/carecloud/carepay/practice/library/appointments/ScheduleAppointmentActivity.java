package com.carecloud.carepay.practice.library.appointments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.ProvidersListAdapter;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonSyntaxException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleAppointmentActivity extends BasePracticeActivity implements View.OnClickListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;

    private LinearLayout noAppointmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_patient);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.provider_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

        ProgressBar appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);

        noAppointmentView = (LinearLayout) findViewById(R.id.no_providers_layout);
        findViewById(R.id.provider_logout).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);

        try {
            appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            getAppointmentList();
        } catch (JsonSyntaxException ex) {
            SystemUtil.showDialogMessage(this, getString(R.string.alert_title_server_error),
                    getString(R.string.alert_title_server_error));
            ex.printStackTrace();
        }
    }

    private String getToday(String appointmentRawDate) {
        // Current date
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
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
            Map<String, String> query = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            query.put("transition", "true");
            WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata()
                    .getTransitions().getLogout(), logOutCall, query, headers);
        } else if (viewId == R.id.btnHome) {
            //WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata().getTransitions().getAuthenticate(), homeCall);
        }
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ScheduleAppointmentActivity.this.finish();
            PracticeNavigationHelper.getInstance().navigateToWorkflow(ScheduleAppointmentActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(ScheduleAppointmentActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ScheduleAppointmentActivity.this.finish();
            PracticeNavigationHelper.getInstance().navigateToWorkflow(ScheduleAppointmentActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(ScheduleAppointmentActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        AppointmentLabelDTO labels = appointmentsResultModel.getMetadata().getLabel();
        if (labels != null) {
            ((TextView) findViewById(R.id.provider_logout)).setText(labels.getAppointmentsBtnLogout());
            ((TextView) findViewById(R.id.provider_screen_header)).setText(labels.getProviderListHeader());
            ((TextView) findViewById(R.id.provider_screen_sub_header)).setText(labels.getProviderListSubHeader());
            ((TextView) findViewById(R.id.no_providers_message_title)).setText(labels.getNoAppointmentsMessageTitle());
            ((TextView) findViewById(R.id.no_providers_message_desc)).setText(labels.getNoAppointmentsMessageText());
        }
    }

    /**
     * Method to update appointment list to UI
     */
    private void getAppointmentList() {
        populateWithLabels();
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                && appointmentsResultModel.getPayload().getResources() != null
                && appointmentsResultModel.getPayload().getResources().size() > 0) {

            noAppointmentView.setVisibility(View.GONE);
            List<AppointmentResourcesDTO> resources = appointmentsResultModel.getPayload().getResources();

            if (resources.size() > 0) {
                Collections.sort(resources, new Comparator<AppointmentResourcesDTO>() {
                    @Override
                    public int compare(final AppointmentResourcesDTO object1, final AppointmentResourcesDTO object2) {
                        return object1.getResource().getProvider().getName()
                                .compareTo(object2.getResource().getProvider().getName());
                    }
                });
            }

            ProvidersListAdapter appointmentsListAdapter = new ProvidersListAdapter(
                    ScheduleAppointmentActivity.this, resources, appointmentsResultModel);
            appointmentsRecyclerView.setAdapter(appointmentsListAdapter);

            //Layout manager for the Recycler View
            RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                    ScheduleAppointmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
            appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
        } else {
            findViewById(R.id.provider_screen_header).setVisibility(View.INVISIBLE);
            findViewById(R.id.provider_screen_sub_header).setVisibility(View.INVISIBLE);
            noAppointmentView.setVisibility(View.VISIBLE);
        }
    }
}
