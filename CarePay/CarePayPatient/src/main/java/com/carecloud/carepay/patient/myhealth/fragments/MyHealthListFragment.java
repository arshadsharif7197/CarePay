package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.adapters.AllergiesRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.adapters.CareTeamRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.adapters.ConditionsRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.adapters.LabsRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.adapters.MedicationsRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.dtos.AssertionDto;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseFragment;

import java.util.List;

/**
 * @author pjohnson on 19/07/17.
 */

public class MyHealthListFragment extends BaseFragment implements MyHealthDataInterface {
    public static final int LABS = 100;
    public static final int MEDICATIONS = 101;
    public static final int ALLERGIES = 102;
    public static final int CONDITIONS = 103;
    public static final int CARE_TEAM = 104;

    private int type;
    private MyHealthInterface callback;
    private MyHealthDto myHealthDto;

    public MyHealthListFragment() {

    }

    /**
     * @param type the type of list to show
     * @return a new instance of MyHealthListFragment
     */
    public static MyHealthListFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        MyHealthListFragment fragment = new MyHealthListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MyHealthInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the Activity must implement MyHealthInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        type = getArguments().getInt("type");
        myHealthDto = (MyHealthDto) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_my_health_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayToolbar(true, null);
                getActivity().onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.actionButton);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.myHealthRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        switch (type) {
            case CARE_TEAM:
                List<ProviderDTO> providers = myHealthDto.getPayload().getMyHealthData()
                        .getProviders().getProviders();
                CareTeamRecyclerViewAdapter careTeamAdapter = new CareTeamRecyclerViewAdapter(
                        providers, providers.size());
                careTeamAdapter.setCallback(this);
                recyclerView.setAdapter(careTeamAdapter);
                title.setText(Label.getLabel("my_health_list_care_team_title"));
                break;
            case CONDITIONS:
                List<AssertionDto> assertionDtos = myHealthDto.getPayload().getMyHealthData()
                        .getAssertions().getAssertions();
                ConditionsRecyclerViewAdapter assertionAdapter = new ConditionsRecyclerViewAdapter(
                        assertionDtos, assertionDtos.size());
                recyclerView.setAdapter(assertionAdapter);
                title.setText(Label.getLabel("my_health_list_condition_title"));
                break;
            case ALLERGIES:
                List<AllergyDto> allergies = myHealthDto.getPayload().getMyHealthData()
                        .getAllergies().getAllergies();
                AllergiesRecyclerViewAdapter allergiesAdapter = new AllergiesRecyclerViewAdapter(
                        allergies, allergies.size());
                allergiesAdapter.setCallback(this);
                recyclerView.setAdapter(allergiesAdapter);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addAllergy();
                    }
                });
                title.setText(Label.getLabel("my_health_list_allergy_title"));
                break;
            case MEDICATIONS:
                List<MedicationDto> medications = myHealthDto.getPayload().getMyHealthData()
                        .getMedications().getMedications();
                MedicationsRecyclerViewAdapter medicationsAdapter = new MedicationsRecyclerViewAdapter(
                        medications, medications.size());
                medicationsAdapter.setCallback(this);
                recyclerView.setAdapter(medicationsAdapter);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addMedication();
                    }
                });
                title.setText(Label.getLabel("my_health_list_medication_title"));
                break;
            case LABS:
                List<LabDto> labs = myHealthDto.getPayload().getMyHealthData().getLabs().getLabs();
                LabsRecyclerViewAdapter labsAdapter = new LabsRecyclerViewAdapter(
                        labs, labs.size());
                labsAdapter.setCallback(this);
                recyclerView.setAdapter(labsAdapter);
                title.setText(Label.getLabel("my_health_list_lab_title"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        callback.displayToolbar(false, null);
    }

    @Override
    public void onSeeAllFullMedicalRecordClicked() {

    }

    @Override
    public void onProviderClicked(ProviderDTO provider) {
        callback.onProviderClicked(provider);
    }

    @Override
    public void onAllergyClicked(AllergyDto allergy) {
        callback.onAllergyClicked(allergy);
    }

    @Override
    public void addAllergy() {
        callback.addAllergy();
    }

    @Override
    public void onMedicationClicked(MedicationDto medication) {
        callback.onMedicationClicked(medication);
    }

    @Override
    public void addMedication() {
        callback.addMedication();
    }

    @Override
    public void onLabClicked(LabDto lab) {
        callback.onLabClicked(lab);
    }
}
