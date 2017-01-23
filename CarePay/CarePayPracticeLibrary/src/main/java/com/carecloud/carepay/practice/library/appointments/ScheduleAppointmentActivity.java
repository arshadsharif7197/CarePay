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
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.VisitTypeDialog;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleAppointmentActivity extends BasePracticeActivity implements View.OnClickListener,
        ProvidersListAdapter.OnProviderListItemClickListener, VisitTypeDialog.OnDialogListItemClickListener  {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel scheduleResourcesModel;

    private ProgressBar appointmentProgressBar;
    private LinearLayout noAppointmentView;

    private AppointmentResourcesDTO selectedResource;
    private VisitTypeDTO selectedVisitTypeDTO;
    private Date startDate;
    private Date endDate;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_patient);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.provider_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);

        noAppointmentView = (LinearLayout) findViewById(R.id.no_providers_layout);
        findViewById(R.id.provider_logout).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);

        getResourcesList();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.provider_logout) {
            logout();
        } else if (viewId == R.id.btnHome) {
            WorkflowServiceHelper.getInstance().execute(scheduleResourcesModel.getMetadata()
                    .getTransitions().getLogout(), homeCall);
        }
    }

    public void logout(){
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getApiStartKey());

        Map<String, String> query = new HashMap<>();
        query.put("transition", "true");

        WorkflowServiceHelper.getInstance().execute(scheduleResourcesModel.getMetadata()
                .getTransitions().getLogout(), logOutCall, query, headers);
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ScheduleAppointmentActivity.this.finish();
            PracticeNavigationHelper.getInstance().navigateToWorkflow(
                    ScheduleAppointmentActivity.this, workflowDTO);
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
            PracticeNavigationHelper.getInstance().navigateToWorkflow(
                    ScheduleAppointmentActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(ScheduleAppointmentActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        AppointmentLabelDTO labels = scheduleResourcesModel.getMetadata().getLabel();
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
    private void getResourcesList() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());

        AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        setPatientId(appointmentsResultModel.getPayload().getAppointments().get(0).getMetadata().getPatientId());
        TransitionDTO resourcesToSchedule = appointmentsResultModel.getMetadata().getLinks().getResourcesToSchedule();
        WorkflowServiceHelper.getInstance().execute(resourcesToSchedule, scheduleResourcesCallback, queryMap);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            scheduleResourcesModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);

            if (scheduleResourcesModel != null && scheduleResourcesModel.getPayload() != null
                    && scheduleResourcesModel.getPayload().getResourcesToSchedule() != null
                    && scheduleResourcesModel.getPayload().getResourcesToSchedule().size() > 0) {

                noAppointmentView.setVisibility(View.GONE);
                List<AppointmentResourcesDTO> resources = scheduleResourcesModel.getPayload()
                        .getResourcesToSchedule().get(0).getResources();

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
                        ScheduleAppointmentActivity.this, resources, scheduleResourcesModel,
                        ScheduleAppointmentActivity.this);
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

            populateWithLabels();
            appointmentProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(ScheduleAppointmentActivity.this);
            appointmentProgressBar.setVisibility(View.GONE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onProviderListItemClickListener(int position) {
        List<AppointmentResourcesDTO> resources = scheduleResourcesModel.getPayload().getResourcesToSchedule().get(0).getResources();
        selectedResource = resources.get(position);
        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(this, selectedResource, this, scheduleResourcesModel);
        visitTypeDialog.show();
    }

    /**
     * what to do with the selected visit type and provider
     * @param selectedVisitType selected visit type from dialog
     */
    public void onDialogListItemClickListener(VisitTypeDTO selectedVisitType) {

        this.selectedVisitTypeDTO = selectedVisitType;
        new PracticeAvailableHoursDialog(ScheduleAppointmentActivity.this,scheduleResourcesModel.getMetadata().getLabel().getAvailableHoursBack()).show();
    }

    public Date getStartDate(){
        return startDate;
    }

    public void setStartDate(Date date){
        this.startDate = date;
    }

    public Date getEndDate(){
        return endDate;
    }

    public void setEndDate(Date date){
        this.endDate = date;
    }

    public AppointmentsResultModel getResourcesToSchedule(){
        return scheduleResourcesModel;
    }

    public AppointmentResourcesDTO getSelectedResource(){
        return selectedResource;
    }

    public VisitTypeDTO getSelectedVisitTypeDTO(){
        return selectedVisitTypeDTO;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
