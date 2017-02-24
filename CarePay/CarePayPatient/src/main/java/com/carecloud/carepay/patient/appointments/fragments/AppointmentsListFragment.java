package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepay.patient.appointments.utils.PatientAppUtil;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsListFragment extends BaseFragment {

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(RequestAppointmentDialog.isAppointmentAdded){
            refreshAppointmentList();
            showAppointmentConfirmation();
            RequestAppointmentDialog.isAppointmentAdded = false;
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

        // Set Title
        ActionBar actionBar = ((AppointmentsActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(appointmentInfo.getMetadata().getLabel().getAppointmentsHeading());
        }

        //Fetch appointment data
        loadAppointmentList();

        return appointmentsListView;
    }

    private void init() {
        String noAptMessageTitle = "";
        String noAptMessageText = "";

        if (appointmentInfo != null) {
            AppointmentLabelDTO labels = appointmentInfo.getMetadata().getLabel();
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
        ((CarePayTextView) appointmentsListView.findViewById(R.id.no_apt_message_title)).setText(noAptMessageTitle);
        ((CarePayTextView) appointmentsListView.findViewById(R.id.no_apt_message_desc)).setText(noAptMessageText);

        FloatingActionButton floatingActionButton = (FloatingActionButton) appointmentsListView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getActivity(), AddAppointmentActivity.class);
                Bundle bundleArg = new Bundle();
                String appointmentInfoString = bundle.getString(CarePayConstants.APPOINTMENT_INFO_BUNDLE);
                bundleArg.putSerializable(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, appointmentInfoString);
                appointmentIntent.putExtras(bundleArg);
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

            appointmentsItems = appointmentInfo.getPayload().getAppointments();
            noAppointmentView.setVisibility(View.GONE);
            appointmentView.setVisibility(View.VISIBLE);

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
                        appointmentListWithHeader, appointmentsListFragment, appointmentInfo, getAppointmentsAdapterListener());
                appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                appointmentRecyclerView.setAdapter(appointmentsAdapter);
            } else {
                showNoAppointmentScreen();
            }
        } else {
            // Show no appointment screen
            showNoAppointmentScreen();
        }
    }

    private AppointmentsAdapter.AppointmentsAdapterListener getAppointmentsAdapterListener() {
        return new AppointmentsAdapter.AppointmentsAdapterListener() {
            @Override
            public void onPreRegisterTapped(AppointmentDTO appointmentDTO, AppointmentsResultModel appointmentInfo) {
                JsonObject queryStringObject = appointmentInfo.getMetadata().getTransitions().getCheckinAtOffice().getQueryString();
                QueryStrings queryStrings = DtoHelper.getConvertedDTO(QueryStrings.class, queryStringObject);

                Map<String, String> queries = new HashMap<>();
                queries.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
                queries.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());
                queries.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());

                Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
                header.put("transition", "true");

                TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckingIn();
                getWorkflowServiceHelper().execute(transitionDTO, preRegisterCallback, queries, header);
            }
        };
    }

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentView.setVisibility(View.GONE);
    }

    private void onRefresh() {
        appointmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAppointmentList();
            }
        });
    }

    private void refreshAppointmentList() {
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
        getWorkflowServiceHelper().execute(transitionDTO, pageRefreshCallback);
    }

    WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            appointmentProgressBar.setVisibility(View.GONE);
            if (appointmentInfo != null) {
                Gson gson = new Gson();
                appointmentInfo = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                loadAppointmentList();
//                checkUpcomingAppointments();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            appointmentProgressBar.setVisibility(View.GONE);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
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
                if (!appointmentDTO.getPayload().getAppointmentStatusModel().getCode()
                        .equalsIgnoreCase(CarePayConstants.CHECKED_IN)) {

                    String title = getSectionHeaderTitle(appointmentDTO.getPayload().getStartTime());
                    if (headerTitle.equalsIgnoreCase(title)) {
                        appointmentListWithHeader.add(appointmentDTO);
                    } else {
                        headerTitle = getSectionHeaderTitle(appointmentDTO.getPayload().getStartTime());
                        AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
                        appointmentSectionHeaderModel.setAppointmentHeader(headerTitle);
                        appointmentListWithHeader.add(appointmentSectionHeaderModel);
                        appointmentListWithHeader.add(appointmentDTO);
                    }
                } else {
                    appointmentListWithHeader.add(0, appointmentDTO);
                }
            }
        }
        return appointmentListWithHeader;
    }

    private String getSectionHeaderTitle(String appointmentRawDate) {
        // Current date
        String currentDate = DateUtil.getInstance().setToCurrent().toStringWithFormatMmDashDdDashYyyy();
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).toStringWithFormatMmDashDdDashYyyy();
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String headerText;
        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            headerText = appointmentInfo.getMetadata().getLabel().getUpcomingAppointmentsHeading();
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            headerText = appointmentInfo.getMetadata().getLabel().getTodayAppointmentsHeading();
        } else {
            headerText = appointmentInfo.getMetadata().getLabel().getTodayAppointmentsHeading();
        }
        return headerText;
    }

    private void showAppointmentConfirmation() {
        String appointmentRequestSuccessMessage = "";

        if (appointmentInfo != null) {
            appointmentRequestSuccessMessage = appointmentInfo.getMetadata().getLabel()
                    .getAppointmentRequestSuccessMessage();
        }

        PatientAppUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage );

    }

    private WorkflowServiceCallback preRegisterCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.getInstance(getContext()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}