package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjohnson on 23/03/17
 */

public class PracticePartialPaymentDialogFragment extends PartialPaymentBaseDialogFragment {

    private PaymentsModel paymentsModel;
    private double fullAmount;
    private PaymentNavigationCallback callback;
    private TextView pendingAmountTextView;
    private double minimumPayment;

    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         fullAmount owed
     * @return an instance of PracticePartialPaymentDialogFragment
     */
    public static PracticePartialPaymentDialogFragment newInstance(PaymentsModel paymentResultModel, double owedAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        args.putDouble("fullAmount", owedAmount);
        PracticePartialPaymentDialogFragment fragment = new PracticePartialPaymentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
        fullAmount = arguments.getDouble("fullAmount");
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        pendingAmountTextView = (TextView) view.findViewById(R.id.pendingAmountTextView);
        pendingAmountTextView.setVisibility(View.VISIBLE);
        updatePendingAmountText(fullAmount);

        minimumPayment = getMinimumPayment();
        if(minimumPayment > 0){
            TextView header = (TextView) view.findViewById(R.id.partialPaymentHeader);
            String headerText = Label.getLabel("payment_partial_minimum_amount") + NumberFormat.getCurrencyInstance().format(minimumPayment);
            header.setText(headerText);
        }
    }

    @Override
    protected void updateLayout() {
        double entry = StringUtil.isNullOrEmpty(numberStr) ? 0D : Double.parseDouble(numberStr);
        updatePendingAmountText(fullAmount - entry);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if ((view.getId() == R.id.enter_amount_button) && !StringUtil.isNullOrEmpty(numberStr)) {
            double amount = Double.parseDouble(numberStr);
            if(amount > fullAmount){
                String errorMessage = Label.getLabel("payment_partial_max_error") + NumberFormat.getCurrencyInstance().format(fullAmount);
                CustomMessageToast toast = new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR);
                toast.show();
                return;
            }
            if(minimumPayment > 0 && amount < minimumPayment){
                String errorMessage = Label.getLabel("payment_partial_minimum_error") + NumberFormat.getCurrencyInstance().format(minimumPayment);
                CustomMessageToast toast = new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR);
                toast.show();
                return;
            }
            createPaymentModel(amount);
            callback.onPayButtonClicked(amount, paymentsModel);
            dismiss();
        }
    }

    private void updatePendingAmountText(double amount) {
        pendingAmountTextView.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(amount));
    }

    private void createPaymentModel(double payAmount) {
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(payAmount);
        postModel.getLineItems().clear();

        List<PendingBalancePayloadDTO> responsibilityTypes = getPendingResponsibilityTypes();
        for (PendingBalancePayloadDTO responsibility : responsibilityTypes) {
            if (payAmount > 0D) {
                double itemAmount;
                if (payAmount >= responsibility.getAmount()) {
                    itemAmount = responsibility.getAmount();
                } else {
                    itemAmount = payAmount;
                }
                payAmount = (double) Math.round((payAmount - itemAmount) * 100) / 100;

                IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
                paymentLineItem.setAmount(itemAmount);

                AppointmentDTO appointmentDTO = callback.getAppointment();
                if (appointmentDTO != null) {
                    paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                    paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                }

                switch (responsibility.getType()){
                    case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COINSURANCE);
                        break;
                    case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_DEDUCTABLE);
                        break;
                    case PendingBalancePayloadDTO.CO_PAY_TYPE:
                    default:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COPAY);
                        break;
                }

                postModel.addLineItem(paymentLineItem);

            }
        }

        if (payAmount > 0) {//payment is greater than any responsibility types
            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(payAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            postModel.addLineItem(paymentLineItem);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<PendingBalancePayloadDTO> getPendingResponsibilityTypes() {
        List<PendingBalancePayloadDTO> responsibilityTypes = new ArrayList<>();
        for (PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                for (PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()) {
                    switch (pendingBalancePayloadDTO.getType()) {
                        case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:
                        case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                            responsibilityTypes.add(pendingBalancePayloadDTO);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return responsibilityTypes;
    }

    private double getMinimumPayment(){
        return paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
    }

}
