package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseFragment;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class MyHealthMainFragment extends BaseFragment implements MyHealthDataInterface {

    private MyHealthInterface callback;
    private MyHealthDto myHealthDto;
    public static final int MAX_ITEMS_TO_SHOW = 3;

    public MyHealthMainFragment() {

    }

    public static MyHealthMainFragment newInstance() {
        return new MyHealthMainFragment();
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
        myHealthDto = (MyHealthDto) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_my_health_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpCareTeamRecyclerView(view);
        setUpConditionsRecyclerView(view);
        setUpAllergiesRecyclerView(view);
        setUpMedicationsRecyclerView(view);
        setUpLabsRecyclerView(view);
    }

    private void setUpLabsRecyclerView(View view) {
        RecyclerView labsRecyclerView = (RecyclerView) view.findViewById(R.id.labsRecyclerView);
        List<LabDto> labs = myHealthDto.getPayload().getMyHealthData().getLabs().getLabs();
        if (labs.isEmpty()) {
            view.findViewById(R.id.labsContainer).setVisibility(View.GONE);
            labsRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            labsRecyclerView.setLayoutManager(linearLayout);
            LabsRecyclerViewAdapter labsAdapter = new LabsRecyclerViewAdapter(labs, MAX_ITEMS_TO_SHOW);
            labsAdapter.setCallback(this);
            labsRecyclerView.setAdapter(labsAdapter);
            TextView seeAll = (TextView) view.findViewById(R.id.labsSeeAllTextView);
            if (labs.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.showListFragment(MyHealthListFragment.LABS);
                    }
                });
            }
        }
    }

    private void setUpMedicationsRecyclerView(View view) {
        RecyclerView medicationsRecyclerView = (RecyclerView) view.findViewById(R.id.medicationsRecyclerView);
        List<MedicationDto> medications = myHealthDto
                .getPayload().getMyHealthData().getMedications().getMedications();
        if (medications.isEmpty()) {
            view.findViewById(R.id.medicationsContainer).setVisibility(View.GONE);
            medicationsRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            medicationsRecyclerView.setLayoutManager(linearLayout);
            MedicationsRecyclerViewAdapter medicationsAdapter = new MedicationsRecyclerViewAdapter(
                    medications, MAX_ITEMS_TO_SHOW);
            medicationsAdapter.setCallback(this);
            medicationsRecyclerView.setAdapter(medicationsAdapter);

            TextView seeAll = (TextView) view.findViewById(R.id.medicationsSeeAllTextView);
            if (medications.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.showListFragment(MyHealthListFragment.MEDICATIONS);
                    }
                });
            }
        }
    }

    private void setUpAllergiesRecyclerView(View view) {
        RecyclerView allergiesRecyclerView = (RecyclerView) view.findViewById(R.id.allergiesRecyclerView);
        List<AllergyDto> allergies = myHealthDto.getPayload().getMyHealthData().getAllergies().getAllergies();
        if (allergies.isEmpty()) {
            view.findViewById(R.id.allergiesContainer).setVisibility(View.GONE);
            allergiesRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            allergiesRecyclerView.setLayoutManager(linearLayout);
            AllergiesRecyclerViewAdapter allergiesAdapter = new AllergiesRecyclerViewAdapter(allergies,
                    MAX_ITEMS_TO_SHOW);
            allergiesAdapter.setCallback(this);
            allergiesRecyclerView.setAdapter(allergiesAdapter);

            TextView seeAll = (TextView) view.findViewById(R.id.allergiesSeeAllTextView);
            if (allergies.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.showListFragment(MyHealthListFragment.ALLERGIES);
                    }
                });
            }
        }
    }

    private void setUpConditionsRecyclerView(View view) {
        RecyclerView conditionsRecyclerView = (RecyclerView) view.findViewById(R.id.conditionsRecyclerView);
        List<AssertionDto> assertions = myHealthDto
                .getPayload().getMyHealthData().getAssertions().getAssertions();
        if (assertions.isEmpty()) {
            view.findViewById(R.id.conditionsContainer).setVisibility(View.GONE);
            conditionsRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            conditionsRecyclerView.setLayoutManager(linearLayout);
            ConditionsRecyclerViewAdapter conditionsAdapter = new ConditionsRecyclerViewAdapter(
                    assertions, MAX_ITEMS_TO_SHOW);
            conditionsRecyclerView.setAdapter(conditionsAdapter);

            TextView seeAll = (TextView) view.findViewById(R.id.conditionsSeeAllTextView);
            if (assertions.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.showListFragment(MyHealthListFragment.CONDITIONS);
                    }
                });
            }
        }

    }

    private void setUpCareTeamRecyclerView(View view) {
        RecyclerView careTeamRecyclerView = (RecyclerView) view.findViewById(R.id.careTeamRecyclerView);
        List<ProviderDTO> providers = myHealthDto
                .getPayload().getMyHealthData().getProviders().getProviders();
        if (providers.isEmpty()) {
            view.findViewById(R.id.providerContainer).setVisibility(View.GONE);
            careTeamRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            careTeamRecyclerView.setLayoutManager(linearLayout);
            CareTeamRecyclerViewAdapter careTeamAdapter = new CareTeamRecyclerViewAdapter(
                    providers, MAX_ITEMS_TO_SHOW);
            careTeamAdapter.setCallback(this);
            careTeamRecyclerView.setAdapter(careTeamAdapter);

            TextView seeAll = (TextView) view.findViewById(R.id.careTeamSeeAllTextView);
            if (providers.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.showListFragment(MyHealthListFragment.CARE_TEAM);
                    }
                });
            }
        }
    }

    @Override
    public void onSeeAllFullMedicalRecordClicked() {
        callback.onSeeAllFullMedicalRecordClicked();
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
