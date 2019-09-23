package com.carecloud.carepay.patient.delegate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileConfirmationCallback;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileManagementInterface;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepay.patient.menu.ProfilesMenuRecyclerAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pjohnson on 2019-06-13.
 */
public class MergeProfileListFragment extends BaseDialogFragment implements ProfilesMenuRecyclerAdapter.ProfileMenuInterface {

    private ProfileManagementInterface callback;
    private DelegateDto delegateDto;
    private ProfileDto selectedProfile;

    public static MergeProfileListFragment newInstance(ProfileDto selectedProfile) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedProfile);
        MergeProfileListFragment fragment = new MergeProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileManagementInterface) {
            callback = (ProfileManagementInterface) context;
        } else {
            throw new ClassCastException("context must implements ProfileManagementInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        delegateDto = (DelegateDto) callback.getDto();
        selectedProfile = DtoHelper.getConvertedDTO(ProfileDto.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profiles_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpProfilesList(view);
    }

    private void setUpProfilesList(View view) {
        RecyclerView profilesRecyclerView = view.findViewById(R.id.profilesRecyclerView);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ProfileDto> filteredProfiles = filterProfiles(selectedProfile,
                delegateDto.getPayload().getUserLinks().getRepresentedUsers());
        Collections.sort(filteredProfiles, (profileDto1, profileDto2) -> getProfileName(profileDto1.getProfile()
                .getDemographics()).compareTo(getProfileName(profileDto2.getProfile().getDemographics())));
        ProfilesMenuRecyclerAdapter adapter = new ProfilesMenuRecyclerAdapter(filteredProfiles,
                ProfilesMenuRecyclerAdapter.BIG_PROFILE_VIEW_TYPE);
        adapter.setCallback(this);
        profilesRecyclerView.setAdapter(adapter);
    }

    private List<ProfileDto> filterProfiles(ProfileDto selectedProfile, List<ProfileDto> representedUsers) {
        List<ProfileDto> filteredList = new ArrayList<>();
        for (ProfileDto profile : representedUsers) {
            if (!profile.getProfile().getId().equals(selectedProfile.getProfile().getId())) {
                filteredList.add(profile);
            }
        }
        return filteredList;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.toolbarTitle);
        title.setText(Label.getLabel("patient.delegate.profileList.title.merge"));
    }

    @Override
    public void onProfileClicked(ProfileDto profile, int position) {
        if (canBeMerged(selectedProfile.getProfile(), profile.getProfile())) {
            showMergeDialog(selectedProfile, profile);
        } else {
            showErrorDialog();
        }
    }

    private boolean canBeMerged(Profile selectedProfile, Profile profile) {
        boolean canBeMerged = true;
        for (ProfileLink selectedProfileLink : selectedProfile.getLinks()) {
            String practiceId = selectedProfileLink.getPracticeId();
            for (ProfileLink profileLink : profile.getLinks()) {
                if (practiceId.equals(profileLink.getPracticeId())) {
                    canBeMerged = false;
                    break;
                }
            }
            if (!canBeMerged) {
                return false;
            }
        }
        return true;
    }

    private void showMergeDialog(ProfileDto selectedProfile, ProfileDto profile) {
        String dialogMessage = String.format("The demographic information\nfrom %s will be\nmerged with and " +
                        "replaced by the\ndemographics of %s.\n\nThis action cannot be undone." +
                        " Do\nyou wish to continue?", getProfileName(selectedProfile.getProfile().getDemographics()),
                getProfileName(profile.getProfile().getDemographics()));
        ConfirmProfileActionDialogFragment fragment = ConfirmProfileActionDialogFragment.newInstance(profile,
                Label.getLabel("profile.confirmAction.dialog.title.merge"), dialogMessage);
        fragment.setCallback(new ProfileConfirmationCallback() {
            @Override
            public void confirmAction(ProfileDto profile) {
                mergeProfiles(MergeProfileListFragment.this.selectedProfile.getProfile(), profile.getProfile());
            }

            @Override
            public void cancel() {

            }
        });
        fragment.show(getFragmentManager(), "merge");
    }

    private void mergeProfiles(Profile profile, Profile profile1) {
        JsonObject postBodyObj = new JsonObject();
        postBodyObj.addProperty("delegate_user_id", profile.getDelegateUser());
        postBodyObj.addProperty("from_profile_id", profile.getId());
        postBodyObj.addProperty("to_profile_id", profile1.getId());
        TransitionDTO nextPageTransition = delegateDto.getMetadata().getTransitions().getMerge();
        getWorkflowServiceHelper().execute(nextPageTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                DelegateDto delegateDto = DtoHelper.getConvertedDTO(DelegateDto.class, workflowDTO);
                dismiss();
                getFragmentManager().popBackStackImmediate();
                callback.updateProfiles(delegateDto.getPayload().getUserLinks());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e("Error", "error");
            }
        }, postBodyObj.toString());
    }

    private void showErrorDialog() {
        LargeAlertDialogFragment fragment = LargeAlertDialogFragment
                .newInstance(Label.getLabel("profile.errorMerge.dialog.message.merge"),
                        Label.getLabel("ok"));
        fragment.show(getFragmentManager(), "merge");
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }
}
