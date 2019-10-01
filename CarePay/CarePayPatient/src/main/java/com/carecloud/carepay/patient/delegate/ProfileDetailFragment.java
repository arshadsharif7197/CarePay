package com.carecloud.carepay.patient.delegate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-06-14.
 */
public class ProfileDetailFragment extends BaseDialogFragment {

    private FragmentActivityInterface callback;
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
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        } else {
            throw new ClassCastException("context must implements FragmentActivityInterface");
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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        phoneTypeValueTextView.setText(selectedProfile.getProfile().getDemographics().getPayload()
                .getAddress().getPhoneNumberType());
        TextView phoneNumberValueTextView = view.findViewById(R.id.phoneNumberValueTextView);
        phoneNumberValueTextView.setText(StringUtil.formatPhoneNumber(selectedProfile.getProfile()
                .getDemographics().getPayload().getAddress().getPhone()));
        TextView emailValueTextView = view.findViewById(R.id.emailValueTextView);
        emailValueTextView.setText(selectedProfile.getProfile().getDemographics().getPayload()
                .getPersonalDetails().getEmailAddress());

        setUpPermissionList(view);
    }

    private void setUpPermissionList(View view) {
        RecyclerView permissionsRecyclerView = view.findViewById(R.id.permissionsRecyclerView);
        permissionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        permissionsRecyclerView.setAdapter(new ProfilePermissionsRecyclerAdapter(selectedProfile.getProfile()
                .getLinks(), getPracticesMap(delegateDto)));
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
}
