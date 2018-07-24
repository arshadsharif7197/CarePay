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
import com.carecloud.carepay.practice.library.base.BasePracticeDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DateRangePickerDialog extends BasePracticeDialogFragment {

    private CalendarPickerView calendarPickerView;
    private Button applyDateRangeButton;

    private DateRangePickerDialogListener callback;

    private Date startDate;
    private Date endDate;
    private Date minDate;
    private Date maxDate;

    private String dialogTitle;
    private String cancelString;
    private boolean isCancelable;
    private CalendarPickerView.SelectionMode selectionMode;
    private Date selectedDate;

    public interface DateRangePickerDialogListener {
        void onRangeSelected(Date start, Date end);

        void onDateRangeCancelled();

        void onDateSelected(Date selectedDate);
    }

    /**
     * @param dialogTitle   title to be shown at the top of the dialog
     * @param closeText     label below the close icon
     * @param startDate     current start date
     * @param endDate       current end date
     * @param minDate       minimum date to be picked
     * @param maxDate       maximum date to be picked
     * @param selectionMode
     * @return new instance of DateRangePickerDialog
     */
    public static DateRangePickerDialog newInstance(String dialogTitle,
                                                    String closeText,
                                                    boolean isCancelable,
                                                    Date startDate,
                                                    Date endDate,
                                                    Date minDate,
                                                    Date maxDate,
                                                    DateRangePickerDialogListener callback,
                                                    String selectionMode) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putBoolean("isCancelable", isCancelable);
        args.putString("cancelString", closeText);
        args.putString("dialogTitle", dialogTitle);
        args.putSerializable("startDate", startDate);
        args.putSerializable("endDate", endDate);
        args.putSerializable("minDate", minDate);
        args.putSerializable("maxDate", maxDate);
        args.putString("selectionMode", selectionMode);

        DateRangePickerDialog dialog = new DateRangePickerDialog();
        dialog.setArguments(args);
        dialog.setCallback(callback);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (null == callback) {
                callback = (DateRangePickerDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DateRangePickerDialogListener");
        }
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
        this.selectionMode = CalendarPickerView.SelectionMode.valueOf(arguments.getString("selectionMode"));

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
        if (startDate.after(endDate)) {
            //switch them
            Date temp = endDate;
            endDate = startDate;
            startDate = temp;
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

        Date today = new Date();
        Button todayButton = (Button) view.findViewById(R.id.dialog_date_range_picker_today_button);
        todayButton.setOnClickListener(todayButtonClickListener);
        if(today.before(startDate)){
            todayButton.setVisibility(View.GONE);
        }
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
        applyDateRangeButton = (Button)
                view.findViewById(com.carecloud.carepaylibrary.R.id.applyDateRangeButton);
        applyDateRangeButton.setEnabled(false);
        applyDateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    if (selectionMode == CalendarPickerView.SelectionMode.RANGE) {
                        if (endDate == null) {
                            endDate = startDate;
                        }
                        callback.onRangeSelected(startDate, endDate);
                    } else {
                        callback.onDateSelected(selectedDate);
                    }
                }
                dismiss();
            }
        });
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
        if (selectionMode == CalendarPickerView.SelectionMode.RANGE) {
            calendarPickerView.init(minDate, maxDate)
                    .inMode(selectionMode)
                    .withSelectedDates(selectedDates);
        } else {
            calendarPickerView.init(minDate, maxDate).inMode(selectionMode);
        }

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
        if (selectionMode == CalendarPickerView.SelectionMode.RANGE) {
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
            applyDateRangeButton.setEnabled(startDate != null);
        } else {
            selectedDate = calendarPickerView.getSelectedDate();
            applyDateRangeButton.setEnabled(selectedDate != null);
        }
    }

    /**
     * Method to remove selected date
     */
    private void clearSelectedDate() {
        /*removing previously selected dates*/
        startDate = null;
        endDate = null;
        selectedDate = null;
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
