package com.carecloud.carepay.patient.appointments.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.adapters.CancelReasonAdapter;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.List;

public class CancelReasonAppointmentDialog extends BaseDialogFragment
        implements CancelReasonAdapter.CancelReasonItemInterface {

    private AppointmentDTO appointmentDTO;

    private Button cancelAppointmentButton;
    private EditText reasonEditText;

    private AppointmentViewModel viewModel;
    private CancellationReasonDTO selectedCancelationReason;

    public interface CancelReasonAppointmentDialogListener {
        void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO,
                                                          int cancellationReason,
                                                          String cancellationReasonComment);
    }

    private FragmentActivityInterface callback;

    /**
     * Contractor for dialog.
     *
     * @param appointmentDTO appointment Item
     */
    public static CancelReasonAppointmentDialog newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        CancelReasonAppointmentDialog fragment = new CancelReasonAppointmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(AppointmentViewModel.class);
        viewModel.getCancelAppointmentObservable().observe(getActivity(), workflowDTO -> {
            logApptCancelMixPanel(appointmentDTO);
            getFragmentManager().popBackStackImmediate(AppointmentDetailDialog.class.getName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            viewModel.getAppointments(viewModel.getAppointmentsDtoObservable().getValue()
                    .getMetadata().getLinks().getAppointments(), true);
        });
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cancel_reason_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpUI(view);
        setUpCancelReasons(view, viewModel.getAppointmentsDtoObservable().getValue()
                .getPayload().getCancellationReasons());
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(com.carecloud.carepay.patient.R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
        ((MenuPatientActivity) getActivity()).displayToolbar(false, null);
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("appointments_cancel_heading"));
    }

    private void setUpUI(View view) {
        reasonEditText = view.findViewById(R.id.reasonEditText);
        cancelAppointmentButton = view.findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setOnClickListener(v -> onCancelAppointment());
    }

    private void setUpCancelReasons(View view, List<CancellationReasonDTO> cancellationReasons) {
        RecyclerView reasonsRecyclerView = view.findViewById(R.id.reasonsRecyclerView);
        reasonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reasonsRecyclerView.setAdapter(new CancelReasonAdapter(cancellationReasons, this));
    }

    private void onCancelAppointment() {
        viewModel.cancelAppointment(appointmentDTO, selectedCancelationReason
                .getAppointmentCancellationReason().getId(), reasonEditText.getText().toString());
    }

    public void setsCancelReasonAppointmentDialogListener(CancelReasonAppointmentDialogListener listener) {
//        this.callback = listener;
    }

    private void logApptCancelMixPanel(AppointmentDTO appointmentDTO) {
        AppointmentCancellationFee cancellationFee = getCancellationFee(appointmentDTO);

        String[] params = {getString(R.string.param_appointment_cancel_reason),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_provider_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_location_id),
                getString(R.string.param_appointment_type),
                getString(R.string.param_payment_amount)
        };
        Object[] values = {
                reasonEditText.getText().toString(),
                appointmentDTO.getMetadata().getPracticeId(),
                getPracticeName(appointmentDTO.getMetadata().getPracticeId()),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getMetadata().getPatientId(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                appointmentDTO.getPayload().getVisitType().getName(),
                cancellationFee != null ? cancellationFee.getAmount() : null
        };
        MixPanelUtil.logEvent(getString(R.string.event_appointment_cancelled), params, values);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_appointment_cancelled), 1);
    }

    private String getPracticeName(String practiceId) {
        for (UserPracticeDTO practiceDTO : viewModel.getAppointmentsDtoObservable().getValue()
                .getPayload().getUserPractices()) {
            if (practiceDTO.getPracticeId().equals(practiceId)) {
                return practiceDTO.getPracticeName();
            }
        }
        return null;
    }

    private AppointmentCancellationFee getCancellationFee(AppointmentDTO appointmentDTO) {
        AppointmentsSettingDTO practiceSettings = getAppointmentSettings(appointmentDTO.getMetadata().getPracticeId());
        if (practiceSettings.shouldChargeCancellationFees()) {
            for (AppointmentCancellationFee cancellationFee : practiceSettings.getCancellationFees()) {
                if (appointmentDTO.getPayload().getVisitType().getId().equals(cancellationFee.getVisitType())) {
                    return cancellationFee;
                }
            }
        }
        return null;
    }

    private AppointmentsSettingDTO getAppointmentSettings(String practiceId) {
        List<AppointmentsSettingDTO> appointmentsSettingsList = viewModel.getAppointmentsDtoObservable()
                .getValue().getPayload().getAppointmentsSettings();
        for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
            if (appointmentsSettingDTO.getPracticeId().equals(practiceId)) {
                return appointmentsSettingDTO;
            }
        }
        return appointmentsSettingsList.get(0);
    }

    @Override
    public void onCancelReasonSelected(CancellationReasonDTO reasonDTO) {
        selectedCancelationReason = reasonDTO;
        if (reasonDTO.getAppointmentCancellationReason().getCode().equals("OT")) {
            reasonEditText.setEnabled(true);
            reasonEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            reasonEditText.setEnabled(false);
            reasonEditText.setText("");
            reasonEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }
        cancelAppointmentButton.setEnabled(true);
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        return false;
    }
}