package com.carecloud.carepay.practice.library.appointments;

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
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModePracticeAppointmentActivity extends BasePracticeAppointmentsActivity
        implements View.OnClickListener,
        ProvidersListAdapter.OnProviderListItemClickListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel scheduleResourcesModel;

    private LinearLayout noAppointmentView;

    private AppointmentLabelDTO labels;

    private List<AppointmentResourcesDTO> resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_patient);

        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.provider_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

        ProgressBar appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);

        noAppointmentView = (LinearLayout) findViewById(R.id.no_providers_layout);
        findViewById(R.id.provider_logout).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);

        try {
            scheduleResourcesModel = getConvertedDTO(AppointmentsResultModel.class);
            populateWithLabels();
        } catch (JsonSyntaxException ex) {
            SystemUtil.showFailureDialogMessage(this, getString(R.string.alert_title_server_error),
                    getString(R.string.alert_title_server_error));
            ex.printStackTrace();
        }

        getResourcesList();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.provider_logout) {
            logout();
        } else if (viewId == R.id.btnHome) {
            goToHome(scheduleResourcesModel.getMetadata().getTransitions().getLogout());
        }
    }

    private void logout() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getApiStartKey());

        Map<String, String> query = new HashMap<>();
        query.put("transition", "true");

        getWorkflowServiceHelper().execute(scheduleResourcesModel.getMetadata()
                .getTransitions().getLogout(), logOutCall, query, headers);
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientModePracticeAppointmentActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        labels = scheduleResourcesModel.getMetadata().getLabel();
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
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());

        AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        setPatientId(getApplicationMode().getPatientId() == null ? "" : getApplicationMode().getPatientId());
        TransitionDTO resourcesToSchedule = appointmentsResultModel.getMetadata().getLinks().getResourcesToSchedule();
        getWorkflowServiceHelper().execute(resourcesToSchedule, scheduleResourcesCallback, queryMap);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            scheduleResourcesModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);

            if (scheduleResourcesModel != null && scheduleResourcesModel.getPayload() != null
                    && scheduleResourcesModel.getPayload().getResourcesToSchedule() != null
                    && scheduleResourcesModel.getPayload().getResourcesToSchedule().size() > 0) {

                noAppointmentView.setVisibility(View.GONE);
                resources = scheduleResourcesModel.getPayload()
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
                        PatientModePracticeAppointmentActivity.this, resources, scheduleResourcesModel,
                        PatientModePracticeAppointmentActivity.this);
                appointmentsRecyclerView.setAdapter(appointmentsListAdapter);

                //Layout manager for the Recycler View
                RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                        PatientModePracticeAppointmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
            } else {
                findViewById(R.id.provider_screen_header).setVisibility(View.INVISIBLE);
                findViewById(R.id.provider_screen_sub_header).setVisibility(View.INVISIBLE);
                noAppointmentView.setVisibility(View.VISIBLE);
            }

            populateWithLabels();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    public void onProviderListItemClickListener(int position) {
        selectVisitType(resources.get(position), scheduleResourcesModel);
    }

    @Override
    protected AppointmentLabelDTO getLabels() {
        return labels;
    }

    @Override
    protected TransitionDTO getMakeAppointmentTransition() {
        return scheduleResourcesModel.getMetadata().getTransitions().getMakeAppointment();
    }

    @Override
    public void onAppointmentRequestSuccess() {
        logout();
    }

    @Override
    protected LinksDTO getLinks() {
        return scheduleResourcesModel.getMetadata().getLinks();
    }
}
