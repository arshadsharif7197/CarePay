package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableHoursAdapter;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class PracticeAvailableHoursDateRangeDialog extends BasePracticeDialog implements PracticeAvailableHoursAdapter.SelectAppointmentTimeSlotCallback {

    private CalendarPickerView calendarPickerView;
    private CustomGothamRoundedMediumButton applyDateRangeButton;
    private List<Date> dateList;
    private Date newStartDate;
    private Date newEndDate;
    private Context context;
    private LayoutInflater inflater;
    private View view;

    /**
     * Instantiates a new Practice available hours dialog.
     *
     * @param context      the context
     * @param cancelString the cancel string
     */
    public PracticeAvailableHoursDateRangeDialog(Context context, String cancelString) {
        super(context, cancelString, false);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddContentView(inflater);
        initCalendarView(view);
        inflateToolbar(view);
    }

    /**
     * Method to inflate toolbar to UI
     *
     * @param view used as view component don't
     */
    private void inflateToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.add_appointment_toolbar_title);
         SystemUtil.setGothamRoundedMediumTypeface(context, titleView);
         toolbar.setTitle("");


        CustomGothamRoundedMediumButton todayButton = (CustomGothamRoundedMediumButton)
                toolbar.findViewById(com.carecloud.carepaylibrary.R.id.today_button);
        todayButton.setOnClickListener(todayButtonClickListener);

        //toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

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


    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dialog_edit_date_range, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);

        inflateUIComponents(view);
    }

    private void inflateUIComponents(View view) {

        removeHeader();

        Button editRangeButton = (Button)
                view.findViewById(R.id.applyDateRangeButton);

        editRangeButton.setOnClickListener(dateRangeClickListener);
        SystemUtil.setGothamRoundedBoldTypeface(context, editRangeButton);

        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);

        TextView sundayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.sundayTextView);
        sundayTextView.setTextColor(ContextCompat.getColor(getContext(),
                com.carecloud.carepaylibrary.R.color.medium_jungle_green));

        TextView mondayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.mondayTextView);
        mondayTextView.setTextColor(ContextCompat.getColor(getContext(), com.carecloud.carepaylibrary.R.color.white));

        TextView tuesdayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.tuesdayTextView);
        tuesdayTextView.setTextColor(ContextCompat.getColor(getContext(), com.carecloud.carepaylibrary.R.color.white));

        TextView wednesdayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.wednesdayTextView);
        wednesdayTextView.setTextColor(ContextCompat.getColor(getContext(), com.carecloud.carepaylibrary.R.color.white));

        TextView thursdayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.thursdayTextView);
        thursdayTextView.setTextColor(ContextCompat.getColor(getContext(), com.carecloud.carepaylibrary.R.color.white));

        TextView fridayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.fridayTextView);
        fridayTextView.setTextColor(ContextCompat.getColor(getContext(), com.carecloud.carepaylibrary.R.color.white));

        TextView saturdayTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.saturdayTextView);
        saturdayTextView.setTextColor(ContextCompat.getColor(getContext(),
                com.carecloud.carepaylibrary.R.color.medium_jungle_green));

    }


    /**
     * Method to inflate and initialize custom calendar view
     *
     * @param view used as view component
     */
    private void initCalendarView(View view) {
        calendarPickerView = (CalendarPickerView) view.findViewById(com.carecloud.carepaylibrary.R.id.calendarView);
        if (newStartDate != null && newEndDate != null) {
            /*Instantiate calendar for a date range selected*/
            Collection<Date> selectedDates = new ArrayList<Date>();
            selectedDates.add(newStartDate);
            selectedDates.add(newEndDate);
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
                view.findViewById(com.carecloud.carepaylibrary.R.id.applyDateRangeButton);
        applyDateRangeButton.setOnClickListener(applyButtonClickListener);
        applyDateRangeButton.setEnabled(false);
    }

    /*
    *   Click listener for apply button
    */
    View.OnClickListener applyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new PracticeAvailableHoursDialog(context, "", newStartDate, newEndDate).show();
            dismiss();

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
                        if (newStartDate != null) {
                            newEndDate = dateList.get(0);
                        } else {
                            newStartDate = dateList.get(0);
                        }
                    }

                    if (newStartDate != null && newEndDate != null) {
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
     * Method to remove selected date
     */
    void clearSelectedDate() {
        /*removing previously selected dates*/
        newStartDate = null;
        newEndDate = null;
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
     * Click listener for edit range and edit date range button
     */
    View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    @Override
    protected void onDialogCancel() {
        new PracticeAvailableHoursDialog(context, "", newStartDate, newEndDate).show();
        dismiss();
    }


    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        new PracticeRequestAppointmentDialog(context, ((ScheduleAppointmentActivity) context).getResourcesToSchedule()
                .getMetadata().getLabel().getAvailableHoursBack(), appointmentsSlotsDTO).show();

        dismiss();
    }
}




