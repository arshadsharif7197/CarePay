package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentFragment;
import com.carecloud.carepaylibray.appointments.interfaces.DateRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customcomponents.CustomCalendarCellDecorator;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by arpit_jain1 on 10/10/2016.
 * Custom Calendar Fragment
 */
public class AppointmentDateRangeFragment extends BaseAppointmentFragment {

    private CalendarPickerView calendarPickerView;
    private Button applyDateRangeButton;
    private Date previousStartDate;
    private Date previousEndDate;
    private Date newStartDate;
    private Date newEndDate;
    private VisitTypeDTO selectedVisitTypeDTO;
    private AppointmentResourcesItemDTO selectedResourcesDTO;
    private AppointmentsResultModel resourcesToScheduleDTO;

    private DateRangeInterface callback;

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
                callback = (DateRangeInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            previousStartDate = (Date)
                    bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            previousEndDate = (Date)
                    bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);

            Gson gson = new Gson();
            String appointmentInfoString;
            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE);
            selectedVisitTypeDTO = gson.fromJson(appointmentInfoString, VisitTypeDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
            selectedResourcesDTO = gson.fromJson(appointmentInfoString, AppointmentResourcesItemDTO.class);

            appointmentInfoString = bundle.getString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE);
            resourcesToScheduleDTO = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);

//            addAppointmentPatientId = bundle.getString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID);
        }
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_date_range, container, false);

        /*inflate toolbar*/
        hideDefaultActionBar();
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Button todayButton = (Button) toolbar.findViewById(R.id.today_button);
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
                R.color.dark_blue));

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
                R.color.dark_blue));
    }

    /**
     * Method to inflate and initialize custom calendar view
     *
     * @param view used as view component
     */
    private void initCalendarView(View view) {
        calendarPickerView = (CalendarPickerView) view.findViewById(R.id.calendarView);
        if (previousStartDate != null && previousEndDate != null) {
            /*Instantiate calendar for a date range selected*/
            Collection<Date> selectedDates = new ArrayList<>();
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

            Date today = new Date();
            calendarPickerView.init(today, getNextSixMonthCalendar().getTime())
                    .inMode(CalendarPickerView.SelectionMode.RANGE);
        }

        calendarPickerView.setOnDateSelectedListener(onDateSelectListener);
        List<CalendarCellDecorator> decorators = new ArrayList<>();
        decorators.add(new CustomCalendarCellDecorator(ContextCompat.getColor(getContext(), R.color.white),
                ContextCompat.getColor(getContext(), R.color.payne_gray)));
        calendarPickerView.setDecorators(decorators);

        applyDateRangeButton = (Button)
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

            getActivity().onBackPressed();
        }
    };

    /**
     * Click listener for calendar dates
     */
    CalendarPickerView.OnDateSelectedListener onDateSelectListener =
            new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    List<Date> dateList = calendarPickerView.getSelectedDates();

                    if (dateList.size() > 1) {
                        newStartDate = dateList.get(0);
                        newEndDate = dateList.get(dateList.size() - 1);
                    } else if (dateList.size() == 1) {
                        if (newStartDate != null) {
                            newEndDate = dateList.get(0);
                        } else {
                            newStartDate = dateList.get(0);
                        }
                    }

                    if (newStartDate != null && newEndDate != null) {
                        long diff = newEndDate.getTime() - newStartDate.getTime();
                        long numOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        if (numOfDays >= 93) {
                            AppointmentLabelDTO label = resourcesToScheduleDTO.getMetadata().getLabel();
                            Toast.makeText(getActivity(), label.getAddAppointmentMaxDateRangeMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            applyDateRangeButton.setEnabled(true);
                        }
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
            callback.onDateRangeSelected(newStartDate, newEndDate, selectedVisitTypeDTO, selectedResourcesDTO, resourcesToScheduleDTO);
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