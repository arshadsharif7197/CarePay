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
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentListAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.google.gson.Gson;

import java.util.List;

public class AppointmentsListFragment extends BaseAppointmentFragment implements AppointmentListAdapter.SelectAppointmentCallback {

    private AppointmentsResultModel appointmentsResultModel;
    private SwipeRefreshLayout refreshLayout;
    private View appointmentView;
    private View noAppointmentView;

    private List<AppointmentDTO> appointmentsItems;
    private RecyclerView appointmentRecyclerView;

    private PatientAppointmentNavigationCallback callback;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = (PatientAppointmentNavigationCallback) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PatientAppointmentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        doRefreshAction();
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
        appointmentsResultModel = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_appointments_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        appointmentRecyclerView = (RecyclerView) view.findViewById(R.id.appointments_recycler_view);
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
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        setRefreshAction();

        appointmentView = view.findViewById(R.id.appointment_section_linear_layout);
        noAppointmentView = view.findViewById(R.id.no_appointment_layout);
        ((TextView) view.findViewById(R.id.no_apt_message_title)).setText(noAptMessageTitle);
        ((TextView) view.findViewById(R.id.no_apt_message_desc)).setText(noAptMessageText);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.newAppointment();
            }
        });
        Button newAppointmentClassicButton = (Button) view.findViewById(R.id.newAppointmentClassicButton);
        newAppointmentClassicButton.setVisibility(View.VISIBLE);
        newAppointmentClassicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.newAppointment();
            }
        });
    }

    private void loadAppointmentList() {
        if (appointmentsItems != null) {
            appointmentsItems.clear();
        }
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload().getAppointments().size() > 0) {
            appointmentsItems = appointmentsResultModel.getPayload().getAppointments();
            noAppointmentView.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
            appointmentView.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            showNoAppointmentScreen();
        }
    }

    private void setAdapter() {
        if (appointmentRecyclerView.getAdapter() == null) {
            AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), appointmentsItems, this);
            appointmentRecyclerView.setAdapter(adapter);
        } else {
            AppointmentListAdapter adapter = (AppointmentListAdapter) appointmentRecyclerView.getAdapter();
            adapter.setAppointmentItems(appointmentsItems);
        }
    }

    private void showNoAppointmentScreen() {
        noAppointmentView.setVisibility(View.VISIBLE);
        appointmentView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {
        callback.displayAppointmentDetails(appointmentDTO);
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

    private void doRefreshAction() {
        // API call to fetch latest appointments
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getAppointments();
        getWorkflowServiceHelper().execute(transitionDTO, pageRefreshCallback);
    }

    WorkflowServiceCallback pageRefreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            refreshLayout.setRefreshing(false);
            if (appointmentsResultModel != null) {
                Gson gson = new Gson();
                appointmentsResultModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                loadAppointmentList();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            refreshLayout.setRefreshing(false);
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
}