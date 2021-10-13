package com.carecloud.carepaylibray.common;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

/**
 * @author pjohnson on 9/07/18.
 */
public class DatePickerFragment extends BaseDialogFragment {

    private static final int DEFAULT_FLAG = 0;
    public static final int FROM_DATE_FLAG = 100;
    public static final int TO_DATE_FLAG = 101;

    private DateRangePickerDialogListener listener;
    private Date startDate;
    private Date endDate;
    private Date selectedDate;
    private Date showDate;
    private Button applyDateButton;
    private CalendarPickerView calendarPickerView;
    private int flag;
    private String toolbarTitle;

    public interface DateRangePickerDialogListener {
        void onDateSelected(Date selectedDate, int flag);
    }

    public static DatePickerFragment newInstance(String dialogTitle,
                                                 Date startDate,
                                                 Date endDate,
                                                 DateRangePickerDialogListener callback) {


        return newInstance(dialogTitle, startDate, endDate, callback, DEFAULT_FLAG);
    }

    public static DatePickerFragment newInstance(String dialogTitle,
                                                 Date startDate,
                                                 Date endDate,
                                                 DateRangePickerDialogListener callback,
                                                 int flag) {
        return newInstance(dialogTitle, startDate, endDate, null, null, callback, flag);
    }

    public static DatePickerFragment newInstance(String dialogTitle,
                                                 Date startDate,
                                                 Date endDate,
                                                 Date selectedDate,
                                                 Date showDate,
                                                 DateRangePickerDialogListener callback,
                                                 int flag) {
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putSerializable("startDate", startDate);
        args.putSerializable("endDate", endDate);
        if (selectedDate != null) {
            args.putSerializable("selectedDate", selectedDate);
        }
        args.putSerializable("showDate", showDate);
        args.putInt("flag", flag);

        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(args);
        dialog.setListener(callback);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        startDate = (Date) arguments.getSerializable("startDate");
        endDate = (Date) arguments.getSerializable("endDate");
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(endDate.getTime());
        endCalendar.add(Calendar.DAY_OF_YEAR, 1);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        if (endCalendar.compareTo(Calendar.getInstance()) < 0) {
            endDate = endCalendar.getTime();
        }
        selectedDate = (Date) arguments.getSerializable("selectedDate");
        showDate = (Date) arguments.getSerializable("showDate");
        flag = arguments.getInt("flag");
        toolbarTitle = arguments.getString("dialogTitle");
    }


    public void onCancel() {
        isVisible = true;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateToolbar(view);
        initCalendarView(view);
        initButtons(view);
    }

    /**
     * Method to inflate toolbar to UI
     *
     * @param view used as view component
     */
    private void inflateToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.add_appointment_toolbar);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);

        Date today = new Date();
        View todayButton = toolbar.findViewById(R.id.today_button);
        todayButton.setOnClickListener(v -> onTodayClicked());
        if (today.before(startDate) || flag == FROM_DATE_FLAG) {
            todayButton.setVisibility(View.GONE);
        }

        TextView toolbarTitleTextView = toolbar.findViewById(R.id.add_appointment_toolbar_title);
        toolbarTitleTextView.setText(toolbarTitle);
        toolbar.setNavigationOnClickListener(v -> cancel());
    }

    private void initCalendarView(View view) {
        calendarPickerView = view.findViewById(R.id.calendarView);
        calendarPickerView.init(startDate, endDate)
                .inMode(CalendarPickerView.SelectionMode.SINGLE);
        if (selectedDate != null) {
            calendarPickerView.selectDate(selectedDate);
            scrollToDate(selectedDate);
        } else {
            if (showDate == null) {
                showDate = new Date();
            }
            scrollToDate(showDate);
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

    private void scrollToDate(final Date date) {
        calendarPickerView.postDelayed(() -> calendarPickerView.scrollToDate(date), 200);
    }

    private void updateSelectedDates() {
        selectedDate = calendarPickerView.getSelectedDate();
        applyDateButton.setEnabled(selectedDate != null);
    }

    private void onTodayClicked() {
        clearSelectedDate();
        applyDateButton.setEnabled(false);
        Date today = new Date();
        calendarPickerView.init(startDate, endDate)
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.SINGLE);
        selectedDate = calendarPickerView.getSelectedDate();
        applyDateButton.setEnabled(true);
    }

    private void initButtons(View view) {
        applyDateButton = view.findViewById(R.id.applyDateRangeButton);
        applyDateButton.setOnClickListener(v -> {
            listener.onDateSelected(selectedDate, flag);
            cancel();
        });
        applyDateButton.setEnabled(false);
    }

    private void clearSelectedDate() {
        selectedDate = null;
    }

    public void  setListener(DateRangePickerDialogListener listener) {
        this.listener = listener;
    }
}
