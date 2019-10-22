package com.carecloud.carepay.patient.delegate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.delegate.adapters.ProfilePermissionsRecyclerAdapter;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileConfirmationCallback;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileManagementInterface;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-06-14.
 */
public class ProfileDetailFragment extends BaseDialogFragment implements ProfilePermissionsRecyclerAdapter.ProfileEditInterface {

    private ProfileManagementInterface callback;
    private DelegateDto delegateDto;
    private ProfileDto selectedProfile;

    public static ProfileDetailFragment newInstance(int profilePosition) {
        Bundle args = new Bundle();
        args.putInt("profilePosition", profilePosition);
        ProfileDetailFragment fragment = new ProfileDetailFragment();
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
        selectedProfile = delegateDto.getPayload().getUserLinks().getRepresentedUsers()
                .get(getArguments().getInt("profilePosition"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpUi(view);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.toolbarTitle);
        title.setText(Label.getLabel("patient.delegate.profileList.title.label"));
    }

    private void setUpUi(View view) {
        TextView profileShortNameTextView = view.findViewById(R.id.profileShortNameTextView);
        profileShortNameTextView.setText(StringUtil.getShortName(selectedProfile.getProfile().getDemographics()
                .getPayload().getPersonalDetails().getFullName()));

        TextView profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileNameTextView.setText(getProfileName(selectedProfile.getProfile().getDemographics()));

        TextView profileRelationTextView = view.findViewById(R.id.profileRelationTextView);
        profileRelationTextView.setText(StringUtil.capitalize(selectedProfile.getProfile()
                .getLinks().get(0).getRelationType()));


        ImageView profileImageView = view.findViewById(R.id.profileImageView);
        PicassoHelper.get().loadImage(profileImageView.getContext(),
                profileImageView,
                profileShortNameTextView, selectedProfile.getProfile().getDemographics().getPayload()
                        .getPersonalDetails().getProfilePhoto());

        TextView dobValueTextView = view.findViewById(R.id.dobValueTextView);
        dobValueTextView.setText(DateUtil.getInstance().setDateRaw(selectedProfile.getProfile()
                .getDemographics().getPayload()
                .getPersonalDetails().getDateOfBirth()).toStringWithFormatMmSlashDdSlashYyyy());
        TextView genderValueTextView = view.findViewById(R.id.genderValueTextView);
        genderValueTextView.setText(selectedProfile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getGender());
        TextView phoneTypeValueTextView = view.findViewById(R.id.phoneTypeValueTextView);
        String phoneNumberType = selectedProfile.getProfile().getDemographics().getPayload()
                .getAddress().getPhoneNumberType();
        if (StringUtil.isNullOrEmpty(phoneNumberType)) {
            phoneNumberType = "N/A";
        }
        phoneTypeValueTextView.setText(phoneNumberType);

        TextView phoneNumberValueTextView = view.findViewById(R.id.phoneNumberValueTextView);
        String phoneType = StringUtil.formatPhoneNumber(selectedProfile.getProfile()
                .getDemographics().getPayload().getAddress().getPhone());
        if (StringUtil.isNullOrEmpty(phoneType)) {
            phoneType = "N/A";
        }
        phoneNumberValueTextView.setText(phoneType);
        
        TextView emailValueTextView = view.findViewById(R.id.emailValueTextView);
        emailValueTextView.setText(selectedProfile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getEmailAddress());

        setUpPermissionList(view);

        Button mergeButton = view.findViewById(R.id.mergeButton);
        if (delegateDto.getPayload().getDelegate() == null) {
            mergeButton.setVisibility(View.VISIBLE);
            mergeButton.setOnClickListener(view1 -> callback.addFragment(MergeProfileListFragment
                            .newInstance(selectedProfile),
                    true));
        }
    }

    private void setUpPermissionList(View view) {
        RecyclerView permissionsRecyclerView = view.findViewById(R.id.permissionsRecyclerView);
        permissionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfilePermissionsRecyclerAdapter adapter = new ProfilePermissionsRecyclerAdapter(selectedProfile
                .getProfile().getLinks(), getPracticesMap(delegateDto),
                delegateDto.getPayload().getDelegate() == null);
        adapter.setCallback(this);
        permissionsRecyclerView.setAdapter(adapter);
    }

    private Map<String, UserPracticeDTO> getPracticesMap(DelegateDto delegateDto) {
        Map<String, UserPracticeDTO> map = new HashMap<>();
        for (UserPracticeDTO practiceDTO : delegateDto.getPayload().getUserLinks().getDelegatePracticeInformation()) {
            map.put(practiceDTO.getPracticeId(), practiceDTO);
        }
        return map;
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }

    @Override
    public void onDisconnectClicked(ProfileLink profileLink) {
        showConfirmDialog(selectedProfile, profileLink);
    }

    private void showConfirmDialog(ProfileDto profile, ProfileLink profileLink) {
        ConfirmProfileActionDialogFragment fragment = ConfirmProfileActionDialogFragment.newInstance(profile,
                Label.getLabel("profile.confirmAction.dialog.title.disconnect"),
                String.format(Label.getLabel("profile.confirmAction.dialog.message.disconnect"),
                        getProfileName(selectedProfile.getProfile().getDemographics())));
        fragment.setCallback(new ProfileConfirmationCallback() {
            @Override
            public void confirmAction(ProfileDto profile) {
                disconnectProfile(profileLink);
            }

            @Override
            public void cancel() {

            }
        });
        fragment.show(getFragmentManager(), "merge");
    }

    private void disconnectProfile(ProfileLink profileLink) {
        JsonObject postBodyObj = new JsonObject();
        postBodyObj.addProperty("practice_mgmt", profileLink.getPracticeMgmt());
        postBodyObj.addProperty("practice_id", profileLink.getPracticeId());
        postBodyObj.addProperty("patient_id", profileLink.getPatientId());
        postBodyObj.addProperty("delegate_user_id", profileLink.getDelegateUserId());
        postBodyObj.addProperty("action", "REVOKE_ACCESS");
        TransitionDTO nextPageTransition = delegateDto.getMetadata().getTransitions().getAction();
        getWorkflowServiceHelper().execute(nextPageTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                DelegateDto delegateDto = DtoHelper.getConvertedDTO(DelegateDto.class, workflowDTO);
                callback.updateProfiles(delegateDto.getPayload().getUserLinks());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                Log.e("Error", "error");
            }
        }, postBodyObj.toString());
    }
}
