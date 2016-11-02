package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialog;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    public static int count;
    TextView checkedInCounterTextview;
    TextView alertTextView;
    TextView modeSwitchTextView;

    HomeScreenDTO homeScreenDTO;

    public enum HomeScreenMode{
        PATIENT_HOME, PRACTICE_HOME
    }

    private HomeScreenMode homeScreenMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setSystemUiVisibility();
        homeScreenDTO=getConvertedDTO(HomeScreenDTO.class);
        homeScreenMode=HomeScreenMode.valueOf(homeScreenDTO.getState().toUpperCase());
        setContentView(R.layout.activity_main_clover);
        checkedInCounterTextview = (TextView) findViewById(R.id.checkedInCounterTextview);
        alertTextView = (TextView) findViewById(R.id.alertTextView);
        modeSwitchTextView= (TextView) findViewById(R.id.modeSwitchTextView);
        // getDemographicInformation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.checkinTextView).setOnClickListener(this);
        findViewById(R.id.appointmentTextView).setOnClickListener(this);
        modeSwitchTextView.setOnClickListener(this);
        if(homeScreenMode==HomeScreenMode.PATIENT_HOME){
            checkedInCounterTextview.setVisibility(View.GONE);
            alertTextView.setVisibility(View.GONE);
            modeSwitchTextView.setVisibility(View.GONE);
        }else{
            if(homeScreenDTO!=null && homeScreenDTO.getPayload()!=null) {
                if(homeScreenDTO.getPayload().getUserPractices()!=null) {
                    WorkflowServiceHelper.getInstance().setUserPracticeDTO(homeScreenDTO.getPayload().getUserPractices().get(0));
                }
                HomeScreenAppointmentCountsDTO homeScreenAppointmentCountsDTO=homeScreenDTO.getPayload().getAppointmentCounts();
                if(homeScreenAppointmentCountsDTO!=null){
                    checkedInCounterTextview.setText(String.valueOf(homeScreenAppointmentCountsDTO.getPending()+ homeScreenAppointmentCountsDTO.getPending()));
                }
            }
        }

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
            /*Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckInActivity.class);
            checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(checkedInIntent);*/
            if(homeScreenDTO!=null && homeScreenDTO.getMetadata()!=null && homeScreenDTO.getMetadata().getTransitions()!=null) {
                WorkflowServiceHelper.getInstance().execute(homeScreenDTO.getMetadata().getTransitions().getPracticeCheckin(),checkInCallback);
            }

        } else if (viewId == R.id.modeSwitchTextView) {

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

    WorkflowServiceCallback checkInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            NavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };

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
