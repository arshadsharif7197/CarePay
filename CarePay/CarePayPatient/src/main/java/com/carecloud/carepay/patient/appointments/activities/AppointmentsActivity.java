package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.demographics.activities.NewReviewDemographicsActivity;
import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class AppointmentsActivity extends BasePatientActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = AppointmentsActivity.class.getSimpleName();

    public static AppointmentDTO model;
    private TextView appointmentsDrawerUserIdTextView;
    private AppointmentsResultModel appointmentsDTO;
    private AppointmentDTO appointmentDTO;
    private WorkflowServiceCallback appointmentsWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(AppointmentsActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
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
            SystemUtil.showFaultDialog(AppointmentsActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appointmentsDTO = getConvertedDTO(AppointmentsResultModel.class);
        if(appointmentsDTO.getPayload() != null
                && appointmentsDTO.getPayload().getAppointments() != null
                && appointmentsDTO.getPayload().getAppointments().size() > 0) {
            appointmentDTO = appointmentsDTO.getPayload().getAppointments().get(0);
        }

        // Get appointment information data
//        AppointmentService aptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class);
//        Call<AppointmentsResultModel> call = aptService.getAppointmentsInformation();
//        call.enqueue(new Callback<AppointmentsResultModel>() {
//
//            @Override
//            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
//                appointmentsDTO = response.body();
//                AppointmentLabelDTO appointmentLabels = appointmentsDTO.getMetadata().getLabel();
//
//                // Set Appointment screen title
//                toolbar.setTitle(StringUtil.getLabelForView(appointmentLabels.getAppointmentsHeading()));
//                gotoAppointmentFragment();
//            }
//
//            @Override
//            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {
//
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get handler to navigation drawer's user id text view
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.appointmentsDrawerIdTextView);
        String userId = CognitoAppHelper.getCurrUser();
        if (userId != null) {
            appointmentsDrawerUserIdTextView.setText(userId);
        } else {
            appointmentsDrawerUserIdTextView.setText("");
        }

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

        if(CognitoAppHelper.getPool().getUser() != null){
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_launch_demogr_review) {
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
            queries.put("practice_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getAppointmentId());

            Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
            header.put("transition", "true");
            WorkflowServiceHelper.getInstance().execute(appointmentsDTO.getMetadata().getTransitions().getChecking_in(), transitionToDemographicsVerifyCallback, queries, header);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_appointments) {
            Log.v(LOG_TAG, "Appointments");
        } else if (id == R.id.nav_payments) {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            AppointmentsActivity.model = null; // appointment clicked item is cleared.
        } else if (id == R.id.nav_settings) {
            Intent demographicActivityIntent = new Intent(AppointmentsActivity.this,
                    DemographicsActivity.class);
            startActivity(demographicActivityIntent);

        } else if (id == R.id.nav_logout) {
            // perform log out, of course
            String userName = CognitoAppHelper.getCurrUser();
            if (userName != null) {
                Log.v(LOG_TAG, "sign out");
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("x-api-key", HttpConstants.getApiStartKey());
                headersMap.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                headersMap.put("transition", "true");
                Map<String, String> queryMap = new HashMap<>();
                TransitionDTO transitionDTO = appointmentsDTO.getMetadata().getTransitions().getLogout();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, appointmentsWorkflowCallback, queryMap, headersMap);
            }
        } else if (id == R.id.nav_purchase) {
            Log.v(LOG_TAG, "Purchase");
        } else if (id == R.id.nav_notification) {
            Log.v(LOG_TAG, "Notification");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setAppointmentModel(AppointmentDTO model) {
        AppointmentsActivity.model = model;
    }

    public AppointmentDTO getModel() {
        return AppointmentsActivity.model;
    }
}
