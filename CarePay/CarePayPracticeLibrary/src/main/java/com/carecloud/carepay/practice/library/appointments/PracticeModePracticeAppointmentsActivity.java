package com.carecloud.carepay.practice.library.appointments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.AdHocFormsListFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.CancelAppointmentConfirmDialogFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.dtos.PracticeAppointmentDTO;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.FormsResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by cocampo on 2/10/17
 */
public class PracticeModePracticeAppointmentsActivity extends BasePracticeAppointmentsActivity
        implements FilterDialog.FilterDialogListener,
        DateRangePickerDialog.DateRangePickerDialogListener,
        PracticeAppointmentDialog.PracticeAppointmentDialogListener,
        ResponsibilityFragmentDialog.PayResponsibilityCallback,
        PaymentDetailInterface,
        CancelAppointmentConfirmDialogFragment.CancelAppointmentCallback {

    private FilterModel filter;

    private Date startDate;
    private Date endDate;

    private CheckInDTO checkInDTO;

    private TwoColumnPatientListView patientListView;
    private boolean needsToConfirmAppointmentCreation;
    private boolean wasCalledFromThisClass;
    private String confirmationMessageText;
    private boolean updateOnSuccess;

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

        count = String.format(Locale.getDefault(), "%d %s", patientListView.getSizeFilteredPendingPatients(),
                Label.getLabel("pending_appointments_label"));
        setTextViewById(R.id.activity_practice_appointments_show_pending_appointments_label, count);

        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    Label.getLabel("today_label"),
                    Label.getLabel("tomorrow_label"),
                    Label.getLabel("this_month_label"),
                    Label.getLabel("next_days_label"),
                    true).toUpperCase(Locale.getDefault());
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
        applyFilter();
    }

    private void initializeViews() {
        TextView findPatientTextView = (TextView) findViewById(R.id.practice_find_patient);
        findPatientTextView.setOnClickListener(getFindPatientListener(true));

        TextView addAppointmentTextView = (TextView) findViewById(R.id.activity_practice_appointments_add);
        addAppointmentTextView.setOnClickListener(getFindPatientListener(false));
        addAppointmentTextView.setEnabled(checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions().canScheduleAppointment);

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
                        Label.getLabel("practice_checkin_filter"),
                        Label.getLabel("practice_checkin_filter_find_patient_by_name"),
                        Label.getLabel("practice_checkin_filter_clear_filters"));

                filterDialog.showPopWindow();
            }
        });

        initializePracticeSelectDateRange();
        initializeShowAllAppointments();
        initializeShowPendingAppointments();
    }

    private void initializeShowAllAppointments() {
        findViewById(R.id.activity_practice_appointments_show_all_appointments_label)
                .setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.activity_practice_appointments_show_pending_appointments_label)
                .setOnClickListener(new View.OnClickListener() {
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
        ArrayList<FilterDataDTO> patients = new ArrayList<>();

        Map<String, String> photoMap = PracticeUtil
                .getProfilePhotoMap(checkInDTO.getPayload().getPatientBalances());

        CheckInPayloadDTO payload = checkInDTO.getPayload();
        if (null == payload) {
            return;
        }
        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> providersSavedFilteredIds = getApplicationPreferences()
                .getSelectedProvidersIds(practiceId, userId);
        Set<String> locationsSavedFilteredIds = getApplicationPreferences()
                .getSelectedLocationsIds(practiceId, userId);

        List<AppointmentDTO> appointments = payload.getAppointments();
        for (AppointmentDTO appointmentDTO : appointments) {
            AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            addPatientOnFilterList(patients, appointmentPayloadDTO, photoMap);
        }

        filter.setDoctors(getFilterProviders(providersSavedFilteredIds));
        filter.setLocations(getFilterLocations(locationsSavedFilteredIds));
        filter.setPatients(patients);
    }

    private ArrayList<FilterDataDTO> getFilterLocations(Set<String> selectedLocationsIds) {
        ArrayList<FilterDataDTO> locations = new ArrayList<>();
        for (LocationDTO locationDTO : checkInDTO.getPayload().getLocations()) {
            FilterDataDTO filterLocation = new FilterDataDTO(locationDTO.getId(), locationDTO.getName(),
                    FilterDataDTO.FilterDataType.LOCATION);
            if (selectedLocationsIds != null
                    && selectedLocationsIds.contains(String.valueOf(filterLocation.getId()))) {
                filterLocation.setChecked(true);

            }
            locations.add(filterLocation);
        }
        return locations;
    }

    private ArrayList<FilterDataDTO> getFilterProviders(Set<String> selectedProvidersIds) {
        ArrayList<FilterDataDTO> providers = new ArrayList<>();
        for (ProviderDTO providerDTO : checkInDTO.getPayload().getProviders()) {
            FilterDataDTO filterProvider = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(),
                    FilterDataDTO.FilterDataType.PROVIDER);
            if (selectedProvidersIds != null && selectedProvidersIds
                    .contains(String.valueOf(filterProvider.getId()))) {
                filterProvider.setChecked(true);
            }
            providers.add(filterProvider);
        }
        return providers;
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patients,
                                        AppointmentsPayloadDTO appointmentPayloadDTO,
                                        Map<String, String> photoMap) {
        PatientModel patientDTO = appointmentPayloadDTO.getPatient();
        FilterDataDTO filterDataDTO = new FilterDataDTO(patientDTO.getPatientId(),
                patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
        if (patients.indexOf(filterDataDTO) < 0) {
            patients.add(filterDataDTO);
        } else {
            filterDataDTO = patients.get(patients.indexOf(filterDataDTO));
        }

        if (StringUtil.isNullOrEmpty(filterDataDTO.getImageURL())) {
            filterDataDTO.setImageURL(photoMap.get(filterDataDTO.getId()));
        }
    }

    @Override
    public void applyFilter() {
        patientListView.applyFilter(filter);
        initializePatientCounter();
    }

    @Override
    public void refreshData() {
        onAppointmentRequestSuccess();
    }

    private void initializePracticeSelectDateRange() {
        findViewById(R.id.activity_practice_appointments_change_date_range_label)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                                Label.getLabel("date_range_picker_dialog_title"),
                                Label.getLabel("date_range_picker_dialog_close"),
                                true,
                                startDate,
                                endDate,
                                DateRangePickerDialog.getPreviousSixMonthCalendar(),
                                DateRangePickerDialog.getNextSixMonthCalendar(),
                                PracticeModePracticeAppointmentsActivity.this,
                                CalendarPickerView.SelectionMode.RANGE.name());
                        displayDialogFragment(dialog, false);

                        wasCalledFromThisClass = true;
                    }
                });
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        if (!wasCalledFromThisClass) {
            super.onRangeSelected(start, end);
        } else {
            this.startDate = start;
            this.endDate = end;

            onAppointmentRequestSuccess();

            wasCalledFromThisClass = false;
        }
    }

    @Override
    public void onDateRangeCancelled() {
        if (!wasCalledFromThisClass) {
            super.onDateRangeCancelled();
        } else {
            wasCalledFromThisClass = false;
        }
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        //Not Implemented
    }

    private View.OnClickListener getFindPatientListener(final boolean needsConfirmation) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                needsToConfirmAppointmentCreation = needsConfirmation;
                TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getFindPatient();
                FindPatientDialog findPatientDialog = new FindPatientDialog(getContext(),
                        transitionDTO,
                        Label.getLabel(needsConfirmation ? "practice_checkin_filter_find_patient_by_name"
                                : "practice_filter_find_patient_first"));
                findPatientDialog.setClickedListener(getFindPatientDialogListener());
                findPatientDialog.show();
            }
        };
    }

    private FindPatientDialog.OnItemClickedListener getFindPatientDialogListener() {
        return new FindPatientDialog.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientModel patient) {
                setPatient(patient);
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
            showErrorNotification(exceptionMessage);
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
            PaymentsModel patientDetails = DtoHelper
                    .getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            showResponsibilityFragment(patientDetails);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    public void showPracticeAppointmentDialog(AppointmentDTO appointmentDTO) {
        AppointmentDisplayStyle dialogStyle = AppointmentDisplayStyle.DEFAULT;
        AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        if (appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            dialogStyle = AppointmentDisplayStyle.REQUESTED;

        } else if (appointmentPayloadDTO.canCheckOut()) {
            dialogStyle = AppointmentDisplayStyle.CHECKED_IN;

        } else if (appointmentPayloadDTO.isAppointmentOver() || appointmentPayloadDTO.isAppointmentFinished()) {
            //todo finished appt options, Doing nothing for now

        } else if (appointmentPayloadDTO.canCheckIn()) {
            dialogStyle = AppointmentDisplayStyle.PENDING;

        }

        PracticeAppointmentDialog dialog = PracticeAppointmentDialog.newInstance(
                dialogStyle,
                appointmentDTO,
                checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions(),
                PracticeModePracticeAppointmentsActivity.this
        );
        displayDialogFragment(dialog, true);
        setPatient(appointmentDTO.getPayload().getPatient());
    }

    private void confirmAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getConfirmAppointment();
        confirmationMessageText = "appointment_schedule_success_message_HTML";
        transitionAppointment(transitionDTO, appointmentDTO, true, getString(R.string.event_appointment_accepted));
    }

    private void rejectAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getDismissAppointment();
        confirmationMessageText = "appointment_rejection_success_message_HTML";
        transitionAppointment(transitionDTO, appointmentDTO, false, getString(R.string.event_appointment_rejected));
    }

    private void transitionAppointment(TransitionDTO transitionDTO,
                                       AppointmentDTO appointmentDTO,
                                       boolean updateOnSuccess,
                                       String event) {
        this.updateOnSuccess = updateOnSuccess;

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());

        getWorkflowServiceHelper().execute(transitionDTO, oneAppointmentsServiceCallback, queryMap);

        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_patient_id)};
        String[] values = {appointmentDTO.getPayload().getVisitType().getName(),
                getApplicationMode().getUserPracticeDTO().getPracticeId(),
                getApplicationMode().getUserPracticeDTO().getPracticeName(),
                appointmentDTO.getMetadata().getPatientId()};
        MixPanelUtil.logEvent(event, params, values);
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
            showSuccessToast(Label.getLabel(confirmationMessageText));
            DtoHelper.putExtra(getIntent(), checkInDTO);
            initializeCheckinDto();
            applyFilter();

            updateOnSuccess = false;
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

            updateOnSuccess = false;
        }

        private void updateAppointment(WorkflowDTO workflowDTO) {
            CheckInPayloadDTO payload = checkInDTO.getPayload();
            if (null == payload) {
                return;
            }

            PracticeAppointmentDTO practiceAppointmentDTO = DtoHelper
                    .getConvertedDTO(PracticeAppointmentDTO.class, workflowDTO);
            AppointmentDTO newAppointmentDTO = practiceAppointmentDTO.getPayload().getPracticeAppointments();

            List<AppointmentDTO> appointments = payload.getAppointments();
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentDTO oldAppointmentDTO = appointments.get(i);
                if (oldAppointmentDTO.getPayload().getId()
                        .equalsIgnoreCase(newAppointmentDTO.getPayload().getId())) {

                    appointments.remove(i);

                    if (updateOnSuccess) {
                        appointments.add(newAppointmentDTO);
                    }

                    break;
                }
            }
        }
    };


    private void showResponsibilityFragment(PaymentsModel paymentsModel) {
        String tag = ResponsibilityFragmentDialog.class.getName();
        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newPatientHeader(paymentsModel);
        FormsResponsibilityFragmentDialog dialog = FormsResponsibilityFragmentDialog
                .newInstance(paymentsModel,
                        checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions(),
                        headerModel, paymentsModel.getPaymentPayload().getPatientBalances().get(0));
        dialog.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onAppointmentRequestSuccess() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", getFormattedDate(startDate));
        queryMap.put("end_date", getFormattedDate(endDate));

        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

        if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
            queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
        }

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getPracticeAppointments();
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
    protected TransitionDTO getMakeAppointmentTransition() {
        return checkInDTO.getMetadata().getTransitions().getMakeAppointment();
    }

    @Override
    protected LinksDTO getLinks() {
        return checkInDTO.getMetadata().getLinks();
    }

    @Override
    public void onLeftActionTapped(PaymentsModel paymentsModel, double owedAmount) {
        getAllPracticeForms(getPatient().getPatientId(), null, paymentsModel);
    }

    @Override
    public void onLeftActionTapped(AppointmentDTO appointmentDTO) {
        AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        if (appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            rejectAppointment(appointmentDTO);
        } else if (appointmentPayloadDTO.isAppointmentOver()) {
            //TODO Add future logic
        } else {
            CancelAppointmentConfirmDialogFragment fragment = CancelAppointmentConfirmDialogFragment
                    .newInstance(appointmentDTO);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
        }
    }

    @Override
    public void onRightActionTapped(PaymentsModel paymentsModel, double amount) {
        newAppointment();
    }

    @Override
    public void onMiddleActionTapped(PaymentsModel paymentsModel, double amount) {

    }

    @Override
    public void onRightActionTapped(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getPayload().getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            confirmAppointment(appointmentDTO);
        } else if (appointmentDTO.getPayload().canCheckIn()) {
            launchPatientModeCheckin(appointmentDTO);
        } else if (appointmentDTO.getPayload().canCheckOut()) {
            launchPatientModeCheckout(appointmentDTO);
        }
    }

    @Override
    public void onMiddleActionTapped(AppointmentDTO appointmentDTO) {
        getAllPracticeForms(appointmentDTO.getMetadata().getPatientId(), appointmentDTO, null);
    }

    private void getAllPracticeForms(String patientId, AppointmentDTO appointmentDTO, PaymentsModel paymentsModel) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientId);
        TransitionDTO adHocForms = checkInDTO.getMetadata().getLinks().getAllPracticeForms();
        getWorkflowServiceHelper().execute(adHocForms, getAdHocServiceCallback(patientId, appointmentDTO, paymentsModel), queryMap);
    }

    WorkflowServiceCallback getAdHocServiceCallback(final String patientId, final AppointmentDTO appointmentDTO, final PaymentsModel paymentsModel) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Gson gson = new Gson();
                AppointmentsResultModel appointmentsResultModel = gson
                        .fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                AdHocFormsListFragment fragment = AdHocFormsListFragment
                        .newInstance(appointmentsResultModel, patientId);
                displayDialogFragment(fragment, false);
                fragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!isVisible()) {
                            return;
                        }
                        if (appointmentDTO != null) {
                            showPracticeAppointmentDialog(appointmentDTO);
                        } else if (paymentsModel != null) {
                            showResponsibilityFragment(paymentsModel);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    @Override
    public void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, false);
        displayDialogFragment(dialog, false);
    }


    private void launchPatientModeCheckin(AppointmentDTO appointmentDTO) {
        getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKIN);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());
        TransitionDTO checkinPatientTransition = checkInDTO.getMetadata()
                .getTransitions().getCheckinPatientMode();
        getWorkflowServiceHelper().execute(checkinPatientTransition,
                getPatientModeCallback(appointmentDTO), queryMap);
    }

    private void launchPatientModeCheckout(AppointmentDTO appointmentDTO) {
        getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKOUT);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());

        TransitionDTO checkoutPatientTransition = checkInDTO.getMetadata().getTransitions()
                .getCheckoutPatientMode();

        getWorkflowServiceHelper().execute(checkoutPatientTransition,
                getPatientModeCallback(appointmentDTO), queryMap);
    }


    private WorkflowServiceCallback getPatientModeCallback(final AppointmentDTO appointmentDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Bundle appointmentInfo = new Bundle();
                appointmentInfo.putString(CarePayConstants.APPOINTMENT_ID,
                        appointmentDTO.getPayload().getId());
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, appointmentInfo);
                finish();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void cancelAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getCancelAppointment();
        confirmationMessageText = "appointment_cancellation_success_message_HTML";
        transitionAppointment(transitionDTO, appointmentDTO, false, getString(R.string.event_appointment_cancelled));
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        showResponsibilityFragment(paymentsModel);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {

    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {

    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance) {
        onDetailItemClick(paymentsModel, paymentLineItem);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //TODO handle this from practice mode
    }
}
