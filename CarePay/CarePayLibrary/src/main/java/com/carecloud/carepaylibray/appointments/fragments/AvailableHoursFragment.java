package com.carecloud.carepaylibray.appointments.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.adapters.AvailableHoursAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailableHoursDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AvailableHoursFragment extends Fragment {

    private AppointmentDTO appointmentDTO;
    private static String appointmentDate;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            appointmentDTO = (AppointmentDTO) bundle.getSerializable("DATA");
        }
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View availableHoursListView = inflater.inflate(R.layout.fragment_available_hours_list, container, false);

        // set the toolbar
        Toolbar toolbar = (Toolbar) availableHoursListView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_available_hours_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), titleView);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch previous fragment
                FragmentManager fm = getFragmentManager();
                ChooseProviderFragment chooseProviderFragment = (ChooseProviderFragment)
                        fm.findFragmentByTag(ChooseProviderFragment.class.getSimpleName());

                if (chooseProviderFragment == null) {
                    chooseProviderFragment = new ChooseProviderFragment();
                }

                fm.beginTransaction().replace(R.id.add_appointments_frag_holder, chooseProviderFragment,
                        ChooseProviderFragment.class.getSimpleName()).commit();
            }
        });

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(getActivity());
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final Button appointmentDatePickerButton = (Button) availableHoursListView.findViewById(R.id.add_appointment_date_pick);
        Calendar calendar = Calendar.getInstance();
        appointmentDate = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(calendar.getTime());
        appointmentDatePickerButton.setText(String.format("%1$tA, %1$tb %1$td", calendar));

        appointmentDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScheduleDate(appointmentDatePickerButton);
            }
        });

        RecyclerView availableHoursRecycleView = (RecyclerView) availableHoursListView.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);
        availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(), getSampleArrayList(), appointmentDTO));

        return availableHoursListView;
    }

    @SuppressLint("DefaultLocale")
    private void setScheduleDate(final Button buttonView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog appointmentDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                appointmentDate = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(calendar.getTime());
                buttonView.setText(String.format("%1$tA, %1$tb %1$td", calendar));
            }
        }, year, month, day);

        appointmentDatePicker.setTitle("Pick a Date");
        appointmentDatePicker.show();
    }

    public static String getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Dummy data for now till it get it from JSON file.
     */
    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();
        items.add("TOMORROW");
        items.add(new AppointmentAvailableHoursDTO("Thursday", "September", "22", "01:30 PM"));
        items.add(new AppointmentAvailableHoursDTO("Thursday", "September", "22", "02:30 PM"));
        items.add(new AppointmentAvailableHoursDTO("Thursday", "September", "22", "03:00 PM"));
        items.add(new AppointmentAvailableHoursDTO("Thursday", "September", "22", "04:30 PM"));
        items.add("FRIDAY, September 23rd");
        items.add(new AppointmentAvailableHoursDTO("Friday", "September", "23", "01:00 PM"));
        items.add(new AppointmentAvailableHoursDTO("Friday", "September", "23", "02:00 PM"));
        items.add(new AppointmentAvailableHoursDTO("Friday", "September", "23", "03:30 PM"));
        items.add(new AppointmentAvailableHoursDTO("Friday", "September", "23", "04:00 PM"));
        items.add("MONDAY, September 26th");
        items.add(new AppointmentAvailableHoursDTO("Monday", "September", "26", "01:30 PM"));
        items.add(new AppointmentAvailableHoursDTO("Monday", "September", "26", "02:30 PM"));
        items.add(new AppointmentAvailableHoursDTO("Monday", "September", "26", "03:00 PM"));
        return items;
    }
}