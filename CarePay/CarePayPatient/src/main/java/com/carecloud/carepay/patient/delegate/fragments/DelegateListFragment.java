package com.carecloud.carepay.patient.delegate.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.carecloud.carepay.patient.delegate.adapters.DelegatesRecyclerAdapter;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepay.patient.menu.ProfilesMenuRecyclerAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Collections;

/**
 * @author pjohnson on 2019-06-13.
 */
public class DelegateListFragment extends BaseDialogFragment implements DelegatesRecyclerAdapter.ManageDelegateInterface {

    private FragmentActivityInterface callback;
    private DemographicDTO delegateDto;

    public static DelegateListFragment newInstance() {
        Bundle args = new Bundle();
        DelegateListFragment fragment = new DelegateListFragment();
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
        delegateDto = (DemographicDTO) callback.getDto();
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
        setUpDelegatesList(view);
    }

    private void setUpDelegatesList(View view) {
        Collections.sort(delegateDto.getPayload()
                .getUserLinks().getDelegates(), (o1, o2) -> getProfileName(o1.getProfile().getDemographics())
                .compareTo(getProfileName(o2.getProfile().getDemographics())));
        RecyclerView delegatesRecyclerView = view.findViewById(R.id.profilesRecyclerView);
        delegatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DelegatesRecyclerAdapter adapter = new DelegatesRecyclerAdapter(delegateDto.getPayload()
                .getUserLinks().getDelegates(), ProfilesMenuRecyclerAdapter.BIG_PROFILE_VIEW_TYPE);
        adapter.setCallback(this);
        delegatesRecyclerView.setAdapter(adapter);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.toolbarTitle);
        title.setText(Label.getLabel("patient.delegate.delegateList.title.label"));
    }

    @Override
    public void onProfileClicked(ProfileDto profile, int position) {
        callback.addFragment(DelegateDetailFragment.newInstance(position), true);
    }

    public void refreshList(UserLinks userLinks) {
        delegateDto.getPayload().setUserLinks(userLinks);
        setUpDelegatesList(getView());
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }
}
