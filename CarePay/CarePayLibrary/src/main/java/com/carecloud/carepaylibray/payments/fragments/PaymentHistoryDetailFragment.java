package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.adapter.PaymentHistoryDetailAdapter;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.utils.DtoHelper;

import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_ACCOUNT;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_CARD;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 9/29/17
 */

public abstract class PaymentHistoryDetailFragment extends BaseDialogFragment {

    private List<PaymentHistoryLineItem> lineItems = new ArrayList<>();

    protected double totalPaid;
    protected PaymentHistoryItem historyItem;
    protected RecyclerView itemsRecycler;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, args);

        totalPaid = historyItem.getPayload().getTotalPaid();
        lineItems = historyItem.getPayload().getLineItems();
        if(totalPaid > 0 && totalPaid != historyItem.getPayload().getAmount()){
            lineItems = getSuccessfulLineItems();
        }
    }


    protected void setAdapter(){
        PaymentHistoryDetailAdapter adapter = (PaymentHistoryDetailAdapter) itemsRecycler.getAdapter();
        if(adapter != null){
            adapter.setLineItems(lineItems);
        }else{
            adapter = new PaymentHistoryDetailAdapter(getContext(), lineItems);
            itemsRecycler.setAdapter(adapter);
        }
    }

    private List<PaymentHistoryLineItem> getSuccessfulLineItems(){
        List<PaymentHistoryLineItem> successfulItems = new ArrayList<>();
        for(PaymentHistoryLineItem lineItem : lineItems){
            if(lineItem.isProcessed()){
                successfulItems.add(lineItem);
            }
        }
        return successfulItems;
    }

    protected static String getPaymentMethod(PapiPaymentMethod paymentMethod){
        String paymentMethodType = paymentMethod.getPaymentMethodType();
        if(paymentMethodType == null){
            paymentMethodType = "";
        }
        switch (paymentMethodType){
            case PAYMENT_METHOD_ACCOUNT:
                return Label.getLabel("payment_method_account");
            case PAYMENT_METHOD_CARD:
            case PAYMENT_METHOD_NEW_CARD:
            default:
                return Label.getLabel("payment_method_creditcard");
        }
    }

    protected abstract void attachCallback(Context context);

    protected abstract void initItemsRecycler(RecyclerView recycler);

}

