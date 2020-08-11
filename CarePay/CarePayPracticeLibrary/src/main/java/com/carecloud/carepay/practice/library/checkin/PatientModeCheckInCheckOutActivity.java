package com.carecloud.carepay.practice.library.checkin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.AppointmentsListAdapter;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.PaymentUpdateBalanceDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientModeCheckInCheckOutActivity extends BasePracticeActivity implements View.OnClickListener,
        AppointmentsListAdapter.AppointmentsAdapterStartCheckInListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;
    private List<AppointmentDTO> appointmentsItems = new ArrayList<>();

    private TextView header;
    private TextView subHeader;

    private @Defs.AppointmentNavigationTypeDef
    int appointmentNavigationType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);
        appointmentNavigationType = getApplicationPreferences().getAppointmentNavigationOption();

        header = findViewById(R.id.titleSelectappointmentcheckin);
        subHeader = findViewById(R.id.titleSelectappointmentsubheader);
        TextView noApptHeader = findViewById(R.id.no_apt_message_title);

        if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
            header.setText(Label.getLabel("practice_app_appointment_checkout_heading"));
            noApptHeader.setText(Label.getLabel("no_appointments_checkout_title"));
        }

        appointmentsRecyclerView = findViewById(R.id.appointments_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);

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
            PatientModeCheckInCheckOutActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findViewById(R.id.logoutTextview).setEnabled(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * Method to update appointment list to UI
     */
    private void getAppointmentList() {
        getAppointmentItems();
        if (appointmentsResultModel != null && !appointmentsItems.isEmpty()) {

            if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT && appointmentsItems.size() == 1) {
                AppointmentDTO appointmentDTO = appointmentsItems.get(0);
                if (appointmentDTO.getPayload().canCheckOut()) {
                    onStartCheckOut(appointmentsItems.get(0));
                    return;
                }
            }

            findViewById(R.id.no_appointment_layout).setVisibility(View.GONE);

            UserPracticeDTO practiceInfo = appointmentsResultModel.getPayload().getUserPractices().get(0);

            AppointmentsListAdapter appointmentsListAdapter = new AppointmentsListAdapter(
                    PatientModeCheckInCheckOutActivity.this, appointmentsItems,
                    appointmentsResultModel, appointmentNavigationType, practiceInfo);
            appointmentsRecyclerView.setAdapter(appointmentsListAdapter);
            appointmentsListAdapter.setListener(this);

            //Layout manager for the Recycler View
            RecyclerView.LayoutManager appointmentsLayoutManager = new LinearLayoutManager(
                    PatientModeCheckInCheckOutActivity.this, LinearLayoutManager.HORIZONTAL, false);
            appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);

        } else {
            header.setVisibility(View.INVISIBLE);
            subHeader.setVisibility(View.INVISIBLE);
            findViewById(R.id.no_appointment_layout).setVisibility(View.VISIBLE);

            View scheduleButton = findViewById(R.id.schedule_appt_button);
            scheduleButton.setOnClickListener(view -> {
                //todo go to Appointment screen
                getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_APPOINTMENT);
                WorkflowDTO workflowDTO = getConvertedDTO(WorkflowDTO.class);
                PracticeNavigationHelper.navigateToWorkflow(PatientModeCheckInCheckOutActivity.this, workflowDTO);
            });
        }
    }

    private List<AppointmentDTO> getAppointmentItems() {
        if (appointmentsResultModel != null) {
            if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
                //need to get just those appointments that are ready for checkout
                for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
                    if (appointmentDTO.getPayload().canCheckOut()) {
                        appointmentsItems.add(appointmentDTO);
                    }
                }
            } else {
                appointmentsItems = appointmentsResultModel.getPayload().getAppointments();
            }
        }
        return appointmentsItems;
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

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            PaymentQueuedDialogFragment dialogFragment = new PaymentQueuedDialogFragment();
            DialogInterface.OnDismissListener dismissListener = dialog -> goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
            dialogFragment.setOnDismissListener(dismissListener);
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getName());

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
        queries.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingIn();
        getWorkflowServiceHelper().execute(transitionDTO, getStartProcessCallback(selectedAppointment), queries, header);
    }

    @Override
    public void onStartCheckOut(AppointmentDTO selectedAppointment) {
        getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);

        Map<String, String> queries = new HashMap<>();
        queries.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());
        queries.put("patient_id", selectedAppointment.getMetadata().getPatientId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingOut();
        getWorkflowServiceHelper().execute(transitionDTO, getStartProcessCallback(selectedAppointment), queries, header);
    }

    private WorkflowServiceCallback getStartProcessCallback(final AppointmentDTO selectedAppointment) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Bundle info = new Bundle();
                info.putString(CarePayConstants.APPOINTMENT_ID, selectedAppointment.getPayload().getId());
                PracticeNavigationHelper.navigateToWorkflow(PatientModeCheckInCheckOutActivity.this,
                        workflowDTO, true, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, info);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return appointmentsResultModel.getMetadata().getTransitions().getLogout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CarePayConstants.DOB_VERIFICATION_REQUEST:
            case CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        //need to do this delayed because onActivityResult executes before onResume
                        // and thus it does not show the progress dialog
                        goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
                    }, 300);
                }
            }
            break;
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}