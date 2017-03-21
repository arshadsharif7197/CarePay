package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.dtos.PracticeAppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
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

public class PracticeModePracticeAppointmentsActivity extends BasePracticeAppointmentsActivity
        implements FilterDialog.FilterDialogListener,
        DateRangePickerDialog.DateRangePickerDialogListener,
        PracticeAppointmentDialog.PracticeAppointmentDialogListener,
        PaymentNavigationCallback, ResponsibilityFragmentDialog.PayResponsibilityCallback {

    private FilterModel filter;

    private Date startDate;
    private Date endDate;

    private CheckInDTO checkInDTO;
    private CheckInLabelDTO checkInLabelDTO;

    private TwoColumnPatientListView patientListView;
    private boolean needsToConfirmAppointmentCreation;

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
        setTextViewById(R.id.practice_patient_count, count);

        count = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPendingPatients());
        setTextViewById(R.id.practice_pending_count, count);

        count = String.format(Locale.getDefault(), "%d %s", patientListView.getSizeFilteredPendingPatients(), checkInLabelDTO.getPendingAppointmentsLabel());
        setTextViewById(R.id.activity_practice_appointments_show_pending_appointments_label, count);

        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    checkInLabelDTO.getTodayLabel(),
                    checkInLabelDTO.getTomorrowLabel(),
                    checkInLabelDTO.getThisMonthLabel(),
                    checkInLabelDTO.getNextDaysLabel()
            ).toUpperCase(Locale.getDefault());
            setTextViewById(R.id.practice_patient_count_label, practiceCountLabel);
        }
    }

    private void initializePatientListView() {
        patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setCheckInDTO(checkInDTO);
        patientListView.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {

                showPracticeAppointmentDialog((AppointmentDTO) dto);
            }
        });
    }

    private void initializeViews() {
        TextView findPatientTextView = (TextView) findViewById(R.id.practice_find_patient);
        findPatientTextView.setOnClickListener(getFindPatientListener(true));

        TextView addAppointmentTextView = (TextView) findViewById(R.id.activity_practice_appointments_add);
        addAppointmentTextView.setOnClickListener(getFindPatientListener(false));

        if (checkInLabelDTO != null) {
            setTextViewById(R.id.practice_go_back, checkInLabelDTO.getGoBack());
            setTextViewById(R.id.activity_practice_appointments_change_date_range_label, checkInLabelDTO.getChangeDateRangeLabel());
            setTextViewById(R.id.activity_practice_appointments_show_all_appointments_label, checkInLabelDTO.getAllAppointmentsLabel());
            setTextViewById(R.id.practice_patient_count_label, checkInLabelDTO.getTodayLabel());
            setTextViewById(R.id.practice_pending_count_label, checkInLabelDTO.getPendingLabel());
            setTextViewById(R.id.practice_filter_label, checkInLabelDTO.getPracticeCheckinFilter());
            findPatientTextView.setText(checkInLabelDTO.getPracticeCheckinFilterFindPatient());
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

        Map<String, String> photoMap = PracticeUtil.getProfilePhotoMap(checkInDTO.getPayload().getPatientBalances());

        CheckInPayloadDTO payload = checkInDTO.getPayload();
        if (null == payload) {
            return;
        }

        List<AppointmentDTO> appointments = payload.getAppointments();
        for (AppointmentDTO appointmentDTO : appointments) {
            AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            addProviderOnProviderFilterList(doctors, appointmentPayloadDTO);
            addLocationOnFilterList(locations, appointmentPayloadDTO);
            addPatientOnFilterList(patients, appointmentPayloadDTO, photoMap);
        }

        filter.setDoctors(doctors);
        filter.setLocations(locations);
        filter.setPatients(patients);
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locations, AppointmentPayloadDTO appointmentPayloadDTO) {
        LocationDTO locationDTO = appointmentPayloadDTO.getLocation();
        FilterDataDTO filterDataDTO = new FilterDataDTO(locationDTO.getId(), locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if (locations.indexOf(filterDataDTO) < 0) {
            locations.add(filterDataDTO);
        }
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patients, AppointmentPayloadDTO appointmentPayloadDTO, Map<String, String> photoMap) {
        PatientModel patientDTO = appointmentPayloadDTO.getPatient();
        FilterDataDTO filterDataDTO = new FilterDataDTO(patientDTO.getPatientId(), patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
        if (patients.indexOf(filterDataDTO) < 0) {
            patients.add(filterDataDTO);
        } else {
            filterDataDTO = patients.get(patients.indexOf(filterDataDTO));
        }

        if (StringUtil.isNullOrEmpty(filterDataDTO.getImageURL())) {
            filterDataDTO.setImageURL(photoMap.get(filterDataDTO.getId()));
        }
    }

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctors, AppointmentPayloadDTO appointmentPayloadDTO) {
        ProviderDTO providerDTO = appointmentPayloadDTO.getProvider();
        FilterDataDTO filterDataDTO = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
        if (doctors.indexOf(filterDataDTO) < 0) {
            doctors.add(filterDataDTO);
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
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
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
                        PracticeModePracticeAppointmentsActivity.this
                );
                dialog.show(ft, tag);
            }
        });
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onAppointmentRequestSuccess();
    }

    @Override
    public void onDateRangeCancelled() {

    }

    private View.OnClickListener getFindPatientListener(final boolean needsConfirmation) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                needsToConfirmAppointmentCreation = needsConfirmation;

                TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getFindPatient();

                FindPatientDialog findPatientDialog = new FindPatientDialog(getContext(),
                        transitionDTO,
                        checkInLabelDTO.getPracticeCheckinFilterFindPatient());
                findPatientDialog.setClickedListener(getFindPatientDialogListener());
                findPatientDialog.show();
            }
        };
    }

    private FindPatientDialog.OnItemClickedListener getFindPatientDialogListener() {
        return new FindPatientDialog.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientModel patient) {
                setPatientId(patient.getPatientId());

                if (needsToConfirmAppointmentCreation) {
                    getPatientBalances(patient);
                } else {
                    newAppointment();
                }
            }

            private void getPatientBalances(PatientModel patient) {
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("patient_id", patient.getPatientId());

                TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPatientBalances();
                getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);
            }
        };
    }

    WorkflowServiceCallback allAppointmentsServiceCallback = new WorkflowServiceCallback() {
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

    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PaymentsModel patientDetails = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());

            String tag = ResponsibilityFragmentDialog.class.getSimpleName();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
                    .newInstance(patientDetails, null, Label.getLabel("create_appointment_label"));
            dialog.show(ft, tag);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior(getContext(), exceptionMessage);
        }
    };

    private void showPracticeAppointmentDialog(AppointmentDTO appointmentDTO) {
        int headerColor = R.color.colorPrimary;
        String leftAction = null;
        String rightAction = null;
        AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
        if (appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            headerColor = R.color.lightningyellow;
            leftAction = checkInLabelDTO.getRejectLabel();
            rightAction = checkInLabelDTO.getAcceptLabel();

        } else if (appointmentPayloadDTO.isAppointmentOver()) {
            // Doing nothing for now
        } else {
            leftAction = checkInLabelDTO.getCancelAppointmentLabel();
        }

        String tag = PracticeAppointmentDialog.class.getSimpleName();

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PracticeAppointmentDialog dialog = PracticeAppointmentDialog.newInstance(
                headerColor,
                checkInLabelDTO.getDateRangePickerDialogClose(),
                checkInLabelDTO.getTodayLabel(),
                checkInLabelDTO.getVisitTypeHeading(),
                leftAction,
                rightAction,
                appointmentDTO,
                PracticeModePracticeAppointmentsActivity.this
        );
        dialog.show(ft, tag);
    }

    private void confirmAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getConfirmAppointment();
        transitionAppointment(transitionDTO, appointmentDTO);
    }

    private void cancelAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getCancelAppointment();
        transitionAppointment(transitionDTO, appointmentDTO);
    }

    private void transitionAppointment(TransitionDTO transitionDTO, AppointmentDTO appointmentDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());

        getWorkflowServiceHelper().execute(transitionDTO, oneAppointmentsServiceCallback, queryMap);
    }

    WorkflowServiceCallback oneAppointmentsServiceCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            updateAppointment(workflowDTO);

            DtoHelper.putExtra(getIntent(), checkInDTO);
            initializeCheckinDto();
            applyFilter();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }

        private void updateAppointment(WorkflowDTO workflowDTO) {
            CheckInPayloadDTO payload = checkInDTO.getPayload();
            if (null == payload) {
                return;
            }

            PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper.getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);
            AppointmentDTO newAppointmentDTO = practiceAppointmentDTO.getPayload().getPracticeAppointments();

            List<AppointmentDTO> appointments = payload.getAppointments();
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentDTO oldAppointmentDTO = appointments.get(i);
                if (oldAppointmentDTO.getPayload().getId().equalsIgnoreCase(newAppointmentDTO.getPayload().getId())) {

                    appointments.remove(i);

                    if (!newAppointmentDTO.getPayload().getAppointmentStatus().getCode().equals(CarePayConstants.CANCELLED)) {
                        appointments.add(newAppointmentDTO);
                    }

                    break;
                }
            }
        }
    };

    @Override
    public void startPartialPayment() {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentMethodAction(String selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentPlanAction() {

    }

    @Override
    public void showReceipt(PaymentsModel paymentsModel) {

    }

    @Override
    public void showAddCard(double amount) {

    }

    @Override
    public void onAppointmentRequestSuccess() {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getPracticeAppointments();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", getFormattedDate(startDate));
        queryMap.put("end_date", getFormattedDate(endDate));

        getWorkflowServiceHelper().execute(transitionDTO, allAppointmentsServiceCallback, queryMap);
    }

    private String getFormattedDate(Date date) {
        DateUtil dateUtil = DateUtil.getInstance();

        if (date == null) {
            dateUtil = dateUtil.setToCurrent();
        } else {
            dateUtil = dateUtil.setDate(date);
        }

        return dateUtil.toStringWithFormatYyyyDashMmDashDd();
    }

    @Override
    protected AppointmentLabelDTO getLabels() {
        return checkInLabelDTO;
    }

    @Override
    protected TransitionDTO getMakeAppointmentTransition() {
        return checkInDTO.getMetadata().getTransitions().getMakeAppointment();
    }

    @Override
    protected LinksDTO getLinks() {
        return checkInDTO.getMetadata().getLinks();
    }

    @Override
    public void onLeftActionTapped() {

    }

    @Override
    public void onLeftActionTapped(AppointmentDTO appointmentDTO) {
        cancelAppointment(appointmentDTO);
    }

    @Override
    public void onRightActionTapped(PaymentsModel paymentsModel, double amount) {
        newAppointment();
    }

    @Override
    public void onRightActionTapped(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getPayload().getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            confirmAppointment(appointmentDTO);
        }
    }

    @Override
    public void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem) {
        String tag = PaymentDetailsFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem);
        dialog.show(ft, tag);
    }
}
