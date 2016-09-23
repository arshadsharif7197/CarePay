package com.carecloud.carepaylibray.appointments.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.adapters.AllProviderAdapter;
import com.carecloud.carepaylibray.appointments.adapters.RecentProviderAdapter;
import com.carecloud.carepaylibray.appointments.dialog.VisitTypeDialog;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;

import java.util.ArrayList;

public class ChooseProviderFragment extends Fragment implements AllProviderAdapter.OnAllListItemClickListener,
        RecentProviderAdapter.OnRecentListItemClickListener, VisitTypeDialog.OnDialogListItemClickListener {

    private ArrayList<AppointmentModel> recentProviderItems = new ArrayList<>();
    private ArrayList<AppointmentModel> allProviderItems = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    @SuppressLint("InflateParams")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AddAppointmentActivity) getActivity()).setTitle("Choose Provider");
        ((AddAppointmentActivity) getActivity()).setToolbarNavigationIcon(
                ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_choose_provider, container, false);

        /// DUMMY DATA START
        AppointmentModel pm = new AppointmentModel();
        pm.setDoctorName("Dr. Ellie Burton");
        pm.setAppointmentType("Family Physician");
        pm.setAppointmentDate("09/08/16 5:12:28 PM UTC");
        pm.setAptId("1234");
        pm.setPlaceName("Mercy Hospital");
        pm.setPlaceAddress("3663 S Miami Ave, Miami, FL 33133, USA");
        recentProviderItems.add(pm);
        allProviderItems.add(pm);

        pm = new AppointmentModel();
        pm.setDoctorName("Dr. Joshua Wellington");
        pm.setAppointmentType("Neurologist");
        pm.setAppointmentDate("09/08/16 5:12:28 PM UTC");
        pm.setAptId("1234");
        pm.setPlaceName("Mercy Hospital");
        pm.setPlaceAddress("3663 S Miami Ave, Miami, FL 33133, USA");
        recentProviderItems.add(pm);
        allProviderItems.add(pm);

        pm = new AppointmentModel();
        pm.setDoctorName("Dr. George Diaz");
        pm.setAppointmentType("Urologist");
        pm.setAppointmentDate("09/08/16 5:12:28 PM UTC");
        pm.setAptId("1234");
        pm.setPlaceName("Mercy Hospital");
        pm.setPlaceAddress("3663 S Miami Ave, Miami, FL 33133, USA");
        allProviderItems.add(pm);

        pm = new AppointmentModel();
        pm.setDoctorName("Dr. Helena S. Harley");
        pm.setAppointmentType("Pediatry");
        pm.setAppointmentDate("09/08/16 5:12:28 PM UTC");
        pm.setAptId("1234");
        pm.setPlaceName("Mercy Hospital");
        pm.setPlaceAddress("3663 S Miami Ave, Miami, FL 33133, USA");
        allProviderItems.add(pm);
        /// DUMMY DATA END

        // Load and display Recent provider
        RecentProviderAdapter recentProviderAdapter = new RecentProviderAdapter(getActivity(), recentProviderItems, this);
        RecyclerView recyclerViewRecent = ((RecyclerView) view.findViewById(R.id.choose_provider_recycler_view_recent));
        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecent.setAdapter(recentProviderAdapter);

        // Load and display All provider
        AllProviderAdapter allProviderAdapter = new AllProviderAdapter(getActivity(), allProviderItems, this);
        RecyclerView recyclerViewAll = ((RecyclerView) view.findViewById(R.id.choose_provider_recycler_view_all));
        recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAll.setAdapter(allProviderAdapter);

        return view;
    }

    private void loadVisitTypeScreen(AppointmentModel model) {
        VisitTypeDialog dialog = new VisitTypeDialog(getActivity(), model, this);
        dialog.show();
    }

    @Override
    public void onAllListItemClickListener(int position) {
        AppointmentModel model = allProviderItems.get(position);
        loadVisitTypeScreen(model);
    }

    @Override
    public void onRecentListItemClickListener(int position) {
        AppointmentModel model = recentProviderItems.get(position);
        loadVisitTypeScreen(model);
    }

    @Override
    public void onDialogListItemClickListener(AppointmentModel model) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AvailableHoursFragment visitTypeFragment = (AvailableHoursFragment)
                fm.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());

        if (visitTypeFragment == null) {
            visitTypeFragment = new AvailableHoursFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", model);
        visitTypeFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.add_appointments_frag_holder, visitTypeFragment,
                AvailableHoursFragment.class.getSimpleName()).commit();
    }
}
