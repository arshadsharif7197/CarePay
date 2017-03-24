package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItemMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentType;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/8/17.
 */
public class PracticeChooseCreditCardFragment extends ChooseCreditCardFragment {

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param amount                     the amount
     * @return an instance of PracticeChooseCreditCardFragment
     */
    public static PracticeChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                               String selectedPaymentMethodLabel,
                                                               double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        PracticeChooseCreditCardFragment chooseCreditCardFragment = new PracticeChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String name = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getFirstName();
        String label = Label.getLabel("payment_user_credit_card_title");
        titleLabel = name + label;
    }

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
            if (paymentsModel.getPaymentPayload().getPaymentPostModel() == null) {
                processPayment(amountToMakePayment);
            } else {
                setCloverPayment(paymentsModel.getPaymentPayload().getPaymentPostModel());
            }

            if (getDialog() != null) {
                dismiss();
            }
        }
    };

    private void processPayment(double amount) {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setType(PaymentType.credit_card);
        paymentObject.setExecution(PaymentExecution.clover);
        paymentObject.setAmount(amount);

        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.getPaymentObjects().add(paymentObject);
        paymentPostModel.setAmount(amount);
        setCloverPayment(paymentPostModel);
    }

    private void setCloverPayment(PaymentPostModel postModel) {

        Intent intent = new Intent();
        intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);

        double paymentAmount = postModel.getAmount();
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, paymentAmount);

        Gson gson = new Gson();
        String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);

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

            paymentLineItems.add(paymentLineItem);

        }
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));
        getActivity().startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());

    }

}
