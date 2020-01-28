package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2020-01-28.
 */
public class PrepaymentPracticeChooseCreditCardFragment extends PracticeChooseCreditCardFragment {

    private AppointmentPrepaymentCallback appointmentPrepaymentCallback;

    public static PrepaymentPracticeChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                               String selectedPaymentMethodLabel,
                                                               double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        PrepaymentPracticeChooseCreditCardFragment chooseCreditCardFragment = new PrepaymentPracticeChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentPrepaymentCallback) {
            appointmentPrepaymentCallback = (AppointmentPrepaymentCallback) context;
        }
    }

    @Override
    protected void showAddCard(double amountToMakePayment, PaymentsModel paymentsModel) {
        PrepaymentPracticeAddNewCreditCardFragment fragment = PrepaymentPracticeAddNewCreditCardFragment
                .newInstance(paymentsModel, amountToMakePayment);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    protected void onFailureAction(ServerErrorDTO serverErrorDto) {
        if ("504".equals(serverErrorDto.getMessage().getBody().getError().getErrorCode())) {
            appointmentPrepaymentCallback.onPrepaymentFailed();
        }
    }
}
