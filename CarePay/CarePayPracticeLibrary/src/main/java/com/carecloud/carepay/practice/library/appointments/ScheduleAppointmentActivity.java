package com.carecloud.carepay.practice.library.appointments;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.VisitTypeDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScheduleAppointmentActivity extends BasePracticeActivity implements View.OnClickListener,
        ProvidersListAdapter.OnProviderListItemClickListener,
        VisitTypeDialog.OnDialogListItemClickListener,
        PracticeAvailableHoursDialog.PracticeAvailableHoursDialogListener,
        DateRangePickerDialog.DateRangePickerDialogListener, PracticeRequestAppointmentDialog.PracticeRequestAppointmentDialogListener {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentsResultModel scheduleResourcesModel;

    private LinearLayout noAppointmentView;

    private AppointmentResourcesDTO selectedResource;
    private AppointmentAvailabilityDTO availabilityDTO;
    private AppointmentsSlotsDTO appointmentsSlotsDTO;
    private AppointmentLabelDTO labels;
    private VisitTypeDTO selectedVisitTypeDTO;
    private Date startDate;
    private Date endDate;
    private String patientId;

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

        getResourcesList();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.provider_logout) {
            logout();
        } else if (viewId == R.id.btnHome) {
            getWorkflowServiceHelper().execute(scheduleResourcesModel.getMetadata()
                    .getTransitions().getLogout(), homeCall);
        }
    }

    public void showAppointmentConfirmation() {
        if (scheduleResourcesModel != null && isVisible()) {
            String appointmentRequestSuccessMessage = scheduleResourcesModel.getMetadata()
                    .getLabel().getAppointmentRequestSuccessMessage();

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("")
                    .setContentText(appointmentRequestSuccessMessage)
                    .setConfirmText(getString(R.string.alert_ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            logout();
                        }
                    })
                    .show();
        } else {
            logout();
        }
    }

    private void logout(){
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
            ScheduleAppointmentActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(ScheduleAppointmentActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            ScheduleAppointmentActivity.this.finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(ScheduleAppointmentActivity.this);
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
        setPatientId(getApplicationMode().getPatientId()==null?"":getApplicationMode().getPatientId());
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
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(ScheduleAppointmentActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onProviderListItemClickListener(int position) {
        selectedResource = resources.get(position);
        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(this, selectedResource, this, scheduleResourcesModel);
        visitTypeDialog.show();
    }

    /**
     * what to do with the selected visit type and provider
     * @param visitTypeDTO selected visit type from dialog
     */
    public void onDialogListItemClickListener(VisitTypeDTO visitTypeDTO) {
        this.selectedVisitTypeDTO = visitTypeDTO;

        String cancelString = labels.getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, visitTypeDTO, this).show();
    }

    @Override
    public void onAppointmentTimeSelected(AppointmentAvailabilityDTO availabilityDTO, AppointmentsSlotsDTO appointmentsSlotsDTO) {
        this.availabilityDTO = availabilityDTO;
        this.appointmentsSlotsDTO = appointmentsSlotsDTO;
        // Call Request appointment Summary dialog from here
        String cancelString = labels.getAvailableHoursBack();
        new PracticeRequestAppointmentDialog(this, cancelString, labels, appointmentsSlotsDTO, availabilityDTO, selectedVisitTypeDTO, this).show();
    }

    @Override
    public void onDateRangeTapped() {
        String tag = DateRangePickerDialog.class.getSimpleName();

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                labels.getPickDateHeading(),
                labels.getDatepickerCancelOption(),
                labels.getTodayDateOption(),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this
        );

        dialog.show(ft, tag);
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        String cancelString = labels.getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, selectedVisitTypeDTO, this, startDate, endDate).show();
    }

    public AppointmentsResultModel getResourcesToSchedule(){
        return scheduleResourcesModel;
    }

    public AppointmentResourcesDTO getSelectedResource(){
        return selectedResource;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public void onAppointmentRequested(String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", getPatientId());
        appointmentJSONObj.addProperty("start_time", appointmentsSlotsDTO.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentsSlotsDTO.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", selectedResource.getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", selectedVisitTypeDTO.getId());
        appointmentJSONObj.addProperty("resource_id", selectedResource.getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", selectedVisitTypeDTO.getName());
        appointmentJSONObj.addProperty("comments", comments);
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = scheduleResourcesModel.getMetadata().getTransitions().getMakeAppointment();

        getWorkflowServiceHelper().execute(transitionDTO, getMakeAppointmentCallback,makeAppointmentJSONObj
                .toString(), queryMap);
    }

    @Override
    public void onAppointmentCancelled() {
        onDateRangeCancelled();
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            showAppointmentConfirmation();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
