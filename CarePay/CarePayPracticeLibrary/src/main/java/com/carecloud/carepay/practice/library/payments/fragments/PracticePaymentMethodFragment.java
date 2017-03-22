package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItemMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodFragment extends PaymentMethodFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setSwipeCardNowVisibility(view);
        paymentMethodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PaymentsMethodsDTO paymentMethod = paymentMethodsList.get(position);
                Bundle args = getArguments();
                double amount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
                handlePaymentButton(paymentMethod, amount);
            }
        });


    }

    @Override
    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        TextView title = (TextView) view.findViewById(R.id.paymentMethodTitleLabel);
        if (title != null) {
            title.setText(getTitlePaymentMethodString());
        }

    }


    private void setSwipeCardNowVisibility(View view) {
        Button swipeCreditCarNowButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        View swipeCreditCardNowLayout = view.findViewById(R.id.swipeCreditCardNowLayout);
        swipeCreditCarNowButton.setEnabled(false);
        swipeCreditCardNowLayout.setVisibility(View.GONE);

    }

    private View.OnClickListener swipeCreditCarNowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setCloverPayment(paymentsModel.getPaymentPayload());
            if (getDialog() != null) {
                dismiss();
            }
        }
    };

    private void setCloverPayment(PaymentsPayloadDTO paymentsPayloadDTO) {
        PaymentPostModel postModel = paymentsPayloadDTO.getPaymentPostModel();

        Intent intent = new Intent();
        intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);

        double paymentAmount = postModel.getAmount();
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, paymentAmount);

        Gson gson = new Gson();
        String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);

        if (postModel != null && postModel.getAmount() > 0) {
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

            paymentLineItems.add(paymentLineItem);

        }
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));
        getActivity().startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());

    }


}