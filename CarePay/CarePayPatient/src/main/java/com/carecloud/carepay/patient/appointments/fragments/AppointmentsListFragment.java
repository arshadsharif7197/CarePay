package com.carecloud.carepay.patient.appointments.fragments;

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

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepay.patient.appointments.utils.CustomPopupNotification;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppointmentsListFragment extends Fragment {

    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();

    private AppointmentsResultModel appointmentInfo;
    private ProgressBar appointmentProgressBar;
    private SwipeRefreshLayout appointmentRefresh;
    private LinearLayout appointmentView;
    private LinearLayout noAppointmentView;
    private View appointmentsListView;

    private AppointmentsAdapter appointmentsAdapter;
    private List<AppointmentDTO> appointmentsItems;
    private List<Object> appointmentListWithHeader;
    private RecyclerView appointmentRecyclerView;
    private AppointmentsListFragment appointmentsListFragment;
    private Bundle bundle;

    private CustomPopupNotification popup;
    private View.OnClickListener negativeActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(
                    CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getPayload().getId());
        }
    };
    private View.OnClickListener positiveActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(
                    CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getPayload().getId());

            onCheckInNow(appointmentsItems.get(0));
        }
    };

    /**
     * call check-in Now api.
     */
    private void onCheckInNow(AppointmentDTO appointmentDTO) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckin();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToDemographicsVerifyCallback, queries, header);
    }

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };

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
    @SuppressLint({"StringFormatMatches", "StringFormatInvalid"})
    private void checkUpcomingAppointmentForReminder() {
        if (appointmentsItems != null && !appointmentsItems.isEmpty()
                && !appointmentsItems.get(0).getPayload().getId().equalsIgnoreCase(
                ApplicationPreferences.Instance.readStringFromSharedPref(
                        CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID))) {

            try {
                // Get appointment date/time in required format
                String appointmentTimeStr = appointmentsItems.get(0).getPayload().getStartTime();
                Date appointmentTime = DateUtil.getInstance().setDateRaw(appointmentTimeStr).getDate();

                // Get current date/time in required format
                String currentTime = DateUtil.getDateRaw(DateUtil.getInstance().setToCurrent().getDate());
                Date currentDate = DateUtil.getInstance().setDateRaw(currentTime).getDate();

                if (appointmentTime != null && currentDate != null) {
                    long differenceInMilli = appointmentTime.getTime() - currentDate.getTime();
                    long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);

                    if (differenceInMinutes <= CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES
                            && differenceInMinutes > 0 && appointmentInfo != null) {
                        AppointmentLabelDTO labels = appointmentInfo.getMetadata().getLabel();
                        String doctorName = appointmentsItems.get(0).getPayload().getProvider().getName();
                        String aptTime = DateUtil.getInstance().setDateRaw(appointmentTimeStr).getTime12Hour();
                        String popupNotificationMsg;
                        if (differenceInMinutes == CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES) {
                            popupNotificationMsg = String.format(labels.getAppointmentsCheckInEarlyPrompt(),
                                    aptTime, doctorName, "2" + " " + labels.getAppointmentPopupNotificationHours());
                        } else if (differenceInMinutes == 60) {
                            popupNotificationMsg = String.format(labels.getAppointmentsCheckInEarlyPrompt(),
                                    aptTime, doctorName, "1" + " " + labels.getAppointmentPopupNotificationHour());
                        } else if (differenceInMinutes > 60) {
                            popupNotificationMsg = String.format(labels.getAppointmentsCheckInEarlyPrompt(),
                                    aptTime, doctorName, "1"+" "+labels.getAppointmentPopupNotificationHour()
                                    +" "+labels.getAppointmentPopupNotificationAnd()+" "+(differenceInMinutes - 60)
                                    +" "+labels.getAppointmentPopupNotificationMinutes());
                        } else {
                            popupNotificationMsg = String.format(labels.getAppointmentsCheckInEarlyPrompt(),
                                    aptTime, doctorName, differenceInMinutes + " "
                                    + labels.getAppointmentPopupNotificationMinutes());
                        }

                        popup = new CustomPopupNotification(getActivity(), getView(),
                                labels.getAppointmentsCheckInEarly(), labels.getDismissMessage(),
                                popupNotificationMsg,
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appointmentsListView = inflater.inflate(R.layout.fragment_appointments_list, container, false);
        appointmentRecyclerView = (RecyclerView) appointmentsListView.findViewById(R.id.appointments_recycler_view);
        appointmentsListFragment = this;

        Gson gson = new Gson();
        bundle = getArguments();
        String appointmentInfoString = bundle.getString(CarePayConstants.APPOINTMENT_INFO_BUNDLE);
        appointmentInfo = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);

        //Fetch appointment data
        loadAppointmentList();

        return appointmentsListView;
    }

    private void init() {
        String noAptPlaceholder = "";
        String noAptMessageTitle = "";
        String noAptMessageText = "";

        if (appointmentInfo != null) {
            AppointmentLabelDTO labels = appointmentInfo.getMetadata().getLabel();
            noAptPlaceholder = labels.getNoAppointmentsPlaceholderLabel();
            noAptMessageTitle = labels.getNoAppointmentsMessageTitle();
            noAptMessageText = labels.getNoAppointmentsMessageText();
        }

        //Pull down to refresh
        appointmentRefresh = (SwipeRefreshLayout) appointmentsListView.findViewById(R.id.swipeRefreshLayout);
        onRefresh();

        appointmentProgressBar = (ProgressBar) appointmentsListView.findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);

        appointmentView = (LinearLayout) appointmentsListView.findViewById(R.id.appointment_section_linear_layout);
        noAppointmentView = (LinearLayout) appointmentsListView.findViewById(R.id.no_appointment_layout);
        ((CarePayTextView) appointmentsListView.findViewById(R.id.no_apt_placeholder_icon)).setText(noAptPlaceholder);
        ((CarePayTextView) appointmentsListView.findViewById(R.id.no_apt_message_title)).setText(noAptMessageTitle);
        ((CarePayTextView) appointmentsListView.findViewById(R.id.no_apt_message_desc)).setText(noAptMessageText);

        FloatingActionButton floatingActionButton = (FloatingActionButton) appointmentsListView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getActivity(), AddAppointmentActivity.class);
                startActivity(appointmentIntent);
            }
        });
    }

    private void loadAppointmentList() {

        // Initialize views
        init();

        appointmentProgressBar.setVisibility(View.GONE);

        if (appointmentInfo != null && appointmentInfo.getPayload() != null
                && appointmentInfo.getPayload().getAppointments() != null
                && appointmentInfo.getPayload().getAppointments().size() > 0) {

            noAppointmentView.setVisibility(View.GONE);
            appointmentView.setVisibility(View.VISIBLE);

            appointmentsItems = appointmentInfo.getPayload().getAppointments();

            // Sort appointment list as per Today and Upcoming
            appointmentListWithHeader = getAppointmentListWithHeader();
            if (appointmentListWithHeader != null && appointmentListWithHeader.size() > 0) {

                if (bundle != null) {
                    Gson gson = new Gson();
                    String appointmentDTOString = bundle.getString(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);
                    AppointmentsResultModel appointmentDTO = gson.fromJson(appointmentDTOString,
                            AppointmentsResultModel.class);

                    if (appointmentDTO != null) {
                        // adding checked-in appointment at the top of the list
                        appointmentListWithHeader.add(0, appointmentDTO);
                    }
                }

                appointmentsAdapter = new AppointmentsAdapter(getActivity(),
                        appointmentListWithHeader, appointmentsListFragment, appointmentInfo);
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

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentView.setVisibility(View.GONE);
    }

    private void onRefresh() {
        appointmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (appointmentsItems != null) {
                    appointmentsItems.clear();
                }
                appointmentRefresh.setRefreshing(false);

                if (appointmentInfo != (new AppointmentsResultModel())) {
                    AppointmentSectionHeaderModel appointmentSectionHeaderModel
                            = new AppointmentSectionHeaderModel();

                    if (appointmentListWithHeader != null) {
                        appointmentListWithHeader.remove(appointmentSectionHeaderModel);
                        appointmentListWithHeader.clear();
                    }

                    if (appointmentsAdapter != null) {
                        appointmentsAdapter.hideHeaderView();
                    }
                }

                // API call to fetch latest appointments
                TransitionDTO transitionDTO = appointmentInfo.getMetadata().getLinks().getAppointments();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, pageRefreshCallback);
            }
        });
    }

    WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            appointmentProgressBar.setVisibility(View.GONE);
            if (appointmentInfo != null) {
                Gson gson = new Gson();
                appointmentInfo = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                loadAppointmentList();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            appointmentProgressBar.setVisibility(View.GONE);
        }
    };

    // Method to return appointmentListWithHeader
    private List<Object> getAppointmentListWithHeader() {
        if (appointmentsItems != null && appointmentsItems.size() > 0) {
            // To sort appointment list based on appointment time
            Collections.sort(appointmentsItems, new Comparator<AppointmentDTO>() {
                public int compare(AppointmentDTO o1, AppointmentDTO o2) {
                    String dateO1 = o1.getPayload().getStartTime();
                    String dateO2 = o2.getPayload().getStartTime();

                    Date date1 = DateUtil.getInstance().setDateRaw(dateO1).getDate();
                    Date date2 = DateUtil.getInstance().setDateRaw(dateO2).getDate();

                    long time1 = 0;
                    long time2 = 0;
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
            Collections.sort(appointmentsItems, new Comparator<AppointmentDTO>() {
                public int compare(AppointmentDTO o1, AppointmentDTO o2) {
                    String date01 = o1.getPayload().getStartTime();

                    String date02 = o2.getPayload().getStartTime();
                    Date date2 = DateUtil.getInstance().setDateRaw(date02).getDate();
                    DateUtil.getInstance().setDateRaw(date01);
                    return DateUtil.getInstance().compareTo(date2);
                }
            });

            // To create appointment list data structure along with headers
            String headerTitle = "";
            appointmentListWithHeader = new ArrayList<>();

            for (AppointmentDTO appointmentDTO : appointmentsItems) {
                String title = getSectionHeaderTitle(appointmentDTO.getPayload().getStartTime());
                if (headerTitle.equalsIgnoreCase(title)
                        && appointmentDTO.getPayload().getAppointmentStatusModel().getId() != 2) {
                    appointmentListWithHeader.add(appointmentDTO);
                } else {
                    headerTitle = getSectionHeaderTitle(appointmentDTO.getPayload().getStartTime());

//                    if (!headerTitle.equals(CarePayConstants.DAY_OVER)) {
                    // If appointment is checked-in, don't add header
                    if (appointmentDTO.getPayload().getAppointmentStatusModel().getId() != 2) {
                        AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
                        appointmentSectionHeaderModel.setAppointmentHeader(headerTitle);
                        appointmentListWithHeader.add(appointmentSectionHeaderModel);
                        appointmentListWithHeader.add(appointmentDTO);
                    } else {
                        appointmentListWithHeader.add(0, appointmentDTO);
                    }
//                    }
                }
            }
        }
        return appointmentListWithHeader;
    }

    private String getSectionHeaderTitle(String appointmentRawDate) {
        // Current date
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDateAsMMddyyyy();
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String headerText;
        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            headerText = CarePayConstants.DAY_UPCOMING;
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = CarePayConstants.DAY_TODAY;
        } else {
            headerText = CarePayConstants.DAY_TODAY;
        }

        return headerText;
    }
}