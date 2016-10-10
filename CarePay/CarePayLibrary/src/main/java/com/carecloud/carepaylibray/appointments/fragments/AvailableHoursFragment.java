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
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailableHoursModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AvailableHoursFragment extends Fragment {

    private AppointmentModel model;
    private static String appointmentDate;
    private Date todayDate, startDate, endDate;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            model = (AppointmentModel) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE);
            todayDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE);
            startDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE);
            endDate = (Date) bundle.getSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE);
        }
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                /*Launch previous fragment*/
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

        CustomProxyNovaSemiBoldLabel  dateRangeCustomTextView = (CustomProxyNovaSemiBoldLabel) availableHoursListView.findViewById(R.id.date_range_custom_text_view);
        dateRangeCustomTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.glitter));

        /*todo : once date format is final then will move to dateUtil*/
        if(todayDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            String formattedDate = simpleDateFormat.format(todayDate);
            dateRangeCustomTextView.setText(formattedDate);
        } else if (startDate != null && endDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            Calendar nextDay = Calendar.getInstance();
            nextDay.add(Calendar.DAY_OF_MONTH, 1);

            String formattedDate = simpleDateFormat.format(startDate) + " to " + simpleDateFormat.format(endDate);
            dateRangeCustomTextView.setText(formattedDate);
        } else {
            /*To show by default one week as range from tomorrow*/
            Calendar rangeStart = Calendar.getInstance();
            rangeStart.add(Calendar.DAY_OF_MONTH, 1);
            Calendar rangeEnd = Calendar.getInstance();
            rangeEnd.add(Calendar.DAY_OF_MONTH, 7);

            startDate = rangeStart.getTime();
            endDate = rangeEnd.getTime();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            String formattedDate = "From tomorrow to " + simpleDateFormat.format(endDate);
            dateRangeCustomTextView.setText(formattedDate);
        }

        Button editRangeButton = (Button) availableHoursListView.findViewById(R.id.add_appointment_date_pick);
        editRangeButton.setOnClickListener(dateRangeClickListener);

        RecyclerView availableHoursRecycleView = (RecyclerView) availableHoursListView.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);
        availableHoursRecycleView.setAdapter(new AvailableHoursAdapter(getActivity(), getSampleArrayList(), model));

        Button editDateRangeButton = (Button) availableHoursListView.findViewById(R.id.edit_date_range_button);
        editDateRangeButton.setOnClickListener(dateRangeClickListener);

        return availableHoursListView;
    }

    /*
   *   Click listener for edit range and edit date range button
   */
    View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            AppointmentDateRangeFragment appointmentDateRangeFragment = (AppointmentDateRangeFragment)
                    fragmentManager.findFragmentByTag(AppointmentDateRangeFragment.class.getSimpleName());

            if (appointmentDateRangeFragment == null) {
                appointmentDateRangeFragment = new AppointmentDateRangeFragment();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE, model);
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_DATE_BUNDLE, todayDate);
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
            bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
            appointmentDateRangeFragment.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, appointmentDateRangeFragment,
                    AvailableHoursFragment.class.getSimpleName()).commit();
        }
    };

    public static String getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Dummy data for now till it get it from JSON file
     * todo : Dummy data will be removed once returned by API
     */
    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();
        items.add("TOMORROW");
        items.add(new AppointmentAvailableHoursModel("Thursday", "September", "22", "01:30 PM"));
        items.add(new AppointmentAvailableHoursModel("Thursday", "September", "22", "02:30 PM"));
        items.add(new AppointmentAvailableHoursModel("Thursday", "September", "22", "03:00 PM"));
        items.add(new AppointmentAvailableHoursModel("Thursday", "September", "22", "04:30 PM"));
        items.add("FRIDAY, September 23rd");
        items.add(new AppointmentAvailableHoursModel("Friday", "September", "23", "01:00 PM"));
        items.add(new AppointmentAvailableHoursModel("Friday", "September", "23", "02:00 PM"));
        items.add(new AppointmentAvailableHoursModel("Friday", "September", "23", "03:30 PM"));
        items.add(new AppointmentAvailableHoursModel("Friday", "September", "23", "04:00 PM"));
        items.add("MONDAY, September 26th");
        items.add(new AppointmentAvailableHoursModel("Monday", "September", "26", "01:30 PM"));
        items.add(new AppointmentAvailableHoursModel("Monday", "September", "26", "02:30 PM"));
        items.add(new AppointmentAvailableHoursModel("Monday", "September", "26", "03:00 PM"));
        return items;
    }
}