package com.carecloud.carepay.practice.library.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterPopupWindow;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.practice.library.payments.fragments.NoAddChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CheckInActivity extends BasePracticeActivity implements CustomFilterPopupWindow.FilterCallBack, PaymentNavigationCallback {

    private RecyclerView checkinginRecyclerView;
    private RecyclerView waitingRoomRecyclerView;
    private Context context;
    private boolean patientFiltered;

    CheckInDTO checkInDTO;

    ArrayList<AppointmentPayloadDTO> checkingInAppointments = new ArrayList<>();
    ArrayList<AppointmentPayloadDTO> waitingRoomAppointments = new ArrayList<>();

    CheckedInAppointmentAdapter checkedInAdapter;
    CheckedInAppointmentAdapter waitingRoomAdapter;

    ArrayList<FilterDataDTO> filterableDoctorLocationList = new ArrayList<>();
    ArrayList<FilterDataDTO> patientList = new ArrayList<>();
    private ArrayList<FilterDataDTO> searchedPatientList = new ArrayList<>();

    CarePayTextView goBackTextview;
    CarePayTextView filterOnTextView;
    CarePayTextView filterTextView;
    CarePayTextView checkingInCounterTextview;
    CarePayTextView waitingCounterTextview;
    private boolean isFilterOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_in);
        patientFiltered = false;
        this.context = this;
        setNavigationBarVisibility();
        initializationView();
        populateList();
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
        checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
        checkinginRecyclerView.setOnDragListener(onCheckingInListDragListener);

        waitingRoomRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
        waitingRoomRecyclerView.setHasFixedSize(true);
        waitingRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
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
                intent.putExtra("appointments_checking_in", "" + checkingInAppointments.size());
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
                CustomFilterPopupWindow customFilterPopupWindow = new CustomFilterPopupWindow(
                        CheckInActivity.this, findViewById(R.id.activity_checked_in),
                        filterableDoctorLocationList, patientList, searchedPatientList);
                customFilterPopupWindow.setTitle(Label.getLabel("practice_checkin_filter"));
                customFilterPopupWindow.setSearchHint(Label.getLabel("practice_checkin_filter_find_patient_by_name"));
                customFilterPopupWindow.setClearFiltersButtonText(Label.getLabel("practice_checkin_filter_clear_filters"));
                customFilterPopupWindow.showPopWindow();
                customFilterPopupWindow.showClearFilterButton(isFilterOn);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void populateList() {
        if (checkInDTO != null && checkInDTO.getPayload() != null && checkInDTO.getPayload().getAppointments().size() > 0) {
            List<AppointmentDTO> appointments = checkInDTO.getPayload().getAppointments();

            ArrayList<FilterDataDTO> doctorsList = new ArrayList<>();
            ArrayList<FilterDataDTO> locationsList = new ArrayList<>();
            patientList = new ArrayList<>();

            for (AppointmentDTO appointmentDTO : appointments) {
                AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
                if (appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase(getString(R.string.checked_in))) {
                    waitingRoomAppointments.add(appointmentPayloadDTO);
                } else if (appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase(getString(R.string.pending)) ||
                        appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase(getString(R.string.checking_in))) {
                    checkingInAppointments.add(appointmentPayloadDTO);
                }
                addProviderOnProviderFilterList(doctorsList, appointmentPayloadDTO);
                addLocationOnFilterList(locationsList, appointmentPayloadDTO);
                addPatientOnFilterList(patientList, appointmentPayloadDTO);
            }
            applySortByAppointmentTime(waitingRoomAppointments);
            applySortByAppointmentTime(checkingInAppointments);

            applyFilterSortByName(patientList);
            setFilterableData(doctorsList, locationsList);

            setAdapter();
        }
    }

    private void setAdapter() {
        checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
        waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));

        checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, checkingInAppointments,
                checkInDTO.getPayload().getPatientBalances(), false);
        checkinginRecyclerView.setAdapter(checkedInAdapter);

        waitingRoomAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, waitingRoomAppointments,
                checkInDTO.getPayload().getPatientBalances(), true);
        waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);
    }

    private void setFilterableData(ArrayList<FilterDataDTO> doctorsList, ArrayList<FilterDataDTO> locationsList) {
        applyFilterSortByName(doctorsList);
        applyFilterSortByName(locationsList);
        filterableDoctorLocationList.add(new FilterDataDTO(Label.getLabel("practice_checkin_filter_doctors", "Doctors")));
        filterableDoctorLocationList.addAll(doctorsList);
        filterableDoctorLocationList.add(new FilterDataDTO(Label.getLabel("practice_checkin_filter_locations", "Locations")));
        filterableDoctorLocationList.addAll(locationsList);
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locationsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;
        LocationDTO locationDTO = appointmentPayloadDTO.getLocation();
        filterDataDTO = new FilterDataDTO(locationDTO.getId(), locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if (locationsList.indexOf(filterDataDTO) < 0) {
            //filterDataDTO.setFilterId(LocationDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            locationsList.add(filterDataDTO);
        } else {
            filterDataDTO = locationsList.get(locationsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patientsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;
        PatientModel patientDTO = appointmentPayloadDTO.getPatient();

        filterDataDTO = new FilterDataDTO(patientDTO.getPatientId(), patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
        if (patientsList.indexOf(filterDataDTO) < 0) {
            //filterDataDTO.setFilterId(patientDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            patientsList.add(filterDataDTO);
        } else {
            filterDataDTO = patientsList.get(patientsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctorsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;// For filtering
        ProviderDTO providerDTO = appointmentPayloadDTO.getProvider();
        filterDataDTO = new FilterDataDTO(providerDTO.getId(), providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
        if (doctorsList.indexOf(filterDataDTO) < 0) {
            //filterDataDTO.setFilterId(providerDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            doctorsList.add(filterDataDTO);
        } else {
            filterDataDTO = doctorsList.get(doctorsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    View.OnDragListener onCheckingInListDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            switch (dragEvent.getAction()) {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    AppointmentPayloadDTO draggedAppointment = getAppintmentById(
                            dragEvent.getClipDescription().getLabel().toString(), waitingRoomAppointments);
                    if (draggedAppointment == null) {
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
                    AppointmentPayloadDTO appointmentDTO = getAppintmentById(
                            dragEvent.getClipDescription().getLabel().toString(), waitingRoomAppointments);

                    if (appointmentDTO != null) {
                        waitingRoomAppointments.remove(appointmentDTO);
                        appointmentDTO.getAppointmentStatus().setName("Pending");

                        checkingInAppointments.add(appointmentDTO);
                        applySortByAppointmentTime(waitingRoomAppointments);
                        applySortByAppointmentTime(checkingInAppointments);
                        waitingRoomAdapter.notifyDataSetChanged();
                        checkedInAdapter.notifyDataSetChanged();
                        checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
                        waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
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
                    AppointmentPayloadDTO appointmentDTO = getAppintmentById(
                            dragEvent.getClipDescription().getLabel().toString(), checkingInAppointments);

                    if (appointmentDTO != null) {
                        checkingInAppointments.remove(appointmentDTO);
                        appointmentDTO.getAppointmentStatus().setName("Checked-In");

                        waitingRoomAppointments.add(appointmentDTO);
                        applySortByAppointmentTime(waitingRoomAppointments);
                        applySortByAppointmentTime(checkingInAppointments);
                        waitingRoomAdapter.notifyDataSetChanged();
                        checkedInAdapter.notifyDataSetChanged();
                        checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
                        waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));

                        ((TextView) findViewById(R.id.drop_here_icon)).setText(Label.getLabel("practice_checkin_success_label"));
                        ((TextView) findViewById(R.id.drop_here_icon)).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icn_check, 0, 0);
                        onCheckInAppointment(appointmentDTO);
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

    private void onCheckInAppointment(AppointmentPayloadDTO appointmentDTO) {
        Map<String, String> queryMap = new HashMap<>();
        if (checkInDTO.getPayload().getAppointments() != null && checkInDTO.getPayload().getAppointments().size() > 0) {
            queryMap.put("practice_mgmt", checkInDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", checkInDTO.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
        }
        queryMap.put("appointment_id", appointmentDTO.getId());
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

    private void applySortByAppointmentTime(ArrayList<AppointmentPayloadDTO> appointments) {
        Collections.sort(appointments, new Comparator<AppointmentPayloadDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(AppointmentPayloadDTO lhs, AppointmentPayloadDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getStartTime().compareTo(rhs.getStartTime());
                }
                return -1;
            }
        });
    }

    private void applyFilterSortByName(ArrayList<FilterDataDTO> filterableList) {
        Collections.sort(filterableList, new Comparator<FilterDataDTO>() {
            @Override
            public int compare(FilterDataDTO lhs, FilterDataDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getDisplayText().compareTo(rhs.getDisplayText());
                }
                return -1;
            }
        });
    }

    private AppointmentPayloadDTO getAppintmentById(String appointmentId, ArrayList<AppointmentPayloadDTO> appointments) {
        for (AppointmentPayloadDTO appointmentDTO : appointments) {
            if (appointmentDTO.getId().equalsIgnoreCase(appointmentId)) {
                return appointmentDTO;
            }
        }
        return null;
    }

    @Override
    public void applyFilter(HashSet<String> appointments) {
        List<AppointmentDTO> appointmentDTOs = checkInDTO.getPayload().getAppointments();
        checkingInAppointments.clear();
        waitingRoomAppointments.clear();
        for (AppointmentDTO appointmentDTO : appointmentDTOs) {
            AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();
            if (appointments.contains(appointmentPayloadDTO.getId())) {
                if (appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase(getString(R.string.checked_in))) {
                    waitingRoomAppointments.add(appointmentPayloadDTO);
                } else {
                    checkingInAppointments.add(appointmentPayloadDTO);
                }
            }
        }

        if (appointments.size() == appointmentDTOs.size()) {
            filterOnTextView.setVisibility(View.GONE);
            filterTextView.setVisibility(View.VISIBLE);
            isFilterOn = false;
        } else {
            filterOnTextView.setVisibility(View.VISIBLE);
            filterTextView.setVisibility(View.GONE);
            isFilterOn = true;
        }
        setAdapter();
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
        AppointmentDetailDialog dialog = new AppointmentDetailDialog(context,
                checkInDTO, getPatientBalanceDTOs(appointmentPayloadDTO.getPatient().getPatientId()),
                appointmentPayloadDTO, isWaitingRoom);
        dialog.show();
    }

    /**
     * if patients was filtered by provider or location set TRUE or FALSE
     *
     * @param patientFiltered true or false if patient screen state on filterpopup
     */
    public void setPatientFiltered(boolean patientFiltered) {
        this.patientFiltered = patientFiltered;
    }

    /**
     * patients was filtered flag
     *
     * @return if patient was filtered by provider or location
     */
    public boolean isPatientFiltered() {
        return this.patientFiltered;
    }

    @Override
    public void startPartialPayment() {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodDialogFragment fragment = new PracticePaymentMethodDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(String selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Gson gson = new Gson();
            Bundle args = new Bundle();
            String paymentsDTOString = gson.toJson(paymentsModel);
            args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
            args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
            args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

            DialogFragment fragment = new NoAddChooseCreditCardFragment();
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            //TODO show alert no cards on file

        }
    }

    @Override
    public void onPaymentPlanAction() {

    }

    @Override
    public void showReceipt(PaymentsModel paymentsModel) {
        PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(this, paymentsModel, paymentsModel);
        receiptDialog.show();
    }

    @Override
    public void showAddCard(double amount) {

    }
}