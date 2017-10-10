package com.carecloud.carepay.practice.library.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.carecloud.carepay.practice.library.homescreen.adapters.OfficeNewsListAdapter;
import com.carecloud.carepay.practice.library.homescreen.dialogs.OfficeNewsDetailsDialog;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAlertsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PatientHomeScreenTransitionsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenPayloadDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenTransitionsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLinksDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    private ImageView modeSwitchImageView;
    private ImageView homeLockImageView;
    private HomeScreenDTO homeScreenDTO;
    private LinearLayout homeCheckInLl;
    private LinearLayout homeAlertLinearLl;
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
        setContentView(R.layout.activity_main_practice_mode);

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

        changeScreenMode(homeScreenMode);
        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
        getNews();
    }

    private void initUIFields() {
        homeCheckInLl = (LinearLayout) findViewById(R.id.homeQueueLayout);
        homeAlertLinearLl = (LinearLayout) findViewById(R.id.homeAlertLayout);
        homeLockImageView = (ImageView) findViewById(R.id.homeLockIcon);
        modeSwitchImageView = (ImageView) findViewById(R.id.homeModeSwitchClickable);
    }

    private void populateWithLabels() {
        // load mode switch options
        modeSwitchOptions.clear();
        modeSwitchOptions.add(Label.getLabel("patient_mode_button"));
        modeSwitchOptions.add(Label.getLabel("logout_button"));
    }

    private void changeScreenMode(HomeScreenMode homeScreenMode) {
        TextView checkinLabel = (TextView) findViewById(R.id.homeCheckinLabel);
        if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            homeCheckInLl.setVisibility(View.GONE);
            homeAlertLinearLl.setVisibility(View.GONE);
            modeSwitchImageView.setVisibility(View.GONE);
            homeLockImageView.setVisibility(View.VISIBLE);
            setNavigationBarVisibility();
            findViewById(R.id.homeCheckoutClickable).setVisibility(View.VISIBLE);
            checkinLabel.setText(Label.getLabel("checkin_button_patient_mode"));
        } else {
            homeCheckInLl.setVisibility(View.VISIBLE);
            homeAlertLinearLl.setVisibility(View.GONE);//// TODO: 10/3/17 this is temporary until alerts is ready to use
            modeSwitchImageView.setVisibility(View.VISIBLE);
            homeLockImageView.setVisibility(View.GONE);
            checkinLabel.setText(Label.getLabel("checkin_button"));

            if (homeScreenDTO != null && homeScreenDTO.getPayload() != null) {
                JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
                Gson gson = new Gson();
                PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO
                        = gson.fromJson(payloadAsJsonObject, PracticeHomeScreenPayloadDTO.class);
                setPracticeUser(practiceHomeScreenPayloadDTO);
                setAppointmentCount(practiceHomeScreenPayloadDTO);
                setAlertCount(practiceHomeScreenPayloadDTO);
            }
        }
    }

    private void setAppointmentCount(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        HomeScreenAppointmentCountsDTO homeScreenAppointmentCountsDTO = practiceHomeScreenPayloadDTO
                .getAppointmentCounts();
        if (homeScreenAppointmentCountsDTO != null) {
            int checkinCounter = homeScreenAppointmentCountsDTO.getCheckingInCount() != null ?
                    homeScreenAppointmentCountsDTO.getCheckingInCount() : 0;
            ((TextView) findViewById(R.id.checkedInCounterTextview)).setText(String.valueOf(checkinCounter));
        }
    }

    private void setAlertCount(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        HomeScreenAlertsDTO alertsDTO = practiceHomeScreenPayloadDTO.getAlerts();
        if (alertsDTO != null) {
            TextView alertText = (TextView) findViewById(R.id.alertTextView);
            int alertCounter = alertsDTO.getCount();
            alertText.setText(String.valueOf(alertCounter));
        }
    }

    private void setPracticeUser(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        if (practiceHomeScreenPayloadDTO.getUserPractices() != null
                && practiceHomeScreenPayloadDTO.getUserPractices().size() > 0) {
            getApplicationMode().setUserPracticeDTO(getAppAuthorizationHelper(),
                    practiceHomeScreenPayloadDTO.getUserPractices().get(0));
        } else {
            showUnAuthorizedDialog();
        }
    }

    /**
     * When no practice user show anauthorized user and logout
     */
    public void showUnAuthorizedDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.unauthorized));
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

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icn_notification_error);
        alertDialog.show();
    }

    BroadcastReceiver newCheckedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String count = intent.getExtras().getString("appointments_checking_in");
            ((TextView) findViewById(R.id.checkedInCounterTextview)).setText(count);
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
        } else if (viewId == R.id.homeLockIcon) {
            unlockPracticeMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableUnavailableItems();
    }

    private void disableUnavailableItems() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            setViewsDisabled((ViewGroup) findViewById(R.id.homeCheckoutClickable));
        }
    }

    private void setViewsDisabled(ViewGroup viewGroup) {
        viewGroup.setEnabled(false);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setViewsDisabled((ViewGroup) view);
            } else {
                view.setEnabled(false);
            }
        }
    }

    private void unlockPracticeMode() {
        Gson gson = new Gson();
        PatientModeLinksDTO pinPadObject = gson.fromJson(homeScreenDTO.getMetadata().getLinks(), PatientModeLinksDTO.class);
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(this, pinPadObject.getPinpad(), false);
        confirmationPinDialog.show();
    }

    private void getNews() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getLinks();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;

        PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
        transitionDTO = transitionsDTO.getOfficeNews();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        queryMap.put("publish_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        getWorkflowServiceHelper().execute(transitionDTO, getNewsCallback, queryMap);
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

        getWorkflowServiceHelper().interrupt();
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
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKOUT);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientCheckout();
        }
        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
    }


    private void navigateToAppointments() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO = null;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = DtoHelper.getConvertedDTO(PracticeHomeScreenTransitionsDTO.class, transitionsAsJsonObject);
            transitionDTO = transitionsDTO.getPracticeAppointments();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> providersSavedFilteredIds = getApplicationPreferences().getSelectedProvidersIds(practiceId, userId);
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

            if(locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()){
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }

        } else {
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_APPOINTMENT);

            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientAppointments();
        }
        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);
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
        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
    }

    private void navigateToCheckIn() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticeCheckin();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> providersSavedFilteredIds = getApplicationPreferences().getSelectedProvidersIds(practiceId, userId);
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

            if(locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()){
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }

        } else {
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKIN);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientCheckin();
        }

        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);

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
                query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
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
     *
     * @param transitionsDTO the transitionsDTO
     */
    private void logOut(TransitionDTO transitionsDTO) {
        Map<String, String> query = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        if (!HttpConstants.isUseUnifiedAuth()) {
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            headers.put("Authorization", getAppAuthorizationHelper().getCurrSession().getIdToken().getJWTToken());
        } else {
            headers.putAll(getWorkflowServiceHelper().getApplicationStartHeaders());
        }
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
            showErrorNotification(exceptionMessage);
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
            getApplicationMode().clearUserPracticeDTO();
            if (!HttpConstants.isUseUnifiedAuth()) {
                // log out previous user from Cognito
                getAppAuthorizationHelper().getPool().getUser().signOut();
                getAppAuthorizationHelper().setUser(null);
            }
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            CloverMainActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback getNewsCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            HomeScreenDTO homeScreenDTO = gson.fromJson(workflowDTO.toString(), HomeScreenDTO.class);
            JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
            PracticeHomeScreenPayloadDTO practiceHomePayloadDTO = gson.fromJson(payloadAsJsonObject,
                    PracticeHomeScreenPayloadDTO.class);
            List<HomeScreenOfficeNewsDTO> officeNews = practiceHomePayloadDTO.getOfficeNews();
            if (!officeNews.isEmpty()) {
                RecyclerView newsList = (RecyclerView) findViewById(R.id.office_news_list);
                newsList.setVisibility(View.VISIBLE);
                findViewById(R.id.office_news_header).setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(CloverMainActivity.this,
                        LinearLayoutManager.HORIZONTAL, false);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(newsList.getContext(),
                        layoutManager.getOrientation());
                newsList.setLayoutManager(layoutManager);
                newsList.addItemDecoration(dividerItemDecoration);

                OfficeNewsListAdapter adapter = new OfficeNewsListAdapter(CloverMainActivity.this,
                        officeNews, officeNewsClickedListener);
                newsList.setAdapter(adapter);
            } else {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                View layoutContainer = findViewById(R.id.layoutContainer);
//                layoutContainer.setLayoutParams(params);
//
//                RelativeLayout.LayoutParams shadowParams = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                shadowParams.addRule(RelativeLayout.BELOW, R.id.layoutContainer);
//                shadowParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                View shadowImage = findViewById(R.id.shadow);
//                shadowImage.setLayoutParams(shadowParams);
            }


        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    OfficeNewsListAdapter.OnOfficeNewsClickedListener officeNewsClickedListener
            = new OfficeNewsListAdapter.OnOfficeNewsClickedListener() {
        @Override
        public void onOfficeNewsSelected(List<HomeScreenOfficeNewsDTO> officeNewsList, int position) {
            OfficeNewsDetailsDialog detailsDialog = new OfficeNewsDetailsDialog(
                    CloverMainActivity.this, officeNewsList, position);
            detailsDialog.show();
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
            disableUnavailableItems();
            showErrorNotification(exceptionMessage);
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
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    public void onBackPressed() {

        getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);

        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            Gson gson = new Gson();
            JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
            final PracticeHomeScreenTransitionsDTO transitionsDTO = gson
                    .fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            logOut(transitionsDTO.getLogout());
            getAppAuthorizationHelper().setUser(null);
            getApplicationMode().setUserPracticeDTO(getAppAuthorizationHelper(), null);
        }

        super.onBackPressed();
    }

}
