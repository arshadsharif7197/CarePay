package com.carecloud.carepay.practice.library.customdialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DateRangePickerDialog extends BaseDialogFragment {

    private CalendarPickerView calendarPickerView;
    private CustomGothamRoundedMediumButton applyDateRangeButton;

    private DateRangePickerDialogListener callback;

    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;

    private String dialogTitle;
    private String cancelString;
    private boolean isCancelable;

    public interface DateRangePickerDialogListener {
        void onRangeSelected(Date start, Date end);

        void onDateRangeCancelled();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if (null == callback) {
                callback = (DateRangePickerDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DateRangePickerDialogListener");
        }
    }

    /**
     * @param dialogTitle title to be shown at the top of the dialog
     * @param closeText label below the close icon
     * @param startDate current start date
     * @param endDate current end date
     * @param minDate minimum date to be picked
     * @param maxDate maximum date to be picked
     * @return new instance of DateRangePickerDialog 
     */
    public static DateRangePickerDialog newInstance(String dialogTitle, String closeText,
                                                    boolean isCancelable, Date startDate, Date endDate, Date minDate, Date maxDate,
                                                    DateRangePickerDialogListener callback) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putBoolean("isCancelable", isCancelable);
        args.putString("cancelString", closeText);
        args.putString("dialogTitle", dialogTitle);
        args.putSerializable("startDate", startDate);
        args.putSerializable("endDate", endDate);
        args.putSerializable("minDate", minDate);
        args.putSerializable("maxDate", maxDate);

        DateRangePickerDialog dialog = new DateRangePickerDialog();
        dialog.setArguments(args);
        dialog.setCallback(callback);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.cancelString = arguments.getString("cancelString");
        this.dialogTitle = arguments.getString("dialogTitle");
        this.startDate = (Date) arguments.getSerializable("startDate");
        this.endDate = (Date) arguments.getSerializable("endDate");
        this.minDate = (Date) arguments.getSerializable("minDate");
        this.maxDate = (Date) arguments.getSerializable("maxDate");
        this.isCancelable = arguments.getBoolean("isCancelable");

        checkDates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initializeApplyDateRangeButton(view);
        initCalendarView(view);
        inflateToolbar(view);

        return view;
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
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), dialogTitleTextView);

        Button todayButton = (Button) view.findViewById(R.id.dialog_date_range_picker_today_button);
        todayButton.setOnClickListener(todayButtonClickListener);
    }

    /**
     * Click listener for today button on toolbar
     */
    private View.OnClickListener todayButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Date today = new Date();
            calendarPickerView.selectDate(today);
            updateSelectedDates();
        }
    };

    private void initializeApplyDateRangeButton(View view) {
        applyDateRangeButton = (CustomGothamRoundedMediumButton)
                view.findViewById(com.carecloud.carepaylibrary.R.id.applyDateRangeButton);
        applyDateRangeButton.setEnabled(false);
        applyDateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onRangeSelected(startDate, endDate);
                }

                dismiss();

            }
        });

        SystemUtil.setGothamRoundedBoldTypeface(getActivity(), applyDateRangeButton);
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
        calendarPickerView.setOnInvalidDateSelectedListener(null);
        calendarPickerView.init(minDate, maxDate)
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(selectedDates);

        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                updateSelectedDates();
            }

            @Override
            public void onDateUnselected(Date date) {
                clearSelectedDate();
            }
        });
    }

    private void updateSelectedDates() {
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
    public static Date getPreviousSixMonthCalendar() {
        final Calendar previousSixMonths = Calendar.getInstance();
        previousSixMonths.add(Calendar.MONTH, -5);
        return previousSixMonths.getTime();
    }

    /**
     * Method to return Calendar instance for next six months
     */
    public static Date getNextSixMonthCalendar() {
        final Calendar nextSixMonths = Calendar.getInstance();
        nextSixMonths.add(Calendar.MONTH, 5);
        return nextSixMonths.getTime();
    }

    @Override
    protected void onDialogCancel() {
        if (null != callback) {
            callback.onDateRangeCancelled();
        }

        dismiss();
    }

    public void setCallback(DateRangePickerDialogListener callback) {
        this.callback = callback;
    }

    @Override
    protected boolean getCancelable() {
        return isCancelable;
    }

    @Override
    protected String getCancelString() {
        return cancelString;
    }

    @Override
    protected int getCancelImageResource() {
        return isCancelable ? R.drawable.icn_close : R.drawable.icn_arrow_left;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_date_range_picker;
    }
}