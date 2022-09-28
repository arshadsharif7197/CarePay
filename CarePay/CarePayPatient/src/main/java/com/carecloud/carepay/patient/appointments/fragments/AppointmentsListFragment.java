package com.carecloud.carepay.patient.appointments.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentListAdapter;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentFlowInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPopUpDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customdialogs.ExitAlertDialog;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.customdialogs.LargeConfirmationAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppointmentsListFragment extends BaseFragment
        implements AppointmentListAdapter.SelectAppointmentCallback {

    private AppointmentsResultModel appointmentsResultModel;
    private SwipeRefreshLayout refreshLayout;
    private View noAppointmentView;
    private String cdrMaguirePractice;
    private RecyclerView appointmentRecyclerView;

    private AppointmentFlowInterface callback;
    private FloatingActionButton floatingActionButton;
    private boolean canScheduleAppointments;
    private AppointmentViewModel viewModel;
    private List<UserPracticeDTO> filteredList;

    public static AppointmentsListFragment newInstance() {
        Bundle args = new Bundle();
        AppointmentsListFragment fragment = new AppointmentsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = (AppointmentFlowInterface) ((AppointmentViewHandler) context)
                        .getAppointmentPresenter();
            } else {
                callback = (AppointmentFlowInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentFlowInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AppointmentViewModel.class);
        appointmentsResultModel = viewModel.getAppointmentsDtoObservable().getValue();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
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
        refreshLayout.setRefreshing(false);
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
        filteredList = filterPracticesList(appointmentsResultModel.getPayload().getUserPractices());

        if (canScheduleAppointments) {
            floatingActionButton.setOnClickListener(view1 -> checkCreateAppointmentChecks());
            newAppointmentClassicButton.setVisibility(View.VISIBLE);
            newAppointmentClassicButton.setOnClickListener(v -> checkCreateAppointmentChecks());
        } else {
            floatingActionButton.hide();
            noAppointmentMessage.setVisibility(View.GONE);
            newAppointmentClassicButton.setVisibility(View.GONE);
        }
    }

    private void checkCreateAppointmentChecks() {
        if (filteredList.size() == 1) {
            UserPracticeDTO selectedPractice = filteredList.get(0);
            AppointmentsPopUpDTO appointmentsPopUpDTO = appointmentsResultModel.getPayload().getAppointmentsSetting(selectedPractice.getPracticeId()).getAppointmentsPopUpDTO();
            if (appointmentsPopUpDTO != null && appointmentsPopUpDTO.isEnabled()) {
                LargeConfirmationAlertDialog largeAlertDialogFragment =
                        LargeConfirmationAlertDialog.newInstance(appointmentsPopUpDTO.getText(),
                                Label.getLabel("button_yes"), Label.getLabel("button_no"));
                largeAlertDialogFragment.setLargeAlertInterface(new LargeAlertDialogFragment.LargeAlertInterface() {
                    @Override
                    public void onActionButton() {
                        CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance();
                        callback.addFragment(fragment, true);
                    }
                });
                largeAlertDialogFragment.show(requireActivity().getSupportFragmentManager(), largeAlertDialogFragment.getClass().getName());
            } else {
                CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance();
                callback.addFragment(fragment, true);
            }
        } else {
            CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance();
            callback.addFragment(fragment, true);
        }
    }

    private boolean canScheduleAppointments() {
        for (UserPracticeDTO practiceDTO : appointmentsResultModel.getPayload().getUserPractices()) {
            if (appointmentsResultModel.getPayload().canScheduleAppointments(practiceDTO.getPracticeId())) {
                cdrMaguirePractice = practiceDTO.getPracticeId();
                return true;
            }
        }
        return false;
    }

    private List<UserPracticeDTO> filterPracticesList(List<UserPracticeDTO> userPractices) {
        List<UserPracticeDTO> filteredList = new ArrayList<>();
        for (UserPracticeDTO practiceDTO : userPractices) {
            if (appointmentsResultModel.getPayload().canScheduleAppointments(practiceDTO.getPracticeId())) {
                filteredList.add(practiceDTO);
            }
        }
        return filteredList;
    }

    private void loadAppointmentList() {
        if (canViewAnyAppointment(appointmentsResultModel.getPayload().getAppointments(),
                appointmentsResultModel.getPayload().getUserPractices())) {

            if (appointmentsResultModel.getPayload().getAppointments().size() > 0) {
                List<AppointmentDTO> appointmentsItems = appointmentsResultModel.getPayload().getAppointments();
                noAppointmentView.setVisibility(View.GONE);
                if (canScheduleAppointments) {
                    floatingActionButton.show();
                } else {
                    floatingActionButton.hide();
                }
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
        floatingActionButton.hide();
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
        floatingActionButton.hide();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setRefreshAction() {
        refreshLayout.setOnRefreshListener(() -> viewModel
                .getAppointments(appointmentsResultModel.getMetadata().getLinks().getAppointments(), true));
    }

    @Override
    public void onItemTapped(AppointmentDTO appointmentDTO) {
        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointmentDTO);
        callback.displayDialogFragment(detailDialog, true);
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