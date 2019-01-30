package com.carecloud.carepay.patient.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.location.BaseLocationListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/15/19.
 */
public class LocationListFragment extends BaseLocationListFragment {

    public static LocationListFragment newInstance(UserPracticeDTO selectedPractice,
                                                   VisitTypeDTO selectedVisitType,
                                                   AppointmentResourcesItemDTO selectedProvider) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedPractice);
        DtoHelper.bundleDto(args, selectedVisitType);
        DtoHelper.bundleDto(args, selectedProvider);
        LocationListFragment fragment = new LocationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("payment_choose_location"));
        callback.displayToolbar(false, null);
    }
}
