package com.carecloud.carepaylibray.appointments.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

public class AppointmentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton floatingActionButtonz = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButtonz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment appointmentsListFragment = (AppointmentsListFragment) fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (appointmentsListFragment == null) {
            appointmentsListFragment = new AppointmentsListFragment();
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
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_purchase) {

        } else if (id == R.id.nav_notification) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showAppointmentsDialog(AppointmentModel appointmentModel) {
        if (appointmentModel != null) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_appointments, null);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setCancelable(false);
            final AlertDialog dialog = alertDialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();

            TextView dateTextView = ((TextView) view.findViewById(R.id.apptDateTextView));
            TextView timeTextView = ((TextView) view.findViewById(R.id.apptTimeTextView));
            TextView shortNameTextView = ((TextView) view.findViewById(R.id.apptShortnameTextView));
            TextView nameTextView = ((TextView) view.findViewById(R.id.apptNameTextView));
            TextView typeTextView = ((TextView) view.findViewById(R.id.apptTypeTextView));
            final TextView addressTextView = ((TextView) view.findViewById(R.id.apptAddressTextView));

            dateTextView.setText(onDateParseToString(appointmentModel.getAppointmentDate())[0]);
            timeTextView.setText(onDateParseToString(appointmentModel.getAppointmentDate())[1]);
            shortNameTextView.setText(Utility.onShortDrName(appointmentModel.getDoctorName()));
            nameTextView.setText(appointmentModel.getDoctorName());
            typeTextView.setText(appointmentModel.getAppointmentType());
            //addressTextView.setText(appointmentModel.getA());

            Utility.setProximaNovaRegularTypeface(this, dateTextView);
            Utility.setGothamRoundedBookTypeface(this, timeTextView);
            Utility.setProximaNovaRegularTypeface(this, shortNameTextView);
            Utility.setProximaNovaSemiboldTypeface(this, nameTextView);
            Utility.setProximaNovaRegularTypeface(this, typeTextView);
            Utility.setGothamRoundedMediumTypeface(this, addressTextView);

            view.findViewById(R.id.dialogAppointHeaderImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
            view.findViewById(R.id.apptLocationImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMapView(addressTextView.getText().toString());
                }
            });
            view.findViewById(R.id.apptDailImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPhoneCall("000000000");
                }
            });
            view.findViewById(R.id.checkOfficeBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckInAtOffice();
                    dialog.cancel();
                }
            });
            view.findViewById(R.id.checkOfficeNow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckInAtNow();
                }
            });
        }
    }

    private String[] onDateParseToString(String dateStr) {
        String stringDate[] = dateStr.split(" ");
        String formateDate[] = new String[2];
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date appointdate = sdf.parse(stringDate[0]);
            formateDate[0] = android.text.format.DateFormat.format("MMMM", appointdate) + " "
                    + Utility.getDayOrdinal(Integer.parseInt(android.text.format.DateFormat.format("dd", appointdate).toString()));
            formateDate[1] = stringDate[1] + " " + stringDate[2];
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return formateDate;
    }
    private void onPhoneCall(final String phoneNumber){
        try{
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getApplicationContext(),"Activity is not founded",Toast.LENGTH_SHORT).show();
        }
    }
    private void onCheckInAtOffice(){
        Intent demographicReviewIntent = new Intent(getApplicationContext(), DemographicReviewActivity.class);
        startActivity(demographicReviewIntent);
        }
    private  void onCheckInEarly(){
        Toast.makeText(AppointmentsActivity.this, "Clicked on onCheckInEarly method,", Toast.LENGTH_LONG).show();
        }
    private void onCheckInAtNow(){
        Toast.makeText(AppointmentsActivity.this, "Clicked on onCheckInAtNow method,", Toast.LENGTH_LONG).show();
        }
    private void onCreateAppointment(){
        Toast.makeText(AppointmentsActivity.this, "Clicked on onCreateAppointment method,", Toast.LENGTH_LONG).show();
        }

    /**
     * show device map view based on address.
     * @param address the String to evaluate
     */
    private void onMapView(final String address){
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
