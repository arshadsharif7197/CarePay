package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentListAdapter;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppointmentsListFragment extends BaseAppointmentFragment
        implements AppointmentListAdapter.SelectAppointmentCallback {

    private AppointmentsResultModel appointmentsResultModel;
    private SwipeRefreshLayout refreshLayout;
    private View noAppointmentView;

    private List<AppointmentDTO> appointmentsItems;
    private RecyclerView appointmentRecyclerView;

    private PatientAppointmentNavigationCallback callback;
    private FloatingActionButton floatingActionButton;
    private boolean canScheduleAppointments;

    public static AppointmentsListFragment newInstance(AppointmentsResultModel appointmentsResultModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        AppointmentsListFragment fragment = new AppointmentsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = (PatientAppointmentNavigationCallback) ((AppointmentViewHandler) context)
                        .getAppointmentPresenter();
            } else {
                callback = (PatientAppointmentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        appointmentRecyclerView = view.findViewById(R.id.appointments_recycler_view);
        appointmentRecyclerView.setLayoutManager(layoutManager);
        // Set Title
        showDefaultActionBar();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Label.getLabel("appointments_heading"));
        }
        setUpViews(view);
        loadAppointmentList();
    }

    private void setUpViews(View view) {
        String noAptMessageTitle = Label.getLabel("no_appointments_message_title");
        String noAptMessageText = Label.getLabel("no_appointments_message_text");

        //Pull down to refresh
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        setRefreshAction();

        noAppointmentView = view.findViewById(R.id.no_appointment_layout);
        ((TextView) view.findViewById(R.id.no_apt_message_title)).setText(noAptMessageTitle);
        TextView noAppointmentMessage = view.findViewById(R.id.no_apt_message_desc);
        noAppointmentMessage.setText(noAptMessageText);

        Button newAppointmentClassicButton = view.findViewById(R.id.newAppointmentClassicButton);
        floatingActionButton = view.findViewById(R.id.fab);
        canScheduleAppointments = canScheduleAppointments();
        if (canScheduleAppointments) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.newAppointment();
                }
            });
            newAppointmentClassicButton.setVisibility(View.VISIBLE);
            newAppointmentClassicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.newAppointment();
                }
            });
        } else {
            floatingActionButton.setVisibility(View.GONE);
            noAppointmentMessage.setVisibility(View.GONE);
            newAppointmentClassicButton.setVisibility(View.GONE);
        }
    }

    private boolean canScheduleAppointments() {
        for (UserPracticeDTO practiceDTO : appointmentsResultModel.getPayload().getUserPractices()) {
            if (appointmentsResultModel.getPayload().canScheduleAppointments(practiceDTO.getPracticeId())) {
                return true;
            }
        }
        return false;
    }

    private void loadAppointmentList() {
        if (canViewAnyAppointment(appointmentsResultModel.getPayload().getAppointments(),
                appointmentsResultModel.getPayload().getUserPractices())) {
            if (appointmentsResultModel.getPayload().getAppointments().size() > 0) {
                appointmentsItems = appointmentsResultModel.getPayload().getAppointments();
                noAppointmentView.setVisibility(View.GONE);
                floatingActionButton.setVisibility(canScheduleAppointments ? View.VISIBLE : View.GONE);
                appointmentRecyclerView.setVisibility(View.VISIBLE);
                setAdapter(appointmentsItems);
            } else {
                showNoAppointmentScreen();
            }
        } else {
            showNoPermissionScreen();
        }
    }

    private void showNoPermissionScreen() {
        appointmentRecyclerView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
        noAppointmentView.setVisibility(View.VISIBLE);
        ((TextView) noAppointmentView.findViewById(R.id.no_apt_message_title))
                .setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
        noAppointmentView.findViewById(R.id.no_apt_message_desc).setVisibility(View.GONE);
    }

    private boolean canViewAnyAppointment(@NonNull List<AppointmentDTO> appointments,
                                          List<UserPracticeDTO> userPractices) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practice : userPractices) {
            if (appointmentsResultModel.getPayload().canViewAppointments(practice.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                appointments = filterAppointments(appointments, practice.getPracticeId());
            }
        }
        appointmentsResultModel.getPayload().setAppointments(appointments);
        return atLeastOneHasPermission || userPractices.isEmpty();
    }

    private List<AppointmentDTO> filterAppointments(List<AppointmentDTO> appointments, String practiceId) {
        List<AppointmentDTO> filteredList = new ArrayList<>();
        for (AppointmentDTO appointmentDTO : appointments) {
            if (!appointmentDTO.getMetadata().getPracticeId().equals(practiceId)) {
                filteredList.add(appointmentDTO);
            }
        }
        return filteredList;
    }

    private void setAdapter(List<AppointmentDTO> appointmentsItems) {
        if (appointmentRecyclerView.getAdapter() == null) {
            Map<String, Set<String>> enabledPracticeLocations = new HashMap<>();
            for (AppointmentDTO appointmentDTO : appointmentsItems) {
                String practiceId = appointmentDTO.getMetadata().getPracticeId();
                if (!enabledPracticeLocations.containsKey(practiceId)) {
                    enabledPracticeLocations.put(practiceId,
                            ApplicationPreferences.getInstance().getPracticesWithBreezeEnabled(practiceId));
                }
            }

            AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), appointmentsItems,
                    this, appointmentsResultModel.getPayload().getUserPractices(), enabledPracticeLocations);
            appointmentRecyclerView.setAdapter(adapter);
        } else {
            AppointmentListAdapter adapter = (AppointmentListAdapter) appointmentRecyclerView.getAdapter();
            adapter.setAppointmentItems(appointmentsItems);
        }
    }

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentRecyclerView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {
        callback.displayAppointmentDetails(appointmentDTO);
    }

    private void setRefreshAction() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                doRefreshAction();
            }
        });
    }

    private void doRefreshAction() {
        // API call to fetch latest appointments
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getAppointments();
        getWorkflowServiceHelper().execute(transitionDTO, pageRefreshCallback);
    }

    private WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if (appointmentsResultModel != null) {
                Gson gson = new Gson();
                appointmentsResultModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                loadAppointmentList();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    public void onItemTapped(AppointmentDTO appointmentDTO) {
        showAppointmentPopup(appointmentDTO);
    }

    @Override
    public void onCheckoutTapped(AppointmentDTO appointmentDTO) {
        callback.onCheckOutStarted(appointmentDTO);
    }

    @Override
    public boolean canCheckOut(AppointmentDTO appointmentDTO) {
        return appointmentsResultModel.getPayload()
                .canCheckInCheckOut(appointmentDTO.getMetadata().getPracticeId());
    }
}