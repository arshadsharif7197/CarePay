package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.ResponsibilityType;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjohnson on 23/03/17
 */

public class PracticePartialPaymentDialogFragment extends PartialPaymentBaseDialogFragment {

    private PaymentsModel paymentsModel;
    private double amount;
    private PaymentNavigationCallback callback;
    private TextView pendingAmountTextView;

    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         amount owed
     * @return an instance of PracticePartialPaymentDialogFragment
     */
    public static PracticePartialPaymentDialogFragment newInstance(PaymentsModel paymentResultModel, double owedAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        args.putDouble("amount", owedAmount);
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
        amount = arguments.getDouble("amount");
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        pendingAmountTextView = (TextView) view.findViewById(R.id.pendingAmountTextView);
        pendingAmountTextView.setVisibility(View.VISIBLE);
        updatePendingAmountText(amount);
    }

    @Override
    protected void updateLayout() {
        double entry = StringUtil.isNullOrEmpty(numberStr) ? 0D : Double.parseDouble(numberStr);
        updatePendingAmountText(amount - entry);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if ((view.getId() == R.id.enter_amount_button) && !StringUtil.isNullOrEmpty(numberStr)) {
            double amount = Double.parseDouble(numberStr);
            createPaymentModel(amount);
            callback.onPayButtonClicked(amount, paymentsModel);
            dismiss();
        }
    }

    private void updatePendingAmountText(double amount) {
        pendingAmountTextView.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(amount));
    }

    private void createPaymentModel(double payAmount) {
        PaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new PaymentPostModel();
        }
        postModel.setAmount(payAmount);

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

                PaymentObject paymentObject = new PaymentObject();
                paymentObject.setAmount(itemAmount);
                switch (responsibility.getType()) {
                    case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        paymentObject.setResponsibilityType(ResponsibilityType.co_insurance);
                        break;
                    case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        paymentObject.setResponsibilityType(ResponsibilityType.deductable);
                        break;
                    case PendingBalancePayloadDTO.CO_PAY_TYPE:
                    default:
                        paymentObject.setResponsibilityType(ResponsibilityType.co_pay);
                        break;
                }
                postModel.addPaymentMethod(paymentObject);
            }
        }

        if (payAmount > 0) {//payment is greater than any responsibility types
            PaymentObject paymentObject = new PaymentObject();
            paymentObject.setAmount(payAmount);
            paymentObject.setDescription("Unapplied Amount");

            postModel.addPaymentMethod(paymentObject);
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

}
