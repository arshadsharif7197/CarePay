package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseCreateAppointmentFragment;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/15/19.
 */
public class CreateAppointmentFragment extends BaseCreateAppointmentFragment implements CreateAppointmentFragmentInterface {

    public static CreateAppointmentFragment newInstance(PatientModel patient) {
        Bundle args = new Bundle();
        args.putString("patientId", patient.getPatientId());
        CreateAppointmentFragment fragment = new CreateAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        patientId = getArguments().getString("patientId");
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
    }

    @Override
    protected void showAvailabilityFragment() {
        callback.showFragment(AvailabilityHourFragment.newInstance(AvailabilityHourFragment.SCHEDULE_MODE));
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("appointments_heading"));
        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        //Not Apply
    }
}
