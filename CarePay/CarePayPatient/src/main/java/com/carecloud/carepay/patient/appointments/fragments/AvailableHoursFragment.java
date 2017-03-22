package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepay.patient.appointments.adapters.AvailableLocationsAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAddressDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
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

public class AvailableHoursFragment extends BaseFragment implements AvailableHoursAdapter.SelectAppointmentTimeSlotCallback, AvailableLocationsAdapter.SelectLocationCallback{

    private Date startDate;
    private Date endDate;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;
    private AppointmentResourcesItemDTO selectedResource;

    private RecyclerView availableHoursRecycleView;
    private RecyclerView availableLocationsRecycleView;
    private TextView titleView;
    private View singleLocation;
    private TextView singleLocationText;

    private String addAppointmentPatientId;
    private List<LocationDTO> selectedLocations = new LinkedList<>();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    private AppointmentNavigationCallback callback;
    private static final String TAG = "AvailableHoursFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            callback = (AppointmentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new Gson();
        String appointmentInfoString;
        Bundle bundle = getArguments();
        if (bundle != null) {
            startDate = (Date)bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            endDate = (Date)bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE);
            selectedVisitTypeDTO = gson.fromJson(appointmentInfoString, VisitTypeDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
            if(appointmentInfoString!=null){
                selectedResource = gson.fromJson(appointmentInfoString, AppointmentResourcesItemDTO.class);
            }

//            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
//            if(appointmentInfoString != null) {
//                selectedResourcesDTO = gson.fromJson(appointmentInfoString, AppointmentResourcesDTO.class);
//                selectedResource = selectedResourcesDTO.getResource();
//            }

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE);
            resourcesToScheduleDTO = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);

            addAppointmentPatientId = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID);
        }

    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View availableHoursListView = inflater.inflate(R.layout.fragment_available_hours_list,
                container, false);

        hideDefaultActionBar();

        /*inflate toolbar*/
        inflateToolbar(availableHoursListView);
        /*inflate other UI components like button etc.*/
        inflateUIComponents(availableHoursListView);
        /*update date range*/
        updateDateRange();

        return availableHoursListView;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
