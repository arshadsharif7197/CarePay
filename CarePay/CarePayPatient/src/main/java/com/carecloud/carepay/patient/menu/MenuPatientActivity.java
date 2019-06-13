package com.carecloud.carepay.patient.menu;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
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
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.customcomponents.CustomMenuItem;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 10/01/17
 */
public abstract class MenuPatientActivity extends BasePatientActivity implements ProfilesMenuRecyclerAdapter.ProfileMenuInterface {

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
    private static UserLinks profileData;

    protected BadgeDrawerToggle toggle;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    protected boolean toolbarVisibility = false;
    private BadgeDrawerArrowDrawable badgeDrawable;
    protected static String profileName;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        inflateDrawer();
        LocalBroadcastManager.getInstance(this).registerReceiver(badgeReceiver,
                new IntentFilter(CarePayConstants.UPDATE_BADGES_BROADCAST));
        ProfileDto selectedProfileDto = null;
        if (getApplicationPreferences().isLandingScreen()) {
            MyHealthDto initialDto = setInitialData();
            Profile selectedProfile = initialDto.getPayload().getDelegate();
            if (selectedProfile != null) {
                selectedProfile.setName(getProfileName(selectedProfile.getDemographics()));
                selectedProfile.setId(selectedProfile.getProfileId());
                profileName = StringUtil.capitalize(selectedProfile.getDemographics().getPayload()
                        .getPersonalDetails().getFirstName());
                selectedProfileDto = new ProfileDto();
                selectedProfileDto.setProfile(selectedProfile);
            }
        }
        if (profileData != null) {
            if (selectedProfileDto != null) {
                Profile loggedInUser = profileData.getLoggedInUser();
                loggedInUser.setBreezeUser(true);
                loggedInUser.setDelegate(true);
                loggedInUser.setProfileName(getProfileName(loggedInUser.getDemographics()));
                loggedInUser.setProfileId("");
                updateProfileList(selectedProfileDto, loggedInUser);
            } else {
                sortProfileList(profileData.getRepresentedUsers());
            }
            populateProfilesList(profileData);
        }
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }

    private MyHealthDto setInitialData() {
        MyHealthDto initialDto = getConvertedDTO(MyHealthDto.class);
        List<PracticePatientIdsDTO> practicePatientIds = initialDto.getPayload().getPracticePatientIds();
        if (!practicePatientIds.isEmpty()) {
            getApplicationPreferences().writeObjectToSharedPreference(
                    CarePayConstants.KEY_PRACTICE_PATIENT_IDS, practicePatientIds);
        }
        setTransitionBalance(initialDto.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(initialDto.getMetadata().getTransitions().getLogout());
        setTransitionProfile(initialDto.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(initialDto.getMetadata().getLinks().getAppointments());
        setTransitionNotifications(initialDto.getMetadata().getLinks().getNotifications());
        setTransitionMyHealth(initialDto.getMetadata().getLinks().getMyHealth());
        setTransitionRetail(initialDto.getMetadata().getLinks().getRetail());
        setTransitionForms(initialDto.getMetadata().getLinks().getUserForms());
        setTransitionMessaging(initialDto.getMetadata().getLinks().getMessaging());
        setProfileData(initialDto.getPayload().getUserLinks());

        ApplicationPreferences.getInstance().writeObjectToSharedPreference(CarePayConstants
                .DEMOGRAPHICS_ADDRESS_BUNDLE, initialDto.getPayload().getDemographicDTO().getPayload().getAddress());

        ApplicationPreferences.getInstance().setPracticesWithBreezeEnabled(initialDto.getPayload()
                .getPracticeInformation());
        ApplicationPreferences.getInstance().setUserPractices(initialDto.getPayload()
                .getPracticeInformation());

        ApplicationPreferences.getInstance().setUserFullName(getProfileName(initialDto.getPayload().getDemographicDTO()));

        String userImageUrl = initialDto.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getProfilePhoto();
        if (userImageUrl != null) {
            getApplicationPreferences().setUserPhotoUrl(userImageUrl);
        }
        getApplicationPreferences().setLandingScreen(false);

        return initialDto;
    }

    private void setProfileData(UserLinks userLinks) {
        profileData = userLinks;
    }

    protected void inflateDrawer() {
        setSupportActionBar(toolbar);
        toggle = new BadgeDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
        toggle.setDrawerArrowDrawable(badgeDrawable);
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.setCallback(new BadgeDrawerToggle.DrawerInterface() {
            @Override
            public void onDrawerClosed(View drawerView) {
                resetViews();
            }
        });
        toggle.syncState();

        navigationView.findViewById(R.id.appointmentMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.myHealthMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.paymentsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.messagesMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.formsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.shopMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.notificationsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.manageProfilesMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.settingsMenuItem).setOnClickListener(menuItemClickListener);
        navigationView.findViewById(R.id.logOutMenuItem).setOnClickListener(menuItemClickListener);

        final ImageView profileListTriggerIcon = findViewById(R.id.profileListTriggerIcon);
        profileListTriggerIcon.setSelected(false);
        profileListTriggerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleProfileList(profileListTriggerIcon);
                rotateIcon(v);
            }
        });
    }

    private void resetViews() {
        View profileListTriggerIcon = findViewById(R.id.profileListTriggerIcon);
        if (profileListTriggerIcon.isSelected()) {
            profileListTriggerIcon.setSelected(false);
            rotateIcon(profileListTriggerIcon);
        }
        View mainMenuItemContainer = findViewById(R.id.mainMenuItemContainer);
        View profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        mainMenuItemContainer.setVisibility(View.VISIBLE);
        profilesRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void populateProfilesList(UserLinks profileData) {
        if (MenuPatientActivity.profileData.getRepresentedUsers().size() > 0) {
            RecyclerView profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
            profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ProfilesMenuRecyclerAdapter adapter = new ProfilesMenuRecyclerAdapter(profileData.getRepresentedUsers(),
                    ProfilesMenuRecyclerAdapter.SMALL_PROFILE_VIEW_TYPE);
            adapter.setCallback(this);
            profilesRecyclerView.setAdapter(adapter);
        } else {
            findViewById(R.id.profileListTriggerIcon).setVisibility(View.GONE);
        }
    }

    @Override
    public void onProfileClicked(ProfileDto profile) {
        enableBadge(profile);
        updateProfileInfo(profile);
        updateProfileView();
        updateProfileList(profile);
        updateBadgeCounterViews();
        onProfileChanged(profile);
        updateMenu(profile);
        closeMenu();
    }

    private void updateMenu(ProfileDto profile) {
        if (profile.getProfile().isBreezeUser()) {
            navigationView.findViewById(R.id.settingsMenuItem).setVisibility(View.VISIBLE);
        } else {
            navigationView.findViewById(R.id.settingsMenuItem).setVisibility(View.GONE);
        }
    }

    private void showManageProfilesItemMenu(boolean show) {
        navigationView.findViewById(R.id.manageProfilesMenuItem).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void enableBadge(ProfileDto profile) {
        toggle.setBadgeEnabled(profile.getProfile().isDelegate());
    }

    protected void updateProfileList(ProfileDto selectedProfile) {
        updateProfileList(selectedProfile, getCurrentProfile());
    }

    protected void updateProfileList(ProfileDto selectedProfile, Profile currentProfile) {
        List<ProfileDto> profileList = new ArrayList<>();
        for (ProfileDto profileDto : profileData.getRepresentedUsers()) {
            if (!profileDto.getProfile().getId().equals(selectedProfile.getProfile().getId())) {
                profileList.add(profileDto);
            }
        }

        ProfileDto profileDto = new ProfileDto();
        if (currentProfile == null) {
            currentProfile = profileData.getLoggedInUser();
            currentProfile.setDelegate(true);
            currentProfile.setBreezeUser(true);
            currentProfile.setId("");
            currentProfile.setName(getProfileName(currentProfile.getDemographics()));
        } else {
            currentProfile.setId(currentProfile.getProfileId());
            currentProfile.setName(currentProfile.getProfileName());
        }
        profileDto.setProfile(currentProfile);
        profileList.add(profileDto);

        sortProfileList(profileList);
        profileData.setRepresentedUsers(profileList);
        populateProfilesList(profileData);
    }

    private void sortProfileList(List<ProfileDto> profileList) {
        Collections.sort(profileList, new Comparator<ProfileDto>() {
            @Override
            public int compare(ProfileDto o1, ProfileDto o2) {
                return getProfileName(o1.getProfile().getDemographics())
                        .compareTo(getProfileName(o2.getProfile().getDemographics()));
            }
        });
    }

    protected abstract Profile getCurrentProfile();

    private void rotateIcon(View view) {
        Animation rotation;
        if (view.isSelected()) {
            rotation = AnimationUtils.loadAnimation(this, R.anim.ic_profile_arrow_rotation);
        } else {
            rotation = AnimationUtils.loadAnimation(this, R.anim.ic_profile_arrow_rotation_unselected);
        }
        rotation.setRepeatCount(0);
        rotation.setDuration(250);
        view.startAnimation(rotation);
        rotation.setFillAfter(true);
    }

    private void toggleProfileList(ImageView profileListTriggerIcon) {
        View mainMenuItemContainer = findViewById(R.id.mainMenuItemContainer);
        View profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        boolean showProfilesRecyclerView = !profileListTriggerIcon.isSelected();
        showManageProfilesItemMenu(showProfilesRecyclerView);
        profilesRecyclerView.setVisibility(showProfilesRecyclerView ? View.VISIBLE : View.INVISIBLE);
        mainMenuItemContainer.setVisibility(showProfilesRecyclerView ? View.INVISIBLE : View.VISIBLE);
        profileListTriggerIcon.setSelected(!profileListTriggerIcon.isSelected());
    }

    private void updateProfileInfo(ProfileDto profile) {
        ApplicationPreferences.getInstance().setProfileId(profile.getProfile().getId());
        ApplicationPreferences.getInstance().setUserFullName(getProfileName(profile.getProfile().getDemographics()));
        ApplicationPreferences.getInstance().setUserId(profile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getEmailAddress());
        ApplicationPreferences.getInstance().setUserPhotoUrl(profile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getProfilePhoto());
        profileName = StringUtil.capitalize(profile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getFirstName());
    }

    private void updateProfileView() {
        TextView userFullNameTextView = navigationView.findViewById(R.id.userNameTextView);
        userFullNameTextView.setText(ApplicationPreferences.getInstance().getFullName());
        TextView menuUserEmailTextView = navigationView.findViewById(R.id.menuUserEmailTextView);
        menuUserEmailTextView.setText(getApplicationPreferences().getUserId());
        setUserImage(ApplicationPreferences.getInstance().getUserPhotoUrl());
    }

    protected abstract void onProfileChanged(ProfileDto profile);

    private void setUserImage(String imageUrl) {
        ImageView userImageView = navigationView.findViewById(R.id.appointmentDrawerIdImageView);
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            Picasso.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.icn_placeholder_user_profile_png)
                    .resize(160, 160)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(userImageView);
        } else {
            userImageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_placeholder_user_profile_png));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadgeCounterViews();
        updateProfileView();
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
                case R.id.manageProfilesMenuItem:
                    callback = manageProfilesCallback;
                    transition = transitionForms;
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
                    closeMenu();
            }

            if (transition == null || transition.getUrl() == null) {
                closeMenu();
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

            closeMenu();
        }
    };

    private void closeMenu() {
        View profileListTriggerIcon = findViewById(R.id.profileListTriggerIcon);
        profileListTriggerIcon.setSelected(false);
        rotateIcon(profileListTriggerIcon);
        drawer.closeDrawer(GravityCompat.START);
    }

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
            ApplicationPreferences.getInstance().setProfileId(null);
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

    private WorkflowServiceCallback manageProfilesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            workflowDTO.setState(NavigationStateConstants.DELEGATE_PROFILES);
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
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
        CustomMenuItem messageMenuItem = navigationView.findViewById(R.id.messagesMenuItem);
        CustomMenuItem formsBadgeCounter = navigationView.findViewById(R.id.formsMenuItem);
        if (StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getProfileId())) {
            int messageBadgeCounter = ApplicationPreferences.getInstance().getMessagesBadgeCounter();
            if (messageBadgeCounter > 0) {
                messageMenuItem.setAlertCounter(messageBadgeCounter);
            }

            int formBadgeCounter = ApplicationPreferences.getInstance().getFormsBadgeCounter();
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
        } else {
            messageMenuItem.hideBadgeCounter();
            formsBadgeCounter.hideBadgeCounter();
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

    protected String getScreenTitle(String mainTitle) {
        if (StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getProfileId())) {
            return mainTitle;
        } else {
            if (ApplicationPreferences.getInstance().getUserLanguage().equals("en")) {
                return String.format("%s's %s", profileName, mainTitle);
            } else {
                return String.format("%s de %s", mainTitle, profileName);
            }
        }
    }
}
