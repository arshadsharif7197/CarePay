package com.carecloud.carepay.practice.library.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.AppointmentsListAdapter;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.PaymentUpdateBalanceDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModePracticeCheckInActivity extends BasePracticeActivity implements View.OnClickListener,
        AppointmentsListAdapter.AppointmentsAdapterStartCheckInListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;
    private List<AppointmentDTO> appointmentsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.appointments_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

        ProgressBar appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        findViewById(R.id.logoutTextview).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);
        try {
            appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            getAppointmentList();
        } catch (JsonSyntaxException ex) {
            showErrorNotification(null);
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE: {
                if (resultCode == CarePayConstants.HOME_PRESSED) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //need to do this delayed because onActivityResult executes before onResume
                            // and thus it does not show the progress dialog
                            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
                        }
                    }, 300);
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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
            getWorkflowServiceHelper().execute(appointmentsResultModel.getMetadata()
                    .getTransitions().getLogout(), logOutCall, query, headers);
        } else if (viewId == R.id.btnHome) {
            findViewById(R.id.btnHome).setEnabled(false);
            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        }
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            findViewById(R.id.logoutTextview).setEnabled(true);
            PatientModePracticeCheckInActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findViewById(R.id.logoutTextview).setEnabled(true);
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * Method to update appointment list to UI
     */
    private void getAppointmentList() {
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                && appointmentsResultModel.getPayload().getAppointments() != null
                && !appointmentsResultModel.getPayload().getAppointments().isEmpty()) {

            findViewById(R.id.no_appointment_layout).setVisibility(View.GONE);
            appointmentsItems = appointmentsResultModel.getPayload().getAppointments();

            AppointmentsListAdapter appointmentsListAdapter = new AppointmentsListAdapter(
                    PatientModePracticeCheckInActivity.this, appointmentsItems, appointmentsResultModel);
            appointmentsRecyclerView.setAdapter(appointmentsListAdapter);
            appointmentsListAdapter.setListener(this);

            //Layout manager for the Recycler View
            RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                    PatientModePracticeCheckInActivity.this, LinearLayoutManager.HORIZONTAL, false);
            appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
        } else {
            findViewById(R.id.titleSelectappointmentsubheader).setVisibility(View.INVISIBLE);
            findViewById(R.id.titleSelectappointmentcheckin).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_appointment_layout).setVisibility(View.VISIBLE);

            View scheduleButton = findViewById(R.id.schedule_appt_button);
            scheduleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo go to Appointment screen
                    getApplicationPreferences().setNavigateToAppointments(true);
                    WorkflowDTO workflowDTO = getConvertedDTO(WorkflowDTO.class);
                    PracticeNavigationHelper.navigateToWorkflow(PatientModePracticeCheckInActivity.this, workflowDTO);
                }
            });
        }
    }

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
                    PaymentUpdateBalanceDTO updateBalanceDTO = gson.fromJson(jsonPayload, PaymentUpdateBalanceDTO.class);
                    updateAppointments(updateBalanceDTO.getUpdateAppointments());
                }
                break;
            }
            default:
                //nothing
                return;
        }
    }

    private void updateAppointments(List<AppointmentDTO> updateAppointments) {
        appointmentsItems = updateAppointments;
        AppointmentsListAdapter adapter = (AppointmentsListAdapter) appointmentsRecyclerView.getAdapter();
        adapter.setList(appointmentsItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStartCheckIn(AppointmentDTO selectedAppointment) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
        queries.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
        queries.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingIn();
        getWorkflowServiceHelper().execute(transitionDTO, transitionToDemographicsVerifyCallback, queries, header);
    }

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(PatientModePracticeCheckInActivity.this, workflowDTO, true, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}