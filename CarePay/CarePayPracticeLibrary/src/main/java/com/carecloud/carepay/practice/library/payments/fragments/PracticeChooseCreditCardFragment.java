package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.CloverPaymentAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 3/8/17.
 */
public class PracticeChooseCreditCardFragment extends ChooseCreditCardFragment {

    /**
     *
     * @param paymentsDTO the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param amount the amount
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
