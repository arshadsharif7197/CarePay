package com.carecloud.carepaylibray.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;

/**
 * @author pjohnson on 9/07/18.
 */
public class DatePickerFragment extends DialogFragment {

    private DateRangePickerDialogListener listener;
    private String dialogTitle;
    private Date startDate;
    private Date endDate;
    private Date selectedDate;
    private Button applyDateButton;
    private CalendarPickerView calendarPickerView;
    private FragmentActivityInterface callback;

    public interface DateRangePickerDialogListener {
        void onDateSelected(Date selectedDate);
    }

    public static DatePickerFragment newInstance(String dialogTitle,
                                                 Date startDate,
                                                 Date endDate,
                                                 DateRangePickerDialogListener callback) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putSerializable("startDate", startDate);
        args.putSerializable("endDate", endDate);

        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(args);
        dialog.setListener(callback);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        dialogTitle = arguments.getString("dialogTitle");
        startDate = (Date) arguments.getSerializable("startDate");
        endDate = (Date) arguments.getSerializable("endDate");
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
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Date today = new Date();
        View todayButton = toolbar.findViewById(R.id.today_button);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTodayClicked();
            }
        });
        if(today.before(startDate)){
            todayButton.setVisibility(View.GONE);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initCalendarView(View view) {
        calendarPickerView = (CalendarPickerView) view.findViewById(R.id.calendarView);
        calendarPickerView.init(startDate, endDate)
                .inMode(CalendarPickerView.SelectionMode.SINGLE);
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
        applyDateButton.setEnabled(true);
    }

    protected void initButtons(View view) {
        applyDateButton = (Button)
                view.findViewById(R.id.applyDateRangeButton);
        applyDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSelected(selectedDate);
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
