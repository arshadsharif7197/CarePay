package com.carecloud.carepay.patient.delegate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepay.patient.menu.ProfilesMenuRecyclerAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.profile.UserLinks;

/**
 * @author pjohnson on 2019-06-13.
 */
public class ProfileListFragment extends BaseDialogFragment implements ProfilesMenuRecyclerAdapter.ProfileMenuInterface {

    private FragmentActivityInterface callback;
    private DelegateDto delegateDto;

    public static ProfileListFragment newInstance() {
        Bundle args = new Bundle();
        ProfileListFragment fragment = new ProfileListFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profiles_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpProfilesList(view);
    }

    private void setUpProfilesList(View view) {
        RecyclerView profilesRecyclerView = view.findViewById(R.id.profilesRecyclerView);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfilesMenuRecyclerAdapter adapter = new ProfilesMenuRecyclerAdapter(delegateDto.getPayload()
                .getUserLinks().getRepresentedUsers(), ProfilesMenuRecyclerAdapter.BIG_PROFILE_VIEW_TYPE);
        adapter.setCallback(this);
        profilesRecyclerView.setAdapter(adapter);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.toolbarTitle);
        title.setText(Label.getLabel("patient.delegate.profileList.title.label"));
    }

    @Override
    public void onProfileClicked(ProfileDto profile, int position) {
        callback.addFragment(ProfileDetailFragment.newInstance(position), true);
    }

    public void refreshList(UserLinks userLinks) {
        delegateDto.getPayload().setUserLinks(userLinks);
        setUpProfilesList(getView());
    }
}
