package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodDialogFragment extends PracticePaymentMethodFragment {

    /**
     *
     * @param paymentsModel the payments model
     * @param amount the amount
     * @return an instance of PracticePaymentMethodDialogFragment
     */
    public static PracticePaymentMethodDialogFragment newInstance(PaymentsModel paymentsModel, double amount) {
        Bundle args = new Bundle();

        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);

        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodDialogFragment fragment = new PracticePaymentMethodDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_dialog_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPayLaterClicked(paymentsModel);
            }
        });
        TextView title = (TextView) view.findViewById(R.id.respons_toolbar_title);
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        super.handlePaymentButton(paymentMethod, amount);
        dismiss();
    }

    @Override
    protected void handleSwipeCard(){
        super.handleSwipeCard();
        dismiss();
    }

}