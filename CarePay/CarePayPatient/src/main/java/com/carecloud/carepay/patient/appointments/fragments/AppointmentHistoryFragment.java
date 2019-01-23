package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.adapters.AppointmentHistoricAdapter;
import com.carecloud.carepay.patient.appointments.adapters.PracticesAdapter;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjohnson on 19/10/18.
 */
public class AppointmentHistoryFragment extends BaseFragment
        implements AppointmentHistoricAdapter.SelectAppointmentCallback {

    private AppointmentViewHandler callback;
    private AppointmentsResultModel appointmentDto;
    private AppointmentHistoricAdapter adapter;
    private RecyclerView historicAppointmentsRecyclerView;
    private static final int BOTTOM_ROW_OFFSET = 10;
    private boolean isPaging;
    private Paging paging;
    private UserPracticeDTO selectedPractice;

    public static AppointmentHistoryFragment newInstance() {
        return new AppointmentHistoryFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = (AppointmentViewHandler) context;
        } else {
            throw new ClassCastException("Activity must implement AppointmentViewHandler");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentDto = ((PatientAppointmentPresenter) callback.getAppointmentPresenter()).getMainAppointmentDto();
        if (!appointmentDto.getPayload().getPagingInfo().isEmpty()) {
            paging = appointmentDto.getPayload().getPagingInfo().get(0).getPaging();
        } else {
            paging = new Paging();
        }

        Collections.sort(appointmentDto.getPayload().getUserPractices(), new Comparator<UserPracticeDTO>() {
            @Override
            public int compare(final UserPracticeDTO object1, final UserPracticeDTO object2) {
                return object1.getPracticeName().compareTo(object2.getPracticeName());
            }
        });
        selectedPractice = appointmentDto.getPayload().getUserPractices().get(0);
        callAppointmentService(selectedPractice, true, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historicAppointmentsRecyclerView = view.findViewById(R.id.historicAppointmentsRecyclerView);
        Map<String, Set<String>> enabledPracticeLocations = new HashMap<>();
        for (AppointmentDTO appointmentDTO : appointmentDto.getPayload().getAppointments()) {
            String practiceId = appointmentDTO.getMetadata().getPracticeId();
            if (!enabledPracticeLocations.containsKey(practiceId)) {
                enabledPracticeLocations.put(practiceId,
                        ApplicationPreferences.getInstance().getPracticesWithBreezeEnabled(practiceId));
            }
        }

        adapter = new AppointmentHistoricAdapter(getContext(), new ArrayList<AppointmentDTO>(),
                appointmentDto.getPayload().getUserPractices(), enabledPracticeLocations, this);
        historicAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historicAppointmentsRecyclerView.setAdapter(adapter);
        historicAppointmentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > adapter.getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    if (hasMorePages()) {
                        callAppointmentService(selectedPractice, false, false);
                        isPaging = true;
                    }
                }
            }
        });

        if (appointmentDto.getPayload().getUserPractices().size() > 1) {
            showPracticeToolbar(view);
        }

        FloatingActionButton floatingActionButton = view.findViewById(com.carecloud.carepaylibrary.R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.getAppointmentPresenter().newAppointment();
            }
        });
    }

    private void showPracticeToolbar(View view) {
        RecyclerView practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        practicesRecyclerView.setVisibility(View.VISIBLE);
        practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        PracticesAdapter adapter = new PracticesAdapter(appointmentDto.getPayload().getUserPractices());
        adapter.setCallback(new PracticesAdapter.PracticeSelectInterface() {
            @Override
            public void onPracticeSelected(UserPracticeDTO userPracticeDTO) {
                selectedPractice = userPracticeDTO;
                callAppointmentService(userPracticeDTO, true, true);
            }
        });
        practicesRecyclerView.setAdapter(adapter);
    }

    private void callAppointmentService(UserPracticeDTO userPracticeDTO,
                                        final boolean refresh,
                                        final boolean showShimmerLayout) {
        long currentPage;
        if (refresh) {
            currentPage = 0;
        } else {
            currentPage = paging.getCurrentPage();
        }

        Map<String, String> queryMap = new HashMap<>();
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -1);
        queryMap.put("start_date", DateUtil.getInstance().setDate(new Date(0)).getServerFormat());
        queryMap.put("end_date", DateUtil.getInstance().setDate(calendar).getServerFormat());
        queryMap.put("page", String.valueOf(currentPage + 1));
        queryMap.put("limit", String.valueOf(paging.getResultsPerPage()));
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        getWorkflowServiceHelper().execute(appointmentDto.getMetadata().getLinks().getAppointments()
                , new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        if (showShimmerLayout) {
                            showShimmerEffect();
                        } else if (!refresh) {
                            adapter.setLoading(true);
                        }
                        if (getView() != null) {
                            getView().findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        if (isVisible()) {
                            getView().findViewById(R.id.fakeView).setVisibility(View.GONE);
                        }
                        if (showShimmerLayout) {
                            hideShimmerEffect();
                        } else if (!refresh) {
                            adapter.setLoading(true);
                        }
                        List<UserPracticeDTO> userPractices = appointmentDto.getPayload().getUserPractices();
                        appointmentDto = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                        appointmentDto.getPayload().setUserPractices(userPractices);
                        isPaging = false;
                        adapter.setLoading(false);
                        if (appointmentDto.getPayload().getPagingInfo().size() > 0) {
                            paging = appointmentDto.getPayload().getPagingInfo().get(0).getPaging();
                        }
                        if (appointmentDto.getPayload().getAppointments().size() > 0) {
                            showHistoricAppointments(appointmentDto.getPayload().getAppointments(), refresh);
                        } else {
                            showNoAppointmentsLayout();
                        }
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        if (isVisible()) {
                            getView().findViewById(R.id.fakeView).setVisibility(View.GONE);
                        }
                        isPaging = false;
                        adapter.setLoading(false);
                        if (showShimmerLayout && ((BaseActivity) getActivity()).isVisible()) {
                            hideShimmerEffect();
                        }
                        showErrorNotification(exceptionMessage);
                    }
                }, queryMap);
    }

    private void hideShimmerEffect() {
        if(isAdded()) {
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

    private void showHistoricAppointments(List<AppointmentDTO> appointments, boolean refresh) {
        getView().findViewById(R.id.noAppointmentsLayout).setVisibility(View.GONE);
        historicAppointmentsRecyclerView.setVisibility(View.VISIBLE);
        adapter.setData(appointments, refresh);
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
        ((PatientAppointmentPresenter) callback.getAppointmentPresenter()).onCheckOutStarted(appointmentDTO);
    }

    private void showAppointmentPopup(AppointmentDTO appointmentDTO) {
        ((PatientAppointmentPresenter) callback.getAppointmentPresenter())
                .displayAppointmentDetails(appointmentDTO);
    }

    private boolean hasMorePages() {
        return paging.getCurrentPage() < paging.getTotalPages();
    }
}
