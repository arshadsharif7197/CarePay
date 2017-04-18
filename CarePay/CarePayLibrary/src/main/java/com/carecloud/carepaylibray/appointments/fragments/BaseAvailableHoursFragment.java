package com.carecloud.carepaylibray.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepaylibray.appointments.adapters.AvailableLocationsAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.ParseException;
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

public abstract class BaseAvailableHoursFragment extends BaseDialogFragment implements AvailableHoursAdapter.SelectAppointmentTimeSlotCallback, AvailableLocationsAdapter.SelectLocationCallback {

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

    private List<LocationDTO> selectedLocations = new LinkedList<>();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    private AppointmentNavigationCallback callback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (AppointmentNavigationCallback) context;
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
            startDate = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime()).getDate();
            endDate = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getEndTime()).getDate();
            selectedVisitTypeDTO = appointmentDTO.getPayload().getVisitType();
            selectedResource = new AppointmentResourcesItemDTO();
            selectedResource.setId(appointmentDTO.getPayload().getResourceId());
            selectedResource.setProvider(appointmentDTO.getPayload().getProvider());
            resourcesToScheduleDTO = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, bundle);
        } else {
            startDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            endDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);
            selectedVisitTypeDTO = DtoHelper.getConvertedDTO(VisitTypeDTO.class, bundle);
            selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, bundle);
            resourcesToScheduleDTO = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, bundle);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_hours_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbar(view);
        inflateUIComponents(view);
        setupEditDateButton(view);
        updateDateRange();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        hideDefaultActionBar();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);

        titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_available_hours_title);
        toolbar.setTitle("");
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
    }


    private void setAdapters() {
        List<LocationDTO> locations = extractAvailableLocations(availabilityDTO);

        if (availableHoursRecycleView.getAdapter() == null) {
            availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(),
                    getAvailableHoursListWithHeader(), this, locations.size() > 1));
        } else {
            AvailableHoursAdapter availableHoursAdapter = (AvailableHoursAdapter) availableHoursRecycleView.getAdapter();
            availableHoursAdapter.setItems(getAvailableHoursListWithHeader());
            availableHoursAdapter.setMultiLocationStyle(locations.size() > 1);
            availableHoursAdapter.notifyDataSetChanged();
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
    }

    private void updateDateRange() {
        if (startDate == null || endDate == null) {
            startDate = Calendar.getInstance().getTime();//today
            endDate = startDate;
        }

        if(titleView != null) {
            String today = Label.getLabel("today_label");
            String tomorrow = Label.getLabel("add_appointment_tomorrow");
            String thisMonth = Label.getLabel("this_month_label");
            String nextDay = Label.getLabel("next_days_label");

            String formattedDate = DateUtil.getFormattedDate(startDate, endDate, today, tomorrow, thisMonth, nextDay);
            titleView.setText(formattedDate);
        }
    }


    protected void selectDateRange(){
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


    private ArrayList<Object> getAvailableHoursListWithHeader() {
        ArrayList<Object> timeSlotsListWithHeaders = new ArrayList<>();

        if (availabilityDTO != null && availabilityDTO.getPayload().getAppointmentAvailability().getPayload().size() > 0) {
            List<AppointmentAvailabilityPayloadDTO> availabilityPayloadDTOs = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
            List<AppointmentsSlotsDTO> appointmentsSlotsDTOList = groupAllLocationSlotsByTime(availabilityPayloadDTOs);//availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getSlots();
            if (appointmentsSlotsDTOList != null && appointmentsSlotsDTOList.size() > 0) {
                // To sort appointment time slots list based on time
                Collections.sort(appointmentsSlotsDTOList, new Comparator<AppointmentsSlotsDTO>() {
                    public int compare(AppointmentsSlotsDTO obj1, AppointmentsSlotsDTO obj2) {
                        String dateO1 = obj1.getStartTime();
                        String dateO2 = obj2.getStartTime();

                        Date date1 = DateUtil.getInstance().setDateRaw(dateO1).getDate();
                        Date date2 = DateUtil.getInstance().setDateRaw(dateO2).getDate();

                        long time1 = 0;
                        long time2 = 0;
                        if (date1 != null) {
                            time1 = date1.getTime();
                        }

                        if (date2 != null) {
                            time2 = date2.getTime();
                        }

                        if (time1 < time2) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });

                // To sort appointment time slots list based on today or tomorrow
                Collections.sort(appointmentsSlotsDTOList, new Comparator<AppointmentsSlotsDTO>() {
                    public int compare(AppointmentsSlotsDTO obj1, AppointmentsSlotsDTO obj2) {
                        String date01 = obj1.getStartTime();
                        String date02 = obj2.getStartTime();

                        Date date2 = DateUtil.getInstance().setDateRaw(date02).getDate();
                        DateUtil.getInstance().setDateRaw(date01);
                        return DateUtil.getInstance().compareTo(date2);
                    }
                });

                // To create appointment time slots list data structure along with headers
                String headerTitle = "";
                for (AppointmentsSlotsDTO timSlotsDTO : appointmentsSlotsDTOList) {

                    String title = getSectionHeaderTitle(timSlotsDTO.getStartTime());
                    if (headerTitle.equalsIgnoreCase(title)) {
                        timeSlotsListWithHeaders.add(timSlotsDTO);
                    } else {
                        headerTitle = title;
                        timeSlotsListWithHeaders.add(headerTitle);
                        timeSlotsListWithHeaders.add(timSlotsDTO);
                    }
                }

            }
        }
        return timeSlotsListWithHeaders;
    }

    private String getSectionHeaderTitle(String timeSlotRawDate) {
        // Current date
        String currentDate = DateUtil.getInstance().setToCurrent().toStringWithFormatMmDashDdDashYyyy();
        final Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // day after tomorrow date
        String dayAfterTomorrowDate = DateUtil.getInstance().setToDayAfterTomorrow().toStringWithFormatMmDashDdDashYyyy();
        final Date dayAfterTomorrowConvertedDate = DateUtil.getInstance().setDateRaw(dayAfterTomorrowDate).getDate();

        // Appointment time slot date
        String appointmentDate = DateUtil.getInstance().setDateRaw(timeSlotRawDate).toStringWithFormatMmDashDdDashYyyy();
        final Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String headerText;
        if (convertedAppointmentDate.after(dayAfterTomorrowConvertedDate) ||
                appointmentDate.equalsIgnoreCase(dayAfterTomorrowDate)) {
            headerText = DateUtil.getInstance().getDateAsDayMonthDayOrdinal();
        } else if (convertedAppointmentDate.after(currentConvertedDate) && convertedAppointmentDate.before(dayAfterTomorrowConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            headerText = Label.getLabel("add_appointment_tomorrow");
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = Label.getLabel("today_appointments_heading");
        } else {
            headerText = Label.getLabel("today_appointments_heading");
        }
        return headerText;
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
        callback.confirmAppointment(appointmentsSlotsDTO, availabilityDTO);
    }

    @Override
    public void onSelectLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {//selected ALL locations
            resetLocationSelections(true);
        } else {
            if (selectedLocations.isEmpty()) {//Initial state, reset location selections to remove the All selected status
                resetLocationSelections(false);
            }
            if (!selectedLocations.contains(locationDTO)) {
                selectedLocations.add(locationDTO);
            } else if (selectedLocations.contains(locationDTO)) {
                selectedLocations.remove(locationDTO);
            }
            updateSelectedLocationsForAdapter();
        }
        setAdapters();
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
            if (isLocationSelected(location)) {
                for (AppointmentsSlotsDTO slotsDTO : availabilityPayloadDTO.getSlots()) {
                    slotsDTO.setLocation(location);
                    appointmentsSlots.add(slotsDTO);
                }
            }
        }

        //need to sort the slots by time just in case there are multiple locations and times are out of order
        Collections.sort(appointmentsSlots, new Comparator<AppointmentsSlotsDTO>() {
            @Override
            public int compare(AppointmentsSlotsDTO o1, AppointmentsSlotsDTO o2) {
                try {
                    Date d1 = dateFormater.parse(o1.getStartTime());
                    Date d2 = dateFormater.parse(o2.getStartTime());

                    return d1.compareTo(d2);
                } catch (ParseException pxe) {
                    pxe.printStackTrace();
                }

                return 0;
            }
        });

        return appointmentsSlots;
    }

    private boolean isLocationSelected(LocationDTO locationDTO) {
        return selectedLocations.isEmpty() || selectedLocations.contains(locationDTO);
    }

    protected abstract void setupEditDateButton(View view);
}