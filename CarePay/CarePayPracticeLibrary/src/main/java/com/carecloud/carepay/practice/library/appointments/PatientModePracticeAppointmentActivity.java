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
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModePracticeAppointmentActivity extends BasePracticeAppointmentsActivity
        implements View.OnClickListener,
        ProvidersListAdapter.OnProviderListItemClickListener {

    private RecyclerView appointmentsRecyclerView;

    private LinearLayout noAppointmentView;

    private List<AppointmentResourcesDTO> resources;

    private AppointmentsResultModel resourcesToSchedule;

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
            appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            populateWithLabels();
        } catch (JsonSyntaxException ex) {
            showErrorNotification(null);
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
            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        }
    }

    private void logout() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getApiStartKey());

        Map<String, String> query = new HashMap<>();
        query.put("transition", "true");

        getWorkflowServiceHelper().execute(appointmentsResultModel.getMetadata()
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
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        ((TextView) findViewById(R.id.provider_logout)).setText(Label.getLabel("practice_app_logout_text"));
        ((TextView) findViewById(R.id.provider_screen_header)).setText(Label.getLabel("provider_list_header"));
        ((TextView) findViewById(R.id.provider_screen_sub_header)).setText(Label.getLabel("provider_list_sub_header"));
        ((TextView) findViewById(R.id.no_providers_message_title)).setText(Label.getLabel("no_appointments_message_title"));
        ((TextView) findViewById(R.id.no_providers_message_desc)).setText(Label.getLabel("no_appointments_message_text"));
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
            resourcesToSchedule = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);

            if (resourcesToSchedule != null && resourcesToSchedule.getPayload() != null
                    && resourcesToSchedule.getPayload().getResourcesToSchedule() != null
                    && resourcesToSchedule.getPayload().getResourcesToSchedule().size() > 0) {

                noAppointmentView.setVisibility(View.GONE);
                resources = resourcesToSchedule.getPayload()
                        .getResourcesToSchedule().get(0).getResources();

                if (resources.size() > 0) {
                    Collections.sort(resources, new Comparator<AppointmentResourcesDTO>() {
                        @Override
                        public int compare(final AppointmentResourcesDTO object1, final AppointmentResourcesDTO object2) {
                            return object1.getResource().getProvider().getName()
                                    .compareTo(object2.getResource().getProvider().getName());
                        }
                    });

                    ProvidersListAdapter appointmentsListAdapter = new ProvidersListAdapter(
                            PatientModePracticeAppointmentActivity.this, resources, resourcesToSchedule,
                            PatientModePracticeAppointmentActivity.this);
                    appointmentsRecyclerView.setAdapter(appointmentsListAdapter);

                    //Layout manager for the Recycler View
                    RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                            PatientModePracticeAppointmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
                } else {
                    showEmptyScreen();
                }
            } else {
                showEmptyScreen();
            }

            populateWithLabels();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
        }
    };

    private void showEmptyScreen() {
        findViewById(R.id.provider_screen_header).setVisibility(View.INVISIBLE);
        findViewById(R.id.provider_screen_sub_header).setVisibility(View.INVISIBLE);
        noAppointmentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProviderListItemClickListener(int position) {
        AppointmentResourcesDTO selectedResource = resources.get(position);
        onProviderSelected(selectedResource, resourcesToSchedule, getSelectedResourcesToSchedule(selectedResource));
    }

    @Override
    protected TransitionDTO getMakeAppointmentTransition() {
        return appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();
    }

    @Override
    public void onAppointmentRequestSuccess() {
        logout();
    }

    @Override
    protected LinksDTO getLinks() {
        return appointmentsResultModel.getMetadata().getLinks();
    }

    private ResourcesToScheduleDTO getSelectedResourcesToSchedule(AppointmentResourcesDTO selectedResource) {
        List<ResourcesToScheduleDTO> resourcesToScheduleDTOList = resourcesToSchedule.getPayload().getResourcesToSchedule();
        for (ResourcesToScheduleDTO resourcesToScheduleDTO : resourcesToScheduleDTOList) {
            for (AppointmentResourcesDTO appointmentResourcesDTO : resourcesToScheduleDTO.getResources()) {
                if (appointmentResourcesDTO == selectedResource) {
                    return resourcesToScheduleDTO;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //Not Implemented
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        //Not Implemented
    }
}
