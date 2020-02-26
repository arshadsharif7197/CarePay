package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentHistoricAdapter;
import com.carecloud.carepay.patient.appointments.adapters.PracticesAdapter;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentFlowInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjohnson on 19/10/18.
 */
public class AppointmentHistoryFragment extends BaseFragment
        implements AppointmentHistoricAdapter.SelectAppointmentCallback {

    private AppointmentFlowInterface callback;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentHistoricAdapter adapter;
    private RecyclerView historicAppointmentsRecyclerView;
    private static final int BOTTOM_ROW_OFFSET = 10;
    private boolean isPaging;
    private Paging paging;
    private UserPracticeDTO selectedPractice;
    private List<String> excludedAppointmentStates;
    private AppointmentViewModel viewModel;

    public static AppointmentHistoryFragment newInstance() {
        return new AppointmentHistoryFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = (AppointmentFlowInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
        } else {
            throw new ClassCastException("Activity must implement AppointmentFlowInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        viewModel = ViewModelProviders.of(getActivity()).get(AppointmentViewModel.class);
        appointmentsResultModel = viewModel.getAppointmentsDtoObservable().getValue();
        setUpViewModels();


        if (!appointmentsResultModel.getPayload().getPagingInfo().isEmpty()) {
            paging = appointmentsResultModel.getPayload().getPagingInfo().get(0).getPaging();
        } else {
            paging = new Paging();
        }

        Collections.sort(appointmentsResultModel.getPayload().getUserPractices(), (object1, object2)
                -> object1.getPracticeName().compareTo(object2.getPracticeName()));
        excludedAppointmentStates = new ArrayList<>();
        excludedAppointmentStates.add(CarePayConstants.REQUESTED);
        excludedAppointmentStates.add(CarePayConstants.CHECKING_IN);
    }

    private void setUpViewModels() {
        List<UserPracticeDTO> userPractices = appointmentsResultModel.getPayload().getUserPractices();
        viewModel.getHistoricAppointmentsObservable().observe(getActivity(), appointmentsResultModel -> {
            List<AppointmentDTO> appointments = filterAppointments(appointmentsResultModel
                    .getPayload().getAppointments());
            appointmentsResultModel.getPayload().setUserPractices(userPractices);
            isPaging = false;
            if (appointmentsResultModel.getPayload().getPagingInfo().size() > 0) {
                paging = appointmentsResultModel.getPayload().getPagingInfo().get(0).getPaging();
            }
            if (isVisible()) {
                getView().findViewById(R.id.fakeView).setVisibility(View.GONE);
            }

            hideShimmerEffect();
            showLoadingInAdapter(false);

            if (appointments.size() > 0) {
                showHistoricAppointments(appointments);
            } else if (!appointmentsResultModel.getPayload().canViewAppointments(selectedPractice.getPracticeId())) {
                //when there are no permissions to see appointments, MW sends no appointments
                showNoPermissionsLayout();
            } else {
                showNoAppointmentsLayout();
            }
        });

        viewModel.getPaginationLoaderObservable().observe(getActivity(), this::showLoadingInAdapter);
    }

    private void showLoadingInAdapter(Boolean showLoading) {
        adapter.setLoading(showLoading);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historicAppointmentsRecyclerView = view.findViewById(R.id.historicAppointmentsRecyclerView);

        if (appointmentsResultModel.getPayload().getUserPractices().isEmpty()) {
            showNoAppointmentsLayout();
        } else {
            selectedPractice = appointmentsResultModel.getPayload().getUserPractices().get(0);
            callAppointmentService(selectedPractice, true, true);
        }

        Map<String, Set<String>> enabledPracticeLocations = new HashMap<>();
        for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
            String practiceId = appointmentDTO.getMetadata().getPracticeId();
            if (!enabledPracticeLocations.containsKey(practiceId)) {
                enabledPracticeLocations.put(practiceId,
                        ApplicationPreferences.getInstance().getPracticesWithBreezeEnabled(practiceId));
            }
        }

        adapter = new AppointmentHistoricAdapter(getContext(), new ArrayList<>(),
                appointmentsResultModel.getPayload().getUserPractices(), enabledPracticeLocations, this);
        historicAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historicAppointmentsRecyclerView.setAdapter(adapter);
        historicAppointmentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > adapter.getItemCount() - BOTTOM_ROW_OFFSET && adapter.getItemCount() > 0 && !isPaging) {
                    if (hasMorePages()) {
                        callAppointmentService(selectedPractice, false, false);
                        isPaging = true;
                    }
                }
            }
        });

        if (appointmentsResultModel.getPayload().getUserPractices().size() > 1) {
            showPracticeToolbar(view);
        }

        FloatingActionButton floatingActionButton = view.findViewById(com.carecloud.carepaylibrary.R.id.fab);
        if (canScheduleAppointments()) {
            floatingActionButton.setOnClickListener(view1 -> {
                CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance();
                callback.addFragment(fragment, true);
            });
        } else {
            floatingActionButton.hide();
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

    private void showPracticeToolbar(View view) {
        RecyclerView practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        practicesRecyclerView.setVisibility(View.VISIBLE);
        practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        PracticesAdapter adapter = new PracticesAdapter(appointmentsResultModel.getPayload().getUserPractices());
        adapter.setCallback(userPracticeDTO -> {
            selectedPractice = userPracticeDTO;
            callAppointmentService(userPracticeDTO, true, true);
        });
        practicesRecyclerView.setAdapter(adapter);
    }

    private void callAppointmentService(final UserPracticeDTO userPracticeDTO,
                                        final boolean refresh,
                                        final boolean showShimmerLayout) {
        if (showShimmerLayout) {
            showShimmerEffect();
        }
        if (getView() != null) {
            getView().findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
            historicAppointmentsRecyclerView.stopScroll();
        }
        if (refresh && adapter != null) {
            adapter.clearData();
        }
        viewModel.getHistoricAppointments(userPracticeDTO, paging, refresh, showShimmerLayout);
    }

    private void hideShimmerEffect() {
        if (isAdded()) {
            getChildFragmentManager().popBackStackImmediate();
        }
    }

    private void showShimmerEffect() {
        getChildFragmentManager().beginTransaction()
                .add(R.id.shimmerLayout, ShimmerFragment.newInstance(R.layout.shimmer_default_header,
                        R.layout.shimmer_default_item))
                .addToBackStack(null)
                .commit();
    }

    private void showNoPermissionsLayout() {
        historicAppointmentsRecyclerView.setVisibility(View.GONE);
        View noAppointmentsLayout = getView().findViewById(R.id.noAppointmentsLayout);
        noAppointmentsLayout.setVisibility(View.VISIBLE);
        noAppointmentsLayout.findViewById(R.id.newAppointmentClassicButton).setVisibility(View.GONE);
        TextView no_apt_message_title = noAppointmentsLayout.findViewById(R.id.no_apt_message_title);
        no_apt_message_title.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
        noAppointmentsLayout.findViewById(R.id.no_apt_message_desc).setVisibility(View.GONE);
    }

    private void showNoAppointmentsLayout() {
        historicAppointmentsRecyclerView.setVisibility(View.GONE);
        View noAppointmentsLayout = getView().findViewById(R.id.noAppointmentsLayout);
        noAppointmentsLayout.setVisibility(View.VISIBLE);
        noAppointmentsLayout.findViewById(R.id.newAppointmentClassicButton).setVisibility(View.GONE);
        TextView no_apt_message_title = noAppointmentsLayout.findViewById(R.id.no_apt_message_title);
        no_apt_message_title.setText(Label.getLabel("appointments.list.history.noAppointments.title"));
        TextView no_apt_message_desc = noAppointmentsLayout.findViewById(R.id.no_apt_message_desc);
        no_apt_message_desc.setText(Label.getLabel("appointments.list.history.noAppointments.subtitle"));
    }

    private void showHistoricAppointments(List<AppointmentDTO> appointments) {
        if (getView() != null) {
            getView().findViewById(R.id.noAppointmentsLayout).setVisibility(View.GONE);
        }
        historicAppointmentsRecyclerView.setVisibility(View.VISIBLE);
        adapter.setData(appointments);
    }

    private List<AppointmentDTO> filterAppointments(List<AppointmentDTO> appointments) {
        List<AppointmentDTO> filteredAppointments = new ArrayList<>();
        for (AppointmentDTO appointment : appointments) {
            if (!excludedAppointmentStates.contains(appointment.getPayload().getAppointmentStatus().getCode())) {
                filteredAppointments.add(appointment);
            }
        }
        return filteredAppointments;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onItemTapped(AppointmentDTO appointmentDTO) {
        showAppointmentPopup(appointmentDTO);
    }

    @Override
    public void onCheckoutTapped(AppointmentDTO appointmentDTO) {
        callback.onCheckOutStarted(appointmentDTO);
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {
        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointmentDTO);
        callback.displayDialogFragment(detailDialog, true);
    }

    private boolean hasMorePages() {
        return paging.getCurrentPage() < paging.getTotalPages();
    }

    @Override
    public boolean canCheckOut(AppointmentDTO appointmentDTO) {
        return appointmentsResultModel.getPayload()
                .canCheckInCheckOut(appointmentDTO.getMetadata().getPracticeId());
    }
}
