package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.util.Log;
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
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cocampo on 2/10/17.
 */

public class PracticeAppointmentsActivity extends BasePracticeActivity
        implements FilterDialog.FilterCallBack {

    private FilterModel filter;

    private ArrayList<FilterDataDTO> patients;
    private ArrayList<FilterDataDTO> locations;
    private ArrayList<FilterDataDTO> doctors;

    private Date startDate;
    private Date endDate;

    private CheckInDTO checkInDTO;
    private CheckInLabelDTO checkInLabelDTO;

    private TwoColumnPatientListView patientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_appointments);

        initializeCheckinDto();
        initializeViews();
    }

    private void initializeCheckinDto() {
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        if (null != checkInDTO) {
            checkInLabelDTO = checkInDTO.getMetadata().getLabel();
            populateLists();
            initializePatientListView();
            initializePatientCounter();
        }
    }

    private void initializePatientCounter() {
        String patientCount = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPatients());
        setViewTextById(R.id.practice_patient_count, patientCount);

        String pendingCount = String.format(Locale.getDefault(), "%d %s", patientListView.getSizeFilteredPendingPatients(), checkInLabelDTO.getPendingAppointmentsLabel());
        setViewTextById(R.id.activity_practice_appointments_show_pending_appointments_label, pendingCount);

        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    checkInLabelDTO.getTodayLabel(),
                    checkInLabelDTO.getTomorrow(),
                    checkInLabelDTO.getThisMonthLabel(),
                    checkInLabelDTO.getNextDaysLabel()
            );
            setViewTextById(R.id.practice_patient_count_label, practiceCountLabel);
        }
    }

    private void initializePatientListView() {
        patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setCheckInDTO(checkInDTO);
        patientListView.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
                AppointmentDTO appointmentDTO = (AppointmentDTO) dto;
                PatientDTO patientDTO = appointmentDTO.getPayload().getPatient();
                String name = patientDTO.getFirstName() + " " + patientDTO.getLastName();

                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        if (checkInDTO != null) {
            CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
            if (checkInLabelDTO != null) {
                setViewTextById(R.id.practice_title, checkInLabelDTO.getActivityHeading());
                setViewTextById(R.id.practice_go_back, checkInLabelDTO.getGoBack());
                setViewTextById(R.id.activity_practice_appointments_change_date_range_label, checkInLabelDTO.getChangeDateRangeLabel());
                setViewTextById(R.id.activity_practice_appointments_show_all_appointments_label, checkInLabelDTO.getAllAppointmentsLabel());
                setViewTextById(R.id.practice_patient_count_label, checkInLabelDTO.getTodayLabel());
            }
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
                        checkInLabelDTO.getPracticeCheckinFilterDoctors(), checkInLabelDTO.getPracticeCheckinFilterLocations(),
                        checkInLabelDTO.getPracticeCheckinFilter(), checkInLabelDTO.getPracticeCheckinFilterFindPatientByName(),
                        checkInLabelDTO.getPracticeCheckinFilterClearFilters());
                FilterDialog filterDialog = new FilterDialog(getContext(),
                        findViewById(R.id.activity_practice_appointments), filter);

                filterDialog.showPopWindow();
            }
        });

        initializePracticeSelectDateRange();
//        initializeShowAllAppointments();
//        initializeShowPendingAppointments();
    }

    private void populateLists() {
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
        patientListView.applyFilter(filter);
        initializePatientCounter();
    }

    private void initializePracticeSelectDateRange() {
        findViewById(R.id.activity_practice_appointments_change_date_range_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateRangePickerDialog.DateRangePickerDialogListener callback = new DateRangePickerDialog.DateRangePickerDialogListener() {
                    @Override
                    public void onRangeSelected(Date start, Date end) {
                        PracticeAppointmentsActivity.this.startDate = start;
                        PracticeAppointmentsActivity.this.endDate = end;

                        reloadCheckInDto();
                    }
                };

                DateRangePickerDialog dialog = new DateRangePickerDialog(getContext(), checkInLabelDTO.getDateRangePickerDialogTitle(), checkInLabelDTO.getDateRangePickerDialogClose(), checkInLabelDTO.getTodayLabel(), startDate, endDate, callback);
                dialog.show();
            }
        });
    }

    private void reloadCheckInDto() {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getPracticeAppointments();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setDate(startDate).toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setDate(endDate).toStringWithFormatYyyyDashMmDashDd());

        WorkflowServiceHelper.getInstance().execute(transitionDTO, checkInCallback, queryMap);
    }

    WorkflowServiceCallback checkInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();

            DtoHelper.putExtra(getIntent(), workflowDTO);
            initializeCheckinDto();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
