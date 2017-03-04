package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepay.patient.appointments.AppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepay.patient.appointments.dialog.CancelAppointmentDialog;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.dialog.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepay.patient.appointments.utils.PatientAppUtil;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.dtos.FaultResponseDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QueueAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
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
    private AppointmentLabelDTO appointmentLabels;

    private AppointmentNavigationCallback callback;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (AppointmentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
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
        this.appointmentLabels = appointmentInfo.getMetadata().getLabel();

        // Set Title
        showDefaultActionBar();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
                callback.newAppointment();
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

            public void onItemTapped(AppointmentDTO appointmentDTO)
            {
                showAppointmentPopup(appointmentDTO) ;
            }
        };
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {

        AppointmentsPayloadDTO payloadDTO = appointmentDTO.getPayload();
        AppointmentsActivity.model = appointmentDTO;

        String statusCode = payloadDTO.getAppointmentStatusModel().getCode();
        boolean isCheckedIn = statusCode.equalsIgnoreCase(CarePayConstants.CHECKED_IN);
        boolean isCanceled = statusCode.equalsIgnoreCase(CarePayConstants.CANCELLED);
        boolean isRequested = statusCode.equalsIgnoreCase(CarePayConstants.REQUESTED);

        // Missed Appointment
        if (payloadDTO.isAppointmentOver() && !isCheckedIn) {
            new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                    BaseDoctorInfoDialog.AppointmentType.MISSED_APPOINTMENT, getCancelAppointmentDialogListener()).show();

        } else if (isCheckedIn) {
            // Checked-In Appointment
            new QueueAppointmentDialog(getContext(), appointmentDTO, appointmentLabels).show();

        } else if (isCanceled) {
            // Cancelled Appointment
            new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                    BaseDoctorInfoDialog.AppointmentType.CANCELLED_APPOINTMENT, getCancelAppointmentDialogListener()).show();

        } else if (!payloadDTO.hasAppointmentStarted() && isAppointmentCancellable(appointmentDTO)) {
            // Appointment as long as it's 24 hours or more in the future
            new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                    BaseDoctorInfoDialog.AppointmentType.CANCEL_APPOINTMENT,
                    getCancelAppointmentDialogListener()).show();

        } else if (isRequested) {
            // Requested Appointment
            new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                    BaseDoctorInfoDialog.AppointmentType.REQUESTED_APPOINTMENT, getCancelAppointmentDialogListener()).show();

        } else {
            new CheckInOfficeNowAppointmentDialog(getContext(), appointmentDTO, appointmentInfo, getCheckInOfficeNowAppointmentDialogListener()).show();
        }

    }

    private CheckInOfficeNowAppointmentDialog.CheckInOfficeNowAppointmentDialogListener getCheckInOfficeNowAppointmentDialogListener() {
        return new CheckInOfficeNowAppointmentDialog.CheckInOfficeNowAppointmentDialogListener() {
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

    /**
     * Gets cancel appointment dialog listener.
     *
     * @return the cancel appointment dialog listener
     */
    public CancelAppointmentDialog.CancelAppointmentDialogListener getCancelAppointmentDialogListener() {
        return new CancelAppointmentDialog.CancelAppointmentDialogListener() {
            @Override
            public void onRefreshAppointmentList(AppointmentDTO appointmentDTO) {

                refreshAppointmentListAfterCancellation(appointmentDTO);
            }


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

            @Override
            public void onCancelAppointmentButtonClicked(AppointmentDTO appointmentDTO, AppointmentsResultModel appointmentInfo) {
                new CancelReasonAppointmentDialog(getContext(), appointmentDTO, appointmentInfo, getCancelReasonAppointmentDialogListener()).show();
            }

            @Override
            public void onRescheduleAppointmentClicked(AppointmentDTO appointmentDTO) {
                callback.rescheduleAppointment(appointmentDTO);
            }

        };
    }


    private void refreshAppointmentListAfterCancellation(AppointmentDTO appointmentDTO) {
        int index = appointmentsAdapter.getAppointmentItems().indexOf(appointmentDTO);
        appointmentDTO.getPayload().getAppointmentStatusModel().setCode(CarePayConstants.CANCELLED);
        appointmentsAdapter.getAppointmentItems().set(index, appointmentDTO);
        appointmentsAdapter.notifyDataSetChanged();
    }

    /**
     * Gets cancel reason appointment dialog listener.
     *
     * @return the cancel reason appointment dialog listener
     */
    public CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener getCancelReasonAppointmentDialogListener() {
        return new CancelReasonAppointmentDialog.CancelReasonAppointmentDialogListener() {
            @Override
            public void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO, int cancellationReasonID, String cancellationReasonComment) {
                onCancelAppointment(appointmentDTO, cancellationReasonID, cancellationReasonComment);
            }
        };
    }

    /**
     * call cancel appointment api.
     */
    private void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReasonID, String cancellationReasonComment) {

        Gson gson = new Gson();
        Map<String, String> queries = new HashMap<>();
        JsonObject queryStringObject = appointmentInfo.getMetadata().getTransitions().getCancel().getQueryString();
        QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

        queries.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());
        queries.put(queryStrings.getPatientId().getName(), appointmentDTO.getMetadata().getPatientId());
        queries.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());

        DataDTO data = appointmentInfo.getMetadata().getTransitions().getCancel().getData();
        JsonObject postBodyObj = new JsonObject();
        if(!StringUtil.isNullOrEmpty(cancellationReasonComment))
        {
            postBodyObj.addProperty(data.getCancellationComments().getName(),cancellationReasonComment);
        }
        if(cancellationReasonID != -1)
        {
            postBodyObj.addProperty(data.getCancellationReasonId().getName(),cancellationReasonID);
        }

        String body = postBodyObj.toString();

        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCancel();
        getWorkflowServiceHelper().execute(transitionDTO, transitionToCancelCallback, body, queries);
    }


    private WorkflowServiceCallback transitionToCancelCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientAppUtil.showSuccessToast(getContext());

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            try {
                FaultResponseDTO fault = DtoHelper.getConvertedDTO(FaultResponseDTO.class, exceptionMessage);
                if(fault.isUnprocessableEntityError())
                {
                    showErrorNotification(fault.getErrorMessageDTO().getServiceErrorDTO().getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private boolean isAppointmentCancellable(AppointmentDTO item) {
        // Get appointment date/time in required format
        String appointmentTimeStr = item.getPayload().getStartTime();
        Date appointmentTime = DateUtil.getInstance().setDateRaw(appointmentTimeStr).getDate();

        // Get current date/time in required format
        Date currentDate = DateUtil.getInstance().setToCurrent().getDate();
        String cancellationNoticePeriodStr = appointmentInfo.getPayload().getAppointmentsSettings().get(0).getCheckin().getCancellationNoticePeriod();

        if (appointmentTime != null && currentDate != null) {
            long differenceInMinutes = DateUtil.getMinutesElapsed(appointmentTime, currentDate);
            long cancellationNoticePeriod = Long.parseLong(cancellationNoticePeriodStr);

            if (differenceInMinutes > cancellationNoticePeriod) {
                return true;
            }
        }
        return false;
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