package com.carecloud.carepay.practice.library.checkin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.practice.library.checkin.dialog.AppointmentDetailDialog;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class PracticeModePracticeCheckInActivity extends BasePracticeActivity implements FilterDialog.FilterDialogListener, PracticePaymentNavigationCallback, AppointmentDetailDialog.AppointmentDialogCallback {


    private RecyclerView checkinginRecyclerView;
    private RecyclerView waitingRoomRecyclerView;

    private FilterModel filter;

    CheckInDTO checkInDTO;

    CheckedInAppointmentAdapter checkedInAdapter;
    CheckedInAppointmentAdapter waitingRoomAdapter;

    CarePayTextView goBackTextview;
    CarePayTextView filterOnTextView;
    CarePayTextView filterTextView;
    CarePayTextView checkingInCounterTextview;
    CarePayTextView waitingCounterTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_in);
        filter = new FilterModel();
        initializationView();
        populateLists();
        setAdapter();
    }

    private void initializationView() {
        goBackTextview = (CarePayTextView) findViewById(R.id.goBackTextview);
        filterOnTextView = (CarePayTextView) findViewById(R.id.filterOnTextView);
        filterTextView = (CarePayTextView) findViewById(R.id.filterTextView);
        checkingInCounterTextview = (CarePayTextView) findViewById(R.id.checkingInCounterTextview);
        waitingCounterTextview = (CarePayTextView) findViewById(R.id.waitingCounterTextview);

        checkinginRecyclerView = (RecyclerView) findViewById(R.id.checkinginRecyclerView);
        checkinginRecyclerView.setHasFixedSize(true);
        checkinginRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModePracticeCheckInActivity.this));
        checkinginRecyclerView.setOnDragListener(onCheckingInListDragListener);

        waitingRoomRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
        waitingRoomRecyclerView.setHasFixedSize(true);
        waitingRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(PracticeModePracticeCheckInActivity.this));
        waitingRoomRecyclerView.setOnDragListener(onWaitListDragListener);

        filterOnTextView.setVisibility(View.GONE);
        filterTextView.setVisibility(View.VISIBLE);

        goBackTextview.setOnClickListener(onGobackButtonClick());
        findViewById(R.id.filterLayout).setOnClickListener(onFilterIconClick());
    }

    @NonNull
    private View.OnClickListener onGobackButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send a broadcast
                Intent intent = new Intent();
                intent.setAction("NEW_CHECKEDIN_NOTIFICATION");
                intent.putExtra("appointments_checking_in", "" + checkedInAdapter.getItemCount());
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
        ArrayList<FilterDataDTO> providers = new ArrayList<>();
        ArrayList<FilterDataDTO> locations = new ArrayList<>();
        ArrayList<FilterDataDTO> patients = new ArrayList<>();

        Map<String, String> photoMap = PracticeUtil.getProfilePhotoMap(checkInDTO.getPayload().getPatientBalances());

        CheckInPayloadDTO payload = checkInDTO.getPayload();
        if (null == payload) {
            return;
        }

        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> providersSavedFilteredIds = getApplicationPreferences().getSelectedProvidersIds(practiceId, userId);
        Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

        List<AppointmentDTO> appointments = payload.getAppointments();
        for (AppointmentDTO appointmentDTO : appointments) {
            AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            addProviderOnProviderFilterList(providers, appointmentPayloadDTO, providersSavedFilteredIds);
            addLocationOnFilterList(locations, appointmentPayloadDTO, locationsSavedFilteredIds);
            addPatientOnFilterList(patients, appointmentPayloadDTO, photoMap);
        }

        filter.setDoctors(providers);
        filter.setLocations(locations);
        filter.setPatients(patients);
    }

    private void setAdapter() {
        checkedInAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, false);
        checkinginRecyclerView.setAdapter(checkedInAdapter);

        waitingRoomAdapter = new CheckedInAppointmentAdapter(getContext(), checkInDTO, true);
        waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);

        applyFilter();
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locationsList,
                                         AppointmentPayloadDTO appointmentPayloadDTO,
                                         Set<String> locationsSavedFilteredIds) {
        FilterDataDTO filterDataDTO;
        LocationDTO locationDTO = appointmentPayloadDTO.getLocation();
        filterDataDTO = new FilterDataDTO(locationDTO.getId(), locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if (locationsList.indexOf(filterDataDTO) < 0) {
            if ((locationsSavedFilteredIds != null) && locationsSavedFilteredIds.contains(String.valueOf(locationDTO.getId()))) {
                filterDataDTO.setChecked(true);
            }
            locationsList.add(filterDataDTO);
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

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctors,
                                                 AppointmentPayloadDTO appointmentPayloadDTO,
                                                 Set<String> selectedProvidersIds) {
        ProviderDTO providerDTO = appointmentPayloadDTO.getProvider();
        FilterDataDTO filterDataDTO = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
        if (doctors.indexOf(filterDataDTO) < 0) {
            if ((selectedProvidersIds != null) && selectedProvidersIds.contains(String.valueOf(providerDTO.getId()))) {
                filterDataDTO.setChecked(true);
            }
            doctors.add(filterDataDTO);
        }
    }

    View.OnDragListener onCheckingInListDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            String patientId = "";
            if (dragEvent.getClipDescription() != null) {
                patientId = dragEvent.getClipDescription().getLabel().toString();
            }
            switch (dragEvent.getAction()) {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    if (waitingRoomAdapter.getAppointmentById(patientId, true) == null) {
                        findViewById(R.id.drop_down_area_view).setVisibility(View.VISIBLE);
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
                    if (waitingRoomAdapter.flipAppointmentById(patientId, true)) {
                        checkedInAdapter.flipAppointmentById(patientId, false);

                        applyFilter();
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

    View.OnDragListener onWaitListDragListener = new View.OnDragListener() {
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
                    String patientId = dragEvent.getClipDescription().getLabel().toString();
                    if (checkedInAdapter.flipAppointmentById(patientId, true)) {
                        waitingRoomAdapter.flipAppointmentById(patientId, false);

                        applyFilter();

                        ((TextView) findViewById(R.id.drop_here_icon)).setText(Label.getLabel("practice_checkin_success_label"));
                        ((TextView) findViewById(R.id.drop_here_icon)).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_check, 0, 0);
                        onCheckInAppointment(patientId);
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

    private void onCheckInAppointment(String patientId) {
        Map<String, String> queryMap = new HashMap<>();
        if (checkInDTO.getPayload().getAppointments() != null && checkInDTO.getPayload().getAppointments().size() > 0) {
            queryMap.put("practice_mgmt", checkInDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", checkInDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
        }
        queryMap.put("appointment_id", patientId);
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getCheckinAppointment();
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
            findViewById(R.id.drop_down_area_view).setVisibility(View.GONE);

            // Reset to original state
            ((TextView) findViewById(R.id.drop_here_icon)).setText(Label.getLabel("practice_checkin_drop_here_label"));
            ((TextView) findViewById(R.id.drop_here_icon)).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_drop_here, 0, 0);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findViewById(R.id.drop_down_area_view).setVisibility(View.GONE);
        }
    };

    @Override
    public void applyFilter() {
        checkedInAdapter.applyFilter(filter);
        waitingRoomAdapter.applyFilter(filter);

        checkingInCounterTextview.setText(String.valueOf(checkedInAdapter.getItemCount()));
        waitingCounterTextview.setText(String.valueOf(waitingRoomAdapter.getItemCount()));
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
     */
    public void onCheckInItemClick(AppointmentPayloadDTO appointmentPayloadDTO, boolean isWaitingRoom) {
        AppointmentDetailDialog dialog = new AppointmentDetailDialog(getContext(),
                checkInDTO, getPatientBalanceDTOs(appointmentPayloadDTO.getPatient().getPatientId()),
                appointmentPayloadDTO, isWaitingRoom, this);
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
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
//        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {

    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
//        Gson gson = new Gson();
        Bundle args = new Bundle();
//        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);
        confirmationFragment.show(getSupportFragmentManager(), confirmationFragment.getClass().getSimpleName());
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
//        navigateToFragment(fragment, true);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());

    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        Gson gson = new Gson();
        PaymentsModel paymentsModel = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
        PatientBalanceDTO balance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        String patientBalance = gson.toJson(balance);
        UpdatePatientBalancesDTO updatePatientBalance = gson.fromJson(patientBalance, UpdatePatientBalancesDTO.class);

        showPaymentDistributionFragment(updatePatientBalance);
    }

    private void showPaymentDistributionFragment(UpdatePatientBalancesDTO updatePatientBalance) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager.findFragmentByTag(PaymentDistributionFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.dismiss();
        }
        updatePatientBalance(updatePatientBalance);
    }

    @Override
    public void onPayLaterClicked(PaymentsModel paymentsModel) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if(paymentsModel!=null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()){
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }


    @Override
    public void showPaymentDistributionDialog(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onFailure(String errorMessage) {
        showErrorNotification(null); // Show default error
    }

    @Override
    public void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems, AddPaymentItemFragment.AddItemCallback callback) {

        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(simpleChargeItems));

        AddPaymentItemFragment fragment = new AddPaymentItemFragment();
        fragment.setCallback(callback);
        if (simpleChargeItems != null && !simpleChargeItems.isEmpty()) {
            fragment.setArguments(args);
        }

        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback, BalanceItemDTO balanceItem, SimpleChargeItem chargeItem) {
        PaymentDistributionEntryFragment entryFragment = new PaymentDistributionEntryFragment();
        if (balanceItem != null) {
            entryFragment.setBalanceItem(balanceItem);
        }
        if (chargeItem != null) {
            entryFragment.setChargeItem(chargeItem);
        }

        entryFragment.setCallback(callback);
        entryFragment.show(getSupportFragmentManager(), entryFragment.getClass().getSimpleName());
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();

//                    PaymentsModel paymentsModel = gson.fromJson(jsonPayload, PaymentsModel.class);
                    WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                    showPaymentConfirmation(workflowDTO);


                }
                break;
            }
            default:
                //nothing
                return;
        }
    }

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            PaymentQueuedDialogFragment dialogFragment = new PaymentQueuedDialogFragment();
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showPaymentDistributionFragment(new UpdatePatientBalancesDTO());
                }
            };
            dialogFragment.setOnDismissListener(dismissListener);
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getName());

        }
    }

    private void updatePatientBalance(UpdatePatientBalancesDTO updateBalance) {
        ListIterator<PatientBalanceDTO> iterator = checkInDTO.getPayload().getPatientBalances().listIterator();
        while (iterator.hasNext()) {
            PatientBalanceDTO patientBalanceDTO = iterator.next();
            if (matchesBalanceDTO(patientBalanceDTO, updateBalance)) {
                try {
                    double pendingResponsibility = Double.parseDouble(updateBalance.getPendingRepsonsibility());
                    patientBalanceDTO.setPendingRepsonsibility(updateBalance.getPendingRepsonsibility());
                    patientBalanceDTO.setBalances(updateBalance.getBalances());

                    break;
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }

        //reload list data
        setAdapter();
    }

    private boolean matchesBalanceDTO(PatientBalanceDTO paymentsPatientBalance, UpdatePatientBalancesDTO updateBalance) {
        return paymentsPatientBalance.getDemographics().getMetadata().getUserId().equals(updateBalance.getDemographics().getMetadata().getUserId()) &&
                paymentsPatientBalance.getBalances().get(0).getMetadata().getPatientId().equals(updateBalance.getBalances().get(0).getMetadata().getPatientId());
    }


    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager.findFragmentByTag(PaymentDistributionFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }
}