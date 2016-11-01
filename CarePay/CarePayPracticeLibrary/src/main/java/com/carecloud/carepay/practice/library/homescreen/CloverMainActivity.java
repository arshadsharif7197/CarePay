package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialog;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenLabelDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenMetadataDTO;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    public static int           count;
    private       TextView      checkedInCounterTextview;
    private       TextView      alertTextView;
    private       ImageView     modeSwitchImageView;
    private       ImageView     homeLockImageView;
    private       HomeScreenDTO homeScreenDTO;
    private       Spinner       homeSpinner;
    private       LinearLayout  homeCheckinLl;
    private       LinearLayout  homeAlertLinearLl;
    private       TextView      homeQueueLabel;
    private       TextView      homeAlertsLabel;
    private       TextView      homeCheckinLabel;
    private       TextView      homePaymentsLabel;
    private       TextView      homeAppointmentsLabel;
    private       TextView      homeCheckoutLabel;
    private       TextView      homeShopLabel;

    private List<String> modeSwitchOptions = new ArrayList<>();
    private List<String> langs             = new ArrayList<>();

    public enum HomeScreenMode {
        PATIENT_HOME, PRACTICE_HOME
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setSystemUiVisibility();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        homeScreenDTO = getConvertedDTO(HomeScreenDTO.class);

        HomeScreenMode homeScreenMode = HomeScreenMode.valueOf(homeScreenDTO.getState().toUpperCase());
        setContentView(R.layout.activity_main_clover);

        // init UI fields
        initUIFields();

        createChangeModeDialog();

        populateWithLabels();

        modeSwitchImageView.setOnClickListener(this);

        // set the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.home_spinner_item, langs);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeSpinner.setAdapter(spinnerArrayAdapter);


        findViewById(R.id.homeCheckinClickable).setOnClickListener(this);
        findViewById(R.id.homeAppointmentsClickable).setOnClickListener(this);

        changeScreenMode(homeScreenMode);
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            if (homeScreenDTO != null && homeScreenDTO.getPayload() != null) {
                if (homeScreenDTO.getPayload().getUserPractices() != null) {
                    WorkflowServiceHelper.getInstance().setUserPracticeDTO(homeScreenDTO.getPayload().getUserPractices().get(0));
                }
                HomeScreenAppointmentCountsDTO homeScreenAppointmentCountsDTO = homeScreenDTO.getPayload().getAppointmentCounts();
                if (homeScreenAppointmentCountsDTO != null) {
                    checkedInCounterTextview.setText(String.valueOf(homeScreenAppointmentCountsDTO.getPending() + homeScreenAppointmentCountsDTO.getPending()));
                }
            }
        }

        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }

    private void initUIFields() {
        homeCheckinLl = (LinearLayout) findViewById(R.id.homeQueueLayout);
        homeAlertLinearLl = (LinearLayout) findViewById(R.id.homeAlertLayout);
        homeLockImageView = (ImageView) findViewById(R.id.homeLockIcon);
        checkedInCounterTextview = (TextView) findViewById(R.id.checkedInCounterTextview);
        alertTextView = (TextView) findViewById(R.id.alertTextView);
        modeSwitchImageView = (ImageView) findViewById(R.id.homeModeSwitchClickable);
        homeSpinner = (Spinner) findViewById(R.id.homePatientLangSpinner);
        homeQueueLabel = (TextView) findViewById(R.id.queueTitleTextView);
        homeAlertsLabel = (TextView) findViewById(R.id.alaertTitleTextView);
        homeCheckinLabel = (TextView) findViewById(R.id.homeCheckinLabel);
        homePaymentsLabel = (TextView) findViewById(R.id.homePaymentsLabel);
        homeAppointmentsLabel = (TextView) findViewById(R.id.homeAppointmentsLabel);
        homeCheckoutLabel = (TextView) findViewById(R.id.homeCheckoutLabel);
        homeShopLabel = (TextView) findViewById(R.id.homeShopLabel);
    }

    private void populateWithLabels() {
        HomeScreenMetadataDTO metadataDTO = homeScreenDTO.getMetadata();

        HomeScreenLabelDTO labels = metadataDTO.getLabel();

        homeQueueLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getCheckinginNotifications()));
        homeAlertsLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getAlerts()));
        homeCheckinLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getCheckinButton()));
        homePaymentsLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getPaymentsButton()));
        homeAppointmentsLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getAppointmentsButton()));
        homeCheckoutLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getCheckoutButton()));
        homeShopLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getShopButton()));

        // load mode switch options
        modeSwitchOptions.clear();
        modeSwitchOptions.add(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getPatientModeLabel()));
        modeSwitchOptions.add(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getLogoutLabel()));
    }

    private void changeScreenMode(HomeScreenMode homeScreenMode) {
        if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            homeCheckinLl.setVisibility(View.GONE);
            homeAlertLinearLl.setVisibility(View.GONE);
            modeSwitchImageView.setVisibility(View.GONE);
            homeLockImageView.setVisibility(View.VISIBLE);
            homeSpinner.setVisibility(View.VISIBLE);
        } else {
            homeCheckinLl.setVisibility(View.VISIBLE);
            homeAlertLinearLl.setVisibility(View.VISIBLE);
            modeSwitchImageView.setVisibility(View.VISIBLE);
            homeLockImageView.setVisibility(View.GONE);
            homeSpinner.setVisibility(View.GONE);
        }
    }

    public void setSystemUiVisibility() {
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
            WorkflowServiceHelper.getInstance().execute(homeScreenDTO.getMetadata().getTransitions().getPracticeCheckin(), checkInCallback);
        } else if (viewId == R.id.homeModeSwitchClickable) {
            createChangeModeDialog().show();
        } else if (viewId == R.id.homeAppointmentsClickable) {
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

    private ChangeModeDialog createChangeModeDialog() {
        return new ChangeModeDialog(this, new ChangeModeDialog.PatientModeClickListener() {
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
        }, modeSwitchOptions);
    }
}
