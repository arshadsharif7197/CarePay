package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
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
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLinksDTO;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.services.DemographicService;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    public static String LOG_TAG = CloverMainActivity.class.getSimpleName();
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
    private HomeScreenMode homeScreenMode;

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
            int checkinCounter = homeScreenAppointmentCountsDTO.getCheckingInCount() != null? homeScreenAppointmentCountsDTO.getCheckingInCount(): 0 ;
            checkedInCounterTextview.setText(String.valueOf(checkinCounter));
        }
    }

    private void setPracticeUser(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        if (practiceHomeScreenPayloadDTO.getUserPractices() != null && practiceHomeScreenPayloadDTO.getUserPractices().size() > 0) {
            ApplicationMode.getInstance().setUserPracticeDTO(practiceHomeScreenPayloadDTO.getUserPractices().get(0));
        } else {
            showUnAuthorizedDialog();
            //SystemUtil.showSuccessDialogMessage(CloverMainActivity.this,getString(R.string.unauthorized),getString(R.string.unauthorized_practice_user));
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
                        getWorkflowServiceHelper().executeApplicationStartRequest(logOutCall);
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
            String count = intent.getExtras().getString("appointments_checking_in");
            checkedInCounterTextview.setText(count);
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
//            findViewById(R.id.homeCheckinClickable).setEnabled(false);
            navigateToCheckIn();
        } else if (viewId == R.id.homePaymentsClickable) {
//            findViewById(R.id.homePaymentsClickable).setEnabled(false);
            navigateToPayments();
        } else if (viewId == R.id.homeAppointmentsClickable) {
//            findViewById(R.id.homeAppointmentsClickable).setEnabled(false);
            navigateToAppointments();
        } else if (viewId == R.id.homeCheckoutClickable) {
//            findViewById(R.id.homeCheckoutClickable).setEnabled(false);
            checkOut();
        } else if (viewId == R.id.homeShopClickable) {
//            findViewById(R.id.homeShopClickable).setEnabled(false);
            navigateToShop();
        } else if (viewId == R.id.homeNewsClickable) {
//            findViewById(R.id.homeNewsClickable).setEnabled(false);
            getNews();
        } else if (viewId == R.id.homeLockIcon) {
            unlockPracticeMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        findViewById(R.id.homeModeSwitchClickable).setEnabled(true);
//        findViewById(R.id.homeCheckinClickable).setEnabled(true);
//        findViewById(R.id.homePaymentsClickable).setEnabled(true);
//        findViewById(R.id.homeAppointmentsClickable).setEnabled(true);
//        findViewById(R.id.homeCheckoutClickable).setEnabled(true);
//        findViewById(R.id.homeShopClickable).setEnabled(true);
//        findViewById(R.id.homeNewsClickable).setEnabled(true);

        disableUnavailableItems();
    }

    private void disableUnavailableItems(){
        if(homeScreenMode == HomeScreenMode.PATIENT_HOME){
            setViewsDisabled((ViewGroup) findViewById(R.id.homePaymentsClickable));
        }
        setViewsDisabled((ViewGroup) findViewById(R.id.homeCheckoutClickable));
        setViewsDisabled((ViewGroup) findViewById(R.id.homeShopClickable));
        setViewsDisabled((ViewGroup) findViewById(R.id.homeNewsClickable));
    }

    private void setViewsDisabled(ViewGroup viewGroup){
        viewGroup.setEnabled(false);
        for(int i=0; i<viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if(view instanceof ViewGroup) {
                setViewsDisabled((ViewGroup) view);
            }else {
                view.setEnabled(false);
            }
        }
    }

    private void unlockPracticeMode() {
        Gson gson = new Gson();
        PatientModeLinksDTO pinPadObject = gson.fromJson(homeScreenDTO.getMetadata().getLinks(), PatientModeLinksDTO.class);
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(this, pinPadObject.getPinpad(), homeScreenDTO.getMetadata().getLabels(), false);
        confirmationPinDialog.show();
    }

    private void getNews() {
        // TODO: 11/17/2016  uncomment after testing ready
//        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
//        Gson gson = new Gson();
//        TransitionDTO transitionDTO;
//        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
//            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
//            transitionDTO = transitionsDTO.getOfficeNews();
//        } else { // patient mode
//            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
//            transitionDTO = transitionsDTO.getOfficeNews();
//        }
//        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);

        // TODO: 11/17/2016  (for build/test); remove after testing ready
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            getDemographicInformation();
        }
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
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
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
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToAppointments() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = DtoHelper.getConvertedDTO(PracticeHomeScreenTransitionsDTO.class, transitionsAsJsonObject);
            TransitionDTO transitionDTO = transitionsDTO.getPracticeAppointments();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);

        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            getApplicationPreferences().setNavigateToAppointments(true);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            TransitionDTO transitionDTO = transitionsDTO.getPatientAppointments();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);
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
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToCheckIn() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            TransitionDTO transitionDTO = transitionsDTO.getPracticeCheckin();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);
        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            getApplicationPreferences().setNavigateToAppointments(false);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            TransitionDTO transitionDTO = transitionsDTO.getPatientCheckin();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
            getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);
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
                Map<String, String> query = new HashMap<>();
                query.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
                query.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
                getWorkflowServiceHelper().execute(transitionsDTO.getPatientMode(), commonTransitionCallback, query);
            }
        }, new ChangeModeDialog.LogoutClickListener() {
            @Override
            public void onLogoutSelected() {
                logOut(transitionsDTO.getLogout());
            }
        }, modeSwitchOptions);
    }

    /**
     * Log out transition
     * @param transitionsDTO the transitionsDTO
     */
    private void logOut(TransitionDTO transitionsDTO){

        Map<String, String> query = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getApiStartKey());
        headers.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
        query.put("transition", "true");
        getWorkflowServiceHelper().execute(transitionsDTO, logOutCall, query, headers);

    }

    WorkflowServiceCallback checkInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
