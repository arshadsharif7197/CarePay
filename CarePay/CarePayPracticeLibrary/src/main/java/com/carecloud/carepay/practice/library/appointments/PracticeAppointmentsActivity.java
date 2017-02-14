package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by cocampo on 2/10/17.
 */

public class PracticeAppointmentsActivity extends BasePracticeActivity
        implements FilterDialog.FilterCallBack {

    private FilterModel filter;

    private ArrayList<FilterDataDTO> patients;
    private ArrayList<FilterDataDTO> locations;
    private ArrayList<FilterDataDTO> doctors;

    private String practiceCheckinFilterDoctorsLabel;
    private String practiceCheckinFilterLocationsLabel;
    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_appointments);

        CheckInDTO checkInDTO = getConvertedDTO(CheckInDTO.class);
        if (null != checkInDTO) {
            populateLists(checkInDTO);
            initializePatientListView(checkInDTO);
        }

        initializeViews(checkInDTO);
    }

    private void initializePatientListView(CheckInDTO checkInDTO) {
        TwoColumnPatientListView purchaseFragment = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        purchaseFragment.setCheckInDTO(checkInDTO);
        purchaseFragment.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
                AppointmentDTO appointmentDTO = (AppointmentDTO) dto;
                PatientDTO patientDTO = appointmentDTO.getPayload().getPatient();
                String name = patientDTO.getFirstName() + " " + patientDTO.getLastName();

                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews(CheckInDTO checkInDTO) {
        if (checkInDTO != null) {
            CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
            if (checkInLabelDTO != null) {
                setViewTextById(R.id.practice_title, checkInLabelDTO.getGoBack());
                setViewTextById(R.id.practice_go_back, checkInLabelDTO.getGoBack());
                setViewTextById(R.id.practice_patient_count_label, "TO-DO");
            }

            String patientCount = String.format(Locale.getDefault(), "%1s", checkInDTO.getPayload().getAppointments().size());
            setViewTextById(R.id.practice_patient_count, patientCount);
        }

        findViewById(R.id.practice_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.practice_filter_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter = new FilterModel(doctors, locations, patients,
                        practiceCheckinFilterDoctorsLabel, practiceCheckinFilterLocationsLabel,
                        practicePaymentsFilter, practicePaymentsFilterFindPatientByName,
                        practicePaymentsFilterClearFilters);
                FilterDialog filterDialog = new FilterDialog(getContext(),
                        findViewById(R.id.activity_practice_appointments), filter);

                filterDialog.showPopWindow();
            }
        });
    }

    private void populateLists(CheckInDTO checkInDTO) {
        CheckInPayloadDTO payload = checkInDTO.getPayload();
        if (null == payload) {
            return;
        }

        doctors = new ArrayList<>();
        locations = new ArrayList<>();
        patients = new ArrayList<>();

        List<AppointmentDTO> appointments = payload.getAppointments();
        for (AppointmentDTO appointmentDTO : appointments) {
            AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            addProviderOnProviderFilterList(appointmentPayloadDTO);
            addLocationOnFilterList(appointmentPayloadDTO);
            addPatientOnFilterList(appointmentPayloadDTO);
        }

        applyFilterSortByName(patients);
        applyFilterSortByName(doctors);
        applyFilterSortByName(locations);
    }

    private void addLocationOnFilterList(AppointmentPayloadDTO appointmentPayloadDTO) {
        LocationDTO locationDTO = appointmentPayloadDTO.getLocation();
        FilterDataDTO filterDataDTO = new FilterDataDTO(locationDTO.getId(), locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if (locations.indexOf(filterDataDTO) < 0) {
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            locations.add(filterDataDTO);
        } else {
            filterDataDTO = locations.get(locations.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addPatientOnFilterList(AppointmentPayloadDTO appointmentPayloadDTO) {
        PatientDTO patientDTO = appointmentPayloadDTO.getPatient();
        FilterDataDTO filterDataDTO = new FilterDataDTO(patientDTO.getId(), patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
        if (patients.indexOf(filterDataDTO) < 0) {
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            patients.add(filterDataDTO);
        } else {
            filterDataDTO = patients.get(patients.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addProviderOnProviderFilterList(AppointmentPayloadDTO appointmentPayloadDTO) {
        ProviderDTO providerDTO = appointmentPayloadDTO.getProvider();
        FilterDataDTO filterDataDTO = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
        if (doctors.indexOf(filterDataDTO) < 0) {
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            doctors.add(filterDataDTO);
        } else {
            filterDataDTO = doctors.get(doctors.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void applyFilterSortByName(ArrayList<FilterDataDTO> filterableList) {
        Collections.sort(filterableList, new Comparator<FilterDataDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(FilterDataDTO lhs, FilterDataDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getDisplayText().compareTo(rhs.getDisplayText());
                }
                return -1;
            }
        });
    }

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.applyFilter(filter);
    }
}
