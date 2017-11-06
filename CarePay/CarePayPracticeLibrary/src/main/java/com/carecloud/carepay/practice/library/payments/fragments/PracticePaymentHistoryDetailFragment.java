package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentHistoryCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.fragments.PaymentHistoryDetailFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 9/29/17
 */

public class PracticePaymentHistoryDetailFragment extends PaymentHistoryDetailFragment {

    private PracticePaymentHistoryCallback callback;
    private PaymentsModel paymentsModel;

    /**
     * Get new instance of PracticePaymentHistoryDetailFragment
     * @param historyItem history item
     * @return new instance of PracticePaymentHistoryDetailFragment
     */
    public static PracticePaymentHistoryDetailFragment newInstance(PaymentHistoryItem historyItem, PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);
        DtoHelper.bundleDto(args, paymentsModel);

        PracticePaymentHistoryDetailFragment fragment = new PracticePaymentHistoryDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context){
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
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
    }

    @Override
    protected void initItemsRecycler(RecyclerView recycler) {
        itemsRecycler = recycler;
        setAdapter();
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
                callback.showPaymentHistory(paymentsModel);
                dismiss();
            }
        });

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(historyItem.getPayload().getDate()).shiftDateToGMT();

        TextView transactionDate = (TextView) view.findViewById(R.id.transaction_date);
        transactionDate.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView transactionTime = (TextView) view.findViewById(R.id.transaction_time);
        transactionTime.setText(dateUtil.getTime12Hour());

        TextView transactionNumber = (TextView) view.findViewById(R.id.transaction_number);
        transactionNumber.setText(historyItem.getPayload().getConfirmation());

        TextView transactionType = (TextView) view.findViewById(R.id.transaction_payment_type);
        transactionType.setText(getPaymentMethod(historyItem.getPayload().getPapiPaymentMethod()));

        TextView transactionTotal = (TextView) view.findViewById(R.id.transaction_total);
        transactionTotal.setText(NumberFormat.getCurrencyInstance().format(totalPaid));

        RecyclerView itemsRecycler = (RecyclerView) view.findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        initItemsRecycler(itemsRecycler);

        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,0);
            }
        }, 100);

        View refundButton = view.findViewById(R.id.refund_button);
        refundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRefund();
            }
        });

        if(historyItem.getPayload().getTotalRefunded() > 0D){
            View refundLayout = view.findViewById(R.id.refund_layout);
            refundLayout.setVisibility(View.VISIBLE);

            TextView refundAmount = (TextView) view.findViewById(R.id.transaction_refunded);
            refundAmount.setText(NumberFormat.getCurrencyInstance().format(historyItem.getPayload().getTotalRefunded()));
        }

    }

    private void processRefund(){
        boolean isCloverPayment = historyItem.getPayload().getMetadata().isExternallyProcessed() && historyItem.getPayload().getExecution().equals(IntegratedPaymentPostModel.EXECUTION_CLOVER);
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if(isCloverPayment && !isCloverDevice){
            new CustomMessageToast(getContext(), Label.getLabel("payment_refund_clover_error"), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }else {
            dismiss();
            callback.startRefundProcess(historyItem, paymentsModel);
        }
    }


}
