package com.carecloud.carepaylibray.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

public class AppointmentsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = AppointmentsActivity.class.getSimpleName();

    public static AppointmentDTO model;
    private TextView appointmentsDrawerUserIdTextView;
    private AppointmentsResultModel appointmentsScreenLabels;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get appointment information data
        AppointmentService aptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class);
        Call<AppointmentsResultModel> call = aptService.getAppointmentsInformation();
        call.enqueue(new Callback<AppointmentsResultModel>() {

            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                appointmentsScreenLabels = response.body();
                AppointmentLabelDTO appointmentLabels = appointmentsScreenLabels.getMetadata().getLabel();

                // Set Appointment screen title
                toolbar.setTitle(StringUtil.getLabelForView(appointmentLabels.getAppointmentsHeading()));
                gotoAppointmentFragment();
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {

            }
        });

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
    }

    private void gotoAppointmentFragment() {
        Intent intent = getIntent();
        AppointmentDTO appointmentDTO = (AppointmentDTO) intent.getSerializableExtra(
                CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);

        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment appointmentsListFragment = (AppointmentsListFragment)
                fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (appointmentsListFragment == null) {
            appointmentsListFragment = new AppointmentsListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.APPOINTMENT_INFO_BUNDLE, appointmentsScreenLabels);
            bundle.putSerializable(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, appointmentDTO);
            appointmentsListFragment.setArguments(bundle);
        }

        fm.beginTransaction().replace(R.id.appointments_list_frag_holder, appointmentsListFragment,
                AppointmentsListFragment.class.getSimpleName()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            return true;
        } else if (id == R.id.action_launch_demogr_review) {
            // temporary launch Demographics Review for QA testing
            // (please do not remove!)
            DemographicService apptService = (new BaseServiceGenerator(getApplicationContext())).createService(DemographicService.class); //, String token, String searchString
            Call<DemographicDTO> call = apptService.fetchDemographicsVerify();
            call.enqueue(new Callback<DemographicDTO>() {
                @Override
                public void onResponse(Call<DemographicDTO> call, Response<DemographicDTO> response) {
                    DemographicDTO demographicDTO = response.body();
                    launchDemographics(demographicDTO);
                }

                @Override
                public void onFailure(Call<DemographicDTO> call, Throwable throwable) {
                    Log.e(LOG_TAG, "failed fetching demogr info", throwable);
                }
            });

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
                CognitoAppHelper.getPool().getUser().signOut();
                CognitoAppHelper.setUser(null);
                // update the drawer user id fields
                appointmentsDrawerUserIdTextView.setText("");

                // go to Sign in screen
                Intent intent = new Intent(this, SigninSignupActivity.class);
                startActivity(intent);
                finish();
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
