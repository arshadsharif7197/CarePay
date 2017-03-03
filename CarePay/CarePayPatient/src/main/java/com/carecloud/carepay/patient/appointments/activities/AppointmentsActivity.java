package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.AppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.activities.NewReviewDemographicsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.IdsDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentNavigationCallback {

    private static final String LOG_TAG = AppointmentsActivity.class.getSimpleName();

    public static AppointmentDTO model;
    private AppointmentsResultModel appointmentsDTO;
    private AppointmentDTO appointmentDTO;

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.getInstance(AppointmentsActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        // get handler to navigation drawer's user id text view
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        appointmentsDTO = getConvertedDTO(AppointmentsResultModel.class);

        if (appointmentsDTO.getPayload() != null ){
            try{
                IdsDTO idsDTO = appointmentsDTO.getPayload().getPractice_patient_ids().get(0);
                practiceId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPracticeId();
                practiceMgmt = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPracticeManagement();
                patientId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPatientId();
                prefix = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPrefix();
                userId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getUserId();
                getApplicationPreferences().setPatientId(patientId);
                getApplicationPreferences().setPracticeManagement(practiceMgmt);
                getApplicationPreferences().setPracticeId(practiceId);
                getApplicationPreferences().setUserId(userId);
                getApplicationPreferences().setPrefix(prefix);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        }

      /*  if (appointmentsDTO.getPayload() != null && appointmentsDTO.getPayload().getAppointments() != null
                && appointmentsDTO.getPayload().getAppointments().size() > 0) {

            appointmentDTO = appointmentsDTO.getPayload().getAppointments().get(0);
            practiceId = appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId();
            practiceMgmt = appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt();
            patientId = appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPatientId();
        }*/

        setTransitionBalance(appointmentsDTO.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(appointmentsDTO.getMetadata().getTransitions().getLogout());
        setTransitionProfile(appointmentsDTO.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(appointmentsDTO.getMetadata().getLinks().getAppointments());

        inflateDrawer();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_APPOINTMENTS).setChecked(true);
        gotoAppointmentFragment();
    }

    private void gotoAppointmentFragment() {
        Intent intent = getIntent();
        appointmentDTO = (AppointmentDTO) intent.getSerializableExtra(
                CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);

        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment appointmentsListFragment = (AppointmentsListFragment)
                fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (appointmentsListFragment == null) {
            appointmentsListFragment = new AppointmentsListFragment();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String appointmentsDTOString = gson.toJson(appointmentsDTO);
//            bundle.putSerializable(CarePayConstants.APPOINTMENT_INFO_BUNDLE, appointmentsDTO);
            bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, appointmentsDTOString);

            String appointmentDTOString = gson.toJson(appointmentDTO);
            bundle.putString(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, appointmentDTOString);
//            bundle.putSerializable(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, appointmentDTO);
            appointmentsListFragment.setArguments(bundle);
        }

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container_main, appointmentsListFragment, appointmentsListFragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (getCognitoAppHelper().getPool().getUser() != null) {
            getCognitoAppHelper().getPool().getUser().signOut();
            getCognitoAppHelper().setUser(null);
        }
        // finish the app
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    private void launchDemographics(DemographicDTO demographicDTO) {
        // do to Demographics
//        Intent intent = new Intent(getApplicationContext(), DemographicReviewActivity.class);
        Intent intent = new Intent(getApplicationContext(), NewReviewDemographicsActivity.class);
        if (demographicDTO != null) {
            // pass the object into the gson
            Gson gson = new Gson();
            String dtostring = gson.toJson(demographicDTO, DemographicDTO.class);
            intent.putExtra("demographics_model", dtostring);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        Changed for SHMRK-1715
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.action_launch_demogr_review) {
//            Map<String, String> queries = new HashMap<>();
//            queries.put("practice_mgmt", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
//            queries.put("practice_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
//            queries.put("appointment_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getAppointmentId());
//
//            Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
//            header.put("transition", "true");
//            getWorkflowServiceHelper().execute(appointmentsDTO.getMetadata().getTransitions().getCheckingIn(),
//                    transitionToDemographicsVerifyCallback, queries, header);
//        }

        return super.onOptionsItemSelected(item);
    }

    public void setAppointmentModel(AppointmentDTO model) {
        AppointmentsActivity.model = model;
    }

    public AppointmentDTO getModel() {
        return AppointmentsActivity.model;
    }

    @Override
    public void newAppointment() {
        ChooseProviderFragment chooseProviderFragment = new ChooseProviderFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleBaseDTO(args, getIntent(), CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, PatientNavigationHelper.class.getSimpleName());
        chooseProviderFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, chooseProviderFragment, chooseProviderFragment.getClass().getSimpleName());
        transaction.addToBackStack(chooseProviderFragment.getClass().getSimpleName());
        transaction.commit();

    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        AvailableHoursFragment availableHoursFragment  = new AvailableHoursFragment();

        Bundle bundle = new Bundle();
        String patientID = appointmentDTO.getPayload().getPatient().getId();
        VisitTypeDTO visitTypeDTO = new VisitTypeDTO();
        visitTypeDTO.setId(appointmentDTO.getPayload().getVisitReasonId());
        AppointmentResourcesItemDTO resourcesItemDTO = new AppointmentResourcesItemDTO();
        resourcesItemDTO.setId(appointmentDTO.getPayload().getResourceId());
        ResourcesToScheduleDTO resourcesToSchedule = new ResourcesToScheduleDTO();
        resourcesToSchedule.getPractice().setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        resourcesToSchedule.getPractice().setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentsDTO.getPayload().getResourcesToSchedule().add(resourcesToSchedule);

        Gson gson = new Gson();
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(resourcesItemDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID, patientID);
        availableHoursFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, availableHoursFragment, availableHoursFragment.getClass().getSimpleName());
        transaction.addToBackStack(availableHoursFragment.getClass().getSimpleName());
        transaction.commit();

    }

    @Override
    public void availableTimes(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO) {
        AvailableHoursFragment availableHoursFragment = new AvailableHoursFragment();

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String patientID = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPatientId(); //TODO this should be updated for multi practice support

        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResourcesDTO.getResource()));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID, patientID);
        availableHoursFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, availableHoursFragment, availableHoursFragment.getClass().getSimpleName());
        transaction.addToBackStack(availableHoursFragment.getClass().getSimpleName());
        transaction.commit();

    }


}
