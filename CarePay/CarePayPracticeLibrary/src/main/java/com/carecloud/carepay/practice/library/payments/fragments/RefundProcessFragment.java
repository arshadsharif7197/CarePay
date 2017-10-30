package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.RefundProcessAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.refund.RefundLineItem;
import com.carecloud.carepaylibray.payments.models.refund.RefundPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 10/25/17
 */

public class RefundProcessFragment extends BaseDialogFragment implements RefundProcessAdapter.RefundItemActionCallback {

    private PracticePaymentNavigationCallback callback;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    private PaymentHistoryItem historyItem;
    private PaymentsModel paymentsModel;
    private List<PaymentHistoryLineItem> lineItems = new ArrayList<>();
    private List<PaymentHistoryLineItem> refundLineItems = new ArrayList<>();

    private TextView refundButton;
    private TextView refundAmount;
    private RecyclerView refundItemsRecycler;

    private double totalPaid;

    /**
     * Create a new instance of RefundProcessFragment
     * @param historyItem history item
     * @return new instance of RefundProcessFragment
     */
    public static RefundProcessFragment newInstance(PaymentHistoryItem historyItem, PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);
        DtoHelper.bundleDto(args, paymentsModel);

        RefundProcessFragment fragment = new RefundProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PracticePaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PracticePaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, args);
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        if(historyItem != null) {
            initLineItems();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_refund_process, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        setupToolbar(view);
        setupButtons(view);

        String date = DateUtil.getInstance().setDateRaw(historyItem.getPayload().getDate().replaceAll("\\.\\d\\d\\dZ", "-00:00")).getFullDateTime();
        TextView paymentDate = (TextView) view.findViewById(R.id.total_paid_date);
        paymentDate.setText(String.format(Label.getLabel("payment_refund_total_paid"), date));

        TextView paymentAmount = (TextView) view.findViewById(R.id.payment_total);
        paymentAmount.setText(currencyFormatter.format(totalPaid));

        refundAmount = (TextView) view.findViewById(R.id.refund_amount);
        setRefundAmount();

        refundItemsRecycler = (RecyclerView) view.findViewById(R.id.line_items_recycler);
        refundItemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter();
    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(Label.getLabel("payment_refund_title"));
        }
    }

    private void setupButtons(View view){
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.displayHistoryItemDetails(historyItem, paymentsModel);
            }
        });

        refundButton = (TextView) view.findViewById(R.id.refund_button);
        refundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRefund();
            }
        });

    }

    private void setAdapter(){
        RefundProcessAdapter adapter = new RefundProcessAdapter(getContext(), paymentsModel, lineItems, this);
        refundItemsRecycler.setAdapter(adapter);
    }

    private void initLineItems(){
        double successfulAmount = 0D;
        for(PaymentHistoryLineItem lineItem : historyItem.getPayload().getLineItems()){
            if(lineItem.isProcessed()){
                successfulAmount += lineItem.getAmount();
                lineItems.add(lineItem);
            }
        }
        refundLineItems.addAll(lineItems);
        totalPaid = successfulAmount;
    }

    private double calcCurrentRefundAmount(){
        double total = 0D;
        for(PaymentHistoryLineItem lineItem : refundLineItems){
            total += lineItem.getAmount();
        }
        return total;
    }

    private void setRefundAmount(){
        refundAmount.setText(currencyFormatter.format(calcCurrentRefundAmount()));
        if(refundLineItems.size() < lineItems.size()){
            refundButton.setText(Label.getLabel("payment_refund_button_partial"));
        }else{
            refundButton.setText(Label.getLabel("payment_refund_button_full"));
        }
    }

    private void processRefund() {
        if(historyItem.getPayload().getMetadata().isExternallyProcessed() && historyItem.getPayload().getExecution().equals(IntegratedPaymentPostModel.EXECUTION_CLOVER)){
            boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
            if(isCloverDevice){
                processCloverRefund();
            }else{
                new CustomMessageToast(getContext(), Label.getLabel("payment_refund_clover_error"), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
            }
        }else{
            processStandardRefund();
        }
    }

    private void processCloverRefund(){
        Intent intent = new Intent();
        intent.setAction(CarePayConstants.CLOVER_REFUND_INTENT);
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, calcCurrentRefundAmount());

        Gson gson = new Gson();
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(getPaymentLineItems()));

        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSACTION_RESPONSE, gson.toJson(historyItem.getPayload().getTransactionResponse()));

        startActivity(intent);

    }

    private void processStandardRefund(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", historyItem.getPayload().getMetadata().getPatientId());

        RefundPostModel refundPostModel = new RefundPostModel();
        refundPostModel.setPaymentRequestId(historyItem.getPayload().getDeepstreamRecordId());
        refundPostModel.setRefundLineItems(getRefundItems());

        Gson gson = new Gson();
        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getRefundPayment();
        getWorkflowServiceHelper().execute(transition, refundCallback, gson.toJson(refundPostModel), queryMap);
    }

    private WorkflowServiceCallback refundCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    private List<PaymentLineItem> getPaymentLineItems(){
        List<PaymentLineItem> paymentLineItems = new ArrayList<>();
        for (IntegratedPaymentLineItem lineItem : refundLineItems) {
            PaymentLineItem paymentLineItem = new PaymentLineItem();
            paymentLineItem.setAmount(lineItem.getAmount());
            paymentLineItem.setDescription(lineItem.getDescription());

            paymentLineItems.add(paymentLineItem);

        }
        return paymentLineItems;
    }

    private List<RefundLineItem> getRefundItems(){
        List<RefundLineItem> refundItems = new ArrayList<>();
        for(PaymentHistoryLineItem lineItem : refundLineItems){
            RefundLineItem refundItem = new RefundLineItem();
            refundItem.setAmount(lineItem.getAmount());
            refundItem.setDescription(lineItem.getDescription());
            refundItem.setLineItemId(lineItem.getLineItemId());

            refundItems.add(refundItem);
        }
        return refundItems;
    }


    @Override
    public void onItemCheckChanged(List<PaymentHistoryLineItem> groupItems, boolean checked) {
        if(checked){
            refundLineItems.addAll(groupItems);
        }else{
            refundLineItems.removeAll(groupItems);
        }
        setRefundAmount();
    }

    @Override
    public void onItemAmountSelected(PaymentHistoryLineItem lineItem) {

    }
}
