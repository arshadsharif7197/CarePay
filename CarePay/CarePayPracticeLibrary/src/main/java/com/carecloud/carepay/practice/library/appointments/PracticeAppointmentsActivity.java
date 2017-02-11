package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;

import java.util.Locale;

/**
 * Created by cocampo on 2/10/17.
 */

public class PracticeAppointmentsActivity extends BasePracticeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_appointments);

        CheckInDTO checkInDTO = getConvertedDTO(CheckInDTO.class);
        if (null != checkInDTO) {
            initializePatientListView(checkInDTO);
        }

        initializeViews(checkInDTO);
    }

    private void initializePatientListView(CheckInDTO checkInDTO) {
        TwoColumnPatientListView purchaseFragment = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        purchaseFragment.setCheckInDTO(checkInDTO);
        purchaseFragment.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
                AppointmentDTO appointmentDTO = (AppointmentDTO) dto;
                PatientDTO patientDTO = appointmentDTO.getPayload().getPatient();
                String name = patientDTO.getFirstName() + " " + patientDTO.getLastName();

                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews(CheckInDTO checkInDTO) {
        if (checkInDTO != null) {
            CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
            if (checkInLabelDTO != null) {
                setViewTextById(R.id.practice_title, checkInLabelDTO.getGoBack());
                setViewTextById(R.id.practice_go_back, checkInLabelDTO.getGoBack());
                setViewTextById(R.id.practice_patient_count_label, "TO-DO");
            }

            String patientCount = String.format(Locale.getDefault(), "%1s", checkInDTO.getPayload().getAppointments().size());
            setViewTextById(R.id.practice_patient_count, patientCount);
        }

        findViewById(R.id.practice_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
