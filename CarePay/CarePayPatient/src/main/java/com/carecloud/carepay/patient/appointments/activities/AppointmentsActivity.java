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
import com.carecloud.carepay.patient.consentforms.ConsentActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.intakeforms.activities.InTakeActivityWebView;
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
import com.carecloud.carepaylibray.constants.CarePayConstants;
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
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
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

        CognitoAppHelper.getPool().getUser().signOut();
        CognitoAppHelper.setUser(null);

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
        Intent intent = new Intent(getApplicationContext(), DemographicReviewActivity.class);
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


            //First call to show intake_forms
            //after response call in activity web view self call
            Map<String, String> queryString = new HashMap<>();
            Map<String, String> header = new HashMap<>();

            header.put("transition", "true");
            queryString.put("appointment_id","4c42acd1-8ed2-4f2e-b2f5-86b33b325a65");//model.getMetadata().getAppointmentId()
            queryString.put("practice_id","77b81aa8-1155-4da7-9fd9-2f6967b09a93");
            queryString.put("practice_mgmt","carecloud");

            TransitionDTO transitionDTO = new TransitionDTO();
            transitionDTO.setMethod("POST");
            transitionDTO.setUrl("dev/workflow/carepay/patient_checkin/consent_forms/update_consent");

            Gson gson = new Gson();
            String body = "{\"appointments\":[{\"metadata\":{\"appointment_id\":\"394d8ad4-4063-470b-be04-2f2d12f6520e\",\"created_dt\":\"2016-11-15T19:26:40.168Z\",\"hash_appointments_id\":\"bcb07848d433be95\",\"patient_id\":\"cd5bc403-4bfe-4d60-ae2d-99e26d4fd4a2\",\"practice_id\":\"77b81aa8-1155-4da7-9fd9-2f6967b09a93\",\"practice_mgmt\":\"carecloud\",\"request\":\"patient\",\"updated_dt\":\"2016-11-15T19:26:40.168Z\",\"user_id\":\"e3f05df59527ba6e\",\"username\":\"srios@carecloud.com\"},\"payload\":{\"provider\":{\"id\":12452,\"name\":\"Dr. Frank Stone\",\"phone_number\":\"5555555555\"},\"patient\":{\"chart_number\":\"1234424\",\"date_of_birth\":\"2016-11-01T12:00:00-04:00\",\"email\":\"srios@carecloud.com\",\"first_name\":\"SAUL\",\"gender_id\":1,\"id\":\"cd5bc403-4bfe-4d60-ae2d-99e26d4fd4a2\",\"last_name\":\"RIOS\",\"primary_phone_number\":\"3051111234\"}}}],\"demographics\":{}}";  //gson.toJson(consentFormDTO.getConsentFormPayloadDTO());





            //TransitionDTO transitionDTO2 = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();

            //consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
            //consentFormDTO convert into json string
            WorkflowServiceHelper.getInstance().execute(transitionDTO, intakeFormCallback, body, queryString, header);

            return true;
        } else if (id == R.id.action_launch_demogr_review) {
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
            queries.put("practice_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", appointmentsDTO.getPayload().getAppointments().get(0).getMetadata().getAppointmentId());

            Map<String, String> header = new HashMap<>();
            header.put("transition", "true");
            WorkflowServiceHelper.getInstance().execute(appointmentsDTO.getMetadata().getTransitions().getCheckin(), transitionToDemographicsVerifyCallback, queries, header);

        }

        return super.onOptionsItemSelected(item);
    }

    WorkflowServiceCallback intakeFormCallback=new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {


            Intent intent = new Intent(getApplicationContext(), InTakeActivityWebView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("appointment_id","4c42acd1-8ed2-4f2e-b2f5-86b33b325a65");//model.getMetadata().getAppointmentId()
            bundle.putString("findings_id","e4de697f-50b8-498c-957f-3c9bca6188ea");
            bundle.putString("patient_id","34338ada-8cf1-4ef4-834c-9c450971ae73");//
            bundle.putString("practice_id","77b81aa8-1155-4da7-9fd9-2f6967b09a93");
            bundle.putString("practice_mgmt","carecloud");
            intent.putExtras(bundle);
            startActivity(intent);



        }

        @Override
        public void onFailure(String exceptionMessage) {
            //// TODO: 11/14/16 change  AppointmentsActivity.this for ConsentActivity.class
            SystemUtil.showDialogMessage(AppointmentsActivity.this,getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

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
