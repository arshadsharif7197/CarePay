package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.interfaces.PracticeAppointmentDialogListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 4/26/17
 */
public class CancelAppointmentConfirmDialogFragment extends BaseDialogFragment {

    private AppointmentDTO appointmentDTO;
    private PracticeAppointmentDialogListener callback;
    private AppointmentsResultModel checkInDto;

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
            callback = (PracticeAppointmentDialogListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement CancelAppointmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        checkInDto = (AppointmentsResultModel) callback.getDto();
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_cancel_appointment_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        if (appointmentDTO != null) {
            String confirmMessageTemplate = Label.getLabel("appointment_confirm_cancel_message");
            TextView messageView = view.findViewById(R.id.cancel_confirm_message);
            if (confirmMessageTemplate != null && confirmMessageTemplate.contains("%s")) {
                String formattedMessage = String.format(confirmMessageTemplate, appointmentDTO.getPayload().getPatient().getFullName());
                messageView.setText(formattedMessage);
            } else {
                messageView.setText(CarePayConstants.NOT_DEFINED);
            }

            Button confirmButton = view.findViewById(R.id.confirm_button);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelAppointment(appointmentDTO);
                }
            });

        }

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(cancelListener);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(cancelListener);

    }

    private void cancelAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDto.getMetadata().getTransitions().getCancelAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());
        getWorkflowServiceHelper().execute(transitionDTO, getAppointmentsServiceCallback(appointmentDTO), queryMap);
    }

    WorkflowServiceCallback getAppointmentsServiceCallback(final AppointmentDTO appointmentDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                SystemUtil.showSuccessToast(getContext(), Label
                        .getLabel("appointment_cancellation_success_message_HTML"));
                logAppointmentAcceptedMixPanelEvent(appointmentDTO);
                callback.refreshData();
                dismiss();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }

        };
    }

    private void logAppointmentAcceptedMixPanelEvent(AppointmentDTO appointmentDTO) {
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_patient_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id)};
        String[] values = {appointmentDTO.getPayload().getVisitType().getName(),
                getApplicationMode().getUserPracticeDTO().getPracticeId(),
                getApplicationMode().getUserPracticeDTO().getPracticeName(),
                appointmentDTO.getMetadata().getPatientId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid()};
        MixPanelUtil.logEvent(getString(R.string.event_appointment_cancelled), params, values);
    }

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancel();
        }
    };
}
