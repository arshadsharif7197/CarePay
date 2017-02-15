package com.carecloud.carepay.practice.library.customdialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Created by cocampo on 2/14/17.
 */

public class DateRangePickerDialog extends BasePracticeDialog {

    private final Context context;
    private CalendarPickerView calendarPickerView;
    private CustomGothamRoundedMediumButton applyDateRangeButton;
    private LayoutInflater inflater;
    private View view;

    private DateRangePickerDialogListener callback;

    private Date startDate;
    private Date endDate;

    private final String dialogTitle;
    private final String todayLabel;

    public interface DateRangePickerDialogListener {
        void onRangeSelected(Date start, Date end);
    }

    public DateRangePickerDialog(Context context, String dialogTitle, String closeText, String todayLabel, Date start, Date end, DateRangePickerDialogListener callback) {
        super(context, closeText, false);
        this.context = context;
        this.dialogTitle = dialogTitle;
        this.todayLabel = todayLabel;
        this.startDate = start;
        this.endDate = end;
        this.callback = callback;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkDates();
    }

    private void checkDates() {
        Date today = DateUtil.getInstance().setToCurrent().getDate();
        // If empty set to today
        if (null == startDate) {
            startDate = today;
        }
        // If empty set to today
        if (null == endDate) {
            endDate = today;
        }
        // If Start Date is after End Date, set End Date to Start Date
        DateTime startDateTime = new DateTime(startDate);
        DateTime endDateTime = new DateTime(endDate);
        if (startDateTime.isAfter(endDateTime)) {
            endDate = startDate;
        }
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
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dialog_date_range_picker_toolbar);
        toolbar.setTitle("");

        TextView dialogTitleTextView = (TextView) view.findViewById(R.id.dialog_date_range_picker_dialog_title);
        dialogTitleTextView.setText(dialogTitle);
        SystemUtil.setGothamRoundedMediumTypeface(context, dialogTitleTextView);

        Button todayButton = (Button) view.findViewById(R.id.dialog_date_range_picker_today_button);
        todayButton.setText(todayLabel);
        todayButton.setOnClickListener(todayButtonClickListener);
    }

    /**
     * Click listener for today button on toolbar
     */
    private View.OnClickListener todayButtonClickListener = new View.OnClickListener() {
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

            startDate = today;
        }
    };

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dialog_date_range_picker, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);

        inflateUIComponents(view);
    }

    private void inflateUIComponents(View view) {
        removeHeader();
        initializeApplyDateRangeButton(view);
        setCancelImage(R.drawable.icn_close);
    }

    private void initializeApplyDateRangeButton(View view) {
        applyDateRangeButton = (CustomGothamRoundedMediumButton)
                view.findViewById(com.carecloud.carepaylibrary.R.id.applyDateRangeButton);
        applyDateRangeButton.setEnabled(false);
        applyDateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (null != callback) {
                    callback.onRangeSelected(startDate, endDate);
                }

                dismiss();

            }
        });

        SystemUtil.setGothamRoundedBoldTypeface(context, applyDateRangeButton);
    }

    /**
     * Method to inflate and initialize custom calendar view
     *
     * @param view used as view component
     */
    private void initCalendarView(View view) {
        /*Instantiate calendar for a date range selected*/
        Collection<Date> selectedDates = new ArrayList<>();
        selectedDates.add(startDate);
        selectedDates.add(endDate);

        calendarPickerView = (CalendarPickerView) view.findViewById(com.carecloud.carepaylibrary.R.id.calendarView);
        calendarPickerView.init(getPreviousSixMonthCalendar(), getNextSixMonthCalendar())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);

        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> dateList = calendarPickerView.getSelectedDates();

                if (dateList.size() > 1) {
                    startDate = dateList.get(0);
                    endDate = dateList.get(dateList.size() - 1);
                } else if (dateList.size() == 1) {
                    if (startDate != null) {
                        endDate = dateList.get(0);
                    } else {
                        startDate = dateList.get(0);
                    }
                }

                applyDateRangeButton.setEnabled(startDate != null && endDate != null);
            }

            @Override
            public void onDateUnselected(Date date) {
                clearSelectedDate();
            }
        });
    }


    /**
     * Method to remove selected date
     */
    private void clearSelectedDate() {
        /*removing previously selected dates*/
        startDate = null;
        endDate = null;
    }

    /**
     * Method to return Calendar instance for previous six months
     */
    private Date getPreviousSixMonthCalendar() {
        final Calendar previousSixMonths = Calendar.getInstance();
        previousSixMonths.add(Calendar.MONTH, -5);
        return previousSixMonths.getTime();
    }

    /**
     * Method to return Calendar instance for next six months
     */
    private Date getNextSixMonthCalendar() {
        final Calendar nextSixMonths = Calendar.getInstance();
        nextSixMonths.add(Calendar.MONTH, 5);
        return nextSixMonths.getTime();
    }

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    @Override
    protected void onDialogCancel() {
        dismiss();
    }
}
