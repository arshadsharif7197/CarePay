package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.CloverPaymentAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 3/29/17
 */

public class PracticeAddNewCreditCardFragment extends AddNewCreditCardFragment {

    public static PracticeAddNewCreditCardFragment newInstance(PaymentsModel paymentsDTO, double amount) {
        Bundle args = new Bundle();
        PracticeAddNewCreditCardFragment fragment = new PracticeAddNewCreditCardFragment();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE) ||
                HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_2_DEVICE);
        Button swipeCardButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        if (isCloverDevice && swipeCardButton != null && !paymentsModel.getPaymentPayload().isPrepayment()) {
            swipeCardButton.setVisibility(View.VISIBLE);
            swipeCardButton.setOnClickListener(swipeCreditCarNowButtonClickListener);
        }
    }


    private View.OnClickListener swipeCreditCarNowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CloverPaymentAdapter cloverPaymentAdapter = new CloverPaymentAdapter((BaseActivity) getActivity(), paymentsModel, callback.getAppointmentId(), callback);
            IntegratedPaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            if (paymentPostModel == null) {
                cloverPaymentAdapter.setCloverPayment(amountToMakePayment);//TODO switch back to connector
            } else {
                cloverPaymentAdapter.setCloverPayment(paymentPostModel);
            }

            if (getDialog() != null) {
                dismiss();
            }
        }
    };

}