package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by arpit_jain1 on 10/10/2016.
 * Custom Calendar Fragment
 */
public class AppointmentDateRangeFragment extends Fragment {

    private CalendarPickerView calendarPickerView;
    private CustomGothamRoundedMediumButton applyDateRangeButton;
    private List<Date> dateList;
    private Date previousStartDate;
    private Date previousEndDate;
    private Date newStartDate;
    private Date newEndDate;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentResourcesDTO selectedResourcesDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String appointmentInfoString;

            previousStartDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            previousEndDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE);
            selectedVisitTypeDTO = gson.fromJson(appointmentInfoString, VisitTypeDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
            selectedResourcesDTO = gson.fromJson(appointmentInfoString, AppointmentResourcesDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE);
            resourcesToScheduleDTO = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);
        }
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_date_range, container, false);

        /*inflate toolbar*/
        inflateToolbar(view);
        /*inflate other UI components like text view, button etc.*/
        inflateUIComponents(view);
        /*inflate and initialize custom calendar view*/
        initCalendarView(view);

        return view;
    }

    /**
     * Method to inflate toolbar to UI
     *
     * @param view used as view component
     */
    private void inflateToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), titleView);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);

        CustomGothamRoundedMediumButton todayButton = (CustomGothamRoundedMediumButton)
                toolbar.findViewById(R.id.today_button);
        todayButton.setOnClickListener(todayButtonClickListener);

        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

    /**
     * Method to inflate UI components
     *
     * @param view used as view component
     */
    private void inflateUIComponents(View view) {
        TextView sundayTextView = (TextView) view.findViewById(R.id.sundayTextView);
        sundayTextView.setTextColor(ContextCompat.getColor(getContext(),
            R.color.medium_jungle_green));

        TextView mondayTextView = (TextView) view.findViewById(R.id.mondayTextView);
        mondayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView tuesdayTextView = (TextView) view.findViewById(R.id.tuesdayTextView);
        tuesdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView wednesdayTextView = (TextView) view.findViewById(R.id.wednesdayTextView);
        wednesdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView thursdayTextView = (TextView) view.findViewById(R.id.thursdayTextView);
        thursdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView fridayTextView = (TextView) view.findViewById(R.id.fridayTextView);
        fridayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView saturdayTextView = (TextView) view.findViewById(R.id.saturdayTextView);
        saturdayTextView.setTextColor(ContextCompat.getColor(getContext(),
            R.color.medium_jungle_green));
    }

    /**
     * Method to inflate and initialize custom calendar view
     *
     * @param view used as view component
     */
    private void initCalendarView(View view) {
        calendarPickerView=(CalendarPickerView)view.findViewById(R.id.calendarView);
        if(previousStartDate!=null && previousEndDate!=null) {
            /*Instantiate calendar for a date range selected*/
            Collection<Date> selectedDates = new ArrayList<Date>();
            selectedDates.add(previousStartDate);
            selectedDates.add(previousEndDate);
            calendarPickerView.init(new Date(), getNextSixMonthCalendar().getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);
        } else {
            /*Instantiate calendar by default a week as a date range*/
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 1);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 7);

            Collection<Date> selectedDates = new ArrayList<Date>();
            selectedDates.add(rangeStart.getTime());
            selectedDates.add(rangeEnd.getTime());

            Date today = new Date();
            calendarPickerView.init(today, getNextSixMonthCalendar().getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);
        }

        calendarPickerView.setOnDateSelectedListener(onDateSelectListener);

        applyDateRangeButton = (CustomGothamRoundedMediumButton)
                view.findViewById(R.id.applyDateRangeButton);
        applyDateRangeButton.setOnClickListener(applyButtonClickListener);
        applyDateRangeButton.setEnabled(false);
    }

    /**
     * Method to return Calendar instance for next six months
     */
    private Calendar getNextSixMonthCalendar() {
        final Calendar nextSixMonths = Calendar.getInstance();
        nextSixMonths.add(Calendar.MONTH, 5);
        return nextSixMonths;
    }

    /**
     * Click listener for navigation icon on toolbar
     */
    View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        //Launch previous fragment
        FragmentManager fm = getFragmentManager();
        AvailableHoursFragment availableHoursFragment = (AvailableHoursFragment)
                fm.findFragmentByTag(AppointmentDateRangeFragment.class.getSimpleName());

        if (availableHoursFragment == null) {
            availableHoursFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE,
                previousStartDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE,
                previousEndDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(selectedResourcesDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(selectedVisitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(resourcesToScheduleDTO));
        availableHoursFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, availableHoursFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };

    /**
     * Click listener for calendar dates
     */
    CalendarPickerView.OnDateSelectedListener onDateSelectListener =
            new CalendarPickerView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Date date) {
            dateList = calendarPickerView.getSelectedDates();

            if (dateList.size() > 1) {
                newStartDate = dateList.get(0);
                newEndDate = dateList.get(dateList.size() - 1);
            } else if (dateList.size() == 1) {
                if(newStartDate != null) {
                    newEndDate = dateList.get(0);
                } else {
                    newStartDate = dateList.get(0);
                }
            }

            if(newStartDate != null && newEndDate != null) {
                applyDateRangeButton.setEnabled(true);
            } else {
                applyDateRangeButton.setEnabled(false);
            }
        }

        @Override
        public void onDateUnselected(Date date) {
            clearSelectedDate();
        }
    };

    /**
     * Click listener for today button on toolbar
     */
    View.OnClickListener todayButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearSelectedDate();
            applyDateRangeButton.setEnabled(false);
            Date today = new Date();
            Calendar nextSixMonths = Calendar.getInstance();
            nextSixMonths.add(Calendar.MONTH, 5);

            calendarPickerView.init(today, nextSixMonths.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);

            newStartDate = today;
        }
    };

    /*
    *   Click listener for apply button
    */
    View.OnClickListener applyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                AvailableHoursFragment availableHoursFragment = (AvailableHoursFragment)
                        fm.findFragmentByTag(AppointmentDateRangeFragment.class.getSimpleName());

                if (availableHoursFragment == null) {
                    availableHoursFragment = new AvailableHoursFragment();
                }

                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE,
                        newStartDate);
                bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE,
                        newEndDate);
                bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(selectedResourcesDTO));
                bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(selectedVisitTypeDTO));
                bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(resourcesToScheduleDTO));

                availableHoursFragment.setArguments(bundle);

                fm.beginTransaction().replace(R.id.add_appointments_frag_holder,
                        availableHoursFragment,
                        AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };

    /**
     * Method to remove selected date
     */
    void clearSelectedDate() {
        /*removing previously selected dates*/
        newStartDate = null;
        newEndDate = null;
    }
}