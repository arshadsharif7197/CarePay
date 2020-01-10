package com.carecloud.carepay.patient.signinsignuppatient.fragments;

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
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.signinsignuppatient.SignInViewModel;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * @author pjohnson on 3/21/19.
 */
public class ChooseProfileFragment extends BaseDialogFragment {

    private UserLinks userLinks;
    private SignInViewModel viewModel;

    public static ChooseProfileFragment newInstance(String user,
                                                    String password) {
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("password", password);
        ChooseProfileFragment fragment = new ChooseProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(SignInViewModel.class);
        userLinks = viewModel.getUserLinksObservable().getValue();
        viewModel.getSignInResultNavigatorObservable().observe(this, workflowDTO -> {
            Log.e("Pablo", "Yeah");
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO, getActivity()
                    .getIntent().getExtras());
        });
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
            setProfileData(profileDto);
            signIn();
        });
    }

    private void setProfileData(ProfileDto profileDto) {
        ApplicationPreferences.getInstance().setProfileId(profileDto.getProfile().getId());
        ApplicationPreferences.getInstance().setUserFullName(StringUtil
                .getCapitalizedUserName(profileDto.getProfile().getDemographics().getPayload()
                        .getPersonalDetails().getFirstName(), profileDto.getProfile().getDemographics()
                        .getPayload().getPersonalDetails().getLastName()));
        ApplicationPreferences.getInstance().setUserId(profileDto.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getEmailAddress());
        ApplicationPreferences.getInstance().setUserPhotoUrl(profileDto.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getProfilePhoto());
    }

    private void signIn() {
        String user = getArguments().getString("user");
        String password = getArguments().getString("password");
        boolean openNotificationScreen = getArguments().getBoolean(CarePayConstants.OPEN_NOTIFICATIONS);
        viewModel.signInStep2(user, password, openNotificationScreen);
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
}
