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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

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

            TextView dateTextView = ((TextView) view.findViewById(R.id.appointDateTextView));
            TextView timeTextView = ((TextView) view.findViewById(R.id.appointTimeTextView));
            TextView shortNameTextView = ((TextView) view.findViewById(R.id.appointShortnameTextView));
            TextView nameTextView = ((TextView) view.findViewById(R.id.appointNameTextView));
            TextView typeTextView = ((TextView) view.findViewById(R.id.appointTypeTextView));
            final TextView addressTextView = ((TextView) view.findViewById(R.id.appointAddressTextView));
            final TextView addressHeaderTextView = ((TextView) view.findViewById(R.id.appointAddressHeaderTextView));
            TextView requestPendingTextView = (TextView)view.findViewById(R.id.appointRequestPendingTextView);

            dateTextView.setText(onDateParseToString(appointmentModel.getAppointmentDate())[0]);
            timeTextView.setText(onDateParseToString(appointmentModel.getAppointmentDate())[1]);
            shortNameTextView.setText(SystemUtil.onShortDrName(appointmentModel.getDoctorName()));
            nameTextView.setText(appointmentModel.getDoctorName());
            typeTextView.setText(appointmentModel.getAppointmentType());
            //addressTextView.setText(appointmentModel.getA());

            SystemUtil.setProximaNovaRegularTypeface(this, dateTextView);
            SystemUtil.setGothamRoundedBoldTypeface(this, timeTextView);
            SystemUtil.setProximaNovaRegularTypeface(this, shortNameTextView);
            SystemUtil.setProximaNovaSemiboldTypeface(this, nameTextView);
            SystemUtil.setProximaNovaRegularTypeface(this, typeTextView);
            SystemUtil.setProximaNovaRegularTypeface(this, addressTextView);
            SystemUtil.setProximaNovaExtraboldTypeface(this, addressHeaderTextView);
            SystemUtil.setGothamRoundedMediumTypeface(this, requestPendingTextView);

            view.findViewById(R.id.dialogAppointHeaderTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
            view.findViewById(R.id.appointLocationImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMapView(addressTextView.getText().toString());
                }
            });
            view.findViewById(R.id.appointDailImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPhoneCall("000000000");
                }
            });
            view.findViewById(R.id.checkOfficeButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckInAtOffice();
                    dialog.cancel();
                }
            });
            view.findViewById(R.id.checkOfficeNowButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckInAtNow();
                }
            });
            view.findViewById(R.id.dialogEditAppointTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onEditAppointMent();
                }
            });
            requestPendingTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRequestPending();
                }
            });
            //second argument is only for testing it will change based on which dialog wants to show.
            onDialogTypeVisible(view,1);
        }
    }
    /**
     * convert date string in to month and day.
     * @param dateStr the String to evaluate
     */
    private String[] onDateParseToString(String dateStr) {
        String stringDate[] = dateStr.split(" ");
        String formateDate[] = new String[2];
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.dateFormatString), Locale.ENGLISH);
            Date appointdate = sdf.parse(stringDate[0]);
            formateDate[0] = android.text.format.DateFormat.format("MMMM", appointdate) + " "
                    + DateUtil.getDayOrdinal(Integer.parseInt(android.text.format.DateFormat.format("dd", appointdate).toString()));
            formateDate[1] = stringDate[1] + " " + stringDate[2];
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return formateDate;
    }
    /**
     * show device phone call UI based on phone number.
     * @param phoneNumber the String to evaluate
     */
    private void onPhoneCall(final String phoneNumber){
        try{
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        }catch (android.content.ActivityNotFoundException ex){

        }
    }
    /**
     * call check-in at office api.
     */
    private void onCheckInAtOffice(){
        Intent demographicReviewIntent = new Intent(getApplicationContext(), DemographicReviewActivity.class);
        startActivity(demographicReviewIntent);
    }
    /**
     * call check-in early api.
     */
    private  void onCheckInEarly(){

    }
    /**
     * call check-in at Nowapi.
     */
    private void onCheckInAtNow(){

    }
    /**
     * create appointment UI.
     */
    private void onCreateAppointment(){

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
    /**
     * show different dialog UI based on type.
     * @param view ,View to evaluate
     * @param type , int to evaluate
     */
    private void onDialogTypeVisible(View view,int type){
        switch (type){
            case 1:
                onCheckOffceNowUI(view);

                break;
            case 2:
                onCheckAtOfficeUI(view);
                break;
            case 3:
                onCheckNowEarlyUI(view);
                break;
            case 4:
                onEditAppointmentUI(view);
                break;
            case 5:
                onPendingAppointmentUI(view);
                break;
            case 6:
                onReasonAppointmentUI(view);
                break;
            default: break;

        }
    }
    /**
     * call edit appointment page.
     */
    private void onEditAppointMent(){

    }
    /**
     * call request pending api.
     */
    private void onRequestPending(){

    }
    private void onCheckOffceNowUI(View view){
        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.checkOfficeNowButton).setVisibility(View.VISIBLE);
        view.findViewById(R.id.checkOfficeButton).setVisibility(View.VISIBLE);
    }
    private void onCheckAtOfficeUI(View view){
        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.checkOfficeButton).setVisibility(View.VISIBLE);
    }
    private void onCheckNowEarlyUI(View view){
        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.checkOfficeNowButton).setVisibility(View.VISIBLE);
    }
    private void onEditAppointmentUI(View view){
        view.findViewById(R.id.dialogEditAppointTextView).setVisibility(View.VISIBLE);
    }
    private void onPendingAppointmentUI(View view){
        view.findViewById(R.id.appointRequestPendingLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.lightningyellow);
        ((TextView) view.findViewById(R.id.appointDateTextView)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.appointTimeTextView)).setTextColor(getResources().getColor(R.color.white));
    }
    private void onReasonAppointmentUI(View view){
        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.checkOfficeNowButton).setVisibility(View.VISIBLE);
        view.findViewById(R.id.reasonTextInputLayout).setVisibility(View.VISIBLE);
    }
}
