package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableHoursAdapter;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
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

/**
 * Created by sudhir_pingale on 1/2/2017.
 */

public class PracticeAvailableHoursDialog extends BasePracticeDialog implements PracticeAvailableHoursAdapter.SelectAppointmentTimeSlotCallback {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private AppointmentAvailabilityDTO availabilityDTO;
    private RecyclerView availableHoursRecycleView;
    private Date startDate;
    private Date endDate;


    /**
     * Instantiates a new Practice available hours dialog.
     *
     * @param context      the context
     * @param cancelString the cancel string
     */
    public PracticeAvailableHoursDialog(Context context, String cancelString) {
        super(context, cancelString, false);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public PracticeAvailableHoursDialog(Context context, String cancelString, Date startDate, Date endDate) {
        super(context, cancelString, false);
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddContentView(inflater);
        getAvailableHoursTimeSlots();
    }

    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dialog_available_hours_slots, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    private void inflateUIComponents(View view) {
        Button editRangeButton = (Button)
                view.findViewById(R.id.add_appointment_date_pick);
        editRangeButton.setOnClickListener(dateRangeClickListener);
        SystemUtil.setGothamRoundedBoldTypeface(context, editRangeButton);
        updateDateRange(view);

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(context);
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        availableHoursRecycleView = (RecyclerView)
                view.findViewById(com.carecloud.carepaylibrary.R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);
        setDialogTitle(((ScheduleAppointmentActivity) context).getResourcesToSchedule().getMetadata().getLabel()
                .getAvailableHoursHeading());
        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);
    }

    /**
     * Click listener for edit range and edit date range button
     */
    View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new PracticeAvailableHoursDateRangeDialog(context, "").show();
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
     *
     * @param availableHoursListView used as view component
     */
    private void updateDateRange(View availableHoursListView) {
        CarePayTextView dateRangeCustomTextView = (CarePayTextView)
                availableHoursListView.findViewById(R.id.date_range_custom_text_view);
        SystemUtil.setProximaNovaRegularTypeface(context, dateRangeCustomTextView);
        dateRangeCustomTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.glitter));

        DateUtil.getInstance().setFormat(CarePayConstants.RAW_DATE_FORMAT_FOR_CALENDAR_DATE_RANGE);

        String selectedRangeLabel = ((ScheduleAppointmentActivity) context).getResourcesToSchedule().getMetadata().getLabel().getAddAppointmentFromToText();

        if (startDate != null && endDate != null) {

            DateUtil.getInstance().setDate(startDate);
            String formattedStartDate = DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear();
            DateUtil.getInstance().setDate(endDate);
            String formattedEndDate = DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear();
            dateRangeCustomTextView.setText(String.format(selectedRangeLabel, formattedStartDate, formattedEndDate));

        } else {
            /*To show by default one week as range from today*/
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 0);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 6);

            //startDate = rangeStart.getTime();
            //endDate = rangeEnd.getTime();

            DateUtil.getInstance().setDate(rangeEnd.getTime());
            dateRangeCustomTextView.setText(String.format(selectedRangeLabel, ((ScheduleAppointmentActivity) context)
                            .getResourcesToSchedule().getMetadata().getLabel().getTodayAppointmentsHeading(),
                    DateUtil.getInstance().getDateAsDayMonthDayOrdinalYear()));
        }
    }

    private void getAvailableHoursTimeSlots() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt", ((ScheduleAppointmentActivity) context).getResourcesToSchedule().getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeMgmt());
        queryMap.put("practice_id", ((ScheduleAppointmentActivity) context).getResourcesToSchedule().getPayload().getResourcesToSchedule().get(0).getPractice().getPracticeId());
        queryMap.put("visit_reason_id", ((ScheduleAppointmentActivity) context).getSelectedVisitTypeDTO().getId() + "");
        queryMap.put("resource_ids", ((ScheduleAppointmentActivity) context).getSelectedResource().getResource().getId() + "");

        if (startDate != null) {
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().getDateAsyyyyMMdd());
        }

        if (endDate != null) {
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().getDateAsyyyyMMdd());
        }

        TransitionDTO transitionDTO = ((ScheduleAppointmentActivity) context).getResourcesToSchedule().getMetadata().getLinks().getAppointmentAvailability();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(context).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);
            availableHoursRecycleView.setAdapter(new PracticeAvailableHoursAdapter(context,
                    getAvailableHoursListWithHeader(), PracticeAvailableHoursDialog.this));
            ProgressDialogUtil.getInstance(context).dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(context).dismiss();
            SystemUtil.showFaultDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private ArrayList<Object> getAvailableHoursListWithHeader() {
        ArrayList<Object> timeSlotsListWithHeaders = new ArrayList<>();

        if (availabilityDTO != null) {
            List<AppointmentsSlotsDTO> appointmentsSlotsDTOList = availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getSlots();
            if (appointmentsSlotsDTOList != null && appointmentsSlotsDTOList.size() > 0) {
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
        new PracticeRequestAppointmentDialog(context, ((ScheduleAppointmentActivity) context).getResourcesToSchedule()
                .getMetadata().getLabel().getAvailableHoursBack(), appointmentsSlotsDTO, availabilityDTO).show();
        dismiss();
    }
}