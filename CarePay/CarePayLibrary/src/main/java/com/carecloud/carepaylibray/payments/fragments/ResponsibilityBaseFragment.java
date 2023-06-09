package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.interfaces.ResponsibilityPaymentInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ResponsibilityBaseFragment extends BaseCheckinFragment
        implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    protected static final String LOG_TAG = ResponsibilityBaseFragment.class.getSimpleName();
    protected AppCompatActivity appCompatActivity;

    protected String totalResponsibilityString;
    protected String payTotalAmountString;
    protected String payPartialAmountString;
    protected String paymentsTitleString;
    protected String payLaterString;
    protected double total;
    protected double nonBalanceTotal;
    protected boolean mustAddToExisting = false;


    protected ResponsibilityPaymentInterface actionCallback;
    protected boolean hasPermissionToViewBalanceDetails = true;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                actionCallback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                actionCallback = (ResponsibilityPaymentInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (actionCallback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();

    }

    protected void fillDetailAdapter(View view, List<PendingBalancePayloadDTO> pendingBalancePayloads) {
        RecyclerView paymentDetailsListRecyclerView = view.findViewById(R.id.responsibility_line_item_recycle_view);
        paymentDetailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(),
                pendingBalancePayloads, this, hasPermissionToViewBalanceDetails);
        paymentDetailsListRecyclerView.setAdapter(adapter);
    }

    protected List<PendingBalancePayloadDTO> getAllPendingBalancePayloads(List<PendingBalanceDTO> pendingBalances) {
        List<PendingBalancePayloadDTO> pendingBalancePayloads = new ArrayList<>();
        for (PendingBalanceDTO pendingBalance : pendingBalances) {
            pendingBalancePayloads.addAll(pendingBalance.getPayload());
        }
        return pendingBalancePayloads;
    }


    protected void getPaymentInformation() {
        if (paymentsModel == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                try {
                    paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
                } catch (Exception e) {
                    Log.e("PAYMENT_ERROR", e.getMessage());
                }
            }
        }
    }

    /**
     * payment labels
     */
    protected void getPaymentLabels() {
        totalResponsibilityString = Label.getLabel("your_total_patient_responsibility");
        payTotalAmountString = Label.getLabel("payment_pay_total_amount_button");
        payPartialAmountString = Label.getLabel("payment_partial_amount_button");
        payLaterString = Label.getLabel("payment_responsibility_pay_later");
        paymentsTitleString = Label.getLabel("payment_patient_balance_toolbar");
    }

    protected void createPaymentModel(double payAmount) {
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

                AppointmentDTO appointmentDTO = actionCallback.getAppointment();
                if (appointmentDTO != null) {
                    paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                    paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                }

                switch (responsibility.getType()) {
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

    protected boolean isPartialPayAvailable(String practiceId, double total) {
        if (practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    if (payloadSettingsDTO.getPayload().getRegularPayments().isAllowPartialPayments()) {
                        double minBalance = payloadSettingsDTO.getPayload().getRegularPayments().getPartialPaymentsThreshold();
                        double minPayment = payloadSettingsDTO.getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
                        return total >= minBalance && total >= minPayment;
                    }

                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public DTO getDto() {
        return paymentsModel;
    }

    protected boolean isPaymentPlanAvailable(String practiceId, double balance) {
        double adjustedBalance = SystemUtil.safeSubtract(balance, nonBalanceTotal);
        if (adjustedBalance <= 0) {
            return false;
        }
        if (practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    PaymentsSettingsPaymentPlansDTO paymentPlanSettings = payloadSettingsDTO.getPayload().getPaymentPlans();
                    if (!paymentPlanSettings.isPaymentPlansEnabled()) {
                        return false;
                    }

                    double maxAllowablePayment = paymentsModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
                    if (maxAllowablePayment > adjustedBalance) {
                        maxAllowablePayment = adjustedBalance;
                    }
                    for (PaymentSettingsBalanceRangeRule rule : paymentPlanSettings.getBalanceRangeRules()) {
                        if (maxAllowablePayment >= rule.getMinBalance().getValue() &&
                                maxAllowablePayment <= rule.getMaxBalance().getValue()) {
                            //found a valid rule that covers this balance
                            if (paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty()) {
                                //don't already have an existing plan so this is the first plan
                                return true;
                            } else if (paymentPlanSettings.isCanHaveMultiple()) {
                                // already have a plan so need to see if I can create a new one
                                return true;
                            }
                            break;//don't need to continue going through these rules
                        }
                    }

                    //check if balance can be added to existing
                    double minAllowablePayment = paymentsModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
                    if (minAllowablePayment > adjustedBalance) {
                        minAllowablePayment = adjustedBalance;
                    }
                    if (paymentPlanSettings.isAddBalanceToExisting() &&
                            !paymentsModel.getPaymentPayload().getValidPlans(practiceId, minAllowablePayment).isEmpty()) {
                        mustAddToExisting = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

}