package com.carecloud.carepay.practice.library.payments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterPopupWindow;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.payments.adapter.PracticePaymentsAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityDialog;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class PaymentsActivity extends BasePracticeActivity implements CustomFilterPopupWindow.FilterCallBack {

    private PaymentsModel paymentsModel;
    private List<PaymentsPatientBalancessDTO> patientBalancesList;

    private boolean isFilterOn;
    private ArrayList<FilterDataDTO> patientList;
    private ArrayList<FilterDataDTO> searchedPatientList = new ArrayList<>();
    private ArrayList<FilterDataDTO> filterableDoctorLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            if (paymentsLabel != null) {
                ((CarePayTextView) findViewById(R.id.practice_payment_title)).setText(paymentsLabel.getPracticePaymentsHeader());
                ((CarePayTextView) findViewById(R.id.practice_payment_go_back)).setText(paymentsLabel.getPracticePaymentsBackLabel());
                ((CarePayTextView) findViewById(R.id.practice_payment_find_patient)).setText(paymentsLabel.getPracticePaymentsFindPatientLabel());
                ((CarePayTextView) findViewById(R.id.practice_payment_filter_label)).setText(paymentsLabel.getPracticePaymentsFilter());
                ((CarePayTextView) findViewById(R.id.practice_payment_in_office_label)).setText(paymentsLabel.getPracticePaymentsInOffice());
            }
        }
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null
                && paymentsModel.getPaymentPayload().getPatientBalances().size() > 0) {

            patientList = new ArrayList<>();
            ArrayList<FilterDataDTO> doctorsList = new ArrayList<>();
            ArrayList<FilterDataDTO> locationsList = new ArrayList<>();

            patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            addProviderOnProviderFilterList(doctorsList, paymentsModel);
            addLocationOnFilterList(locationsList, paymentsModel);
            addPatientOnFilterList(patientList, patientBalancesList);

            setFilterableData(doctorsList, locationsList);
            setAdapter();

            ((CarePayTextView) findViewById(R.id.practice_payment_in_office_count))
                    .setText(String.format(Locale.getDefault(), "%1s", patientBalancesList.size()));
        }

        findViewById(R.id.practice_payment_filter_label).setOnClickListener(onFilterIconClick());
        findViewById(R.id.practice_payment_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setAdapter() {
        RecyclerView inOfficeList = (RecyclerView) findViewById(R.id.practice_payment_in_office_list);
        inOfficeList.setHasFixedSize(true);
        inOfficeList.setLayoutManager(new GridLayoutManager(this, 2));

        PracticePaymentsAdapter paymentsAdapter = new PracticePaymentsAdapter(this, paymentsModel);
        inOfficeList.setAdapter(paymentsAdapter);
    }

    private void setFilterableData(ArrayList<FilterDataDTO> doctorsList, ArrayList<FilterDataDTO> locationsList) {
        applyFilterSortByName(doctorsList);
        applyFilterSortByName(locationsList);

        PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
        filterableDoctorLocationList.add(new FilterDataDTO(paymentsLabel.getPracticePaymentsFilterDoctors(),
                FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(doctorsList);

        filterableDoctorLocationList.add(new FilterDataDTO(paymentsLabel.getPracticePaymentsFilterLocations(),
                FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(locationsList);
    }

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctorsList, PaymentsModel paymentsModel) {
        FilterDataDTO filterDataDTO;
        List<ProviderDTO> providers = paymentsModel.getPaymentPayload().getProviders();
        for (ProviderDTO provider : providers) {
            filterDataDTO = new FilterDataDTO(provider.getName(), FilterDataDTO.FilterDataType.PROVIDER);
            if (doctorsList.indexOf(filterDataDTO) < 0) {
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
                doctorsList.add(filterDataDTO);
            } else {
//                filterDataDTO = doctorsList.get(doctorsList.indexOf(filterDataDTO));
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            }
        }
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locationsList, PaymentsModel paymentsModel) {
        FilterDataDTO filterDataDTO;
        List<LocationDTO> locations = paymentsModel.getPaymentPayload().getLocations();
        for (LocationDTO location : locations) {
            filterDataDTO = new FilterDataDTO(location.getName(), FilterDataDTO.FilterDataType.LOCATION);
            if (locationsList.indexOf(filterDataDTO) < 0) {
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
                locationsList.add(filterDataDTO);
            } else {
//                filterDataDTO = locationsList.get(locationsList.indexOf(filterDataDTO));
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            }
        }
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patientsList, List<PaymentsPatientBalancessDTO> patientBalancesList) {
        FilterDataDTO filterDataDTO;
        for (PaymentsPatientBalancessDTO patientBalances : patientBalancesList) {

            DemographicsSettingsPersonalDetailsPayloadDTO personalDetails
                    = patientBalances.getDemographics().getPayload().getPersonalDetails();
            String fullName = personalDetails.getFirstName() + " " + personalDetails.getLastName();
            filterDataDTO = new FilterDataDTO(fullName, FilterDataDTO.FilterDataType.PATIENT);

            if (patientsList.indexOf(filterDataDTO) < 0) {
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
                patientsList.add(filterDataDTO);
            } else {
//                filterDataDTO = patientsList.get(patientsList.indexOf(filterDataDTO));
//                filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            }
        }
    }

    public void onPatientItemClick(int selectedIndex) {
        ResponsibilityDialog responsibilityDialog = new ResponsibilityDialog(this, paymentsModel, selectedIndex);
        responsibilityDialog.show();
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
                CustomFilterPopupWindow customFilterPopupWindow
                        = new CustomFilterPopupWindow(PaymentsActivity.this,
                        findViewById(R.id.activity_practice_payment), filterableDoctorLocationList,
                        patientList, searchedPatientList);

                PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
                if (paymentsLabel != null) {
                    customFilterPopupWindow.setTitle(paymentsLabel.getPracticePaymentsFilter());
                    customFilterPopupWindow.setSearchHint(paymentsLabel.getPracticePaymentsFilterFindPatientByName());
                    customFilterPopupWindow.setClearFiltersButtonText(paymentsLabel.getPracticePaymentsFilterClearFilters());
                }
                customFilterPopupWindow.showPopWindow();
                customFilterPopupWindow.showClearFilterButton(isFilterOn);
            }
        };
    }

    @Override
    public void applyFilter(HashSet<String> patientBalances) {
        if (patientBalances.size() == patientBalancesList.size()) {
            isFilterOn = false;
        } else {
            isFilterOn = true;
        }

        setAdapter();
    }
}
