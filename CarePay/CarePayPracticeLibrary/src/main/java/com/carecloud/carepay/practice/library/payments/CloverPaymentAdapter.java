package com.carecloud.carepay.practice.library.payments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItemMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/28/17.
 */

public class CloverPaymentAdapter {

    private PaymentsModel paymentsModel;
    private Activity activity;
    private String appointmentId;

    /**
     * Constructor
     * @param activity Activity
     * @param paymentsModel payment model
     */
    public CloverPaymentAdapter(Activity activity, PaymentsModel paymentsModel, String appointmentId){
        this.activity = activity;
        this.paymentsModel = paymentsModel;
        this.appointmentId = appointmentId;
    }

    /**
     * Set Generic Payment
     * @param amount amount to pay
     */
    public void setCloverPayment(double amount){
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setAmount(amount);

        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.getPaymentObjects().add(paymentObject);
        paymentPostModel.setAmount(amount);
        setCloverPayment(paymentPostModel);

    }

    /**
     * Set Applied Payment
     * @param postModel payment model for payment application
     */
    public void setCloverPayment(PaymentPostModel postModel) {

        Intent intent = new Intent();
        intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);

        double paymentAmount = postModel.getAmount();
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, paymentAmount);

        if(appointmentId != null) {
            intent.putExtra(CarePayConstants.APPOINTMENT_ID, appointmentId);
        }

        Gson gson = new Gson();
        String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);

        String queueTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getQueuePayment());
        intent.putExtra(CarePayConstants.CLOVER_QUEUE_PAYMENT_TRANSITION, queueTransitionString);


        String paymentMetadata = gson.toJson(paymentsModel.getPaymentPayload().getPatientBalances().get(0));
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_METADATA, paymentMetadata);

        if (postModel.getAmount() > 0) {
            String paymentPostModelString = gson.toJson(postModel);
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL, paymentPostModelString);
        }

        List<PaymentLineItem> paymentLineItems = new ArrayList<>();
        for (PaymentObject paymentObject : postModel.getPaymentObjects()) {
            PaymentLineItem paymentLineItem = new PaymentLineItem();
            paymentLineItem.setAmount(paymentObject.getAmount());
            paymentLineItem.setDescription(paymentObject.getDescription());

            PaymentLineItemMetadata metadata = new PaymentLineItemMetadata();
            metadata.setPatientID(paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getMetadata().getPatientId());
            metadata.setPracticeID(paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getMetadata().getPracticeId());
            metadata.setLocationID(paymentObject.getLocationID());
            metadata.setProviderID(paymentObject.getProviderID());
            paymentLineItem.setMetadata(metadata);

            paymentLineItems.add(paymentLineItem);

        }
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));
        activity.startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());

    }

}
