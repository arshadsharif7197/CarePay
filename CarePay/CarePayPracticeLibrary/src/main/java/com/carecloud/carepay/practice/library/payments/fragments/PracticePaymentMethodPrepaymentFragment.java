package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 8/23/17
 */

public class PracticePaymentMethodPrepaymentFragment extends PracticePaymentMethodDialogFragment {

    private AppointmentPrepaymentCallback callback;

    /**
     * @param paymentsModel the payments DTO
     * @param amount        the amount
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PracticePaymentMethodPrepaymentFragment newInstance(PaymentsModel paymentsModel, double amount){
        PracticePaymentMethodPrepaymentFragment fragment = new PracticePaymentMethodPrepaymentFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof AppointmentViewHandler){
                callback = (AppointmentPrepaymentCallback) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (AppointmentPrepaymentCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_pre_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle){
        super.onViewCreated(view, icicle);

        TextView prepaymentAmount = view.findViewById(R.id.prepaymentAmount);
        prepaymentAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amountToMakePayment));

        TextView title = view.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("appointments_prepayment_title"));

        Button swipeCreditCarNowButton = view.findViewById(R.id.swipeCreditCarNowButton);
        if(swipeCreditCarNowButton != null){
            swipeCreditCarNowButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPaymentMethodAction(PaymentsMethodsDTO paymentMethod,
                                         double amount,
                                         PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PrepaymentPracticeChooseCreditCardFragment fragment = PrepaymentPracticeChooseCreditCardFragment
                    .newInstance(paymentsModel,
                            paymentMethod.getLabel(), amount);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
            hideDialog();
        } else {
            PrepaymentPracticeAddNewCreditCardFragment fragment = PrepaymentPracticeAddNewCreditCardFragment
                    .newInstance(paymentsModel, amount);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
            hideDialog();
        }
    }


}
