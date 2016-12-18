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
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepay.patient.appointments.adapters.ProviderAdapter;
import com.carecloud.carepay.patient.appointments.dialog.VisitTypeDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentProvidersDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChooseProviderFragment extends Fragment implements ProviderAdapter.OnProviderListItemClickListener,
        VisitTypeDialog.OnDialogListItemClickListener {

    private RecyclerView providersRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;

    private ChooseProviderFragment chooseProviderFragment;
    private List<AppointmentProvidersDTO> providersModel;
    private AppointmentProvidersDTO selectedProvider;

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

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE)) {
            Gson gson = new Gson();
            String appointmentInfoString = extras.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
            appointmentsResultModel = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);
        }

        // set the toolbar
        Toolbar toolbar = (Toolbar) chooseProviderView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(appointmentsResultModel.getMetadata().getLabel().getChooseProviderHeading());
        titleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setTitle("");

        TextView otherView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        otherView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        otherView.setVisibility(View.GONE);
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

        //Fetch provider data
        getProvidersInformation();

        return chooseProviderView;
    }

    private void getProvidersInformation() {
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                && appointmentsResultModel.getPayload().getProviders() != null
                && appointmentsResultModel.getPayload().getProviders().size() > 0) {

            providersModel = appointmentsResultModel.getPayload().getProviders();
            List<Object> providersListWithHeader = getProvidersListWithHeader();

            if (providersListWithHeader != null && providersListWithHeader.size() > 0) {
                ProviderAdapter providerAdapter = new ProviderAdapter(
                        getActivity(), providersListWithHeader, ChooseProviderFragment.this,
                        chooseProviderFragment);
                providersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                providersRecyclerView.setAdapter(providerAdapter);
            }
        }
    }

    private List<Object> getProvidersListWithHeader() {
        List<Object> providersListWithHeader = new ArrayList<>();
        if (providersModel != null && providersModel.size() > 0) {
            AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
            appointmentSectionHeaderModel.setAppointmentHeader(
                    appointmentsResultModel.getMetadata().getLabel().getChooseProviderAllHeader());
            providersListWithHeader.add(appointmentSectionHeaderModel);

            for (AppointmentProvidersDTO providersScheduleItem : providersModel) {
                providersListWithHeader.add(providersScheduleItem);
            }
        }

        return providersListWithHeader;
    }

    private void loadVisitTypeScreen(AppointmentProvidersDTO model) {
        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(this.getContext(), model, this);
        visitTypeDialog.show();
    }

    @Override
    public void onProviderListItemClickListener(int position) {
        AppointmentProvidersDTO model = providersModel.get(position - 1);
        selectedProvider = model;
        loadVisitTypeScreen(selectedProvider);
    }

    /**
     * what to do with the selected visit type and provider
     * @param selectedVisitType selected visit type from dialog
     */
    public void onDialogListItemClickListener(VisitTypeDTO selectedVisitType) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AvailableHoursFragment visitTypeFragment = (AvailableHoursFragment)
                fragmentManager.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());

        if (visitTypeFragment == null) {
            visitTypeFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        AppointmentDTO appointmentDTO= new AppointmentDTO();
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_BUNDLE, gson.toJson(appointmentDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(selectedProvider));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(selectedVisitType));
        visitTypeFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, visitTypeFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
    }

    /**
     *
     * @return the appointmentResultModel
     */
    public AppointmentsResultModel getAppointmentsResultModel() {
        return appointmentsResultModel;
    }

}
