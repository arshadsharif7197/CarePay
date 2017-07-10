package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 4/26/17
 */
public class CancelAppointmentConfirmDialogFragment extends BaseDialogFragment {

    private AppointmentDTO appointmentDTO;
    private CancelAppointmentCallback callback;

    public interface CancelAppointmentCallback {
        void cancelAppointment(AppointmentDTO appointmentDTO);

        void showPracticeAppointmentDialog(AppointmentDTO appointmentDTO);
    }

    public static CancelAppointmentConfirmDialogFragment newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);

        CancelAppointmentConfirmDialogFragment fragment = new CancelAppointmentConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (CancelAppointmentCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement CancelAppointmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_cancel_appointment_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        if (appointmentDTO != null) {
            String confirmMessageTemplate = Label.getLabel("appointment_confirm_cancel_message", null);
            TextView messageView = (TextView) view.findViewById(R.id.cancel_confirm_message);
            if (confirmMessageTemplate != null && confirmMessageTemplate.contains("%s")) {
                String formattedMessage = String.format(confirmMessageTemplate, appointmentDTO.getPayload().getPatient().getFullName());
                messageView.setText(formattedMessage);
            } else {
                messageView.setText(CarePayConstants.NOT_DEFINED);
            }

            Button confirmButton = (Button) view.findViewById(R.id.confirm_button);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.cancelAppointment(appointmentDTO);
                    dismiss();
                }
            });

        }

        Button backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(dismissListener);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(dismissListener);

    }

    private View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            callback.showPracticeAppointmentDialog(appointmentDTO);
        }
    };
}
