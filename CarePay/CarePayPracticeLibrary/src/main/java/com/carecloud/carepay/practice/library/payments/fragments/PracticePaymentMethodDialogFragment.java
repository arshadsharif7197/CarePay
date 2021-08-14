package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.dialogs.ConfirmCashDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.payments.interfaces.PaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodDialogFragment extends PracticePaymentMethodFragment {

    protected PaymentMethodDialogInterface dialogCallback;

    /**
     * @param paymentsModel the payments model
     * @param amount        the amount
     * @return an instance of PracticePaymentMethodDialogFragment
     */
    public static PracticePaymentMethodDialogFragment newInstance(PaymentsModel paymentsModel, double amount) {
        Bundle args = new Bundle();

        Gson gson = new Gson();
/*        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);*/

//        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodDialogFragment fragment = new PracticePaymentMethodDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            dialogCallback = (PaymentMethodDialogInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must imlement PaymentMethodDialogInterface");
        }
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
                cancel();
            }
        });
        TextView title = view.findViewById(R.id.respons_toolbar_title);
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, final double amount) {
        if (paymentMethod.getType().equals(CarePayConstants.TYPE_CASH) &&
                getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            ConfirmCashDialogFragment fragment = new ConfirmCashDialogFragment();
            fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ((PaymentInterface) callback).onPayButtonClicked(amount, paymentsModel);
                }
            });
            fragment.setConfirmCashCallback(new ConfirmCashDialogFragment.ConfirmCashCallback() {
                @Override
                public void onConfirmCash() {
                    dialogCallback.onCashSelected(paymentsModel);
                }
            });
            fragment.show(getFragmentManager(), fragment.getClass().getName());
            logPaymentMethodSelection(getString(com.carecloud.carepaylibrary.R.string.payment_cash));
        } else {
            super.handlePaymentButton(paymentMethod, amount);
            hideDialog();
        }
    }

    @Override
    protected void handleSwipeCard() {
        super.handleSwipeCard();
        dismiss();
    }

    @Override
    protected void onPaymentCashFinished() {
        callback.onPaymentCashFinished();
    }

    @Override
    protected void onPaymentMethodAction(PaymentsMethodsDTO paymentMethod,
                                         double amount,
                                         PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticeChooseCreditCardFragment fragment = PracticeChooseCreditCardFragment
                    .newInstance(paymentsModel,
                            paymentMethod.getLabel(), amount);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
            hideDialog();
        } else {
            PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment
                    .newInstance(paymentsModel, amount);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
            hideDialog();
        }
    }

}