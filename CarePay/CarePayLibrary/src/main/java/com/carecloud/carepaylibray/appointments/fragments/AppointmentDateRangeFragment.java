package com.carecloud.carepaylibray.appointments.fragments;

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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AppointmentDateRangeFragment extends Fragment {

    private AppointmentModel model;
    private CalendarPickerView calendarPickerView;
    private List<Date> dateList;
    private Date todayDate, startDate, endDate;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            model = (AppointmentModel)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE);
            todayDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE);
            startDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            endDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);
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
     * Method to inflate toolbar to UI.
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
     */
    private void inflateUIComponents(View view) {
        TextView sundayTextView = (TextView) view.findViewById(R.id.sundayTextView);
        TextView mondayTextView = (TextView) view.findViewById(R.id.mondayTextView);
        TextView tuesdayTextView = (TextView) view.findViewById(R.id.tuesdayTextView);
        TextView wednesdayTextView = (TextView) view.findViewById(R.id.wednesdayTextView);
        TextView thursdayTextView = (TextView) view.findViewById(R.id.thursdayTextView);
        TextView fridayTextView = (TextView) view.findViewById(R.id.fridayTextView);
        TextView saturdayTextView = (TextView) view.findViewById(R.id.saturdayTextView);

        sundayTextView.setTextColor(ContextCompat.getColor(getContext(),
                R.color.medium_jungle_green));
        mondayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        tuesdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        wednesdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        thursdayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        fridayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        saturdayTextView.setTextColor(ContextCompat.getColor(getContext(),
                R.color.medium_jungle_green));
    }

    /**
     * Method to inflate and initialize custom calendar view
     */
    private void initCalendarView(View view) {
        calendarPickerView=(CalendarPickerView)view.findViewById(R.id.calendarView);
        if(todayDate!=null) {
            /*Instantiate calendar for today/single date selected*/
            calendarPickerView.init(new Date(), getNextSixMonthCalendar().getTime())
                .withSelectedDate(todayDate)
                .inMode(CalendarPickerView.SelectionMode.RANGE);
        } else if(startDate!=null&&endDate!=null) {
            /*Instantiate calendar for a date range selected*/
            Collection<Date> selectedDates = new ArrayList<Date>();
            selectedDates.add(startDate);
            selectedDates.add(endDate);
            calendarPickerView.init(new Date(), getNextSixMonthCalendar().getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);
        } else {
            /*Instantiate calendar by default a week as a date range*/
            Date today = new Date();
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 1);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 7);

            Collection<Date> selectedDates = new ArrayList<Date>();
            selectedDates.add(rangeStart.getTime());
            selectedDates.add(rangeEnd.getTime());

            calendarPickerView.init(today, getNextSixMonthCalendar().getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);
        }

        calendarPickerView.setOnDateSelectedListener(onDateSelectListener);

        CustomGothamRoundedMediumButton applyDateRangeButton = (CustomGothamRoundedMediumButton)
                view.findViewById(R.id.applyDateRangeButton);
        applyDateRangeButton.setOnClickListener(applyButtonClickListener);

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
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE, model);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE, todayDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE,
                startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
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
            if (dateList.size() == 1) {
                todayDate = dateList.get(0);
            }
        }

        @Override
        public void onDateUnselected(Date date) {
        }
    };

    /**
     * Click listener for today button on toolbar
     */
    View.OnClickListener todayButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Date today = new Date();
        Calendar nextSixMonths = Calendar.getInstance();
        nextSixMonths.add(Calendar.MONTH, 5);

        calendarPickerView.init(today, nextSixMonths.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        FragmentManager fm = getFragmentManager();
        AvailableHoursFragment availableHoursFragment = (AvailableHoursFragment)
                fm.findFragmentByTag(AppointmentDateRangeFragment.class.getSimpleName());

        if (availableHoursFragment == null) {
            availableHoursFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE, model);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE, today);
        availableHoursFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, availableHoursFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
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
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE, model);
        if (dateList != null && dateList.size() > 1) {
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE,
                dateList.get(0));
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE,
                dateList.get(dateList.size() - 1));
        } else if(todayDate != null) {
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE,
                todayDate);
        } else if (startDate != null && endDate != null) {
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE,
                startDate);
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE,
                endDate);
        }

        availableHoursFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, availableHoursFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };
}