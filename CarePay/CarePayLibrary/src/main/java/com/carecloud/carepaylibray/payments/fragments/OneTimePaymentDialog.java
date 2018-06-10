package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 2/5/18
 */

public class OneTimePaymentDialog extends PartialPaymentDialog {

    private PaymentPlanDTO paymentPlanDTO;
    private PaymentsModel paymentsDTO;
    private Context context;
    private OneTimePaymentInterface callback;

    /**
     * Contructor
     *
     * @param context        context must implement PayNowClickListener
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     */
    public OneTimePaymentDialog(Context context,
                                PaymentsModel paymentsDTO,
                                PaymentPlanDTO paymentPlanDTO,
                                OneTimePaymentInterface callback) {
        super(context, paymentsDTO, null);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
        this.paymentPlanDTO = paymentPlanDTO;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button payButton = (Button) findViewById(R.id.payPartialButton);
        payButton.setText(Label.getLabel("payment_plan_one_time_payment"));
    }

    @Override
    protected double getMinimumPayment(String practiceId) {
        return 0D;
    }

    @Override
    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(),
                paymentPlanDTO.getPayload().getAmountPaid());
    }

    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);
            callback.onStartOneTimePayment(paymentsDTO, paymentPlanDTO);
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private void createPaymentModel(double amount) {
        IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);

        paymentsDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

}
