package com.carecloud.carepay.patient.checkout;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.patient.appointments.createappointment.LocationListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.ProviderListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.VisitTypeListFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityMetadataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.ProvidersReasonDTO;
import com.carecloud.carepaylibray.checkout.BaseNextAppointmentFragment;

import java.util.ArrayList;

public class NextAppointmentFragment extends BaseNextAppointmentFragment {

    /**
     * new Instance of BaseNextAppointmentFragment
     */
    public NextAppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * @param appointmentId the appointment id to checkout
     * @return a new instance of BaseNextAppointmentFragment
     */
    public static NextAppointmentFragment newInstance(String appointmentId) {
        NextAppointmentFragment fragment = new NextAppointmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(CarePayConstants.APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_appointment, container, false);
    }

    @Override
    protected void showChooseProviderFragment() {
        callback.showFragment(ProviderListFragment.newInstance(selectedPractice, selectedVisitType, selectedLocation));
    }

    @Override
    protected void showVisitTypeFragment() {
        callback.showFragment(VisitTypeListFragment.newInstance(selectedPractice, selectedLocation, selectedResource));
    }

    @Override
    protected void showAvailabilityFragment() {
        AppointmentAvailabilityPayloadDTO payload = new AppointmentAvailabilityPayloadDTO();
        payload.setLocation(selectedLocation);
        payload.setResource(selectedResource);
        ProvidersReasonDTO reasonDTO = new ProvidersReasonDTO();
        reasonDTO.setAmount(selectedVisitType.getAmount());
        reasonDTO.setName(selectedVisitType.getName());
        reasonDTO.setDescription(selectedVisitType.getDescription());
        reasonDTO.setId(selectedVisitType.getId());
        payload.setVisitReason(reasonDTO);
        AppointmentAvailabilityDataDTO appointmentAvailabilityDataDTO = new AppointmentAvailabilityDataDTO();
        ArrayList<AppointmentAvailabilityPayloadDTO> payloadList = new ArrayList<>();
        payloadList.add(payload);
        appointmentAvailabilityDataDTO.setPayload(payloadList);
        AppointmentAvailabilityMetadataDTO metadataDTO = new AppointmentAvailabilityMetadataDTO();
        metadataDTO.setPracticeId(selectedPractice.getPracticeId());
        metadataDTO.setPracticeMgmt(selectedPractice.getPracticeMgmt());
        appointmentAvailabilityDataDTO.setMetadata(metadataDTO);
        appointmentsResultModel.getPayload().setAppointmentAvailability(appointmentAvailabilityDataDTO);
        callback.showFragment(AvailabilityHourFragment.newInstance(AvailabilityHourFragment.SELECT_MODE));
    }

    @Override
    protected void showChooseLocationFragment() {
        callback.showFragment(LocationListFragment.newInstance(selectedPractice, selectedVisitType, selectedResource));
    }
}
