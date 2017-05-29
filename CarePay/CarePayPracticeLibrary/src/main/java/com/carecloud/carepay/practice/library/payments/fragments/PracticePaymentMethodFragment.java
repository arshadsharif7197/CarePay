package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.CloverPaymentAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodFragment extends PaymentMethodFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setSwipeCardNowVisibility(view);
    }

    @Override
    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
    }

    private void setSwipeCardNowVisibility(View view) {
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        Button swipeCreditCarNowButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        View swipeCreditCardNowLayout = view.findViewById(R.id.swipeCreditCardNowLayout);
        swipeCreditCarNowButton.setEnabled(isCloverDevice);
        swipeCreditCardNowLayout.setVisibility(isCloverDevice?View.VISIBLE:View.GONE);
        swipeCreditCarNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSwipeCard();
            }
        });

    }

    protected void handleSwipeCard(){
        CloverPaymentAdapter cloverPaymentAdapter = new CloverPaymentAdapter(getActivity(), paymentsModel);
        PaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (paymentPostModel == null) {
            cloverPaymentAdapter.setCloverPayment(amountToMakePayment);
        } else {
            cloverPaymentAdapter.setCloverPayment(paymentPostModel);
        }
    }
}