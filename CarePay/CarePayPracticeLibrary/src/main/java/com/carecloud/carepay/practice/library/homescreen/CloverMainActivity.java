package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;

import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialog;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;

public class CloverMainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int count;
    TextView checkedInCounterTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemUiVisibility();
        setContentView(R.layout.activity_main_clover);
        checkedInCounterTextview = (TextView) findViewById(R.id.checkedInCounterTextview);
        // getDemographicInformation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.checkinTextView).setOnClickListener(this);
        findViewById(R.id.appointmentTextView).setOnClickListener(this);
        findViewById(R.id.modeswitch).setOnClickListener(this);
        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));

    }

    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    BroadcastReceiver newCheckedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            count = Integer.parseInt(checkedInCounterTextview.getText().toString()) + 1;
            checkedInCounterTextview.setText(String.valueOf(count));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newCheckedInReceiver);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.checkinTextView) {
            Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckInActivity.class);
            checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(checkedInIntent);

        } else if (viewId == R.id.modeswitch) {

            ChangeModeDialog changeModeDialog = new ChangeModeDialog(this, new ChangeModeDialog.PatientModeClickListener() {

                @Override
                public void onPatientModeSelected() {
                    Intent appointmentIntent = new Intent(CloverMainActivity.this, PatientModeSplashActivity.class);
                    appointmentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(appointmentIntent);
                    Toast.makeText(CloverMainActivity.this, "Patient Mode selected...", Toast.LENGTH_SHORT).show();
                }
            }, new ChangeModeDialog.LogoutClickListener() {

                @Override
                public void onLogoutSelected() {
                    Toast.makeText(CloverMainActivity.this, "Logout selected...", Toast.LENGTH_SHORT).show();
                }
            });

            changeModeDialog.show();
        } else if (viewId == R.id.appointmentTextView){
            Intent appointmentIntent = new Intent(CloverMainActivity.this, AppointmentsActivity.class);
            appointmentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(appointmentIntent);

        }
    }

    /*private void getDemographicInformation() {
        AppointmentService apptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.fetchCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {

                if (response.code() == 200 && response.body().getPayload() != null && response.body().getPayload().getAppointments() != null) {
                    checkedInCounterTextview.setText(String.valueOf(response.body().getPayload().getAppointments().size()));
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {

            }
        });
    }*/

}
