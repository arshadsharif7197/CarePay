package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItemMetadata;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodFragment extends PaymentMethodFragment {
    private Boolean isCloverDevice;

    private String swipeCardNowString;
    private String swipeCardSeparatorString;
    private String swipeCardAlternateSeparatorString;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        getLabels();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_practice, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);


        setSwipeCardNowVisibility(view);

    }

    @Override
    protected void setupTitleViews(View view){
        super.setupTitleViews(view);
        TextView title = (TextView) view.findViewById(R.id.paymentMethodTitleLabel);
        if(title!=null) {
            title.setText(getTitlePaymentMethodString());
        }

    }


    private void setSwipeCardNowVisibility(View view)
    {
        Button swipeCreditCarNowButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        TextView swipeCardSeparatorLabel = (TextView) view.findViewById(R.id.swipeCardSeparatorLabel);
        View swipeCreditCardNowLayout = view.findViewById(R.id.swipeCreditCardNowLayout);
        if(isCloverDevice) {
            swipeCreditCarNowButton.setEnabled(true);
            swipeCreditCarNowButton.setText(swipeCardNowString);
            swipeCreditCarNowButton.setOnClickListener(swipeCreditCarNowButtonClickListener);

            swipeCardSeparatorLabel.setText(swipeCardAlternateSeparatorString);
        } else {
            swipeCreditCarNowButton.setEnabled(false);
            swipeCardSeparatorLabel.setText(swipeCardSeparatorString);
            swipeCreditCardNowLayout.setVisibility(View.GONE);
        }

    }

    private View.OnClickListener swipeCreditCarNowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setCloverPayment();
            if(getDialog()!=null){
                dismiss();
            }
        }
    };



    /**
     * payment labels
     */
    private void getLabels() {
        if (paymentsModel != null) {
            PaymentsMetadataModel paymentsMetadataModel = paymentsModel.getPaymentsMetadata();
            if (paymentsMetadataModel != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataModel.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    swipeCardNowString = paymentsLabelsDTO.getPaymentCloverSwipeNowButtonLabel();
                    swipeCardSeparatorString = paymentsLabelsDTO.getPaymentCloverSwipeNowSeparatorText();
                    swipeCardAlternateSeparatorString = paymentsLabelsDTO.getPaymentCloverAlternatePayButton();

                }
            }
        }
    }

    private void setCloverPayment()
    {
        try
        {
            PatientBalanceDTO patientPayments = paymentList.get(0);

            double paymentAmount = getArguments().getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            if(paymentAmount==0){
                paymentAmount = patientPayments.getBalances().get(0).getPayload().get(0).getAmount();
            }
            Gson gson = new Gson();
            String patientPaymentMetaDataString = gson.toJson(patientPayments.getBalances().get(0).getMetadata());
            String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
            Intent intent = new Intent();
            intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_METADATA, patientPaymentMetaDataString);
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, paymentAmount);
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);

            List<PaymentLineItem> paymentLineItems = new ArrayList<>();
            List<PendingBalanceDTO> balances = paymentList.get(0).getBalances();
            for(PendingBalanceDTO balance : balances) {

                PaymentLineItem paymentLineItem = new PaymentLineItem();
                paymentLineItem.setAmount(balance.getPayload().get(0).getAmount());
                paymentLineItem.setDescription(balance.getPayload().get(0).getType());

                PaymentLineItemMetadata metadata = new PaymentLineItemMetadata();
                metadata.setPatientID(balance.getMetadata().getPatientId());
                metadata.setPracticeID(balance.getMetadata().getPracticeId());
//                metadata.setProviderID(balance.getMetadata().getProviderID()); //TODO this is missing in the DTO
//                metadata.setLocationID(balance.getMetadata().getLocationID()); //TODO this is missing in the DTO

                paymentLineItems.add(paymentLineItem);

            }

            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));
            getActivity().startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}