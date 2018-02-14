package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanAddCreditCardFragment extends PaymentPlanAddCreditCardFragment {

    private PaymentPlanInterface callback;

    /**
     * @param paymentsModel payment model
     * @param paymentPlanPostModel payment plan post model
     * @return new instance
     */
    public static PracticePaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel){
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PracticePaymentPlanAddCreditCardFragment fragment = new PracticePaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel payment model
     * @param paymentPlanDTO payment plan details
     * @return new instance
     */
    public static PracticePaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO){
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PracticePaymentPlanAddCreditCardFragment fragment = new PracticePaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentPlanInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onStartPaymentPlan(paymentsModel, paymentPlanPostModel);
            }
        });

    }

    @Override
    protected void makePaymentCall() {
        super.makePaymentCall();
        dismiss();
    }


}
