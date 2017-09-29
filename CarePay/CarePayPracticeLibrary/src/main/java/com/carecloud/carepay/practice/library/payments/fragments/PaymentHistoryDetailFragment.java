package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentHistoryDetailAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentHistoryCallback;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_ACCOUNT;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_CARD;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 9/29/17
 */

public class PaymentHistoryDetailFragment extends BaseDialogFragment {

    private PracticePaymentHistoryCallback callback;
    private PaymentHistoryItem historyItem;
    private List<PaymentHistoryLineItem> lineItems = new ArrayList<>();

    private RecyclerView itemsRecycler;

    /**
     * Get new instance of PaymentHistoryDetailFragment
     * @param historyItem history item
     * @return new instance of PaymentHistoryDetailFragment
     */
    public static PaymentHistoryDetailFragment newInstance(PaymentHistoryItem historyItem){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);

        PaymentHistoryDetailFragment fragment = new PaymentHistoryDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PracticePaymentHistoryCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implememnt PracticePaymentHistoryCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_history_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(historyItem.getPayload().getDate());

        TextView transactionDate = (TextView) view.findViewById(R.id.transaction_date);
        transactionDate.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView transactionTime = (TextView) view.findViewById(R.id.transaction_time);
        transactionTime.setText(dateUtil.getTime12Hour());

        TextView transactionNumber = (TextView) view.findViewById(R.id.transaction_number);
        transactionNumber.setText(historyItem.getPayload().getConfirmation());

        TextView transactionType = (TextView) view.findViewById(R.id.transaction_payment_type);
        transactionType.setText(getPaymentMethod(historyItem.getPayload().getPapiPaymentMethod()));

        double totalPaid = historyItem.getPayload().getTotalPaid();
        TextView transactionTotal = (TextView) view.findViewById(R.id.transaction_total);
        transactionTotal.setText(NumberFormat.getCurrencyInstance().format(totalPaid));

        lineItems = historyItem.getPayload().getLineItems();
        if(totalPaid > 0 && totalPaid != historyItem.getPayload().getAmount()){
            lineItems = getSuccessfulLineItems();
        }

        itemsRecycler = (RecyclerView) view.findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter();
    }

    private void setAdapter(){
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

    private static String getPaymentMethod(PapiPaymentMethod paymentMethod){
        switch (paymentMethod.getPaymentMethodType()){
            case PAYMENT_METHOD_ACCOUNT:
                return Label.getLabel("payment_method_account");
            case PAYMENT_METHOD_CARD:
            case PAYMENT_METHOD_NEW_CARD:
            default:
                return Label.getLabel("payment_method_creditcard");
        }
    }

}
