package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckInCompletedDialogFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckCompleteInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 8/06/17.
 */

public class CompleteCheckActivity extends BasePracticeActivity implements CheckCompleteInterface {

    private DTO dto;
    private AppointmentsResultModel appointmentsResultModel;
    private WorkflowDTO workflowDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_check);
        Gson gson = new Gson();
        Bundle extra = getIntent().getBundleExtra(CarePayConstants.EXTRA_BUNDLE);
        boolean hasPayment = extra.getBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, false);
        boolean isCash = extra.getBoolean(CarePayConstants.EXTRA_PAYMENT_CASH, false);
        boolean isAdHocForms = extra.getBoolean(CarePayConstants.ADHOC_FORMS, false);
        boolean isCheckOut = extra.getBoolean("isCheckOut", false);
        if (getWorkflowDto() != null) {
            String workflowString = getWorkflowDto().toString();
            AppointmentDTO appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, extra);
            if (hasPayment || isCash) {
                if (isCheckOut) {
                    appointmentDTO.getPayload().getAppointmentStatus().setName("Checked-Out");
                } else if (appointmentDTO.getPayload().getAppointmentStatus().getName().toLowerCase()
                        .equals("Pending".toLowerCase())) {
                    appointmentDTO.getPayload().getAppointmentStatus().setName("Checked-In");
                }
                dto = gson.fromJson(workflowString, PaymentsModel.class);
            } else {
                dto = gson.fromJson(workflowString, AppointmentsResultModel.class);
            }
            String appointmentTransitionsWorkflow = extra.getString(CarePayConstants.EXTRA_APPOINTMENT_TRANSITIONS);
            if (appointmentTransitionsWorkflow != null) {
                appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class,
                        appointmentTransitionsWorkflow);
            }

            replaceFragment(CheckInCompletedDialogFragment.newInstance(appointmentDTO, extra), false);
        }
    }

    private WorkflowDTO getWorkflowDto(){
        if(workflowDTO == null) {
            Bundle extra = getIntent().getBundleExtra(CarePayConstants.EXTRA_BUNDLE);
            if (extra != null) {
                long id = extra.getLong(CarePayConstants.EXTRA_WORKFLOW);
                workflowDTO = retrieveStoredWorkflow(id);
            }
        }
        return workflowDTO;
    }

    @Override
    public void onBackPressed(){
        //kill this action on this activity as it leads to instability in the app
    }

    @Override
    public DTO getDto() {
        return dto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void logout() {
        if (NavigationStateConstants.PATIENT_HOME.equals(getWorkflowDto().getState())) {
            navigateToWorkflow(getWorkflowDto());
        } else {
            if (appointmentsResultModel == null) {
                appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getWorkflowDto());
            }
            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        }
    }

    @Override
    public void showConfirmationPinDialog() {
        if (appointmentsResultModel == null) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getWorkflowDto());
        }
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(this,
                appointmentsResultModel.getMetadata().getLinks().getPinpad(), false);
        confirmationPinDialog.show();
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getPracticeMode();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, patientHomeCallback, query);
    }

    WorkflowServiceCallback patientHomeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowDTO retrieveStoredWorkflow(long id) {
        WorkFlowRecord workFlowRecord = WorkFlowRecord.findById(WorkFlowRecord.class, id);
        if (workFlowRecord != null) {
            return new WorkflowDTO(workFlowRecord);
        }
        return null;
    }
}
