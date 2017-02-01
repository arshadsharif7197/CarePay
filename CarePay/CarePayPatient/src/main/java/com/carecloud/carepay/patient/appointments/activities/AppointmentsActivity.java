package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.activities.NewReviewDemographicsActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.IdsDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class AppointmentsActivity extends MenuPatientActivity {

    private static final String LOG_TAG = AppointmentsActivity.class.getSimpleName();

    public static AppointmentDTO model;
    private AppointmentsResultModel appointmentsDTO;
    private AppointmentDTO appointmentDTO;

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(AppointmentsActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
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

        if (appointmentsDTO.getPayload() != null && appointmentsDTO.getPayload().getAppointments() != null){
            IdsDTO idsDTO = appointmentsDTO.getPayload().getPractice_patient_ids().get(0);
            practiceId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPracticeId();
            practiceMgmt = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPracticeManagement();
            patientId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPatientId();
            prefix = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getPrefix();
            userId = appointmentsDTO.getPayload().getPractice_patient_ids().get(0).getUserId();
            ApplicationPreferences.Instance.setPatientId(patientId);
            ApplicationPreferences.Instance.setPracticeManagement(practiceMgmt);
            ApplicationPreferences.Instance.setPracticeId(practiceId);
            ApplicationPreferences.Instance.setUserId(userId);
            ApplicationPreferences.Instance.setPrefix(prefix);
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

        fm.beginTransaction().replace(R.id.appointments_list_frag_holder, appointmentsListFragment,
                AppointmentsListFragment.class.getSimpleName()).commit();
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (CognitoAppHelper.getPool().getUser() != null) {
            CognitoAppHelper.getPool().getUser().signOut();
            CognitoAppHelper.setUser(null);
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
//            Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
//            header.put("transition", "true");
//            WorkflowServiceHelper.getInstance().execute(appointmentsDTO.getMetadata().getTransitions().getCheckingIn(),
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
}
