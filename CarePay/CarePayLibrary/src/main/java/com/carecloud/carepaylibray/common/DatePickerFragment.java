package com.carecloud.carepaylibray.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;

/**
 * @author pjohnson on 9/07/18.
 */
public class DatePickerFragment extends BaseDialogFragment {

    private static final int DEFAULT_FLAG = 0;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        startDate = (Date) arguments.getSerializable("startDate");
        endDate = (Date) arguments.getSerializable("endDate");
        selectedDate = (Date) arguments.getSerializable("selectedDate");
        showDate = (Date) arguments.getSerializable("showDate");
        flag = arguments.getInt("flag");
        toolbarTitle = arguments.getString("dialogTitle");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTodayClicked();
            }
        });
        if (today.before(startDate)) {
            todayButton.setVisibility(View.GONE);
        }

        TextView toolbarTitleTextView = toolbar.findViewById(R.id.add_appointment_toolbar_title);
        toolbarTitleTextView.setText(toolbarTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
        calendarPickerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarPickerView.scrollToDate(date);
            }
        }, 200);
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

    protected void initButtons(View view) {
        applyDateButton = view.findViewById(R.id.applyDateRangeButton);
        applyDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSelected(selectedDate, flag);
                dismiss();
            }
        });
        applyDateButton.setEnabled(false);
    }

    private void clearSelectedDate() {
        selectedDate = null;
    }

    public void setListener(DateRangePickerDialogListener listener) {
        this.listener = listener;
    }
}
