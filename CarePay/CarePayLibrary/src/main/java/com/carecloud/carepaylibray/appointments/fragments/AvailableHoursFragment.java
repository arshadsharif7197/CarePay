package com.carecloud.carepaylibray.appointments.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AvailableHoursModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AvailableHoursFragment extends Fragment {

    private AppointmentModel model;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            model = (AppointmentModel) bundle.getSerializable("DATA");
        }

        getActivity().setTitle(R.string.apt_available_hours_title);
        ((AddAppointmentActivity) getActivity()).setToolbarNavigationIcon(
                ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_hours_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.available_hours_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new AvailableHoursAdapter(getActivity(), getSampleArrayList(), model));

        final Button datePickerButton = (Button) view.findViewById(R.id.add_appointment_date_pick);
        Calendar calendar = Calendar.getInstance();
        datePickerButton.setText(String.format("%1$tA, %1$tb %1$td", calendar));

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScheduleDate(datePickerButton);
            }
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void setScheduleDate(final Button buttonView) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                buttonView.setText(String.format("%1$tA, %1$tb %1$td", calendar));
            }
        }, year, month, day);

        mDatePicker.setTitle("Pick a Date");
        mDatePicker.show();
    }

    /**
     * Dummy data for now till it get it from JSON file
     */
    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();
        items.add("TOMORROW");
        items.add(new AvailableHoursModel("Thursday", "September", "22", "01:30 PM"));
        items.add(new AvailableHoursModel("Thursday", "September", "22", "02:30 PM"));
        items.add(new AvailableHoursModel("Thursday", "September", "22", "03:00 PM"));
        items.add(new AvailableHoursModel("Thursday", "September", "22", "04:30 PM"));
        items.add("FRIDAY, September 23rd");
        items.add(new AvailableHoursModel("Friday", "September", "23", "01:00 PM"));
        items.add(new AvailableHoursModel("Friday", "September", "23", "02:00 PM"));
        items.add(new AvailableHoursModel("Friday", "September", "23", "03:30 PM"));
        items.add(new AvailableHoursModel("Friday", "September", "23", "04:00 PM"));
        items.add("MONDAY, September 26th");
        items.add(new AvailableHoursModel("Monday", "September", "26", "01:30 PM"));
        items.add(new AvailableHoursModel("Monday", "September", "26", "02:30 PM"));
        items.add(new AvailableHoursModel("Monday", "September", "26", "03:00 PM"));
        return items;
    }
}