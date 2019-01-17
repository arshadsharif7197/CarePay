package com.carecloud.carepay.patient.appointments.createappointment.availablehours;

import android.content.Context;
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
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourceDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProvidersReasonDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 1/16/19.
 */
public class AvailabilityHourFragment extends BaseFragment {

    private CreateAppointmentInterface callback;
    private AppointmentsResultModel appointmentModelDto;
    private ProvidersReasonDTO selectedProviderReason;
    private AppointmentResourceDTO selectedResource;
    private LocationDTO selectedLocation;
    private String today = Label.getLabel("today_label");
    private String tomorrow = Label.getLabel("add_appointment_tomorrow");

    public static AvailabilityHourFragment newInstance() {
        Bundle args = new Bundle();
        AvailabilityHourFragment fragment = new AvailabilityHourFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateAppointmentInterface) {
            callback = (CreateAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement CreateAppointmentInterface.");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentModelDto = (AppointmentsResultModel) callback.getDto();
        selectedProviderReason = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getVisitReason();
        selectedLocation = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getLocation();
        selectedResource = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getResource();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_availability_hours_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpPrepaymentMessage(view);

        View noAppointmentLayout = view.findViewById(R.id.no_appointment_layout);
        RecyclerView availableHoursRecyclerView = view.findViewById(R.id.availableHoursRecyclerView);
        if (appointmentModelDto.getPayload().getAppointmentAvailability().getPayload().get(0).getSlots().size() > 0) {
            noAppointmentLayout.setVisibility(View.GONE);
            availableHoursRecyclerView.setVisibility(View.VISIBLE);
            availableHoursRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            availableHoursRecyclerView.setAdapter(new AvailabilityHourAdapter(getAllAvailableTimeSlots(appointmentModelDto
                    .getPayload().getAppointmentAvailability().getPayload().get(0).getSlots()),
                    new AvailabilityHourAdapter.OnTimeSlotListItemClickListener() {
                        @Override
                        public void onTimeSlotListItemClickListener(AppointmentsSlotsDTO slot) {
                            showAppointmentConfirmationFragment(slot);
                        }
                    }));
        } else {
            noAppointmentLayout.setVisibility(View.VISIBLE);
            noAppointmentLayout.setVisibility(View.GONE);
        }
    }

    private void showAppointmentConfirmationFragment(AppointmentsSlotsDTO slot) {
        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(slot.getStartTime());
        payloadDTO.setEndTime(slot.getEndTime());
        payloadDTO.setLocation(selectedLocation);
        payloadDTO.setVisitReasonId(selectedProviderReason.getId());

        VisitTypeDTO visitTypeDTO = new VisitTypeDTO();
        visitTypeDTO.setId(selectedProviderReason.getId());
        visitTypeDTO.setName(selectedProviderReason.getName());
        visitTypeDTO.setAmount(selectedProviderReason.getAmount());
        payloadDTO.setVisitType(visitTypeDTO);

        payloadDTO.setProvider(selectedResource.getProvider());
        payloadDTO.setProviderId(String.valueOf(selectedResource.getProvider().getId()));
        payloadDTO.setResource(selectedResource);
        payloadDTO.setResourceId(selectedResource.getId());

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);
        callback.showAppointmentConfirmationFragment(appointmentDTO);
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
        TextView title = toolbar.findViewById(R.id.add_appointment_toolbar_title);
        title.setText(Label.getLabel("createAppointment.visitTypeList.title.label.visitType"));
        callback.displayToolbar(false, null);
    }

    private void setUpPrepaymentMessage(View view) {
        TextView prepaymentMessage = view.findViewById(R.id.prepaymentMessage);
        double visitTypeAmount = getVisitTypeAmount(selectedProviderReason.getId());
        if (visitTypeAmount > 0) {
            String message = Label.getLabel("appointments_prepayment_message")
                    + NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeAmount);
            prepaymentMessage.setText(message);
            prepaymentMessage.setVisibility(View.VISIBLE);
        } else {
            prepaymentMessage.setVisibility(View.GONE);
        }
    }

    private double getVisitTypeAmount(int visitTypeId) {
        List<AppointmentsSettingDTO> appointmentsSettingsList = appointmentModelDto.getPayload()
                .getAppointmentsSettings();
        for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
            if (appointmentsSettingDTO.getPracticeId().equals(appointmentModelDto.getPayload()
                    .getAppointmentAvailability().getMetadata().getPracticeId())) {
                for (AppointmentsPrePaymentDTO prePaymentDTO : appointmentsSettingDTO.getPrePayments()) {
                    if (prePaymentDTO.getVisitType() == visitTypeId) {
                        return prePaymentDTO.getAmount();
                    }
                }
            }
        }
        return 0;
    }

    private List<AppointmentsSlotsDTO> getAllAvailableTimeSlots(List<AppointmentsSlotsDTO> slots) {
        Collections.sort(slots, new Comparator<AppointmentsSlotsDTO>() {
            @Override
            public int compare(AppointmentsSlotsDTO lhs, AppointmentsSlotsDTO rhs) {
                if (lhs != null && rhs != null) {
                    Date d1 = DateUtil.getInstance().setDateRaw(lhs.getStartTime()).getDate();
                    Date d2 = DateUtil.getInstance().setDateRaw(rhs.getStartTime()).getDate();

                    return d1.compareTo(d2);
                }
                return -1;
            }
        });
        return insertDayHeaders(slots);
    }

    private List<AppointmentsSlotsDTO> insertDayHeaders(List<AppointmentsSlotsDTO> slots) {
        List<AppointmentsSlotsDTO> slotsWithHeaders = new ArrayList<>();
        Date lastDate = new Date(0);
        AppointmentsSlotsDTO headerTemplate;
        for (AppointmentsSlotsDTO slot : slots) {
            Date slotDate = DateUtil.getInstance().setDateRaw(slot.getStartTime()).getDate();
            if (slotDate != null && !DateUtil.isSameDay(lastDate, slotDate)) {
                headerTemplate = new AppointmentsSlotsDTO();
                headerTemplate.setHeader(true);
                headerTemplate.setStartTime(DateUtil.getFormattedDate(slotDate, today, tomorrow));
                slotsWithHeaders.add(headerTemplate);
                lastDate = slotDate;
            }
            slotsWithHeaders.add(slot);
        }
        return slotsWithHeaders;
    }
}
