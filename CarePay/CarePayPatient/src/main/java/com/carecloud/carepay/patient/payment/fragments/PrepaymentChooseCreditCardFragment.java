package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2020-01-28.
 */
public class PrepaymentChooseCreditCardFragment extends ChooseCreditCardFragment {

    private AppointmentPrepaymentCallback appointmentPrepaymentCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentPrepaymentCallback) {
            appointmentPrepaymentCallback = (AppointmentPrepaymentCallback) context;
        } else if (context instanceof AppointmentViewHandler) {
            appointmentPrepaymentCallback = (AppointmentPrepaymentCallback)
                    ((AppointmentViewHandler) context).getAppointmentPresenter();
        }
    }

    public static PrepaymentChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                 String selectedPaymentMethodLabel,
                                                                 double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        PrepaymentChooseCreditCardFragment chooseCreditCardFragment = new PrepaymentChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    protected void showAddCard(double amountToMakePayment, PaymentsModel paymentsModel) {
        callback.addFragment(PrepaymentAddNewCreditCardFragment.newInstance(paymentsModel, amountToMakePayment),
                true);
    }

    @Override
    protected void onFailureAction(ServerErrorDTO serverErrorDto) {
        if ("504".equals(serverErrorDto.getMessage().getBody().getError().getErrorCode())) {
            appointmentPrepaymentCallback.onPrepaymentFailed();
        }
    }
}
