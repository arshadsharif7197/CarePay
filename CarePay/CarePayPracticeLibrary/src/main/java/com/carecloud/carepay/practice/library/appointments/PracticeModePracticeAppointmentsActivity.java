package com.carecloud.carepay.practice.library.appointments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.fragments.AdHocFormsListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeModeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.interfaces.PracticeAppointmentDialogListener;
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
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.createappointment.availabilityhour.BaseAvailabilityHourFragment;
import com.carecloud.carepaylibray.appointments.interfaces.VideoAppointmentCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
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
        PracticeAppointmentDialogListener,
        ResponsibilityFragmentDialog.PayResponsibilityCallback,
        PaymentDetailInterface,
        VideoAppointmentCallback {

    private FilterModel filterModel;

    private CheckInDTO checkInDTO;

    private TwoColumnPatientListView patientListView;
    private boolean needsToConfirmAppointmentCreation;
    private boolean wasCalledFromThisClass;
    private View filterTextView;
    private View filterTextViewOn;
    private TextView patientCountLabelTextView;
    private FilterDialog filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_practice_appointments);

        filterModel = new FilterModel();
        initializeCheckinDto();
        initializeViews();
        setUpFilter();
    }

    private void initializeCheckinDto() {
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        populateLists();
        initializePatientListView();
        initializePatientCounter();

    }

    private void initializePatientCounter() {
        String count = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPatients());
        setTextViewById(R.id.practice_patient_count, count);

        count = String.format(Locale.getDefault(), "%1s", patientListView.getSizeFilteredPendingPatients());
        setTextViewById(R.id.practice_pending_count, count);

        patientCountLabelTextView = findViewById(R.id.patientCountLabelTextView);

        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    Label.getLabel("today_label"),
                    Label.getLabel("tomorrow_label"),
                    Label.getLabel("this_month_label"),
                    Label.getLabel("next_days_label"),
                    true).toUpperCase(Locale.getDefault());
            patientCountLabelTextView.setText(practiceCountLabel);
        }
    }

    private void initializePatientListView() {
        patientListView = findViewById(R.id.list_patients);
        patientListView.setCheckInDTO(checkInDTO);
        patientListView.setCallback(dto -> showPracticeAppointmentDialog((AppointmentDTO) dto));
        applyFilter();
    }

    private void initializeViews() {
        TextView findPatientTextView = findViewById(R.id.practice_find_patient);
        findPatientTextView.setOnClickListener(getFindPatientListener(true));

        TextView addAppointmentTextView = findViewById(R.id.activity_practice_appointments_add);
        addAppointmentTextView.setOnClickListener(getFindPatientListener(false));
        addAppointmentTextView.setEnabled(checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions().canScheduleAppointment);

        findViewById(R.id.practice_go_back).setOnClickListener(view -> onBackPressed());

        final TextView showPendingAppointmentTextView = findViewById(R.id.showPendingAppointmentTextView);
        showPendingAppointmentTextView.setOnClickListener(v -> {
            showPendingAppointmentTextView.setSelected(!showPendingAppointmentTextView.isSelected());
            if (showPendingAppointmentTextView.isSelected()) {
                showViewById(R.id.practice_pending_count);
                disappearViewById(R.id.practice_patient_count);
                filterModel.setFilteringByPending(true);
                patientCountLabelTextView.setText(Label.getLabel("pending_label"));
            } else {
                showViewById(R.id.practice_patient_count);
                disappearViewById(R.id.practice_pending_count);
                filterModel.setFilteringByPending(false);
                patientCountLabelTextView.setText(Label.getLabel("today_label"));
            }
            applyFilter();
        });

        initializePracticeSelectDateRange();
    }

    private void setUpFilter() {
        filterTextView = findViewById(R.id.filterTextView);
        filterTextView.setOnClickListener(onFilterIconClick());
        filterTextViewOn = findViewById(R.id.filterTextViewOn);
        filterTextViewOn.setOnClickListener(onFilterIconClick());
        if (filterModel.areThereActiveFilters()) {
            filterTextViewOn.setVisibility(View.VISIBLE);
        } else {
            filterTextView.setVisibility(View.VISIBLE);
        }
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

        filterModel.setDoctors(getFilterProviders(providersSavedFilteredIds));
        filterModel.setLocations(getFilterLocations(locationsSavedFilteredIds));
        filterModel.setPatients(patients);
        if (filterDialog != null && filterDialog.isShowing()) {
            filterDialog.refresh();
        }
    }

    @NonNull
    private View.OnClickListener onFilterIconClick() {
        return view -> {
            filterDialog = new FilterDialog(getContext(),
                    findViewById(R.id.activity_practice_appointments), filterModel);

            filterDialog.showPopWindow();
        };
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
        patientListView.applyFilter(filterModel);
        initializePatientCounter();
    }

    @Override
    public void refreshData() {
        onAppointmentRequestSuccess();
    }

    @Override
    public void showFilterFlag(boolean areThereActiveFilters) {
        filterTextView.setVisibility(areThereActiveFilters ? View.GONE : View.VISIBLE);
        filterTextViewOn.setVisibility(areThereActiveFilters ? View.VISIBLE : View.GONE);
    }

    private void initializePracticeSelectDateRange() {
        findViewById(R.id.activity_practice_appointments_change_date_range_label)
                .setOnClickListener(view -> {

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
        return view -> {
            needsToConfirmAppointmentCreation = needsConfirmation;
            TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getFindPatient();
            FindPatientDialog findPatientDialog = FindPatientDialog.newInstance(transitionDTO,
                    Label.getLabel(needsConfirmation ? "practice_checkin_filter_find_patient_by_name"
                            : "practice_filter_find_patient_first"));
            findPatientDialog.setListener(getFindPatientDialogListener());
            displayDialogFragment(findPatientDialog, false);
        };
    }

    private FindPatientDialog.OnItemClickedListener getFindPatientDialogListener() {
        return patient -> {
            setPatient(patient);
            getPatientBalances(patient, needsToConfirmAppointmentCreation);
        };
    }

    private void getPatientBalances(PatientModel patient, boolean needsToConfirmAppointmentCreation) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patient.getPatientId());
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO,
                getPatientBalancesCallback(patient, needsToConfirmAppointmentCreation), queryMap);
    }

    private WorkflowServiceCallback getPatientBalancesCallback(final PatientModel patient,
                                                               final boolean needsToConfirmAppointmentCreation) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                PaymentsModel patientDetails = DtoHelper
                        .getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
                patient.setProfilePhoto(patientDetails.getPaymentPayload().getPatientBalances().get(0)
                        .getDemographics().getPayload().getPersonalDetails().getProfilePhoto());
                patient.setPhoneNumber(patientDetails.getPaymentPayload().getPatientBalances().get(0)
                        .getDemographics().getPayload().getAddress().getPhone());
                if (needsToConfirmAppointmentCreation) {
                    showResponsibilityFragment(patientDetails);
                } else {
                    newAppointment();
                }
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
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
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    private void showPracticeAppointmentDialog(AppointmentDTO appointmentDTO) {
        AppointmentDisplayStyle dialogStyle = AppointmentDisplayStyle.DEFAULT;
        AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        if (appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            dialogStyle = AppointmentDisplayStyle.REQUESTED;

        } else if (appointmentPayloadDTO.canCheckOut() || appointmentPayloadDTO.getVisitType().hasVideoOption()) {
            dialogStyle = AppointmentDisplayStyle.CHECKED_IN;

        } else if (appointmentPayloadDTO.isAppointmentOver() && appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.PENDING)) {
            dialogStyle = AppointmentDisplayStyle.MISSED;

        } else if (appointmentPayloadDTO.isAppointmentOver() || appointmentPayloadDTO.isAppointmentFinished()) {
            //todo finished appt options, Doing nothing for now

        } else if (appointmentPayloadDTO.canCheckIn()) {
            dialogStyle = AppointmentDisplayStyle.PENDING;

        }

        PracticeAppointmentDialog dialog = PracticeAppointmentDialog.newInstance(
                dialogStyle,
                appointmentDTO,
                checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions()
        );
        displayDialogFragment(dialog, true);
        setPatient(appointmentDTO.getPayload().getPatient());
    }

    private void showResponsibilityFragment(PaymentsModel paymentsModel) {
        String tag = ResponsibilityFragmentDialog.class.getName();
        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newPatientHeader(paymentsModel);
        FormsResponsibilityFragmentDialog dialog = FormsResponsibilityFragmentDialog
                .newInstance(paymentsModel,
                        checkInDTO.getPayload().getUserAuthModel().getUserAuthPermissions(),
                        headerModel, paymentsModel.getPaymentPayload().getPatientBalances().get(0));
        dialog.show(getSupportFragmentManager(), tag);
    }

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

    @Override
    public void startVideoVisit(AppointmentDTO appointmentDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        TransitionDTO videoVisitTransition = checkInDTO.getMetadata().getLinks().getVideoVisit();
        getWorkflowServiceHelper().execute(videoVisitTransition, startVideoVisitCallback, queryMap);
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
    public void onLeftActionTapped(PaymentsModel paymentsModel, double owedAmount) {
        getAllPracticeForms(getPatient().getPatientId(), paymentsModel);
    }

    @Override
    public void onRightActionTapped(PaymentsModel paymentsModel, double amount) {
        newAppointment();
    }

    @Override
    public void onMiddleActionTapped(PaymentsModel paymentsModel, double amount) {

    }

    private void getAllPracticeForms(String patientId, PaymentsModel paymentsModel) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientId);
        TransitionDTO adHocForms = checkInDTO.getMetadata().getLinks().getAllPracticeForms();
        getWorkflowServiceHelper().execute(adHocForms, getAdHocServiceCallback(patientId, paymentsModel), queryMap);
    }

    WorkflowServiceCallback getAdHocServiceCallback(final String patientId, final PaymentsModel paymentsModel) {
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
                AdHocFormsListFragment fragment = AdHocFormsListFragment.newInstance(appointmentsResultModel, patientId);
                displayDialogFragment(fragment, false);
                fragment.setOnDismissListener(dialog -> {
                    if (!isVisible()) {
                        return;
                    }
                    showResponsibilityFragment(paymentsModel);
                });
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
                Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        };
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
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //TODO handle this from practice mode
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel;
    }

    @Override
    public void appointmentScheduledSuccessfully() {
        getSupportFragmentManager().popBackStackImmediate(AvailabilityHourFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStackImmediate();
        onAppointmentRequestSuccess();
    }

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        //Not Apply
    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setResourceProvider(resource);
        }
    }

    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setVisitType(visitTypeDTO);
        }
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setLocation(locationDTO);
        }
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO,
                                                    final BaseAvailabilityHourFragment baseAvailabilityHourFragment) {
        PracticeModeRequestAppointmentDialog fragment = PracticeModeRequestAppointmentDialog.newInstance(appointmentDTO, getPatient());
        fragment.setOnCancelListener(dialogInterface -> baseAvailabilityHourFragment.showDialog());
        showFragment(fragment);
    }

    @Override
    public void onPaymentCashFinished() {
        //NA
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    private WorkflowServiceCallback startVideoVisitCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            AppointmentsResultModel appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            String url = appointmentsResultModel.getPayload().getVideoVisitModel().getPayload().getVisitUrl();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivityForResult(intent, 555);
            }
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    @Override
    public void onPrepaymentFailed() {
        //No prepayment in practice mode
    }
}