//        getAvailableHoursTimeSlots();
    }

    @Override
    public void onResume(){
        super.onResume();
        getAvailableHoursTimeSlots();
    }

    /**
     * Method to inflate toolbar to UI
     *
     * @param view used as view component
     */
    private void inflateToolbar(View view) {
        Toolbar toolbar = (Toolbar)
                view.findViewById(R.id.add_appointment_toolbar);
        titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_available_hours_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), titleView);
        toolbar.setTitle("");

        String range = null;
        try {
            range = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentSelectRangeButton();
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        TextView titleOther = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        titleOther.setText(range!=null?range:getString(R.string.edit_range_button_label));
        titleOther.setVisibility(View.VISIBLE);
        titleOther.setOnClickListener(dateRangeClickListener);

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(navigationOnClickListener);

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

        try {
            String location = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentLocationsLabel();
            TextView locationsLabel = (TextView) view.findViewById(R.id.location_text);
            locationsLabel.setText(location != null ? location : getString(R.string.locations_label));
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private void setAdapters(){
        try {
            List<LocationDTO> locations = extractAvailableLocations(availabilityDTO);

            if (availableHoursRecycleView.getAdapter() == null) {
                availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(),
                        getAvailableHoursListWithHeader(), AvailableHoursFragment.this, locations.size() > 1));
            } else {
                AvailableHoursAdapter availableHoursAdapter = (AvailableHoursAdapter) availableHoursRecycleView.getAdapter();
                availableHoursAdapter.setItems(getAvailableHoursListWithHeader());
                availableHoursAdapter.setMultiLocationStyle(locations.size() > 1);
                availableHoursAdapter.notifyDataSetChanged();
            }

            if(locations.isEmpty()){
                availableLocationsRecycleView.setVisibility(View.GONE);
                singleLocation.setVisibility(View.GONE);
            } else if (locations.size() > 1) {
                availableLocationsRecycleView.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.GONE);
                if (availableLocationsRecycleView.getAdapter() == null) {
                    String all = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentAllLocationsItem();
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
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Method to update date range that is selected on calendar
     */
    private void updateDateRange(){
        if(startDate == null || endDate == null){
            startDate = Calendar.getInstance().getTime();//today
            endDate = startDate;
        }
        String today = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentsTodayHeadingSmall();
        String tomorrow = resourcesToScheduleDTO.getMetadata().getLabel().getAddAppointmentTomorrow();
        String thisMonth = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentThisMonthTitle();
        String nextDay = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentNextDaysTitle();

        String formattedDate = DateUtil.getFormattedDate(startDate, endDate, today, tomorrow, thisMonth, nextDay);
        titleView.setText(formattedDate);
    }


    /**
     *Click listener for toolbar navigation
     */
    View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().onBackPressed();
        }
    };



    /**
     *Click listener for edit range and edit date range button
     */
    private View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.selectDateRange(startDate, endDate, selectedVisitTypeDTO, selectedResource, resourcesToScheduleDTO);
        }
    };

    private void resetLocatonSelections(boolean clearAll){
        RecyclerView.LayoutManager layoutManager = availableLocationsRecycleView.getLayoutManager();
        for(int i=0; i<layoutManager.getChildCount(); i++) {
            layoutManager.getChildAt(i).findViewById(R.id.available_location).setSelected(false);
        }
        ((AvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).resetLocationsSelected(clearAll);
        selectedLocations.clear();
    }

    private void updateSelectedLocationsForAdapter(){
        if(selectedLocations.isEmpty()){//if user removed last location reset everything
            resetLocatonSelections(true);
        }else{
            ((AvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).updateSelectedLocations(selectedLocations);
        }
    }


    private ArrayList<Object> getAvailableHoursListWithHeader(){
        ArrayList<Object> timeSlotsListWithHeaders = new ArrayList<>();

        if(availabilityDTO!=null && availabilityDTO.getPayload().getAppointmentAvailability().getPayload().size()>0) {
            List<AppointmentAvailabilityPayloadDTO> availabilityPayloadDTOs = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
            List<AppointmentsSlotsDTO> appointmentsSlotsDTOList = groupAllLocatonSlotsByTime(availabilityPayloadDTOs);//availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getSlots();
            if(appointmentsSlotsDTOList!=null && appointmentsSlotsDTOList.size()>0){
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
                appointmentDate.equalsIgnoreCase(dayAfterTomorrowDate)){
            headerText = DateUtil.getInstance().getDateAsDayMonthDayOrdinal();
        } else if (convertedAppointmentDate.after(currentConvertedDate) && convertedAppointmentDate.before(dayAfterTomorrowConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            headerText = availabilityDTO.getMetadata().getLabel().getAddAppointmentTomorrow();
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
        } else {
            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
        }
        return headerText;
    }

    private void getAvailableHoursTimeSlots(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt",resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());
        queryMap.put("visit_reason_id", selectedVisitTypeDTO.getId()+"");
        queryMap.put("resource_ids", selectedResource.getId()+"");
        if(startDate!=null){
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        if(endDate!=null){
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
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
        }
    };

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {

        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(appointmentsSlotsDTO.getStartTime());
        payloadDTO.setEndTime(appointmentsSlotsDTO.getEndTime());
        payloadDTO.setProviderId(selectedResource.getProvider().getId().toString());
        payloadDTO.setVisitReasonId(selectedVisitTypeDTO.getId());
        payloadDTO.setResourceId(selectedResource.getId());
        payloadDTO.setChiefComplaint(selectedVisitTypeDTO.getName());

        PatientModel patientDTO = new PatientModel();
        patientDTO.setPatientId(addAppointmentPatientId);
        payloadDTO.setPatient(patientDTO);

        ProviderDTO providersDTO;
        providersDTO = selectedResource.getProvider();

        LocationDTO locationDTO = availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation();
        if(locationDTO == null){
            locationDTO = new LocationDTO();
            AppointmentAddressDTO addressDTO = new AppointmentAddressDTO();
            locationDTO.setName(resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentsPlaceNameHeading());
            locationDTO.setAddress(addressDTO);
        }

        payloadDTO.setLocation(locationDTO);
        payloadDTO.setProvider(providersDTO);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);

        final RequestAppointmentDialog requestAppointmentDialog =  new RequestAppointmentDialog(getActivity(),appointmentDTO,resourcesToScheduleDTO);
        requestAppointmentDialog.show();
    }

    @Override
    public void onSelectLocation(LocationDTO locationDTO) {
        if(locationDTO == null) {//selected ALL locations
            resetLocatonSelections(true);
        }else{
            if(selectedLocations.isEmpty()) {//Initial state, reset location selections to remove the All selected status
                resetLocatonSelections(false);
            }
            if(!selectedLocations.contains(locationDTO)){
                selectedLocations.add(locationDTO);
            }else if(selectedLocations.contains(locationDTO)){
                selectedLocations.remove(locationDTO);
            }

            updateSelectedLocationsForAdapter();

        }

        setAdapters();
    }

    private List<LocationDTO> extractAvailableLocations(AppointmentAvailabilityDTO availabilityDTO){
        List<LocationDTO> locationsDTOs = new LinkedList<>();
        List<AppointmentAvailabilityPayloadDTO> availableAppointments = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        for(AppointmentAvailabilityPayloadDTO availableAppointment : availableAppointments){
            locationsDTOs.add(availableAppointment.getLocation());
        }
//        locationsDTOs.addAll(getExtraLocationsStub(3));
        return locationsDTOs;
    }

    private List<AppointmentsSlotsDTO> groupAllLocatonSlotsByTime(List<AppointmentAvailabilityPayloadDTO> appointmentAvailabilityPayloadDTOs){
        List<AppointmentsSlotsDTO> appointmentsSlots = new LinkedList<>();
        String locationName = null;
        for(AppointmentAvailabilityPayloadDTO availabilityPayloadDTO : appointmentAvailabilityPayloadDTOs){
            if(isLocationSelected(availabilityPayloadDTO.getLocation())) {
                locationName = availabilityPayloadDTO.getLocation().getName();
                for (AppointmentsSlotsDTO slotsDTO : availabilityPayloadDTO.getSlots()) {
                    slotsDTO.setLocationName(locationName);
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
                }catch (ParseException pxe){
                    pxe.printStackTrace();
                }

                return 0;
            }
        });

        return appointmentsSlots;
    }

    private boolean isLocationSelected(LocationDTO locationDTO){
        return selectedLocations.isEmpty() || selectedLocations.contains(locationDTO);
    }

}