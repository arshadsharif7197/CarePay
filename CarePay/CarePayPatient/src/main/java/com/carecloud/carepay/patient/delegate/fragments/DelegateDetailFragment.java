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
import com.carecloud.carepay.patient.delegate.adapters.DelegatePermissionRecyclerAdapter;
import com.carecloud.carepay.patient.delegate.interfaces.DelegateManagementInterface;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileConfirmationCallback;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.Permission;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 2019-06-14.
 */
public class DelegateDetailFragment extends BaseDialogFragment
        implements DelegatePermissionRecyclerAdapter.DelegateEditInterface {

    private DelegateManagementInterface callback;
    private DemographicDTO delegateDto;
    private ProfileDto selectedProfile;

    public static DelegateDetailFragment newInstance(int profilePosition) {
        Bundle args = new Bundle();
        args.putInt("profilePosition", profilePosition);
        DelegateDetailFragment fragment = new DelegateDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DelegateManagementInterface) {
            callback = (DelegateManagementInterface) context;
        } else {
            throw new ClassCastException("context must implements ProfileManagementInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        delegateDto = (DemographicDTO) callback.getDto();
        selectedProfile = delegateDto.getPayload().getUserLinks().getDelegates()
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
        title.setText(Label.getLabel("patient.delegate.delegateList.title.label"));
    }

    private void setUpUi(View view) {
        TextView profileShortNameTextView = view.findViewById(R.id.profileShortNameTextView);
        profileShortNameTextView.setText(StringUtil.getShortName(selectedProfile.getProfile().getDelegateDemographics()
                .getPayload().getPersonalDetails().getFullName()));

        TextView profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileNameTextView.setText(getProfileName(selectedProfile.getProfile().getDelegateDemographics()));

        TextView profileRelationTextView = view.findViewById(R.id.profileRelationTextView);
        profileRelationTextView.setText(StringUtil.capitalize(selectedProfile.getProfile()
                .getLinks().get(0).getRelationType()));


        ImageView profileImageView = view.findViewById(R.id.profileImageView);
        PicassoHelper.get().loadImage(profileImageView.getContext(),
                profileImageView,
                profileShortNameTextView, selectedProfile.getProfile().getDelegateDemographics().getPayload()
                        .getPersonalDetails().getProfilePhoto());

        TextView dobValueTextView = view.findViewById(R.id.dobValueTextView);
        dobValueTextView.setText(DateUtil.getInstance().setDateRaw(selectedProfile.getProfile()
                .getDelegateDemographics().getPayload()
                .getPersonalDetails().getDateOfBirth()).toStringWithFormatMmSlashDdSlashYyyy());
        TextView genderValueTextView = view.findViewById(R.id.genderValueTextView);
        genderValueTextView.setText(selectedProfile.getProfile().getDelegateDemographics().getPayload()
                .getPersonalDetails().getGender());
        TextView phoneTypeValueTextView = view.findViewById(R.id.phoneTypeValueTextView);
        phoneTypeValueTextView.setText(selectedProfile.getProfile().getDelegateDemographics().getPayload()
                .getAddress().getPhoneNumberType());
        TextView phoneNumberValueTextView = view.findViewById(R.id.phoneNumberValueTextView);
        phoneNumberValueTextView.setText(StringUtil.formatPhoneNumber(selectedProfile.getProfile()
                .getDelegateDemographics().getPayload().getAddress().getPhone()));
        TextView emailValueTextView = view.findViewById(R.id.emailValueTextView);
        emailValueTextView.setText(selectedProfile.getProfile().getDelegateDemographics().getPayload()
                .getPersonalDetails().getEmailAddress());

        setUpPermissionList(view);

        Button mergeButton = view.findViewById(R.id.mergeButton);
        mergeButton.setVisibility(View.GONE);
    }

    private void setUpPermissionList(View view) {
        RecyclerView permissionsRecyclerView = view.findViewById(R.id.permissionsRecyclerView);
        permissionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DelegatePermissionRecyclerAdapter adapter = new DelegatePermissionRecyclerAdapter(selectedProfile
                .getProfile().getLinks(), getPracticesMap(delegateDto),
                delegateDto.getPayload().getDelegate() == null);
        adapter.setCallback(this);
        permissionsRecyclerView.setAdapter(adapter);
    }

    private Map<String, UserPracticeDTO> getPracticesMap(DemographicDTO delegateDto) {
        Collections.sort(delegateDto.getPayload().getUserLinks().getDelegatePracticeInformation(),
                (o1, o2) -> o1.getPracticeName().compareTo(o2.getPracticeName()));
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

    private void showConfirmDialog(ProfileDto profile, ProfileLink profileLink) {
        ConfirmProfileActionDialogFragment fragment = ConfirmProfileActionDialogFragment.newInstance(profile,
                Label.getLabel("profile.confirmAction.dialog.title.revoke"),
                String.format(Label.getLabel("profile.confirmAction.dialog.message.revoke"),
                        getProfileName(profile.getProfile().getDelegateDemographics())));
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
                DelegateDetailFragment.this.delegateDto.getPayload().setUserLinks(delegateDto.getPayload().getUserLinks());
                callback.updateProfiles(delegateDto.getPayload().getUserLinks());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                Log.e("Error", "error");
            }
        }, postBodyObj.toString());
    }

    @Override
    public void onUpdatePermissionsClicked(ProfileLink profileLink, List<Permission> permissions) {
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", profileLink.getPracticeMgmt());
        query.put("practice_id", profileLink.getPracticeId());
        query.put("patient_id", profileLink.getPatientId());
        query.put("delegate_user_id", profileLink.getDelegateUserId());
        JsonObject postBodyObj = generateBody(permissions);
        TransitionDTO nextPageTransition = delegateDto.getMetadata().getTransitions().getUpdatePermissions();
        getWorkflowServiceHelper().execute(nextPageTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                DelegateDto delegateDto = DtoHelper.getConvertedDTO(DelegateDto.class, workflowDTO);
                DelegateDetailFragment.this.delegateDto.getPayload().setUserLinks(delegateDto.getPayload().getUserLinks());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                Log.e("Error", "error");
            }
        }, postBodyObj.toString(), query);
    }

    private JsonObject generateBody(List<Permission> permissions) {
        JsonObject jsonObject = new JsonObject();
        for (Permission permission : permissions) {
            JsonObject permissionJson = new JsonObject();
            permissionJson.addProperty("enabled", permission.isEnabled());
            jsonObject.add(permission.getKey(), permissionJson);
        }
        return jsonObject;
    }

    @Override
    public void onRevokedAccessClicked(ProfileLink profileLink) {
        showConfirmDialog(selectedProfile, profileLink);
    }
}
