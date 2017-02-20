package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableHoursAdapter;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableLocationsAdapter;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationsDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
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

/**
 * Created by sudhir_pingale on 1/2/2017.
 */

public class PracticeAvailableHoursDialog extends BasePracticeDialog implements PracticeAvailableHoursAdapter.SelectAppointmentTimeSlotCallback, PracticeAvailableLocationsAdapter.SelectLocationCallback {

    private Date startDate;
    private Date endDate;
    private Context context;
    private LayoutInflater inflater;
    private AppointmentAvailabilityDTO availabilityDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;

    private RecyclerView availableHoursRecycleView;
    private RecyclerView availableLocationsRecycleView;
    private TextView titleView;
    private View singleLocation;
    private TextView singleLocationText;
    private View progressView;

    private List<AppointmentLocationsDTO> selectedLocations = new LinkedList<>();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    /**
     * Instantiates a new Practice available hours dialog.
     *
     * @param context      the context
     * @param cancelString the cancel string
     */
    public PracticeAvailableHoursDialog(Context context, String cancelString) {
        this(context, cancelString, null, null);
    }

    public PracticeAvailableHoursDialog(Context context, String cancelString, Date startDate, Date endDate) {
        super(context, cancelString, false);
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcesToScheduleDTO = ((ScheduleAppointmentActivity)context).getResourcesToSchedule();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddContentView(inflater);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getAvailableHoursTimeSlots();
                if(progressView!=null){
                    progressView.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_available_hours_slots, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    private void inflateUIComponents(View view) {
        singleLocation = view.findViewById(R.id.practice_available_single_location);
        singleLocationText = (TextView) view.findViewById(R.id.practice_single_location_text);

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(context);
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        availableHoursRecycleView = (RecyclerView) view.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);

        LinearLayoutManager availableLocationsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        availableLocationsRecycleView = (RecyclerView) view.findViewById(R.id.available_locations_recycler);
        availableLocationsRecycleView.setLayoutManager(availableLocationsLayoutManager);

        String range = null;
        try {
            range = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentEditDateRangeButton();

            String location = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentLocationsLabel();
            TextView locationsLabel = (TextView) view.findViewById(R.id.location_text);
            locationsLabel.setText(location != null ? location : context.getString(R.string.locations_label));

            setDialogTitle(resourcesToScheduleDTO.getMetadata().getLabel()
                    .getAvailableHoursHeading());
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }

        TextView editRangeButton = (TextView) view.findViewById(R.id.edit_date_range_button);
        editRangeButton.setText(range != null ? range : context.getString(R.string.edit_date_range_button_label));
        editRangeButton.setOnClickListener(dateRangeClickListener);
        SystemUtil.setGothamRoundedBoldTypeface(context, editRangeButton);

        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);

        progressView = view.findViewById(R.id.progressview);
        progressView.setVisibility(View.VISIBLE);

        updateDateRange();
    }

