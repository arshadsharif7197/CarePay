package com.carecloud.carepay.patient.appointments.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.IdsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsActivity extends MenuPatientActivity implements PatientAppointmentNavigationCallback {

    private AppointmentsResultModel appointmentsResultModel;
    private ResourcesPracticeDTO selectedResourcesPracticeDTO;
    private AppointmentResourcesDTO selectedAppointmentResourcesDTO;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentDTO appointmentDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        displayToolbar(true, null);

        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        // get handler to navigation drawer's user id text view
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        if (appointmentsResultModel.getPayload() != null) {
            try {
                List<IdsDTO> practicePatientIds = appointmentsResultModel.getPayload().getPracticePatientIds();
                if (practicePatientIds.isEmpty()) {
                    IdsDTO[] practicePatientIdArray = getApplicationPreferences().getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS, IdsDTO[].class);
                    practicePatientIds = Arrays.asList(practicePatientIdArray);
                } else {
                    getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.KEY_PRACTICE_PATIENT_IDS, appointmentsResultModel.getPayload().getPracticePatientIds());
                }
                practiceId = practicePatientIds.get(0).getPracticeId();
                practiceMgmt = practicePatientIds.get(0).getPracticeManagement();
                patientId = practicePatientIds.get(0).getPatientId();
                prefix = practicePatientIds.get(0).getPrefix();
                userId = practicePatientIds.get(0).getUserId();
                getApplicationPreferences().setPatientId(patientId);
                getApplicationPreferences().setPracticeManagement(practiceMgmt);
                getApplicationPreferences().setPracticeId(practiceId);
                getApplicationPreferences().setUserId(userId);
                getApplicationPreferences().setPrefix(prefix);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        }

        setTransitionBalance(appointmentsResultModel.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        setTransitionProfile(appointmentsResultModel.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(appointmentsResultModel.getMetadata().getLinks().getAppointments());
        setTransitionNotifications(appointmentsResultModel.getMetadata().getLinks().getNotifications());

        String userImageUrl = appointmentsResultModel.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getProfilePhoto();
        if (userImageUrl != null) {
            getApplicationPreferences().setUserPhotoUrl(userImageUrl);
        }

        inflateDrawer();
        gotoAppointmentFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_APPOINTMENTS).setChecked(true);
    }

    private void gotoAppointmentFragment() {
        AppointmentsListFragment appointmentsListFragment = new AppointmentsListFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, gson.toJson(appointmentsResultModel));
        appointmentsListFragment.setArguments(bundle);

        navigateToFragment(appointmentsListFragment, false);
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
                displayToolbar(true, null);
            }
        } else {

            if (!HttpConstants.isUseUnifiedAuth() && getAppAuthorizationHelper().getPool().getUser() != null) {
                getAppAuthorizationHelper().getPool().getUser().signOut();
                getAppAuthorizationHelper().setUser(null);
            }
            // finish the app
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    private void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    @Override
    public void newAppointment() {
        Bundle args = new Bundle();
        Gson gson = new Gson();

        args.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentsResultModel));

        ChooseProviderFragment chooseProviderFragment = new ChooseProviderFragment();
        chooseProviderFragment.setArguments(args);

        navigateToFragment(chooseProviderFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        selectedResourcesPracticeDTO = appointmentsResultModel.getPayload().getResourcesToSchedule().get(0).getPractice();
        String tag = VisitTypeFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO,
                appointmentsResultModel);
        dialog.show(ft, tag);
    }

    @Override
    public void selectTime(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO,
                           AppointmentsResultModel appointmentsResultModel) {
        selectedAppointmentResourcesDTO = appointmentResourcesDTO;
        selectedVisitTypeDTO = visitTypeDTO;
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel,
                        appointmentResourcesDTO.getResource(), null, null, visitTypeDTO);
        navigateToFragment(availableHoursFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void selectTime(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource,
                           AppointmentsResultModel appointmentsResultModel) {
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResource,
                        startDate, endDate, visitTypeDTO);
        navigateToFragment(availableHoursFragment, false);
        displayToolbar(false, null);
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));

        AppointmentDateRangeFragment appointmentDateRangeFragment = new AppointmentDateRangeFragment();
        appointmentDateRangeFragment.setArguments(bundle);

        navigateToFragment(appointmentDateRangeFragment, false);
        displayToolbar(false, null);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        this.appointmentDTO = appointmentDTO;
        selectedVisitTypeDTO = appointmentDTO.getPayload().getVisitType();
        AppointmentResourcesItemDTO resourcesItemDTO = new AppointmentResourcesItemDTO();
        resourcesItemDTO.setId(appointmentDTO.getPayload().getResourceId());
        resourcesItemDTO.setProvider(appointmentDTO.getPayload().getProvider());

        selectedAppointmentResourcesDTO = new AppointmentResourcesDTO();
        selectedAppointmentResourcesDTO.setResource(resourcesItemDTO);

        ResourcesToScheduleDTO resourcesToSchedule = new ResourcesToScheduleDTO();
        resourcesToSchedule.getPractice().setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        resourcesToSchedule.getPractice().setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentsResultModel.getPayload().getResourcesToSchedule().add(resourcesToSchedule);

        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment.newInstance(appointmentsResultModel, appointmentDTO);

        navigateToFragment(availableHoursFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void confirmAppointment(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        this.availabilityDTO = availabilityDTO;
        ProviderDTO providersDTO;
        if (selectedAppointmentResourcesDTO != null) {
            providersDTO = selectedAppointmentResourcesDTO.getResource().getProvider();
        } else {
            providersDTO = appointmentDTO.getPayload().getProvider();
        }

        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(appointmentsSlot.getStartTime());
        payloadDTO.setEndTime(appointmentsSlot.getEndTime());
        payloadDTO.setLocation(appointmentsSlot.getLocation());
        payloadDTO.setVisitReasonId(selectedVisitTypeDTO.getId());
        payloadDTO.setChiefComplaint(selectedVisitTypeDTO.getName());

        payloadDTO.setProvider(providersDTO);
        payloadDTO.setProviderId(providersDTO.getId().toString());
//        payloadDTO.setResourceId(selectedAppointmentResourcesDTO.getResource().getId());

        PatientModel patientDTO = new PatientModel();
        patientDTO.setPatientId(patientId);
        payloadDTO.setPatient(patientDTO);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);

        final RequestAppointmentDialog requestAppointmentDialog = new RequestAppointmentDialog(getContext(),
                appointmentDTO, appointmentsSlot);
        requestAppointmentDialog.show();
    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", getApplicationPreferences().getPracticeManagement());
        queryMap.put("practice_id", getApplicationPreferences().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", patientId);
        appointmentJSONObj.addProperty("start_time", appointmentSlot.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentSlot.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", appointmentSlot.getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", selectedAppointmentResourcesDTO.getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", selectedVisitTypeDTO.getId());
        appointmentJSONObj.addProperty("resource_id", selectedAppointmentResourcesDTO.getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", selectedVisitTypeDTO.getName());
        appointmentJSONObj.addProperty("comments", comments);
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();

        getWorkflowServiceHelper().execute(transitionDTO, getMakeAppointmentCallback, makeAppointmentJSONObj.toString(), queryMap);
    }

    @Override
    public void onAppointmentUnconfirmed() {

    }

    @Override
    public void onAppointmentRequestSuccess() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStackImmediate();
        }
        displayToolbar(true, null);

        refreshAppointments();
        showAppointmentConfirmation();
    }

    private void refreshAppointments(){
        AppointmentsListFragment fragment = (AppointmentsListFragment)
                getSupportFragmentManager().findFragmentById(R.id.container_main);

        if (fragment != null) {
            fragment.refreshAppointmentList();
        }

    }

    private void showAppointmentConfirmation() {
        String appointmentRequestSuccessMessage = "";

        if (appointmentsResultModel != null) {
            appointmentRequestSuccessMessage = appointmentsResultModel.getMetadata().getLabel()
                    .getAppointmentRequestSuccessMessage();
        }

        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onAppointmentRequestSuccess();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
            onAppointmentUnconfirmed();
        }
    };

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO) {
        new CancelReasonAppointmentDialog(getContext(), appointmentDTO, appointmentsResultModel, new CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener() {
            @Override
            public void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment) {
                onCancelAppointment(appointmentDTO, cancellationReason, cancellationReasonComment);
            }
        }).show();
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        DataDTO data = appointmentsResultModel.getMetadata().getTransitions().getCancel().getData();
        JsonObject postBodyObj = new JsonObject();
        if (!StringUtil.isNullOrEmpty(cancellationReasonComment)) {
            postBodyObj.addProperty(data.getCancellationComments().getName(), cancellationReasonComment);
        }
        if (cancellationReason != -1) {
            postBodyObj.addProperty(data.getCancellationReasonId().getName(), cancellationReason);
        }

        String body = postBodyObj.toString();

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCancel();
        getWorkflowServiceHelper().execute(transitionDTO, cancelCallback, body, queries);
    }

    @Override
    public void onCheckInStarted(AppointmentDTO appointmentDTO) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingIn();
        getWorkflowServiceHelper().execute(transitionDTO, checkinCallback, queries, header);
    }

    @Override
    public void onCheckOutStarted(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onCheckInOfficeStarted(AppointmentDTO appointmentDTO) {
        new QrCodeViewDialog(getContext(), appointmentDTO, appointmentsResultModel.getMetadata(), new QrCodeViewDialog.QRCodeViewDialogListener() {
            @Override
            public void onGenerateQRCodeError(String errorMessage) {
                showErrorNotification(null);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), errorMessage);
            }
        }).show();
    }

    @Override
    public void onRescheduleAppointment(AppointmentDTO appointmentDTO) {
        rescheduleAppointment(appointmentDTO);
    }

    private WorkflowServiceCallback checkinCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback cancelCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("appointment_cancellation_success_message_HTML"));
            refreshAppointments();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    public void displayAppointmentDetails(AppointmentDTO appointmentDTO) {
        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointmentDTO);
        detailDialog.show(getSupportFragmentManager(), detailDialog.getClass().getName());
    }
}
