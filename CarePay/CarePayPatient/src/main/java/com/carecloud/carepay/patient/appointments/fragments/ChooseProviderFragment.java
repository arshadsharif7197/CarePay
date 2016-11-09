package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.adapters.ProviderAdapter;
import com.carecloud.carepay.patient.appointments.dialog.VisitTypeDialog;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.ProvidersScheduleDTO;
import com.carecloud.carepay.patient.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.constants.CarePayConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseProviderFragment extends Fragment implements ProviderAdapter.OnProviderListItemClickListener,
        VisitTypeDialog.OnDialogListItemClickListener {

    private RecyclerView providersRecyclerView;
    private ProgressBar appointmentProgressBar;
    private AppointmentsResultModel appointmentsResultModel;

    private ChooseProviderFragment chooseProviderFragment;
    private List<ProvidersScheduleDTO> providersScheduleModel;
    private List<Object> providersListWithHeader;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    @SuppressLint("InflateParams")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View chooseProviderView = inflater.inflate(R.layout.fragment_choose_provider, container, false);
        chooseProviderFragment = this;

        // set the toolbar
        Toolbar toolbar = (Toolbar) chooseProviderView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_choose_provider_title);
        titleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setTitle("");

        TextView otherView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        otherView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        otherView.setVisibility(View.VISIBLE);
        otherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                OtherProviderFragment otherProviderFragment = (OtherProviderFragment)
//                        fragmentManager.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());
//
//                if (otherProviderFragment == null) {
//                    otherProviderFragment = new OtherProviderFragment();
//                }
//
//                fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, otherProviderFragment,
//                        AvailableHoursFragment.class.getSimpleName()).commit();
            }
        });

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        providersRecyclerView = ((RecyclerView) chooseProviderView.findViewById(R.id.providers_recycler_view));
        appointmentProgressBar = (ProgressBar) chooseProviderView.findViewById(R.id.providers_progress_bar);
        appointmentProgressBar.setVisibility(View.GONE);

        //Fetch provider data
        getProvidersInformation();

        return chooseProviderView;
    }

    private void getProvidersInformation() {
        appointmentProgressBar.setVisibility(View.VISIBLE);
        AppointmentService aptService = (new BaseServiceGenerator(getActivity())).createService(AppointmentService.class);
        Call<AppointmentsResultModel> call = aptService.getProvidersList();
        call.enqueue(new Callback<AppointmentsResultModel>() {

            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                appointmentsResultModel = response.body();
                appointmentProgressBar.setVisibility(View.GONE);

                if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                        && appointmentsResultModel.getPayload().getProvidersSchedule() != null
                        && appointmentsResultModel.getPayload().getProvidersSchedule().size() > 0) {

                    providersScheduleModel = appointmentsResultModel.getPayload().getProvidersSchedule();
                    providersListWithHeader = getProvidersListWithHeader();

                    if (providersListWithHeader != null && providersListWithHeader.size() > 0) {
                        ProviderAdapter providerAdapter = new ProviderAdapter(
                                getActivity(), providersListWithHeader, ChooseProviderFragment.this,
                                chooseProviderFragment);
                        providersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        providersRecyclerView.setAdapter(providerAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {
                appointmentProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private List<Object> getProvidersListWithHeader() {
        List<Object> providersListWithHeader = new ArrayList<>();
        if (providersScheduleModel != null && providersScheduleModel.size() > 0) {
            AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
            appointmentSectionHeaderModel.setAppointmentHeader(CarePayConstants.DAY_UPCOMING);
            providersListWithHeader.add(appointmentSectionHeaderModel);

            for (ProvidersScheduleDTO providersScheduleItem : providersScheduleModel) {
                providersListWithHeader.add(providersScheduleItem);
            }
        }

        return providersListWithHeader;
    }

    private void loadVisitTypeScreen(ProvidersScheduleDTO model) {
//        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(getActivity(), model, this);
//        visitTypeDialog.show();
    }

    @Override
    public void onProviderListItemClickListener(int position) {
        ProvidersScheduleDTO model = providersScheduleModel.get(position);
        loadVisitTypeScreen(model);
    }

    @Override
    public void onDialogListItemClickListener(AppointmentDTO model) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AvailableHoursFragment visitTypeFragment = (AvailableHoursFragment)
                fragmentManager.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());

        if (visitTypeFragment == null) {
            visitTypeFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_BUNDLE, model);
        visitTypeFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, visitTypeFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
    }
}
