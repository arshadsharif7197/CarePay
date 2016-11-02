package com.carecloud.carepay.practice.library.checkin;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.LocationDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.ProviderDTO;
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterPopupWindow;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class CheckInActivity extends BasePracticeActivity implements CustomFilterPopupWindow.FilterCallBack {

    private RecyclerView checkinginRecyclerView;

    private RecyclerView waitingRoomRecyclerView;

    CheckInDTO checkInDTO;

    ArrayList<AppointmentPayloadDTO> checkingInAppointments = new ArrayList<>();
    ArrayList<AppointmentPayloadDTO> waitingRoomAppointments = new ArrayList<>();

    CheckedInAppointmentAdapter checkedInAdapter;
    CheckedInAppointmentAdapter waitingRoomAdapter;
    TextView checkingInCounterTextview;
    TextView waitingCounterTextview;
    ArrayList<FilterDataDTO> filterableDoctorLocationList =new ArrayList<>();
    ArrayList<FilterDataDTO> patientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInDTO = getConvertedDTO(CheckInDTO.class);
        setContentView(R.layout.activity_check_in);
        checkinginRecyclerView = (RecyclerView) findViewById(R.id.checkinginRecyclerView);
        checkinginRecyclerView.setHasFixedSize(true);
        checkinginRecyclerView.setItemAnimator(new DefaultItemAnimator());
        waitingRoomRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
        waitingRoomRecyclerView.setHasFixedSize(true);
        waitingRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkingInCounterTextview= (TextView) findViewById(R.id.checkingInCounterTextview);
        waitingCounterTextview= (TextView) findViewById(R.id.waitingCounterTextview);
        findViewById(R.id.goBackTextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        populateList();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void populateList() {
        if (checkInDTO != null && checkInDTO.getPayload() != null && checkInDTO.getPayload().getAppointments().size() > 0) {
            final List<AppointmentDTO> appointments= checkInDTO.getPayload().getAppointments();

            ArrayList<FilterDataDTO> doctorsList = new ArrayList<>();
            ArrayList<FilterDataDTO> locationsList = new ArrayList<>();
            patientList = new ArrayList<>();

            for (AppointmentDTO appointmentDTO : appointments) {
                AppointmentPayloadDTO appointmentPayloadDTO=appointmentDTO.getPayload();
                if (appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase("Checked-In")) {
                    waitingRoomAppointments.add(appointmentPayloadDTO);
                } else {
                    checkingInAppointments.add(appointmentPayloadDTO);
                }
                addProviderOnProviderFilterList(doctorsList, appointmentPayloadDTO);
                addLocationOnFilterList(locationsList, appointmentPayloadDTO);
                addPatientOnFilterList(patientList,appointmentPayloadDTO);
            }
            applySortByAppointmentTime(waitingRoomAppointments);
            applySortByAppointmentTime(checkingInAppointments);

            applyFilterSortByName(patientList);
            setFilterableData(doctorsList, locationsList);

            checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
            waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
            checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, checkingInAppointments);
            checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
            checkinginRecyclerView.setAdapter(checkedInAdapter);
            waitingRoomAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, waitingRoomAppointments);
            waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
            waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);
            waitingRoomRecyclerView.setOnDragListener(onWaitListDragListener);
            checkinginRecyclerView.setOnDragListener(onCheckingInListDragListener);
            findViewById(R.id.filterImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomFilterPopupWindow(CheckInActivity.this,findViewById(R.id.activity_checked_in), filterableDoctorLocationList,patientList).showPopWindow();
                }
            });
        }
    }

    private void setFilterableData(ArrayList<FilterDataDTO> doctorsList, ArrayList<FilterDataDTO> locationsList) {
        applyFilterSortByName(doctorsList);
        applyFilterSortByName(locationsList);
        filterableDoctorLocationList.add(new FilterDataDTO("Doctors", FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(doctorsList);
        filterableDoctorLocationList.add(new FilterDataDTO("Locations", FilterDataDTO.FilterDataType.HEADER));
        filterableDoctorLocationList.addAll(locationsList);
    }

    private void addLocationOnFilterList(ArrayList<FilterDataDTO> locationsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;LocationDTO locationDTO=appointmentPayloadDTO.getLocation();
        filterDataDTO =new FilterDataDTO(locationDTO.getName(), FilterDataDTO.FilterDataType.LOCATION);
        if( locationsList.indexOf(filterDataDTO)<0) {
            //filterDataDTO.setFilterId(locationDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            locationsList.add(filterDataDTO);
        }else{
            filterDataDTO=locationsList.get(locationsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addPatientOnFilterList(ArrayList<FilterDataDTO> patientsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;
        PatientDTO patientDTO=appointmentPayloadDTO.getPatient();

        filterDataDTO =new FilterDataDTO(patientDTO.getFullName(), FilterDataDTO.FilterDataType.PATIENT);
        if( patientsList.indexOf(filterDataDTO)<0) {
            //filterDataDTO.setFilterId(patientDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            patientsList.add(filterDataDTO);
        }else{
            filterDataDTO=patientsList.get(patientsList.indexOf(filterDataDTO));
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
        }
    }

    private void addProviderOnProviderFilterList(ArrayList<FilterDataDTO> doctorsList, AppointmentPayloadDTO appointmentPayloadDTO) {
        FilterDataDTO filterDataDTO;// For filtering
        ProviderDTO providerDTO=appointmentPayloadDTO.getProvider();
        filterDataDTO =new FilterDataDTO(providerDTO.getName(), FilterDataDTO.FilterDataType.PROVIDER);
        if( doctorsList.indexOf(filterDataDTO)<0) {
            //filterDataDTO.setFilterId(providerDTO.getId());
            filterDataDTO.getAppointmentList().add(appointmentPayloadDTO.getId());
            doctorsList.add(filterDataDTO);
        }else{
            filterDataDTO=doctorsList.get(doctorsList.indexOf(filterDataDTO));
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
                    AppointmentPayloadDTO appointmentDTO = getAppintmentById(dragEvent.getClipDescription().getLabel().toString(), waitingRoomAppointments);
                    waitingRoomAppointments.remove(appointmentDTO);
                    if (appointmentDTO != null) {
                        appointmentDTO.getAppointmentStatus().setName("Pending");
                    }
                    checkingInAppointments.add(appointmentDTO);
                    applySortByAppointmentTime(waitingRoomAppointments);
                    applySortByAppointmentTime(checkingInAppointments);
                    waitingRoomAdapter.notifyDataSetChanged();
                    checkedInAdapter.notifyDataSetChanged();
                    checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
                    waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
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
                    AppointmentPayloadDTO appointmentDTO = getAppintmentById(dragEvent.getClipDescription().getLabel().toString(), checkingInAppointments);
                    checkingInAppointments.remove(appointmentDTO);
                    if (appointmentDTO != null) {
                        appointmentDTO.getAppointmentStatus().setName("Checked-In");
                    }
                    waitingRoomAppointments.add(appointmentDTO);
                    applySortByAppointmentTime(waitingRoomAppointments);
                    applySortByAppointmentTime(checkingInAppointments);
                    waitingRoomAdapter.notifyDataSetChanged();
                    checkedInAdapter.notifyDataSetChanged();
                    checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
                    waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
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

    private void applySortByAppointmentTime(ArrayList<AppointmentPayloadDTO> appointments) {
        Collections.sort(appointments, new Comparator<AppointmentPayloadDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(AppointmentPayloadDTO lhs, AppointmentPayloadDTO rhs) {
                return lhs.getStartTime().compareTo(rhs.getStartTime());
            }
        });
    }

    private void applyFilterSortByName(ArrayList<FilterDataDTO> filterableList) {
        Collections.sort(filterableList, new Comparator<FilterDataDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(FilterDataDTO lhs, FilterDataDTO rhs) {
                return lhs.getDisplayText().compareTo(rhs.getDisplayText());
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
        List<AppointmentDTO> appointmentDTOs= checkInDTO.getPayload().getAppointments();
        checkingInAppointments.clear();
        waitingRoomAppointments.clear();
        for (AppointmentDTO appointmentDTO : appointmentDTOs) {
            AppointmentPayloadDTO appointmentPayloadDTO=appointmentDTO.getPayload();
            if(appointments.contains(appointmentPayloadDTO.getId())) {
                if (appointmentPayloadDTO.getAppointmentStatus().getName().equalsIgnoreCase("Checked-In")) {
                    waitingRoomAppointments.add(appointmentPayloadDTO);
                } else {
                    checkingInAppointments.add(appointmentPayloadDTO);
                }
            }
        }
        checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
        waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
        checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, checkingInAppointments);
        checkinginRecyclerView.setAdapter(checkedInAdapter);
        waitingRoomAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, waitingRoomAppointments);
        waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);
    }
}