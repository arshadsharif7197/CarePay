package com.carecloud.carepay.patient.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.consentforms.ConsentFormsActivity;
import com.carecloud.carepay.patient.messages.activities.MessagesActivity;
import com.carecloud.carepay.patient.myhealth.MyHealthActivity;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.notifications.activities.NotificationActivity;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.retail.activities.RetailActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMenuItem;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 10/01/17
 */
public abstract class MenuPatientActivity extends BasePatientActivity {

    //transitions
    private static TransitionDTO transitionBalance;
    private static TransitionDTO transitionProfile;
    private static TransitionDTO transitionAppointments;
    private static TransitionDTO transitionForms;
    private static TransitionDTO transitionLogout;
    private static TransitionDTO transitionNotifications;
    private static TransitionDTO transitionMyHealth;
    private static TransitionDTO transitionRetail;
    private static TransitionDTO transitionMessaging;

    protected ActionBarDrawerToggle toggle;
    protected TextView appointmentsDrawerUserIdTextView;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    protected boolean toolbarVisibility = false;
    private TextView userFullNameTextView;
    private BadgeDrawerArrowDrawable badgeDrawable;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        appointmentsDrawerUserIdTextView = navigationView.findViewById(R.id.appointmentsDrawerIdTextView);
        userFullNameTextView = navigationView.findViewById(R.id.userNameTextView);
        inflateDrawer();
        LocalBroadcastManager.getInstance(this).registerReceiver(badgeReceiver,
                new IntentFilter(CarePayConstants.UPDATE_BADGES_BROADCAST));
        if (getApplicationPreferences().isLandingScreen()) {
            setInitialData();
        }
    }

    private void setInitialData() {
        MyHealthDto myHealthDto = getConvertedDTO(MyHealthDto.class);
        List<PracticePatientIdsDTO> practicePatientIds = myHealthDto.getPayload().getPracticePatientIds();
        if (!practicePatientIds.isEmpty()) {
            getApplicationPreferences().writeObjectToSharedPreference(
                    CarePayConstants.KEY_PRACTICE_PATIENT_IDS, practicePatientIds);
        }
        setTransitionBalance(myHealthDto.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(myHealthDto.getMetadata().getTransitions().getLogout());
        setTransitionProfile(myHealthDto.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(myHealthDto.getMetadata().getLinks().getAppointments());
        setTransitionNotifications(myHealthDto.getMetadata().getLinks().getNotifications());
        setTransitionMyHealth(myHealthDto.getMetadata().getLinks().getMyHealth());
        setTransitionRetail(myHealthDto.getMetadata().getLinks().getRetail());
        setTransitionForms(myHealthDto.getMetadata().getLinks().getUserForms());
        setTransitionMessaging(myHealthDto.getMetadata().getLinks().getMessaging());

        ApplicationPreferences.getInstance().writeObjectToSharedPreference(CarePayConstants
                .DEMOGRAPHICS_ADDRESS_BUNDLE, myHealthDto.getPayload().getDemographicDTO().getPayload().getAddress());

        ApplicationPreferences.getInstance().setPracticesWithBreezeEnabled(myHealthDto.getPayload()
                .getPracticeInformation());
        ApplicationPreferences.getInstance().setUserPractices(myHealthDto.getPayload()
                .getPracticeInformation());

        ApplicationPreferences.getInstance().setUserFullName(StringUtil
                .getCapitalizedUserName(myHealthDto.getPayload().getDemographicDTO().getPayload().getPersonalDetails().getFirstName(),
                        myHealthDto.getPayload().getDemographicDTO().getPayload().getPersonalDetails().getLastName()));

        String userImageUrl = myHealthDto.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getProfilePhoto();
        if (userImageUrl != null) {
            getApplicationPreferences().setUserPhotoUrl(userImageUrl);
        }
        getApplicationPreferences().setLandingScreen(false);
    }


    protected void inflateDrawer() {
        setSupportActionBar(toolbar);
        toggle = new BadgeDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
        toggle.setDrawerArrowDrawable(badgeDrawable);
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.findViewById(R.id.appointmentMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.myHealthMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.paymentsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.messagesMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.formsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.shopMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.notificationsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.settingsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.logOutMenuItem).setOnClickListener(menuItemClickListener);
    }

    private void setUserImage() {
        String imageUrl = ApplicationPreferences.getInstance().getUserPhotoUrl();
        ImageView userImageView = navigationView.findViewById(R.id.appointmentDrawerIdImageView);
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            Picasso.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.icn_placeholder_user_profile_png)
                    .resize(160, 160)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(userImageView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserImage();
        updateBadgeCounterViews();
        setUserFullName();
        if (appointmentsDrawerUserIdTextView != null) {
            String userId = getApplicationPreferences().getUserId();
            if (userId != null) {
                appointmentsDrawerUserIdTextView.setText(userId);
            } else {
                appointmentsDrawerUserIdTextView.setText("");
            }
        }
    }

    private void setUserFullName() {
        userFullNameTextView.setText(ApplicationPreferences.getInstance().getFullName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.clear();
    }

    View.OnClickListener menuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WorkflowServiceCallback callback = null;
            TransitionDTO transition = null;
            Map<String, String> headersMap = new HashMap<>();
            Map<String, String> queryMap = new HashMap<>();
            String payload = null;

            int id = view.getId();
            switch (id) {
                case R.id.myHealthMenuItem:
                    startActivity(MyHealthActivity.class);
                    break;
                case R.id.appointmentMenuItem:
                    startActivity(AppointmentsActivity.class);
                    break;
                case R.id.paymentsMenuItem:
                    startActivity(ViewPaymentBalanceHistoryActivity.class);
                    break;
                case R.id.messagesMenuItem:
                    startActivity(MessagesActivity.class);
                    break;
                case R.id.formsMenuItem:
                    startActivity(ConsentFormsActivity.class);
                    break;
                case R.id.shopMenuItem:
                    startActivity(RetailActivity.class);
                    break;
                case R.id.notificationsMenuItem:
                    startActivity(NotificationActivity.class);
                    break;
                case R.id.settingsMenuItem:
                    callback = demographicsSettingsCallBack;
                    transition = transitionProfile;
                    break;
                case R.id.logOutMenuItem:
                    transition = transitionLogout;
                    callback = logoutWorkflowCallback;
                    headersMap.put("x-api-key", HttpConstants.getApiStartKey());
                    headersMap.put("transition", "true");

                    UnifiedSignInUser user = new UnifiedSignInUser();
                    user.setEmail(ApplicationPreferences.getInstance().getUserId());
                    user.setDeviceToken(((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                            .getString(CarePayConstants.FCM_TOKEN, null));
                    UnifiedSignInDTO signInDTO = new UnifiedSignInDTO();
                    signInDTO.setUser(user);

                    payload = new Gson().toJson(signInDTO);
                    break;
                default:
                    drawer.closeDrawer(GravityCompat.START);
            }

            if (transition == null || transition.getUrl() == null) {
                drawer.closeDrawer(GravityCompat.START);
                return;
            }

            if (payload != null) {
                //do transition with payload
                getWorkflowServiceHelper().execute(transition, callback, payload, queryMap, headersMap);
            } else if (headersMap.isEmpty()) {
                //do regular transition
                getWorkflowServiceHelper().execute(transition, callback, queryMap);
            } else {
                //do transition with headers since no query params are required we can ignore them
                getWorkflowServiceHelper().execute(transition, callback, queryMap, headersMap);
            }

            drawer.closeDrawer(GravityCompat.START);
        }
    };

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    private WorkflowServiceCallback demographicsSettingsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.setAccessPaymentsBalances(false);
            navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            hideProgressDialog();
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback logoutWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            getAppAuthorizationHelper().setAccessToken(null);
            navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    protected WorkflowServiceCallback notificationsWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.setAccessPaymentsBalances(false);
            navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };


    public static void setTransitionBalance(TransitionDTO transitionBalance) {
        MenuPatientActivity.transitionBalance = transitionBalance;
    }

    public static void setTransitionProfile(TransitionDTO transitionProfile) {
        MenuPatientActivity.transitionProfile = transitionProfile;
    }

    public static void setTransitionAppointments(TransitionDTO transitionAppointments) {
        MenuPatientActivity.transitionAppointments = transitionAppointments;
    }

    public static void setTransitionForms(TransitionDTO transitionForms) {
        MenuPatientActivity.transitionForms = transitionForms;
    }

    public static void setTransitionMyHealth(TransitionDTO transitionMyHealth) {
        MenuPatientActivity.transitionMyHealth = transitionMyHealth;
    }

    public static void setTransitionLogout(TransitionDTO transitionLogout) {
        MenuPatientActivity.transitionLogout = transitionLogout;
    }

    public static void setTransitionNotifications(TransitionDTO transitionNotifications) {
        MenuPatientActivity.transitionNotifications = transitionNotifications;
    }

    public static void setTransitionRetail(TransitionDTO transitionRetail) {
        MenuPatientActivity.transitionRetail = transitionRetail;
    }

    public static void setTransitionMessaging(TransitionDTO transitionMessaging) {
        MenuPatientActivity.transitionMessaging = transitionMessaging;
    }

    public static TransitionDTO getTransitionAppointments() {
        return transitionAppointments;
    }

    public static TransitionDTO getTransitionForms() {
        return transitionForms;
    }

    public static TransitionDTO getTransitionNotifications() {
        return transitionNotifications;
    }

    public static TransitionDTO getTransitionLogout() {
        return transitionLogout;
    }

    public static TransitionDTO getTransitionBalance() {
        return transitionBalance;
    }

    public static TransitionDTO getTransitionRetail() {
        return transitionRetail;
    }

    public static TransitionDTO getTransitionMyHealth() {
        return transitionMyHealth;
    }

    public static TransitionDTO getTransitionMessaging() {
        return transitionMessaging;
    }

    protected void displayMessagesScreen() {
        //backward compat for pending notification merge
        startActivity(MessagesActivity.class);
    }

    /**
     * Display toolbar.
     *
     * @param visibility the visibility
     */
    public void displayToolbar(boolean visibility, String toolBarTitle) {
        TextView toolbarText = findViewById(R.id.toolbar_title);
        if (toolBarTitle != null) {
            toolbarText.setText(StringUtil.isNullOrEmpty(toolBarTitle) ? CarePayConstants.NOT_DEFINED : toolBarTitle);
        }
        if (visibility) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(getResources().getDimension(R.dimen.respons_toolbar_elevation));
                getSupportActionBar().show();
            }
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void updateBadgeCounters() {
        TransitionDTO badgeCounterTransition = ApplicationPreferences.getInstance().getBadgeCounterTransition();
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(badgeCounterTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                UnifiedSignInResponse dto = DtoHelper.getConvertedDTO(UnifiedSignInResponse.class, workflowDTO);
                ApplicationPreferences.getInstance()
                        .setMessagesBadgeCounter(dto.getPayload().getBadgeCounter().getMessages());
                ApplicationPreferences.getInstance()
                        .setFormsBadgeCounter(dto.getPayload().getBadgeCounter().getPendingForms());
                if (isVisible) {
                    updateBadgeCounterViews();
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {

            }
        }, queryMap);
    }

    protected void selectMenuItem(int menuItemId) {
        CustomMenuItem menuItem = findViewById(menuItemId);
        menuItem.select();
    }

    protected void updateBadgeCounterViews() {
        int messageBadgeCounter = ApplicationPreferences.getInstance().getMessagesBadgeCounter();
        CustomMenuItem messageMenuItem = navigationView.findViewById(R.id.messagesMenuItem);
        if (messageBadgeCounter > 0) {
            messageMenuItem.setAlertCounter(messageBadgeCounter);
        }

        int formBadgeCounter = ApplicationPreferences.getInstance().getFormsBadgeCounter();
        CustomMenuItem formsBadgeCounter = navigationView.findViewById(R.id.formsMenuItem);
        if (formBadgeCounter > 0) {
            formsBadgeCounter.setAlertCounter(formBadgeCounter);
        }

        int badgeSums = messageBadgeCounter + formBadgeCounter;
        if (badgeSums > 0) {
            //Uncomment this for showing the number of pending badges in the hamburger menu
//            badgeDrawable.setText(String.valueOf(badgeSums));
            badgeDrawable.setEnabled(true);
        } else {
            badgeDrawable.setEnabled(false);
        }
    }

    private BroadcastReceiver badgeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBadgeCounters();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (badgeReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(badgeReceiver);
        }
    }
}
