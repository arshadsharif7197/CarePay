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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

public class AppointmentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView appointmentsDrawerUserIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get handler to navigation drawer's user id text view
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.appointmentsDrawerIdTextView);
        String userId = CognitoAppHelper.getCurrUser();
        if(userId != null) {
            appointmentsDrawerUserIdTextView.setText(userId);
        } else {
            appointmentsDrawerUserIdTextView.setText("");
        }

        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment appointmentsListFragment = (AppointmentsListFragment)
                fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (appointmentsListFragment == null) {
            appointmentsListFragment = new AppointmentsListFragment();
        }

        Intent intent = getIntent();
        AppointmentModel appointmentModel = (AppointmentModel) intent.getSerializableExtra(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);

        Bundle bundle = new Bundle();
        bundle.putSerializable(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, appointmentModel);
        appointmentsListFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.appointments_list_frag_holder, appointmentsListFragment,
                AppointmentsListFragment.class.getSimpleName()).commit();
        Log.d("Cognito Token", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_appointments) {

        } else if (id == R.id.nav_payments) {
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
            AppointmentsActivity.model = null; // appointment clicked item is cleared.
        } else if (id == R.id.nav_settings) {
            Intent demographicActivityIntent= new Intent(AppointmentsActivity.this, DemographicsActivity.class);
            startActivity(demographicActivityIntent);

        } else if (id == R.id.nav_logout) {
            // perform log out, of course
            String userName = CognitoAppHelper.getCurrUser();
            if(userName != null) {
                Log.v(LOG_TAG, "sign out");
                CognitoAppHelper.getPool().getUser().signOut();
                CognitoAppHelper.setUser(null);
                // update the drawer user id fields
                appointmentsDrawerUserIdTextView.setText("");

                // go to Sign in screen
                Intent intent = new Intent(this, SigninSignupActivity.class);
                startActivity(intent);
                finish(); // TODO: 9/27/2016 uncomment
            }
        } else if (id == R.id.nav_purchase) {

        } else if (id == R.id.nav_notification) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static AppointmentModel model;
    public void setAppointmentModel(AppointmentModel model) {
        AppointmentsActivity.model = model;
    }

    public AppointmentModel getModel() {
        return AppointmentsActivity.model;
    }
}
