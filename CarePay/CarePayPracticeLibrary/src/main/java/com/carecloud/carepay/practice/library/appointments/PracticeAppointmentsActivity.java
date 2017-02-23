package com.carecloud.carepay.practice.library.appointments;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cocampo on 2/10/17.
 */

public class PracticeAppointmentsActivity extends BasePracticeActivity
        implements FilterDialog.FilterCallBack, DateRangePickerDialog.DateRangePickerDialogListener {

    private FilterModel filter;

    private Date startDate;
    private Date endDate;

    private CheckInDTO checkInDTO;
    private CheckInLabelDTO checkInLabelDTO;

    private TwoColumnPatientListView patientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_appointments);

        filter = new FilterModel();
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
        String count = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPatients());
        setViewTextById(R.id.practice_patient_count, count);

        count = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPendingPatients());
        setViewTextById(R.id.practice_pending_count, count);

        count = String.format(Locale.getDefault(), "%d %s", patientListView.getSizeFilteredPendingPatients(), checkInLabelDTO.getPendingAppointmentsLabel());
        setViewTextById(R.id.activity_practice_appointments_show_pending_appointments_label, count);

        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    checkInLabelDTO.getTodayLabel(),
                    checkInLabelDTO.getTomorrow(),
                    checkInLabelDTO.getThisMonthLabel(),
                    checkInLabelDTO.getNextDaysLabel()
            ).toUpperCase(Locale.getDefault());
            setViewTextById(R.id.practice_patient_count_label, practiceCountLabel);
        }
    }

    private void initializePatientListView() {
        patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setCheckInDTO(checkInDTO);
        patientListView.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
//                AppointmentDTO appointmentDTO = (AppointmentDTO) dto;
//                PatientDTO patientDTO = appointmentDTO.getPayload().getPatient();
//                String name = patientDTO.getFirstName() + " " + patientDTO.getLastName();
//
//                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        if (checkInLabelDTO != null) {
            setViewTextById(R.id.practice_title, checkInLabelDTO.getActivityHeading());
            setViewTextById(R.id.practice_go_back, checkInLabelDTO.getGoBack());
            setViewTextById(R.id.activity_practice_appointments_change_date_range_label, checkInLabelDTO.getChangeDateRangeLabel());
            setViewTextById(R.id.activity_practice_appointments_show_all_appointments_label, checkInLabelDTO.getAllAppointmentsLabel());
            setViewTextById(R.id.practice_patient_count_label, checkInLabelDTO.getTodayLabel());
            setViewTextById(R.id.practice_pending_count_label, checkInLabelDTO.getPendingLabel());
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
                FilterDialog filterDialog = new FilterDialog(getContext(),
                        findViewById(R.id.activity_practice_appointments), filter,
                        checkInLabelDTO.getPracticeCheckinFilterDoctors(), checkInLabelDTO.getPracticeCheckinFilterLocations(),
                        checkInLabelDTO.getPracticeCheckinFilter(), checkInLabelDTO.getPracticeCheckinFilterFindPatientByName(),
                        checkInLabelDTO.getPracticeCheckinFilterClearFilters());

                filterDialog.showPopWindow();
            }
        });

        initializePracticeSelectDateRange();
        initializeShowAllAppointments();
        initializeShowPendingAppointments();
    }

    private void initializeShowAllAppointments() {
        findViewById(R.id.activity_practice_appointments_show_all_appointments_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewById(R.id.activity_practice_appointments_show_pending_appointments_label);
                showViewById(R.id.practice_patient_count);
                showViewById(R.id.practice_patient_count_label);
                hideViewById(R.id.activity_practice_appointments_show_all_appointments_label);
                disappearViewById(R.id.practice_pending_count);
                disappearViewById(R.id.practice_pending_count_label);
                filter.setFilteringByPending(false);
                applyFilter();
            }
        });
    }

    private void initializeShowPendingAppointments() {
        findViewById(R.id.activity_practice_appointments_show_pending_appointments_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewById(R.id.activity_practice_appointments_show_all_appointments_label);
                showViewById(R.id.practice_pending_count);
                showViewById(R.id.practice_pending_count_label);
                hideViewById(R.id.activity_practice_appointments_show_pending_appointments_label);
                disappearViewById(R.id.practice_patient_count);
                disappearViewById(R.id.practice_patient_count_label);
                filter.setFilteringByPending(true);
                applyFilter();
            }
        });
    }

    private void populateLists() {
        ArrayList<FilterDataDTO> doctors = new ArrayList<>();
        ArrayList<FilterDataDTO> locations = new ArrayList<>();
        ArrayList<FilterDataDTO> patients = new ArrayList<>();

        CheckInPayloadDTO payload = checkInDTO.getPayload();
        if (null == payload) {
            return;
        }

        List<AppointmentDTO> appointments = payload.getAppointments();
        for (AppointmentDTO appointmentDTO : appointments) {
            AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            addProviderOnProviderFilterList(doctors, appointmentPayloadDTO);
            addLocationOnFilterList(locations, appointmentPayloadDTO);
            addPatientOnFilterList(patients, appointmentPayloadDTO);
        }

        filter.setDoctors(doctors);
        filter.setLocations(locations);
        filter.setPatients(patients);
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locations, AppointmentPayloadDTO appointmentPayloadDTO) {
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

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patients, AppointmentPayloadDTO appointmentPayloadDTO) {
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

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctors, AppointmentPayloadDTO appointmentPayloadDTO) {
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

    @Override
    public void applyFilter() {
        patientListView.applyFilter(filter);
        initializePatientCounter();
    }

    private void initializePracticeSelectDateRange() {
        findViewById(R.id.activity_practice_appointments_change_date_range_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tag = DateRangePickerDialog.class.getSimpleName();

                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(tag);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                        checkInLabelDTO.getDateRangePickerDialogTitle(),
                        checkInLabelDTO.getDateRangePickerDialogClose(),
                        checkInLabelDTO.getTodayLabel(),
                        true,
                        startDate,
                        endDate,
                        DateRangePickerDialog.getPreviousSixMonthCalendar(),
                        DateRangePickerDialog.getNextSixMonthCalendar(),
                        PracticeAppointmentsActivity.this
                );
                dialog.show(ft, tag);
            }
        });
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getPracticeAppointments();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setDate(startDate).toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setDate(endDate).toStringWithFormatYyyyDashMmDashDd());

        getWorkflowServiceHelper().execute(transitionDTO, workflowServiceCallback, queryMap);
    }

    @Override
    public void onDateRangeCancelled() {

    }

    WorkflowServiceCallback workflowServiceCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            DtoHelper.putExtra(getIntent(), workflowDTO);
            initializeCheckinDto();
            applyFilter();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
