package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.CloverPaymentAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;

/**
 * Created by lmenendez on 3/29/17.
 */

public class PracticeAddNewCreditCardFragment extends AddNewCreditCardFragment {

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        Button swipeCardButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        if (isCloverDevice) {
            swipeCardButton.setVisibility(View.VISIBLE);
            swipeCardButton.setOnClickListener(swipeCreditCarNowButtonClickListener);
        }
    }


    private View.OnClickListener swipeCreditCarNowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CloverPaymentAdapter cloverPaymentAdapter = new CloverPaymentAdapter(getActivity(), paymentsModel, callback.getAppointmentId());
            PaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            if (paymentPostModel == null) {
                cloverPaymentAdapter.setCloverPayment(amountToMakePayment);
            } else {
                cloverPaymentAdapter.setCloverPayment(paymentPostModel);
            }

            if (getDialog() != null) {
                dismiss();
            }
        }
    };

}