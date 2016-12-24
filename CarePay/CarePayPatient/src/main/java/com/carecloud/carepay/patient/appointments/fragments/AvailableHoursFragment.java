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
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentProviderDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableHoursFragment extends Fragment implements AvailableHoursAdapter.SelectAppointmentTimeSlotCallback{

    private static String appointmentDate;
    private Date startDate;
    private Date endDate;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentResourcesDTO selectedResourcesDTO;
    private RecyclerView availableHoursRecycleView;
    private AppointmentsResultModel resourcesToScheduleDTO;

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
        updateDateRange(availableHoursListView);

        return availableHoursListView;
    }

    /**
     * Method to inflate toolbar to UI
     *
     * @param availableHoursListView used as view component
     */
    private void inflateToolbar(View availableHoursListView) {
        Toolbar toolbar = (Toolbar)
                availableHoursListView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_available_hours_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), titleView);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

    /**
     * Method to inflate UI components
     *
     * @param availableHoursListView used as view component
     */
    private void inflateUIComponents(View availableHoursListView) {
        Button editRangeButton = (Button)
                availableHoursListView.findViewById(R.id.add_appointment_date_pick);
        editRangeButton.setOnClickListener(dateRangeClickListener);

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(getActivity());
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        availableHoursRecycleView = (RecyclerView)
                availableHoursListView.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);

        /*availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(),
                getSampleArrayList(), AvailableHoursFragment.this));*/

        Button editDateRangeButton = (Button)
                availableHoursListView.findViewById(R.id.edit_date_range_button);
        editDateRangeButton.setOnClickListener(dateRangeClickListener);
    }

    /**
     * Method to update date range that is selected on calendar
     *
     * @param availableHoursListView used as view component
     */
    private void updateDateRange(View availableHoursListView) {
        CustomProxyNovaSemiBoldLabel dateRangeCustomTextView = (CustomProxyNovaSemiBoldLabel)
                availableHoursListView.findViewById(R.id.date_range_custom_text_view);
        dateRangeCustomTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.glitter));

        DateUtil.getInstance().setFormat(CarePayConstants.RAW_DATE_FORMAT_FOR_CALENDAR_DATE_RANGE);

        if (startDate != null && endDate != null) {
            DateUtil.getInstance().setDate(startDate);
            String formattedStartDate = DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear();
            DateUtil.getInstance().setDate(endDate);
            String formattedEndDate = DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear();

            dateRangeCustomTextView.setText(formattedStartDate + " " +
                    getResources().getString(R.string.to) + " " + formattedEndDate);
        } else {
            /*To show by default one week as range from today*/
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 0);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 6);

            startDate = rangeStart.getTime();
            endDate = rangeEnd.getTime();

            DateUtil.getInstance().setDate(endDate);
            dateRangeCustomTextView.setText(
                    getResources().getString(R.string.date_range_from_today_to)
                    + " " + DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear());
        }
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
    View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
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
        appointmentDateRangeFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder,
            appointmentDateRangeFragment,
            AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };

    public static String getAppointmentDate() {
        return appointmentDate;
    }

    private ArrayList<Object> getAvailableHoursListWithHeader(){
        ArrayList<Object> timeSlotsListWithHeaders = new ArrayList<>();

        if(availabilityDTO!=null){
            List<AppointmentsSlotsDTO> appointmentsSlotsDTOList = availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getSlots();
            if(appointmentsSlotsDTOList!=null && appointmentsSlotsDTOList.size()>0){
                // To sort appointment time slots list based on time
                Collections.sort(appointmentsSlotsDTOList, new Comparator<AppointmentsSlotsDTO>() {
                    public int compare(AppointmentsSlotsDTO obj1, AppointmentsSlotsDTO obj2) {
                        String dateO1 = obj1.getStartTime();
                        String dateO2 = obj2.getStartTime();

                        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
                        Date date1 = DateUtil.getInstance().setDateRaw(dateO1).getDate();
                        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
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

                        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
                        Date date2 = DateUtil.getInstance().setDateRaw(date02).getDate();
                        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
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
                        headerTitle = getSectionHeaderTitle(timSlotsDTO.getStartTime());
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
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        final Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // day after tomorrow date
        String dayAfterTomorrowDate = DateUtil.getInstance().setToDayAfterTomorrow().getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        final Date dayAfterTomorrowConvertedDate = DateUtil.getInstance().setDateRaw(dayAfterTomorrowDate).getDate();

        // Appointment time slot date
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        String appointmentDate = DateUtil.getInstance().setDateRaw(timeSlotRawDate).getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        final Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String headerText;
        if (convertedAppointmentDate.after(dayAfterTomorrowConvertedDate) ||
                appointmentDate.equalsIgnoreCase(dayAfterTomorrowDate)){
            headerText = DateUtil.getInstance().getDateAsDayMonthDayOrdinal();
        } else if (convertedAppointmentDate.after(currentConvertedDate) && convertedAppointmentDate.before(dayAfterTomorrowConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            headerText = "TOMORROW";
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = "TODAY";
        } else {
            headerText = "TODAY";
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
            queryMap.put("start_date", DateUtil.getInstance().getDateAsyyyyMMdd());
        }
        if(endDate!=null){
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().getDateAsyyyyMMdd());
        }

        TransitionDTO transitionDTO = resourcesToScheduleDTO.getMetadata().getLinks().getAppointmentAvailability();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getActivity()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);
            availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(),
                    getAvailableHoursListWithHeader(), AvailableHoursFragment.this));
            ProgressDialogUtil.getInstance(getActivity()).dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        AppointmentsPayloadDTO payloadDTO = new AppointmentsPayloadDTO();
        AppointmentProviderDTO providersDTO = new AppointmentProviderDTO();
        AppointmentLocationDTO locationDTO = new AppointmentLocationDTO();

//        payload.getStartTime()
//        payload.getProvider().getName()
//        payload.getProvider().getPhone()
//        payload.getProvider().getSpecialty().getName()
//        payload.getLocation().getName()
//        payload.getLocation().getAddress()
//        payload.getLocation().getAddress().getPlaceAddressString()

//        start_time": "2016-12-19T15:15:00-05:00",
//        o	   "end_time": "2016-12-19T15:20:00-05:00",
//                o	   "location_id": 8775,
//                o	   "provider_id": 130,
//                o	   "visit_reason_id": 47128,
//                o	   "resource_id": 162,
//                o	   "patient": {
//            o	     "id": "000f50a8-f04f-4f3f-8a81-6e546c8e32d4"
//            o	   }


        //new RequestAppointmentDialog(getActivity(),appointmentDTO,new AppointmentLabelDTO()).show();
    }
}