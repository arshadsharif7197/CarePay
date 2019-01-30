package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.createappointment.BaseRequestAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/17/19.
 */
public class RequestAppointmentDialogFragment extends BaseRequestAppointmentDialogFragment {

    public static RequestAppointmentDialogFragment newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        RequestAppointmentDialogFragment fragment = new RequestAppointmentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
