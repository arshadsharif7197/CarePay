package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.interfaces.ResponsibilityPaymentInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.ResponsibilityType;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class ResponsibilityBaseFragment extends BaseCheckinFragment
        implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    protected static final String LOG_TAG = ResponsibilityBaseFragment.class.getSimpleName();
    protected AppCompatActivity appCompatActivity;

    protected PaymentsModel paymentDTO = null;
    protected String totalResponsibilityString;
    protected String payTotalAmountString;
    protected String payPartialAmountString;
    protected String paymentsTitleString;
    protected String payLaterString;
    protected double total;

    protected ResponsibilityPaymentInterface actionCallback;

    @Override
    public void attachCallback(Context context) {
        try {
            if(context instanceof PaymentViewHandler){
                actionCallback = ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                actionCallback = (ResponsibilityPaymentInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(actionCallback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    protected void fillDetailAdapter(View view, List<PendingBalancePayloadDTO> pendingBalancePayloads) {
        RecyclerView paymentDetailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.responsibility_line_item_recycle_view));
        paymentDetailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(), pendingBalancePayloads, this);
        paymentDetailsListRecyclerView.setAdapter(adapter);
    }

    protected List<PendingBalancePayloadDTO> getAllPendingBalancePayloads(List<PendingBalanceDTO> pendingBalances){
        List<PendingBalancePayloadDTO> pendingBalancePayloads = new ArrayList<>();
        for(PendingBalanceDTO pendingBalance : pendingBalances){
            pendingBalancePayloads.addAll(pendingBalance.getPayload());
        }
        return pendingBalancePayloads;
    }


    protected void getPaymentInformation() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            try {
                paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
            } catch (Exception e) {
                Log.e("PAYMENT_ERROR", e.getMessage());
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

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    protected void createPaymentModel(double payAmount){
        PaymentPostModel postModel = paymentDTO.getPaymentPayload().getPaymentPostModel();
        if( postModel == null){
            postModel = new PaymentPostModel();
        }
        postModel.setAmount(payAmount);

        List<PendingBalancePayloadDTO> responsibilityTypes = getPendingResponsibilityTypes();
        for(PendingBalancePayloadDTO responsibility : responsibilityTypes){
            if(payAmount > 0D) {
                double itemAmount;
                if (payAmount >= responsibility.getAmount()) {
                    itemAmount = responsibility.getAmount();
                } else {
                    itemAmount = payAmount;
                }
                payAmount = (double) Math.round((payAmount - itemAmount) * 100) / 100;

                PaymentObject paymentObject = new PaymentObject();
                paymentObject.setAmount(itemAmount);
                switch (responsibility.getType()){
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

        if(payAmount > 0){//payment is greater than any responsibility types
            PaymentObject paymentObject = new PaymentObject();
            paymentObject.setAmount(payAmount);
            paymentObject.setDescription("Unapplied Amount");

            postModel.addPaymentMethod(paymentObject);
        }

        paymentDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<PendingBalancePayloadDTO> getPendingResponsibilityTypes(){
        List<PendingBalancePayloadDTO> responsibilityTypes = new ArrayList<>();
        for(PatientBalanceDTO patientBalanceDTO : paymentDTO.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()){
                for(PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()){
                    switch (pendingBalancePayloadDTO.getType()){
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