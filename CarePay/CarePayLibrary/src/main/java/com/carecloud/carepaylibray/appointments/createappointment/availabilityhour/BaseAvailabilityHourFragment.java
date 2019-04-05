package com.carecloud.carepaylibray.appointments.createappointment.availabilityhour;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author pjohnson on 1/16/19.
 */
public abstract class BaseAvailabilityHourFragment extends BaseDialogFragment implements DateCalendarRangeInterface {

    public static final int SELECT_MODE = 100;
    public static final int SCHEDULE_MODE = 101;

    protected ScheduleAppointmentInterface callback;
    private AppointmentsResultModel appointmentModelDto;
    private VisitTypeDTO selectedVisitReason;
    private AppointmentResourcesItemDTO selectedResource;
    private LocationDTO selectedLocation;
    private String today = Label.getLabel("today_label");
    private String tomorrow = Label.getLabel("add_appointment_tomorrow");
    protected Date endDate;
    protected Date startDate;
    protected TextView toolbarTitle;
    protected int mode;
    private boolean alreadyCalled;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
        } else if (context instanceof ScheduleAppointmentInterface) {
            callback = (ScheduleAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement AppointmentViewHandler.");
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
        selectedVisitReason = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getVisitReason();
        selectedLocation = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getLocation();
        selectedResource = appointmentModelDto.getPayload().getAppointmentAvailability()
                .getPayload().get(0).getResource();
        mode = getArguments().getInt("mode");
        initDates();
    }

    private void initDates() {
        startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 5);
        endDate = cal.getTime();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpPrepaymentMessage(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!alreadyCalled) {
            callAvailabilityService();
            alreadyCalled = true;
        }
    }

    private void setUpTimeSlotsList(View view) {
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
                            if (mode == SCHEDULE_MODE) {
                                showAppointmentConfirmationFragment(slot);
                            } else {
                                dismiss();
                                callback.setAppointmentSlot(slot);
                            }
                        }
                    }));
        } else {
            availableHoursRecyclerView.setVisibility(View.GONE);
            noAppointmentLayout.setVisibility(View.VISIBLE);
            TextView titleTextView = noAppointmentLayout.findViewById(R.id.no_apt_message_title);
            titleTextView.setText(Label.getLabel("no_appointment_slots_title"));
            TextView subTitleTextView = noAppointmentLayout.findViewById(R.id.no_apt_message_desc);
            subTitleTextView.setText(Label.getLabel("no_appointment_slots_message"));
            Button changeDatesButton = noAppointmentLayout.findViewById(R.id.newAppointmentClassicButton);
            changeDatesButton.setVisibility(getChangeDatesToolbarButtonVisibility() ? View.VISIBLE : View.GONE);
            changeDatesButton.setText(Label.getLabel("change_dates"));
            changeDatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDateRange();
                }
            });
        }
    }

    protected boolean getChangeDatesToolbarButtonVisibility() {
        return false;
    }

    private void showAppointmentConfirmationFragment(AppointmentsSlotsDTO slot) {
        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(slot.getStartTime());
        payloadDTO.setEndTime(slot.getEndTime());
        payloadDTO.setLocation(selectedLocation);
        payloadDTO.setVisitReasonId(selectedVisitReason.getId());

        payloadDTO.setVisitType(selectedVisitReason);

        payloadDTO.setProvider(selectedResource.getProvider());
        payloadDTO.setProviderId(String.valueOf(selectedResource.getProvider().getId()));
        payloadDTO.setResource(selectedResource);
        payloadDTO.setResourceId(selectedResource.getId());

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);
        callback.showAppointmentConfirmationFragment(appointmentDTO);
    }

    protected abstract void selectDateRange();

    private void setUpPrepaymentMessage(View view) {
        TextView prepaymentMessage = view.findViewById(R.id.prepaymentMessage);
        if (prepaymentMessage != null) {
            double visitTypeAmount = getVisitTypeAmount(selectedVisitReason.getId());
            if (visitTypeAmount > 0) {
                selectedVisitReason.setAmount(visitTypeAmount);
                String message = Label.getLabel("appointments_prepayment_message")
                        + NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeAmount);
                prepaymentMessage.setText(message);
                prepaymentMessage.setVisibility(View.VISIBLE);
            } else {
                prepaymentMessage.setVisibility(View.GONE);
            }
        }
    }

    private double getVisitTypeAmount(String visitTypeId) {
        List<AppointmentsSettingDTO> appointmentsSettingsList = appointmentModelDto.getPayload()
                .getAppointmentsSettings();
        for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
            if (appointmentsSettingDTO.getPracticeId().equals(appointmentModelDto.getPayload()
                    .getAppointmentAvailability().getMetadata().getPracticeId())) {
                for (AppointmentsPrePaymentDTO prePaymentDTO : appointmentsSettingDTO.getPrePayments()) {
                    if (prePaymentDTO.getVisitType().equals(visitTypeId)) {
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

    @Override
    public void setDateRange(Date newStartDate, Date newEndDate) {
        startDate = newStartDate;
        endDate = newEndDate;
        toolbarTitle.setText(getToolbarTitle(startDate, endDate));
        callAvailabilityService();
    }

    private String getToolbarTitle(Date startDate, Date endDate) {
        String today = Label.getLabel("today_label");
        String tomorrow = Label.getLabel("add_appointment_tomorrow");
        String thisMonth = Label.getLabel("this_month_label");
        String nextDay = Label.getLabel("next_days_label");

        return DateUtil.getFormattedDate(startDate, endDate, today, tomorrow,
                thisMonth, nextDay, false);
    }

    private void callAvailabilityService() {
        final AppointmentAvailabilityDataDTO availabilityDataDTO = appointmentModelDto.getPayload()
                .getAppointmentAvailability();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", availabilityDataDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", availabilityDataDTO.getMetadata().getPracticeId());
        queryMap.put("visit_reason_id", String.valueOf(availabilityDataDTO.getPayload().get(0).getVisitReason().getId()));
        queryMap.put("resource_ids", String.valueOf(selectedResource.getId()));
        queryMap.put("location_ids", String.valueOf(selectedLocation.getId()));
        if (startDate != null) {
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        if (endDate != null) {
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        TransitionDTO transitionDTO = appointmentModelDto.getMetadata().getLinks().getAppointmentAvailability();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                AppointmentsResultModel availabilityDto = DtoHelper
                        .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                if (availabilityDto.getPayload().getAppointmentAvailability().getPayload().isEmpty()) {
                    AppointmentAvailabilityPayloadDTO payload = new AppointmentAvailabilityPayloadDTO();
                    payload.setLocation(selectedLocation);
                    payload.setResource(selectedResource);
                    payload.setVisitReason(selectedVisitReason);
                    availabilityDto.getPayload().getAppointmentAvailability().getPayload().add(payload);
                }

                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0)
                        .getResource().getProvider().setPhoto(selectedResource.getProvider().getPhoto());
                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0)
                        .getVisitReason().setAmount(availabilityDataDTO.getPayload().get(0)
                        .getVisitReason().getAmount());
                appointmentModelDto.getPayload().setAppointmentAvailability(availabilityDto.getPayload()
                        .getAppointmentAvailability());
                setUpTimeSlotsList(getView());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, queryMap);
    }
}
