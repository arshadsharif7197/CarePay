package com.carecloud.carepay.practice.library.checkin;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.customcomponent.AppointmentStatusCartView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CheckInActivity extends BasePracticeActivity {

    private RecyclerView checkinginRecyclerView;

    private RecyclerView waitingRoomRecyclerView;

    CheckInDTO checkInDTO;

    ArrayList<AppointmentDTO> checkingInAppointments = new ArrayList<>();
    ArrayList<AppointmentDTO> waitingRoomAppointments = new ArrayList<>();

    CheckedInAppointmentAdapter checkedInAdapter;
    CheckedInAppointmentAdapter waitingRoomAdapter;
    TextView checkingInCounterTextview;
    TextView waitingCounterTextview;

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
        populateLis();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void populateLis() {
        if (checkInDTO != null && checkInDTO.getPayload() != null && checkInDTO.getPayload().getAppointments().size() > 0) {

            for (AppointmentDTO appointmentDTO : checkInDTO.getPayload().getAppointments()) {
                if (appointmentDTO.getPayload().getAppointmentStatus().getName().equalsIgnoreCase("Checked-In")) {
                    waitingRoomAppointments.add(appointmentDTO);
                } else {
                    checkingInAppointments.add(appointmentDTO);
                }
            }
            applySort(waitingRoomAppointments);
            applySort(checkingInAppointments);
            checkingInCounterTextview.setText(String.valueOf(checkingInAppointments.size()));
            waitingCounterTextview.setText(String.valueOf(waitingRoomAppointments.size()));
            checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, checkingInAppointments);
            checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
            checkinginRecyclerView.setAdapter(checkedInAdapter);

            waitingRoomAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, waitingRoomAppointments);
            waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
            waitingRoomRecyclerView.setAdapter(waitingRoomAdapter);

            waitingRoomRecyclerView.setOnDragListener(new View.OnDragListener() {
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
                            AppointmentDTO appointmentDTO = getAppintmentById(dragEvent.getClipDescription().getLabel().toString(), checkingInAppointments);
                            checkingInAppointments.remove(appointmentDTO);
                            appointmentDTO.getPayload().getAppointmentStatus().setName("Checked-In");
                            waitingRoomAppointments.add(appointmentDTO);
                            applySort(waitingRoomAppointments);
                            applySort(checkingInAppointments);
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
            });

            checkinginRecyclerView.setOnDragListener(new View.OnDragListener() {
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
                            AppointmentDTO appointmentDTO = getAppintmentById(dragEvent.getClipDescription().getLabel().toString(), waitingRoomAppointments);
                            waitingRoomAppointments.remove(appointmentDTO);
                            appointmentDTO.getPayload().getAppointmentStatus().setName("Pending");
                            checkingInAppointments.add(appointmentDTO);
                            applySort(waitingRoomAppointments);
                            applySort(checkingInAppointments);
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
            });
        }
    }

    private void applySort(ArrayList<AppointmentDTO> appointments) {
        Collections.sort(appointments, new Comparator<AppointmentDTO>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(AppointmentDTO lhs, AppointmentDTO rhs) {
                return lhs.getPayload().getStartTime().compareTo(rhs.getPayload().getStartTime());
            }
        });
    }

    private AppointmentDTO getAppintmentById(String appointmentId, ArrayList<AppointmentDTO> appointments) {
        for (AppointmentDTO appointmentDTO : appointments) {
            if (appointmentDTO.getPayload().getId().equalsIgnoreCase(appointmentId)) {
                return appointmentDTO;
            }
        }
        return null;
    }


    /*private void getDemographicInformation() {
        AppointmentService apptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.getCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {

                if(response.code()==200 && response.body().getPayload()!=null && response.body().getPayload().getAppointments()!=null) {
                    CheckedInAppointmentAdapter checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, new ArrayList<>(response.body().getPayload().getAppointments()));
                    checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
                    checkinginRecyclerView.setAdapter(checkedInAdapter);

                    waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
                    waitingRoomRecyclerView.setAdapter(checkedInAdapter);
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {

            }
        });
    }*/
}