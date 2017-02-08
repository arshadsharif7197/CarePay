package com.carecloud.carepay.practice.library.checkin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.practice.library.checkin.dialog.AppointmentDetailDialog;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientBalanceDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterPopupWindow;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CheckInActivity extends BasePracticeActivity implements CustomFilterPopupWindow.FilterCallBack {

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
    ArrayList<FilterDataDTO> patientList;
    private ArrayList<FilterDataDTO> searchedPatientList = new ArrayList<>();

    CarePayTextView goBackTextview;
    CarePayTextView filterOnTextView;
    CarePayTextView filterTextView;
    CarePayTextView checkingInTextView;
    CarePayTextView waitingRoomTextView;
    CarePayTextView checkingInCounterTextview;
    CarePayTextView waitingCounterTextview;
    private boolean isFilterOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        checkInDTO = getConvertedDTO(CheckInDTO.class);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_in);
        patientFiltered=false;
        this.context = this;
        setNavigationBarVisibility();
        initializationView();
        populateList();
    }

    private void initializationView() {
        goBackTextview = (CarePayTextView) findViewById(R.id.goBackTextview);
        filterOnTextView = (CarePayTextView) findViewById(R.id.filterOnTextView);
        filterTextView = (CarePayTextView) findViewById(R.id.filterTextView);
        checkingInTextView = (CarePayTextView) findViewById(R.id.checkingInTextView);
        waitingRoomTextView = (CarePayTextView) findViewById(R.id.waitingRoomTextView);
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

        setLabels();

    }

    private void setLabels() {
        CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
        if (checkInLabelDTO != null) {
            goBackTextview.setText(checkInLabelDTO.getGoBack());
            filterOnTextView.setText(checkInLabelDTO.getPracticeCheckinFilterOn());
            filterTextView.setText(checkInLabelDTO.getPracticeCheckinFilter());
            checkingInTextView.setText(checkInLabelDTO.getPracticeCheckinDetailDialogCheckingIn().toUpperCase());
            waitingRoomTextView.setText(checkInLabelDTO.getPracticeCheckinWaitingRoom().toUpperCase());
            checkingInCounterTextview.setText("0");
            waitingCounterTextview.setText("0");
        }
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
                CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
                CustomFilterPopupWindow customFilterPopupWindow = new CustomFilterPopupWindow(
                        CheckInActivity.this, findViewById(R.id.activity_checked_in),
                        filterableDoctorLocationList, patientList, searchedPatientList);

                if (checkInLabelDTO != null) {
                    customFilterPopupWindow.setTitle(StringUtil.getFormatedLabal(
                            CheckInActivity.this, checkInLabelDTO.getPracticeCheckinFilter()));
                    customFilterPopupWindow.setSearchHint(StringUtil.getFormatedLabal(
                            CheckInActivity.this, checkInLabelDTO.getPracticeCheckinFilterFindPatientByName()));
                    customFilterPopupWindow.setClearFiltersButtonText(StringUtil.getFormatedLabal(
                            CheckInActivity.this, checkInLabelDTO.getPracticeCheckinFilterClearFilters()));
                }
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

        checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, checkingInAppointments, false);
        checkinginRecyclerView.setAdapter(checkedInAdapter);

        waitingRoomAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, waitingRoomAppointments, true);
        waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);
    }

    private void setFilterableData(ArrayList<FilterDataDTO> doctorsList, ArrayList<FilterDataDTO> locationsList) {
        applyFilterSortByName(doctorsList);
        applyFilterSortByName(locationsList);

        CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();

        filterableDoctorLocationList.add(new FilterDataDTO(checkInLabelDTO.getPracticeCheckinFilterDoctors(), FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(doctorsList);
        filterableDoctorLocationList.add(new FilterDataDTO(checkInLabelDTO.getPracticeCheckinFilterLocations(), FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(locationsList);
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locationsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;
        LocationDTO locationDTO = appointmentPayloadDTO.getLocation();
        filterDataDTO = new FilterDataDTO(locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if (locationsList.indexOf(filterDataDTO) < 0) {
            //filterDataDTO.setFilterId(locationDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            locationsList.add(filterDataDTO);
        } else {
            filterDataDTO = locationsList.get(locationsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patientsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;
        PatientDTO patientDTO = appointmentPayloadDTO.getPatient();

        filterDataDTO = new FilterDataDTO(patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
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
        filterDataDTO = new FilterDataDTO(providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
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
        queryMap.put("appointment_id" , appointmentDTO.getId());
        TransitionDTO transitionDTO = checkInDTO.getMetadata().getTransitions().getCheckinAppointment();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, checkInAppointmentCallback, queryMap);
    }

    private WorkflowServiceCallback checkInAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
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

    private PatientBalanceDTO getPatientBalanceDTO(String patientId) {
        List<PatientBalanceDTO> patientBalanceDTOList = checkInDTO.getPayload().getPatientBalances();
        for (int i = 0; i < patientBalanceDTOList.size(); i++) {
            if (patientBalanceDTOList.get(i).getMetadata().getPatientId().equalsIgnoreCase(patientId)) {
                return patientBalanceDTOList.get(i);
            }
        }
        return null;
    }

    private AppointmentDTO getPatientBalanceDTOs(String patientId) {
        List<AppointmentDTO>allApps =  checkInDTO.getPayload().getAppointments();
        for (AppointmentDTO ap:allApps)
              {
                  if(ap.getPayload().getPatient().getId().equalsIgnoreCase(patientId)){
                      return ap;
                  }
        }
        return null;
    }

    /**
     * On check in item click.
     *
     * @param appointmentPayloadDTO the appointment payload dto
     */
    public void onCheckInItemClick(AppointmentPayloadDTO appointmentPayloadDTO, boolean isWaitingroom) {
        AppointmentDetailDialog dialog = new AppointmentDetailDialog(context,
                checkInDTO, getPatientBalanceDTOs(appointmentPayloadDTO.getPatient().getId()),
                appointmentPayloadDTO, isWaitingroom);
        dialog.show();
    }

    /**
     * if patients was filtered by provider or location set TRUE or FALSE
     * @param patientFiltered true or false if patient screen state on filterpopup
     */
    public void setPatientFiltered(boolean patientFiltered){
        this.patientFiltered=patientFiltered;
    }

    /**
     * patients was filtered flag
     * @return if patient was filtered by provider or location
     */
    public boolean isPatientFiltered(){
        return this.patientFiltered;
    }

}