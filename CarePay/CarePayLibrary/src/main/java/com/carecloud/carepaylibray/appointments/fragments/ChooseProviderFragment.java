package com.carecloud.carepaylibray.appointments.fragments;

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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.adapters.ProviderAdapter;
import com.carecloud.carepaylibray.appointments.dialog.VisitTypeDialog;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

public class ChooseProviderFragment extends Fragment implements ProviderAdapter.OnAllListItemClickListener,
        VisitTypeDialog.OnDialogListItemClickListener {

    private ArrayList<Appointment> recentProviderItems = new ArrayList<>();
    private ArrayList<Appointment> allProviderItems = new ArrayList<>();

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

        // set the toolbar
        Toolbar toolbar = (Toolbar) chooseProviderView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(R.string.apt_choose_provider_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), titleView);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AddAppointmentActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        /// DUMMY DATA START
        Appointment appointmentModel = new Appointment();
        recentProviderItems.add(appointmentModel);
        allProviderItems.add(appointmentModel);

        appointmentModel = new Appointment();
        recentProviderItems.add(appointmentModel);
        allProviderItems.add(appointmentModel);

        appointmentModel = new Appointment();
        allProviderItems.add(appointmentModel);

        appointmentModel = new Appointment();
        allProviderItems.add(appointmentModel);
        /// DUMMY DATA END

        // Load and display Recent provider
        ProviderAdapter recentProviderAdapter = new ProviderAdapter(getActivity(), recentProviderItems, this);
        RecyclerView recyclerViewRecent = ((RecyclerView)
                chooseProviderView.findViewById(R.id.choose_provider_recycler_view_recent));
        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecent.setAdapter(recentProviderAdapter);

        // Load and display All provider
        ProviderAdapter allProviderAdapter = new ProviderAdapter(getActivity(), allProviderItems, this);
        RecyclerView recyclerViewAll = ((RecyclerView)
                chooseProviderView.findViewById(R.id.choose_provider_recycler_view_all));
        recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAll.setAdapter(allProviderAdapter);

        return chooseProviderView;
    }

    private void loadVisitTypeScreen(Appointment model) {
        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(getActivity(), model, this);
        visitTypeDialog.show();
    }

    @Override
    public void onAllListItemClickListener(int position) {
        Appointment model = allProviderItems.get(position);
        loadVisitTypeScreen(model);
    }

    @Override
    public void onDialogListItemClickListener(Appointment model) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AvailableHoursFragment visitTypeFragment = (AvailableHoursFragment)
                fragmentManager.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());

        if (visitTypeFragment == null) {
            visitTypeFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", model);
        visitTypeFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, visitTypeFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
    }
}