    private void setAdapters(){
        try {
            List<AppointmentLocationsDTO> locations = extractAvailableLocations(availabilityDTO);

            if (availableHoursRecycleView.getAdapter() == null) {
                availableHoursRecycleView.setAdapter(new PracticeAvailableHoursAdapter(context,
                        getAvailableHoursListWithHeader(), PracticeAvailableHoursDialog.this, locations.size() > 1));
            } else {
                PracticeAvailableHoursAdapter availableHoursAdapter = (PracticeAvailableHoursAdapter) availableHoursRecycleView.getAdapter();
                availableHoursAdapter.setItems(getAvailableHoursListWithHeader());
                availableHoursAdapter.setMultiLocationStyle(locations.size() > 1);
                availableHoursAdapter.notifyDataSetChanged();
            }

            if (locations.size() > 1) {
                availableLocationsRecycleView.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.GONE);
                if (availableLocationsRecycleView.getAdapter() == null) {
                    String all = resourcesToScheduleDTO.getMetadata().getLabel().getAppointmentAllLocationsItem();
                    availableLocationsRecycleView.setAdapter(new PracticeAvailableLocationsAdapter(getContext(), locations, this, all));
                } else {
                    PracticeAvailableLocationsAdapter availableLocationsAdapter = (PracticeAvailableLocationsAdapter) availableLocationsRecycleView.getAdapter();
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
     * Click listener for edit range and edit date range button
     */
    private View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new PracticeAvailableHoursDateRangeDialog(context, availabilityDTO, "", startDate, endDate).show();
            dismiss();
        }
    };

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    @Override
    protected void onDialogCancel() {
/*
        new VisitTypeDialog(context, ((ScheduleAppointmentActivity)context).getSelectedResource(),
                ((ScheduleAppointmentActivity)context), ((ScheduleAppointmentActivity)context).getResourcesToSchedule()).show();
*/
        dismiss();
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
        setDialogTitle(formattedDate);
    }


    private void getAvailableHoursTimeSlots() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", resourcesToScheduleDTO.getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());
        queryMap.put("visit_reason_id", ((ScheduleAppointmentActivity) context).getSelectedVisitTypeDTO().getId() + "");
        queryMap.put("resource_ids", ((ScheduleAppointmentActivity) context).getSelectedResource().getResource().getId() + "");

        if (startDate != null) {
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        if (endDate != null) {
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        TransitionDTO transitionDTO = resourcesToScheduleDTO.getMetadata().getLinks().getAppointmentAvailability();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(context).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(context).dismiss();
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);

            setAdapters();

            updateDateRange();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(context).dismiss();
            SystemUtil.showDefaultFailureDialog(context);
            Log.e(context.getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void resetLocatonSelections(boolean clearAll){
        RecyclerView.LayoutManager layoutManager = availableLocationsRecycleView.getLayoutManager();
        for(int i=0; i<layoutManager.getChildCount(); i++) {
            layoutManager.getChildAt(i).findViewById(R.id.available_location).setSelected(false);
        }
        ((PracticeAvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).resetLocationsSelected(clearAll);
        selectedLocations.clear();
    }

    private void updateSelectedLocationsForAdapter(){
        if(selectedLocations.isEmpty()){//if user removed last location reset everything
            resetLocatonSelections(true);
        }else{
            ((PracticeAvailableLocationsAdapter) availableLocationsRecycleView.getAdapter()).updateSelectedLocations(selectedLocations);
        }
    }

    private ArrayList<Object> getAvailableHoursListWithHeader() {
        ArrayList<Object> timeSlotsListWithHeaders = new ArrayList<>();

        if (availabilityDTO != null && availabilityDTO.getPayload().getAppointmentAvailability().getPayload().size()>0) {
            List<AppointmentAvailabilityPayloadDTO> availabilityPayloadDTOs = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
            List<AppointmentsSlotsDTO> appointmentsSlotsDTOList = groupAllLocatonSlotsByTime(availabilityPayloadDTOs);
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
                        headerTitle = getSectionHeaderTitle(timSlotsDTO.getStartTime());
                        timeSlotsListWithHeaders.add(headerTitle);
                        timeSlotsListWithHeaders.add(timSlotsDTO);
                    }
                }
//                if(!StringUtil.isNullOrEmpty(headerTitle) && headerTitle.contains(",")) {
//                    rangeEndDateString = headerTitle.split(", ")[1];
//                } else {
//                    rangeEndDateString = headerTitle.toLowerCase();
//                }
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
            headerText = availabilityDTO.getMetadata().getLabel().getAddAppointmentTomorrow();
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
        } else {
            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
        }
        return headerText;
    }

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        // Call Request appointment Summary dialog from here
        new PracticeRequestAppointmentDialog(context, resourcesToScheduleDTO.getMetadata().getLabel().getAvailableHoursBack(), appointmentsSlotsDTO, availabilityDTO).show();
        dismiss();
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

    }


    private List<AppointmentLocationsDTO> extractAvailableLocations(AppointmentAvailabilityDTO availabilityDTO){
        List<AppointmentLocationsDTO> locationsDTOs = new LinkedList<>();
        List<AppointmentAvailabilityPayloadDTO> availableAppointments = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        for(AppointmentAvailabilityPayloadDTO availableAppointment : availableAppointments){
            locationsDTOs.add(availableAppointment.getLocation());
        }
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

}