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
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.signinsignup.dto.SignInPayloadMetadata;
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
            logAppointmentCancelMixPanel(appointmentDTO);
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
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
        ((MenuPatientActivity) getActivity()).displayToolbar(false, null);
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("appointments_cancel_heading"));
    }

    private void setUpUI(View view) {
        reasonEditText = view.findViewById(R.id.reasonEditText);
        cancelAppointmentButton = view.findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setOnClickListener(v -> {
            final AppointmentCancellationFee cancellationFee = viewModel.getCancellationFee(appointmentDTO);
            if (cancellationFee == null) {
                onCancelAppointment();
            } else {
                initPaymentFlow(cancellationFee);
            }
        });
    }

    private void onCancelAppointment() {
        viewModel.cancelAppointment(appointmentDTO, selectedCancelationReason
                .getAppointmentCancellationReason().getId(), reasonEditText.getText().toString());
    }

    private void initPaymentFlow(AppointmentCancellationFee cancellationFee) {
        preparePostModel(cancellationFee);
        PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, Double.parseDouble(cancellationFee.getAmount()),
                        Label.getLabel("appointment_cancellation_fee_title"));
        callback.addFragment(prepaymentFragment, true);
        logCancelationStartedMixPanel(cancellationFee);
    }

    private void preparePostModel(AppointmentCancellationFee cancellationFee) {
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(Double.parseDouble(cancellationFee.getAmount()));
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(Double.parseDouble(cancellationFee.getAmount()));
        paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
        paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_CANCELLATION);


        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentId(appointmentDTO.getPayload().getId());
        postModel.getMetadata().setCancellationReasonId(String
                .valueOf(selectedCancelationReason.getAppointmentCancellationReason().getId()));

        SignInPayloadMetadata queryMetadata = new SignInPayloadMetadata();
        queryMetadata.setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        queryMetadata.setPatientId(appointmentDTO.getMetadata().getPatientId());
        queryMetadata.setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        postModel.setQueryMetadata(queryMetadata);

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private void logCancelationStartedMixPanel(AppointmentCancellationFee cancellationFee) {
        String[] params = {getString(R.string.param_payment_amount),
                getString(R.string.param_provider_id),
                getString(R.string.param_practice_id),
                getString(R.string.param_location_id)
        };
        Object[] values = {cancellationFee.getAmount(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getMetadata().getPracticeId(),
                appointmentDTO.getPayload().getLocation().getGuid()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_cancellation_started), params, values);
    }

    private void setUpCancelReasons(View view, List<CancellationReasonDTO> cancellationReasons) {
        RecyclerView reasonsRecyclerView = view.findViewById(R.id.reasonsRecyclerView);
        reasonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reasonsRecyclerView.setAdapter(new CancelReasonAdapter(cancellationReasons, this));
    }

    public void setsCancelReasonAppointmentDialogListener(CancelReasonAppointmentDialogListener listener) {
//        this.callback = listener;
    }

    private void logAppointmentCancelMixPanel(AppointmentDTO appointmentDTO) {
        AppointmentCancellationFee cancellationFee = viewModel.getCancellationFee(appointmentDTO);

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