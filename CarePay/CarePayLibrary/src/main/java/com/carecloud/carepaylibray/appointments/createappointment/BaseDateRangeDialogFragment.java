package com.carecloud.carepaylibray.appointments.createappointment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author pjohnson on 1/17/19.
 */
public class BaseDateRangeDialogFragment extends BaseDialogFragment {

    private static final long MAX_DATE_RANGE = 35;
    private Date previousStartDate;
    private Date previousEndDate;
    private Date newEndDate;
    private Date newStartDate;
    private CalendarPickerView calendarPickerView;
    private Button applyDateRangeButton;
    private DateCalendarRangeInterface callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
        } else if (context instanceof DateCalendarRangeInterface) {
            callback = (DateCalendarRangeInterface) context;
        } else {
            throw new ClassCastException("context must implement AppointmentViewHandler.");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle bundle = getArguments();
        previousStartDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
        previousEndDate = (Date)
                bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCalendarView(view);
    }

    private void initCalendarView(View view) {
        calendarPickerView = view.findViewById(R.id.calendarView);
        if (previousStartDate != null && previousEndDate != null) {
            /*Instantiate calendar for a date range selected*/
            Collection<Date> selectedDates = new ArrayList<>();
            selectedDates.add(previousStartDate);
            selectedDates.add(previousEndDate);

            calendarPickerView.init(new Date(), getNextFifteenMonthCalendar().getTime())
                    .inMode(CalendarPickerView.SelectionMode.RANGE)
                    .withSelectedDates(selectedDates);
        } else {
            /*Instantiate calendar by default a week as a date range*/
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 1);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 7);

            Date today = new Date();
            calendarPickerView.init(today, getNextFifteenMonthCalendar().getTime())
                    .inMode(CalendarPickerView.SelectionMode.RANGE);
        }

        calendarPickerView.setOnDateSelectedListener(onDateSelectListener);

        applyDateRangeButton = view.findViewById(R.id.applyDateRangeButton);
        applyDateRangeButton.setOnClickListener(v -> {
            if (newEndDate == null) {
                newEndDate = newStartDate;
            }
            callback.setDateRange(newStartDate, newEndDate);
            dismiss();
        });
        applyDateRangeButton.setEnabled(false);
    }

    private Calendar getNextFifteenMonthCalendar() {
        final Calendar nextSixMonths = Calendar.getInstance();
        nextSixMonths.add(Calendar.MONTH, 15);
        return nextSixMonths;
    }

    private CalendarPickerView.OnDateSelectedListener onDateSelectListener =
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

                    boolean acceptableRange = true;
                    if (newStartDate != null && newEndDate != null) {
                        long diff = newEndDate.getTime() - newStartDate.getTime();
                        long numOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        if (numOfDays >= MAX_DATE_RANGE) {
                            Toast.makeText(getActivity(),
                                    String.format(Label.getLabel("appointment.availability.dateRange.maxDaysValidation.message")
                                            , MAX_DATE_RANGE),
                                    Toast.LENGTH_LONG).show();
                            acceptableRange = false;
                        }
                    }

                    applyDateRangeButton.setEnabled(newStartDate != null && acceptableRange);
                }

                @Override
                public void onDateUnselected(Date date) {
                    clearSelectedDate();
                }
            };


    protected View.OnClickListener todayButtonClickListener = new View.OnClickListener() {
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
            applyDateRangeButton.setEnabled(true);
        }
    };

    private void clearSelectedDate() {
        /*removing previously selected dates*/
        newStartDate = null;
        newEndDate = null;
    }
}
