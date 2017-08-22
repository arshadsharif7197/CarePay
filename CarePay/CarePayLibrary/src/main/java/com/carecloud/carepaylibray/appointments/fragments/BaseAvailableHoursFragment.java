package com.carecloud.carepaylibray.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.adapters.AvailableLocationsAdapter;
import com.carecloud.carepaylibray.appointments.adapters.FilterableAvailableHoursAdapter;
import com.carecloud.carepaylibray.appointments.interfaces.AvailableHoursInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class BaseAvailableHoursFragment extends BaseAppointmentDialogFragment implements FilterableAvailableHoursAdapter.SelectAppointmentTimeSlotCallback, AvailableLocationsAdapter.SelectLocationCallback {

    private Date startDate;
    private Date endDate;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;
    private AppointmentResourcesItemDTO selectedResource;

    private RecyclerView availableHoursRecycleView;
    private RecyclerView availableLocationsRecycleView;
    private View singleLocation;
    private TextView singleLocationText;
    private View progressView;
    protected TextView titleView;
    private View noAppointmentLayout;
    private View locationsLayout;

    private Map<String, LocationDTO> selectedLocations = new HashMap<>();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    private AvailableHoursInterface callback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (AvailableHoursInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        AppointmentDTO appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, bundle);
        if (appointmentDTO != null) {
            selectedVisitTypeDTO = appointmentDTO.getPayload().getVisitType();
            selectedResource = new AppointmentResourcesItemDTO();
            selectedResource.setId(appointmentDTO.getPayload().getResourceId());
            selectedResource.setProvider(appointmentDTO.getPayload().getProvider());
            resourcesToScheduleDTO = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, bundle);
        } else {
            selectedVisitTypeDTO = DtoHelper.getConvertedDTO(VisitTypeDTO.class, bundle);
            selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, bundle);
            resourcesToScheduleDTO = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, bundle);
        }
        startDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
        endDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_hours_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbar(view);
        inflateUIComponents(view);
        setupEditDateButton(view);
        updateDateRange();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getAvailableHoursTimeSlots();
                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void inflateToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);
        titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_available_hours_title);
        toolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }


    /**
     * Method to inflate UI components
     *
     * @param view used as view component
     */
    private void inflateUIComponents(View view) {
        singleLocation = view.findViewById(R.id.appointment_available_single_location);
        singleLocationText = (TextView) view.findViewById(R.id.available_single_location_text);

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(getContext());
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        availableHoursRecycleView = (RecyclerView) view.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);

        LinearLayoutManager availableLocationsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        availableLocationsRecycleView = (RecyclerView) view.findViewById(R.id.available_locations_recycler);
        availableLocationsRecycleView.setLayoutManager(availableLocationsLayoutManager);

        TextView locationsLabel = (TextView) view.findViewById(R.id.location_text);
        locationsLabel.setText(Label.getLabel("appointment_locations_label"));

        progressView = view.findViewById(R.id.progressview);
        progressView.setVisibility(View.VISIBLE);

        noAppointmentLayout = view.findViewById(R.id.no_appointment_layout);
        locationsLayout = view.findViewById(R.id.location_layout);

        TextView noApptTitle = (TextView) view.findViewById(R.id.no_apt_message_title);
        noApptTitle.setText(Label.getLabel("no_appointment_slots_title"));
        TextView noApptMessage = (TextView) view.findViewById(R.id.no_apt_message_desc);
        noApptMessage.setText(Label.getLabel("no_appointment_slots_message"));

        TextView prepaymentMessage = (TextView) view.findViewById(R.id.prepaymentMessage);
        if(selectedVisitTypeDTO.getAmount() > 0) {
            String message = Label.getLabel("appointments_prepayment_message") + NumberFormat.getCurrencyInstance().format(selectedVisitTypeDTO.getAmount());
            prepaymentMessage.setText(message);
            prepaymentMessage.setVisibility(View.VISIBLE);
        }else{
            prepaymentMessage.setVisibility(View.GONE);
        }
    }


    private void setAdapters() {
        List<LocationDTO> locations = extractAvailableLocations(availabilityDTO);
        FilterableAvailableHoursAdapter.LocationMode locationMode = locations.size() > 1 ? FilterableAvailableHoursAdapter.LocationMode.MULTI : FilterableAvailableHoursAdapter.LocationMode.SINGLE;

        FilterableAvailableHoursAdapter hoursAdapter;
        if (availableHoursRecycleView.getAdapter() == null) {
            hoursAdapter = new FilterableAvailableHoursAdapter(getContext(), getAllAvailableTimeSlots(), selectedLocations, this, locationMode);
            availableHoursRecycleView.setAdapter(hoursAdapter);

        } else {
            hoursAdapter = (FilterableAvailableHoursAdapter) availableHoursRecycleView.getAdapter();
            hoursAdapter.setAllTimeSlots(getAllAvailableTimeSlots());
            hoursAdapter.setMode(locationMode);
        }

        if (locations.isEmpty()) {
            availableLocationsRecycleView.setVisibility(View.GONE);
            singleLocation.setVisibility(View.GONE);
        } else if (locations.size() > 1) {
            availableLocationsRecycleView.setVisibility(View.VISIBLE);
            singleLocation.setVisibility(View.GONE);
            if (availableLocationsRecycleView.getAdapter() == null) {
                String all = Label.getLabel("appointment_all_locations_item");
                availableLocationsRecycleView.setAdapter(new AvailableLocationsAdapter(getContext(), locations, this, all));
            } else {
                AvailableLocationsAdapter availableLocationsAdapter = (AvailableLocationsAdapter) availableLocationsRecycleView.getAdapter();
                availableLocationsAdapter.setItems(locations);
                availableLocationsAdapter.notifyDataSetChanged();
            }
        } else {
            availableLocationsRecycleView.setVisibility(View.GONE);
            singleLocation.setVisibility(View.VISIBLE);
            singleLocationText.setText(locations.get(0).getName());
        }

        if (hoursAdapter.getItemCount() < 1) {
            noAppointmentLayout.setVisibility(View.VISIBLE);
            locationsLayout.setVisibility(View.GONE);
            availableHoursRecycleView.setVisibility(View.GONE);
        } else {
            noAppointmentLayout.setVisibility(View.GONE);
            locationsLayout.setVisibility(View.VISIBLE);
            availableHoursRecycleView.setVisibility(View.VISIBLE);
        }

    }

    private void updateDateRange() {
        if (startDate == null || endDate == null) {
            startDate = Calendar.getInstance().getTime();//today
            endDate = startDate;
        }

        if (titleView != null) {
            String today = Label.getLabel("today_label");
            String tomorrow = Label.getLabel("add_appointment_tomorrow");
            String thisMonth = Label.getLabel("this_month_label");
            String nextDay = Label.getLabel("next_days_label");

            String formattedDate = DateUtil.getFormattedDate(startDate, endDate, today, tomorrow, thisMonth, nextDay);
            titleView.setText(formattedDate);
        }
    }


    protected void selectDateRange() {
        callback.selectDateRange(startDate, endDate, selectedVisitTypeDTO, selectedResource, resourcesToScheduleDTO);
    }

    private void resetLocationSelections(boolean clearAll) {
        RecyclerView.LayoutManager layoutManager = availableLocationsRecycleView.getLayoutManager();
        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            layoutManager.getChildAt(i).findViewById(R.id.available_location).setSelected(false);
        }
        ((AvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).resetLocationsSelected(clearAll);
        selectedLocations.clear();
    }

    private void updateSelectedLocationsForAdapter() {
        if (selectedLocations.isEmpty()) {//if user removed last location reset everything
            resetLocationSelections(true);
        } else {
            ((AvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).updateSelectedLocations(selectedLocations);
        }
    }


    private List<AppointmentsSlotsDTO> getAllAvailableTimeSlots() {
        List<AppointmentsSlotsDTO> timeSlots = new ArrayList<>();
        if (availabilityDTO != null && !availabilityDTO.getPayload().getAppointmentAvailability().getPayload().isEmpty()) {
            List<AppointmentAvailabilityPayloadDTO> availabilityPayloadDTOs = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
            timeSlots = groupAllLocationSlotsByTime(availabilityPayloadDTOs);
            if (!timeSlots.isEmpty()) {
                Collections.sort(timeSlots, new Comparator<AppointmentsSlotsDTO>() {
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

            }
        }
        return timeSlots;
    }


    private void getAvailableHoursTimeSlots() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());
        queryMap.put("visit_reason_id", selectedVisitTypeDTO.getId() + "");
        queryMap.put("resource_ids", selectedResource.getId() + "");
        if (startDate != null) {
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        if (endDate != null) {
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        TransitionDTO transitionDTO = resourcesToScheduleDTO.getMetadata().getLinks().getAppointmentAvailability();

        getWorkflowServiceHelper().execute(transitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);
            setAdapters();
            updateDateRange();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getActivity(), exceptionMessage);
        }
    };

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        callback.onHoursAndLocationSelected(appointmentsSlotsDTO, availabilityDTO);
    }

    @Override
    public void onSelectLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {//selected ALL locations
            resetLocationSelections(true);
        } else {
            if (selectedLocations.isEmpty()) {//Initial state, reset location selections to remove the All selected status
                resetLocationSelections(false);
            }
            if (!selectedLocations.containsKey(locationDTO.getGuid())) {
                selectedLocations.put(locationDTO.getGuid(), locationDTO);
            } else if (selectedLocations.containsKey(locationDTO.getGuid())) {
                selectedLocations.remove(locationDTO.getGuid());
            }
            updateSelectedLocationsForAdapter();
        }

        if (availableHoursRecycleView.getAdapter() != null) {
            FilterableAvailableHoursAdapter adapter = (FilterableAvailableHoursAdapter) availableHoursRecycleView.getAdapter();
            adapter.updateFilteredSlots();
        }
    }

    private List<LocationDTO> extractAvailableLocations(AppointmentAvailabilityDTO availabilityDTO) {
        List<LocationDTO> locationsDTOs = new LinkedList<>();
        List<AppointmentAvailabilityPayloadDTO> availableAppointments = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        for (AppointmentAvailabilityPayloadDTO availableAppointment : availableAppointments) {
            locationsDTOs.add(availableAppointment.getLocation());
        }
        return locationsDTOs;
    }

    private List<AppointmentsSlotsDTO> groupAllLocationSlotsByTime(List<AppointmentAvailabilityPayloadDTO> appointmentAvailabilityPayloadDTOs) {
        List<AppointmentsSlotsDTO> appointmentsSlots = new LinkedList<>();
        for (AppointmentAvailabilityPayloadDTO availabilityPayloadDTO : appointmentAvailabilityPayloadDTOs) {
            LocationDTO location = availabilityPayloadDTO.getLocation();
            for (AppointmentsSlotsDTO slotsDTO : availabilityPayloadDTO.getSlots()) {
                slotsDTO.setLocation(location);
                appointmentsSlots.add(slotsDTO);
            }

        }
        return appointmentsSlots;
    }

    protected abstract void setupEditDateButton(View view);
}