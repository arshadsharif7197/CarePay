package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepay.patient.appointments.adapters.AvailableLocationsAdapter;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAddressDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationsDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentPatientDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentProviderDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
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

public class AvailableHoursFragment extends Fragment implements AvailableHoursAdapter.SelectAppointmentTimeSlotCallback, AvailableLocationsAdapter.SelectLocationCallback{

    private static String appointmentDate;
    private Date startDate;
    private Date endDate;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentResourcesDTO selectedResourcesDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;

    private RecyclerView availableHoursRecycleView;
    private RecyclerView availableLocationsRecycleView;
    private TextView titleView;
    private View singleLocation;
    private TextView singleLocationText;

//    private CarePayTextView dateRangeCustomTextView;

    private String addAppointmentPatientId;
    private List<AppointmentLocationsDTO> selectedLocations = new LinkedList<>();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");


    @Override
    public void onStart() {
        super.onStart();
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
            selectedResourcesDTO = gson.fromJson(appointmentInfoString, AppointmentResourcesDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE);
            resourcesToScheduleDTO = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);

            addAppointmentPatientId = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID);
        }

        getAvailableHoursTimeSlots();
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View availableHoursListView = inflater.inflate(R.layout.fragment_available_hours_list,
                container, false);

        /*inflate toolbar*/
        inflateToolbar(availableHoursListView);
        /*inflate other UI components like button etc.*/
        inflateUIComponents(availableHoursListView);
        /*update date range*/
        updateDateRange();

        return availableHoursListView;
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

        String range = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentSelectRangeButton();
        TextView titleOther = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        titleOther.setText(range!=null?range:getString(R.string.edit_range_button_label));
        titleOther.setVisibility(View.VISIBLE);
        titleOther.setOnClickListener(dateRangeClickListener);

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);

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

        availableHoursRecycleView = (RecyclerView)
                view.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);


        LinearLayoutManager availableLocationsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        availableLocationsRecycleView = (RecyclerView) view.findViewById(R.id.available_locations_recycler);
        availableLocationsRecycleView.setLayoutManager(availableLocationsLayoutManager);

        String location = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentLocationsLabel();
        TextView locationsLabel = (TextView) view.findViewById(R.id.location_text);
        locationsLabel.setText(location!=null?location:getString(R.string.locations_label));
    }

    private void setAdapters(){
        List<AppointmentLocationsDTO> locations = extractAvailableLocations(availabilityDTO);

        if(availableHoursRecycleView.getAdapter() == null){
            availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(),
                    getAvailableHoursListWithHeader(), AvailableHoursFragment.this, locations.size()>1));
        }else{
            AvailableHoursAdapter availableHoursAdapter = (AvailableHoursAdapter) availableHoursRecycleView.getAdapter();
            availableHoursAdapter.setItems(getAvailableHoursListWithHeader());
            availableHoursAdapter.setMultiLocationStyle(locations.size()>1);
            availableHoursAdapter.notifyDataSetChanged();
        }

        if(locations.size()>1) {
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
        }else{
            availableLocationsRecycleView.setVisibility(View.GONE);
            singleLocation.setVisibility(View.VISIBLE);
            singleLocationText.setText(locations.get(0).getName());
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
                /*Launch previous fragment*/
            FragmentManager fm = getFragmentManager();
            ChooseProviderFragment chooseProviderFragment = (ChooseProviderFragment)
                    fm.findFragmentByTag(ChooseProviderFragment.class.getSimpleName());

            if (chooseProviderFragment == null) {
                chooseProviderFragment = new ChooseProviderFragment();
            }

            fm.beginTransaction().replace(R.id.add_appointments_frag_holder,
                    chooseProviderFragment,
                    ChooseProviderFragment.class.getSimpleName()).commit();
        }
    };

    /**
     *Click listener for edit range and edit date range button
     */
    private View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            AppointmentDateRangeFragment appointmentDateRangeFragment = (AppointmentDateRangeFragment)
                    fragmentManager.findFragmentByTag(AppointmentDateRangeFragment.class.getSimpleName());

            if (appointmentDateRangeFragment == null) {
                appointmentDateRangeFragment = new AppointmentDateRangeFragment();
            }

            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
            bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(selectedResourcesDTO));
            bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(selectedVisitTypeDTO));
            bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(resourcesToScheduleDTO));
            bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID,addAppointmentPatientId);
            appointmentDateRangeFragment.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder,
                    appointmentDateRangeFragment,
                    AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };

    public static String getAppointmentDate() {
        return appointmentDate;
    }

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
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt",resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());
        queryMap.put("visit_reason_id", selectedVisitTypeDTO.getId()+"");
        queryMap.put("resource_ids", selectedResourcesDTO.getResource().getId()+"");
        if(startDate!=null){
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        if(endDate!=null){
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        TransitionDTO transitionDTO = resourcesToScheduleDTO.getMetadata().getLinks().getAppointmentAvailability();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);

            setAdapters();

            updateDateRange();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {

        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        payloadDTO.setStartTime(appointmentsSlotsDTO.getStartTime());
        payloadDTO.setEndTime(appointmentsSlotsDTO.getEndTime());
        payloadDTO.setProviderId(selectedResourcesDTO.getResource().getProvider().getId().toString());
        payloadDTO.setVisitReasonId(selectedVisitTypeDTO.getId());
        payloadDTO.setResourceId(selectedResourcesDTO.getResource().getId());
        payloadDTO.setChiefComplaint(selectedVisitTypeDTO.getName());

        AppointmentPatientDTO patientDTO = new AppointmentPatientDTO();
        patientDTO.setId(addAppointmentPatientId);
        payloadDTO.setPatient(patientDTO);

        AppointmentProviderDTO providersDTO;
        providersDTO = selectedResourcesDTO.getResource().getProvider();

        AppointmentLocationsDTO locationDTO = availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation();
        if(locationDTO == null){
            locationDTO = new AppointmentLocationsDTO();
            AppointmentAddressDTO addressDTO = new AppointmentAddressDTO();
            locationDTO.setName(resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentsPlaceNameHeading());
            locationDTO.setAddress(addressDTO);
        }

        payloadDTO.setLocation(locationDTO);
        payloadDTO.setProvider(providersDTO);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPayload(payloadDTO);

        new RequestAppointmentDialog(getActivity(),appointmentDTO,resourcesToScheduleDTO).show();
    }

    @Override
    public void onSelectLocation(AppointmentLocationsDTO appointmentLocationsDTO) {
        if(appointmentLocationsDTO == null) {//selected ALL locations
            resetLocatonSelections(true);
        }else{
            if(selectedLocations.isEmpty()) {//Initial state, reset location selections to remove the All selected status
                resetLocatonSelections(false);
            }
            if(!selectedLocations.contains(appointmentLocationsDTO)){
                selectedLocations.add(appointmentLocationsDTO);
            }else if(selectedLocations.contains(appointmentLocationsDTO)){
                selectedLocations.remove(appointmentLocationsDTO);
            }

            updateSelectedLocationsForAdapter();

        }

        setAdapters();

    }


    private List<AppointmentLocationsDTO> extractAvailableLocations(AppointmentAvailabilityDTO availabilityDTO){
        List<AppointmentLocationsDTO> locationsDTOs = new LinkedList<>();
        List<AppointmentAvailabilityPayloadDTO> availableAppointments = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        for(AppointmentAvailabilityPayloadDTO availableAppointment : availableAppointments){
            locationsDTOs.add(availableAppointment.getLocation());
        }
        locationsDTOs.addAll(getExtraLocationsStub(3));
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

    private boolean isLocationSelected(AppointmentLocationsDTO appointmentLocationsDTO){
        return selectedLocations.isEmpty() || selectedLocations.contains(appointmentLocationsDTO);
    }

    List<AppointmentLocationsDTO> extraLocationsStub = new LinkedList<>();
    List<AppointmentLocationsDTO> getExtraLocationsStub(int count){
        if(extraLocationsStub.size()<count) {
            for (int i = 0; i <(count-extraLocationsStub.size()); i++){
                AppointmentLocationsDTO locationsDTO = new AppointmentLocationsDTO();
                locationsDTO.setName("Stub Location "+i);
                extraLocationsStub.add(locationsDTO);
            }
        }
        return extraLocationsStub;
    }

}