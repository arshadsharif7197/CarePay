package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialog;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenLabelDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenMetadataDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PatientHomeScreenTransitionsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenPayloadDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenTransitionsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    public static int           count;
    private       TextView      checkedInCounterTextview;
    private       TextView      alertTextView;
    private       ImageView     modeSwitchImageView;
    private       ImageView     homeLockImageView;
    private       HomeScreenDTO homeScreenDTO;
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
    private HomeScreenMode     homeScreenMode;

    public enum HomeScreenMode {
        PATIENT_HOME, PRACTICE_HOME
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        homeScreenDTO = getConvertedDTO(HomeScreenDTO.class);

        homeScreenMode = HomeScreenMode.valueOf(homeScreenDTO.getState().toUpperCase());
        homeScreenDTO = getConvertedDTO(HomeScreenDTO.class);

        setContentView(R.layout.activity_main_clover);

        // init UI fields
        initUIFields();

        createChangeModeDialog();

        populateWithLabels();

        modeSwitchImageView.setOnClickListener(this);
        findViewById(R.id.homeLockIcon).setOnClickListener(this);
        findViewById(R.id.homeCheckinClickable).setOnClickListener(this);
        findViewById(R.id.homeAppointmentsClickable).setOnClickListener(this);
        findViewById(R.id.homePaymentsClickable).setOnClickListener(this);
        findViewById(R.id.homeCheckoutClickable).setOnClickListener(this);
        findViewById(R.id.homeShopClickable).setOnClickListener(this);
        findViewById(R.id.homeNewsClickable).setOnClickListener(this);

        changeScreenMode(homeScreenMode);

        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }

    private void initUIFields() {
        homeCheckinLl = (LinearLayout) findViewById(R.id.homeQueueLayout);
        homeAlertLinearLl = (LinearLayout) findViewById(R.id.homeAlertLayout);
        homeLockImageView = (ImageView) findViewById(R.id.homeLockIcon);
        checkedInCounterTextview = (TextView) findViewById(R.id.checkedInCounterTextview);
        alertTextView = (TextView) findViewById(R.id.alertTextView);
        modeSwitchImageView = (ImageView) findViewById(R.id.homeModeSwitchClickable);
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

        HomeScreenLabelDTO labels = metadataDTO.getLabels();

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
        } else {
            homeCheckinLl.setVisibility(View.VISIBLE);
            homeAlertLinearLl.setVisibility(View.VISIBLE);
            modeSwitchImageView.setVisibility(View.VISIBLE);
            homeLockImageView.setVisibility(View.GONE);
            if (homeScreenDTO != null && homeScreenDTO.getPayload() != null) {
                JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
                Gson gson = new Gson();
                PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO = gson.fromJson(payloadAsJsonObject, PracticeHomeScreenPayloadDTO.class);
                setPracticeUser(practiceHomeScreenPayloadDTO);
                setAppointmentCount(practiceHomeScreenPayloadDTO);
            }
        }
    }

    private void setAppointmentCount(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        HomeScreenAppointmentCountsDTO homeScreenAppointmentCountsDTO = practiceHomeScreenPayloadDTO.getAppointmentCounts();
        if (homeScreenAppointmentCountsDTO != null) {
            checkedInCounterTextview.setText(String.valueOf(homeScreenAppointmentCountsDTO.getPendingCount() + homeScreenAppointmentCountsDTO.getPendingCount()));
        }
    }

    private void setPracticeUser(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        if (practiceHomeScreenPayloadDTO.getUserPractices() != null && practiceHomeScreenPayloadDTO.getUserPractices().size() > 0) {
            WorkflowServiceHelper.getInstance().setUserPracticeDTO(practiceHomeScreenPayloadDTO.getUserPractices().get(0));
        } else {
            showUnAuthorizedDialog();
            //SystemUtil.showDialogMessage(CloverMainActivity.this,getString(R.string.unauthorized),getString(R.string.unauthorized_practice_user));
        }
    }

    /**
     * When no practice user show anauthorized user and logout
     */
    public void showUnAuthorizedDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.unauthorized));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.unauthorized_practice_user))
                .setCancelable(false)
                .setPositiveButton(getString(com.carecloud.carepaylibrary.R.string.alert_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        WorkflowServiceHelper.getInstance().executeApplicationStartRequest(logOutCall);
                        //CloverMainActivity.this.onBackPressed();
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, com.carecloud.carepaylibrary.R.drawable.icn_notification_error);

        // show it
        alertDialog.show();
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

        if (viewId == R.id.homeModeSwitchClickable) {
            navigateToPatientHome();
        } else if (viewId == R.id.homeCheckinClickable) {
            navigateToCheckIn();
        } else if (viewId == R.id.homePaymentsClickable) {
            navigateToPayments();
        } else if (viewId == R.id.homeAppointmentsClickable) {
            navigateToAppointments();
        } else if (viewId == R.id.homeCheckoutClickable) {
            checkOut();
        } else if (viewId == R.id.homeShopClickable) {
            navigateToShop();
        } else if (viewId == R.id.homeNewsClickable) {
            getNews();
        } else if(viewId == R.id.homeLockIcon) {
            unlockPracticeMode();
        }
    }

    private void unlockPracticeMode() {
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(this);
        confirmationPinDialog.show();
    }

    private void getNews() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getOfficeNews();
        } else { // patient mode
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getOfficeNews();
        }
        WorkflowServiceHelper.getInstance().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToShop() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getShop();
        } else {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getShop();
        }
        WorkflowServiceHelper.getInstance().execute(transitionDTO, commonTransitionCallback);
    }

    private void checkOut() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticeCheckout();
        } else {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientCheckout();
        }
        WorkflowServiceHelper.getInstance().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToAppointments() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            // transition needed
            Intent appointmentIntent = new Intent(CloverMainActivity.this, AppointmentsActivity.class);
            appointmentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(appointmentIntent);
        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            // transition needed
        }
    }

    private void navigateToPayments() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;

        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticePayments();
        } else {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientPayments();
        }
        WorkflowServiceHelper.getInstance().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToCheckIn() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            TransitionDTO transitionDTO = transitionsDTO.getPracticeCheckin();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.toDateStringAsYYYYMMDD(new Date()));
            queryMap.put("end_date", DateUtil.toDateStringAsYYYYMMDD(new Date()));
            WorkflowServiceHelper.getInstance().execute(transitionDTO, checkInCallback, queryMap);
        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            TransitionDTO transitionDTO = transitionsDTO.getPatientCheckin();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.toDateStringAsYYYYMMDD(new Date()));
            queryMap.put("end_date", DateUtil.toDateStringAsYYYYMMDD(new Date()));
            WorkflowServiceHelper.getInstance().execute(transitionDTO, checkInCallback, queryMap);
        }
    }

    private void navigateToPatientHome() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            createChangeModeDialog().show();
        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            // add transitions
        }
    }

    private ChangeModeDialog createChangeModeDialog() {
        Gson gson = new Gson();
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        final PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
        return new ChangeModeDialog(this, new ChangeModeDialog.PatientModeClickListener() {
            @Override
            public void onPatientModeSelected() {
                WorkflowServiceHelper.getInstance().execute(transitionsDTO.getPatientMode(), commonTransitionCallback);
            }
        }, new ChangeModeDialog.LogoutClickListener() {
            @Override
            public void onLogoutSelected() {
                Map<String, String> query = new HashMap<>();
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-key", HttpConstants.getApiStartKey());
                headers.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                query.put("transition", "true");
                WorkflowServiceHelper.getInstance().execute(transitionsDTO.getLogout(), logOutCall, query, headers);
            }
        }, modeSwitchOptions);
    }

    WorkflowServiceCallback checkInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(CloverMainActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(CloverMainActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(CloverMainActivity.this, workflowDTO);
            CloverMainActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(CloverMainActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(CloverMainActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };
}
