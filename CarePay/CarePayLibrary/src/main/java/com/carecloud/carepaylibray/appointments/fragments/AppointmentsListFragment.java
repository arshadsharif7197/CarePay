package com.carecloud.carepaylibray.appointments.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.appointments.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsListFragment extends Fragment {

    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();

    private AppointmentsResultModel appointmentsResultModel;
    private ProgressBar appointmentProgressBar;
    private SwipeRefreshLayout appointmentRefresh;
    private LinearLayout noAppointmentView;

    private AppointmentsAdapter appointmentsAdapter;
    private List<Appointment> appointmentsItems;
    private List<Object> appointmentListWithHeader;
    private RecyclerView appointmentRecyclerView;
    private AppointmentsListFragment appointmentsListFragment;
    private Bundle bundle;

    private CustomPopupNotification popup;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This function will check today's appointment
     * and notify if its within 2 hours.
     */
    @SuppressLint("StringFormatMatches")
    private void checkUpcomingAppointmentForReminder() {
        if (appointmentsItems != null && !appointmentsItems.isEmpty()
                && !appointmentsItems.get(0).getPayload().getId().equalsIgnoreCase(
                        ApplicationPreferences.Instance.readStringFromSharedPref(
                                CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID))) {

            try {
                // Get appointment date/time in required format
                String appointmentTimeStr = appointmentsItems.get(0).getPayload().getStartTime();
                Date appointmentTime = DateUtil.parseStringToDate(appointmentTimeStr);

                // Get current date/time in required format
                String currentTime = DateUtil.parseDateToString(new Date());
                Date currentDate = DateUtil.parseStringToDate(currentTime);

                if (appointmentTime != null && currentDate != null) {
                    long differenceInMilli = appointmentTime.getTime() - currentDate.getTime();
                    long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);

                    if (differenceInMinutes <= CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES
                            && differenceInMinutes > 0) {

                        String appointmentInDuration;
                        if (differenceInMinutes == CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES) {
                            appointmentInDuration = "2 hours";
                        } else if (differenceInMinutes == 60) {
                            appointmentInDuration = "1 hour";
                        } else if (differenceInMinutes > 60) {
                            appointmentInDuration = " hour and " + (differenceInMinutes - 60) + " minutes";
                        } else {
                            appointmentInDuration = differenceInMinutes + " minutes";
                        }

                        String doctorName = appointmentsItems.get(0).getPayload().getProvider().getName();
                        popup = new CustomPopupNotification(getActivity(), getView(),
                                getString(R.string.checkin_early), getString(R.string.dismiss),
                                getString(R.string.apt_popup_message_text, doctorName, appointmentInDuration),
                                positiveActionListener, negativeActionListener);
                        popup.showPopWindow();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    private View.OnClickListener negativeActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(
                    CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getPayload().getId());
        }
    };

    private View.OnClickListener positiveActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(
                    CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getPayload().getId());

            //TODO: Go for next flow
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View appointmentsListView = inflater.inflate(R.layout.fragment_appointments_list, container, false);
        appointmentRecyclerView = (RecyclerView) appointmentsListView.findViewById(R.id.appointments_recycler_view);
        appointmentsListFragment = this;

        //Pull down to refresh
        appointmentRefresh = (SwipeRefreshLayout) appointmentsListView.findViewById(R.id.swipeRefreshLayout);
        onRefresh();

        appointmentProgressBar = (ProgressBar) appointmentsListView.findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        bundle = getArguments();

        noAppointmentView = (LinearLayout) appointmentsListView.findViewById(R.id.no_appointment_layout);

        FloatingActionButton floatingActionButton = (FloatingActionButton) appointmentsListView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getActivity(), AddAppointmentActivity.class);
                startActivity(appointmentIntent);
            }
        });

        //Fetch appointment data
        getAppointmentInformation();

        return appointmentsListView;
    }

    private void getAppointmentInformation() {
        appointmentProgressBar.setVisibility(View.VISIBLE);

        AppointmentService aptService = (new BaseServiceGenerator(getActivity())).createService(AppointmentService.class);
        Call<AppointmentsResultModel> call = aptService.fetchAppointmentInformation();
        call.enqueue(new Callback<AppointmentsResultModel>() {

            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                appointmentsResultModel = response.body();
                appointmentProgressBar.setVisibility(View.GONE);

                if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                        && appointmentsResultModel.getPayload().getAppointments() != null
                        && appointmentsResultModel.getPayload().getAppointments().size() > 0) {

                    noAppointmentView.setVisibility(View.GONE);
                    appointmentRefresh.setVisibility(View.VISIBLE);

                    appointmentsItems = appointmentsResultModel.getPayload().getAppointments();

                    // Sort appointment list as per Today and Upcoming
                    appointmentListWithHeader = getAppointmentListWithHeader();
                    if (appointmentListWithHeader != null && appointmentListWithHeader.size() > 0) {
                        if (bundle != null) {
                            Appointment appointmentModel = (Appointment)
                                    bundle.getSerializable(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);

                            if (appointmentModel != null) {
                                // adding checked-in appointment at the top of the list
                                appointmentListWithHeader.add(0, appointmentModel);
                            }
                        }

                        appointmentsAdapter = new AppointmentsAdapter(getActivity(),
                                appointmentListWithHeader, appointmentsListFragment);
                        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        appointmentRecyclerView.setAdapter(appointmentsAdapter);
                    } else {
                        showNoAppointmentScreen();
                    }
                } else {
                    // Show no appointment screen
                    showNoAppointmentScreen();
                }

                checkUpcomingAppointmentForReminder();
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable t) {

            }
        });
    }

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentRefresh.setVisibility(View.GONE);
    }

    private void onRefresh() {
        appointmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                appointmentsItems.clear();
                appointmentRefresh.setRefreshing(false);

                if (appointmentsResultModel != (new AppointmentsResultModel())) {
                    AppointmentSectionHeaderModel appointmentSectionHeaderModel
                            = new AppointmentSectionHeaderModel();
                    appointmentListWithHeader.remove(appointmentSectionHeaderModel);
//                    appointmentListWithHeader.remove(appointmentModel);
                    appointmentListWithHeader.clear();
                }

                // API call to fetch latest appointments
                getAppointmentInformation();
            }
        });
    }

    // Method to return appointmentListWithHeader
    private List<Object> getAppointmentListWithHeader() {
        if (appointmentsItems != null && appointmentsItems.size() > 0) {
            // To sort appointment list based on appointment time
            Collections.sort(appointmentsItems, new Comparator<Appointment>() {
                public int compare(Appointment o1, Appointment o2) {
                    String dateO1 = o1.getPayload().getStartTime();
                    String dateO2 = o2.getPayload().getStartTime();

                    Date date1 = DateUtil.parseStringToDate(dateO1);
                    Date date2 = DateUtil.parseStringToDate(dateO2);

                    long time1 = 0, time2 = 0;
                    if (date1 != null) {
                        time1 = date1.getTime();
                    }

                    if (date2 != null) {
                        time2 = date2.getTime();
                    }

                    if (time1 < time2) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            // To sort appointment list based on today or tomorrow
            Collections.sort(appointmentsItems, new Comparator<Appointment>() {
                public int compare(Appointment o1, Appointment o2) {
                    return o2.getPayload().getStartTime().compareTo(o1.getPayload().getStartTime());
                }
            });

            // To create appointment list data structure along with headers
            String previousDay = "";
            appointmentListWithHeader = new ArrayList<>();

            for (Appointment appointmentModel : appointmentsItems) {
                if (previousDay.equalsIgnoreCase(appointmentModel.getPayload().getStartTime())) {
                    appointmentListWithHeader.add(appointmentModel);
                } else {
                    // Current date
                    String currentDate = DateUtil.parseDateToString(CarePayConstants.DATE_FORMAT, new Date());
                    Date currentConvertedDate = DateUtil.parseDateToString(CarePayConstants.DATE_FORMAT, currentDate);

                    // Appointment start date
                    String appointmentTime = appointmentModel.getPayload().getStartTime();
                    Date appointmentDate = DateUtil.parseStringToDate(appointmentTime);

                    String appointmentDateWithoutTime = DateUtil.parseDateToString(
                            CarePayConstants.DATE_FORMAT, appointmentDate);
                    Date convertedAppointmentDate = DateUtil.parseDateToString(
                            CarePayConstants.DATE_FORMAT, appointmentDateWithoutTime);

                    if (convertedAppointmentDate.after(currentConvertedDate) &&
                                !appointmentDateWithoutTime.equalsIgnoreCase(currentDate)) {
                        previousDay = CarePayConstants.DAY_UPCOMING;
                    } else if (convertedAppointmentDate.before(currentConvertedDate)) {
                        // Do nothing
                        continue;
                    } else {
                        previousDay = CarePayConstants.DAY_TODAY;
                    }

                    // If appointment is checked-in, don't add header
                    if (appointmentModel.getPayload().getAppointmentStatusModel().getId() != 2) {
                        AppointmentSectionHeaderModel appointmentSectionHeaderModel
                                = new AppointmentSectionHeaderModel();
                        appointmentSectionHeaderModel.setAppointmentHeader(previousDay);
                        appointmentListWithHeader.add(appointmentSectionHeaderModel);
                        appointmentListWithHeader.add(appointmentModel);
                    } else {
                        appointmentListWithHeader.add(0, appointmentModel);
                    }
                }
            }
        }
        return appointmentListWithHeader;
    }
}