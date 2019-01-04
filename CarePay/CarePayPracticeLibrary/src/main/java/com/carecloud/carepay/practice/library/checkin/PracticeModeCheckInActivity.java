package com.carecloud.carepay.practice.library.checkin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.practice.library.checkin.dialog.AppointmentDetailDialog;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.IntegratedPaymentsChooseDeviceFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticePaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.AddRetailItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentHistoryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentPlanDashboardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeEditOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModeAddToExistingPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModePaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentHistoryDetailFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAddCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanListFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanTermsFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeValidPlansFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RefundDetailFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RefundProcessFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RetailItemOptionsFragment;
import com.carecloud.carepay.practice.library.payments.interfaces.AddPaymentItemCallback;
import com.carecloud.carepay.practice.library.payments.interfaces.PaymentPlanDashboardInterface;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.practice.library.payments.interfaces.ShamrockPaymentsCallback;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class PracticeModeCheckInActivity extends BasePracticeActivity
        implements FilterDialog.FilterDialogListener, PracticePaymentNavigationCallback,
        AppointmentDetailDialog.AppointmentDialogCallback, PaymentPlanEditInterface,
        CheckedInAppointmentAdapter.CheckinItemCallback, ShamrockPaymentsCallback,
        PaymentPlanDashboardInterface {


    private RecyclerView checkingInRecyclerView;
    private RecyclerView checkedInRecyclerView;
    private RecyclerView checkingOutRecyclerView;
    private RecyclerView checkedOutRecyclerView;

    private FilterModel filter;

    private Handler handler;

    CheckedInAppointmentAdapter checkingInAdapter;
    CheckedInAppointmentAdapter checkedInAdapter;
    CheckedInAppointmentAdapter checkingOutAdapter;
    CheckedInAppointmentAdapter checkedOutAdapter;

    CarePayTextView goBackTextView;
    CarePayTextView filterOnTextView;
    CarePayTextView filterTextView;
    CarePayTextView checkingInCounterTextView;
    CarePayTextView waitingCounterTextView;
    CarePayTextView checkingOutCounterTextView;
    CarePayTextView checkedOutCounterTextView;

    CheckInDTO checkInDTO;
    private PaymentsModel selectedPaymentModel;
    private PaymentHistoryItem recentRefundItem;
    private String patientId;

    private boolean paymentMethodCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_in);
        filter = new FilterModel();
        initializationView();
        populateLists();
        setAdapter();
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        if (recentRefundItem != null) {
            if (recentRefundItem.getPayload() != null) {
                completeRefundProcess(recentRefundItem, selectedPaymentModel);
            } else {
                displayPendingTransactionDialog(true);
            }
        }
        recentRefundItem = null;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initializationView() {
        goBackTextView = (CarePayTextView) findViewById(R.id.goBackTextview);
        filterOnTextView = (CarePayTextView) findViewById(R.id.filterOnTextView);
        filterTextView = (CarePayTextView) findViewById(R.id.filterTextView);
        checkingInCounterTextView = (CarePayTextView) findViewById(R.id.checkingInCounterTextview);
        waitingCounterTextView = (CarePayTextView) findViewById(R.id.waitingCounterTextview);
        checkingOutCounterTextView = (CarePayTextView) findViewById(R.id.checkingOutCounterTextview);
        checkedOutCounterTextView = (CarePayTextView) findViewById(R.id.checkedOutCounterTextview);

        checkingInRecyclerView = (RecyclerView) findViewById(R.id.checkinginRecyclerView);
        checkingInRecyclerView.setHasFixedSize(true);
        checkingInRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkingInRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModeCheckInActivity.this));
        checkingInRecyclerView.setOnDragListener(onCheckingInListDragListener);

        checkedInRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
        checkedInRecyclerView.setHasFixedSize(true);
        checkedInRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkedInRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModeCheckInActivity.this));
        checkedInRecyclerView.setOnDragListener(onCheckedInListDragListener);

        checkingOutRecyclerView = (RecyclerView) findViewById(R.id.checkingOutRecyclerView);
        checkingOutRecyclerView.setHasFixedSize(true);
        checkingOutRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkingOutRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModeCheckInActivity.this));

        checkedOutRecyclerView = (RecyclerView) findViewById(R.id.checkedOutRecyclerView);
        checkedOutRecyclerView.setHasFixedSize(true);
        checkedOutRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkedOutRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModeCheckInActivity.this));


        filterOnTextView.setVisibility(View.GONE);
        filterTextView.setVisibility(View.VISIBLE);

        goBackTextView.setOnClickListener(onGoBackButtonClick());
        findViewById(R.id.filterLayout).setOnClickListener(onFilterIconClick());
    }

    @NonNull
    private View.OnClickListener onGoBackButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send a broadcast
                Intent intent = new Intent();
                intent.setAction("NEW_CHECKEDIN_NOTIFICATION");
                intent.putExtra("appointments_checking_in", "" + checkingInAdapter.getItemCount());
                sendBroadcast(intent);
                onBackPressed();
            }
        };
    }

    @NonNull
    private View.OnClickListener onFilterIconClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog filterDialog = new FilterDialog(getContext(),
                        findViewById(R.id.activity_checked_in), filter,
                        Label.getLabel("practice_checkin_filter"),
                        Label.getLabel("practice_checkin_filter_find_patient_by_name"),
                        Label.getLabel("practice_checkin_filter_clear_filters"));

                filterDialog.showPopWindow();
            }
        };
    }

    private void populateLists() {
//        ArrayList<FilterDataDTO> providers = new ArrayList<>();
        ArrayList<FilterDataDTO> patients = new ArrayList<>();

        Map<String, String> photoMap = PracticeUtil.getProfilePhotoMap(checkInDTO.getPayload()
                .getPatientBalances());

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
//            addProviderOnProviderFilterList(providers, appointmentPayloadDTO, providersSavedFilteredIds);
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
            if (selectedLocationsIds != null && selectedLocationsIds.contains(String.valueOf(filterLocation.getId()))) {
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
            if (selectedProvidersIds != null && selectedProvidersIds.contains(String.valueOf(filterProvider.getId()))) {
                filterProvider.setChecked(true);
            }
            providers.add(filterProvider);
        }
        return providers;
    }


    private void setAdapter() {
        handler.removeCallbacksAndMessages(null);

        checkingInAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, this,
                CheckedInAppointmentAdapter.CHECKING_IN);
        checkingInRecyclerView.setAdapter(checkingInAdapter);

        checkedInAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, this,
                CheckedInAppointmentAdapter.CHECKED_IN);
        checkedInRecyclerView.setAdapter(checkedInAdapter);

        checkingOutAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, this,
                CheckedInAppointmentAdapter.CHECKING_OUT);
        checkingOutRecyclerView.setAdapter(checkingOutAdapter);

        checkedOutAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, this,
                CheckedInAppointmentAdapter.CHECKED_OUT);
        checkedOutRecyclerView.setAdapter(checkedOutAdapter);

        applyFilter();
        scheduleCheckingInOutRefresh();
        scheduleCheckedInRefresh();
        scheduleAllLanesRefresh();
    }

    private void scheduleCheckingInOutRefresh() {
        handler.postDelayed(refreshCheckingInOutTime, 1000 * 5);
    }

    private Runnable refreshCheckingInOutTime = new Runnable() {
        @Override
        public void run() {
            checkingInAdapter.notifyDataSetChanged();
            checkingOutAdapter.notifyDataSetChanged();
            scheduleCheckingInOutRefresh();
        }
    };

    private void scheduleCheckedInRefresh() {
        handler.postDelayed(refreshCheckedInTime, 1000 * 30);
    }

    private Runnable refreshCheckedInTime = new Runnable() {
        @Override
        public void run() {
            checkedInAdapter.notifyDataSetChanged();
            scheduleCheckedInRefresh();
        }
    };

    private void scheduleAllLanesRefresh() {
        handler.postDelayed(refreshAllLanes, 1000 * 60 * 3);
    }

    private Runnable refreshAllLanes = new Runnable() {
        @Override
        public void run() {
            refreshLists(false);
        }
    };

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

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctors,
                                                 AppointmentsPayloadDTO appointmentPayloadDTO,
                                                 Set<String> selectedProvidersIds) {
        ProviderDTO providerDTO = appointmentPayloadDTO.getProvider();
        FilterDataDTO filterDataDTO = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(),
                FilterDataDTO.FilterDataType.PROVIDER);
        if (doctors.indexOf(filterDataDTO) < 0) {
            if ((selectedProvidersIds != null) && selectedProvidersIds
                    .contains(String.valueOf(providerDTO.getId()))) {
                filterDataDTO.setChecked(true);
            }
            doctors.add(filterDataDTO);
        }
    }

    View.OnDragListener onCheckingInListDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            String appointmentId = "";
            if (dragEvent.getClipDescription() != null) {
                appointmentId = dragEvent.getClipDescription().getLabel().toString();
            }
            switch (dragEvent.getAction()) {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    if (checkedInAdapter.getAppointmentById(appointmentId, true) == null) {
                        findViewById(R.id.drop_down_area_view).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.drop_down_checking_area_view).setVisibility(View.VISIBLE);
                    }
                    break;
                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    if (checkedInAdapter.flipAppointmentById(appointmentId, true)) {
                        checkingInAdapter.flipAppointmentById(appointmentId, false);
                        applyFilter();

                        ((TextView) findViewById(R.id.drop__checking_here_icon)).setText(Label
                                .getLabel("practice_checkin_success_label"));
                        ((TextView) findViewById(R.id.drop__checking_here_icon))
                                .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_check, 0, 0);
                        undoAppointmentCheckin(appointmentId);
                    } else {
                        findViewById(R.id.drop_down_area_view).setVisibility(View.GONE);
                    }

                    break;
                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    //rv.updateState(customState);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    View.OnDragListener onCheckedInListDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            switch (dragEvent.getAction()) {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    String appointmentId = dragEvent.getClipDescription().getLabel().toString();
                    if (checkingInAdapter.flipAppointmentById(appointmentId, true)) {
                        checkedInAdapter.flipAppointmentById(appointmentId, false);

                        applyFilter();

                        ((TextView) findViewById(R.id.drop_here_icon)).setText(Label
                                .getLabel("practice_checkin_success_label"));
                        ((TextView) findViewById(R.id.drop_here_icon))
                                .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_check, 0, 0);
                        onCheckInAppointment(appointmentId);
                    } else {
                        findViewById(R.id.drop_down_checking_area_view).setVisibility(View.GONE);
                    }
                    break;
                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    //rv.updateState(customState);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void onCheckInAppointment(String appointmentId) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentId);
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getCheckinAppointment();
        getWorkflowServiceHelper().execute(transitionDTO, checkInAppointmentCallback, queryMap);
    }

    private void undoAppointmentCheckin(String appointmentId) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentId);
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getConfirmAppointment();
        getWorkflowServiceHelper().execute(transitionDTO, checkInAppointmentCallback, queryMap);
    }

    private WorkflowServiceCallback checkInAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            refreshLists(false);
            findViewById(R.id.drop_down_area_view).setVisibility(View.GONE);
            findViewById(R.id.drop_down_checking_area_view).setVisibility(View.GONE);
            // Reset to original state
            ((TextView) findViewById(R.id.drop_here_icon)).setText(Label
                    .getLabel("practice_checkin_drop_here_label"));
            ((TextView) findViewById(R.id.drop_here_icon))
                    .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_drop_here, 0, 0);
            ((TextView) findViewById(R.id.drop__checking_here_icon)).setText(Label
                    .getLabel("practice_checkin_drop_here_label"));
            ((TextView) findViewById(R.id.drop__checking_here_icon))
                    .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_drop_here, 0, 0);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            findViewById(R.id.drop_down_area_view).setVisibility(View.GONE);
            findViewById(R.id.drop_down_checking_area_view).setVisibility(View.GONE);
        }
    };

    @Override
    public void applyFilter() {
        checkingInAdapter.applyFilter(filter);
        checkedInAdapter.applyFilter(filter);
        checkingOutAdapter.applyFilter(filter);
        checkedOutAdapter.applyFilter(filter);
        checkingInCounterTextView.setText(String.valueOf(checkingInAdapter.getItemCount()));
        waitingCounterTextView.setText(String.valueOf(checkedInAdapter.getItemCount()));
        checkingOutCounterTextView.setText(String.valueOf(checkingOutAdapter.getItemCount()));
        checkedOutCounterTextView.setText(String.valueOf(checkedOutAdapter.getItemCount()));
    }

    @Override
    public void refreshData() {
        refreshLists(true);
    }

    private PendingBalanceDTO getPatientBalanceDTOs(String patientId) {
        List<PatientBalanceDTO> patientBalances = checkInDTO.getPayload().getPatientBalances();

        for (PatientBalanceDTO patientBalanceDTO : patientBalances) {
            PendingBalanceDTO pendingBalanceDTO = patientBalanceDTO.getBalances().get(0);
            if (pendingBalanceDTO.getMetadata().getPatientId().equals(patientId)) {
                return pendingBalanceDTO;
            }
        }
        return null;
    }

    /**
     * On check in item click.
     *
     * @param appointmentPayloadDTO the appointment payload dto
     * @param theRoom               the room
     */
    @Override
    public void onCheckInItemClick(AppointmentsPayloadDTO appointmentPayloadDTO, int theRoom) {
        patientId = appointmentPayloadDTO.getPatient().getPatientId();
        AppointmentDetailDialog dialog = new AppointmentDetailDialog(getContext(),
                checkInDTO, getPatientBalanceDTOs(appointmentPayloadDTO.getPatient().getPatientId()),
                appointmentPayloadDTO, theRoom, this);
        dialog.setOwnerActivity(this);
        dialog.show();
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsModel));
        PaymentDistributionFragment fragment = new PaymentDistributionFragment();
        fragment.setArguments(args);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanAction(final PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedBalance = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);
        PracticeModePaymentPlanFragment fragment = PracticeModePaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                false};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    double amount) {
    }

    @Override
    public void showChooseDeviceList(PaymentsModel paymentsModel, double paymentAmount) {
        IntegratedPaymentsChooseDeviceFragment fragment = IntegratedPaymentsChooseDeviceFragment
                .newInstance(paymentsModel, paymentAmount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void dismissChooseDeviceList(double amount, PaymentsModel paymentsModel) {
        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment.newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PatientModePaymentPlanEditFragment.class.getName());
        if (fragment != null && fragment instanceof PatientModePaymentPlanEditFragment) {
            ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
            return;
        }

        fragment = getSupportFragmentManager().findFragmentByTag(PracticeModePaymentPlanFragment.class.getName());
        if (fragment != null && fragment instanceof PracticeModePaymentPlanFragment) {
            ((PracticeModePaymentPlanFragment) fragment).replacePaymentMethod(creditCard);
            return;
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        Gson gson = new Gson();
        PaymentsModel paymentsModel = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
        UpdatePatientBalancesDTO updatePatientBalance = null;
        if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            PatientBalanceDTO balance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
            String patientBalance = gson.toJson(balance);
            updatePatientBalance = gson.fromJson(patientBalance,
                    UpdatePatientBalancesDTO.class);
        }
        hidePaymentDistributionFragment(updatePatientBalance);
    }

    private void hidePaymentDistributionFragment(UpdatePatientBalancesDTO updatePatientBalance) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.dismiss();
        }
        if (updatePatientBalance != null) {
            updatePatientBalance(updatePatientBalance);
        }
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }


    @Override
    public void showPaymentDistributionDialog(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
        selectedPaymentModel = paymentsModel;
    }

    @Override
    public void onFailure(String errorMessage) {
        showErrorNotification(errorMessage); // Show default error
    }

    @Override
    public void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems,
                                 AddPaymentItemCallback callback) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(simpleChargeItems));

        AddPaymentItemFragment fragment = new AddPaymentItemFragment();
        fragment.setCallback(callback);
        if (simpleChargeItems != null && !simpleChargeItems.isEmpty()) {
            fragment.setArguments(args);
        }

        displayDialogFragment(fragment, false);
    }

    @Override
    public void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback,
                                BalanceItemDTO balanceItem, SimpleChargeItem chargeItem) {
        PaymentDistributionEntryFragment entryFragment = new PaymentDistributionEntryFragment();
        if (balanceItem != null) {
            entryFragment.setBalanceItem(balanceItem);
        }
        if (chargeItem != null) {
            entryFragment.setChargeItem(chargeItem);
        }

        entryFragment.setCallback(callback);
        displayDialogFragment(entryFragment, false);
    }

    @Override
    public void showPaymentPlanDashboard(PaymentsModel paymentsModel) {
        PaymentPlanDashboardFragment fragment = PaymentPlanDashboardFragment.newInstance(paymentsModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void showRetailItems(PaymentsModel paymentsModel,
                                AddPaymentItemCallback callback) {
        AddRetailItemFragment retailItemFragment = AddRetailItemFragment.getInstance(paymentsModel);
        retailItemFragment.setCallback(callback);
        displayDialogFragment(retailItemFragment, false);
    }

    @Override
    public void showRetailItemOptions(RetailItemDto retailItemDto, AddPaymentItemCallback callback) {
        RetailItemOptionsFragment optionsFragment = RetailItemOptionsFragment.newInstance(retailItemDto);
        optionsFragment.setCallback(callback);
        displayDialogFragment(optionsFragment, false);
    }

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                boolean isRefund = data.getBooleanExtra(CarePayConstants.CLOVER_REFUND_INTENT_FLAG, false);
                final String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    if (isRefund) {
                        Log.d("Process Refund Success", jsonPayload);
                        PaymentHistoryItem historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, jsonPayload);
                        if (isVisible()) {
                            completeRefundProcess(historyItem, selectedPaymentModel);
                        } else {
                            recentRefundItem = historyItem;
                        }
                    } else {
                        Gson gson = new Gson();
                        WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                        showPaymentConfirmation(workflowDTO);
                    }

                }
                break;
            }
            default:
                //nothing
        }
    }

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            displayPendingTransactionDialog(false);
        } else if (resultCode == CarePayConstants.REFUND_RETRY_PENDING_RESULT_CODE) {
            if (isVisible()) {
                displayPendingTransactionDialog(true);
            } else {
                recentRefundItem = new PaymentHistoryItem();
                recentRefundItem.setPayload(null);
            }
        }
    }

    private void displayPendingTransactionDialog(boolean isRefund) {
        PaymentQueuedDialogFragment dialogFragment = PaymentQueuedDialogFragment.newInstance(isRefund);
        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hidePaymentDistributionFragment(new UpdatePatientBalancesDTO());
            }
        };
        dialogFragment.setOnDismissListener(dismissListener);
        displayDialogFragment(dialogFragment, false);
    }


    private void updatePatientBalance(UpdatePatientBalancesDTO updateBalance) {
        ListIterator<PatientBalanceDTO> iterator = checkInDTO.getPayload()
                .getPatientBalances().listIterator();
        while (iterator.hasNext()) {
            PatientBalanceDTO patientBalanceDTO = iterator.next();
            if (matchesBalanceDTO(patientBalanceDTO, updateBalance)) {
                patientBalanceDTO.setPendingRepsonsibility(updateBalance.getPendingRepsonsibility());
                patientBalanceDTO.setBalances(updateBalance.getBalances());

                break;
            }
        }
        //reload list data
        setAdapter();
    }

    private boolean matchesBalanceDTO(PatientBalanceDTO paymentsPatientBalance,
                                      UpdatePatientBalancesDTO updateBalance) {
        return paymentsPatientBalance.getDemographics().getMetadata().getUserId()
                .equals(updateBalance.getDemographics().getMetadata().getUserId())
                && paymentsPatientBalance.getBalances().get(0).getMetadata().getPatientId()
                .equals(updateBalance.getBalances().get(0).getMetadata().getPatientId());
    }


    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        if (paymentMethodCancelled) {
            paymentMethodCancelled = false;
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //TODO handle this from practice mode
    }

    @Override
    public void showPaymentHistory(PaymentsModel paymentsModel) {
        PaymentHistoryFragment fragment = PaymentHistoryFragment.newInstance(paymentsModel);
        displayDialogFragment(fragment, false);
    }


    @Override
    public void onDismissPaymentHistory() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void displayHistoryItemDetails(PaymentHistoryItem item, PaymentsModel paymentsModel) {
        PracticePaymentHistoryDetailFragment fragment = PracticePaymentHistoryDetailFragment.newInstance(item, paymentsModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void startRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel) {
//        getSupportFragmentManager().popBackStack(PaymentDistributionFragment.class.getName(), 0);
        RefundProcessFragment fragment = RefundProcessFragment.newInstance(historyItem, paymentsModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void completeRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        RefundDetailFragment fragment = RefundDetailFragment.newInstance(historyItem, paymentsModel);
        displayDialogFragment(fragment, false);

    }

    @Override
    public void displayPaymentPlansList(final PaymentsModel paymentsModel) {
        PracticePaymentPlanListFragment fragment = PracticePaymentPlanListFragment
                .newInstance(paymentsModel, getPracticeInfo(paymentsModel).getPracticeId());
        fragment.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showPaymentHistory(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanSelected(final PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        displayDialogFragment(fragment, false);
    }

    private void refreshLists(boolean isBlocking) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

        if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
            queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
        }

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPracticeAppointments();
        getWorkflowServiceHelper().execute(transitionDTO, getRefreshListCallback(isBlocking), queryMap);

    }

    private WorkflowServiceCallback getRefreshListCallback(final boolean isBlocking) {

        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                if (isBlocking) {
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                checkInDTO = DtoHelper.getConvertedDTO(CheckInDTO.class, workflowDTO);
                populateLists();
                setAdapter();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void onMakeOneTimePayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO) {
        PracticeOneTimePaymentFragment fragment = PracticeOneTimePaymentFragment
                .newInstance(paymentsModel, 0, paymentPlanDTO);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartOneTimePayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paymentMethodCancelled = true;
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_payment_plan_id),
                getString(R.string.param_payment_plan_amount),
                getString(R.string.param_patient_id)
        };
        Object[] values = {
                paymentPlanDTO.getMetadata().getPracticeId(),
                paymentPlanDTO.getMetadata().getPaymentPlanId(),
                paymentPlanDTO.getPayload().getAmount(),
                paymentPlanDTO.getMetadata().getPatientId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_onetime_payment), params, values);
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          final PaymentsModel paymentsModel,
                                          final PaymentPlanDTO paymentPlanDTO,
                                          boolean onlySelectMode,
                                          final Date paymentDate) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(),
                            paymentPlanDTO, onlySelectMode, paymentDate);
            fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (paymentDate == null) {
                        onStartOneTimePayment(paymentsModel, paymentPlanDTO);
                    } else {
                        onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
                    }
                }
            });
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode,
                                     final Date paymentDate) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (paymentDate == null) {
                    onStartOneTimePayment(paymentsModel, paymentPlanDTO);
                } else {
                    onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
                }
            }
        });
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
                displayDialogFragment(fragment, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onScheduleOneTimePayment(final PaymentsModel paymentsModel,
                                         final PaymentPlanDTO paymentPlanDTO,
                                         Date paymentDate) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paymentMethodCancelled = true;
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();
        if (scheduledPayment.getMetadata().getOneTimePaymentId() == null
                && paymentsModel.getPaymentPayload().getScheduledOneTimePayments().size() > 0) {
            scheduledPayment = paymentsModel.getPaymentPayload().getScheduledOneTimePayments().get(0);
        }
        List<ScheduledPaymentModel> scheduledPaymentModels = this.selectedPaymentModel
                .getPaymentPayload().getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId()
                    .equals(scheduledPayment.getMetadata().getOneTimePaymentId())) {
                scheduledPaymentModels.remove(scheduledPayment);
                break;
            }
        }
        scheduledPaymentModels.add(scheduledPayment);
        completePaymentProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
        showSuccessToast(message);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_scheduled), 1);
    }

    @Override
    public void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO,
                                                       ScheduledPaymentPayload scheduledPaymentPayload) {
        showSuccessToast(String.format(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPaymentPayload.getAmount()),
                DateUtil.getInstance()
                        .setDateRaw(scheduledPaymentPayload.getPaymentDate())
                        .toStringWithFormatMmSlashDdSlashYyyy()));
        completePaymentProcess(workflowDTO);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_scheduled_payments_deleted), 1);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            new CustomMessageToast(this, builder.toString(), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
            onDismissPaymentMethodDialog(paymentsModel);
        } else {
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                    .newInstance(workflowDTO, isOneTimePayment);
            displayDialogFragment(confirmationFragment, false);

            if(isOneTimePayment){
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_one_time_payments_completed), 1);
            }
        }
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel,
                                          boolean onlySelectMode) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel);
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                     PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, (PaymentPlanDTO) null, onlySelectMode);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanTermsFragment fragment = PracticePaymentPlanTermsFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel,
                                      PendingBalancePayloadDTO paymentLineItem,
                                      PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, selectedBalance.getPayload().get(0), false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanEditFragment fragment = PatientModePaymentPlanEditFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            //no changes to plan
            return;
        }
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onDismissEditPaymentPlan(final PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                           PendingBalanceDTO selectedBalance,
                                           double amount) {
        PracticeValidPlansFragment fragment = PracticeValidPlansFragment
                .newInstance(paymentsModel, selectedBalance, amount);
        displayDialogFragment(fragment, false);

    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    PaymentPlanDTO selectedPlan,
                                    double amount) {
        //Not used anymore
    }

    @Override
    public void onEditPaymentPlanPaymentMethod(final PaymentsModel paymentsModel,
                                               final PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, true);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartEditScheduledPayment(final PaymentsModel paymentsModel,
                                            final PaymentPlanDTO paymentPlanDTO,
                                            ScheduledPaymentModel scheduledPaymentModel) {
        PracticeEditOneTimePaymentFragment fragment = PracticeEditOneTimePaymentFragment
                .newInstance(paymentsModel, 0, paymentPlanDTO, scheduledPaymentModel);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO, boolean isDeleted) {
        String message = Label.getLabel("payment.cancelPaymentPlan.success.banner.text");
        if (isDeleted) {
            message = Label.getLabel("payment.deletePaymentPlan.success.banner.text");
        }
        showSuccessToast(Label.getLabel(message));

        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientId);

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                PaymentsModel patientDetails = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
                if (patientDetails != null && !patientDetails.getPaymentPayload().getPatientBalances().isEmpty()) {
                    patientDetails.getPaymentPayload().setLocations(checkInDTO.getPayload().getLocations());
                    patientDetails.getPaymentPayload().setLocationIndex(checkInDTO.getPayload().getLocationIndex());
                    patientDetails.getPaymentPayload().setProviders(checkInDTO.getPayload().getProviders());
                    patientDetails.getPaymentPayload().setProviderIndex(checkInDTO.getPayload().getProviderIndex());
                    showPaymentDistributionDialog(patientDetails);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
            }
        }, queryMap);
    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback, boolean isDeletion) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        String title = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.title");
        String message = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.message");
        if (isDeletion) {
            title = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.title");
            message = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.message");
        }
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(title, message);
        confirmDialogFragment.setCallback(confirmationCallback);
        String tag = confirmDialogFragment.getClass().getName();
        confirmDialogFragment.show(ft, tag);
    }

    @Override
    public DTO getDto() {
        return checkInDTO;
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        //TODO what should we show in this case because we don't have PP represented at the top level payload
    }

    @Override
    public void onAddBalanceToExistingPlan(final PaymentsModel paymentsModel, PaymentPlanDTO paymentPlan) {
        PracticeModeAddToExistingPaymentPlanFragment fragment = PracticeModeAddToExistingPaymentPlanFragment
                .newInstance(paymentsModel, paymentPlan);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showPaymentPlanDetail(final PaymentsModel paymentsModel,
                                      PaymentPlanDTO paymentPlan,
                                      boolean isCompleted) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlan, isCompleted);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }
}