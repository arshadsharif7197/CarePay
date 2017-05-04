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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.adapters.AppointmentListAdapter;
import com.carecloud.carepay.patient.appointments.dialog.CancelAppointmentDialog;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.dialog.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.appointment.DataDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog.AppointmentType;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.customdialogs.QueueAppointmentDialog;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsListFragment extends BaseFragment implements AppointmentListAdapter.SelectAppointmentCallback {

    private AppointmentsResultModel appointmentInfo;
    private ProgressBar appointmentProgressBar;
    private SwipeRefreshLayout refreshLayout;
    private View appointmentView;
    private View noAppointmentView;
    private View appointmentsListView;

    private List<AppointmentDTO> appointmentsItems;
    private RecyclerView appointmentRecyclerView;
    private AppointmentLabelDTO appointmentLabels;

    private AppointmentNavigationCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (AppointmentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        Bundle bundle = getArguments();
        String appointmentInfoString = bundle.getString(CarePayConstants.APPOINTMENT_INFO_BUNDLE);
        appointmentInfo = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);
        this.appointmentLabels = appointmentInfo.getMetadata().getLabel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appointmentsListView = inflater.inflate(R.layout.fragment_appointments_list, container, false);
        return appointmentsListView;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        appointmentRecyclerView = (RecyclerView) appointmentsListView.findViewById(R.id.appointments_recycler_view);
        appointmentRecyclerView.setLayoutManager(layoutManager);

        // Set Title
        showDefaultActionBar();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(appointmentInfo.getMetadata().getLabel().getAppointmentsHeading());
        }

        //Fetch appointment data
        loadAppointmentList();
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
        refreshLayout = (SwipeRefreshLayout) appointmentsListView.findViewById(R.id.swipeRefreshLayout);
        setRefreshAction();

        appointmentProgressBar = (ProgressBar) appointmentsListView.findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);

        appointmentView = appointmentsListView.findViewById(R.id.appointment_section_linear_layout);
        noAppointmentView = appointmentsListView.findViewById(R.id.no_appointment_layout);
        ((TextView) appointmentsListView.findViewById(R.id.no_apt_message_title)).setText(noAptMessageTitle);
        ((TextView) appointmentsListView.findViewById(R.id.no_apt_message_desc)).setText(noAptMessageText);

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

        if (appointmentInfo!=null && appointmentInfo.getPayload().getAppointments().size() > 0) {

            appointmentsItems = appointmentInfo.getPayload().getAppointments();
            noAppointmentView.setVisibility(View.GONE);
            appointmentView.setVisibility(View.VISIBLE);

            setAdapter();

        } else {
            // Show no appointment screen
            showNoAppointmentScreen();
        }
    }

    private void setAdapter(){
        if(appointmentRecyclerView.getAdapter() == null){
            AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), appointmentsItems, this);
            appointmentRecyclerView.setAdapter(adapter);
        }else{
            AppointmentListAdapter adapter = (AppointmentListAdapter) appointmentRecyclerView.getAdapter();
            adapter.setAppointmentItems(appointmentsItems);
        }
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {
        AppointmentsPayloadDTO payloadDTO = appointmentDTO.getPayload();
        String statusCode = payloadDTO.getAppointmentStatus().getCode();
        switch (statusCode) {
            case CarePayConstants.IN_PROGRESS_IN_ROOM:
            case CarePayConstants.IN_PROGRESS_OUT_ROOM:
            case CarePayConstants.CHECKED_IN:
                new QueueAppointmentDialog(getContext(), appointmentDTO, appointmentLabels).show();
                break;
            case CarePayConstants.CANCELLED:
                new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                        AppointmentType.CANCELLED, getCancelAppointmentDialogListener()).show();
                break;
            case CarePayConstants.REQUESTED:
                new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                        AppointmentType.REQUESTED, getCancelAppointmentDialogListener()).show();
                break;
            default:
                // Missed Appointment
                if (payloadDTO.isAppointmentOver()) {
                    new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                            AppointmentType.MISSED, getCancelAppointmentDialogListener()).show();
                } else if (payloadDTO.canCheckInNow(appointmentInfo)) {
                    new CheckInOfficeNowAppointmentDialog(getContext(), appointmentDTO, appointmentInfo, getCheckInOfficeNowAppointmentDialogListener()).show();
                } else if (payloadDTO.isAppointmentCancellable(appointmentInfo)) {
                    new CancelAppointmentDialog(getContext(), appointmentDTO, appointmentInfo,
                            AppointmentType.CANCEL,
                            getCancelAppointmentDialogListener()).show();
                } else {
                    new CheckInOfficeNowAppointmentDialog(getContext(), appointmentDTO, appointmentInfo, getCheckInOfficeNowAppointmentDialogListener()).show();
                }
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

            @Override
            public void onCheckInAtOfficeButtonClicked(AppointmentDTO appointmentDTO) {
                new QrCodeViewDialog(getContext(), appointmentDTO, appointmentInfo.getMetadata(), qrCodeViewDialogListener()).show();
            }

            @Override
            public void onDemographicsVerifyCallbackError(String errorMessage) {
                showErrorNotification(null);
            }


        };
    }

    /**
     * Qr code view dialog listener qr code view dialog . qr code view dialog listener.
     *
     * @return the qr code view dialog . qr code view dialog listener
     */
    public QrCodeViewDialog.QRCodeViewDialogListener qrCodeViewDialogListener() {
        return new QrCodeViewDialog.QRCodeViewDialogListener() {

            @Override
            public void onGenerateQRCodeError(String errorMessage) {

                showErrorNotification(null); //Shows Default connection issue when passed null.
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


    private void refreshAppointmentListAfterCancellation(AppointmentDTO cancelAppointmentDTO) {
        for(AppointmentDTO appointmentDTO : appointmentsItems){
            if(appointmentDTO == cancelAppointmentDTO){
                appointmentDTO.getPayload().getAppointmentStatus().setCode(CarePayConstants.CANCELLED);
                break;
            }
        }
        setAdapter();

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
        if (!StringUtil.isNullOrEmpty(cancellationReasonComment)) {
            postBodyObj.addProperty(data.getCancellationComments().getName(), cancellationReasonComment);
        }
        if (cancellationReasonID != -1) {
            postBodyObj.addProperty(data.getCancellationReasonId().getName(), cancellationReasonID);
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
            SystemUtil.showSuccessToast(getContext(), appointmentLabels.getAppointmentCancellationSuccessMessage());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentView.setVisibility(View.GONE);
    }

    private void setRefreshAction() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefreshAction();
            }
        });
    }

    /**
     * Reload appointment list
     */
    public void refreshAppointmentList() {
        refreshLayout.setRefreshing(true);
        doRefreshAction();
    }

    private void doRefreshAction(){
        if (appointmentsItems != null) {
            appointmentsItems.clear();
        }

        // API call to fetch latest appointments
        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getLinks().getAppointments();
        getWorkflowServiceHelper().execute(transitionDTO, pageRefreshCallback);
    }

    WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            refreshLayout.setRefreshing(false);
            appointmentProgressBar.setVisibility(View.GONE);
            if (appointmentInfo != null) {
                Gson gson = new Gson();
                appointmentInfo = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                loadAppointmentList();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            refreshLayout.setRefreshing(false);
            appointmentProgressBar.setVisibility(View.GONE);
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback preRegisterCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(null);
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onItemTapped(AppointmentDTO appointmentDTO) {
        showAppointmentPopup(appointmentDTO);
    }
}