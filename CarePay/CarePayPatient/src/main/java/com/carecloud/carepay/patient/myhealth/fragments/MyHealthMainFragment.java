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
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthProviderDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.patient.visitsummary.VisitSummaryDialogFragment;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.PortalSetting;
import com.carecloud.carepaylibray.appointments.models.PortalSettingDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class MyHealthMainFragment extends BaseFragment {

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
        setUpVisitSummaryButton(view);
    }

    private void setUpVisitSummaryButton(View view) {
        boolean isVisitSummaryEnabled = false;
        for (UserPracticeDTO userPracticeDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (userPracticeDTO.isVisitSummaryEnabled()) {
                for (PortalSettingDTO portalSettingDTO : myHealthDto.getPayload().getPortalSettings()) {
                    if (userPracticeDTO.getPracticeId().equals(portalSettingDTO.getMetadata().getPracticeId())) {
                        for (PortalSetting portalSetting : portalSettingDTO.getPayload()) {
                            if (portalSetting.getName().toLowerCase().equals("visit summary")
                                    && portalSetting.getStatus().toLowerCase().equals("a")) {
                                isVisitSummaryEnabled = myHealthDto.getPayload()
                                        .canViewAndCreateVisitSummaries(userPracticeDTO.getPracticeId());
                                break;
                            }
                        }
                    }
                    if (isVisitSummaryEnabled) break;
                }
            }
            if (isVisitSummaryEnabled) break;
        }
        TextView visitSummaryButton = view.findViewById(R.id.visitSummaryButton);
        visitSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.displayDialogFragment(VisitSummaryDialogFragment.newInstance(), true);
            }
        });
        visitSummaryButton.setVisibility(isVisitSummaryEnabled ? View.VISIBLE : View.GONE);
    }

    private void setUpLabsRecyclerView(View view) {
        boolean hasPermissionsToViewLabs = hasPermissionsToViewLabs(myHealthDto
                .getPayload().getMyHealthData().getLabs().getLabs());
        List<LabDto> labs = myHealthDto
                .getPayload().getMyHealthData().getLabs().getLabs();
        if (!hasPermissionsToViewLabs) {
            showNoPermissionLayout(view, R.id.labsContainer, R.id.labsRecyclerView,
                    R.id.labsNoDataContainer, R.id.labsNoDataTitle, R.id.labsNoDataSubTitle);
        } else if (labs.isEmpty()) {
            view.findViewById(R.id.labsContainer).setVisibility(View.GONE);
            view.findViewById(R.id.labsRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.labsNoDataContainer).setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            RecyclerView labsRecyclerView = view.findViewById(R.id.labsRecyclerView);
            labsRecyclerView.setLayoutManager(linearLayout);
            LabsRecyclerViewAdapter labsAdapter = new LabsRecyclerViewAdapter(labs, MAX_ITEMS_TO_SHOW);
            labsAdapter.setCallback(callback);
            labsRecyclerView.setAdapter(labsAdapter);
            TextView seeAll = view.findViewById(R.id.labsSeeAllTextView);
            if (labs.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewLabList));
                        callback.showListFragment(MyHealthListFragment.LABS);
                    }
                });
            }
        }
    }

    private boolean hasPermissionsToViewLabs(List<LabDto> labs) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practiceDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (myHealthDto.getPayload().canViewLabs(practiceDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                labs = filterLabsByPermission(labs, practiceDTO.getPracticeId());
            }
        }
        myHealthDto.getPayload().getMyHealthData().getLabs().setLabs(labs);
        return atLeastOneHasPermission || myHealthDto.getPayload().getPracticeInformation().isEmpty();
    }

    private List<LabDto> filterLabsByPermission(List<LabDto> labs, String practiceId) {
        List<LabDto> filteredList = new ArrayList<>();
        for (LabDto labDto : labs) {
            if (!labDto.getBusinessEntity().getGuid().equals(practiceId)) {
                filteredList.add(labDto);
            }
        }
        return filteredList;
    }

    private void setUpMedicationsRecyclerView(View view) {
        boolean hasPermissionsToViewMedications = hasPermissionsToViewMedications(myHealthDto
                .getPayload().getMyHealthData().getMedications().getMedications());
        List<MedicationDto> medications = myHealthDto
                .getPayload().getMyHealthData().getMedications().getMedications();
        if (!hasPermissionsToViewMedications) {
            showNoPermissionLayout(view, R.id.medicationsContainer, R.id.medicationsRecyclerView,
                    R.id.medicationsNoDataContainer, R.id.medicationsNoDataTitle, R.id.medicationsNoDataSubTitle);
        } else if (medications.isEmpty()) {
            view.findViewById(R.id.medicationsContainer).setVisibility(View.GONE);
            view.findViewById(R.id.medicationsRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.medicationsNoDataContainer).setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            RecyclerView medicationsRecyclerView = view.findViewById(R.id.medicationsRecyclerView);
            medicationsRecyclerView.setLayoutManager(linearLayout);
            MedicationsRecyclerViewAdapter medicationsAdapter = new MedicationsRecyclerViewAdapter(
                    medications, MAX_ITEMS_TO_SHOW);
            medicationsAdapter.setCallback(callback);
            medicationsRecyclerView.setAdapter(medicationsAdapter);

            TextView seeAll = view.findViewById(R.id.medicationsSeeAllTextView);
            if (medications.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewMedicationList));
                        callback.showListFragment(MyHealthListFragment.MEDICATIONS);
                    }
                });
            }
        }
    }

    private boolean hasPermissionsToViewMedications(List<MedicationDto> medications) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practiceDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (myHealthDto.getPayload().canViewMedications(practiceDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                medications = filterMedicationsByPermission(medications, practiceDTO.getPracticeId());
            }
        }
        myHealthDto.getPayload().getMyHealthData().getMedications().setMedications(medications);
        return atLeastOneHasPermission || myHealthDto.getPayload().getPracticeInformation().isEmpty();
    }

    private List<MedicationDto> filterMedicationsByPermission(List<MedicationDto> medications, String practiceId) {
        List<MedicationDto> filteredList = new ArrayList<>();
        for (MedicationDto medicationDto : medications) {
            if (!medicationDto.getPractice().equals(practiceId)) {
                filteredList.add(medicationDto);
            }
        }
        return filteredList;
    }

    private void setUpAllergiesRecyclerView(View view) {
        boolean hasPermissionsToViewAllergies = hasPermissionsToViewAllergies(myHealthDto.getPayload()
                .getMyHealthData().getAllergies().getAllergies());
        List<AllergyDto> allergies = myHealthDto
                .getPayload().getMyHealthData().getAllergies().getAllergies();
        if (!hasPermissionsToViewAllergies) {
            showNoPermissionLayout(view, R.id.allergiesContainer, R.id.allergiesRecyclerView,
                    R.id.allergiesNoDataContainer, R.id.allergiesNoDataTitle, R.id.allergiesNoDataSubTitle);
        } else if (allergies.isEmpty()) {
            view.findViewById(R.id.allergiesContainer).setVisibility(View.GONE);
            view.findViewById(R.id.allergiesRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.allergiesNoDataContainer).setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            RecyclerView allergiesRecyclerView = view.findViewById(R.id.allergiesRecyclerView);
            allergiesRecyclerView.setLayoutManager(linearLayout);
            AllergiesRecyclerViewAdapter allergiesAdapter = new AllergiesRecyclerViewAdapter(allergies,
                    MAX_ITEMS_TO_SHOW);
            allergiesAdapter.setCallback(callback);
            allergiesRecyclerView.setAdapter(allergiesAdapter);

            TextView seeAll = view.findViewById(R.id.allergiesSeeAllTextView);
            if (allergies.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewAllergyList));
                        callback.showListFragment(MyHealthListFragment.ALLERGIES);
                    }
                });
            }
        }
    }

    private boolean hasPermissionsToViewAllergies(List<AllergyDto> allergies) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practiceDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (myHealthDto.getPayload().canViewAllergies(practiceDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                allergies = filterAllergiesByPermission(allergies, practiceDTO.getPracticeId());
            }
        }
        myHealthDto.getPayload().getMyHealthData().getAllergies().setAllergies(allergies);
        return atLeastOneHasPermission || myHealthDto.getPayload().getPracticeInformation().isEmpty();
    }

    private List<AllergyDto> filterAllergiesByPermission(List<AllergyDto> allergies, String practiceId) {
        List<AllergyDto> filteredList = new ArrayList<>();
        for (AllergyDto allergyDto : allergies) {
            if (!allergyDto.getBusinessEntity().getGuid().equals(practiceId)) {
                filteredList.add(allergyDto);
            }
        }
        return filteredList;
    }

    private void setUpConditionsRecyclerView(View view) {
        boolean hasPermissionsToViewConditions = hasPermissionsToViewConditions(myHealthDto
                .getPayload().getMyHealthData().getAssertions().getAssertions());
        List<AssertionDto> assertions = myHealthDto
                .getPayload().getMyHealthData().getAssertions().getAssertions();
        if (!hasPermissionsToViewConditions) {
            showNoPermissionLayout(view, R.id.conditionsContainer, R.id.conditionsRecyclerView,
                    R.id.conditionsNoDataContainer, R.id.conditionsNoDataTitle, R.id.conditionsNoDataSubtitle);
        } else if (assertions.isEmpty()) {
            view.findViewById(R.id.conditionsContainer).setVisibility(View.GONE);
            view.findViewById(R.id.conditionsRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.conditionsNoDataContainer).setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            RecyclerView conditionsRecyclerView = view.findViewById(R.id.conditionsRecyclerView);
            conditionsRecyclerView.setLayoutManager(linearLayout);
            ConditionsRecyclerViewAdapter conditionsAdapter = new ConditionsRecyclerViewAdapter(
                    assertions, MAX_ITEMS_TO_SHOW);
            conditionsRecyclerView.setAdapter(conditionsAdapter);

            TextView seeAll = view.findViewById(R.id.conditionsSeeAllTextView);
            if (assertions.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewConditionList));
                        callback.showListFragment(MyHealthListFragment.CONDITIONS);
                    }
                });
            }
        }
    }

    private boolean hasPermissionsToViewConditions(List<AssertionDto> assertions) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practiceDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (myHealthDto.getPayload().canViewConditions(practiceDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                assertions = filterConditionsByPermission(assertions, practiceDTO.getPracticeId());
            }
        }
        myHealthDto.getPayload().getMyHealthData().getAssertions().setAssertions(assertions);
        return atLeastOneHasPermission || myHealthDto.getPayload().getPracticeInformation().isEmpty();
    }

    private List<AssertionDto> filterConditionsByPermission(List<AssertionDto> assertions, String practiceId) {
        List<AssertionDto> filteredList = new ArrayList<>();
        for (AssertionDto assertion : assertions) {
            if (!assertion.getPractice().equals(practiceId)) {
                filteredList.add(assertion);
            }
        }
        return filteredList;
    }

    private void setUpCareTeamRecyclerView(View view) {
        boolean hasPermissionsToViewCareTeam = hasPermissionsToViewCareTeam(myHealthDto
                .getPayload().getMyHealthData().getProviders().getProviders());
        List<MyHealthProviderDto> providers = myHealthDto
                .getPayload().getMyHealthData().getProviders().getProviders();
        if (!hasPermissionsToViewCareTeam) {
            showNoPermissionLayout(view, R.id.careTeamContainer, R.id.careTeamRecyclerView,
                    R.id.careTeamNoDataContainer, R.id.careTeamNoDataTitle, R.id.careTeamNoDataSubTitle);
        } else if (providers.isEmpty()) {
            view.findViewById(R.id.careTeamContainer).setVisibility(View.GONE);
            view.findViewById(R.id.careTeamRecyclerView).setVisibility(View.GONE);
            view.findViewById(R.id.careTeamNoDataContainer).setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayout = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            RecyclerView careTeamRecyclerView = view.findViewById(R.id.careTeamRecyclerView);
            careTeamRecyclerView.setLayoutManager(linearLayout);
            CareTeamRecyclerViewAdapter careTeamAdapter = new CareTeamRecyclerViewAdapter(
                    providers, MAX_ITEMS_TO_SHOW);
            careTeamAdapter.setCallback(callback);
            careTeamRecyclerView.setAdapter(careTeamAdapter);

            TextView seeAll = view.findViewById(R.id.careTeamSeeAllTextView);
            if (providers.size() <= MAX_ITEMS_TO_SHOW) {
                seeAll.setVisibility(View.GONE);
            } else {
                seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewCareTeamList));
                        callback.showListFragment(MyHealthListFragment.CARE_TEAM);
                    }
                });
            }
        }
    }

    private boolean hasPermissionsToViewCareTeam(List<MyHealthProviderDto> providers) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practiceDTO : myHealthDto.getPayload().getPracticeInformation()) {
            if (myHealthDto.getPayload().canViewCareTeam(practiceDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            } else {
                providers = filterCareTeamByPermission(providers, practiceDTO.getPracticeId());
            }
        }
        myHealthDto.getPayload().getMyHealthData().getProviders().setProviders(providers);
        return atLeastOneHasPermission || myHealthDto.getPayload().getPracticeInformation().isEmpty();
    }

    private List<MyHealthProviderDto> filterCareTeamByPermission(List<MyHealthProviderDto> providers,
                                                                 String practiceId) {
        List<MyHealthProviderDto> filteredList = new ArrayList<>();
        for (MyHealthProviderDto providerDto : providers) {
            if (!providerDto.getBusinessEntity().getGuid().equals(practiceId)) {
                filteredList.add(providerDto);
            }
        }
        return filteredList;
    }

    private void showNoPermissionLayout(View view, int containerToHide, int recyclerToHide,
                                        int noDataContainer, int noDataTitle, int noDataSubtitle) {
        view.findViewById(containerToHide).setVisibility(View.GONE);
        view.findViewById(recyclerToHide).setVisibility(View.GONE);
        View noPermissionView = view.findViewById(noDataContainer);
        noPermissionView.setVisibility(View.VISIBLE);
        TextView noDataTitleTextView = noPermissionView.findViewById(noDataTitle);
        noDataTitleTextView.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
        noPermissionView.findViewById(noDataSubtitle).setVisibility(View.GONE);
    }

}