//            findViewById(R.id.homeCheckinClickable).setEnabled(true);
//            findViewById(R.id.homeAppointmentsClickable).setEnabled(true);
            SystemUtil.showDefaultFailureDialog(CloverMainActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            // log out previous user from Cognito
            CognitoAppHelper.getPool().getUser().signOut();
            CognitoAppHelper.setUser(null);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            CloverMainActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(CloverMainActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
//            findViewById(R.id.homeCheckinClickable).setEnabled(true);
//            findViewById(R.id.homeModeSwitchClickable).setEnabled(true);
//            findViewById(R.id.homePaymentsClickable).setEnabled(true);
//            findViewById(R.id.homeCheckoutClickable).setEnabled(true);
//            findViewById(R.id.homeShopClickable).setEnabled(true);

            disableUnavailableItems();
            SystemUtil.showDefaultFailureDialog(CloverMainActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        // call for transition
        Gson gson = new Gson();
        PatientHomeScreenTransitionsDTO transitions = gson.fromJson(homeScreenDTO.getMetadata().getTransitions(),
                PatientHomeScreenTransitionsDTO.class);
        TransitionDTO transitionDTO = transitions.getPracticeMode();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("transition", "true");
        // pass the pin when supported
        getWorkflowServiceHelper().execute(transitionDTO, practiceModeCallback, queryMap);
    }

    WorkflowServiceCallback practiceModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(CloverMainActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    /**
     * For build/test
     */
    private void getDemographicInformation() {
        // TODO: 11/17/2016 remove method
        DemographicService apptService = (new BaseServiceGenerator(this).createService(DemographicService.class)); //, String token, String searchString
        Call<DemographicDTO> call = apptService.fetchDemographicInformation();
        call.enqueue(new Callback<DemographicDTO>() {
            @Override
            public void onResponse(Call<DemographicDTO> call, Response<DemographicDTO> response) {
                Log.v(LOG_TAG, "demographics fetched");
                DemographicDTO demographicDTO = response.body();
                launchPatientModeCheckinActivity(demographicDTO);
            }

            @Override
            public void onFailure(Call<DemographicDTO> call, Throwable throwable) {
//                findViewById(R.id.homeNewsClickable).setEnabled(true);
                SystemUtil.showDefaultFailureDialog(CloverMainActivity.this);
                Log.e(LOG_TAG, "failed fetching demogr info", throwable);
            }
        });
    }

    /**
     * For build/test
     *
     * @param demographicDTO The DTO
     */
    private void launchPatientModeCheckinActivity(DemographicDTO demographicDTO) {
        // TODO: 11/17/2016 remove method
        // do to Demographics
        Intent intent = new Intent(this, PatientModeCheckinActivity.class);
        // pass the object into the gson
        Gson gson = new Gson();
        intent.putExtra(WorkflowDTO.class.getSimpleName(), gson.toJson(demographicDTO, DemographicDTO.class));

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        // log out previous user from Cognito
        Log.v(this.getClass().getSimpleName(), "sign out Cognito");
        CognitoAppHelper.getPool().getUser().signOut();
        CognitoAppHelper.setUser(null);
        ApplicationMode.getInstance().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);

        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            Gson gson = new Gson();
            JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
            final PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            logOut(transitionsDTO.getLogout());
        }

        super.onBackPressed();
    }

}
