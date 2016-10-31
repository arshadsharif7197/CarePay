package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;

import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialog;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

public class CloverMainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int count;

    private TextView                checkedInCounterTextview;
    private WorkflowServiceCallback applicationStartCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
        setContentView(R.layout.activity_main_clover);

        checkedInCounterTextview = (TextView) findViewById(R.id.checkedInCounterTextview);

        // set click listeners
        findViewById(R.id.homeCheckinClickable).setOnClickListener(this);
        findViewById(R.id.homeAppointmentsClickable).setOnClickListener(this);
        findViewById(R.id.homeModeSwitchClickable).setOnClickListener(this);

        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
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
        if (viewId == R.id.homeCheckinClickable) {
            Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckInActivity.class);
            checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(checkedInIntent);
        } else if (viewId == R.id.homeModeSwitchClickable) {
            ChangeModeDialog changeModeDialog = new ChangeModeDialog(this, patientModeClickListener, logoutClickListener);
            changeModeDialog.show();
        } else if (viewId == R.id.homeAppointmentsClickable) {
            Intent appointmentIntent = new Intent(CloverMainActivity.this, AppointmentsActivity.class);
            appointmentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(appointmentIntent);
        }
    }

    private void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private ChangeModeDialog.PatientModeClickListener patientModeClickListener = new ChangeModeDialog.PatientModeClickListener() {
        @Override
        public void onPatientModeSelected() {
            WorkflowServiceHelper.getInstance().executeGetRequest(
                    "https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_mode/authenticate/start",
                    applicationStartCallback);
            applicationStartCallback = new WorkflowServiceCallback() {
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
        }
    };

    private ChangeModeDialog.LogoutClickListener logoutClickListener = new ChangeModeDialog.LogoutClickListener() {
        @Override
        public void onLogoutSelected() {
            Toast.makeText(CloverMainActivity.this, "Logout selected...", Toast.LENGTH_SHORT).show();
        }
    };
}
