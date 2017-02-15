package com.carecloud.carepay.practice.library.payments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityDialog;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PaymentsActivity extends BasePracticeActivity implements FilterDialog.FilterCallBack {

    private PaymentsLabelDTO paymentsLabel;
    private PaymentsModel paymentsModel;
    private FilterModel filter;

    private ArrayList<FilterDataDTO> patients;
    private ArrayList<FilterDataDTO> locations = new ArrayList<>();
    private ArrayList<FilterDataDTO> doctors = new ArrayList<>();

    private String practiceCheckinFilterDoctorsLabel;
    private String practiceCheckinFilterLocationsLabel;
    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_payment);
        setNavigationBarVisibility();
        paymentsModel = getConvertedDTO(PaymentsModel.class);

        setLabels();
        populateList();
    }

    private void setLabels() {
        if (paymentsModel != null) {
            paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            if (paymentsLabel != null) {

                setViewTextById(R.id.practice_payment_title, paymentsLabel.getPracticePaymentsHeader());
                setViewTextById(R.id.practice_payment_go_back, paymentsLabel.getPracticePaymentsBackLabel());
                setViewTextById(R.id.practice_payment_find_patient, paymentsLabel.getPracticePaymentsFindPatientLabel());
                setViewTextById(R.id.practice_payment_filter_label, paymentsLabel.getPracticePaymentsFilter());
                setViewTextById(R.id.practice_payment_in_office_label, paymentsLabel.getPracticePaymentsInOffice());

                practiceCheckinFilterDoctorsLabel = paymentsLabel.getPracticePaymentsFilterDoctors();
                practiceCheckinFilterLocationsLabel = paymentsLabel.getPracticePaymentsFilterLocations();
                practicePaymentsFilter = paymentsLabel.getPracticePaymentsFilter();
                practicePaymentsFilterFindPatientByName = paymentsLabel.getPracticePaymentsFilterFindPatientByName();
                practicePaymentsFilterClearFilters = paymentsLabel.getPracticePaymentsFilterClearFilters();
            }
        }
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null
                && paymentsModel.getPaymentPayload().getPatientBalances().size() > 0) {

            List<PaymentsPatientBalancessDTO> patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            addProviderOnProviderFilterList(paymentsModel);
            addLocationOnFilterList(paymentsModel);
            addPatientOnFilterList(patientBalancesList);

            applyFilterSortByName(doctors);
            applyFilterSortByName(locations);
            initializePatientListView();

            setViewTextById(R.id.practice_payment_in_office_count,
                    String.format(Locale.getDefault(), "%1s", patientBalancesList.size()));
        }

        findViewById(R.id.practice_payment_find_patient).setOnClickListener(onFindPatientClick());
        findViewById(R.id.practice_payment_filter_label).setOnClickListener(onFilterIconClick());
        findViewById(R.id.practice_payment_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializePatientListView() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setPaymentsModel(paymentsModel);
        patientListView.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
                PaymentsPatientBalancessDTO paymentsPatientBalancessDTO = (PaymentsPatientBalancessDTO) dto;

                ResponsibilityDialog responsibilityDialog = new ResponsibilityDialog(getContext(), paymentsModel, paymentsPatientBalancessDTO);
                responsibilityDialog.show();
            }
        });
    }

    private void addProviderOnProviderFilterList(PaymentsModel paymentsModel) {
        doctors = new ArrayList<>();
        List<ProviderDTO> providers = paymentsModel.getPaymentPayload().getProviders();
        for (ProviderDTO provider : providers) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(provider.getId(), provider.getName(), FilterDataDTO.FilterDataType.PROVIDER);
            if (doctors.indexOf(filterDataDTO) < 0) {
                doctors.add(filterDataDTO);
            }
        }
    }

    private void addLocationOnFilterList(PaymentsModel paymentsModel) {
        this.locations = new ArrayList<>();
        List<LocationDTO> locations = paymentsModel.getPaymentPayload().getLocations();
        for (LocationDTO location : locations) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(location.getId(), location.getName(), FilterDataDTO.FilterDataType.LOCATION);
            if (this.locations.indexOf(filterDataDTO) < 0) {
                this.locations.add(filterDataDTO);
            }
        }
    }

    private void addPatientOnFilterList(List<PaymentsPatientBalancessDTO> balances) {
        patients = new ArrayList<>();
        for (PaymentsPatientBalancessDTO patientBalances : balances) {

            DemographicsSettingsPersonalDetailsPayloadDTO personalDetails
                    = patientBalances.getDemographics().getPayload().getPersonalDetails();
            String fullName = personalDetails.getFirstName() + " " + personalDetails.getLastName();
            String patientId = getPatientId(patientBalances);
            FilterDataDTO filterDataDTO = new FilterDataDTO(patientId, fullName, FilterDataDTO.FilterDataType.PATIENT);

            if (patients.indexOf(filterDataDTO) < 0) {
                patients.add(filterDataDTO);
            }
        }
    }

    private String getPatientId(PaymentsPatientBalancessDTO patientBalances) {
        List<PatienceBalanceDTO> balances = patientBalances.getBalances();
        if (balances.isEmpty()) {
            return null;
        }

        return balances.get(0).getMetadata().getPatientId();
    }

    private void applyFilterSortByName(ArrayList<FilterDataDTO> filterableList) {
        Collections.sort(filterableList, new Comparator<FilterDataDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(FilterDataDTO lhs, FilterDataDTO rhs) {
                return lhs.getDisplayText().compareTo(rhs.getDisplayText());
            }
        });
    }

    @NonNull
    private View.OnClickListener onFilterIconClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter = new FilterModel(doctors, locations, patients,
                        practiceCheckinFilterDoctorsLabel, practiceCheckinFilterLocationsLabel,
                        practicePaymentsFilter, practicePaymentsFilterFindPatientByName,
                        practicePaymentsFilterClearFilters);
                FilterDialog filterDialog = new FilterDialog(PaymentsActivity.this,
                        findViewById(R.id.activity_practice_payment), filter);

                filterDialog.showPopWindow();
            }
        };
    }

    private View.OnClickListener onFindPatientClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getFindPatient();

                FindPatientDialog findPatientDialog = new FindPatientDialog(PaymentsActivity.this,
                        transitionDTO, paymentsLabel.getPracticePaymentsDetailDialogCloseButton(),
                        paymentsLabel.getPracticePaymentsFindPatientLabel());
                findPatientDialog.show();
            }
        };
    }

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.applyFilter(filter);
    }
}
