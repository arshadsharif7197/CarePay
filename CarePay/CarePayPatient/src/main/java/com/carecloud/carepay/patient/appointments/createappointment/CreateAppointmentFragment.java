package com.carecloud.carepay.patient.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.adapters.PracticesAdapter;
import com.carecloud.carepay.patient.appointments.createappointment.location.LocationListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.provider.ProviderListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.visitType.VisitTypeListFragment;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseCreateAppointmentFragment;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/15/19.
 */
public class CreateAppointmentFragment extends BaseCreateAppointmentFragment implements CreateAppointmentFragmentInterface {

    public static CreateAppointmentFragment newInstance() {
        return new CreateAppointmentFragment();
    }

    public static CreateAppointmentFragment newInstance(UserPracticeDTO userPracticeDTO,
                                                        AppointmentResourcesItemDTO selectedResource,
                                                        VisitTypeDTO selectedVisitTypeDTO,
                                                        LocationDTO selectedLocation) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, userPracticeDTO);
        DtoHelper.bundleDto(args, selectedResource);
        DtoHelper.bundleDto(args, selectedVisitTypeDTO);
        DtoHelper.bundleDto(args, selectedLocation);
        CreateAppointmentFragment fragment = new CreateAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        showPracticeList(view);
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
        title.setText(Label.getLabel("appointments_heading"));
        callback.displayToolbar(false, null);
    }

    private void showPracticeList(View view) {
        RecyclerView practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        if (appointmentsModelDto.getPayload().getUserPractices().size() > 1) {
            practicesRecyclerView.setVisibility(View.VISIBLE);
            practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            PracticesAdapter adapter = new PracticesAdapter(appointmentsModelDto.getPayload().getUserPractices());
            adapter.setCallback(new PracticesAdapter.PracticeSelectInterface() {
                @Override
                public void onPracticeSelected(UserPracticeDTO userPracticeDTO) {
                    if (!selectedPractice.getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                        resetForm();
                    }
                    selectedPractice = userPracticeDTO;
                }
            });
            practicesRecyclerView.setAdapter(adapter);
        } else {
            practicesRecyclerView.setVisibility(View.GONE);
        }
    }

    protected void showLocationList(UserPracticeDTO selectedPractice,
                                    AppointmentResourcesItemDTO selectedProvider,
                                    VisitTypeDTO selectedVisitType) {
        LocationListFragment fragment = LocationListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedProvider);
        callback.showFragment(fragment);
    }

    protected void showVisitTypeList(UserPracticeDTO selectedPractice,
                                     AppointmentResourcesItemDTO selectedProvider,
                                     LocationDTO selectedLocation) {
        VisitTypeListFragment fragment = VisitTypeListFragment
                .newInstance(selectedPractice, selectedLocation, selectedProvider);
        callback.showFragment(fragment);
    }

    protected void showProviderList(UserPracticeDTO selectedPractice,
                                    VisitTypeDTO selectedVisitType,
                                    LocationDTO selectedLocation) {
        ProviderListFragment fragment = ProviderListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedLocation);
        callback.showFragment(fragment);
    }
}
