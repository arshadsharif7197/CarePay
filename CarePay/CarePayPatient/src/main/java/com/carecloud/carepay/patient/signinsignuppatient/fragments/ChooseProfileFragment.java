package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.patient.session.PatientSessionService;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 3/21/19.
 */
public class ChooseProfileFragment extends BaseDialogFragment {

    private UserLinks userLinks;

    public static ChooseProfileFragment newInstance(TransitionDTO authenticateTransition,
                                                    String user,
                                                    String password,
                                                    UserLinks userLinks) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, authenticateTransition);
        DtoHelper.bundleDto(args, userLinks);
        args.putString("user", user);
        args.putString("password", password);
        ChooseProfileFragment fragment = new ChooseProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        userLinks = DtoHelper.getConvertedDTO(UserLinks.class, args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        hideProgressDialog();
        return inflater.inflate(R.layout.fragment_choose_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!userLinks.getRepresentedUsers().isEmpty()) {
            view.findViewById(R.id.mainView).setVisibility(View.VISIBLE);
            LinearLayout profilesContainer = view.findViewById(R.id.profilesContainer);
            userLinks.getRepresentedUsers().add(0, createProfileForLoggedUser(userLinks.getLoggedInUser()));
            int count = 0;
            LinearLayout rowLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams rllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rllp.weight = 2;
            rllp.gravity = Gravity.CENTER_HORIZONTAL;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (ProfileDto profileDto : userLinks.getRepresentedUsers()) {
                if (count % 2 == 0) {
                    rowLayout = new LinearLayout(getContext());
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    rowLayout.setLayoutParams(rllp);
                    rowLayout.setWeightSum(2);
                    rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                    profilesContainer.addView(rowLayout);
                }
                createProfileView(rowLayout, inflater, profileDto);
                count++;
            }
        } else {
            callStartUpService();
        }
    }

    private void createProfileView(LinearLayout rowLayout, LayoutInflater inflater, final ProfileDto profileDto) {
        View profileView = inflater.inflate(R.layout.layout_profile, rowLayout, false);
        if (profileDto.getProfile().isDelegate()) {
            View headerView = profileView.findViewById(R.id.profileHeaderView);
            headerView.setBackground(getContext().getResources()
                    .getDrawable(R.drawable.top_round_charcoal_background));
        }
        TextView profileNameTextView = profileView.findViewById(R.id.profileNameTextView);
        profileNameTextView.setText(String.format("%s\n%s",
                StringUtil.capitalize(profileDto.getProfile().getDemographics().getPayload().getPersonalDetails().getFirstName()),
                StringUtil.capitalize(profileDto.getProfile().getDemographics().getPayload().getPersonalDetails().getLastName())));

        TextView profileShortNameTextView = profileView.findViewById(R.id.profileShortNameTextView);
        profileShortNameTextView.setText(StringUtil.getShortName(StringUtil
                .getCapitalizedUserName(profileDto.getProfile().getDemographics().getPayload()
                        .getPersonalDetails().getFirstName(), profileDto.getProfile().getDemographics()
                        .getPayload().getPersonalDetails().getLastName())));
        ImageView profileImageView = profileView.findViewById(R.id.profileImageView);
        PicassoHelper.get().loadImage(getContext(), profileImageView, profileShortNameTextView,
                profileDto.getProfile().getDemographics().getPayload().getPersonalDetails().getProfilePhoto(),
                getContext().getResources().getDimensionPixelSize(R.dimen.profileImageViewSize));
        rowLayout.addView(profileView);
        profileView.setOnClickListener(view -> {
            ApplicationPreferences.getInstance().setProfileId(profileDto.getProfile().getId());
            ApplicationPreferences.getInstance().setUserFullName(StringUtil
                    .getCapitalizedUserName(profileDto.getProfile().getDemographics().getPayload()
                            .getPersonalDetails().getFirstName(), profileDto.getProfile().getDemographics()
                            .getPayload().getPersonalDetails().getLastName()));
            ApplicationPreferences.getInstance().setUserId(profileDto.getProfile().getDemographics().getPayload()
                    .getPersonalDetails().getEmailAddress());
            ApplicationPreferences.getInstance().setUserPhotoUrl(profileDto.getProfile().getDemographics().getPayload()
                    .getPersonalDetails().getProfilePhoto());
            callStartUpService();
        });
    }

    private ProfileDto createProfileForLoggedUser(Profile profile) {
        profile.setDelegate(true);
        profile.setBreezeUser(true);
        profile.setId("");
        profile.setName(StringUtil
                .getCapitalizedUserName(profile.getDemographics().getPayload()
                        .getPersonalDetails().getFirstName(), profile.getDemographics()
                        .getPayload().getPersonalDetails().getLastName()));
        ProfileDto profileDto = new ProfileDto();
        profileDto.setProfile(profile);
        return profileDto;
    }

    private void callStartUpService() {
        Bundle args = getArguments();
        String user = args.getString("user");
        String password = args.getString("password");
        TransitionDTO authenticateTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, args);
        Map<String, String> query = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        String languageId = ApplicationPreferences.getInstance().getUserLanguage();
        header.put("Accept-Language", languageId);
        getWorkflowServiceHelper().execute(authenticateTransition, getSignInCallback(user, password), query, header);
    }

    private WorkflowServiceCallback getSignInCallback(final String user, final String password) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                boolean shouldShowNotificationScreen = getArguments()
                        .getBoolean(CarePayConstants.OPEN_NOTIFICATIONS);
                ApplicationPreferences.getInstance().setUserPhotoUrl(null);
                ApplicationPreferences.getInstance().setUserFullName("N/A");
                ApplicationPreferences.getInstance().writeObjectToSharedPreference(CarePayConstants
                        .DEMOGRAPHICS_ADDRESS_BUNDLE, null);
                ApplicationPreferences.getInstance().setLandingScreen(true);
                Intent serviceIntent = new Intent(getActivity(), PatientSessionService.class);
                getActivity().startService(serviceIntent);
                if (shouldShowNotificationScreen) {
                    manageNotificationAsLandingScreen(workflowDTO.toString());
                } else {
                    PatientNavigationHelper.navigateToWorkflow(getActivity(),
                            workflowDTO, getActivity().getIntent().getExtras());
                }
                ApplicationPreferences.getInstance().setUserName(user);
                String encryptedPassword = EncryptionUtil.encrypt(getContext(), password, user);
                ApplicationPreferences.getInstance().setUserPassword(encryptedPassword);

                MyHealthDto myHealthDto = DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO);
                String userId;
                if (myHealthDto.getPayload().getUserLinks().getLoggedInUser() != null) {
                    userId = myHealthDto.getPayload().getUserLinks().getLoggedInUser().getUserId();
                } else {
                    userId = myHealthDto.getPayload().getDemographicDTO().getMetadata().getUserId();
                }
                MixPanelUtil.setUser(getContext(), userId, myHealthDto.getPayload().getDemographicDTO().getPayload());

                MixPanelUtil.logEvent(getString(R.string.event_signin_loginSuccess),
                        getString(R.string.param_login_type),
                        getString(R.string.login_password));
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    private void manageNotificationAsLandingScreen(String workflow) {
        final Gson gson = new Gson();
        final MyHealthDto landingDto = gson.fromJson(workflow, MyHealthDto.class);

        showProgressDialog();
        TransitionDTO transition = landingDto.getMetadata().getLinks().getNotifications();
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                NotificationsDTO notificationsDTO = gson
                        .fromJson(workflowDTO.toString(), NotificationsDTO.class);
                notificationsDTO.getPayload().setPracticePatientIds(landingDto
                        .getPayload().getPracticePatientIds());

                notificationsDTO.getMetadata().getLinks().setPatientBalances(landingDto.getMetadata()
                        .getLinks().getPatientBalances());
                notificationsDTO.getMetadata().getTransitions().setLogout(landingDto.getMetadata()
                        .getTransitions().getLogout());
                notificationsDTO.getMetadata().getLinks().setProfileUpdate(landingDto.getMetadata()
                        .getLinks().getProfileUpdate());
                notificationsDTO.getMetadata().getLinks().setAppointments(landingDto.getMetadata()
                        .getLinks().getAppointments());
                notificationsDTO.getMetadata().getLinks().setNotifications(landingDto.getMetadata()
                        .getLinks().getNotifications());
                notificationsDTO.getMetadata().getLinks().setMyHealth(landingDto.getMetadata()
                        .getLinks().getMyHealth());

                WorkflowDTO notificationWorkFlow = gson.fromJson(gson.toJson(notificationsDTO),
                        WorkflowDTO.class);
                hideProgressDialog();
                PatientNavigationHelper.setAccessPaymentsBalances(false);
                Bundle args = new Bundle();
                args.putBoolean(CarePayConstants.OPEN_NOTIFICATIONS, true);
                PatientNavigationHelper.navigateToWorkflow(getContext(), notificationWorkFlow, args);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        });
    }
}
